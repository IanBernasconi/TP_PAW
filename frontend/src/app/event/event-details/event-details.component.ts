import { Component, ViewChild } from "@angular/core";
import { Store } from "@ngrx/store";
import { ActivatedRoute, Router } from "@angular/router";
import { EventService } from "src/app/services/eventService/event-service.service";
import { Event, EventToCreate } from "src/shared/models/event.model";
import { Relation, Relations } from "src/shared/models/relation.model";
import { Subject, filter, map, take, takeUntil, tap } from "rxjs";
import { MatDialog } from "@angular/material/dialog";
import {
  DeleteEventDialogComponent,
  DeleteEventDialogData,
} from "./delete-event-dialog/delete-event-dialog.component";
import {
  EditEventDialogComponent,
  EditEventDialogData,
} from "./edit-event-dialog/edit-event-dialog.component";
import { Review } from "src/shared/models/review.model";
import { ReviewDialogData } from "./event-table/review-dialog/review-dialog.component";
import { ContactProviderDialogData } from "./event-table/contact-provider-dialog/contact-provider-dialog.component";
import {
  ToastMessages,
  ToastService,
} from "src/app/services/toastService/toast.service";
import { selectUser } from "src/app/store/user/user.selector";
import {
  selectContactProviderErrorMessage,
  selectDeleteErrorMessage,
  selectEditErrorMessage,
  selectEventsErrorMessage,
  selectEventsLoadingRelated,
  selectFinishContactProvider,
  selectFinishDelete,
  selectFinishEdit,
  selectFinishRemoveOffering,
  selectFinishReviewOffering,
  selectRemoveOfferingErrorMessage,
  selectReviewOfferingErrorMessage,
  selectSelectedEvent,
} from "src/app/store/events/events.selector";
import {
  EventContactProviderActions,
  EventDeleteActions,
  EventEditActions,
  EventLastMessageUpdateActions,
  EventMarkConversationAsReadActions,
  EventRemoveOfferingActions,
  EventReviewOfferingActions,
  EventSelectActions,
} from "src/app/store/events/events.actions";
import { Links } from "src/shared/models/pagination-utils.model";
import { BaseComponent } from "src/app/utils/base-component.component";
import { Message } from "src/shared/models/message.model";
import { MatTabChangeEvent, MatTabGroup } from "@angular/material/tabs";
import { is, th } from "date-fns/locale";
import { OfferingSelectActions } from "src/app/store/offerings/offerings.actions";
import { Offering } from "src/shared/models/offering.model";
import { RelationService } from "src/app/services/relationService/relation.service";
import { OfferingCategory } from "src/shared/models/utils.model";
import { NavigationService } from "src/app/services/navigationService/navigation.service";

@Component({
  selector: "event-details",
  templateUrl: "./event-details.component.html",
  styleUrls: ["./event-details.component.scss"],
})
export class EventDetailsComponent extends BaseComponent {
  @ViewChild("tabGroup") tabGroup?: MatTabGroup;

  currentDate: string = new Date().toISOString().split("T")[0];

  constructor(
    toastService: ToastService,
    store: Store,
    private route: ActivatedRoute,
    private eventService: EventService,
    private router: Router,
    public dialog: MatDialog,
  ) {
    super(store, toastService);
  }

  isLoading$ = this.store.select(selectEventsLoadingRelated);
  loggedUser$ = this.store.select(selectUser);
  selectedEvent$ = this.store.select(selectSelectedEvent);
  error$ = this.store.select(selectEventsErrorMessage);
  actionMessage = $localize`Go back`;

  tabIndex: { [key: string]: number } = {
    details: 0,
    guests: 1,
    chat: 2,
  };

  tabUrlNames = Object.keys(this.tabIndex);

  selectedChatTab: Subject<boolean> = new Subject<boolean>();

  destroyed$ = new Subject<void>();

  isGoingToExplore: boolean = false;

  ngOnInit(): void {
    this.selectedEvent$
      .pipe(
        take(1),
        tap((event) => {
          if (!event) {
            // No event selected, so we need to get the event from the route
            const idParam = this.route.snapshot.paramMap.get("id");
            if (idParam) {
              let eventId = parseInt(idParam, 10);
              this.store.dispatch(
                EventSelectActions.selectEvent({
                  event: this.eventService.getEventURI(eventId),
                })
              );
            }
          }
        })
      )
      .subscribe();

    this.route.queryParams
      .pipe(
        takeUntil(this.destroyed$),
        filter(
          (params) =>
            !!params["tab"] && this.tabIndex[params["tab"]] !== this.lastTab
        ),
        map((params) => params["tab"]),
        tap((tab) => {
          if (this.tabGroup) {
            this.lastTab = this.tabGroup.selectedIndex ?? 0;
            this.tabGroup.selectedIndex = this.tabIndex[tab ?? "details"];
          }

          this.activeTab = this.tabIndex[tab ?? "details"];
        })
      )
      .subscribe();
  }

  activeTab: number = 0;

  lastTab: number = -1;

  goBack() {
    this.selectedEvent$.pipe(take(1)).subscribe((event) => {
      if (
        event &&
        new Date(event.event.date) < RelationService.getStartOfToday()
      ) {
        this.router.navigate(["/events"], { queryParams: { tab: "finished" } });
      } else {
        this.router.navigate(["/events"]);
      }
    });
  }

