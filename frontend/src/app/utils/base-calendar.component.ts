import { Directive } from '@angular/core';
import { CalendarEvent, CalendarView } from 'angular-calendar';

@Directive()
export class BaseCalendarComponent {
    constructor() {
        this.viewDate = new Date();
    }

    view: CalendarView = CalendarView.Month;
    viewDate!: Date;
    activeDayIsOpen: boolean = false;

    currentDay: Date = new Date();

    dayClicked({ date, events }: { date: Date; events: CalendarEvent[] }): void {
        if (this.isSameMonth(date, this.viewDate)) {
            if (
                (this.isSameDay(this.viewDate, date) && this.activeDayIsOpen === true) ||
                events.length === 0
            ) {
                this.activeDayIsOpen = false;
            } else {
                this.activeDayIsOpen = true;
            }
            this.viewDate = date;
        }
    }

    isSameDay(date1: Date, date2: Date): boolean {
        return date1.setHours(0, 0, 0, 0) === date2.setHours(0, 0, 0, 0);
    }

    isSameMonth(date1: Date, date2: Date): boolean {
        return date1.getFullYear() === date2.getFullYear() && date1.getMonth() === date2.getMonth();
    }

    calculateStartOfMonth(): Date {
        const date = new Date(this.viewDate);
        date.setDate(1);
        date.setHours(0, 0, 0, 0);
        const dayOfWeek = date.getDay();
        date.setDate(date.getDate() - ((dayOfWeek + 7) % 7)); // Subtract the number of days to the previous Sunday
        return date;
    }

    calculateEndOfMonth(): Date {
        const date = new Date(this.viewDate);
        date.setMonth(date.getMonth() + 1);
        date.setDate(0);
        date.setHours(23, 59, 59, 999);
        const dayOfWeek = date.getDay();
        date.setDate(date.getDate() + ((13 - dayOfWeek) % 7)); // Add the number of days to the next Saturday
        return date;
    }

    isDateInPast(date: Date): boolean {
        return date < this.currentDay;
    }
}