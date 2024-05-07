import { createSelector, createFeatureSelector } from '@ngrx/store';
import { UserState } from './user.reducer';


export const selectUserState = createFeatureSelector<UserState>('user');

export const selectUserIsLoggedIn = createSelector(selectUserState, (state: UserState) => state.loggedIn);

export const selectUser = createSelector(selectUserState, (state: UserState) => state.user);


export const selectUserIsLoading = createSelector(selectUserState, (state: UserState) => state.isLoading);

export const selectUserErrorMessage = createSelector(selectUserState, (state: UserState) => state.errorMessage);

export const selectUserFinishUpdate = createSelector(selectUserState, (state: UserState) => state.finishUpdate);

export const selectUserUpdateError = createSelector(selectUserState, (state: UserState) => state.updateError);

export const selectUserGoingToUrl = createSelector(selectUserState, (state: UserState) => state.goingToUrl);