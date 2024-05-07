import { createEntityAdapter, EntityState, Update } from '@ngrx/entity';

import { Event, EventRelated, EventWithOffering } from 'src/shared/models/event.model';
import { EventSelectActions, EventsFetchActions, EventCreateActions, EventDeleteActions, EventEditActions, EventReviewOfferingActions, EventContactProviderActions, EventRemoveOfferingActions, EventLastMessageUpdateActions, EventMarkConversationAsReadActions, EventOfferingFetchActions, EventAddOfferingActions, EventsAddOfferingActions, EventsRemoveOfferingActions } from './events.actions';
import { createReducer, on } from '@ngrx/store';
import { Links } from 'src/shared/models/pagination-utils.model';
import { Offering } from 'src/shared/models/offering.model';
import { URI } from 'src/shared/types';
import { HttpErrorResponse } from '@angular/common/http';

export interface EventsState extends EntityState<EventWithOffering> {
    links: Links;
    selectedEvent: EventRelated | null;
    isLoading: boolean;
    isLoadingOfferings: boolean;
    isLoadingRelated: boolean;
    isLoadingRelatedConversations: boolean;
    errorMessage?: HttpErrorResponse;

    finishCreate: boolean;
    finishEdit: boolean;
    finishDelete: boolean;
    finishReviewOffering: boolean;
    finsihContactProvider: boolean;
    finishRemoveOffering: boolean;
    createErrorMessage?: HttpErrorResponse;
    editErrorMessage?: HttpErrorResponse;
    deleteErrorMessage?: HttpErrorResponse;
    reviewOfferingErrorMessage?: HttpErrorResponse;
    contactProviderErrorMessage?: HttpErrorResponse;
    removeOfferingErrorMessage?: HttpErrorResponse;
}

const eventsAdapter = createEntityAdapter<EventWithOffering>({
    selectId: (event) => event.self
});

const initialState: EventsState = eventsAdapter.getInitialState({
    links: new Links({}),
    selectedEvent: null,
    isLoading: false,
    isLoadingOfferings: false,
    isLoadingRelated: false,
    isLoadingRelatedConversations: false,
    finishCreate: false,
    finishEdit: false,
    finishDelete: false,
    finishReviewOffering: false,
    finsihContactProvider: false,
    finishRemoveOffering: false,
});

