import { inject } from "@angular/core";
import {
  catchError,
  map,
  of,
  switchMap,
  take,
  tap,
  withLatestFrom,
} from "rxjs";
import { Actions, createEffect, ofType } from "@ngrx/effects";

import { OfferingService } from "src/app/services/offeringService/offering.service";
import {
  ProviderCalendarSelectOffering,
  ProviderMarkConversationAsReadActions,
  ProviderOfferingConversationsFetchActions,
  ProviderRelationStatusUpdateActions,
  UserOfferingsFetchActions,
  UserRelationsFetchActions,
} from "./user-offerings.actions";
import { RelationService } from "src/app/services/relationService/relation.service";
import { MessageService } from "src/app/services/messageService/message.service";
import { Store } from "@ngrx/store";
import { selectUser } from "../../user/user.selector";
import { UserService } from "src/app/services/userService/user.service";
import { EventService } from "src/app/services/eventService/event-service.service";
import { OfferingStatusKeys } from "src/shared/models/offering-status.model";
import { OfferingsWithOwners } from "src/shared/models/offering.model";

export const fetchOfferingsEffect = createEffect(
  (actions$ = inject(Actions), offeringService = inject(OfferingService)) => {
    return actions$.pipe(
      ofType(UserOfferingsFetchActions.fetchUserOfferings),
      switchMap(({ uri, user }) => {
        return offeringService.getOfferings(undefined, uri).pipe(
          map((offerings) =>
            UserOfferingsFetchActions.fetchUserOfferingsComplete({
              offerings: new OfferingsWithOwners(
                offerings.offerings,
                offerings.links,
                new Map([[user.self, user]])
              ),
            })
          ),
          catchError((error) =>
            of(UserOfferingsFetchActions.fetchUserOfferingsError({ error }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const fetchOfferingsConversationDataEffect = createEffect(
  (
    actions$ = inject(Actions),
    relationService = inject(RelationService),
    store = inject(Store)
  ) => {
    return actions$.pipe(
      ofType(UserOfferingsFetchActions.fetchUserOfferingsComplete),
      withLatestFrom(store.select(selectUser)),
      switchMap(([{ offerings }, user]) => {
        if (!user) {
          return of(
            UserOfferingsFetchActions.fetchUserOfferingsError({
              error: "User not found",
            })
          );
        }
        return relationService
          .getOfferingsConversationData(offerings.offerings, user)
          .pipe(
            map((relationsByOffering) =>
              UserOfferingsFetchActions.fetchUserOfferingsConversationDataComplete(
                { relationsByOffering }
              )
            ),
            catchError((error) =>
              of(UserOfferingsFetchActions.fetchUserOfferingsError({ error }))
            )
          );
      })
    );
  },
  { functional: true }
);

export const fetchRelationsEffect = createEffect(
  (actions$ = inject(Actions), eventService = inject(EventService)) => {
    return actions$.pipe(
      ofType(UserRelationsFetchActions.fetchUserRelations),
      switchMap(({ uri, filter }) => {
        return eventService.getProviderCalendarData(uri, filter).pipe(
          map((offeringData) =>
            UserRelationsFetchActions.fetchUserRelationsComplete({
              providerOfferingsRelations: offeringData,
            })
          ),
          catchError((error) =>
            of(UserRelationsFetchActions.fetchUserRelationsError({ error }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const selectOfferingEffect = createEffect(
  (actions$ = inject(Actions), offeringService = inject(OfferingService)) => {
    return actions$.pipe(
      ofType(ProviderCalendarSelectOffering.selectProviderCalendarOffering),
      switchMap(({ uri }) => {
        return offeringService.getOfferingByURI(uri).pipe(
          map((offering) =>
            ProviderCalendarSelectOffering.selectProviderCalendarOfferingComplete(
              { offering }
            )
          ),
          catchError((error) =>
            of(
              ProviderCalendarSelectOffering.selectProviderCalendarOfferingError(
                { error }
              )
            )
          )
        );
      })
    );
  },
  { functional: true }
);

export const fetchProviderConversationsEffect = createEffect(
  (
    actions$ = inject(Actions),
    messageService = inject(MessageService),
    store = inject(Store)
  ) => {
    return actions$.pipe(
      ofType(ProviderOfferingConversationsFetchActions.fetchOfferingProviderConversations),
      switchMap(({ offering, past }) =>
        store.select(selectUser).pipe(
          take(1),
          switchMap((user) => {
            if (!user) {
              return of(
                ProviderOfferingConversationsFetchActions.fetchOfferingProviderConversationsError(
                  { error: "User not found" }
                )
              );
            }
            return messageService
              .getProviderChatData(
                user!.providerRelations,
                offering,
                past ? {
                  to: RelationService.getStartOfToday().toISOString(), status: [OfferingStatusKeys.DONE],
                } : {
                  from: RelationService.getStartOfToday().toISOString(),
                  status: [OfferingStatusKeys.ACCEPTED, OfferingStatusKeys.PENDING,]
                }
              )
              .pipe(
                map((chatData) =>
                  ProviderOfferingConversationsFetchActions.fetchOfferingProviderConversationsComplete(
                    { chatData }
                  )
                ),
                catchError((error) =>
                  of(
                    ProviderOfferingConversationsFetchActions.fetchOfferingProviderConversationsError(
                      { error }
                    )
                  )
                )
              );
          })
        )
      )
    );
  }, { functional: true }
);

export const acceptProviderRelationEffect = createEffect(
  (actions$ = inject(Actions), relationService = inject(RelationService)) => {
    return actions$.pipe(
      ofType(ProviderRelationStatusUpdateActions.acceptProviderRelation),
      switchMap(({ uri }) => {
        return relationService.acceptRelation(uri).pipe(
          map((relation) =>
            ProviderRelationStatusUpdateActions.providerRelationStatusUpdateComplete(
              { relation }
            )
          ),
          catchError((error) =>
            of(
              ProviderRelationStatusUpdateActions.providerRelationStatusUpdateError(
                { error }
              )
            )
          )
        );
      })
    );
  },
  { functional: true }
);

export const rejectProviderRelationEffect = createEffect(
  (actions$ = inject(Actions), relationService = inject(RelationService)) => {
    return actions$.pipe(
      ofType(ProviderRelationStatusUpdateActions.rejectProviderRelation),
      switchMap(({ uri }) => {
        return relationService.rejectRelation(uri).pipe(
          map((relation) =>
            ProviderRelationStatusUpdateActions.providerRelationStatusUpdateComplete(
              { relation }
            )
          ),
          catchError((error) =>
            of(
              ProviderRelationStatusUpdateActions.providerRelationStatusUpdateError(
                { error }
              )
            )
          )
        );
      })
    );
  },
  { functional: true }
);

export const markConversationAsReadEffect = createEffect(
  (
    actions$ = inject(Actions),
    messageService = inject(MessageService),
    store = inject(Store)
  ) => {
    return actions$.pipe(
      ofType(ProviderMarkConversationAsReadActions.markConversationAsRead),
      withLatestFrom(store.select(selectUser)),
      switchMap(([{ relation }, user]) => {
        if (user) {
          return messageService
            .markConversationAsRead(relation, user?.self)
            .pipe(
              map((relation) =>
                ProviderMarkConversationAsReadActions.markConversationAsReadComplete(
                  { relation }
                )
              ),
              catchError((error) =>
                of(
                  ProviderMarkConversationAsReadActions.markConversationAsReadError(
                    { error }
                  )
                )
              )
            );
        } else {
          return of(
            ProviderMarkConversationAsReadActions.markConversationAsReadError({
              error: "User not found",
            })
          );
        }
      })
    );
  },
  { functional: true }
);
