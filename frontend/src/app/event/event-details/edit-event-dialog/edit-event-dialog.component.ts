import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Event, EventToCreate } from 'src/shared/models/event.model';
import { User } from 'src/shared/models/user.model';

export class EditEventDialogData {
  event: EventToCreate;
  loggedUser: User;

  constructor(event: Event, loggedUser: User) {
    this.event = event;
    this.loggedUser = loggedUser;
  }
}


@Component({
  selector: 'edit-event-dialog',
  templateUrl: './edit-event-dialog.component.html',
  styleUrls: ['./edit-event-dialog.component.scss']
})
export class EditEventDialogComponent implements OnInit {


  isFormValid: boolean = true;

  constructor(public dialogRef: MatDialogRef<EditEventDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: EditEventDialogData) { }


  ngOnInit(): void {
  }

  updateEvent(event: EventToCreate) {
    this.data.event = event;
  }

  toggleValidForm(valid: boolean) {
    this.isFormValid = valid;
  }

}
