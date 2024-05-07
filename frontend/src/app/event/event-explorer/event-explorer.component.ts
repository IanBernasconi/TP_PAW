import { Component, ViewChild } from "@angular/core";
import { Store } from "@ngrx/store";
import { ActivatedRoute, Router } from "@angular/router";
import { Event, RangeFilter } from "src/shared/models/event.model";
import { selectUser } from "src/app/store/user/user.selector";
import { Subject, filter, take, takeUntil } from "rxjs";
import {
  selectEvents,
  selectEventsLinks,
  selectEventsLoading,
  selectEventsLoadingOfferings,
} from "src/app/store/events/events.selector";
import {
  EventSelectActions,
  EventsFetchActions,
} from "src/app/store/events/events.actions";
import { URI } from "src/shared/types";
import { PaginatedBaseComponent } from "src/app/utils/paginated-base-component";
import { EventService } from "src/app/services/eventService/event-service.service";
import { MatTabChangeEvent, MatTabGroup } from "@angular/material/tabs";
import { RelationService } from "src/app/services/relationService/relation.service";

@Component({
  selector: "app-event-explorer",
  templateUrl: "./event-explorer.component.html",
  styleUrls: ["./event-explorer.component.scss"],
})
export class EventExplorerComponent extends PaginatedBaseComponent {
  @ViewChild("tabGroup") tabGroup?: MatTabGroup;

  filter: any;

  events$ = this.store.select(selectEvents);
  finishedFilter = {
    from: null,
    to: RelationService.getStartOfToday().toISOString(),
  };
  activeFilter = {
    from: RelationService.getStartOfToday().toISOString(),
    to: null,
  };
  eventFilter: RangeFilter = this.activeFilter;
  links$ = this.store.select(selectEventsLinks);
  isLoading$ = this.store.select(selectEventsLoading);
  isLoadingOfferings$ = this.store.select(selectEventsLoadingOfferings);

  loggedUser$ = this.store.select(selectUser);

  constructor(route: ActivatedRoute, router: Router, private store: Store) {
    super(route, router);
  }

  destroy$ = new Subject<void>();

  ngAfterViewInit() {
    if (this.tabGroup) {
      if (this.route.snapshot.queryParamMap.get("tab") === "finished") {
        this.tabGroup.selectedIndex = 1;
      } else {
        this.loggedUser$
          .pipe(
            filter((user) => !!user),
            take(1)
          )
          .subscribe({
            next: (user) => {
              this.store.dispatch(
                EventsFetchActions.fetchEvents({
                  uri: EventService.getURIForPage(
                    this.getPageFromURL(),
                    user!.createdEvents
                  ),
                  filter: this.eventFilter,
                })
              );
            },
          });
        this.tabGroup.selectedIndex = 0;
      }
    }

    this.links$
      .pipe(
        takeUntil(this.destroy$),
        filter((links) => !!links)
      )
      .subscribe((links) => {
        this.persistPageToURL(links.getCurrentPage());
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  navigateToEvent(event: Event) {
    this.router.navigate(["/events", event.id]);
  }

  changePage(uri: URI) {
    this.store.dispatch(
      EventsFetchActions.fetchEvents({ uri, filter: this.eventFilter })
    );
  }

  onTabChange(event: MatTabChangeEvent) {
    if (event.index === 0) {
      this.eventFilter = this.activeFilter;
    } else {
      this.eventFilter = this.finishedFilter;
    }
    this.loggedUser$.pipe(take(1)).subscribe({
      next: (user) => {
        this.store.dispatch(
          EventsFetchActions.fetchEvents({
            uri: EventService.getURIForPage(0, user!.createdEvents),
            filter: this.eventFilter,
          })
        );
      },
      error: (err) => console.log(err),
    });

    this.router.navigate([], {
      queryParams: { tab: event.tab.textLabel?.toLowerCase() ?? "active" },
      replaceUrl: true,
    });
  }
}
