import { inject } from '@angular/core';
import { catchError, concatMap, forkJoin, map, merge, mergeMap, of, switchMap, tap, withLatestFrom } from 'rxjs';
import { Actions, createEffect, ofType } from '@ngrx/effects';


import { EventCreateActions, EventSelectActions, EventsFetchActions, EventDeleteActions, EventEditActions, EventReviewOfferingActions, EventContactProviderActions, EventRemoveOfferingActions, EventMarkConversationAsReadActions, EventOfferingFetchActions, EventAddOfferingActions, EventsRemoveOfferingActions, EventsAddOfferingActions } from './events.actions';
import { EventService } from 'src/app/services/eventService/event-service.service';
import { Links } from 'src/shared/models/pagination-utils.model';
import { EventRelated, EventWithOffering, Events, RelationInfo } from 'src/shared/models/event.model';
import { ReviewService } from 'src/app/services/reviewService/review.service';
import { RelationService } from 'src/app/services/relationService/relation.service';
import { OfferingService } from 'src/app/services/offeringService/offering.service';
import { UserService } from 'src/app/services/userService/user.service';
import { User } from 'src/shared/models/user.model';
import { URI } from 'src/shared/types';
import { MessageService } from 'src/app/services/messageService/message.service';
import { Store } from '@ngrx/store';
import { selectUser } from '../user/user.selector';
import { ChangeOfferingFromEventsActions } from '../offerings/offerings.actions';
import { HttpErrorResponse } from '@angular/common/http';


export const fetchEventsEffect = createEffect(
    (actions$ = inject(Actions), eventService = inject(EventService)) => {
        return actions$.pipe(
            ofType(EventsFetchActions.fetchEvents),
            switchMap(({ uri, filter }) => {
                return eventService.getEvents(uri, filter).pipe(
                    map(events => EventsFetchActions.fetchEventsComplete({ events })),
                    catchError(error => of(EventsFetchActions.fetchEventsError({ error })))
                );
            })
        );
    }, { functional: true }
);

export const fetchEventsOfferingsStartEffect = createEffect((
    actions$ = inject(Actions)
) => {
    return actions$.pipe(
        ofType(EventsFetchActions.fetchEventsComplete),
        mergeMap(({ events }) => {
            return events.events.map(event => EventOfferingFetchActions.fetchEventOfferings({ event }));
        })
    );
}, { functional: true }
);

export const fetchEventsOfferingsEffect = createEffect((
    actions$ = inject(Actions),
    offeringService = inject(OfferingService),
) => {
    return actions$.pipe(
        ofType(EventOfferingFetchActions.fetchEventOfferings),
        mergeMap(({ event }) => {
            return offeringService.getOfferingsByEvent(event).pipe(
                map(offerings => EventOfferingFetchActions.fetchEventOfferingsComplete({ event: new EventWithOffering(event, offerings.offerings) })),
                catchError(error => of(EventOfferingFetchActions.fetchEventOfferingsError({ error })))
            );
        })
    );
}, { functional: true }
);


export const selectEventEffect = createEffect((
    actions$ = inject(Actions),
    eventService = inject(EventService)
) => {
    return actions$.pipe(
        ofType(EventSelectActions.selectEvent),
        switchMap(({ event }) => {
            return eventService.getEventRelated(event).pipe(
                map(eventRelated => EventSelectActions.selectEventComplete({ eventInfo: eventRelated })),
                catchError(error => of(EventSelectActions.selectEventError({ error })))
            );
        })
    );
}, { functional: true }
);


export const fetchEventConversationsStartEffect = createEffect((
    actions$ = inject(Actions)
) => {
    return actions$.pipe(
        ofType(EventSelectActions.selectEventComplete),
        map(({ eventInfo }) => EventSelectActions.fetchEventConversation({ eventInfo }))
    );
}, { functional: true }
);


export const fetchEventConversationsEffect = createEffect((
    actions$ = inject(Actions),
    messageService = inject(MessageService)
) => {
    return actions$.pipe(
        ofType(EventSelectActions.fetchEventConversation),
        switchMap(({ eventInfo }) => {
            return messageService.getLastMessagesByRelations(eventInfo.relations.map(relationInfo => relationInfo.relation)).pipe(
                map((lastMessagesByUri) => EventSelectActions.fetchEventConversationComplete({ lastMessagesByUri })),
                catchError(error => of(EventSelectActions.selectEventError({ error })))
            );
        })
    );
}, { functional: true }
);


export const createEventEffect = createEffect((
    actions$ = inject(Actions),
    eventService = inject(EventService),
    store = inject(Store)
) => {
    return actions$.pipe(
        ofType(EventCreateActions.createEvent),
        switchMap(({ event, offeringToAdd }) => {
            return eventService.createEvent(event).pipe(
                map(event => {
                    if (offeringToAdd) {
                        store.dispatch(ChangeOfferingFromEventsActions.changeOfferingFromEvents({ offering: offeringToAdd.offering, eventsToAdd: [event], eventsToRemove: [], provider: offeringToAdd.provider }));
                    }
                    return EventCreateActions.createEventComplete({ event })
                }),
                catchError(error => of(EventCreateActions.createEventError({ error })))
            );
        })
    );
}, { functional: true }
);

