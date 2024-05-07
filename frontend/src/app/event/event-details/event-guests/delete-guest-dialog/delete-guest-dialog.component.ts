import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Guest } from 'src/shared/models/guest.model';

@Component({
  selector: 'delete-guest-dialog',
  templateUrl: './delete-guest-dialog.component.html',
  styleUrls: ['./delete-guest-dialog.component.scss']
})


export class DeleteGuestDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<DeleteGuestDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Guest) { }

  ngOnInit(): void {
  }

}
