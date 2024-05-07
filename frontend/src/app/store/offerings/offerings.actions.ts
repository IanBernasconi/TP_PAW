import { createActionGroup, props } from '@ngrx/store';
import { Offering, OfferingFilter, OfferingRelated, OfferingToCreate, Offerings } from 'src/shared/models/offering.model';
import { Reviews } from 'src/shared/models/review.model';
import { Event, RangeFilter } from 'src/shared/models/event.model';
import { URI } from 'src/shared/types';
import { User } from 'src/shared/models/user.model';
import { HttpErrorResponse } from '@angular/common/http';

export const OfferingsFetchActions = createActionGroup({
    source: 'Offerings-Fetch',
    events: {
        'FetchOfferings': props<{ uri?: string, filter?: OfferingFilter }>(),
        'FetchOfferingsComplete': props<{ offerings: Offerings }>(),
        'FetchOfferingsOwnersComplete': props<{ ownersByUri: Map<URI, User> }>(),
        'FetchOfferingsLikes': props<{ offerings: Offering[], user: User }>(),
        'FetchOfferingsLikesComplete': props<{ likes: Map<URI, boolean> }>(),
        'FetchOfferingsError': props<{ error: HttpErrorResponse }>()
    }
});

export const OfferingsLikeActions = createActionGroup({
    source: 'Offerings-Like',
    events: {
        'LikeOffering': props<{ offering: URI, user: User }>(),
        'LikeOfferingComplete': props<{ offering: URI, user: User }>(),
        'LikeOfferingError': props<{ error: HttpErrorResponse }>(),
        'DeleteLike': props<{ offering: URI, user: User }>(),
        'DeleteLikeComplete': props<{ offering: URI, user: User }>(),
        'DeleteLikeError': props<{ error: HttpErrorResponse }>()
    }
});

export const OfferingSelectActions = createActionGroup({
    source: 'Offering-Select',
    events: {
        'SelectOffering': props<{ offering: URI }>(),
        'SelectOfferingPartial': props<{ offering: OfferingRelated }>(),
        'SelectOfferingIsLikedComplete': props<{ isLiked: boolean }>(),
        'SelectOfferingRecommendationsComplete': props<{ recommendations: Offering[] }>(),
        'SelectOfferingReviews': props<{ uri: URI }>(),
        'SelectOfferingReviewsComplete': props<{ reviews: Reviews }>(),
        'SelectOfferingComplete': props<{ offering: OfferingRelated }>(),
        'SelectOfferingError': props<{ error: HttpErrorResponse }>(),
        'SelectOwnerOccupiedDates': props<{ user: User, filter: RangeFilter }>(),
        'SelectOwnerOccupiedDatesComplete': props<{ dates: Date[] }>(),
        'ClearSelectedOffering': props
    }
});

export const OfferingUpdateActions = createActionGroup({
    source: 'Offering-Update',
    events: {
        'UpdateOffering': props<{ uri: URI, updatedOffering: OfferingToCreate }>(),
        'UpdateOfferingComplete': props<{ offering: Offering }>(),
        'UpdateOfferingError': props<{ error: HttpErrorResponse }>(),
        'ResetUpdateOfferingFlags': props
    }
});

export const OfferingDeleteActions = createActionGroup({
    source: 'Offering-Delete',
    events: {
        'DeleteOffering': props<{ offering: Offering }>(),
        'DeleteOfferingComplete': props<{ offering: Offering }>(),
        'DeleteOfferingError': props<{ error: HttpErrorResponse }>(),
        'ResetDeleteOfferingFlags': props
    }
});

export const ChangeOfferingFromEventsActions = createActionGroup({
    source: 'Change-Offering-From-Events',
    events: {
        'ChangeOfferingFromEvents': props<{ offering: Offering, eventsToAdd: Event[], eventsToRemove: Event[], provider: User }>(),
        'ChangeOfferingFromEventsComplete': props<{ relations: Map<URI, boolean>, offering: Offering, provider: User, eventsToAdd: Event[], eventsToRemove: Event[] }>(),
        'ChangeOfferingFromEventsError': props<{ error: HttpErrorResponse }>(),
        'ResetChangeOfferingFromEventsFlags': props
    }
});

export const OfferingCreateActions = createActionGroup({
    source: 'Offering-Create',
    events: {
        'CreateOffering': props<{ offering: OfferingToCreate }>(),
        'CreateOfferingComplete': props<{ offering: Offering }>(),
        'CreateOfferingError': props<{ error: HttpErrorResponse }>(),
        'ResetCreateOfferingFlags': props
    }
});