import { TestBed } from '@angular/core/testing';

import { MessageService } from './message.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Message, Messages } from 'src/shared/models/message.model';
import { Links } from 'src/shared/models/pagination-utils.model';
import { OfferingStatus } from 'src/shared/models/offering-status.model';
import { Relation, RelationReadStatus, Relations } from 'src/shared/models/relation.model';
import { RelationService } from '../relationService/relation.service';
import { of } from 'rxjs';
import { EventService } from '../eventService/event-service.service';
import { Event, Events } from 'src/shared/models/event.model';
import { Offering, ProviderChatData } from 'src/shared/models/offering.model';
import { District, OfferingCategory, PriceType } from 'src/shared/models/utils.model';

describe('MessageService', () => {
  let service: MessageService;
  let httpMock: HttpTestingController;
  let mockRelationService: jasmine.SpyObj<RelationService>;
  let mockEventService: jasmine.SpyObj<EventService>;

  const mockRelationURI = 'http://localhost:8080/api/relations/1';

  const mockUser1Uri = 'http://localhost:8080/api/users/1';

  const mockMessage1: Message = {
    self: 'http://localhost:8080/api/messages/1',
    relation: mockRelationURI,
    sender: mockUser1Uri,
    receiver: 'http://localhost:8080/api/users/2',
    message: 'Mock message 1',
    timestamp: new Date(),
    isRead: true
  }

  const mockMessage2: Message = {
    self: 'http://localhost:8080/api/messages/2',
    relation: mockRelationURI,
    sender: 'http://localhost:8080/api/users/2',
    receiver: mockUser1Uri,
    message: 'Mock message 2',
    timestamp: new Date(),
    isRead: false
  }

  const mockMessages = new Messages([mockMessage1, mockMessage2], new Links({}));

  const mockRelation: Relation = {
    relationId: 1,
    status: OfferingStatus.ACCEPTED as keyof typeof OfferingStatus,
    self: 'http://localhost:8080/api/relations/1',
    event: 'http://localhost:8080/api/events/1',
    offering: 'http://localhost:8080/api/services/1',
    review: 'http://localhost:8080/api/relations/1/review',
    lastMessage: 'http://localhost:8080/api/messages/1',
    messages: 'http://localhost:8080/api/relations/1/messages',
    organizer: 'http://localhost:8080/api/users/2',
    provider: 'http://localhost:8080/api/users/1',
    providerUnreadMessagesCount: 0,
    organizerUnreadMessagesCount: 0
  }

  const mockEvent = new Event(1, 'Mock event', 'Mock description', '2021-05-05', 10, 'http://localhost:8080/api/events/1', 'http://localhost:8080/api/users/1', "AGRONOMIA" as keyof District, 'http://localhost:8080/api/relations?event=1', 'http://localhost:8080/api/services?event=1', 'http://localhost:8080/api/reviews?event=1', 'http://localhost:8080/api/events/1/guests')

  const mockOffering1: Offering = {
    id: 1,
    deleted: false,
    likes: 0,
    rating: 0,
    self: 'http://localhost:8080/api/services/1',
    owner: 'http://localhost:8080/api/users/1',
    reviews: 'http://localhost:8080/api/services/1/reviews',
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

  const mockRelations = new Relations([mockRelation], new Links({}));

  const mockProviderChatData: ProviderChatData = {
    offering: mockOffering1,
    relations: mockRelations,
    events: new Events([mockEvent], new Links({})),
    lastMessagesByRelation: new Map([[mockRelationURI, mockMessage1]])
  }

  beforeEach(() => {
    mockRelationService = jasmine.createSpyObj('RelationService', ['getAllRelations']);
    mockRelationService.getAllRelations.and.returnValue(of(mockRelations));

    mockEventService = jasmine.createSpyObj('EventService', ['getEventsByURIs']);
    mockEventService.getEventsByURIs.and.returnValue(of([mockEvent]));

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        MessageService,
        { provide: RelationService, useValue: mockRelationService },
        { provide: EventService, useValue: mockEventService }
      ]
    });
    service = TestBed.inject(MessageService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('#getMessages() should return an Observable<Messages>', () => {
    service.getMessages(mockRelationURI + '/messages').subscribe(messages => {
      expect(messages).toEqual(mockMessages);
    });

    const req = httpMock.expectOne(mockRelationURI + '/messages');
    expect(req.request.method).toBe('GET');
    expect(req.request.body).toBeNull();
    req.flush([mockMessage1, mockMessage2]);
  });

  it('#getMessages() should return an Observable<Messages> with previous messages', () => {
    const mockPreviousMessages = new Messages([mockMessage1], new Links({}));
    service.getMessages(mockRelationURI + '/messages', mockPreviousMessages).subscribe(messages => {
      expect(messages).toEqual(jasmine.objectContaining({
        messages: mockMessages.messages,
        links: mockMessages.links
      }));
    });

    const req = httpMock.expectOne(mockRelationURI + '/messages');
    expect(req.request.method).toBe('GET');
    expect(req.request.body).toBeNull();
    req.flush([mockMessage1, mockMessage2]);
  });

  it('#sendMessage() should send a POST request to the correct endpoint and return an Observable<Message>', () => {
    service.sendMessage(mockMessage1).subscribe(message => {
      expect(message).toEqual(mockMessage1);
    });

    const req = httpMock.expectOne(mockRelationURI + '/messages');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockMessage1);
    req.flush(mockMessage1, { headers: { Location: mockMessage1.self } });
  });

  it('#markConvesarionAsRead() should send a PATCH request to the correct endpoint and return an Observable<Relation>', () => {
    const mockAuxRelation = { ...mockRelation, providerUnreadMessagesCount: 1 };

    service.markConversationAsRead(mockAuxRelation, mockMessage1.sender).subscribe(relation => {
      expect(relation).toEqual(mockRelation);
    });

    const req = httpMock.expectOne(mockRelationURI);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual(new RelationReadStatus(mockMessage1.sender, true));
    req.flush(mockRelation);

    const mockAuxRelation2 = { ...mockRelation, organizerUnreadMessagesCount: 1 };
    service.markConversationAsRead(mockAuxRelation2, mockMessage1.receiver).subscribe(relation => {
      expect(relation).toEqual(mockRelation);
    });
    const req2 = httpMock.expectOne(mockRelationURI);
    expect(req2.request.method).toBe('PATCH');
    expect(req2.request.body).toEqual(new RelationReadStatus(mockMessage1.receiver, true));
    req2.flush(mockRelation);
  });

  it('#getLastMessagesByRelations() should send a GET request to the correct endpoint and return an Observable<Map<URI, Message>>', () => {
    service.getLastMessagesByRelations([mockRelation]).subscribe(messages => {
      expect(messages).toEqual(new Map([[mockRelationURI, mockMessage1]]));
    });

    const req = httpMock.expectOne(mockRelation.lastMessage!);
    expect(req.request.method).toBe('GET');
    expect(req.request.body).toBeNull();
    req.flush(mockMessage1);
  });

  it('#getProviderChatData() should return an Observable<ProviderChatData>', () => {
    spyOn(service, 'getRelationsWithLastMessages').and.returnValue(of({ relations: mockRelations.relations, lastMessagesByUri: new Map([[mockRelationURI, mockMessage1]]) }));
    service.getProviderChatData(mockRelation.provider, mockOffering1).subscribe(data => {
      expect(data).toEqual(mockProviderChatData);
    });
  });

  it('#getRelationsWithLastMessages() should return an Observable<{ relations: Relation[], lastMessagesByUri: Map<URI, Message> }>', () => {
    spyOn(service, 'getLastMessagesByRelations').and.returnValue(of(new Map([[mockRelationURI, mockMessage1]])));
    service.getRelationsWithLastMessages(mockRelations.relations).subscribe(data => {
      expect(data).toEqual({ relations: mockRelations.relations, lastMessagesByUri: new Map([[mockRelationURI, mockMessage1]]) });
    });
  });
});
