import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { Store } from "@ngrx/store";
import { GuestService } from "src/app/services/guestService/guest.service";
import {
  ToastMessages,
  ToastService,
} from "src/app/services/toastService/toast.service";
import { BaseComponent } from "src/app/utils/base-component.component";
import { Guest, GuestStatusUpdate } from "src/shared/models/guest.model";

@Component({
  selector: "answer-guest-invitation",
  templateUrl: "./answer-guest-invitation.component.html",
  styleUrls: ["./answer-guest-invitation.component.scss"],
})
export class AnswerGuestInvitationComponent
  extends BaseComponent
  implements OnInit {
  constructor(
    toastService: ToastService,
    store: Store,
    private route: ActivatedRoute,
    private router: Router,
    private guestService: GuestService
  ) {
    super(store, toastService);
  }

  ngOnInit(): void {
    if (this.route.queryParams) {
      this.route.queryParams.subscribe((params) => {
        const token = params["token"];
        const eventIdParam = this.route.snapshot.paramMap.get("eventId");
        const guestIdParam = this.route.snapshot.paramMap.get("guestId");
        if (eventIdParam && guestIdParam && token) {
          const eventId = parseInt(eventIdParam, 10);
          const guestId = parseInt(guestIdParam, 10);

          // if url container "accept"
          const acceptAnswer = this.route.snapshot.toString().includes("accept");
          const rejectAnswer = this.route.snapshot.toString().includes("reject");
          let guestStatusUpdate: GuestStatusUpdate;
          if (acceptAnswer && !rejectAnswer) {
            guestStatusUpdate = {
              status: "ACCEPTED",
              token: token,
            };
          } else if (rejectAnswer && !acceptAnswer) {
            guestStatusUpdate = {
              status: "REJECTED",
              token: token,
            };
          } else {
            this.toastService.error(ToastMessages.guest.accept.error);
            this.router.navigate([""]);
          }

          this.guestService
            .updateGuestStatus(eventId, guestId, guestStatusUpdate!)
            .subscribe({
              next: () => {
                this.toastService.success(
                  acceptAnswer
                    ? ToastMessages.guest.accept.success
                    : ToastMessages.guest.reject.success
                );

                this.router.navigate([""]);
              },
              error: (err) => {
                if (err.status === 409) {
                  this.toastService.warn(ToastMessages.guest.alreadyAnswered);
                } else {
                  this.toastService.error(
                    acceptAnswer
                      ? ToastMessages.guest.accept.error
                      : ToastMessages.guest.reject.error
                  );
                }
                this.router.navigate([""]);
              },
            });
        }
      });
    }
  }
}
