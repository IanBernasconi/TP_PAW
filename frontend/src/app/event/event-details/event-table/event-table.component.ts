import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { Store } from '@ngrx/store';
import { Event, RelationInfo } from 'src/shared/models/event.model';
import { Relation } from 'src/shared/models/relation.model';
import { MatDialog } from '@angular/material/dialog';
import { DeleteRelationDialogComponent, DeleteRelationDialogData } from './delete-relation-dialog/delete-relation-dialog.component';
import { ContactProviderDialogComponent, ContactProviderDialogData } from './contact-provider-dialog/contact-provider-dialog.component';
import { OfferingStatus } from 'src/shared/models/offering-status.model';
import { ReviewDialogComponent, ReviewDialogData } from './review-dialog/review-dialog.component';
import { selectUser } from 'src/app/store/user/user.selector';
import { URI } from 'src/shared/types';
import { FormControl } from '@angular/forms';
import { EnumService } from 'src/app/services/enumService/enum.service';
import { filter, map, take } from 'rxjs';
import { OfferingCategory } from 'src/shared/models/utils.model';
import { Offering } from 'src/shared/models/offering.model';

@Component({
  selector: 'event-table',
  templateUrl: './event-table.component.html',
  styleUrls: ['./event-table.component.scss']
})
export class EventTableComponent implements OnInit {
  @Input({ required: true }) event!: Event;
  @Input({ required: true }) relationsByOfferingCategory!: Map<keyof OfferingCategory, URI[]>
  @Input({ required: true }) relations!: Map<URI, RelationInfo>;
  @Input({ required: true }) active!: boolean;

  @Output() reviewOffering = new EventEmitter<ReviewDialogData>();
  @Output() contactProvider = new EventEmitter<ContactProviderDialogData>();
  @Output() removeRelation = new EventEmitter<Relation>();
  @Output() goToExplore = new EventEmitter<keyof OfferingCategory>();
  @Output() goToService = new EventEmitter<Offering>();

  description!: string;
  relationsByOfferingCategoryKeys: (keyof OfferingCategory)[] = [];
  loggedUser$ = this.store.select(selectUser);
  OfferingStatus = OfferingStatus;
  OfferingCategory = this.enumService.getOfferingCategories();

  notIncludedCategories: OfferingCategory[] = [];


  otherCategory = new FormControl(
    { value: '', disabled: true }
  );

  flatRelationsUri: URI[] = [];

  constructor(public dialog: MatDialog, private store: Store, private enumService: EnumService) { }

  getRelationsByCategory(category: keyof OfferingCategory): RelationInfo[] {
    return this.relationsByOfferingCategory.get(category)?.map(uri => this.relations.get(uri)!) ?? [];
  }


  ngOnInit(): void {
    this.description = this.event?.description;
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['relationsByOfferingCategory']) {
      this.relationsByOfferingCategoryKeys = Array.from(this.relationsByOfferingCategory.keys());
      this.OfferingCategory.pipe(take(1)).subscribe(categories => {
        this.notIncludedCategories = categories.filter(category => !this.relationsByOfferingCategoryKeys.includes(category.name as keyof OfferingCategory));
      })
      this.flatRelationsUri = [...this.relationsByOfferingCategory.values()].reduce((acc, val) => acc.concat(val), []);
    }
  }

  openDeleteRelationDialog(relationInfo: RelationInfo) {
    const dialogRef = this.dialog.open(DeleteRelationDialogComponent, {
      data: new DeleteRelationDialogData(relationInfo)
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.removeRelation.emit(relationInfo.relation);
      }
    });
  }

  enableOtherCategory() {
    this.otherCategory.enable();
    if (this.otherCategory.value === '') {
      this.otherCategory.setValue(this.notIncludedCategories[0].name);
    }
  }

  openContactProviderDialog(relationInfo: RelationInfo) {
    const dialogRef = this.dialog.open(ContactProviderDialogComponent, {
      data: new ContactProviderDialogData(relationInfo)
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.contactProvider.emit(result);
      }
    });
  }

  openReviewDialog(relationInfo: RelationInfo) {
    let content: string = "";
    let rating: number = 5;
    const dialogRef = this.dialog.open(ReviewDialogComponent, {
      data: new ReviewDialogData(content, rating, relationInfo),
      width: '500px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result.rating > 0) {
        this.reviewOffering.emit(result);
      }
    });
  }

  goToExploreCategory(category: keyof OfferingCategory | string) {
    this.goToExplore.emit(category as keyof OfferingCategory);
  }

  getCategoryValue(category: string) {
    return this.enumService.getCategoryValue(category);
  }
}