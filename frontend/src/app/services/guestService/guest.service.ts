import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from 'src/environments/environment';
import { VndType } from 'src/shared/VndType';
import { Event } from 'src/shared/models/event.model';
import { Guest, GuestStatusUpdate, Guests } from 'src/shared/models/guest.model';
import { Links, parseLinks } from 'src/shared/models/pagination-utils.model';
import { URI } from 'src/shared/types';

@Injectable({
  providedIn: 'root'
})
export class GuestService {

  apiUrl = `${environment.apiUrl}`

  constructor(private http: HttpClient) { }

  getGuestByURI(uri: URI): Observable<Guest> {
    const headers = new HttpHeaders().set('Accept', VndType.APPLICATION_GUEST);
    return this.http.get<Guest>(uri, { headers });
  }

  getGuests(uri: URI): Observable<Guests> {
    const headers = new HttpHeaders().set('Accept', VndType.APPLICATION_GUESTS);

    let params = new HttpParams();

    return this.http.get<Guest[]>(uri, { params: params, headers: headers, observe: 'response' }).pipe(
      map(response => {
        const guests: Guest[] = response.body || [];
        const linksHeader = response.headers.get('Link');
        const links = linksHeader ? parseLinks(linksHeader) : {};
        return new Guests(guests, new Links(links));
      })
    )
  }

  addGuest(guest: Guest, uri: URI): Observable<void> {
    const headers = new HttpHeaders().set('Content-Type', VndType.APPLICATION_GUEST);
    return this.http.post<void>(uri, guest, { headers: headers });
  }

  deleteGuest(uri: URI) {
    return this.http.delete(uri)
  }

  updateGuest(uri: URI, guestStatusUpdate: GuestStatusUpdate) {
    const headers = new HttpHeaders().set('Content-Type', VndType.APPLICATION_GUEST_STATUS);
    return this.http.patch(uri, guestStatusUpdate, { headers: headers })
  }

  getGuest(eventId: number, guestId: number): Observable<Guest> {
    return this.getGuestByURI(`${this.apiUrl}/events/${eventId}/guests/${guestId}`);
  }

  updateGuestStatus(eventId: number, guestId: number, guestStatusUpdate: GuestStatusUpdate) {
    return this.updateGuest(`${this.apiUrl}/events/${eventId}/guests/${guestId}`, guestStatusUpdate);
  }

}