import { Component, Input, OnInit } from "@angular/core";
import { EnumService } from "src/app/services/enumService/enum.service";
import { Event } from "src/shared/models/event.model";

@Component({
  selector: "event-tags",
  templateUrl: "./event-tags.component.html",
  styleUrls: ["./event-tags.component.scss"],
})
export class EventTagsComponent {
  @Input() event!: Event;

  constructor(private enumService: EnumService) { }

  currentDate: string = new Date().toISOString().split("T")[0];
  hoverDate: boolean = false;
  daysLeft: number | null = null;

  calculateDaysLeft(eventDate: string): void {
    const date = new Date(eventDate);
    const now = new Date();
    const timeDifference = date.getTime() - now.getTime();
    this.daysLeft = Math.ceil(timeDifference / (1000 * 3600 * 24));
  }

  getDistrictValue(district: string) {
    return this.enumService.getDistrictValue(district);
  }
}
