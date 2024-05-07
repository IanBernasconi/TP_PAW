import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Event, EventWithOffering } from 'src/shared/models/event.model';
import { Offering, OfferingRelated } from 'src/shared/models/offering.model';
import { URI } from 'src/shared/types';
import { Router, NavigationExtras } from '@angular/router';
import { User } from 'src/shared/models/user.model';
import { RelationService } from 'src/app/services/relationService/relation.service';
import { of } from 'rxjs';


@Component({
  selector: 'add-to-event-modal',
  templateUrl: './add-to-event-modal.component.html',
  styleUrls: ['./add-to-event-modal.component.scss']
})
export class AddToEventModalComponent {

  modifiedEvents: Map<Event, URI | null> = new Map();
  events: EventWithOffering[] = [];

  constructor(private dialogRef: MatDialogRef<AddToEventModalComponent>, @Inject(MAT_DIALOG_DATA) public data: { offeringData: OfferingRelated, events: EventWithOffering[], provider: User }, private router: Router) {
    if (data.events) {
      for (let event of data.events) {
        this.events.push(new EventWithOffering(event, Array.from(event.offeringsByURI?.values() ?? [])));
      }
    }
  }

  isEventInOffering(event: EventWithOffering) {
    return event.offeringsByURI?.has(this.data.offeringData.offering.self);
  }

  addOfferingToEvent(event: EventWithOffering) {
    event.offeringsByURI?.set(this.data.offeringData.offering.self, this.data.offeringData.offering);
    this.toggleEvent(event, this.data.offeringData.offering.self);
  }

  removeOfferingFromEvent(event: EventWithOffering) {
    event.offeringsByURI?.delete(this.data.offeringData.offering.self);
    this.toggleEvent(event, null);
  }

  isEventToBeRemoved(event: EventWithOffering) {
    return this.modifiedEvents.has(event) && this.modifiedEvents.get(event) === null;
  }

  toggleEvent(event: EventWithOffering, offering: URI | null) {
    if (this.modifiedEvents.has(event)) {
      this.modifiedEvents.delete(event);
    } else {
      this.modifiedEvents.set(event, offering);
    }
  }

  addNewEvent() {
    this.dialogRef.close({ createNew: true });
  }

  isEventDateOccupied(event: Event) {
    return this.data.offeringData.eventDates?.some(date => RelationService.getStartOf(date).toISOString() === RelationService.getStartOf(event.date).toISOString());
  }

}
