import { inject } from "@angular/core";
import {
  catchError,
  map,
  of,
  switchMap,
  tap,
  withLatestFrom,
} from "rxjs";
import { Actions, createEffect, ofType } from "@ngrx/effects";

import {
  ChangeOfferingFromEventsActions,
  OfferingCreateActions,
  OfferingDeleteActions,
  OfferingSelectActions,
  OfferingUpdateActions,
  OfferingsFetchActions,
  OfferingsLikeActions,
} from "./offerings.actions";
import { OfferingService } from "src/app/services/offeringService/offering.service";
import { ReviewService } from "src/app/services/reviewService/review.service";
import { Store } from "@ngrx/store";
import { RelationService } from "src/app/services/relationService/relation.service";
import { UserService } from "src/app/services/userService/user.service";
import { selectUser } from "../user/user.selector";

export const fetchOfferingsEffect = createEffect(
  (actions$ = inject(Actions), offeringService = inject(OfferingService)) => {
    return actions$.pipe(
      ofType(OfferingsFetchActions.fetchOfferings),
      switchMap(({ uri, filter }) => {
        return offeringService.getOfferings(filter, uri).pipe(
          map((offerings) =>
            OfferingsFetchActions.fetchOfferingsComplete({ offerings })
          ),
          catchError((error) =>
            of(OfferingsFetchActions.fetchOfferingsError({ error }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const fetchOfferingsOwnersEffect = createEffect(
  (actions$ = inject(Actions), offeringService = inject(OfferingService)) => {
    return actions$.pipe(
      ofType(OfferingsFetchActions.fetchOfferingsComplete),
      switchMap(({ offerings }) => {
        return offeringService.getOfferingsOwners(offerings.offerings).pipe(
          map((owners) =>
            OfferingsFetchActions.fetchOfferingsOwnersComplete({
              ownersByUri: owners,
            })
          ),
          catchError((error) =>
            of(OfferingsFetchActions.fetchOfferingsError({ error }))
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
      ofType(OfferingSelectActions.selectOffering),
      switchMap(({ offering }) => {
        return offeringService.getOfferingByURI(offering).pipe(
          map((offering) =>
            OfferingSelectActions.selectOfferingPartial({
              offering: { offering: offering },
            })
          ),
          catchError((error) => {
            return of(OfferingSelectActions.selectOfferingError({ error }));
          })
        );
      })
    );
  },
  { functional: true }
);

export const selectOfferingPartialEffect = createEffect(
  (
    actions$ = inject(Actions),
    offeringService = inject(OfferingService),
    store = inject(Store)
  ) => {
    return actions$.pipe(
      ofType(OfferingSelectActions.selectOfferingPartial),
      switchMap(({ offering }) => {
        store.dispatch(
          OfferingSelectActions.selectOfferingReviews({
            uri: offering.offering.reviews,
          })
        );
        return offeringService.getOfferingRelatedInfo(offering.offering).pipe(
          map((offering) =>
            OfferingSelectActions.selectOfferingComplete({ offering: offering })
          ),
          catchError((error) =>
            of(OfferingSelectActions.selectOfferingError({ error }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const selectOfferingReviewsEffect = createEffect(
  (actions$ = inject(Actions), reviewService = inject(ReviewService)) => {
    return actions$.pipe(
      ofType(OfferingSelectActions.selectOfferingReviews),
      switchMap(({ uri }) => {
        return reviewService.getReviews(uri).pipe(
          map((reviews) =>
            OfferingSelectActions.selectOfferingReviewsComplete({
              reviews: reviews,
            })
          ),
          catchError((error) =>
            of(OfferingSelectActions.selectOfferingError({ error }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const changeOfferingFromEventsEffect = createEffect(
  (actions$ = inject(Actions), relationService = inject(RelationService)) => {
    return actions$.pipe(
      ofType(ChangeOfferingFromEventsActions.changeOfferingFromEvents),
      switchMap(({ offering, eventsToAdd, eventsToRemove, provider }) => {
        return relationService
          .changeOfferingFromEvents(offering, eventsToAdd, eventsToRemove)
          .pipe(
            map((result) =>
              ChangeOfferingFromEventsActions.changeOfferingFromEventsComplete({
                relations: result,
                offering,
                provider,
                eventsToAdd,
                eventsToRemove,
              })
            ),
            catchError((error) =>
              of(
                ChangeOfferingFromEventsActions.changeOfferingFromEventsError({
                  error,
                })
              )
            )
          );
      })
    );
  },
  { functional: true }
);

export const fetchOfferingOwnerOccupiedDatesEffect = createEffect(
  (actions$ = inject(Actions), userService = inject(UserService)) => {
    return actions$.pipe(
      ofType(OfferingSelectActions.selectOwnerOccupiedDates),
      switchMap(({ user, filter }) => {
        return userService.getUserOccupiedDates(user, filter).pipe(
          map((dates) =>
            OfferingSelectActions.selectOwnerOccupiedDatesComplete({ dates })
          ),
          catchError((error) =>
            of(OfferingSelectActions.selectOfferingError({ error }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const fetchOfferingsLikesStartEffect = createEffect(
  (actions$ = inject(Actions), store = inject(Store)) => {
    return actions$.pipe(
      ofType(OfferingsFetchActions.fetchOfferingsComplete),
      withLatestFrom(store.select(selectUser)),
      switchMap(([{ offerings }, user]) => {
        if (user) {
          return of(
            OfferingsFetchActions.fetchOfferingsLikes({
              offerings: offerings.offerings,
              user,
            })
          );
        } else {
          return of(
            OfferingsFetchActions.fetchOfferingsLikesComplete({
              likes: new Map(),
            })
          );
        }
      })
    );
  },
  { functional: true }
);

export const fetchOfferingsLikesEffect = createEffect(
  (actions$ = inject(Actions), offeringService = inject(OfferingService)) => {
    return actions$.pipe(
      ofType(OfferingsFetchActions.fetchOfferingsLikes),
      switchMap(({ offerings, user }) => {
        return offeringService.getOfferingsLikes(offerings, user).pipe(
          map((likes) =>
            OfferingsFetchActions.fetchOfferingsLikesComplete({ likes })
          ),
          catchError((error) =>
            of(OfferingsFetchActions.fetchOfferingsError({ error }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const selectOfferingIsLikedEffect = createEffect(
  (
    actions$ = inject(Actions),
    offeringService = inject(OfferingService),
    store = inject(Store)
  ) => {
    return actions$.pipe(
      ofType(OfferingSelectActions.selectOfferingPartial),
      withLatestFrom(store.select(selectUser)),
      switchMap(([{ offering }, user]) => {
        if (user) {
          return offeringService
            .getOfferingLike(offering.offering.self, user)
            .pipe(
              map((offeringLike) =>
                OfferingSelectActions.selectOfferingIsLikedComplete({
                  isLiked: offeringLike.liked,
                })
              ),
              catchError((error) =>
                of(OfferingSelectActions.selectOfferingError({ error }))
              )
            );
        } else {
          return of(
            OfferingSelectActions.selectOfferingIsLikedComplete({
              isLiked: false,
            })
          );
        }
      })
    );
  },
  { functional: true }
);

export const likeOfferingEffect = createEffect(
  (actions$ = inject(Actions), offeringService = inject(OfferingService)) => {
    return actions$.pipe(
      ofType(OfferingsLikeActions.likeOffering),
      switchMap(({ offering, user }) => {
        return offeringService.likeOffering(offering, user).pipe(
          map(() =>
            OfferingsLikeActions.likeOfferingComplete({ offering, user })
          ),
          catchError((error) =>
            of(OfferingsLikeActions.likeOfferingError({ error }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const deleteLikeEffect = createEffect(
  (actions$ = inject(Actions), offeringService = inject(OfferingService)) => {
    return actions$.pipe(
      ofType(OfferingsLikeActions.deleteLike),
      switchMap(({ offering, user }) => {
        return offeringService.deleteLike(offering, user).pipe(
          map(() =>
            OfferingsLikeActions.deleteLikeComplete({ offering, user })
          ),
          catchError((error) =>
            of(OfferingsLikeActions.deleteLikeError({ error }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const updateOfferingEffect = createEffect(
  (actions$ = inject(Actions), offeringService = inject(OfferingService)) => {
    return actions$.pipe(
      ofType(OfferingUpdateActions.updateOffering),
      switchMap(({ uri, updatedOffering }) => {
        return offeringService.editOffering(uri, updatedOffering).pipe(
          map((offering) =>
            OfferingUpdateActions.updateOfferingComplete({ offering })
          ),
          catchError((error) =>
            of(OfferingUpdateActions.updateOfferingError({ error }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const deleteOfferingEffect = createEffect(
  (actions$ = inject(Actions), offeringService = inject(OfferingService)) => {
    return actions$.pipe(
      ofType(OfferingDeleteActions.deleteOffering),
      switchMap(({ offering }) => {
        return offeringService.deleteOffering(offering).pipe(
          map(() => OfferingDeleteActions.deleteOfferingComplete({ offering })),
          catchError((error) =>
            of(OfferingDeleteActions.deleteOfferingError({ error }))
          )
        );
      })
    );
  },
  { functional: true }
);

export const fetchOfferingRecommendationsEffect = createEffect(
  (actions$ = inject(Actions), offeringService = inject(OfferingService)) => {
    return actions$.pipe(
      ofType(OfferingSelectActions.selectOfferingPartial),
      switchMap(({ offering }) => {
        return offeringService
          .getOfferingRecommendations(offering.offering)
          .pipe(
            map((recommendations) =>
              OfferingSelectActions.selectOfferingRecommendationsComplete({
                recommendations,
              })
            ),
            catchError((error) =>
              of(OfferingSelectActions.selectOfferingError({ error }))
            )
          );
      })
    );
  },
  { functional: true }
);

export const createOfferingEffect = createEffect(
  (actions$ = inject(Actions), offeringService = inject(OfferingService)) => {
    return actions$.pipe(
      ofType(OfferingCreateActions.createOffering),
      switchMap(({ offering }) => {
        return offeringService.createOffering(offering).pipe(
          map((offering) =>
            OfferingCreateActions.createOfferingComplete({ offering })
          ),
          catchError((error) =>
            of(OfferingCreateActions.createOfferingError({ error }))
          )
        );
      })
    );
  },
  { functional: true }
);
