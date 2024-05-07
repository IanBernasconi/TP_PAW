import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, CanActivateFn, Router, UrlTree } from '@angular/router';
import { Observable, firstValueFrom, of } from 'rxjs';
import { Store } from '@ngrx/store';
import { filter, map, switchMap, take } from 'rxjs/operators';
import { selectUser, selectUserIsLoading } from 'src/app/store/user/user.selector';
import { ToastMessages, ToastService } from 'src/app/services/toastService/toast.service';

@Injectable({
  providedIn: 'root'
})
class ProviderGuard {
  constructor(private store: Store, private router: Router, private toastService: ToastService) { }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> {
    return this.store.select(selectUserIsLoading).pipe(
      filter(loading => !loading),
      take(1),
      switchMap(() => {
        return this.store.select(selectUser).pipe(
          map(user => {
            if (!!user && user.provider === true) {
              return true;
            }
            this.toastService.warn(ToastMessages.access.providerStatusNeeded);
            return this.router.parseUrl('/user/profile');
          }
          ));
      })
    )
  }
}

export const IsProviderGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> => {
  return inject(ProviderGuard).canActivate(route, state);
}