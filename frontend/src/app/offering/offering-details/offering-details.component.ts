import { Component } from "@angular/core";
import { Store } from "@ngrx/store";
import { ActivatedRoute, Router } from "@angular/router";
import { OfferingService } from "src/app/services/offeringService/offering.service";
import {
  DeleteOfferingDialogComponent,
  DeleteOfferingDialogData,
} from "../delete-offering-dialog/delete-offering-dialog.component";
import { MatDialog } from "@angular/material/dialog";
import {
  ToastMessages,
  ToastService,
} from "src/app/services/toastService/toast.service";
import { selectUser } from "src/app/store/user/user.selector";
import { Subject, filter, take } from "rxjs";
import {
  selectChangeOfferingFromEventsError,
  selectChangeOfferingFromEventsFlag,
  selectOfferingDeleteError,
  selectOfferingDeleteFlag,
  selectOfferingsError,
  selectOfferingsLoading,
  selectSelectedOffering,
} from "src/app/store/offerings/offerings.selector";
import {
  ChangeOfferingFromEventsActions,
  OfferingDeleteActions,
  OfferingSelectActions,
  OfferingsLikeActions,
} from "src/app/store/offerings/offerings.actions";
import {
  selectEvents,
  selectSelectedEvent,
} from "src/app/store/events/events.selector";
import {
  EventSelectActions,
  EventsFetchActions,
} from "src/app/store/events/events.actions";
import { Event, RangeFilter } from "src/shared/models/event.model";
import { URI } from "src/shared/types";
import { BaseComponent } from "src/app/utils/base-component.component";
import { User } from "src/shared/models/user.model";
import { Offering } from "src/shared/models/offering.model";
import { NavigationService } from "src/app/services/navigationService/navigation.service";

@Component({
  selector: "offering-details",
  templateUrl: "./offering-details.component.html",
  styleUrls: ["./offering-details.component.scss"],
})
export class OfferingDetailsComponent extends BaseComponent {
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dialog: MatDialog,
    toastService: ToastService,
    store: Store,
    private navigationService: NavigationService
  ) {
    super(store, toastService);
  }

  loggedUser$ = this.store.select(selectUser);
  destroy$ = new Subject<void>();

  userEvents$ = this.store.select(selectEvents);
  selectedEvent$ = this.store.select(selectSelectedEvent);
  selectedOffering$ = this.store.select(selectSelectedOffering);
  isLoading$ = this.store.select(selectOfferingsLoading);

  error$ = this.store.select(selectOfferingsError);

  ngOnInit() {
    // Subscribe to route parameter changes to get the ID
    this.route.paramMap.subscribe((params) => {
      const idParam = params.get("id");
      if (idParam) {
        const offeringId = parseInt(idParam, 10);
        this.store.dispatch(
          OfferingSelectActions.selectOffering({
            offering: OfferingService.getUriFromId(offeringId),
          })
        );
      }
    });
  }

  actionMessage = $localize`Go back`;

  openDeleteOfferingDialog() {
    this.selectedOffering$.pipe(take(1)).subscribe((offering) => {
      if (offering) {
        const dialogRef = this.dialog.open(DeleteOfferingDialogComponent, {
          data: new DeleteOfferingDialogData(offering.offering),
        });

        dialogRef.afterClosed().subscribe((result) => {
          if (result) {
            this.subscribeToSuccess(
              selectOfferingDeleteFlag,
              ToastMessages.offering.delete.success,
              OfferingDeleteActions.resetDeleteOfferingFlags(),
              () => this.router.navigate(["my-services"])
            );
            this.subscribeToError(
              selectOfferingDeleteError,
              ToastMessages.offering.delete.error,
              OfferingDeleteActions.resetDeleteOfferingFlags()
            );

            this.store.dispatch(
              OfferingDeleteActions.deleteOffering({
                offering: offering.offering,
              })
            );
          }
        });
      }
    });
  }

  editOffering() {
    this.selectedOffering$.pipe(take(1)).subscribe((offering) => {
      if (offering) {
        this.navigationService.navigateWithoutHistory([
          "my-services",
          offering.offering.id,
          "edit",
        ]);
      }
    });
  }

  goBack() {
    this.store.dispatch(OfferingSelectActions.clearSelectedOffering());
    this.navigationService.back("/" + this.route.snapshot.url.join("/"));
  }

  changeReviewPage(uri: URI) {
    this.store.dispatch(OfferingSelectActions.selectOfferingReviews({ uri }));
  }

  changeOfferingEvents(
    data: {
      offering: Offering;
      eventsToAdd: Event[];
      eventsToRemove: Event[];
      provider: User;
    },
    successAction?: () => void
  ) {
    this.subscribeToError(
      selectChangeOfferingFromEventsError,
      ToastMessages.offering.changeEvents.error,
      ChangeOfferingFromEventsActions.resetChangeOfferingFromEventsFlags()
    );
    this.subscribeToSuccess(
      selectChangeOfferingFromEventsFlag,
      ToastMessages.offering.changeEvents.success,
      ChangeOfferingFromEventsActions.resetChangeOfferingFromEventsFlags(),
      successAction
    );

    this.store.dispatch(
      ChangeOfferingFromEventsActions.changeOfferingFromEvents(data)
    );
  }

  addToSelectedEvent(data: {
    offering: Offering;
    eventToAdd: Event;
    provider: User;
  }) {
    this.changeOfferingEvents(
      {
        offering: data.offering,
        eventsToAdd: [data.eventToAdd],
        eventsToRemove: [],
        provider: data.provider,
      },
      () => this.router.navigate(["events", data.eventToAdd.id])
    );
  }

  removeFromSelectedEvent(data: {
    offering: Offering;
    eventToRemove: Event;
    provider: User;
  }) {
    this.changeOfferingEvents(
      {
        offering: data.offering,
        eventsToAdd: [],
        eventsToRemove: [data.eventToRemove],
        provider: data.provider,
      },
      () => this.router.navigate(["events", data.eventToRemove.id])
    );
  }

  fetchDates(rangeFilter: RangeFilter) {
    this.loggedUser$.pipe(take(1)).subscribe({
      next: (user) => {
        if (user) {
          this.store.dispatch(
            EventsFetchActions.fetchEvents({
              uri: user.createdEvents,
              filter: rangeFilter,
            })
          );
        }
      },
    });
    this.selectedOffering$.pipe(take(1)).subscribe((offering) => {
      if (offering && offering.owner) {
        this.store.dispatch(
          OfferingSelectActions.selectOwnerOccupiedDates({
            user: offering?.owner,
            filter: rangeFilter,
          })
        );
      }
    });
  }

  likeOffering(offering: Offering) {
    this.loggedUser$.pipe(take(1)).subscribe((user) => {
      if (user) {
        this.store.dispatch(
          OfferingsLikeActions.likeOffering({
            offering: offering.self,
            user: user,
          })
        );
      } else {
        this.router.navigate(["/login"]);
      }
    });
  }

  deleteLike(offering: Offering) {
    this.loggedUser$.pipe(take(1)).subscribe((user) => {
      if (user) {
        this.store.dispatch(
          OfferingsLikeActions.deleteLike({
            offering: offering.self,
            user: user,
          })
        );
      } else {
        this.router.navigate(["/login"]);
      }
    });
  }

  navigateToOffering(offering: Offering) {
    this.store.dispatch(
      OfferingSelectActions.selectOffering({ offering: offering.self })
    );
    this.router.navigate(["services", offering.id]);
  }

  navigateToEvent(event: Event) {
    this.router.navigate(["events", event.id]);
  }
}
