import {
  Component,
  EventEmitter,
  Input,
  Output,
  SimpleChanges,
  TemplateRef,
  ViewChild,
} from "@angular/core";
import { Router } from "@angular/router";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { CalendarMonthViewDay } from "angular-calendar";
import { CalendarEvent } from "calendar-utils";
import { Observable, Subject, take, takeUntil, tap } from "rxjs";
import { OfferingStatus } from "src/shared/models/offering-status.model";
import { Relation } from "src/shared/models/relation.model";
import { User } from "src/shared/models/user.model";
import { BaseCalendarComponent } from "src/app/utils/base-calendar.component";
import { Store } from "@ngrx/store";
import {
  selectProviderCalendarSelectedOffering,
  selectProviderCalendarData,
} from "src/app/store/offerings/user-offerings/user-offerings.selector";
import {
  ProviderCalendarSelectOffering,
  UserRelationsFetchActions,
} from "src/app/store/offerings/user-offerings/user-offerings.actions";
import { EnumService } from "src/app/services/enumService/enum.service";
import { Event } from "src/shared/models/event.model";
import {
  Offering,
  ProviderCalendarData,
} from "src/shared/models/offering.model";

interface EventGroupMeta {
  type: string;
}

@Component({
  selector: "calendar",
  templateUrl: "./calendar.component.html",
  styleUrls: ["./calendar.component.scss"],
})
export class CalendarComponent extends BaseCalendarComponent {
  @Input({ required: true }) loggedUser!: User;
  @Output() onRelationSelected = new EventEmitter<{
    relation: Relation;
    event: Event;
  }>();

  @ViewChild("modalContent") modalContent!: TemplateRef<any>;

  selectedRelation?: Relation;

  locale: string = $localize`en`;

  constructor(
    private modal: NgbModal,
    private router: Router,
    private store: Store,
    private enumService: EnumService
  ) {
    super();
  }

  offering$ = this.store.select(selectProviderCalendarSelectedOffering);
  calendarData$: Observable<ProviderCalendarData | undefined> =
    this.store.select(selectProviderCalendarData);

  calendarEvents: CalendarEvent[] = [];

  OfferingStatus = OfferingStatus;

  destroyed$ = new Subject<void>();

  ngOnInit(): void {
    this.fetchRelations();

    this.calendarData$.pipe(takeUntil(this.destroyed$)).subscribe({
      next: (calendarData) => {
        const allEvents: CalendarEvent[] = [];
        if (calendarData) {
          for (let relation of calendarData.relations.relations) {
            const event = calendarData.events.eventsByURI.get(relation.event);
            if (event) {
              allEvents.push({
                id: relation.self,
                title: event.name,
                start: new Date(event.date),
                color: {
                  primary: this.getPrimaryColor(relation.status),
                  secondary: this.getSecondaryColor(relation.status),
                },
                meta: {
                  type: relation.status,
                  numberOfGuests: calendarData.events.eventsByURI.get(
                    relation.event
                  )?.numberOfGuests,
                  district: calendarData.events.eventsByURI.get(relation.event)
                    ?.district,
                },
              });
            }
          }
        }
        this.calendarEvents = allEvents;
      },
    });
  }

  fetchRelations() {
    if (this.loggedUser?.provider) {
      this.store.dispatch(
        UserRelationsFetchActions.fetchUserRelations({
          uri: this.loggedUser.providerRelations,
          filter: {
            status: ["DONE", "PENDING", "ACCEPTED"],
            from: this.calculateStartOfMonth().toISOString(),
            to: this.calculateEndOfMonth().toISOString(),
          },
        })
      );
    }
  }

  getDistrict(event: CalendarEvent): Observable<string | undefined> {
    return this.getDistrictValue(event.meta.district);
  }

  getDistrictValue(district: string): Observable<string | undefined> {
    return this.enumService.getDistrictValue(district);
  }

  beforeMonthViewRender({ body }: { body: CalendarMonthViewDay[] }): void {
    body.forEach((cell) => {
      const groups: { [key: string]: CalendarEvent<EventGroupMeta>[] } = {};
      cell.events.forEach((event: CalendarEvent<EventGroupMeta>) => {
        groups[event.meta!.type] = groups[event.meta!.type] || [];
        groups[event.meta!.type].push(event);
      });

      (cell as any)["eventGroups"] = Object.entries(groups);
    });
  }

  goToChat(): void {
    this.modal.dismissAll();
    this.calendarData$
      .pipe(
        take(1),
        tap((calendarData) => {
          if (calendarData) {
            this.goToChatEmit(calendarData);
          }
        })
      )
      .subscribe();
  }

  private goToChatEmit(calendarData: ProviderCalendarData) {
    this.onRelationSelected.emit({
      relation: this.selectedRelation!,
      event: calendarData.events.eventsByURI.get(
        this.selectedRelation!.event
      )!,
    });
  }


  getPrimaryColor(status: string): string {
    const rootStyles = getComputedStyle(document.documentElement);
    switch (status) {
      case "PENDING":
        return rootStyles.getPropertyValue("--pending-color").trim();
      case "ACCEPTED":
        return rootStyles.getPropertyValue("--accepted-color").trim();
      case "DONE":
        return rootStyles.getPropertyValue("--done-color").trim();
      case "REJECTED":
        return rootStyles.getPropertyValue("--rejected-color").trim();
      default:
        return "#ff0000"; // default color
    }
  }

  getSecondaryColor(status: string): string {
    const primaryColor = this.getPrimaryColor(status);
    return primaryColor + "44";
  }

  closeOpenMonthViewDay() {
    this.activeDayIsOpen = false;
    this.fetchRelations();
  }

  handleEvent(action: string, event: CalendarEvent, goToChat: Boolean = false): void {
    this.calendarData$
      .pipe(
        take(1),
        tap((calendarData) => {
          if (calendarData) {
            this.selectedRelation =
              calendarData.relations.relationsByURIMap.get(event.id! as string);
            if (this.selectedRelation) {
              this.store.dispatch(
                ProviderCalendarSelectOffering.selectProviderCalendarOffering({
                  uri: this.selectedRelation.offering,
                })
              );
              if (!goToChat) {
                this.modal.open(this.modalContent, {
                  size: "lg",
                  centered: true,
                });
              } else {
                this.goToChatEmit(calendarData);
              }
            }
          }
        })
      )
      .subscribe();
  }

  ngOnDestroy() {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  goBack() {
    this.router.navigate(["/services"]);
  }
}
