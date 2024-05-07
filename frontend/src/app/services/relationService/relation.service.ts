import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, forkJoin, map, of, switchMap, take, tap } from "rxjs";
import { VndType } from "src/shared/VndType";
import { Event } from "src/shared/models/event.model";
import { Offering } from "src/shared/models/offering.model";
import { Links, parseLinks } from "src/shared/models/pagination-utils.model";
import {
  Relation,
  RelationStatusChange,
  Relations,
  RelationsFilter,
} from "src/shared/models/relation.model";
import { URI } from "src/shared/types";
import { environment } from "src/environments/environment";
import { User } from "src/shared/models/user.model";
import {
  OfferingStatus,
  OfferingStatusKeys,
} from "src/shared/models/offering-status.model";

const apiUrl = `${environment.apiUrl}/relations`;

@Injectable({
  providedIn: "root",
})
export class RelationService {
  constructor(private http: HttpClient) {}

  getRelations(
    uri: URI,
    filter?: RelationsFilter,
    pageSize?: number
  ): Observable<Relations> {
    const headers = new HttpHeaders().set(
      "Accept",
      VndType.APPLICATION_RELATIONS
    );
    let params = new HttpParams();
    if (filter) {
      params = this.addFilterToParams(filter, params);
    }
    if (pageSize) {
      params = params.set("pageSize", pageSize.toString());
    }
    return this.http
      .get<Relation[]>(uri, { params, headers, observe: "response" })
      .pipe(
        map((response) => {
          const relations: Relation[] = response.body || [];
          const linksHeader = response.headers.get("Link");
          const links = linksHeader ? parseLinks(linksHeader) : {};
          return new Relations(relations, new Links(links));
        })
      );
  }

  private addFilterToParams(
    filter: RelationsFilter,
    params: HttpParams
  ): HttpParams {
    Object.entries(filter).forEach(([key, value]) => {
      if (Array.isArray(value)) {
        value.forEach((item) => {
          if (item !== undefined && item !== null) {
            params = params.append(key, item.toString());
          }
        });
      } else if (value !== undefined && value !== null) {
        params = params.set(key, value.toString());
      }
    });
    return params;
  }

  getOfferingsConversationData(
    offerings: Offering[],
    loggedUser: User
  ): Observable<Map<URI, Relations>> {
    const uri = loggedUser.providerRelations;
    const currentDate = RelationService.getStartOfToday().toISOString();
    const pageSize = 50;

    const observables = [];

    for (let offering of offerings) {
      const filter: RelationsFilter = {
        status: ["ACCEPTED", "PENDING"],
        offering: offering.id,
        from: currentDate,
      };

      observables.push(
        this.getRelations(uri, filter, pageSize).pipe(
          map((relations) => ({ offering, relations }))
        )
      );
    }

    return forkJoin(observables).pipe(
      map((results) => {
        const resultMap = new Map();
        for (let result of results) {
          resultMap.set(result.offering.self, result.relations);
        }
        return resultMap;
      })
    );
  }

  getAllRelations(uri: URI, filter?: RelationsFilter): Observable<Relations> {
    return this.getAllRelationsRec(uri, true, filter);
  }

  private getAllRelationsRec(
    uri: URI,
    isFirst: boolean,
    filter?: RelationsFilter,
    relations: Relations[] = []
  ): Observable<Relations> {
    if (!isFirst) {
      filter = undefined;
    }
    return this.getRelations(uri, filter, 100).pipe(
      switchMap((currentRelations) => {
        relations.push(currentRelations);
        let next = currentRelations.links.getNextLink();
        if (next) {
          return this.getAllRelationsRec(next, false, filter, relations);
        } else {
          let links = new Links({});
          if (relations.length > 0) {
            if (relations[0].links.getFirstLink()) {
              links.setFirstLink(relations[0].links.getFirstLink()!);
            }
            if (relations[relations.length - 1].links.getLastLink()) {
              links.setLastLink(
                relations[relations.length - 1].links.getLastLink()!
              );
            }
          }
          return of(
            new Relations(
              relations.flatMap((relations) => relations.relations),
              links
            )
          );
        }
      })
    );
  }

  changeOfferingFromEvents(
    offering: Offering,
    eventsToAdd: Event[],
    eventsToRemove: Event[]
  ): Observable<Map<URI, boolean>> {
    const observables = [];

    for (let event of eventsToAdd) {
      observables.push(
        this.getAllRelations(event.relations).pipe(
          take(1),
          switchMap((relations) => {
            if (
              !relations.relations.some(
                (relation) => relation.offering === offering.self
              )
            ) {
              return this.createRelation(event.self, offering.self).pipe(
                map((uri) => ({ uri, created: true }))
              );
            } else {
              return of(null);
            }
          })
        )
      );
    }

    for (let event of eventsToRemove) {
      observables.push(
        this.getAllRelations(event.relations).pipe(
          take(1),
          switchMap((relations) => {
            const relation = relations.relations.find(
              (relation) => relation.offering === offering.self
            );
            if (relation) {
              return this.deleteRelation(relation.self).pipe(
                map(() => ({ uri: relation.self, created: false }))
              );
            } else {
              return of(null);
            }
          })
        )
      );
    }

    return forkJoin(observables).pipe(
      map((results) => {
        const resultMap = new Map();
        for (let result of results) {
          if (result) {
            resultMap.set(result.uri, result.created);
          }
        }
        return resultMap;
      })
    );
  }

  createRelation(event: URI, offering: URI): Observable<URI> {
    const headers = new HttpHeaders()
      .set("Accept", VndType.APPLICATION_RELATION)
      .set("Content-Type", VndType.APPLICATION_RELATION);

    return this.http
      .post(
        apiUrl,
        { event, offering, status: OfferingStatusKeys.NEW },
        { headers, observe: "response" }
      )
      .pipe(
        switchMap((response) => {
          if (response.headers.get("Location")) {
            return of(response.headers.get("Location")!);
          }
          throw new Error("Location header is null");
        })
      );
  }

  deleteRelation(uri: URI) {
    return this.http.delete(uri);
  }

  contactProvider(uri: URI): Observable<Relation> {
    return this.changeRelationStatus(uri, new RelationStatusChange("PENDING"));
  }

  acceptRelation(uri: URI): Observable<Relation> {
    return this.changeRelationStatus(uri, new RelationStatusChange("ACCEPTED"));
  }

  rejectRelation(uri: URI): Observable<Relation> {
    return this.changeRelationStatus(uri, new RelationStatusChange("REJECTED"));
  }

  changeRelationStatus(
    uri: URI,
    relationStatusChange: RelationStatusChange
  ): Observable<Relation> {
    const headers = new HttpHeaders()
      .set("Accept", VndType.APPLICATION_RELATION)
      .set("Content-Type", VndType.APPLICATION_RELATION_STATUS);

    return this.http.patch<Relation>(uri, relationStatusChange, { headers });
  }

  getRelation(uri: URI): Observable<Relation> {
    const headers = new HttpHeaders().set(
      "Accept",
      VndType.APPLICATION_RELATION
    );
    return this.http.get<Relation>(uri, { headers });
  }

  static getUriFromId(id: number): URI {
    return `${apiUrl}/${id}`;
  }

  static getStartOfToday() {
    let start = new Date();
    start.setHours(0, 0, 0, 0);
    return start;
  }

  static getStartOf(date: Date | string) {
    let start = new Date(date);
    start.setHours(0, 0, 0, 0);
    return start;
  }
}
