import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import {
  Event,
  RangeFilter,
  Events,
  EventRelated,
  RelationInfo,
  EventToCreate,
} from "src/shared/models/event.model";
import { Observable, forkJoin, map, of, shareReplay, switchMap, tap } from "rxjs";
import { URI } from "src/shared/types";
import { VndType } from "src/shared/VndType";
import { HttpHeaders } from "@angular/common/http";
import { environment } from "src/environments/environment";
import { Links, parseLinks } from "src/shared/models/pagination-utils.model";
import { ReviewService } from "../reviewService/review.service";
import { OfferingService } from "../offeringService/offering.service";
import { RelationService } from "../relationService/relation.service";
import { User } from "src/shared/models/user.model";
import { UserService } from "../userService/user.service";
import { RelationsFilter } from "src/shared/models/relation.model";
import { ProviderCalendarData } from "src/shared/models/offering.model";

const apiUrl = `${environment.apiUrl}/events`;

@Injectable({
  providedIn: "root",
})
export class EventService {
  constructor(
    private http: HttpClient,
    private relationService: RelationService,
    private offeringService: OfferingService,
    private reviewService: ReviewService,
    private userService: UserService
  ) { }

  getEvents(uri: URI, filter?: RangeFilter): Observable<Events> {
    const headers = new HttpHeaders().set("Accept", VndType.APPLICATION_EVENTS);
    let params = new HttpParams({
      fromString: new URL(uri).searchParams.toString(),
    });

    if (filter) {
      Object.entries(filter).forEach(([key, value]) => {
        if (value !== undefined) {
          if (value == null) {
            params = params.delete(key);
          } else {
            params = params.set(key, value.toString());
          }
        }
      });
    }
    if (!params.has("pageSize")) {
      params = params.set("pageSize", "6");
    }

    let uriWithoutParams = uri.split("?")[0];

    return this.http
      .get<Event[]>(uriWithoutParams, { headers, params, observe: "response" })
      .pipe(
        map((response) => {
          const events = response.body || [];
          const linksHeader = response.headers.get("Link");
          const links = linksHeader ? parseLinks(linksHeader) : {};
          return new Events(events, new Links(links));
        })
      );
  }

  getEvent(id: number): Observable<Event> {
    const headers = new HttpHeaders().set("Accept", VndType.APPLICATION_EVENT);
    return this.http.get<Event>(apiUrl + "/" + id, { headers });
  }

  getEventURI(id: number): URI {
    return apiUrl + "/" + id;
  }

  getEventRelated(uri: URI): Observable<EventRelated> {
    return this.getEventByURI(uri).pipe(
      switchMap((event) =>
        forkJoin([
          this.relationService.getAllRelations(event.relations),
          this.offeringService.getOfferingsByEvent(event),
          this.reviewService.getAllReviews(event.reviews),
        ]).pipe(
          switchMap(([relations, offerings, reviews]) => {
            let relationsInfo: RelationInfo[] = [];
            const providersByURI = new Map<URI, User>();
            const owners = offerings.offerings
              .map((offering) => offering.owner)
              .filter((owner, index, self) => self.indexOf(owner) === index);
            const getUserObservables = owners.map((owner) =>
              this.userService.getUser(owner)
            );
            if (getUserObservables.length === 0) {
              return of(new EventRelated(event, relationsInfo));
            }
            return forkJoin(getUserObservables).pipe(
              map((users) => {
                for (let user of users) {
                  providersByURI.set(user.self, user);
                }
                for (let relation of relations.relations) {
                  let offering = offerings?.offeringsByURI.get(
                    relation.offering
                  );
                  if (offering) {
                    let provider = providersByURI.get(offering.owner);
                    let review = null;
                    if (
                      reviews &&
                      relation.review &&
                      reviews.reviewByURI.get(relation.review)
                    ) {
                      review = reviews.reviewByURI.get(relation.review)!;
                    }
                    if (provider) {
                      relationsInfo.push({
                        relation: relation,
                        offering: offering,
                        provider: provider,
                        review: review,
                        alreadyReviewed: review !== null,
                      });
                    }
                  }
                }
                return new EventRelated(event, relationsInfo);
              })
            );
          })
        )
      )
    );
  }

  getEventByURI(uri: URI): Observable<Event> {
    const headers = new HttpHeaders().set("Accept", VndType.APPLICATION_EVENT);
    return this.http.get<Event>(uri, { headers });
  }

  getEventsByURIs(uris: URI[]): Observable<Event[]> {
    if (uris.length === 0) {
      return of([]);
    }
    return forkJoin(uris.map((uri) => this.getEventByURI(uri)));
  }

  deleteEvent(event: Event) {
    return this.http.delete(apiUrl + "/" + event.id);
  }

  editEvent(uri: URI, event: EventToCreate): Observable<Event> {
    const headers = new HttpHeaders()
      .set("Accept", VndType.APPLICATION_EVENT)
      .set("Content-Type", VndType.APPLICATION_EVENT);
    return this.http.put<Event>(uri, event, { headers });
  }

  createEvent(event: EventToCreate): Observable<Event> {
    const headers = new HttpHeaders().set(
      "Content-Type",
      VndType.APPLICATION_EVENT
    );
    return this.http.post(apiUrl, event, { headers, observe: "response" }).pipe(
      switchMap((response) => {
        if (response.headers.get("Location")) {
          return this.getEventByURI(response.headers.get("Location")!);
        }
        throw new Error("Location header is null");
      })
    );
  }

  getProviderCalendarData(
    uri: URI,
    filter?: RelationsFilter
  ): Observable<ProviderCalendarData> {
    // We need to get all relations, not only the first page, to display them in the calendar
    return this.relationService.getAllRelations(uri, filter).pipe(
      switchMap((relations) => {
        // Remove duplicate events
        let events = relations.relations
          .map((relation) => relation.event)
          .filter((event, index, self) => self.indexOf(event) === index);

        return this.getEventsByURIs(events).pipe(
          map((events) => {
            return {
              relations,
              events: new Events(events, new Links({})),
            };
          })
        );
      })
    );
  }

  static getURIForPage(page: number, uri?: URI): URI {
    const url = new URL(uri ? uri : apiUrl);
    const params = new URLSearchParams(url.search);
    params.set("page", page.toString());
    url.search = params.toString();
    return url.toString();
  }
}
