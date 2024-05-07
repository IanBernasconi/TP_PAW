import { NgModule } from "@angular/core";
import {CommonModule, NgOptimizedImage} from "@angular/common";
import { EventExplorerComponent } from "./event-explorer/event-explorer.component";
import { RouterModule, Routes } from "@angular/router";
import { MatIconModule } from "@angular/material/icon";
import { MatCardModule } from "@angular/material/card";
import { MatGridListModule } from "@angular/material/grid-list";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatSelectModule } from "@angular/material/select";
import { MatInputModule } from "@angular/material/input";
import { MatSliderModule } from "@angular/material/slider";
import { MatButtonModule } from "@angular/material/button";
import { MatButtonToggleModule } from "@angular/material/button-toggle";
import { MatRadioModule } from "@angular/material/radio";
import { MatTabsModule } from "@angular/material/tabs";
import { MatDialogModule } from "@angular/material/dialog";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatNativeDateModule } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";

import { EventsFilterComponent } from "./event-explorer/events-filter/events-filter.component";
import { EventsListComponent } from "./event-explorer/events-list/events-list.component";
import { EventItemComponent } from "./event-explorer/event-item/event-item.component";
import { EventDetailsComponent } from "./event-details/event-details.component";
import { EventTableComponent } from "./event-details/event-table/event-table.component";
import { EventGuestsComponent } from "./event-details/event-guests/event-guests.component";
import { EventChatComponent } from "./event-details/event-chat/event-chat.component";
import { EventTagsComponent } from "./event-details/event-tags/event-tags.component";
import { DeleteRelationDialogComponent } from "./event-details/event-table/delete-relation-dialog/delete-relation-dialog.component";
import { ContactProviderDialogComponent } from "./event-details/event-table/contact-provider-dialog/contact-provider-dialog.component";
import { DeleteEventDialogComponent } from "./event-details/delete-event-dialog/delete-event-dialog.component";
import { EditEventDialogComponent } from "./event-details/edit-event-dialog/edit-event-dialog.component";
import { ReviewDialogComponent } from "./event-details/event-table/review-dialog/review-dialog.component";
import { NgbModule } from "@ng-bootstrap/ng-bootstrap";
import { UtilsModule } from "../utils/utils.module";
import { NewGuestDialogComponent } from "./event-details/event-guests/new-guest-dialog/new-guest-dialog.component";
import { MatDividerModule } from "@angular/material/divider";
import { DeleteGuestDialogComponent } from "./event-details/event-guests/delete-guest-dialog/delete-guest-dialog.component";
import { ErrorPageComponent } from "../components/error-pages/error-page/error-page.component";
import { LoadingPageComponent } from "../components/loading-page/loading-page.component";
import { EventCreateComponent } from "./event-create/event-create.component";
import { EventDataFormComponent } from "./event-data-form/event-data-form.component";
import { NgxSkeletonLoaderModule } from "ngx-skeleton-loader";
import { IsLoggedGuard } from "../guards/is-logged-in/is-logged.guard";
import { OfferingCardComponent } from "../offering/offering-list/offering-card/offering-card.component";
import { OfferingModule } from "../offering/offering.module";
import { AnswerGuestInvitationComponent } from "./answer-guest-invitation/answer-guest-invitation.component";
import { IdMustBeNumberGuard } from "../guards/idMustBeNumber/id-must-be-number.guard";
import { NotFoundPageComponent } from "../components/error-pages/not-found-page/not-found-page.component";

const routes: Routes = [
  {
    path: "events",
    component: EventExplorerComponent,
    canActivate: [IsLoggedGuard],
    title: $localize`PartyPicker | Events`
  },
  {
    path: "events/create",
    component: EventCreateComponent,
    canActivate: [IsLoggedGuard],
    title: $localize`PartyPicker | Create Event`
  },
  {
    path: "events/:id",
    component: EventDetailsComponent,
    data: { idParams: ["id"] },
    canActivate: [IsLoggedGuard, IdMustBeNumberGuard],
    title: $localize`PartyPicker | Details`
  },
  {
    path: "events/:eventId/guests/invite/:guestId/accept",
    component: AnswerGuestInvitationComponent,
    data: { idParams: ["eventId", "guestId"] },
    canActivate: [IdMustBeNumberGuard]
  },
  {
    path: "events/:eventId/guests/invite/:guestId/reject",
    component: AnswerGuestInvitationComponent,
    data: { idParams: ["eventId", "guestId"] },
    canActivate: [IdMustBeNumberGuard]
  }
];

@NgModule({
  declarations: [
    EventExplorerComponent,
    EventsFilterComponent,
    EventsListComponent,
    EventItemComponent,
    EventDetailsComponent,
    EventTableComponent,
    EventGuestsComponent,
    EventChatComponent,
    EventTagsComponent,
    DeleteRelationDialogComponent,
    ContactProviderDialogComponent,
    DeleteEventDialogComponent,
    EditEventDialogComponent,
    ReviewDialogComponent,
    NewGuestDialogComponent,
    DeleteGuestDialogComponent,
    EventCreateComponent,
    EventDataFormComponent,
    AnswerGuestInvitationComponent,
  ],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        MatIconModule,
        MatCardModule,
        MatGridListModule,
        MatFormFieldModule,
        MatSelectModule,
        MatInputModule,
        MatSliderModule,
        MatButtonModule,
        MatButtonToggleModule,
        MatRadioModule,
        MatTabsModule,
        MatDialogModule,
        MatNativeDateModule,
        MatDatepickerModule,
        MatDividerModule,
        FormsModule,
        ReactiveFormsModule,
        NgbModule,
        UtilsModule,
        ErrorPageComponent,
        LoadingPageComponent,
        OfferingModule,
        NgxSkeletonLoaderModule.forRoot({
            animation: "pulse",
            loadingText: "This item is loading...",
        }),
        NgOptimizedImage,
    ],
})
export class EventModule { }
