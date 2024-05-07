import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Events, Event, EventRelated, EventWithOffering } from 'src/shared/models/event.model';
import { Offering, OfferingRelated } from 'src/shared/models/offering.model';
import { User } from 'src/shared/models/user.model';
import { AddToEventModalComponent } from './add-to-event-modal/add-to-event-modal.component';
import { MatDialog } from '@angular/material/dialog';
import { URI } from 'src/shared/types';
import { NavigationExtras, Router } from '@angular/router';

@Component({
  selector: 'offering-contact-information',
  templateUrl: './contact-information.component.html',
  styleUrls: ['./contact-information.component.scss']
})
export class ContactInformationComponent {
  @Input() offeringData?: OfferingRelated;
  @Input() loggedUser?: User;
  @Input() userEvents?: EventWithOffering[];
  @Input() selectedEvent?: EventRelated; // Todo change how selected event works

  @Input() isLoading: boolean = false;

  @Output() changeOfferingFromEvents: EventEmitter<{ offering: Offering, eventsToAdd: Event[], eventsToRemove: Event[], provider: User }> = new EventEmitter();
  @Output() addToSelectedEventEmitter: EventEmitter<{ offering: Offering, eventToAdd: Event, provider: User }> = new EventEmitter();
  @Output() removeFromSelectedEventEmitter: EventEmitter<{ offering: Offering, eventToRemove: Event, provider: User }> = new EventEmitter();


  constructor(public dialog: MatDialog, private router: Router) { }

  openHireDialog() {
    const dialogRef = this.dialog.open(AddToEventModalComponent, {
      data: {
        offeringData: this.offeringData,
        events: this.userEvents,
        provider: this.offeringData?.owner
      },
      height: '60%',
      width: '35%'
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        if (result.createNew) {
          this.addNewEvent();
          return;
        }
        const eventsToAdd: Event[] = [];
        const eventsToRemove: Event[] = [];
        result = result as Map<Event, URI | null>;
        for (let [event, offering] of result.entries()) {
          if (offering) {
            eventsToAdd.push(event);
          } else {
            eventsToRemove.push(event);
          }
        }

        if (this.offeringData) {
          this.changeOfferingFromEvents.emit({ offering: this.offeringData.offering, eventsToAdd, eventsToRemove, provider: this.offeringData.owner! });
        }
      }
    });
  }

  addToSelectedEvent() {
    if (this.selectedEvent && this.offeringData) {
      this.addToSelectedEventEmitter.emit({ offering: this.offeringData.offering, eventToAdd: this.selectedEvent.event, provider: this.offeringData.owner! });
    }
  }

  removeFromSelectedEvent() {
    if (this.selectedEvent && this.offeringData) {
      this.removeFromSelectedEventEmitter.emit({ offering: this.offeringData.offering, eventToRemove: this.selectedEvent.event, provider: this.offeringData.owner! });
    }
  }

  selectedEventHasOffering() {
    return this.selectedEvent?.relations.some(rel => rel.offering.self === this.offeringData?.offering.self);
  }

  isFutureEvent(event: Event) {
    return new Date(event.date) > new Date();
  }


  addNewEvent() {
    const navigationExtras: NavigationExtras = {
      state: {
        offering: this.offeringData?.offering,
        provider: this.offeringData?.owner
      }
    };
    this.router.navigate(['/events/create'], navigationExtras);
  }

}
