import { createActionGroup, props } from '@ngrx/store';
import { Message } from 'src/shared/models/message.model';
import { Event, Events } from 'src/shared/models/event.model';
import { Offering, Offerings, ProviderChatData, ProviderCalendarData, OfferingsWithOwners } from 'src/shared/models/offering.model';
import { Relation, Relations, RelationsFilter } from 'src/shared/models/relation.model';
import { URI } from 'src/shared/types';
import { User } from 'src/shared/models/user.model';


export const UserOfferingsFetchActions = createActionGroup({
    source: 'User-Offerings-Fetch',
    events: {
        'FetchUserOfferings': props<{ uri: URI, user: User }>(),
        'FetchUserOfferingsComplete': props<{ offerings: OfferingsWithOwners }>(),
        'FetchUserOfferingsConversationDataComplete': props<{ relationsByOffering: Map<URI, Relations> }>(),
        'FetchUserOfferingsError': props<{ error: string }>()
    }
});

// For calendar
export const UserRelationsFetchActions = createActionGroup({
    source: 'User-Relations-Fetch',
    events: {
        'FetchUserRelations': props<{ uri: URI, filter?: RelationsFilter }>(),
        'FetchUserRelationsComplete': props<{ providerOfferingsRelations: ProviderCalendarData }>(),
        'FetchUserRelationsError': props<{ error: string }>()
    }
});

export const ProviderCalendarSelectOffering = createActionGroup({
    source: 'Provider-Calendar-Select-Offering',
    events: {
        'SelectProviderCalendarOffering': props<{ uri: URI }>(),
        'SelectProviderCalendarOfferingComplete': props<{ offering: Offering }>(),
        'SelectProviderCalendarOfferingError': props<{ error: string }>()
    }
});

// For chat
export const ProviderOfferingConversationsFetchActions = createActionGroup({
    source: 'Provider-Offering-Conversations-Fetch',
    events: {
        'FetchOfferingProviderConversations': props<{ offering: Offering, past?: boolean }>(),
        'FetchOfferingProviderConversationsComplete': props<{ chatData: ProviderChatData }>(),
        'FetchOfferingProviderConversationsError': props<{ error: string }>()
    }
});

export const ProviderLastMessageUpdateActions = createActionGroup({
    source: 'Provider-Last-Message-Update',
    events: {
        'UpdateProviderLastMessage': props<{ message: Message }>()
    }
});

export const ProviderMarkConversationAsReadActions = createActionGroup({
    source: 'Provider-Mark-Conversation-As-Read',
    events: {
        'MarkConversationAsRead': props<{ relation: Relation }>(),
        'MarkConversationAsReadComplete': props<{ relation: Relation }>(),
        'MarkConversationAsReadError': props<{ error: string }>()
    }
});

export const ProviderRelationStatusUpdateActions = createActionGroup({
    source: 'Provider-Relation-Status-Update',
    events: {
        'AcceptProviderRelation': props<{ uri: URI }>(),
        'RejectProviderRelation': props<{ uri: URI }>(),
        'ProviderRelationStatusUpdateComplete': props<{ relation: Relation }>(),
        'ProviderRelationStatusUpdateError': props<{ error: string }>(),
    }
});