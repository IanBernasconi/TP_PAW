import { createFeatureSelector, createSelector } from "@ngrx/store"
import * as fromEvents from "./events.reducer";


export const selectEventsState = createFeatureSelector<fromEvents.EventsState>('events');

export const selectEvents = createSelector(selectEventsState, fromEvents.selectAll);

export const selectEventsLinks = createSelector(selectEventsState, (state) => state.links);

export const selectEventsMap = createSelector(selectEventsState, fromEvents.selectEntities);

export const selectEventsLoading = createSelector(selectEventsState, (state) => state.isLoading);

export const selectEventsLoadingOfferings = createSelector(selectEventsState, (state) => state.isLoadingOfferings);

export const selectEventsLoadingRelated = createSelector(selectEventsState, (state) => state.isLoadingRelated);

export const selectEventsLoadingRelatedConversations = createSelector(selectEventsState, (state) => state.isLoadingRelatedConversations);

export const selectEventsErrorMessage = createSelector(selectEventsState, (state) => state.errorMessage);

export const selectSelectedEvent = createSelector(selectEventsState, (state) => state.selectedEvent);

export const selectFinishCreate = createSelector(selectEventsState, (state) => state.finishCreate);

export const selectFinishEdit = createSelector(selectEventsState, (state) => state.finishEdit);

export const selectFinishDelete = createSelector(selectEventsState, (state) => state.finishDelete);

export const selectFinishReviewOffering = createSelector(selectEventsState, (state) => state.finishReviewOffering);

export const selectFinishContactProvider = createSelector(selectEventsState, (state) => state.finsihContactProvider);

export const selectFinishRemoveOffering = createSelector(selectEventsState, (state) => state.finishRemoveOffering);

export const selectCreateErrorMessage = createSelector(selectEventsState, (state) => state.createErrorMessage);

export const selectEditErrorMessage = createSelector(selectEventsState, (state) => state.editErrorMessage);

export const selectDeleteErrorMessage = createSelector(selectEventsState, (state) => state.deleteErrorMessage);

export const selectReviewOfferingErrorMessage = createSelector(selectEventsState, (state) => state.reviewOfferingErrorMessage);

export const selectContactProviderErrorMessage = createSelector(selectEventsState, (state) => state.contactProviderErrorMessage);

export const selectRemoveOfferingErrorMessage = createSelector(selectEventsState, (state) => state.removeOfferingErrorMessage);



