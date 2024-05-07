import { createReducer, on } from '@ngrx/store';

import { User } from "src/shared/models/user.model";
import { UserChangePasswordActions, UserFetchActions, UserLoginActions, UserLogoutActions, UserUpdateActions, UserUrlActions, UserVerifyActions } from './user.actions';
import { ActivatedRouteSnapshot } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

export interface UserState {
    user?: User;
    loggedIn: boolean;
    isLoading: boolean;
    errorMessage?: HttpErrorResponse;

    goingToUrl?: string;

    finishUpdate: boolean;
    updateError?: string;
}

export const initialState: UserState = {
    loggedIn: false,
    isLoading: false,
    finishUpdate: false,
};

export const userReducer = createReducer(
    initialState,
    on(UserLoginActions.login, (state, { username, password }) => ({ ...state, isLoading: true, errorMessage: undefined })),
    on(UserLoginActions.loginComplete, (state, { user }) => ({ ...state, user, loggedIn: true, isLoading: false })),
    on(UserLoginActions.loginError, (state, { error }) => ({ ...state, errorMessage: error, isLoading: false })),

    on(UserLogoutActions.logout, (state) => ({ ...state, isLoading: true, errorMessage: undefined })),
    on(UserLogoutActions.logoutComplete, (state) => ({ ...state, user: undefined, loggedIn: false, isLoading: false })),
    on(UserLogoutActions.logoutError, (state, { error }) => ({ ...state, errorMessage: error, isLoading: false })),

    on(UserFetchActions.fetchUser, (state) => ({ ...state, isLoading: true, errorMessage: undefined })),
    on(UserFetchActions.fetchUserComplete, (state, { user }) => ({ ...state, user, loggedIn: true, isLoading: false })),
    on(UserFetchActions.fetchUserError, (state, { error }) => ({ ...state, errorMessage: error, isLoading: false })),

    on(UserChangePasswordActions.changePassword, (state) => ({ ...state, isLoading: true, errorMessage: undefined, finishUpdate: false })),
    on(UserChangePasswordActions.changePasswordComplete, (state, { user }) => ({ ...state, user, isLoading: false, loggedIn: true, finishUpdate: true })),
    on(UserChangePasswordActions.changePasswordError, (state, { error }) => ({ ...state, isLoading: false, updateError: error.message })),

    on(UserVerifyActions.verify, (state) => ({ ...state, isLoading: true, errorMessage: undefined, finishUpdate: false })),
    on(UserVerifyActions.verifyComplete, (state, { user }) => ({ ...state, user, loggedIn: true, isLoading: false, finishUpdate: true })),
    on(UserVerifyActions.verifyError, (state, { error }) => ({ ...state, errorMessage: error, isLoading: false, updateError: error.message })),

    on(UserUpdateActions.updateUser, (state, { user, profileImageData }) => ({ ...state, isLoading: true })),
    on(UserUpdateActions.userBecomeProvider, (state) => ({ ...state, isLoading: true })),
    on(UserUpdateActions.resetUpdate, (state) => ({ ...state, finishUpdate: false, updateError: undefined })),
    on(UserUpdateActions.updateUserComplete, (state, { user }) => ({ ...state, user, finishUpdate: true, isLoading: false })),
    on(UserUpdateActions.updateUserError, (state, { error }) => ({ ...state, updateError: error, isLoading: false })),

    on(UserUrlActions.setUrl, (state, { url }) => ({ ...state, goingToUrl: url })),
    on(UserUrlActions.resetUrl, (state) => ({ ...state, goingToUrl: undefined })),
);
