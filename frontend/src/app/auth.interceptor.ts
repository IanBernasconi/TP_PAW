import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse,
  HttpResponse
} from '@angular/common/http';
import { EMPTY, Observable, Subject, catchError, concatMap, filter, finalize, mergeMap, retry, retryWhen, switchMap, take, tap, throwError } from 'rxjs';
import { AuthService } from "./services/authService/auth-service.service";
import { Router } from '@angular/router';
import { ToastMessages, ToastService } from './services/toastService/toast.service';
import { Store } from '@ngrx/store';
import { UserLogoutActions } from './store/user/user.actions';
import { th } from 'date-fns/locale';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor() { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    const token = AuthService.getToken();
    if (token != null && req.headers.get('Authorization') == null) {
      req = req.clone({
        setHeaders: {
          'Authorization': `Bearer ${token}`,
        },
      });
    }

    return next.handle(req);
  }

}


@Injectable()
export class UnauthorizedResponseInterceptor implements HttpInterceptor {

  private refreshTokenInProgress = false;
  private refreshTokenSubject: Subject<any> = new Subject<any>();

  constructor(private router: Router, private toastService: ToastService, private store: Store, private authService: AuthService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError(e => {
        if (e instanceof HttpErrorResponse && e.status === 401) {
          if (req.headers.get('Authorization') != null) {
            const authHeader = req.headers.get('Authorization');
            if (authHeader != null) {
              const token = authHeader.split(' ')[0];
              if (token === 'Basic') {
                this.toastService.warn(ToastMessages.access.loginFailed);
                this.router.navigate(['user', 'login']);
                return throwError(() => e);
              }
            }
          }
          const refreshToken = AuthService.getRefreshToken();
          if (refreshToken != null) {
            return this.refreshTokenRequest(refreshToken, next, req);
          } else {
            this.logoutAndRedirect();
            return throwError(() => e);
          }
        } else {
          return throwError(() => e);
        }
      })
    );
  }

  private refreshTokenRequest(refreshToken: string, next: HttpHandler, req: HttpRequest<any>) {
    if (this.refreshTokenInProgress) {
      return this.refreshTokenSubject.pipe(
        filter(result => result !== null),
        take(1),
        switchMap((newToken) => {
          return next.handle(this.changeRequestToken(req, newToken));
        })
      );
    } else {
      this.refreshTokenInProgress = true;
      this.refreshTokenSubject.next(null);
      return next.handle(this.changeRequestToken(req, refreshToken)).pipe(
        tap({
          next: val => {
            if (val instanceof HttpResponse) {
              this.refreshTokenInProgress = false;
              this.refreshTokenSubject.next(val.headers.get('X-Authtoken'));
            }
          }
        }),
      );
    }
  }

  private changeRequestToken(req: HttpRequest<any>, token: string) {
    return req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  private logoutAndRedirect() {
    this.store.dispatch(UserLogoutActions.logout());
    this.toastService.warn(ToastMessages.access.sessionExpired);
    this.router.navigate(['user', 'login']);
  }
}

@Injectable()
export class AuthorizedResponseInterceptor implements HttpInterceptor {


  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    return next.handle(req).pipe(
      tap({
        next: val => {
          if (val instanceof HttpResponse) {
            const authHeader = val.headers.get('X-Authtoken');
            if (authHeader != null) {
              AuthService.setToken(authHeader);
            }
            const refreshTokenHeader = val.headers.get('X-RefreshToken');
            if (refreshTokenHeader != null) {
              AuthService.setRefreshToken(refreshTokenHeader);
            }
          }
        }
      })
    )
  }
}


@Injectable()
export class ForbiddenResponseInterceptor implements HttpInterceptor {

  constructor(private router: Router, private toastService: ToastService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    return next.handle(req).pipe(
      tap({
        error: e => {
          if (e instanceof HttpErrorResponse && e.status === 403) {
            this.toastService.error(ToastMessages.access.forbidden);
            this.router.navigate(['services']);
          }
        }
      })
    )
  }

}