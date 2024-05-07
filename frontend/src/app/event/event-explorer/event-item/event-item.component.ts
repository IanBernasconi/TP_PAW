import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Event } from 'src/shared/models/event.model';
import { Offering, Offerings } from 'src/shared/models/offering.model';

@Component({
  selector: 'event-item',
  templateUrl: './event-item.component.html',
  styleUrls: ['./event-item.component.scss']
})
export class EventItemComponent {
  @Input() offerings: Offering[] | undefined;
  @Input() event: Event | undefined;
  @Input() isLoading: boolean | undefined;
  @Input() isLoadingOfferings: boolean | undefined;
  @Output() clickedEventEvent = new EventEmitter<Event>();

  sortedOfferings: Offering[] = [];

  ngOnInit(): void {
    if (this.offerings) {
      this.sortedOfferings = this.offerings.sort((a, b) => b.images.length - a.images.length);
    }
  }

}