  onTabChanged(event: MatTabChangeEvent) {
    this.router.navigate([], {
      queryParams: { tab: this.tabUrlNames[event.index] },
      replaceUrl: true,
    });
    if (event.index === 2) {
      this.selectedChatTab.next(true);
    } else {
      this.selectedChatTab.next(false);
    }
  }

  openDeleteEventDialog() {
    this.selectedEvent$.pipe(take(1)).subscribe((event) => {
      if (event) {
        const dialogRef = this.dialog.open(DeleteEventDialogComponent, {
          data: new DeleteEventDialogData(event.event),
        });

        dialogRef.afterClosed().subscribe((result) => {
          if (result) {
            this.subscribeToSuccess(
              selectFinishDelete,
              ToastMessages.event.delete.success,
              EventDeleteActions.resetDeleteEventFlags(),
              () => this.goBack()
            );
            this.subscribeToError(
              selectDeleteErrorMessage,
              ToastMessages.event.delete.error,
              EventDeleteActions.resetDeleteEventFlags()
            );

            this.store.dispatch(
              EventDeleteActions.deleteEvent({ event: event.event })
            );
          }
        });
      }
    });
  }

  openEditEventDialog() {
    this.loggedUser$.pipe(take(1)).subscribe((loggedUser) => {
      if (loggedUser) {
        this.selectedEvent$.pipe(take(1)).subscribe((event) => {
          if (event) {
            let currentEvent = event.event;
            const dialogRef = this.dialog.open(EditEventDialogComponent, {
              data: new EditEventDialogData(currentEvent, loggedUser),
              width: "500px",
              maxHeight: "70vh",
            });

            dialogRef.afterClosed().subscribe((result) => {
              if (result) {
                // if there where changes, call editEvent
                if (
                  result.event.name != currentEvent.name ||
                  result.event.description != currentEvent.description ||
                  result.event.date != currentEvent.date ||
                  result.event.guests != currentEvent.numberOfGuests ||
                  result.event.district != currentEvent.district
                ) {
                  let modifiedEvent: EventToCreate = {
                    name: result.event.name,
                    description: result.event.description,
                    date: result.event.date,
                    numberOfGuests: result.event.numberOfGuests,
                    district: result.event.district,
                    owner: currentEvent.owner,
                  };

                  this.subscribeToSuccess(
                    selectFinishEdit,
                    ToastMessages.event.update.success,
                    EventEditActions.resetEditEventFlags()
                  );
                  this.subscribeToError(
                    selectEditErrorMessage,
                    ToastMessages.event.update.error,
                    EventEditActions.resetEditEventFlags()
                  );

                  this.store.dispatch(
                    EventEditActions.editEvent({
                      uri: currentEvent.self,
                      event: modifiedEvent,
                    })
                  );
                }
              }
            });
          }
        });
      }
    });
  }

  reviewOffering(result: ReviewDialogData) {
    let review: Review = new Review(
      result.content,
      result.rating,
      "",
      "",
      result.relationInfo.relation.self
    );

    this.subscribeToSuccess(
      selectFinishReviewOffering,
      ToastMessages.review.create.success,
      EventReviewOfferingActions.resetReviewOfferingFlags()
    );
    this.subscribeToError(
      selectReviewOfferingErrorMessage,
      ToastMessages.review.create.error,
      EventReviewOfferingActions.resetReviewOfferingFlags()
    );

    this.store.dispatch(
      EventReviewOfferingActions.reviewOffering({ review: review })
    );
  }

  contactProvider(result: ContactProviderDialogData) {
    this.subscribeToSuccess(
      selectFinishContactProvider,
      ToastMessages.relation.create.success,
      EventContactProviderActions.resetContactProviderFlags()
    );
    this.subscribeToError(
      selectContactProviderErrorMessage,
      ToastMessages.relation.create.error,
      EventContactProviderActions.resetContactProviderFlags()
    );

    this.store.dispatch(
      EventContactProviderActions.contactProvider({
        uri: result.relationInfo.relation.self,
      })
    );
  }

  removeRelation(relation: Relation) {
    this.subscribeToSuccess(
      selectFinishRemoveOffering,
      ToastMessages.relation.delete.success,
      EventDeleteActions.resetDeleteEventFlags()
    );
    this.subscribeToError(
      selectRemoveOfferingErrorMessage,
      ToastMessages.relation.delete.error,
      EventDeleteActions.resetDeleteEventFlags()
    );

    this.store.dispatch(
      EventRemoveOfferingActions.removeOffering({ uri: relation.self })
    );
  }

  markConversationAsRead(relation: Relation) {
    this.store.dispatch(
      EventMarkConversationAsReadActions.markConversationAsRead({
        relation: relation,
      })
    );
  }

  updateLastMessage(message: Message) {
    this.store.dispatch(
      EventLastMessageUpdateActions.updateEventLastMessage({ message: message })
    );
  }

  override ngOnDestroy(): void {
    super.ngOnDestroy();
    this.destroyed$.next();
    this.destroyed$.complete();

    if (!this.isGoingToExplore) {
      this.store.dispatch(EventSelectActions.resetSelectEvent());
    }
  }

  navigateToOffering(offering: Offering) {
    this.selectedEvent$.pipe(take(1)).subscribe((event) => {
      this.isGoingToExplore = true;
      this.router.navigate(["services", offering.id]);
    });
  }

  navigateToExplorer(category: keyof OfferingCategory) {
    this.selectedEvent$.pipe(take(1)).subscribe((event) => {
      this.isGoingToExplore = true;
      this.router.navigate(["services"], {
        queryParams: { category: category, availableOn: event?.event.date },
      });
    });
  }
}
