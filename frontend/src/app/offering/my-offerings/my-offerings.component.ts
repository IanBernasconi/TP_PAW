import { Component, ViewChild } from "@angular/core";
import { Store } from "@ngrx/store";
import { MatTabChangeEvent, MatTabGroup } from "@angular/material/tabs";
import { ActivatedRoute, Router } from "@angular/router";
import { OfferingStatus } from "src/shared/models/offering-status.model";
import { Offering } from "src/shared/models/offering.model";
import { Relation } from "src/shared/models/relation.model";
import { URI } from "src/shared/types";
import { selectUser } from "src/app/store/user/user.selector";
import { Subject, filter, take, takeUntil, tap } from "rxjs";
import {
  selectUserConversationsData,
  selectUserOfferings,
  selectUserOfferingsLoading,
} from "src/app/store/offerings/user-offerings/user-offerings.selector";
import { UserOfferingsFetchActions } from "src/app/store/offerings/user-offerings/user-offerings.actions";
import { OfferingService } from "src/app/services/offeringService/offering.service";
import { PaginatedBaseComponent } from "src/app/utils/paginated-base-component";
import { Event } from "src/shared/models/event.model";

@Component({
  selector: "my-offerings",
  templateUrl: "./my-offerings.component.html",
  styleUrls: ["./my-offerings.component.scss"],
})
export class MyOfferingsComponent extends PaginatedBaseComponent {
  @ViewChild("tabGroup") tabGroup?: MatTabGroup;

  constructor(route: ActivatedRoute, router: Router, private store: Store) {
    super(route, router);
  }

  offerings$ = this.store.select(selectUserOfferings);
  conversationData$ = this.store.select(selectUserConversationsData);
  isLoading$ = this.store.select(selectUserOfferingsLoading);

  loggedUser$ = this.store.select(selectUser);

  selectedRelation?: Relation;

  OfferingStatus = OfferingStatus;

  destroyed$ = new Subject<void>();

  selectedChatTab: Subject<boolean> = new Subject<boolean>();

  tabIndex: { [key: string]: number } = {
    services: 0,
    conversations: 1,
    calendar: 2,
  };

  tabUrlNames = Object.keys(this.tabIndex);


  ngOnInit() {
    this.loggedUser$
      .pipe(
        filter((user) => !!user),
        take(1),
        tap((user) => {
          this.store.dispatch(
            UserOfferingsFetchActions.fetchUserOfferings({
              uri: OfferingService.getURIForPage(
                this.getPageFromURL(),
                user!.createdOfferings
              ),
              user: user!,
            })
          );
        })
      )
      .subscribe();

    this.route.queryParams
      .pipe(
        takeUntil(this.destroyed$),
        filter((params) => !!params["tab"]),
        tap((params) => {
          if (this.tabGroup) {
            this.tabGroup.selectedIndex =
              this.tabIndex[params["tab"] ?? "services"];
          }
          this.activeTab = this.tabIndex[params["tab"] ?? "services"];
        })
      )
      .subscribe();

    this.offerings$.subscribe((offerings) => {
      if (offerings) {
        this.persistPageToURL(offerings.links.getCurrentPage());
      }
    });
  }

  activeTab: number = 0;

  ngOnDestroy() {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

  fetchOfferings(uri: URI) {
    this.loggedUser$
      .pipe(
        filter((user) => !!user),
        take(1),
        tap((user) => {
          this.store.dispatch(
            UserOfferingsFetchActions.fetchUserOfferings({
              uri: uri,
              user: user!,
            })
          );
        })
      )
      .subscribe();
  }

  private ignoreNextTabChange: boolean = false;

  onTabChanged(event: MatTabChangeEvent) {
    if (!this.ignoreNextTabChange) {
      this.router.navigate([], {
        queryParams: { tab: this.tabUrlNames[event.index] },
        replaceUrl: true,
      });
      if (event.index === 1) {
        this.selectedChatTab.next(true);
      } else {
        this.selectedChatTab.next(false);
      }
    }
    this.ignoreNextTabChange = false;
  }

  goBack() {
    this.router.navigate(["/services"]);
  }

  navigateToOffering(offering: Offering) {
    this.router.navigate(["services", offering.id]);
  }

  calendarRelationSelected(data: { relation: Relation; event: Event }) {
    this.offerings$
      .pipe(
        take(1),
        tap((offerings) => {
          if (offerings) {
            this.ignoreNextTabChange = true;
            const isPastEvent = new Date(data.event.date) < new Date();
            this.router.navigate([], {
              queryParams: {
                tab: this.tabUrlNames[1],
                service: offerings.offeringsByURI.get(data.relation.offering)
                  ?.id,
                conversation: data.relation.relationId,
                status: isPastEvent ? "past" : null,
              },
            });
          }
        })
      )
      .subscribe();
  }
}
