import { inject } from '@angular/core';
import { catchError, exhaustMap, map, of, switchMap, tap } from 'rxjs';
import { Actions, createEffect, ofType } from '@ngrx/effects';

import { UserService } from 'src/app/services/userService/user.service';
import { UserChangePasswordActions, UserFetchActions, UserLoginActions, UserLogoutActions, UserUpdateActions, UserVerifyActions } from './user.actions';
import { AuthService } from 'src/app/services/authService/auth-service.service';
import { HttpErrorResponse } from '@angular/common/http';

export const loginEffect = createEffect((
    actions$ = inject(Actions),
    authService = inject(AuthService)
) => {
    return actions$.pipe(
        ofType(UserLoginActions.login),
        switchMap(({ username, password, rememberMe }) => {
            return authService.login(username, password, rememberMe).pipe(
                map(user => UserLoginActions.loginComplete({ user, rememberMe })),
                catchError(error => of(UserLoginActions.loginError({ error })))
            );
        })
    );
}, { functional: true }
);

export const logoutEffect = createEffect(
    (actions$ = inject(Actions), authService = inject(AuthService)) => {
        return actions$.pipe(
            ofType(UserLogoutActions.logout),
            switchMap(() => authService.logout().pipe(
                map(() => UserLogoutActions.logoutComplete()),
                catchError(error => of(UserLogoutActions.logoutError({ error })))
            ))
        );
    }, { functional: true }
);

export const fetchEffect = createEffect(
    (actions$ = inject(Actions), authService = inject(AuthService), userService = inject(UserService)) => {
        return actions$.pipe(
            ofType(UserFetchActions.fetchUser),
            switchMap(() => {
                const userUri = authService.getLoggedUser();
                if (!userUri) {
                    return of(UserFetchActions.fetchUserError({ error: new HttpErrorResponse({ status: 401 }) }));
                }
                return userService.getUser(userUri).pipe(
                    map(user => UserFetchActions.fetchUserComplete({ user })),
                    catchError(error => of(UserFetchActions.fetchUserError({ error })))
                );
            })
        );
    }, { functional: true }
);

export const updateEffect = createEffect(
    (actions$ = inject(Actions), userService = inject(UserService)) => {
        return actions$.pipe(
            ofType(UserUpdateActions.updateUser),
            switchMap(({ uri, user, profileImageData }) => {
                return userService.updateUser(uri, user, profileImageData).pipe(
                    map(user => UserUpdateActions.updateUserComplete({ user })),
                    catchError(error => of(UserUpdateActions.updateUserError({ error })))
                );
            })
        );
    }, { functional: true }
);

export const becomeProviderEffect = createEffect(
    (actions$ = inject(Actions), userService = inject(UserService)) => {
        return actions$.pipe(
            ofType(UserUpdateActions.userBecomeProvider),
            switchMap(({ uri }) => {
                return userService.becomeProvider(uri, true).pipe(
                    map((user) => UserUpdateActions.updateUserComplete({ user })),
                    catchError(error => of(UserUpdateActions.updateUserError({ error })))
                );
            })
        );
    }, { functional: true }
);

export const changePasswordEffect = createEffect(
    (actions$ = inject(Actions), userService = inject(UserService)) => {
        return actions$.pipe(
            ofType(UserChangePasswordActions.changePassword),
            switchMap(({ newPassword, token }) => {
                return userService.resetPassword(newPassword, token).pipe(
                    map(user => UserChangePasswordActions.changePasswordComplete({ user })),
                    catchError(error => of(UserChangePasswordActions.changePasswordError({ error })))
                );
            })
        );
    }, { functional: true }
);

export const verifyEffect = createEffect(
    (actions$ = inject(Actions), userService = inject(UserService)) => {
        return actions$.pipe(
            ofType(UserVerifyActions.verify),
            switchMap(({ token }) => {
                return userService.verify(token).pipe(
                    map(user => UserVerifyActions.verifyComplete({ user })),
                    catchError(error => of(UserVerifyActions.verifyError({ error })))
                );
            })
        );
    }, { functional: true }
);