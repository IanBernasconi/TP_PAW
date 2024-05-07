import { Component, EventEmitter, Input, OnInit, Output, SimpleChange, SimpleChanges } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { GuestService } from "src/app/services/guestService/guest.service";
import { Event, EventRelated } from "src/shared/models/event.model";
import { Guest, GuestStatus, Guests } from "src/shared/models/guest.model";
import { URI } from "src/shared/types";
import { NewGuestDialogComponent } from "./new-guest-dialog/new-guest-dialog.component";
import { DeleteGuestDialogComponent } from "./delete-guest-dialog/delete-guest-dialog.component";
import { forkJoin } from "rxjs";
import {
  ToastMessages,
  ToastService,
} from "src/app/services/toastService/toast.service";

@Component({
  selector: "event-guests",
  templateUrl: "./event-guests.component.html",
  styleUrls: ["./event-guests.component.scss"],
})
export class EventGuestsComponent {
  @Input({ required: true }) event!: Event;
  @Input({ required: true }) active: boolean = true;

  errorMessage?: string;

  guests!: Guests;

  GuestStatus = GuestStatus;

  constructor(
    public dialog: MatDialog,
    private guestService: GuestService,
    private toastService: ToastService
  ) { }

  ngOnChanges(changes: SimpleChanges): void {
    if (["event"]) {
      if (this.event) {
        this.fetchGuests();
      }
    }
  }

  openAddGuestDialog() {
    const dialogRef = this.dialog.open(NewGuestDialogComponent, {
      data: this.guests.guests.map((guest) => guest.email),
      width: "600px",
    });

    dialogRef.afterClosed().subscribe({
      next: (result) => {
        if (result) {
          let guest: Guest = {
            email: result,
            status: "NEW",
          };
          this.addGuest(guest);
        }
      },
      error: (err) =>
        (this.errorMessage = "There was an error adding the guest"),
    });
  }

  openDeleteGuestDialog(guest: Guest) {
    const dialogRef = this.dialog.open(DeleteGuestDialogComponent, {
      data: guest,
    });

    dialogRef.afterClosed().subscribe({
      next: (result) => {
        if (result) {
          this.deleteGuest(guest);
        }
      },
      error: (err) =>
        (this.errorMessage = "There was an error deleting the guest"),
    });
  }

  hasUninvitedGuests(): boolean {
    if (!this.guests) return false;
    return this.guests.guests.some((guest) => guest.status === "NEW");
  }

  changePage(page: URI) {
    this.guestService.getGuests(page).subscribe({
      next: (guests) => {
        if (guests.guests.length === 0) {
          this.fetchGuests();
          return;
        };
        this.guests = guests;
      },
    });
  }

  addGuest(guest: Guest) {
    this.guestService.addGuest(guest, this.event.guests).subscribe({
      next: (guest) => {
        this.guestService.getGuests(this.event.guests).subscribe({
          next: (guests) => {
            this.guests = guests;
            this.toastService.success(ToastMessages.guest.add.success);
          },
        });
      },
    });
  }

  deleteGuest(guest: Guest) {
    if (!guest.self) return;
    this.guestService.deleteGuest(guest.self).subscribe({
      next: () => {
        this.guests.guests = this.guests.guests.filter(
          (g) => g.self != guest.self
        );
        this.toastService.success(ToastMessages.guest.delete.success);
      },
    });
  }

  inviteAllGuests() {
    const observables = this.guests.guests
      .filter((guest) => guest.status == "NEW")
      .map((guest) => {
        guest.status = "PENDING";
        if (!guest.self) return;
        return this.guestService.updateGuest(guest.self, guest);
      });

    forkJoin(observables).subscribe({
      next: () => {
        this.guestService.getGuests(this.event.guests).subscribe((guests) => {
          this.guests = guests;
          this.toastService.success(ToastMessages.guest.invite.success);
        });
      },
    });
  }

  fetchGuests() {
    if (!this.event) return;
    this.guestService.getGuests(this.event.guests).subscribe({
      next: (guests) => (this.guests = guests),
    });
  }
}
