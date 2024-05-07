import { TestBed } from '@angular/core/testing';

import { EventService } from './event-service.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Event, EventRelated, Events, RelationInfo } from 'src/shared/models/event.model';
import { Links } from 'src/shared/models/pagination-utils.model';
import { Guest, GuestStatus, Guests } from 'src/shared/models/guest.model';
import { Relation, Relations } from 'src/shared/models/relation.model';
import { OfferingStatus } from 'src/shared/models/offering-status.model';
import { Offering, Offerings } from 'src/shared/models/offering.model';
import { User } from 'src/shared/models/user.model';
import { RelationService } from '../relationService/relation.service';
import { OfferingService } from '../offeringService/offering.service';
import { ReviewService } from '../reviewService/review.service';
import { of } from 'rxjs';
import { Reviews } from 'src/shared/models/review.model';
import { UserService } from '../userService/user.service';
import { District, OfferingCategory, PriceType } from 'src/shared/models/utils.model';

describe('EventService', () => {
  let service: EventService;
  let httpMock: HttpTestingController;

  let mockRelationServices: jasmine.SpyObj<RelationService>;
  let mockOfferingService: jasmine.SpyObj<OfferingService>;
  let mockReviewService: jasmine.SpyObj<ReviewService>;
  let mockUserService: jasmine.SpyObj<UserService>;
  let mockEventService: jasmine.SpyObj<EventService>;

  const mockEvent = new Event(1, 'Mock event', 'Mock description', '2021-05-05', 10, 'http://localhost:8080/api/events/1', 'http://localhost:8080/api/users/1', "AGRONOMIA" as keyof District, 'http://localhost:8080/api/relations?event=1', 'http://localhost:8080/api/services?event=1', 'http://localhost:8080/api/reviews?event=1', 'http://localhost:8080/api/events/1/guests')

  const mockEvents = new Events([mockEvent], new Links({}));

  const mockGuest: Guest = {
    email: 'guest@gmail.com',
    status: GuestStatus.NEW as keyof typeof GuestStatus,
    self: 'http://localhost:8080/api/events/1/guests/1'
  }

  const mockGuests = new Guests([mockGuest], new Links({}));

  const mockRelation: Relation = {
    relationId: 1,
    status: OfferingStatus.ACCEPTED as keyof typeof OfferingStatus,
    self: 'http://localhost:8080/api/relations/1',
    event: 'http://localhost:8080/api/events/1',
    offering: 'http://localhost:8080/api/services/1',
    review: 'http://localhost:8080/api/relations/1/review',
    lastMessage: 'http://localhost:8080/api/messages/1',
    messages: 'http://localhost:8080/api/relations/1/messages',
    organizer: 'http://localhost:8080/api/users/1',
    provider: 'http://localhost:8080/api/users/2',
    providerUnreadMessagesCount: 100,
    organizerUnreadMessagesCount: 0
  };

  const mockRelations = new Relations([mockRelation], new Links({}));

  const mockOffering: Offering = {
    id: 1,
    deleted: false,
    likes: 0,
    rating: 0,
    self: 'http://localhost:8080/api/services/1',
    owner: 'http://localhost:8080/api/users/2',
    reviews: 'http://localhost:8080/api/services/1/reviews',
    name: 'Offering 1',
    description: 'Description 1',
    category: 'CATERING' as keyof OfferingCategory,
    minPrice: 10,
    maxPrice: 100,
    priceType: "OTHER" as keyof PriceType,
    maxGuests: 10,
    district: "AGRONOMIA" as keyof District,
    images: [],
  }

  const mockOfferings = new Offerings([mockOffering], new Links({}));

  const mockProvider: User = {
    userId: 2,
    averageRating: 0,
    totalLikes: 0,
    totalEventsWorkedOn: 0,
    self: 'http://localhost:8080/api/users/2',
    createdEvents: 'http://localhost:8080/api/users/2/createdEvents',
    createdOfferings: 'http://localhost:8080/api/users/2/createdOfferings',
    providerRelations: 'http://localhost:8080/api/users/2/providerRelations',
    occupiedDates: 'http://localhost:8080/api/users/2/occupiedDates',
    name: 'John Doe',
    email: 'jhondoe@gmail.com',
    language: 'en',
    description: 'I am a description',
    provider: true,
    profilePicture: null
  }

  const mockRelationInfo: RelationInfo = {
    relation: mockRelation,
    offering: mockOffering,
    provider: mockProvider,
    review: null,
    alreadyReviewed: false
  }

  const mockRecomendation: Offering = {
    id: 2,
    deleted: false,
    likes: 0,
    rating: 0,
    self: 'http://localhost:8080/api/services/2',
    owner: 'http://localhost:8080/api/users/3',
    reviews: 'http://localhost:8080/api/services/2/reviews',
    name: 'Offering 2',
    description: 'Description 2',
    category: "DECORATION" as keyof OfferingCategory,
    minPrice: 10,
    maxPrice: 100,
    priceType: "OTHER" as keyof PriceType,
    maxGuests: 10,
    district: "AGRONOMIA" as keyof District,
    images: []
  }

  const mockEventRelated = new EventRelated(mockEvent, [mockRelationInfo], undefined);

  const mockReviews = new Reviews([], new Links({}));

  beforeEach(() => {
    mockRelationServices = jasmine.createSpyObj('RelationService', ['getAllRelations']);
    mockRelationServices.getAllRelations.and.returnValue(of(mockRelations));

    mockOfferingService = jasmine.createSpyObj('OfferingService', ['getOfferingsByEvent']);
    mockOfferingService.getOfferingsByEvent.and.returnValue(of(mockOfferings));

    mockReviewService = jasmine.createSpyObj('ReviewService', ['getAllReviews']);
    mockReviewService.getAllReviews.and.returnValue(of(mockReviews));

    mockUserService = jasmine.createSpyObj('UserService', ['getUser']);
    mockUserService.getUser.and.returnValue(of(mockProvider));

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        EventService,
        { provide: RelationService, useValue: mockRelationServices },
        { provide: OfferingService, useValue: mockOfferingService },
        { provide: ReviewService, useValue: mockReviewService },
        { provide: UserService, useValue: mockUserService },
      ]
    });
    service = TestBed.inject(EventService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('#getEvents() should return an Observable<Events>', () => {
    const now = new Date();
    service.getEvents('http://localhost:8080/api/events?user=1&from=' + now.toISOString() + '&page=0').subscribe(events => {
      expect(events).toEqual(mockEvents);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/events?user=1&from=' + now.toISOString() + '&page=0' + '&pageSize=6');

    expect(req.request.method).toBe('GET');
    expect(req.request.body).toBeNull();

    req.flush([mockEvent]);
  });

  it('#getEvent() should return an Observable<Event>', () => {
    service.getEvent(1).subscribe(event => {
      expect(event).toEqual(mockEvent);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/events/1');

    expect(req.request.method).toBe('GET');
    expect(req.request.body).toBeNull();

    req.flush(mockEvent);
  });

  it('#getEventByUri() should return an Observable<Event>', () => {
    service.getEventByURI(mockEvent.self).subscribe(event => {
      expect(event).toEqual(mockEvent);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/events/1');

    expect(req.request.method).toBe('GET');
    expect(req.request.body).toBeNull();

    req.flush(mockEvent);
  });

  it('#deleteEvent() should send a DELETE request to the correct endpoint ', () => {
    service.deleteEvent(mockEvent).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/events/1');

    expect(req.request.method).toBe('DELETE');
    expect(req.request.body).toBeNull();

    req.flush({});
  });

  it('#editEvent() should send a PUT request to the correct endpoint ', () => {

    const auxMockEvent = { ...mockEvent, name: 'Mock event edited' };
    service.editEvent(auxMockEvent.self, auxMockEvent).subscribe(event => {
      expect(event).toEqual(auxMockEvent);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/events/1');

    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(auxMockEvent);

    req.flush(auxMockEvent);
  });

  it('#createEvent() should send a POST request to the correct endpoint ', () => {
    const mockJustCreatedEvent = { ...mockEvent, id: 0, self: '', relations: '', relatedOfferings: '', reviews: '', owner: '' };
    service.createEvent(mockJustCreatedEvent).subscribe(event => {
      expect(event).toEqual(mockEvent);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/events');

    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockJustCreatedEvent);

    req.flush({ mockEvent }, { headers: { Location: 'http://localhost:8080/api/events/1' } });

    const req2 = httpMock.expectOne('http://localhost:8080/api/events/1');

    expect(req2.request.method).toBe('GET');
    expect(req2.request.body).toBeNull();
    req2.flush(mockEvent);
  });

  it('#getEventRelated() should return an Observable<EventRelated>', () => {
    service.getEventRelated(mockEvent.self).subscribe(eventRelated => {
      expect(eventRelated).toEqual(mockEventRelated);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/events/1');

    expect(req.request.method).toBe('GET');
    expect(req.request.body).toBeNull();
    req.flush(mockEvent);
  });
});
