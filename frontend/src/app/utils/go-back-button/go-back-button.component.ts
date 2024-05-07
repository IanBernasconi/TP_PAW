import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'go-back-button',
  templateUrl: './go-back-button.component.html',
  styleUrls: ['./go-back-button.component.scss']
})
export class GoBackButtonComponent {
  @Output() goBack = new EventEmitter<void>();

}
