import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot } from '@angular/router';
import { createActionGroup, props } from '@ngrx/store';
import { ImageUpdateData } from 'src/app/services/userService/user.service';
import { User, UserUpdate } from "src/shared/models/user.model";
import { URI } from 'src/shared/types';

export const UserLoginActions = createActionGroup({
    source: 'User-Login',
    events: {
        'Login': props<{ username: string, password: string, rememberMe: boolean }>(),
        'LoginComplete': props<{ user: User, rememberMe: boolean }>(),
        'LoginError': props<{ error: HttpErrorResponse }>()
    }
});

export const UserLogoutActions = createActionGroup({
    source: 'User-Logout',
    events: {
        'Logout': props,
        'LogoutComplete': props,
        'LogoutError': props<{ error: HttpErrorResponse }>()
    }
});

export const UserFetchActions = createActionGroup({
    source: 'User-Fetch',
    events: {
        'FetchUser': props,
        'FetchUserComplete': props<{ user: User }>(),
        'FetchUserError': props<{ error: HttpErrorResponse }>()
    }
});

export const UserChangePasswordActions = createActionGroup({
    source: 'User-ChangePassword',
    events: {
        'ChangePassword': props<{ newPassword: string, token: string }>(),
        'ChangePasswordComplete': props<{ user: User }>(),
        'ChangePasswordError': props<{ error: HttpErrorResponse }>()
    }
});

export const UserVerifyActions = createActionGroup({
    source: 'User-Verify',
    events: {
        'Verify': props<{ token: string }>(),
        'VerifyComplete': props<{ user: User }>(),
        'VerifyError': props<{ error: HttpErrorResponse }>()
    }
});

export const UserUpdateActions = createActionGroup({
    source: 'User-Update',
    events: {
        'UpdateUser': props<{ uri: URI, user: UserUpdate, profileImageData?: ImageUpdateData }>(),
        'UserBecomeProvider': props<{ uri: URI }>(),
        'ResetUpdate': props,
        'UpdateUserComplete': props<{ user: User }>(),
        'UpdateUserError': props<{ error: string }>()
    }
});

export const UserUrlActions = createActionGroup({
    source: 'User-Url',
    events: {
        'SetUrl': props<{ url: string }>(),
        'ResetUrl': props
    }
});