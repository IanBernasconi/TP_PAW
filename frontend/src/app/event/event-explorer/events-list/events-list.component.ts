import { Component, EventEmitter, Input, Output } from '@angular/core';
import { OfferingService } from 'src/app/services/offeringService/offering.service';
import { Event, EventWithOffering } from 'src/shared/models/event.model';
import { Offering } from 'src/shared/models/offering.model';
import { Links } from 'src/shared/models/pagination-utils.model';
import { URI } from 'src/shared/types';

@Component({
  selector: 'events-list',
  templateUrl: './events-list.component.html',
  styleUrls: ['./events-list.component.scss']
})
export class EventsListComponent {
  @Input({ required: true }) events!: EventWithOffering[];
  @Input({ required: true }) links: Links | null = new Links({});
  @Input({ required: true }) isLoading!: boolean;
  @Input({ required: true }) isLoadingOfferings!: boolean;

  @Output() clickedEventEvent = new EventEmitter<Event>();
  @Output() changePageEvent = new EventEmitter<URI>();


  getEventOfferings(event: EventWithOffering): Offering[] {
    return Array.from(event.offeringsByURI?.values() ?? []);
  }

}
