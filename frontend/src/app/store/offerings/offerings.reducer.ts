import { Offering, OfferingRelated, Offerings, OfferingsWithOwners } from "src/shared/models/offering.model";
import { Links } from "src/shared/models/pagination-utils.model";
import { createReducer, on, select } from '@ngrx/store';
import { ChangeOfferingFromEventsActions, OfferingCreateActions, OfferingDeleteActions, OfferingSelectActions, OfferingUpdateActions, OfferingsFetchActions, OfferingsLikeActions } from "./offerings.actions";
import { URI } from "src/shared/types";
import { HttpErrorResponse } from "@angular/common/http";


export interface OfferingsState {
    offerings: OfferingsWithOwners;
    likesByOffering: Map<URI, boolean>;
    selectedOffering: OfferingRelated | null;
    isLoading: boolean;
    isLoadingLikes: boolean;
    isLoadingDates: boolean;
    error?: HttpErrorResponse;
    hasError: boolean;
    updateOfferingFlag: boolean;
    deleteOfferingFlag: boolean;
    changeOfferingFromEventsFlag: boolean;
    createOfferingFlag: boolean;
    changeOfferingFromEventsError?: string;
    updateOfferingError?: HttpErrorResponse;
    deleteOfferingError?: HttpErrorResponse;
    createOfferingError?: HttpErrorResponse;
}

export const initialOfferingsState: OfferingsState = {
    offerings: new OfferingsWithOwners([], new Links({})),
    likesByOffering: new Map(),
    selectedOffering: null,
    isLoading: false,
    isLoadingLikes: false,
    isLoadingDates: false,
    hasError: false,
    updateOfferingFlag: false,
    deleteOfferingFlag: false,
    changeOfferingFromEventsFlag: false,
    createOfferingFlag: false,
};

