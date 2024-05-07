import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import {
  Observable,
  catchError,
  map,
  of,
  switchMap,
  tap,
  throwError,
} from "rxjs";
import {
  User,
  UserPasswordUpdate,
  UserUpdate,
} from "src/shared/models/user.model";
import { URI } from "src/shared/types";
import { VndType } from "src/shared/VndType";
import { ImageService } from "../imageService/image.service";
import { environment } from "src/environments/environment";
import { RangeFilter } from "src/shared/models/event.model";

@Injectable({
  providedIn: "root",
})
export class UserService {
  apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient, private imageService: ImageService) { }

  getUser(user: URI): Observable<User> {
    const headers = new HttpHeaders().set("Accept", VndType.APPLICATION_USER);
    return this.http.get<User>(user, { headers });
  }

  requestPasswordReset(email: string) {
    return this.getUserFromEmail(email).pipe(
      switchMap((users) => {
        if (users.length > 0) {
          const user = users[0];
          const headers = new HttpHeaders().set(
            "Content-Type",
            VndType.APPLICATION_USER_RESET_PASSWORD
          );
          return this.http.patch(`${user.self}`, { password: null }, { headers: headers });
        }
        return of(null);
      })
    );
  }

  resetPassword(newPassword: string, token: string): Observable<User> {
    const headers = new HttpHeaders().set("Content-Type", VndType.APPLICATION_USER_PASSWORD)
      .set("Accept", VndType.APPLICATION_USER)
      .set("Authorization", `Bearer ${token}`);

    try {
      const parts = token.split(".");
      const json = JSON.parse(atob(parts[1]));
      if (!json.user)
        return throwError(() => new Error("Invalid token"));
      const url = json.user;
      const body: UserPasswordUpdate = {
        password: newPassword,
      };
      return this.http.patch<User>(`${url}`, body, { headers: headers });
    } catch (e) {
      console.error(e);
      return throwError(() => new Error("Invalid token"));
    }
  }

  verify(token: string): Observable<User> {
    const headers = new HttpHeaders().set("Content-Type", VndType.APPLICATION_USER_VERIFY)
      .set("Accept", VndType.APPLICATION_USER)
      .set("Authorization", `Bearer ${token}`);

    const parts = token.split(".");
    const verifyUrl = JSON.parse(atob(parts[1])).user;

    const body = {
      verified: true,
    };

    return this.http.patch<User>(`${verifyUrl}`, body, { headers });
  }

  getUserFromEmail(email: string): Observable<User[]> {
    const headers = new HttpHeaders().set("Accept", VndType.APPLICATION_USERS);
    const params = new HttpParams().set("email", email);
    return this.http.get<User[]>(`${this.apiUrl}`, { headers, params });
  }

  isEmailTaken(email: string): Observable<boolean> {
    return this.getUserFromEmail(email).pipe(map((users) => users.length > 0));
  }

  register(user: UserUpdate): Observable<any> {
    const headers = new HttpHeaders().set(
      "Content-Type",
      VndType.APPLICATION_USER
    );
    return this.http.post<any>(`${this.apiUrl}`, user, { headers });
  }

  becomeProvider(uri: URI, isProvider: boolean): Observable<User> {
    const headers = new HttpHeaders()
      .set("Content-Type", VndType.APPLICATION_USER_PROVIDER)
      .set("Accept", VndType.APPLICATION_USER);

    const body = {
      provider: isProvider,
    };
    return this.http.patch<User>(`${uri}`, body, { headers });
  }

  updateUser(
    uri: URI,
    updatedUser: UserUpdate,
    profileImageData?: ImageUpdateData
  ): Observable<User> {
    if (profileImageData?.profileImage) {
      return this.uploadProfilePicture(profileImageData.profileImage).pipe(
        map((profilePicture) => ({ ...updatedUser, profilePicture })),
        switchMap((updatedUserWithProfilePicture) =>
          this.updateUserApiCall(uri, updatedUserWithProfilePicture)
        )
      );
    } else if (profileImageData?.removeProfilePicture) {
      return this.updateUserApiCall(uri, {
        ...updatedUser,
        profilePicture: "",
      }); // An empty string is used to remove the profile picture
    } else {
      return this.updateUserApiCall(uri, {
        ...updatedUser,
        profilePicture: null,
      });
    }
  }

  getUserOccupiedDates(user: User, range: RangeFilter): Observable<Date[]> {
    const headers = new HttpHeaders().set(
      "Accept",
      VndType.APPLICATION_USER_OCCUPIED_DATES
    );
    let params = new HttpParams();
    if (range.from) params = params.set("from", range.from);
    if (range.to) params = params.set("to", range.to);
    return this.http
      .get<{ occupiedDates: string[] }>(`${user.occupiedDates}`, {
        params,
        headers,
      })
      .pipe(
        map((response) => response.occupiedDates.map((date) => new Date(date)))
      );
  }

  private uploadProfilePicture(file: File): Observable<URI> {
    return this.imageService.uploadImage(file);
  }

  private updateUserApiCall(
    uri: URI,
    updatedUser: UserUpdate
  ): Observable<User> {
    const headers = new HttpHeaders()
      .set("Accept", VndType.APPLICATION_USER)
      .set("Content-Type", VndType.APPLICATION_USER);
    return this.http.put<User>(uri, updatedUser, { headers });
  }
}

export interface ImageUpdateData {
  profileImage?: File;
  removeProfilePicture: boolean;
}
