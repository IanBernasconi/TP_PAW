import { Injectable } from "@angular/core";
import { AbstractControl, AsyncValidator, ValidationErrors } from "@angular/forms";
import { Observable, catchError, map, of } from "rxjs";
import { UserService } from "src/app/services/userService/user.service";

@Injectable({ providedIn: 'root' })
export class UniqueEmailValidator implements AsyncValidator {
    constructor(private userService: UserService) { }

    validate(control: AbstractControl): Observable<ValidationErrors | null> {
        return this.userService.isEmailTaken(control.value).pipe(
            map((isEmailTaken) => (isEmailTaken ? { uniqueEmail: true } : null)),
            catchError(() => of(null)),
        );
    }
}