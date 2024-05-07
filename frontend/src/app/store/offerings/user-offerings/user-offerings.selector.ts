import { createSelector, createFeatureSelector } from '@ngrx/store';
import { ProviderOfferingsState } from './user-offerings.reducer';


export const selectUserOfferingsState = createFeatureSelector<ProviderOfferingsState>('userOfferings');

export const selectProviderCalendarData = createSelector(
    selectUserOfferingsState,
    (state: ProviderOfferingsState) => state.providerOfferingsRelations
);

export const selectProviderCalendarSelectedOffering = createSelector(
    selectUserOfferingsState,
    (state: ProviderOfferingsState) => state.providerOfferingsRelations?.selectedOffering
);

export const selectOfferingChatData = createSelector(
    selectUserOfferingsState,
    (state: ProviderOfferingsState) => state.selectedOfferingChats
);

export const selectOfferingChatEvents = createSelector(
    selectUserOfferingsState,
    (state: ProviderOfferingsState) => state.selectedOfferingChats?.events
);

export const selectOfferingChatOffering = createSelector(
    selectUserOfferingsState,
    (state: ProviderOfferingsState) => state.selectedOfferingChats?.offering
);

export const selectOfferingChatLastMessages = createSelector(
    selectUserOfferingsState,
    (state: ProviderOfferingsState) => state.selectedOfferingChats?.lastMessagesByRelation
);

export const selectUserOfferings = createSelector(
    selectUserOfferingsState,
    (state: ProviderOfferingsState) => state.offerings
);

export const selectUserOfferingsLoading = createSelector(
    selectUserOfferingsState,
    (state: ProviderOfferingsState) => state.isLoading
);

export const selectUserConversationsData = createSelector(
    selectUserOfferingsState,
    (state: ProviderOfferingsState) => state.offeringsConversationData
);