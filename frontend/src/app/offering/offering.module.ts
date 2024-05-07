import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OfferingCreateComponent } from './offering-create/offering-create.component';
import { RouterModule, Routes, Scroll } from '@angular/router';
import { OfferingDataFormComponent } from './offering-data-form/offering-data-form.component';
import { OfferingCardComponent } from './offering-list/offering-card/offering-card.component';
import { HeaderComponent } from './offering-details/header/header.component';
import { OfferingDetailsImagesComponent } from './offering-details/offering-details-images/offering-details-images.component';
import { ContactInformationComponent } from './offering-details/contact-information/contact-information.component';
import { OfferingDetailsComponent } from './offering-details/offering-details.component';
import { OfferingExplorerComponent } from './offering-explorer/offering-explorer.component';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatSliderModule } from '@angular/material/slider';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatRadioModule } from '@angular/material/radio';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { OfferingFilterComponent } from './offering-explorer/offering-filter/offering-filter.component';
import { MyOfferingsComponent } from './my-offerings/my-offerings.component';
import { OfferingListComponent } from './offering-list/offering-list.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { EditOfferingComponent } from './edit-offering/edit-offering.component';
import { DeleteOfferingDialogComponent } from './delete-offering-dialog/delete-offering-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { ReviewsComponent } from './offering-details/reviews/reviews.component';
import { NgbPopoverModule, NgbRating } from '@ng-bootstrap/ng-bootstrap';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { UtilsModule } from '../utils/utils.module';
import { MatTabsModule } from '@angular/material/tabs';
import { MatBadgeModule } from '@angular/material/badge';
import { MatToolbarModule } from '@angular/material/toolbar';
import { OfferingsChatComponent } from './my-offerings/chat-list-component/offerings-chat/offerings-chat.component';
import { CalendarComponent } from './my-offerings/calendar/calendar.component';
import { CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { ImageUploadComponent } from './offering-data-form/image-upload/image-upload.component';
import { ImageCardComponent } from './offering-data-form/image-upload/image-card/image-card.component';
import { ErrorPageComponent } from '../components/error-pages/error-page/error-page.component';
import { LoadingPageComponent } from '../components/loading-page/loading-page.component';
import { CalendarAvailabilityComponent } from './offering-details/calendar-availability/calendar-availability.component';
import { ChatListComponentComponent } from './my-offerings/chat-list-component/chat-list-component.component';
import { IsProviderGuard } from '../guards/is-provider/is-provider.guard';
import { IsLoggedGuard } from '../guards/is-logged-in/is-logged.guard';
import { NgxSkeletonLoaderModule } from 'ngx-skeleton-loader';
import { AddToEventModalComponent } from './offering-details/contact-information/add-to-event-modal/add-to-event-modal.component';
import { IdMustBeNumberGuard } from '../guards/idMustBeNumber/id-must-be-number.guard';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NotFoundPageComponent } from '../components/error-pages/not-found-page/not-found-page.component';


const routes: Routes = [
  {
    path: 'services', component: OfferingExplorerComponent,
    title: $localize`PartyPicker | Explore`
  },
  {
    path: 'services/:id',
    component: OfferingDetailsComponent,
    data: { idParams: ['id'] },
    canActivate: [IdMustBeNumberGuard],
    title: $localize`PartyPicker | Details`
  },
  {
    path: 'my-services/:id/edit',
    component: EditOfferingComponent,
    data: { idParams: ['id'] },
    canActivate: [IsLoggedGuard, IsProviderGuard, IdMustBeNumberGuard],
    title: $localize`PartyPicker | Edit`
  },
  {
    path: 'my-services',
    component: MyOfferingsComponent,
    canActivate: [IsLoggedGuard, IsProviderGuard],
    title: $localize`PartyPicker | My Services`
  },
  {
    path: 'my-services/create',
    component: OfferingCreateComponent,
    canActivate: [IsLoggedGuard, IsProviderGuard],
    title: $localize`PartyPicker | Create`
  },
];


@NgModule({
  declarations: [
    OfferingCreateComponent,
    OfferingDataFormComponent,
    OfferingCardComponent,
    HeaderComponent,
    OfferingDetailsImagesComponent,
    ContactInformationComponent,
    OfferingDetailsComponent,
    OfferingListComponent,
    OfferingFilterComponent,
    OfferingExplorerComponent,
    MyOfferingsComponent,
    EditOfferingComponent,
    DeleteOfferingDialogComponent,
    ReviewsComponent,
    OfferingsChatComponent,
    CalendarComponent,
    ImageUploadComponent,
    ImageCardComponent,
    CalendarAvailabilityComponent,
    ChatListComponentComponent,
    AddToEventModalComponent
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    CalendarModule,
    CalendarModule.forRoot({
      provide: DateAdapter,
      useFactory: adapterFactory,
    }),
    MatIconModule,
    MatCardModule,
    MatGridListModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatSliderModule,
    MatButtonModule,
    MatCheckboxModule,
    MatButtonToggleModule,
    MatTabsModule,
    MatRadioModule,
    MatBadgeModule,
    MatDialogModule,
    MatDividerModule,
    MatTooltipModule,
    MatListModule,
    MatToolbarModule,
    MatButtonModule,
    NgbPopoverModule,
    FormsModule,
    ReactiveFormsModule,
    NgbRating,
    UtilsModule,
    ErrorPageComponent,
    LoadingPageComponent,
    NgxSkeletonLoaderModule.forRoot({ animation: 'pulse', loadingText: 'This item is loading...' }),
  ],
  exports: [
    OfferingCardComponent,
    OfferingListComponent
  ]
})
export class OfferingModule { }
