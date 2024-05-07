import { TestBed } from '@angular/core/testing';

import { RelationService } from './relation.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Relation, Relations } from 'src/shared/models/relation.model';
import { OfferingStatus } from 'src/shared/models/offering-status.model';
import { URI } from 'src/shared/types';
import { Links } from 'src/shared/models/pagination-utils.model';
import { EventService } from '../eventService/event-service.service';
import { OfferingService } from '../offeringService/offering.service';
import { of } from 'rxjs';
import { Event } from 'src/shared/models/event.model';
import { Offering } from 'src/shared/models/offering.model';
import { District, OfferingCategory, PriceType } from 'src/shared/models/utils.model';

describe('RelationService', () => {
  let service: RelationService;
  let httpMock: HttpTestingController;
  let mockEventService: jasmine.SpyObj<EventService>;
  let mockOfferingService: jasmine.SpyObj<OfferingService>;

  const mockRelation1: Relation = {
    relationId: 1,
    status: 'NEW',
    self: 'http://localhost:8080/api/relations/1',
    event: 'http://localhost:8080/api/events/1',
    offering: 'http://localhost:8080/api/offerings/1',
    review: 'http://localhost:8080/api/relations/1/review',
    lastMessage: 'http://localhost:8080/api/messages/1',
    messages: 'http://localhost:8080/api/relations/1/messages',
    organizer: 'http://localhost:8080/api/users/2',
    provider: 'http://localhost:8080/api/users/1',
    providerUnreadMessagesCount: 0,
    organizerUnreadMessagesCount: 0
  };

  const mockRelation2: Relation = {
    relationId: 2,
    status: OfferingStatus.PENDING as keyof typeof OfferingStatus,
    self: 'http://localhost:8080/api/relations/2',
    event: 'http://localhost:8080/api/events/1',
    offering: 'http://localhost:8080/api/offerings/2',
    provider: 'http://localhost:8080/api/users/3',
    organizer: 'http://localhost:8080/api/users/2',
    messages: 'http://localhost:8080/api/relations/2/messages',
    review: 'http://localhost:8080/api/relations/2/review',
    lastMessage: 'http://localhost:8080/api/messages/2',
    providerUnreadMessagesCount: 0,
    organizerUnreadMessagesCount: 0
  };

  const mockRelations = new Relations([mockRelation1, mockRelation2], new Links({}));

  const mockEvent = new Event(1, 'Mock event', 'Mock description', '2021-05-05', 10, 'http://localhost:8080/api/events/1', 'http://localhost:8080/api/users/1', "AGRONOMIA" as keyof District, 'http://localhost:8080/api/relations?event=1', 'http://localhost:8080/api/services?event=1', 'http://localhost:8080/api/reviews?event=1', 'http://localhost:8080/api/events/1/guests')
  const mockOffering: Offering = {
    id: 1,
    deleted: false,
    likes: 0,
    rating: 0,
    self: 'http://localhost:8080/api/offerings/1',
    owner: 'http://localhost:8080/api/users/1',
    reviews: 'http://localhost:8080/api/offerings/1/reviews',
    name: 'Offering 1',
    description: 'Description 1',
    category: "CATERING" as keyof OfferingCategory,
    minPrice: 10,
    maxPrice: 100,
    priceType: "OTHER" as keyof PriceType,
    maxGuests: 10,
    district: "AGRONOMIA" as keyof District,
    images: [],
  }

  beforeEach(() => {
    mockEventService = jasmine.createSpyObj('EventService', ['getEvent']);
    mockEventService.getEvent.and.returnValue(of(mockEvent));
    mockOfferingService = jasmine.createSpyObj('OfferingService', ['getOffering']);
    mockOfferingService.getOffering.and.returnValue(of(mockOffering));

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        RelationService,
        { provide: EventService, useValue: mockEventService },
        { provide: OfferingService, useValue: mockOfferingService }
      ]
    });
    service = TestBed.inject(RelationService);
    httpMock = TestBed.inject(HttpTestingController);

  });

  afterEach(() => {
    // Ensure that there are no outstanding requests
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('#getRelations() should return an Observable<Relations>', () => {

    service.getRelations(mockEvent.relations).subscribe((relations) => {
      expect(relations).toEqual(mockRelations);
    })

    const req = httpMock.expectOne('http://localhost:8080/api/relations?event=1');

    expect(req.request.method).toBe('GET');

    req.flush([mockRelation1, mockRelation2]);

    service.getRelations(mockEvent.relations, { status: [OfferingStatus.ACCEPTED as keyof typeof OfferingStatus] }).subscribe((relations) => {
      expect(relations).toEqual(new Relations([mockRelation1], new Links({})));
    });

    const req2 = httpMock.expectOne('http://localhost:8080/api/relations?event=1&status=Accepted');
    expect(req2.request.method).toBe('GET');
    req2.flush([mockRelation1]);
  });

  it('#createRelation() should return an Observable<URI>', () => {
    service.createRelation(mockEvent.self, mockOffering.self).subscribe((uri) => {
      expect(uri).toEqual(mockRelation1.self);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/relations');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      event: mockEvent.self,
      offering: mockOffering.self,
      status: 'NEW'
    });

    req.flush({ uri: mockRelation1.self }, { headers: { Location: mockRelation1.self } });
  });

  it('#getAllRelations() should return an Observable<Relations>', () => {
    service.getAllRelations(mockEvent.relations).subscribe((relations) => {
      expect(relations).toEqual(mockRelations);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/relations?event=1&pageSize=100');
    expect(req.request.method).toBe('GET');
    req.flush([mockRelation1, mockRelation2]);
  });

});
