import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteOfferingDialogComponent } from './delete-offering-dialog.component';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';

describe('DeleteOfferingDialogComponent', () => {
  let component: DeleteOfferingDialogComponent;
  let fixture: ComponentFixture<DeleteOfferingDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MatDialogModule],
      declarations: [DeleteOfferingDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: {} }
      ]
    });
    fixture = TestBed.createComponent(DeleteOfferingDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
