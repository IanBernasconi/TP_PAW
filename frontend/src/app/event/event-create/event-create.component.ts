import { Component } from "@angular/core";
import { Store, createSelector } from "@ngrx/store";
import { Router } from "@angular/router";
import {
  ToastMessages,
  ToastService,
} from "src/app/services/toastService/toast.service";
import { EventToCreate } from "src/shared/models/event.model";
import { selectUser } from "src/app/store/user/user.selector";
import {
  EventAddOfferingActions,
  EventCreateActions,
} from "src/app/store/events/events.actions";
import {
  selectCreateErrorMessage,
  selectFinishCreate,
} from "src/app/store/events/events.selector";
import { BaseComponent } from "src/app/utils/base-component.component";
import { ActivatedRoute } from "@angular/router";
import { ChangeOfferingFromEventsActions } from "src/app/store/offerings/offerings.actions";
import { Offering } from "src/shared/models/offering.model";
import { User } from "src/shared/models/user.model";

@Component({
  selector: "event-create",
  templateUrl: "./event-create.component.html",
  styleUrls: ["./event-create.component.scss"],
})
export class EventCreateComponent extends BaseComponent {
  constructor(
    toastService: ToastService,
    private router: Router,
    store: Store,
    private route: ActivatedRoute
  ) {
    super(store, toastService);
  }

  loggedUser$ = this.store.select(selectUser);
  offeringToAddId?: number;
  offering?: Offering;
  provider?: User;

  ngOnInit() {
    const state = history.state;
    if (state) {
      this.offering = state["offering"];
      this.provider = state["provider"];
    }
  }

  createEvent(event: EventToCreate) {
    this.subscribeToSuccess(
      selectFinishCreate,
      ToastMessages.event.create.success,
      EventCreateActions.resetCreateEventFlags(),
      () => this.router.navigate(["/events"])
    );
    this.subscribeToError(
      selectCreateErrorMessage,
      ToastMessages.event.create.error,
      EventCreateActions.resetCreateEventFlags()
    );

    let offeringToAdd;
    if (this.offering && this.provider) {
      offeringToAdd = { offering: this.offering, provider: this.provider };
    }
    this.store.dispatch(
      EventCreateActions.createEvent({ event, offeringToAdd: offeringToAdd })
    );
  }

  goBack() {
    if (this.offering) {
      this.router.navigate(["/services/", this.offering.id]);
    } else {
      this.router.navigate(["/events"]);
    }
  }
}
