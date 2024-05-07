import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Offering } from 'src/shared/models/offering.model';

export class DeleteOfferingDialogData {
  offering: Offering;

  constructor(offering: Offering) {
    this.offering = offering;
  }
}


@Component({
  selector: 'app-delete-offering-dialog',
  templateUrl: './delete-offering-dialog.component.html',
  styleUrls: ['./delete-offering-dialog.component.scss']
})
export class DeleteOfferingDialogComponent {

  constructor(public dialogRef: MatDialogRef<DeleteOfferingDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DeleteOfferingDialogData) { }

}
