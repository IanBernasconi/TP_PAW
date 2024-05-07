import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Event } from 'src/shared/models/event.model';

export class DeleteEventDialogData {
  event: Event;

  constructor(event: Event) {
    this.event = event;
  }
}



@Component({
  selector: 'delete-event-dialog',
  templateUrl: './delete-event-dialog.component.html',
  styleUrls: ['./delete-event-dialog.component.scss']
})
export class DeleteEventDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<DeleteEventDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DeleteEventDialogData) { }

  ngOnInit(): void {
  }

}
