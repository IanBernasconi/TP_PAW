import { Component, Input, OnInit, Output, EventEmitter } from "@angular/core";
import {
  Offering,
  OfferingsWithOwners,
} from "src/shared/models/offering.model";
import { HostListener } from "@angular/core";
import { URI } from "src/shared/types";
import { User } from "src/shared/models/user.model";

@Component({
  selector: "offering-list",
  templateUrl: "./offering-list.component.html",
  styleUrls: ["./offering-list.component.scss"],
})
export class OfferingListComponent implements OnInit {
  @Input({ required: true }) offerings!: OfferingsWithOwners | null;
  @Input() likes?: Map<URI, boolean>;
  @Input() isLoadingLikes: boolean = false;
  @Input() isLoading: boolean = false;
  @Input() loggedUser?: User;
  @Input() hidePagination: boolean = false;
  @Input() maxRows?: number;

  @Output() clickedOfferingEvent = new EventEmitter<Offering>();
  @Output() changePageEvent = new EventEmitter<URI>();

  @Output() likeOffering: EventEmitter<Offering> = new EventEmitter<Offering>();
  @Output() deleteLike: EventEmitter<Offering> = new EventEmitter<Offering>();

  cols: number = 4; // Number of columns for the grid list
  mockOfferings = Array(this.cols)
    .fill(0)
    .map((x, i) => i); // Dummy array to iterate over in the template

  ngOnInit(): void {
    this.setCols();
  }

  @HostListener("window:resize", ["$event"])
  onResize(event: any): void {
    this.setCols();
  }

  private setCols(): void {
    this.cols = Math.round(window.innerWidth / 300);
    this.mockOfferings = Array(this.cols)
      .fill(0)
      .map((x, i) => i);
  }

  navigateToOffering(offering: Offering) {
    this.clickedOfferingEvent.emit(offering);
  }
}
