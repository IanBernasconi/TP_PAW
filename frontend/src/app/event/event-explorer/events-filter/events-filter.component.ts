import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Event } from 'src/shared/models/event.model';

// map with keys all, active and finished
const filters ={
  active: (event: Event) => event.date >= new Date().toISOString(),
  finished: (event: Event) => event.date < new Date().toISOString(),
}
@Component({
  selector: 'events-filter',
  templateUrl: './events-filter.component.html',
  styleUrls: ['./events-filter.component.scss']
})
export class EventsFilterComponent implements OnInit {
  @Input() filter:any;
  @Output() filterChange = new EventEmitter<any>();


  constructor() { }

  ngOnInit(): void {
    this.updateEventsFilter('active');
  }

  eventsFilter: string = 'active';

  updateEventsFilter(value: keyof typeof filters){
    this.filter= filters[value];
    this.filterChange.emit(this.filter);
  }
}
