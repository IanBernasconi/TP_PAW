import { Component, OnInit } from "@angular/core";
import { Store } from "@ngrx/store";
import { OfferingService } from "src/app/services/offeringService/offering.service";
import {
  Offering,
  OfferingFilter,
  Offerings,
  SortType,
} from "src/shared/models/offering.model";
import { ActivatedRoute, Router } from "@angular/router";
import { URI } from "src/shared/types";
import {
  selectOfferings,
  selectOfferingsLikes,
  selectOfferingsLikesLoading,
  selectOfferingsLoading,
} from "src/app/store/offerings/offerings.selector";
import {
  OfferingSelectActions,
  OfferingsFetchActions,
  OfferingsLikeActions,
} from "src/app/store/offerings/offerings.actions";
import { PaginatedBaseComponent } from "src/app/utils/paginated-base-component";
import { selectUser } from "src/app/store/user/user.selector";
import { take } from "rxjs";
import { EventSelectActions } from "src/app/store/events/events.actions";

@Component({
  selector: "app-offering-explorer",
  templateUrl: "./offering-explorer.component.html",
  styleUrls: ["./offering-explorer.component.scss"],
})
export class OfferingExplorerComponent extends PaginatedBaseComponent {
  constructor(route: ActivatedRoute, router: Router, private store: Store) {
    super(route, router);
  }

  loggedUser$ = this.store.select(selectUser);

  offerings$ = this.store.select(selectOfferings);
  isLoading$ = this.store.select(selectOfferingsLoading);

  offeringsLikes$ = this.store.select(selectOfferingsLikes);
  isLoadingLikes$ = this.store.select(selectOfferingsLikesLoading);

  filter: OfferingFilter = new OfferingFilter();

  ngOnInit(): void {
    this.filter = new OfferingFilter(
      this.route.snapshot.queryParams["category"] ?? this.filter.category,
      this.route.snapshot.queryParams["minPrice"] ?? this.filter.minPrice,
      this.route.snapshot.queryParams["maxPrice"] ?? this.filter.maxPrice,
      this.route.snapshot.queryParams["attendees"] ?? this.filter.attendees,
      this.route.snapshot.queryParams["districts"] != "undefined" &&
        this.route.snapshot.queryParams["districts"] != undefined
        ? [this.route.snapshot.queryParams["districts"]]
        : this.filter.districts,
      this.route.snapshot.queryParams["likedBy"] ?? this.filter.likedBy,
      this.route.snapshot.queryParams["sortType"] ?? this.filter.sortType,
      this.route.snapshot.queryParams["search"] ?? this.filter.search,
      this.route.snapshot.queryParams["availableOn"] ?? this.filter.availableOn
    ).validate();

    const uri = OfferingService.getURIForPage(this.getPageFromURL());
    this.fetchOfferings(uri);

    this.offerings$.subscribe((offerings) => {
      if (offerings) {
        this.persistPageToURL(offerings.links.getCurrentPage());
      }
    });
  }

  filterChanged(filter: OfferingFilter) {
    if (this.filter.category !== filter.category) {
      this.store.dispatch(EventSelectActions.resetSelectEvent());
    }
    this.filter = filter;
    this.router.navigate([], {
      queryParamsHandling: "merge",
      queryParams: filter,
    });
    this.fetchOfferings();
  }

  fetchOfferings(uri?: URI) {
    this.store.dispatch(
      OfferingsFetchActions.fetchOfferings({ uri: uri, filter: this.filter })
    );
  }

  navigateToOffering(offering: Offering) {
    this.router.navigate(["services", offering.id]);
  }

  likeOffering(offering: Offering) {
    this.loggedUser$.pipe(take(1)).subscribe((user) => {
      if (user) {
        this.store.dispatch(
          OfferingsLikeActions.likeOffering({
            offering: offering.self,
            user: user,
          })
        );
      } else {
        this.router.navigate(["/login"]);
      }
    });
  }

  deleteLike(offering: Offering) {
    this.loggedUser$.pipe(take(1)).subscribe((user) => {
      if (user) {
        this.store.dispatch(
          OfferingsLikeActions.deleteLike({
            offering: offering.self,
            user: user,
          })
        );
      } else {
        this.router.navigate(["/login"]);
      }
    });
  }
}
