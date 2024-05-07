import {
  Component,
  EventEmitter,
  Input,
  Output,
  SimpleChanges,
} from "@angular/core";
import { CalendarEvent } from "angular-calendar";
import { Event, RangeFilter } from "src/shared/models/event.model";
import { BaseCalendarComponent } from "src/app/utils/base-calendar.component";
import { URI } from "src/shared/types";
import { RelationService } from "src/app/services/relationService/relation.service";

@Component({
  selector: "calendar-availability",
  templateUrl: "./calendar-availability.component.html",
  styleUrls: ["./calendar-availability.component.scss"],
})
export class CalendarAvailabilityComponent extends BaseCalendarComponent {
  @Input({ required: true }) eventDates!: Date[];
  @Input({ required: true }) userEvents!: Event[];

  @Output() fetchDates: EventEmitter<RangeFilter> = new EventEmitter();
  @Output() goToEvent: EventEmitter<Event> = new EventEmitter();

  excludedDates: Map<string, boolean> = new Map();

  calendarEvents: CalendarEvent[] = [];

  locale: string = $localize`en`;

  ngOnInit(): void {
    this.getOwnerOccupiedDates();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes["eventDates"]) {
      this.excludedDates.clear();
      this.eventDates?.forEach((eventDate) => {
        this.excludedDates.set(this.formatDate(eventDate), true);
      });
    }
    if (changes["userEvents"]) {
      this.calendarEvents = [];
      this.userEvents.forEach((event) => {
        this.calendarEvents.push({
          id: event.id,
          start: new Date(event.date),
          title: event.name,
          color: {
            primary: "000000",
            secondary: "000000",
          },
          meta: {
            event: event,
          },
        });
      });
    }
  }

  formatDate(date: Date): string {
    let month = "" + (date.getMonth() + 1),
      day = "" + date.getDate(),
      year = date.getFullYear();

    if (month.length < 2) month = "0" + month;
    if (day.length < 2) day = "0" + day;

    return [year, month, day].join("-");
  }

  handleEvent(action: string, event: CalendarEvent): void {
    this.goToEvent.emit(event.meta!.event);
  }

  isDateOccupied(date: Date): boolean {
    const formattedInputDate = this.formatDate(date);
    return (
      this.excludedDates.has(formattedInputDate) || this.isDateInPast(date)
    );
  }

  getOwnerOccupiedDates() {
    this.fetchDates.emit({
      from: RelationService.getStartOfToday().toISOString()
    });
  }
}
