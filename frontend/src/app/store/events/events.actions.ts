import { createAction, createActionGroup, props } from '@ngrx/store';
import { Message } from 'src/shared/models/message.model';
import { Event, RangeFilter, EventRelated, EventWithOffering, Events, EventToCreate } from 'src/shared/models/event.model';
import { Relation, Relations } from 'src/shared/models/relation.model';
import { Review } from 'src/shared/models/review.model';
import { URI } from 'src/shared/types';
import { Offering } from 'src/shared/models/offering.model';
import { User } from 'src/shared/models/user.model';
import { HttpErrorResponse } from '@angular/common/http';

export const EventsFetchActions = createActionGroup({
    source: 'Events-Fetch',
    events: {
        'FetchEvents': props<{ uri: string, filter?: RangeFilter }>(),
        'FetchEventsComplete': props<{ events: Events }>(),
        'FetchEventsError': props<{ error: HttpErrorResponse }>()
    }
});

export const EventOfferingFetchActions = createActionGroup({
    source: 'Event-Offerings-Fetch',
    events: {
        'FetchEventOfferings': props<{ event: EventWithOffering }>(),
        'FetchEventOfferingsComplete': props<{ event: EventWithOffering }>(),
        'FetchEventOfferingsError': props<{ error: HttpErrorResponse }>()
    }
});

export const EventSelectActions = createActionGroup({
    source: 'Event-Select',
    events: {
        'SelectEvent': props<{ event: URI }>(),
        'SelectEventComplete': props<{ eventInfo: EventRelated }>(),
        'SelectEventOfferingsRecommendationsComplete': props<{ recommendations: Offering[] }>(),
        'FetchEventConversation': props<{ eventInfo: EventRelated }>(),
        'FetchEventConversationComplete': props<{ lastMessagesByUri: Map<URI, Message> }>(),
        'SelectEventError': props<{ error: HttpErrorResponse }>(),
        'ResetSelectEvent': props
    }
});

export const EventLastMessageUpdateActions = createActionGroup({
    source: 'Event-Last-Message-Update',
    events: {
        'UpdateEventLastMessage': props<{ message: Message }>()
    }
});

export const EventMarkConversationAsReadActions = createActionGroup({
    source: 'Event-Mark-Conversation-As-Read',
    events: {
        'MarkConversationAsRead': props<{ relation: Relation }>(),
        'MarkConversationAsReadComplete': props<{ relation: Relation }>(),
        'MarkConversationAsReadError': props<{ error: HttpErrorResponse }>()
    }
});

export const EventDeleteActions = createActionGroup({
    source: 'Event-Delete',
    events: {
        'DeleteEvent': props<{ event: Event }>(),
        'DeleteEventComplete': props<{ event: Event }>(),
        'DeleteEventError': props<{ error: HttpErrorResponse }>(),
        'ResetDeleteEventFlags': props
    }
});

export const EventCreateActions = createActionGroup({
    source: 'Event-Create',
    events: {
        'CreateEvent': props<{ event: EventToCreate, offeringToAdd?: { offering: Offering, provider: User } }>(),
        'CreateEventComplete': props<{ event: Event }>(),
        'CreateEventError': props<{ error: HttpErrorResponse }>(),
        'ResetCreateEventFlags': props
    }
});

export const EventEditActions = createActionGroup({
    source: 'Event-Edit',
    events: {
        'EditEvent': props<{ uri: URI, event: EventToCreate }>(),
        'EditEventComplete': props<{ event: Event }>(),
        'EditEventError': props<{ error: HttpErrorResponse }>(),
        'ResetEditEventFlags': props
    }
});

export const EventReviewOfferingActions = createActionGroup({
    source: 'Event-Review-Offering',
    events: {
        'ReviewOffering': props<{ review: Review }>(),
        'ReviewOfferingComplete': props<{ review: Review }>(),
        'ReviewOfferingError': props<{ error: HttpErrorResponse }>(),
        'ResetReviewOfferingFlags': props
    }
});


export const EventContactProviderActions = createActionGroup({
    source: 'Event-Contact-Provider',
    events: {
        'ContactProvider': props<{ uri: URI }>(),
        'ContactProviderComplete': props<{ relation: Relation }>(),
        'ContactProviderError': props<{ error: HttpErrorResponse }>(),
        'ResetContactProviderFlags': props
    }
});

export const EventRemoveOfferingActions = createActionGroup({
    source: 'Event-Remove-Offering',
    events: {
        'RemoveOffering': props<{ uri: URI }>(),
        'RemoveOfferingComplete': props<{ uri: URI }>(),
        'RemoveOfferingError': props<{ error: HttpErrorResponse }>(),
        'ResetRemoveOfferingFlags': props
    }
});

export const EventAddOfferingActions = createActionGroup({
    source: 'Event-Add-Offering',
    events: {
        'AddOfferingComplete': props<{ relation: Relation, offering: Offering, provider: User }>(),
    }
});

export const EventsAddOfferingActions = createActionGroup({
    source: 'Events-Add-Offering',
    events: {
        'AddOfferingComplete': props<{ events: URI[], offering: Offering, provider: User }>(),
    }
});

export const EventsRemoveOfferingActions = createActionGroup({
    source: 'Events-Remove-Offering',
    events: {
        'RemoveOfferingComplete': props<{ events: URI[], offering: Offering, provider: User }>(),
    }
});