export const offeringsReducer = createReducer(
    initialOfferingsState,
    on(OfferingsFetchActions.fetchOfferings, (state, { uri, filter }) => ({ ...state, isLoading: true })),
    on(OfferingsFetchActions.fetchOfferingsComplete, (state, { offerings }) => ({ ...state, offerings: new OfferingsWithOwners(offerings.offerings, offerings.links), isLoading: false })),
    on(OfferingsFetchActions.fetchOfferingsOwnersComplete, (state, { ownersByUri }) => ({ ...state, offerings: state.offerings.setOwnersByUri(ownersByUri) })),
    on(OfferingsFetchActions.fetchOfferingsLikes, (state, { offerings, user }) => ({ ...state, isLoadingLikes: true })),
    on(OfferingsFetchActions.fetchOfferingsLikesComplete, (state, { likes }) => ({ ...state, likesByOffering: likes, isLoadingLikes: false })),
    on(OfferingsFetchActions.fetchOfferingsError, (state, { error }) => ({ ...state, error: error, hasError: true, isLoading: false })),

    on(OfferingsLikeActions.likeOffering, (state, { offering, user }) => ({ ...state })),
    on(OfferingsLikeActions.likeOfferingComplete, (state, { offering, user }) => ({
        ...state,
        likesByOffering: new Map(state.likesByOffering).set(offering, true),
        selectedOffering: state.selectedOffering ? {
            ...state.selectedOffering,
            offering: {
                ...state.selectedOffering.offering,
                likes: state.selectedOffering.offering.likes + 1
            },
            isLiked: true
        } : null,
        isLoadingLikes: false
    })),
    on(OfferingsLikeActions.likeOfferingError, (state, { error }) => ({ ...state, error: error, hasError: true, isLoadingLikes: false })),
    on(OfferingsLikeActions.deleteLike, (state, { offering, user }) => ({ ...state })),
    on(OfferingsLikeActions.deleteLikeComplete, (state, { offering, user }) => ({
        ...state,
        likesByOffering: new Map(state.likesByOffering).set(offering, false),
        selectedOffering: state.selectedOffering ? {
            ...state.selectedOffering,
            offering: {
                ...state.selectedOffering.offering,
                likes: state.selectedOffering.offering.likes - 1
            },
            isLiked: false
        } : null,
        isLoadingLikes: false
    })),
    on(OfferingsLikeActions.deleteLikeError, (state, { error }) => ({ ...state, error: error, hasError: true, isLoadingLikes: false })),

    on(OfferingSelectActions.selectOffering, (state, { offering }) => ({ ...state, selectedOffering: offering === state.selectedOffering?.offering.self ? state.selectedOffering : null, isLoading: true, error: undefined, hasError: false })),
    on(OfferingSelectActions.selectOfferingPartial, (state, { offering }) => ({ ...state, selectedOffering: offering, isLoading: true })),
    on(OfferingSelectActions.selectOfferingReviews, (state, { uri }) => ({ ...state, isLoading: true })),
    on(OfferingSelectActions.selectOfferingReviewsComplete, (state, { reviews }) => ({ ...state, selectedOffering: { ...state.selectedOffering!, reviews: reviews }, isLoading: false })),
    on(OfferingSelectActions.selectOfferingComplete, (state, { offering }) => ({ ...state, selectedOffering: { ...state.selectedOffering!, ...offering }, isLoading: false })),
    on(OfferingSelectActions.selectOfferingError, (state, { error }) => ({ ...state, error: error, hasError: true, isLoading: false })),
    on(OfferingSelectActions.clearSelectedOffering, (state) => ({ ...state, selectedOffering: null, isLoading: false })),
    on(OfferingSelectActions.selectOwnerOccupiedDates, (state, { user, filter }) => ({ ...state, isLoadingDates: true })),
    on(OfferingSelectActions.selectOwnerOccupiedDatesComplete, (state, { dates }) => ({ ...state, selectedOffering: { ...state.selectedOffering!, eventDates: dates }, isLoadingDates: false })),
    on(OfferingSelectActions.selectOfferingIsLikedComplete, (state, { isLiked }) => ({ ...state, selectedOffering: { ...state.selectedOffering!, isLiked: isLiked } })),
    on(OfferingSelectActions.selectOfferingRecommendationsComplete, (state, { recommendations }) => ({ ...state, selectedOffering: { ...state.selectedOffering!, recommendations: recommendations } })),

    on(OfferingUpdateActions.updateOffering, (state, { uri, updatedOffering }) => ({ ...state, isLoading: true })),
    on(OfferingUpdateActions.updateOfferingComplete, (state, { offering }) => ({
        ...state,
        selectedOffering: {
            ...state.selectedOffering!,
            offering: offering
        },
        isLoading: false,
        updateOfferingFlag: true
    })),
    on(OfferingUpdateActions.updateOfferingError, (state, { error }) => ({ ...state, error: error, hasError: true, isLoading: false, updateOfferingFlag: true, updateOfferingError: error })),
    on(OfferingUpdateActions.resetUpdateOfferingFlags, (state) => ({ ...state, updateOfferingFlag: false, updateOfferingError: undefined })),

    on(OfferingDeleteActions.deleteOffering, (state, { offering }) => ({ ...state, isLoading: true })),
    on(OfferingDeleteActions.deleteOfferingComplete, (state, { offering }) => ({
        ...state, isLoading: false,
        deleteOfferingFlag: true,
        offerings: new OfferingsWithOwners(state.offerings.offerings.filter(o => o.self !== offering.self), state.offerings.links, state.offerings.ownersByUri),
        selectedOffering: null,
    })),
    on(OfferingDeleteActions.deleteOfferingError, (state, { error }) => ({ ...state, error: error, hasError: true, isLoading: false, deleteOfferingFlag: true, deleteOfferingError: error })),
    on(OfferingDeleteActions.resetDeleteOfferingFlags, (state) => ({ ...state, deleteOfferingFlag: false, deleteOfferingError: undefined })),

    on(ChangeOfferingFromEventsActions.changeOfferingFromEvents, (state, { offering, eventsToAdd, eventsToRemove }) => ({ ...state, isLoading: true })),
    on(ChangeOfferingFromEventsActions.changeOfferingFromEventsComplete, (state, { relations, offering, provider }) => ({ ...state, isLoading: false, changeOfferingFromEventsFlag: true })),
    on(ChangeOfferingFromEventsActions.changeOfferingFromEventsError, (state, { error }) => ({ ...state, changeOfferingFromEventsError: error.error.message, changeOfferingFromEventsFlag: true, isLoading: false })),

    on(OfferingCreateActions.createOffering, (state, { offering }) => ({ ...state, isLoading: true })),
    on(OfferingCreateActions.createOfferingComplete, (state, { offering }) => ({
        ...state,
        isLoading: false,
        createOfferingFlag: true,
        offerings: new OfferingsWithOwners([...state.offerings.offerings, offering], state.offerings.links, state.offerings.ownersByUri),
    })),
    on(OfferingCreateActions.createOfferingError, (state, { error }) => ({ ...state, error: error, hasError: true, isLoading: false, createOfferingFlag: true, createOfferingError: error })),
    on(OfferingCreateActions.resetCreateOfferingFlags, (state) => ({ ...state, createOfferingFlag: false, createOfferingError: undefined })),

);
