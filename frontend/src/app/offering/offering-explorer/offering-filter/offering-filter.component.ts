import {
  Component,
  Output,
  EventEmitter,
  Input,
  ViewChild,
  SimpleChanges,
} from "@angular/core";
import {
  OfferingFilter,
  offeringCategoryPriceTypes,
  SortType,
} from "src/shared/models/offering.model";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { defaultOfferingFilter } from "src/shared/models/offering.model";
import { MatTabChangeEvent, MatTabGroup } from "@angular/material/tabs";
import { User } from "src/shared/models/user.model";
import { EnumService } from "src/app/services/enumService/enum.service";
import { Observable, map, of } from "rxjs";

@Component({
  selector: "offering-filter",
  templateUrl: "./offering-filter.component.html",
  styleUrls: ["./offering-filter.component.scss"],
})
export class OfferingFilterComponent {
  @Input() filter: OfferingFilter = new OfferingFilter();
  @Input({ required: true }) loggedUser?: User;

  constructor(private enumService: EnumService) {}

  @Output() filterChangedEvent = new EventEmitter<OfferingFilter>();

  @ViewChild("tabGroup") tabGroup?: MatTabGroup;

  OfferingCategory$ = this.enumService.getOfferingCategories();
  SortType = SortType;
  districts$ = this.enumService
    .getDistricts()
    .pipe(
      map((districts) =>
        districts.filter(
          (d) =>
            d.name !== "ALL" &&
            d.name !== "NOT_YET_DEFINED" &&
            d.name !== "NOT_SPECIFIED"
        )
      )
    );

  filterForm: FormGroup = new FormGroup({});

  preventFormSubmit = false;

  ngOnInit(): void {
    this.filterForm = new FormGroup({
      category: new FormControl(this.filter.category, [Validators.required]),
      minPrice: new FormControl(this.filter.minPrice, [Validators.min(1)]),
      maxPrice: new FormControl(this.filter.maxPrice, [Validators.min(1)]),
      attendees: new FormControl(this.filter.attendees, [Validators.min(0)]),
      districts: new FormControl({
        value: this.filter.districts,
        disabled: true,
      }),
      likedBy: new FormControl(this.filter.likedBy),
      sortType: new FormControl(this.filter.sortType),
      search: new FormControl(this.filter.search),
      availableOn: new FormControl(this.filter.availableOn),
    });

    this.filterForm.get("sortType")?.valueChanges.subscribe((value) => {
      if (!this.preventFormSubmit) {
        this.onSubmit();
      }
    });

    this.filterForm.get("likedBy")?.valueChanges.subscribe((value) => {
      if (!this.preventFormSubmit) {
        this.onSubmit();
      }
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes["loggedUser"] && this.filter.likedBy && this.tabGroup) {
      this.tabGroup.selectedIndex = 1;
    }
  }

  onSubmit() {
    this.filter = new OfferingFilter(
      this.filterForm.get("category")?.value ?? defaultOfferingFilter.category,
      this.filterForm.get("minPrice")?.value ?? defaultOfferingFilter.minPrice,
      this.filterForm.get("maxPrice")?.value ?? defaultOfferingFilter.maxPrice,
      this.filterForm.get("attendees")?.value ??
        defaultOfferingFilter.attendees,
      this.filterForm.get("districts")?.value ??
        defaultOfferingFilter.districts,
      this.filterForm.get("likedBy")?.value ?? defaultOfferingFilter.likedBy,
      this.filterForm.get("sortType")?.value ?? defaultOfferingFilter.sortType,
      this.filterForm.get("search")?.value ?? defaultOfferingFilter.search,
      this.filterForm.get("availableOn")?.value
    ).validate();
    if (
      this.filterForm.value.districts &&
      this.filterForm.value.districts.length > 0
    ) {
      this.filter.districts = this.filterForm.value.districts;
    }
    this.preventFormSubmit = false;
    this.filterChangedEvent.emit(this.filter);
  }

  onReset() {
    this.preventFormSubmit = true;
    this.filter = new OfferingFilter();
    this.filterForm.patchValue(this.filter);
    this.preventFormSubmit = false;
    this.onSubmit();
  }

  showPriceRangeSelector() {
    return this.filterForm.value.category != "ALL";
  }

  currentCategoryPriceType(): Observable<string | undefined> {
    if (this.filterForm.value.category) {
      return this.enumService.getPriceTypeValue(
        offeringCategoryPriceTypes.get(this.filterForm.value.category) ??
          "OTHER"
      );
    } else {
      return this.enumService.getPriceTypeValue(
        offeringCategoryPriceTypes.get("OTHER") ?? "OTHER"
      );
    }
  }

  toggleEnableDistricts() {
    if (this.filterForm.controls["districts"].disabled) {
      this.filterForm.controls["districts"].enable();
    } else {
      this.filterForm.controls["districts"].disable();
      this.filterForm.patchValue({ districts: [] });
    }
  }

  onTabChange(event: MatTabChangeEvent) {
    if (event.index === 0) {
      this.filterForm.patchValue({ likedBy: undefined });
    } else {
      this.filterForm.patchValue({ likedBy: this.loggedUser?.userId });
    }
  }
}
