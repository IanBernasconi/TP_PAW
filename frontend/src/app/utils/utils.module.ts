import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PaginatorComponent } from './paginator/paginator.component';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatSliderModule } from '@angular/material/slider';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatRadioModule } from '@angular/material/radio';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { ChatSelectorCardComponent } from './chat/chat-selector-card/chat-selector-card.component';
import { MessagesContainerComponent } from './chat/messages-container/messages-container.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { ScrollDirective } from '../../shared/directives/scroll.directive';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ConnectFormDirective } from 'src/shared/directives/connect-form.directive';
import { GoBackButtonComponent } from './go-back-button/go-back-button.component';



@NgModule({
  declarations: [
    PaginatorComponent,
    ChatSelectorCardComponent,
    MessagesContainerComponent,
    ScrollDirective,
    ConnectFormDirective,
    GoBackButtonComponent
  ],
  imports: [
    CommonModule,
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
    MatRadioModule,
    MatDialogModule,
    MatDividerModule,
    MatListModule,
    FormsModule,
    ReactiveFormsModule,
    MatProgressSpinnerModule,
    InfiniteScrollModule
  ],
  exports: [
    PaginatorComponent,
    ChatSelectorCardComponent,
    MessagesContainerComponent,
    ScrollDirective,
    ConnectFormDirective,
    GoBackButtonComponent
  ]
})
export class UtilsModule { }
