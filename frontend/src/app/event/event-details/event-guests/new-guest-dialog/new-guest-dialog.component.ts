import { Component, Inject, OnInit } from '@angular/core';
import { AbstractControl, FormControl, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'new-guest-dialog',
  templateUrl: './new-guest-dialog.component.html',
  styleUrls: ['./new-guest-dialog.component.scss']
})
export class NewGuestDialogComponent implements OnInit {

  checkDuplicateEmails: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    if (this.data && Array.isArray(this.data)) {
      return this.data.includes(control.value) ? { duplicateEmail: true } : null;
    }
    return null;
  }

  emailFormControl = new FormControl('', [
    Validators.required,
    Validators.email,
    this.checkDuplicateEmails
  ]);

  constructor(public dialogRef: MatDialogRef<NewGuestDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: string[]) { }

  ngOnInit(): void {
  }

}
