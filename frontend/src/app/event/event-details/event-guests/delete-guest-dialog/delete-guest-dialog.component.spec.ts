import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteGuestDialogComponent } from './delete-guest-dialog.component';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';

describe('DeleteGuestDialogComponent', () => {
  let component: DeleteGuestDialogComponent;
  let fixture: ComponentFixture<DeleteGuestDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MatDialogModule],
      declarations: [DeleteGuestDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: {} }
      ]
    });
    fixture = TestBed.createComponent(DeleteGuestDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