export const eventsReducer = createReducer(
    initialState,
    on(EventsFetchActions.fetchEvents, (state, { uri }) => ({ ...state, isLoading: true, errorMessage: undefined })),
    on(EventsFetchActions.fetchEventsComplete, (state, { events }) => ({ ...state, ...eventsAdapter.setAll(events.events, state), links: events.links, isLoading: false, })),
    on(EventsFetchActions.fetchEventsError, (state, { error }) => ({ ...state, errorMessage: error, isLoading: false })),

    on(EventOfferingFetchActions.fetchEventOfferings, (state, { event }) => ({ ...state, isLoadingOfferings: true, errorMessage: undefined })),
    on(EventOfferingFetchActions.fetchEventOfferingsComplete, (state, { event }) => ({ ...state, ...eventsAdapter.updateOne({ id: event.self, changes: event }, state), isLoadingOfferings: false, })),
    on(EventOfferingFetchActions.fetchEventOfferingsError, (state, { error }) => ({ ...state, errorMessage: error, isLoadingOfferings: false })),

    on(EventSelectActions.selectEvent, (state, { event }) => ({ ...state, isLoadingRelated: true, selectedEvent: event === state.selectedEvent?.event.self ? state.selectedEvent : null, errorMessage: undefined })),
    on(EventSelectActions.selectEventComplete, (state, { eventInfo }) => ({ ...state, selectedEvent: eventInfo, isLoadingRelated: false, })),
    on(EventSelectActions.fetchEventConversation, (state, { eventInfo }) => ({ ...state, isLoadingRelatedConversations: true })),
    on(EventSelectActions.fetchEventConversationComplete, (state, { lastMessagesByUri }) => ({ ...state, selectedEvent: state.selectedEvent ? state.selectedEvent.updateLastMessages(lastMessagesByUri) : null, isLoadingRelatedConversations: false })),
    on(EventSelectActions.selectEventError, (state, { error }) => ({ ...state, errorMessage: error, isLoading: false, isLoadingRelated: false, isLoadingRelatedConversations: false })),
    on(EventSelectActions.selectEventOfferingsRecommendationsComplete, (state, { recommendations }) => ({ ...state, selectedEvent: state.selectedEvent ? state.selectedEvent.setRecommendations(recommendations) : null })),
    on(EventSelectActions.resetSelectEvent, (state) => ({ ...state, selectedEvent: null })),

    on(EventLastMessageUpdateActions.updateEventLastMessage, (state, { message }) => ({
        ...state,
        selectedEvent: state.selectedEvent ? state.selectedEvent.updateLastMessage(message) : null,
    })),

    on(EventMarkConversationAsReadActions.markConversationAsRead, (state, { relation }) => ({ ...state, isLoading: true })),
    on(EventMarkConversationAsReadActions.markConversationAsReadComplete, (state, { relation }) => ({
        ...state,
        isLoading: false,
        selectedEvent: state.selectedEvent ? state.selectedEvent.markRelationAsRead(relation) : null,
    })),
    on(EventMarkConversationAsReadActions.markConversationAsReadError, (state, { error }) => ({ ...state, errorMessage: error, isLoading: false })),

    on(EventCreateActions.createEvent, (state, { event }) => ({ ...state, isLoading: true })),
    on(EventCreateActions.createEventComplete, (state, { event }) => ({ ...state, ...eventsAdapter.addOne(event, state), isLoading: false, finishCreate: true })),
    on(EventCreateActions.createEventError, (state, { error }) => ({ ...state, errorMessage: error, isLoading: false, createErrorMessage: error })),
    on(EventCreateActions.resetCreateEventFlags, (state) => ({ ...state, finishCreate: false, createErrorMessage: undefined })),

    on(EventEditActions.editEvent, (state, { event }) => ({ ...state, isLoading: true })),
    on(EventEditActions.editEventComplete, (state, { event }) => ({ ...state, ...eventsAdapter.updateOne({ id: event.self, changes: event }, state), isLoading: false, finishEdit: true, selectedEvent: state.selectedEvent ? state.selectedEvent.updatedEvent(event) : null })),
    on(EventEditActions.editEventError, (state, { error }) => ({ ...state, errorMessage: error, isLoading: false, editErrorMessage: error })),
    on(EventEditActions.resetEditEventFlags, (state) => ({ ...state, finishEdit: false, editErrorMessage: undefined })),

    on(EventDeleteActions.deleteEvent, (state, { event }) => ({ ...state, isLoading: true })),
    on(EventDeleteActions.deleteEventComplete, (state, { event }) => ({ ...state, ...eventsAdapter.removeOne(event.self, state), isLoading: false, selectedEvent: null, finishDelete: true })),
    on(EventDeleteActions.deleteEventError, (state, { error }) => ({ ...state, errorMessage: error, isLoading: false, deleteErrorMessage: error })),
    on(EventDeleteActions.resetDeleteEventFlags, (state) => ({ ...state, finishDelete: false, deleteErrorMessage: undefined })),

    on(EventReviewOfferingActions.reviewOffering, (state, { review }) => ({ ...state, isLoading: true })),
    on(EventReviewOfferingActions.reviewOfferingComplete, (state, { review }) => ({ ...state, finishReviewOffering: true, isLoading: false, selectedEvent: state.selectedEvent ? state.selectedEvent.addReview(review) : null })),
    on(EventReviewOfferingActions.reviewOfferingError, (state, { error }) => ({ ...state, errorMessage: error, isLoading: false, reviewOfferingErrorMessage: error })),
    on(EventReviewOfferingActions.resetReviewOfferingFlags, (state) => ({ ...state, finishReviewOffering: false, reviewOfferingErrorMessage: undefined })),

    on(EventContactProviderActions.contactProvider, (state, { uri }) => ({ ...state, isLoading: true })),
    on(EventContactProviderActions.contactProviderComplete, (state, { relation }) => ({ ...state, finsihContactProvider: true, isLoading: false, selectedEvent: state.selectedEvent ? state.selectedEvent.updateRelation(relation) : null })),
    on(EventContactProviderActions.contactProviderError, (state, { error }) => ({ ...state, errorMessage: error, isLoading: false, contactProviderErrorMessage: error })),
    on(EventContactProviderActions.resetContactProviderFlags, (state) => ({ ...state, finsihContactProvider: false, contactProviderErrorMessage: undefined })),

    on(EventRemoveOfferingActions.removeOffering, (state, { uri }) => ({ ...state, isLoading: true })),
    on(EventRemoveOfferingActions.removeOfferingComplete, (state, { uri }) => ({ ...state, isLoading: false, finishRemoveOffering: true, selectedEvent: state.selectedEvent ? state.selectedEvent.removeRelation(uri) : null })),
    on(EventRemoveOfferingActions.removeOfferingError, (state, { error }) => ({ ...state, errorMessage: error, isLoading: false, removeOfferingErrorMessage: error })),
    on(EventRemoveOfferingActions.resetRemoveOfferingFlags, (state) => ({ ...state })),

    on(EventAddOfferingActions.addOfferingComplete, (state, { relation, offering, provider }) => ({
        ...state,
        selectedEvent: state.selectedEvent ? state.selectedEvent.addRelation(relation, offering, provider) : null
    })),

    on(EventsAddOfferingActions.addOfferingComplete, (state, { events, offering, provider }) => {
        const updates = events.map((event) => {
            const previousEvent = state.entities[event];
            const offeringsByURI = new Map(previousEvent?.offeringsByURI);
            offeringsByURI.set(offering.self, offering);
            return {
                id: event,
                changes: {
                    ...previousEvent,
                    offeringsByURI: offeringsByURI
                }
            } as Update<EventWithOffering>;
        });
        return eventsAdapter.updateMany(updates, state);
    }),

    on(EventsRemoveOfferingActions.removeOfferingComplete, (state, { events, offering, provider }) => {
        const updates = events.map((event) => {
            const previousEvent = state.entities[event];
            const offeringsByURI: Map<URI, Offering> = new Map(previousEvent?.offeringsByURI);
            offeringsByURI.delete(offering.self);
            return {
                id: event,
                changes: {
                    ...previousEvent,
                    offeringsByURI
                }
            } as Update<EventWithOffering>;
        });
        return eventsAdapter.updateMany(updates, {
            ...state,
            selectedEvent: state.selectedEvent ? state.selectedEvent.removeRelation(offering.self).removeRecommendation(offering.self) : null
        });
    })


);


export const {
    selectAll,
    selectEntities,
    selectIds,
    selectTotal
} = eventsAdapter.getSelectors();