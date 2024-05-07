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
class NotLoginGuard {
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
            if (!user) {
              return true;
            }
            this.toastService.warn(ToastMessages.access.noLoginNeeded);
            return this.router.parseUrl('services')
          })
        );
      })
    )
  }
}

export const IsNotLoggedGuard: CanActivateFn = (route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> => {
  return inject(NotLoginGuard).canActivate(route);
}