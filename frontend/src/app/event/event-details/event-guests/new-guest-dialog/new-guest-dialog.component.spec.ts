import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewGuestDialogComponent } from './new-guest-dialog.component';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('NewGuestDialogComponent', () => {
  let component: NewGuestDialogComponent;
  let fixture: ComponentFixture<NewGuestDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MatFormFieldModule, MatDialogModule, FormsModule, MatInputModule, BrowserAnimationsModule],
      declarations: [NewGuestDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: {} },
      ]
    });
    fixture = TestBed.createComponent(NewGuestDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
