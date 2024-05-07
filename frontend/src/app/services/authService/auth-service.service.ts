import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams, HttpRequest, HttpResponse } from "@angular/common/http";
import { Observable, Subject, of, throwError } from 'rxjs';
import { URI } from 'src/shared/types';
import { UserService } from '../userService/user.service';
import { switchMap, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { User } from 'src/shared/models/user.model';
import { VndType } from 'src/shared/VndType';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  apiAuthUrl = `${environment.apiUrl}/users`;
  apiServicesUrl = `${environment.apiUrl}/services`;


  constructor(private http: HttpClient) {
  }

  login(email: string, password: string, rememberMe: boolean): Observable<User> {

    const basicToken = btoa(`${email}:${password}`);
    const headers = new HttpHeaders({
      Authorization: `Basic ${basicToken}`
    }).set('Accept', VndType.APPLICATION_USERS);

    if (rememberMe) {
      localStorage.setItem('rememberMe', true.toString());
    } else {
      localStorage.removeItem('rememberMe');
    }

    const params = new HttpParams().set('email', email);

    return this.http.get<User[]>(`${this.apiAuthUrl}`, { params: params, headers: headers }).pipe(
      switchMap(users => users.length > 0 ? of(users[0]) : throwError(() => new Error('No user found'))),
    );
  }

  refreshToken(): Observable<void> {
    const refreshToken = AuthService.getRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token'));
    }
    const headers = new HttpHeaders({
      Authorization: `Bearer ${refreshToken}`
    });
    return this.http.get<void>(`${this.apiServicesUrl}`, { headers: headers });

  }


  private static getUserFromToken(token: string): URI | null {
    if (!token) {
      return null;
    }
    const parts = token.split('.');
    return JSON.parse(atob(parts[1])).user;
  }

  logout(): Observable<void> {
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('refreshToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('rememberMe');
    return of(undefined);
  }

  static getToken(): string | null {
    if (sessionStorage.getItem('token')) {
      return sessionStorage.getItem('token');
    } else {
      return null;
    }
  }

  static setToken(token: string): void {
    sessionStorage.setItem('token', token);
  }

  static setRefreshToken(token: string): void {
    if (localStorage.getItem('rememberMe') === 'true') {
      localStorage.setItem('refreshToken', token);
    } else {
      localStorage.removeItem('refreshToken');
      sessionStorage.setItem('refreshToken', token);
    }
  }

  static getRefreshToken(): string | null {
    if (sessionStorage.getItem('refreshToken')) {
      return sessionStorage.getItem('refreshToken');
    }
    return localStorage.getItem('refreshToken');
  }

  getLoggedUser(): URI | null {
    return AuthService.getUserFromToken(AuthService.getToken()!);
  }
}
