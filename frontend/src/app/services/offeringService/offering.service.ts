import { Injectable } from '@angular/core';
import { Observable, forkJoin, map, of, switchMap } from 'rxjs';
import { Offering, OfferingFilter, OfferingLike, OfferingRelated, OfferingToCreate, Offerings, ProviderCalendarData } from 'src/shared/models/offering.model';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { User } from 'src/shared/models/user.model';
import { UserService } from '../userService/user.service';
import { URI } from 'src/shared/types';
import { Links, parseLinks } from 'src/shared/models/pagination-utils.model';
import { Event } from 'src/shared/models/event.model';
import { VndType } from 'src/shared/VndType';
import { environment } from 'src/environments/environment';

const apiUrl = `${environment.apiUrl}/services`;

@Injectable({
  providedIn: 'root'
})
export class OfferingService {


  constructor(private http: HttpClient,
    private userService: UserService) {
  }

  getOfferingByURI(uri: URI): Observable<Offering> {
    const headers = new HttpHeaders().set('Accept', VndType.APPLICATION_OFFERING);
    return this.http.get<Offering>(uri, { headers });
  }

  getOfferingRelatedInfo(offering: Offering): Observable<OfferingRelated> {
    return this.userService.getUser(offering.owner).pipe(
      map(user => {
        return {
          offering: offering,
          owner: user,
        };
      })
    );
  }

  getOfferings(filter?: OfferingFilter, uri?: URI): Observable<Offerings> {
    let params = new HttpParams({ fromString: new URL(uri ? uri : apiUrl).searchParams.toString() });

    if (filter) {
      Object.entries(filter)
        .forEach(([key, value]) => {
          if (value !== undefined && value !== null) {
            if (key === 'districts') {
              value.forEach((district: string) => {
                params = params.append(key, district);
              });
            } else {
              params = params.set(key, value.toString());
            }
          }
        });
    }

    let uriWithoutParams = uri ? uri.split('?')[0] : apiUrl;

    const headers = new HttpHeaders().set('Accept', VndType.APPLICATION_OFFERINGS);
    return this.http.get<Offering[]>(uriWithoutParams, { params, headers, observe: 'response' }).pipe(
      map(response => {
        const offerings: Offering[] = response.body || [];
        const linksHeader = response.headers.get('Link');
        const links = linksHeader ? parseLinks(linksHeader) : {};
        return new Offerings(offerings, new Links(links));
      })
    );
  }

  getOfferingsOwners(offerings: Offering[]): Observable<Map<URI, User>> {
    const ownersByUri: Map<URI, User> = new Map();

    const ownersToRetrieve: URI[] = Array.from(new Set(offerings.map(offering => offering.owner)));

    const ownersObservables: Observable<User>[] = ownersToRetrieve.map(user => (this.userService.getUser(user)));

    return forkJoin(ownersObservables).pipe(
      map(owners => {
        owners.forEach(owner => {
          ownersByUri.set(owner.self, owner);
        });
        return ownersByUri;
      })
    );
  }

  static getURIForPage(page: number, uri?: URI): URI {
    const url = new URL(uri ? uri : apiUrl);
    const params = new URLSearchParams(url.search);
    params.set('page', page.toString());
    url.search = params.toString();
    return url.toString();
  }

  createOffering(offering: OfferingToCreate): Observable<Offering> {
    const headers = new HttpHeaders().set('Content-Type', VndType.APPLICATION_OFFERING);
    return this.http.post(apiUrl, offering, { headers, observe: 'response' }).pipe(
      switchMap(response => {
        const location = response.headers.get('Location');
        if (!location) {
          throw new Error('Location header is null');
        }
        return this.getOfferingByURI(location);
      })
    );
  }

  deleteOffering(offering: Offering) {
    return this.http.delete(offering.self);
  }

  editOffering(uri: URI, offering: OfferingToCreate): Observable<Offering> {
    const headers = new HttpHeaders().set('Accept', VndType.APPLICATION_OFFERING)
      .set('Content-Type', VndType.APPLICATION_OFFERING);

    return this.http.put<Offering>(uri, offering, { headers });
  }

  getOfferingsByEvent(event: Event): Observable<Offerings> {
    if (!event) {
      return of(new Offerings([], new Links({})));
    }
    return this.getOfferings(undefined, event.relatedOfferings);
  }

  getOffering(id: number): Observable<Offering> {
    return this.getOfferingByURI(`${apiUrl}/${id}`);
  }

  getOfferingsLikes(offerings: Offering[], user: User): Observable<Map<URI, boolean>> {
    const headers = new HttpHeaders().set('Accept', VndType.APPLICATION_LIKE);

    const likesByOffering: Map<URI, boolean> = new Map();

    const likesObservables: Observable<OfferingLike>[] = offerings.map(offering => (this.getOfferingLike(offering.self, user)));

    return forkJoin(likesObservables).pipe(
      map(likes => {
        likes.forEach(like => {
          if (like.liked) {
            likesByOffering.set(like.offering, like.liked);
          }
        });
        return likesByOffering;
      })
    );
  }

  getOfferingLike(offering: URI, user: User): Observable<OfferingLike> {
    const headers = new HttpHeaders().set('Accept', VndType.APPLICATION_LIKE);
    const uri = offering + '/likes/' + user.userId;
    return this.http.get<OfferingLike>(uri, { headers });
  }


  likeOffering(offering: URI, user: User): Observable<URI> {
    const headers = new HttpHeaders().set('Content-Type', VndType.APPLICATION_LIKE);
    const uri = offering + '/likes';
    return this.http.post(uri, { user: user.self }, { headers, observe: 'response' }).pipe(
      map(response => response.headers.get('Location')!)
    );
  }

  deleteLike(offering: URI, user: User): Observable<void> {
    const uri = this.buildLikeUri(offering, user);
    return this.http.delete<void>(uri);
  }

  getOfferingRecommendations(offering: Offering): Observable<Offering[]> {
    const headers = new HttpHeaders().set('Accept', VndType.APPLICATION_OFFERINGS);
    const params = new HttpParams().set('recommendedForOffering', offering.id!);
    return this.http.get<Offering[]>(apiUrl, { headers, params });
  }

  getEventRecommendations(event: Event): Observable<Offering[]> {
    const headers = new HttpHeaders().set('Accept', VndType.APPLICATION_OFFERINGS);
    const params = new HttpParams().set('recommendedForEvent', event.id!);
    return this.http.get<Offering[]>(apiUrl, { headers, params });
  }

  static getUriFromId(id: number): URI {
    return `${apiUrl}/${id}`;
  }

  private buildLikeUri(offering: URI, user: User): URI {
    return offering + '/likes/' + user.userId;
  }

}
