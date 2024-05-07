import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { errorMessages } from 'src/shared/models/utils.model';

@Component({
  selector: 'error-page',
  templateUrl: './error-page.component.html',
  styleUrls: ['./error-page.component.scss'],
  imports: [CommonModule],
  standalone: true
})
export class ErrorPageComponent {
  @Input({ required: true }) errorCode!: number;
  @Input({ required: true }) errorDescription!: string;
  @Input({ required: true }) actionMessage!: string;
  @Output() onAction = new EventEmitter();

  errorMessages = errorMessages;


  onActionClick() {
    this.onAction.emit();
  }

}
