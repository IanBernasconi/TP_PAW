import { Offerings, ProviderChatData, ProviderCalendarData, OfferingsWithOwners } from "src/shared/models/offering.model";
import { Links } from "src/shared/models/pagination-utils.model";
import { createReducer, on } from '@ngrx/store';
import { ProviderCalendarSelectOffering, ProviderMarkConversationAsReadActions, ProviderLastMessageUpdateActions, ProviderOfferingConversationsFetchActions, ProviderRelationStatusUpdateActions, UserOfferingsFetchActions, UserRelationsFetchActions } from "./user-offerings.actions";
import { Relations } from "src/shared/models/relation.model";
import { URI } from "src/shared/types";

export interface ProviderOfferingsState {
    offerings: OfferingsWithOwners;
    offeringsConversationData?: Map<URI, Relations>;
    selectedOfferingChats?: ProviderChatData;
    providerOfferingsRelations?: ProviderCalendarData;
    isLoading: boolean;
    errorMessage: string;
    hasError: boolean;
}

export const initialOfferingsState: ProviderOfferingsState = {
    offerings: new OfferingsWithOwners([], new Links({})),
    isLoading: false,
    errorMessage: '',
    hasError: false
};

export const userOfferingsReducer = createReducer(
    initialOfferingsState,
    on(UserOfferingsFetchActions.fetchUserOfferings, (state) => ({ ...state, isLoading: true })),
    on(UserOfferingsFetchActions.fetchUserOfferingsComplete, (state, { offerings }) => ({
        ...state,
        offerings: offerings,
        isLoading: false
    })),
    on(UserOfferingsFetchActions.fetchUserOfferingsError, (state, { error }) => ({ ...state, errorMessage: error, hasError: true, isLoading: false })),
    on(UserOfferingsFetchActions.fetchUserOfferingsConversationDataComplete, (state, { relationsByOffering }) => ({
        ...state,
        offeringsConversationData: relationsByOffering
    })),

    on(UserRelationsFetchActions.fetchUserRelations, (state) => ({ ...state, isLoading: true })),
    on(UserRelationsFetchActions.fetchUserRelationsComplete, (state, { providerOfferingsRelations }) => ({
        ...state,
        providerOfferingsRelations: providerOfferingsRelations,
        isLoading: false
    })),
    on(UserRelationsFetchActions.fetchUserRelationsError, (state, { error }) => ({ ...state, errorMessage: error, hasError: true, isLoading: false })),


    on(ProviderCalendarSelectOffering.selectProviderCalendarOffering, (state, { uri }) => ({
        ...state,
        providerOfferingsRelations: state.providerOfferingsRelations ? {
            ...state.providerOfferingsRelations,
            selectedOffering: state.providerOfferingsRelations.selectedOffering?.self === uri ? state.providerOfferingsRelations.selectedOffering : undefined
        } : undefined,
        isLoading: true
    })),
    on(ProviderCalendarSelectOffering.selectProviderCalendarOfferingComplete, (state, { offering }) => ({
        ...state,
        providerOfferingsRelations: state.providerOfferingsRelations ? {
            ...state.providerOfferingsRelations,
            selectedOffering: offering
        } : undefined,
        isLoading: false
    })),
    on(ProviderCalendarSelectOffering.selectProviderCalendarOfferingError, (state, { error }) => ({ ...state, errorMessage: error, hasError: true, isLoading: false })),

    on(ProviderOfferingConversationsFetchActions.fetchOfferingProviderConversations, (state, { offering }) => ({
        ...state,
        selectedOfferingChats: state.selectedOfferingChats?.offering.self === offering.self ? state.selectedOfferingChats : undefined,
        isLoading: true
    })),
    on(ProviderOfferingConversationsFetchActions.fetchOfferingProviderConversationsComplete, (state, { chatData }) => ({
        ...state,
        selectedOfferingChats: chatData,
        isLoading: false
    })),
    on(ProviderOfferingConversationsFetchActions.fetchOfferingProviderConversationsError, (state, { error }) => ({ ...state, errorMessage: error, hasError: true, isLoading: false })),

    on(ProviderLastMessageUpdateActions.updateProviderLastMessage, (state, { message }) => ({
        ...state,
        selectedOfferingChats: state.selectedOfferingChats ? {
            ...state.selectedOfferingChats,
            lastMessagesByRelation: new Map([...state.selectedOfferingChats.lastMessagesByRelation, [message.relation, message]])
        } : undefined,
    })),


    on(ProviderMarkConversationAsReadActions.markConversationAsRead, (state, { relation }) => ({ ...state, isLoading: true })),
    on(ProviderMarkConversationAsReadActions.markConversationAsReadComplete, (state, { relation }) => ({
        ...state,
        isLoading: false,
        selectedOfferingChats: state.selectedOfferingChats ? {
            ...state.selectedOfferingChats,
            relations: state.selectedOfferingChats.relations.updateRelation(relation)
        } : undefined,
        offeringsConversationData: state.offeringsConversationData ?
            new Map(
                [...state.offeringsConversationData.entries()].map(([key, value]) => [
                    key, key === relation.offering ? value.updateRelation(relation) : value
                ])
            ) : undefined
    })),
    on(ProviderMarkConversationAsReadActions.markConversationAsReadError, (state, { error }) => ({ ...state, errorMessage: error, hasError: true, isLoading: false })),

    on(ProviderRelationStatusUpdateActions.acceptProviderRelation, (state, { uri }) => ({ ...state, isLoading: true })),
    on(ProviderRelationStatusUpdateActions.rejectProviderRelation, (state, { uri }) => ({ ...state, isLoading: true })),
    on(ProviderRelationStatusUpdateActions.providerRelationStatusUpdateComplete, (state, { relation }) => ({
        ...state,
        selectedOfferingChats: state.selectedOfferingChats ? {
            ...state.selectedOfferingChats,
            relations: state.selectedOfferingChats.relations.updateRelation(relation)
        } : undefined,
        offeringsConversationData: state.offeringsConversationData ?
            new Map(
                [...state.offeringsConversationData.entries()].map(([key, value]) => [
                    key, key === relation.offering ? value.updateRelation(relation) : value
                ])
            ) : undefined,
        isLoading: false
    })),
    on(ProviderRelationStatusUpdateActions.providerRelationStatusUpdateError, (state, { error }) => ({ ...state, errorMessage: error, hasError: true, isLoading: false })),

);