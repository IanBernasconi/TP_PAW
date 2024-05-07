import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, UrlTree } from '@angular/router';
import { Observable, } from 'rxjs';
import { Store } from '@ngrx/store';
import { filter, map, switchMap, take } from 'rxjs/operators';
import { selectUser, selectUserIsLoading } from 'src/app/store/user/user.selector';
import { ToastMessages, ToastService } from 'src/app/services/toastService/toast.service';
import { UserUrlActions } from 'src/app/store/user/user.actions';

@Injectable({
  providedIn: 'root'
})
class LoginGuard {
  constructor(private store: Store, private router: Router, private toastService: ToastService) { }

  canActivate(
    route: ActivatedRouteSnapshot,
  ): Observable<boolean | UrlTree> {
    return this.store.select(selectUserIsLoading).pipe(
      filter(loading => !loading),
      take(1),
      switchMap(() => {
        return this.store.select(selectUser).pipe(
          map(user => {
            if (!!user) {
              return true;
            }
            this.store.dispatch(UserUrlActions.setUrl({ url: route.url.join('/') }));
            this.toastService.warn(ToastMessages.access.loginNeeded);
            return this.router.parseUrl('/user/login')
          }
          ));
      })
    )
  }
}

export const IsLoggedGuard: CanActivateFn = (route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> => {
  return inject(LoginGuard).canActivate(route);
}