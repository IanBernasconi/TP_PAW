import { Injectable, inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanActivateFn, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
class IdMustBeNumber {

  constructor(private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot): boolean {

    const idParams = route.data['idParams'];
    if (!idParams) {
      // If there are no idParams, we don't need to check anything
      return true;
    }

    for (const idParamName of idParams) {
      const id = route.paramMap.get(idParamName);
      const isNumber = !isNaN(Number(id));

      if (!isNumber) {
        // Redirect to the NotFoundPage if the id is not a number
        this.router.navigate(['/not-found']);
        return false;
      }
    }

    return true;
  }

}

export const IdMustBeNumberGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  return inject(IdMustBeNumber).canActivate(route);
}