import { createSelector, createFeatureSelector } from '@ngrx/store';
import { OfferingsState } from './offerings.reducer';

export const selectOfferingsState = createFeatureSelector<OfferingsState>('offerings');

export const selectOfferings = createSelector(
    selectOfferingsState,
    (state: OfferingsState) => state.offerings
);

export const selectOfferingsLoading = createSelector(
    selectOfferingsState,
    (state: OfferingsState) => state.isLoading
);

export const selectOfferingsError = createSelector(
    selectOfferingsState,
    (state: OfferingsState) => state.error
);

export const selectSelectedOffering = createSelector(
    selectOfferingsState,
    (state: OfferingsState) => state.selectedOffering
);

export const selectChangeOfferingFromEventsFlag = createSelector(
    selectOfferingsState,
    (state: OfferingsState) => state.changeOfferingFromEventsFlag
);

export const selectChangeOfferingFromEventsError = createSelector(
    selectOfferingsState,
    (state: OfferingsState) => state.changeOfferingFromEventsError
);

export const selectOfferingsLikes = createSelector(
    selectOfferingsState,
    (state: OfferingsState) => state.likesByOffering
);

export const selectOfferingsLikesLoading = createSelector(
    selectOfferingsState,
    (state: OfferingsState) => state.isLoadingLikes
);

export const selectOfferingUpdateFlag = createSelector(
    selectOfferingsState,
    (state: OfferingsState) => state.updateOfferingFlag
);

export const selectOfferingUpdateError = createSelector(
    selectOfferingsState,
    (state: OfferingsState) => state.updateOfferingError
);

export const selectOfferingDeleteFlag = createSelector(
    selectOfferingsState,
    (state: OfferingsState) => state.deleteOfferingFlag
);

export const selectOfferingDeleteError = createSelector(
    selectOfferingsState,
    (state: OfferingsState) => state.deleteOfferingError
);

export const selectOfferingCreateFlag = createSelector(
    selectOfferingsState,
    (state: OfferingsState) => state.createOfferingFlag
);

export const selectOfferingCreateError = createSelector(
    selectOfferingsState,
    (state: OfferingsState) => state.createOfferingError
);