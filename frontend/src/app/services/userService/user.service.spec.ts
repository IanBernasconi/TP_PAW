import { TestBed } from "@angular/core/testing";

import { UserService } from "./user.service";
import {
  HttpClientTestingModule,
  HttpTestingController,
} from "@angular/common/http/testing";
import { User, UserUpdate } from "src/shared/models/user.model";
import { ImageService } from "../imageService/image.service";
import { of } from "rxjs";

describe("UserService", () => {
  let service: UserService;
  let httpMock: HttpTestingController;
  let mockImageService: jasmine.SpyObj<ImageService>;

  const mockUser: User = {
    userId: 1,
    averageRating: 0,
    totalLikes: 0,
    totalEventsWorkedOn: 0,
    self: "http://localhost:8080/api/users/1",
    createdEvents: "http://localhost:8080/api/users/1/createdEvents",
    createdOfferings: "http://localhost:8080/api/users/1/createdOfferings",
    providerRelations: "http://localhost:8080/api/users/1/providerRelations",
    occupiedDates: "http://localhost:8080/api/users/1/occupiedDates",
    name: "John Doe",
    email: "jhondoe@gmail.com",
    language: "en",
    description: "I am a description",
    provider: true,
    profilePicture: null,
  };

  beforeEach(() => {
    mockImageService = jasmine.createSpyObj("ImageService", ["uploadImage"]);

    // Set up a default return value
    mockImageService.uploadImage.and.returnValue(
      of("localhost:8080/api/images/1")
    );

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        UserService,
        { provide: ImageService, useValue: mockImageService },
      ],
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    // Ensure that there are no outstanding requests
    httpMock.verify();
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("#getUser() should return an Observable<User>", () => {
    service.getUser(mockUser.self).subscribe((user) => {
      expect(user).toEqual(mockUser);
    });

    const req = httpMock.expectOne("http://localhost:8080/api/users/1");
    expect(req.request.method).toBe("GET");
    req.flush(mockUser);
  });

  it('#getUserFromEmail() should return an Observable<User[]>', () => {
    service.getUserFromEmail(mockUser.email).subscribe(users => {
      expect(users).toEqual([mockUser]);
    });

    const req = httpMock.expectOne(`http://localhost:8080/api/users?email=${mockUser.email}`);
    expect(req.request.method).toBe('GET');
    req.flush([mockUser]);
  });

  it("#requestPasswordReset() should send send a PATCH request to the correct endpoint", () => {
    const resetPasswordData = { email: "test@example.com" };
    const spy = spyOn(service, 'getUserFromEmail').and.callThrough().and.returnValue(of([mockUser]));
    service.requestPasswordReset(resetPasswordData.email).subscribe();

    expect(spy).toHaveBeenCalledWith(resetPasswordData.email);

    const req = httpMock.expectOne(mockUser.self);
    expect(req.request.method).toBe("PATCH");
    expect(req.request.body).toEqual({ password: null });
    req.flush(mockUser);
  });

  it("#register() should send a POST request to the correct endpoint", () => {
    const mockUserUpdate: UserUpdate = {
      name: 'Mock User',
      email: 'mock@gmail.com',
      language: 'en',
      description: 'mock description',
      provider: false,
      profilePicture: null,
      password: 'mockPassword'
    }
    service.register(mockUserUpdate).subscribe();

    const req = httpMock.expectOne("http://localhost:8080/api/users");
    expect(req.request.method).toBe("POST");
    expect(req.request.body).toEqual(mockUserUpdate);

    req.flush({});
  });

  it("#updateUser() should send a PUT request to the correct endpoint ", () => {
    const auxMockUser = { ...mockUser, name: "John Doe Updated" };

    service.updateUser(mockUser.self, auxMockUser).subscribe((user) => {
      expect(user).toEqual(auxMockUser);
    });

    const req = httpMock.expectOne(mockUser.self);

    expect(req.request.method).toBe("PUT");
    expect(req.request.body).toEqual(auxMockUser);

    req.flush(auxMockUser);

    //  with profile picture
    const mockPicture = new File([""], "filename");
    const mockPictureData = {
      profileImage: mockPicture,
      removeProfilePicture: false,
    };
    service
      .updateUser(mockUser.self, mockUser, mockPictureData)
      .subscribe((user) => {
        expect(user).toEqual(mockUser);
      });

    const req2 = httpMock.expectOne(mockUser.self);

    expect(req2.request.method).toBe("PUT");
    expect(req2.request.body).toEqual({
      ...mockUser,
      profilePicture: "localhost:8080/api/images/1",
    });
    req2.flush(mockUser);

    // without profile picture and remove profile picture on true
    const mockPictureData2 = {
      profileImage: undefined,
      removeProfilePicture: true,
    };

    service
      .updateUser(mockUser.self, mockUser, mockPictureData2)
      .subscribe((user) => {
        expect(user).toEqual(mockUser);
      });

    const req3 = httpMock.expectOne(mockUser.self);
    expect(req3.request.method).toBe("PUT");
    expect(req3.request.body).toEqual({ ...mockUser, profilePicture: "" });
    req3.flush(mockUser);
  });

  it("#getUserOccupiedDates() with from range should return an Observable<Date[]>", () => {
    const mockExpectedDates: Date[] = [
      new Date("2021-06-01"),
      new Date("2021-06-02"),
      new Date("2021-06-03"),
    ];
    const mockOccupiedDates = {
      occupiedDates: mockExpectedDates.map((date) => date.toISOString()),
    };
    let mockRange = { from: "2021-06-01", to: null };
    service
      .getUserOccupiedDates(mockUser, mockRange)
      .subscribe((occupiedDates) => {
        expect(occupiedDates).toEqual(mockExpectedDates);
      });

    const req = httpMock.expectOne(
      "http://localhost:8080/api/users/1/occupiedDates?from=2021-06-01"
    );
    expect(req.request.method).toBe("GET");
    expect(req.request.params.get("from")).toBe("2021-06-01");
    expect(req.request.params.get("to")).toBeNull();

    req.flush(mockOccupiedDates);
  });

  it("#getUserOccupiedDates() with to range should return an Observable<Date[]>", () => {
    const mockOccupiedDates: Date[] = [
      new Date("2021-06-01"),
      new Date("2021-06-02"),
      new Date("2021-06-03"),
    ];
    const mockOccupiedDatesResponse = {
      occupiedDates: mockOccupiedDates.map((date) => date.toISOString()),
    };
    const mockRange = { from: null, to: "2021-06-03" };

    service
      .getUserOccupiedDates(mockUser, mockRange)
      .subscribe((occupiedDates) => {
        expect(occupiedDates).toEqual(mockOccupiedDates);
      });
    const req = httpMock.expectOne(
      "http://localhost:8080/api/users/1/occupiedDates?to=2021-06-03"
    );
    expect(req.request.method).toBe("GET");
    expect(req.request.params.get("from")).toBeNull();
    expect(req.request.params.get("to")).toBe("2021-06-03");

    req.flush(mockOccupiedDatesResponse);
  });
});
