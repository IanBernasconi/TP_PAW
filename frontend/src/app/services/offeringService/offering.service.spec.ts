import { TestBed } from '@angular/core/testing';

import { OfferingService } from './offering.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Offering, OfferingFilter, Offerings } from 'src/shared/models/offering.model';
import { Links } from 'src/shared/models/pagination-utils.model';
import { ImageService } from '../imageService/image.service';
import { of } from 'rxjs';
import { EventService } from '../eventService/event-service.service';
import { Event } from 'src/shared/models/event.model';
import { District, OfferingCategory, PriceType } from 'src/shared/models/utils.model';

describe('OfferingService', () => {
  let service: OfferingService;
  let httpMock: HttpTestingController;
  let mockImageService: jasmine.SpyObj<ImageService>;
  let mockEventService: jasmine.SpyObj<EventService>;

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

  const mockOffering2: Offering = {
    id: 2,
    deleted: false,
    likes: 0,
    rating: 0,
    self: 'http://localhost:8080/api/services/2',
    owner: 'http://localhost:8080/api/users/2',
    reviews: 'http://localhost:8080/api/services/2/reviews',
    name: 'Offering 2',
    description: 'Description 2',
    category: "DECORATION" as keyof OfferingCategory,
    minPrice: 10,
    maxPrice: 100,
    priceType: "OTHER" as keyof PriceType,
    maxGuests: 10,
    district: "AGRONOMIA" as keyof District,
    images: [],
  }

  const mockOfferings = new Offerings([mockOffering1, mockOffering2], new Links({}));

  const mockEvent = new Event(1, 'Mock event', 'Mock description', '2021-05-05', 10, 'http://localhost:8080/api/events/1', 'http://localhost:8080/api/users/1', "AGRONOMIA" as keyof District, 'http://localhost:8080/api/relations?event=1', 'http://localhost:8080/api/services?event=1', 'http://localhost:8080/api/reviews?event=1', 'http://localhost:8080/api/events/1/guests')

  beforeEach(() => {
    mockImageService = jasmine.createSpyObj('ImageService', ['uploadImage']);
    // Set up a default return value
    mockImageService.uploadImage.and.returnValue(of('localhost:8080/api/images/1'));

    mockEventService = jasmine.createSpyObj('EventService', ['getEvent']);
    mockEventService.getEvent.and.returnValue(of(mockEvent));


    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        OfferingService,
        { provide: ImageService, useValue: mockImageService }
      ]
    });
    service = TestBed.inject(OfferingService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    // Ensure that there are no outstanding requests
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('#getOfferingByURI() should return an Observable<Offering>', () => {

    service.getOfferingByURI(mockOffering1.self).subscribe(offering => {
      expect(offering).toEqual(mockOffering1);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/services/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockOffering1);
  });

  it('#getOfferings() should return an Observable<Offerings>', () => {

    service.getOfferings().subscribe(offerings => {
      expect(offerings).toEqual(mockOfferings);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/services');

    expect(req.request.method).toBe('GET');

    req.flush([mockOffering1, mockOffering2]);
  });

  it('#getOfferings() should return an Observable<Offerings> with filter', () => {
    const mockOfferingFilter = new OfferingFilter("CATERING" as keyof OfferingCategory)

    service.getOfferings(mockOfferingFilter).subscribe(offerings => {
      expect(offerings).toEqual(new Offerings([mockOffering1], new Links({})));
    });

    const req = httpMock.expectOne('http://localhost:8080/api/services?category=CATERING&minPrice=1&maxPrice=1000000&attendees=0&districts=ALL&sortType=RATING_DESC&search=');

    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('category')).toBe('CATERING');

    req.flush([mockOffering1]);
  })

  it('#createOffering() should send a POST request to the correct endpoint', () => {
    service.createOffering(mockOffering1).subscribe(offering => {
      expect(offering).toEqual(mockOffering1);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/services');

    expect(req.request.method).toBe('POST');

    req.flush({ mockOffering1 }, { headers: { location: 'http://localhost:8080/api/services/1' } });

    const req2 = httpMock.expectOne('http://localhost:8080/api/services/1');
    expect(req2.request.method).toBe('GET');
    expect(req2.request.body).toBeNull();
    req2.flush(mockOffering1);
  });

  it('#deleteOffering() should send a DELETE request to the correct endpoint', () => {
    service.deleteOffering(mockOffering1).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/services/1');

    expect(req.request.method).toBe('DELETE');

    req.flush({});
  });

  it('#editOffering() should send a PUT request to the correct endpoint', () => {
    const auxMockOffering = { ...mockOffering1, name: 'Offering 1 edited' };
    service.editOffering(mockOffering1.self, auxMockOffering).subscribe(offering => {
      expect(offering).toEqual(auxMockOffering);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/services/1');
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(auxMockOffering);

    req.flush(auxMockOffering);
  });

  it('#getOfferingsByEvent() should return an Observable<Offerings>', () => {
    service.getOfferingsByEvent(mockEvent).subscribe(offerings => {
      expect(offerings).toEqual(mockOfferings);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/services?event=1');

    expect(req.request.method).toBe('GET');

    req.flush([mockOffering1, mockOffering2]);
  });

  it('#getOffering() should return an Observable<Offering>', () => {
    service.getOffering(1).subscribe(offering => {
      expect(offering).toEqual(mockOffering1);
    });

    const req = httpMock.expectOne('http://localhost:8080/api/services/1');

    expect(req.request.method).toBe('GET');

    req.flush(mockOffering1);
  })


});
