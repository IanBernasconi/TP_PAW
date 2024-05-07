import { TestBed } from '@angular/core/testing';

import { GuestService } from './guest.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Event } from 'src/shared/models/event.model';
import { Guest, GuestStatus, Guests } from 'src/shared/models/guest.model';
import { Links } from 'src/shared/models/pagination-utils.model';
import { District } from 'src/shared/models/utils.model';

describe('GuestService', () => {
  let service: GuestService;
  let httpMock: HttpTestingController;

  const mockGuest1: Guest = {
    email: 'guest1@gmail.com',
    status: GuestStatus.NEW as keyof typeof GuestStatus,
    self: 'http://localhost:8080/api/events/1/guests/1'
  }

  const mockGuest2: Guest = {
    email: 'guest2@gmail.com',
    status: GuestStatus.NEW as keyof typeof GuestStatus,
    self: 'http://localhost:8080/api/events/1/guests/2'
  }
  const mockGuests = new Guests([mockGuest1, mockGuest2], new Links({}));

  const mockEvent = new Event(1, 'Mock event', 'Mock description', '2021-05-05', 10, 'http://localhost:8080/api/events/1', 'http://localhost:8080/api/users/1', "AGRONOMIA" as keyof District, 'http://localhost:8080/api/relations?event=1', 'http://localhost:8080/api/services?event=1', 'http://localhost:8080/api/reviews?event=1', 'http://localhost:8080/api/events/1/guests')


  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(GuestService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('#getGuests() should return an Observable<Guests>', () => {
    service.getGuests(mockEvent.guests).subscribe(guests => {
      expect(guests).toEqual(mockGuests);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/events/1/guests');
    expect(req.request.method).toBe('GET');
    expect(req.request.body).toBeNull();

    req.flush([mockGuest1, mockGuest2]);
  });

  it('#addGuest() should send a POST request to the correct endpoint', () => {
    service.addGuest(mockGuest1, mockEvent.guests).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/events/1/guests');

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockGuest1);
    req.flush({});
  });

  it('#deleteGuest() should send a DELETE request to the correct endpoint ', () => {
    const guestSelf = mockGuest1.self;
    if (guestSelf) {
      service.deleteGuest(guestSelf).subscribe();

      const req = httpMock.expectOne('http://localhost:8080/api/events/1/guests/1');
      expect(req.request.method).toBe('DELETE');
      expect(req.request.body).toBeNull();
      req.flush({});
    }
  });

  it('#updateGuest() should send a PATCH request to the correct endpoint', () => {
    if (mockGuest1.self) {
      service.updateGuest(mockGuest1.self, mockGuest1).subscribe();

      const req = httpMock.expectOne('http://localhost:8080/api/events/1/guests/1');

      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toEqual(mockGuest1);
      req.flush({});
    }
  });
});