export const deleteEventEffect = createEffect((
    actions$ = inject(Actions),
    eventService = inject(EventService)
) => {
    return actions$.pipe(
        ofType(EventDeleteActions.deleteEvent),
        switchMap(({ event }) => {
            return eventService.deleteEvent(event).pipe(
                map(() => EventDeleteActions.deleteEventComplete({ event })),
                catchError(error => of(EventDeleteActions.deleteEventError({ error })))
            );
        })
    );
}, { functional: true }
);

export const editEventEffect = createEffect((
    actions$ = inject(Actions),
    eventService = inject(EventService)
) => {
    return actions$.pipe(
        ofType(EventEditActions.editEvent),
        switchMap(({ uri, event }) => {
            return eventService.editEvent(uri, event).pipe(
                map(event => EventEditActions.editEventComplete({ event })),
                catchError(error => of(EventEditActions.editEventError({ error })))
            );
        })
    );
}, { functional: true }
);

export const reviewOfferingEffect = createEffect((
    actions$ = inject(Actions),
    reviewService = inject(ReviewService)
) => {
    return actions$.pipe(
        ofType(EventReviewOfferingActions.reviewOffering),
        switchMap(({ review }) => {
            return reviewService.createReview(review).pipe(
                map(review => EventReviewOfferingActions.reviewOfferingComplete({ review })),
                catchError(error => of(EventReviewOfferingActions.reviewOfferingError({ error })))
            );
        })
    );
}, { functional: true }
);

export const contactProviderEffect = createEffect((
    actions$ = inject(Actions),
    relationService = inject(RelationService)
) => {
    return actions$.pipe(
        ofType(EventContactProviderActions.contactProvider),
        switchMap(({ uri }) => {
            return relationService.contactProvider(uri).pipe(
                map(relation => EventContactProviderActions.contactProviderComplete({ relation })),
                catchError(error => of(EventContactProviderActions.contactProviderError({ error })))
            );
        })
    );
}, { functional: true }
);

export const removeOfferingEffect = createEffect((
    actions$ = inject(Actions),
    relationService = inject(RelationService)
) => {
    return actions$.pipe(
        ofType(EventRemoveOfferingActions.removeOffering),
        switchMap(({ uri }) => {
            return relationService.deleteRelation(uri).pipe(
                map(relation => EventRemoveOfferingActions.removeOfferingComplete({ uri })),
                catchError(error => of(EventRemoveOfferingActions.removeOfferingError({ error })))
            );
        })
    );
}, { functional: true }
);

export const markConversationAsReadEffect = createEffect((
    actions$ = inject(Actions),
    messageService = inject(MessageService),
    store = inject(Store)) => {
    return actions$.pipe(
        ofType(EventMarkConversationAsReadActions.markConversationAsRead),
        withLatestFrom(store.select(selectUser)),
        switchMap(([{ relation }, user]) => {
            if (user) {
                return messageService.markConversationAsRead(relation, user?.self).pipe(
                    map((conversation) => EventMarkConversationAsReadActions.markConversationAsReadComplete({ relation: conversation })),
                    catchError(error => of(EventMarkConversationAsReadActions.markConversationAsReadError({ error })))
                );
            } else {
                return of(EventMarkConversationAsReadActions.markConversationAsReadError({ error: new HttpErrorResponse({ error: 'User not logged in' }) }));
            }
        })
    );
}, { functional: true }
);

export const updateChangedOfferingsEffect = createEffect((
    actions$ = inject(Actions),
    relationService = inject(RelationService),
) => {
    return actions$.pipe(
        ofType(ChangeOfferingFromEventsActions.changeOfferingFromEventsComplete),
        mergeMap(({ relations, offering, provider, eventsToAdd, eventsToRemove }) => {
            const observables = [];

            for (let relation of relations.entries()) {
                if (relation[1] === false) {
                    observables.push(of(EventRemoveOfferingActions.removeOfferingComplete({ uri: relation[0] })));
                } else {
                    observables.push(
                        relationService.getRelation(relation[0]).pipe(
                            map((relation) => {
                                return EventAddOfferingActions.addOfferingComplete({ relation, offering, provider });
                            })
                        )
                    );
                }

                observables.push(of(EventsAddOfferingActions.addOfferingComplete({ events: eventsToAdd.map(event => event.self), offering, provider })));
                observables.push(of(EventsRemoveOfferingActions.removeOfferingComplete({ events: eventsToRemove.map(event => event.self), offering, provider })));
            }
            return merge(...observables);
        })
    );
}, { functional: true }
);

export const fetchEventOfferingsRecommendationsEffect = createEffect((
    actions$ = inject(Actions),
    offeringService = inject(OfferingService),
) => {
    return actions$.pipe(
        ofType(EventSelectActions.selectEventComplete),
        mergeMap(({ eventInfo }) => {
            return offeringService.getEventRecommendations(eventInfo.event).pipe(
                map((recommendations) => EventSelectActions.selectEventOfferingsRecommendationsComplete({ recommendations })),
                catchError(error => of(EventSelectActions.selectEventError({ error })))
            );
        })
    );
}, { functional: true }
);