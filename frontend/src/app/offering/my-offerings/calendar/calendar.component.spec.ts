import { ComponentFixture, TestBed } from "@angular/core/testing";

import { CalendarComponent } from "./calendar.component";
import { provideMockStore } from "@ngrx/store/testing";
import { CalendarDatePipe } from "angular-calendar/modules/common/calendar-date/calendar-date.pipe";
import { formatDate } from "@angular/common";
import {
  CalendarCommonModule,
  CalendarDateFormatter,
  CalendarMonthModule,
  CalendarUtils,
  DateAdapter,
  DateFormatterParams,
} from "angular-calendar";
import { adapterFactory } from "angular-calendar/date-adapters/date-fns";
import { HttpClientTestingModule } from "@angular/common/http/testing";

interface GetWeekViewHeaderArgs {
  viewDate: Date;
  weekStartsOn: number;
  excluded?: number[];
  weekendDays?: number[];
}

interface GetWeekViewArgs {
  events: any[]; // Replace with the actual type of your events
  viewDate: Date;
  weekStartsOn: number;
  excluded?: number[];
  weekendDays?: number[];
}

interface GetMonthViewArgs {
  events: any[]; // Replace with the actual type of your events
  viewDate: Date;
  weekStartsOn: number;
  excluded?: number[];
  weekendDays?: number[];
  monthStartsOn?: number;
  monthEndsOn?: number;
}

interface WeekDay {
  date: Date;
  day: number;
  isPast: boolean;
  isToday: boolean;
  isFuture: boolean;
  isWeekend: boolean;
}

interface WeekView {
  period: {
    start: Date;
    end: Date;
  };
  allDayEventRows: any[]; // Replace with the actual type of your events
  hourColumns: any[]; // Replace with the actual type of your events
}

interface MonthView {
  rowOffsets: number[];
  days: any[]; // Replace with the actual type of your days
}

describe("CalendarComponent", () => {
  let component: CalendarComponent;
  let fixture: ComponentFixture<CalendarComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [CalendarCommonModule, CalendarMonthModule, HttpClientTestingModule],
      declarations: [CalendarComponent],
      providers: [
        provideMockStore({}),
        { provide: DateAdapter, useFactory: adapterFactory },
        {
          provide: CalendarDateFormatter,
          useValue: {
            monthViewTitle: ({ date, locale }: DateFormatterParams) =>
              formatDate(date, "MMM y", locale || "en-US"),
          },
          // Add other required formatters here
        },
        {
          provide: CalendarUtils,
          useValue: {
            getWeekViewHeader: ({
              viewDate,
              weekStartsOn,
              excluded,
              weekendDays,
            }: GetWeekViewHeaderArgs): WeekDay[] => {
              // Mock implementation or return mock data
              return [];
            },
            getWeekView: ({
              events,
              viewDate,
              weekStartsOn,
              excluded,
              weekendDays,
            }: GetWeekViewArgs): WeekView => {
              // Mock implementation or return mock data
              return {
                period: { start: new Date(), end: new Date() },
                allDayEventRows: [],
                hourColumns: [],
              };
            },
            getMonthView: ({
              events,
              viewDate,
              weekStartsOn,
              excluded,
              weekendDays,
              monthStartsOn,
              monthEndsOn,
            }: GetMonthViewArgs): MonthView => {
              // Mock implementation or return mock data
              return { rowOffsets: [], days: [] };
            },
            // Add other required methods here
          },
        },
      ],
    });
    fixture = TestBed.createComponent(CalendarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
