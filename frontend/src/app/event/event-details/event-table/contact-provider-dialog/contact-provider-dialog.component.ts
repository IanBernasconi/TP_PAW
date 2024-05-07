import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { RelationInfo } from 'src/shared/models/event.model';

export class ContactProviderDialogData {
  relationInfo: RelationInfo;
  constructor(relationInfo: RelationInfo) {
    this.relationInfo = relationInfo;
  }
}


@Component({
  selector: 'contact-provider-dialog',
  templateUrl: './contact-provider-dialog.component.html',
  styleUrls: ['./contact-provider-dialog.component.scss']
})
export class ContactProviderDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<ContactProviderDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ContactProviderDialogData) { }

  ngOnInit(): void {
  }

}
