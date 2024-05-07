import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContactProviderDialogComponent } from './contact-provider-dialog.component';
import { MAT_DIALOG_DATA, MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';

describe('ContactProviderDialogComponent', () => {
  let component: ContactProviderDialogComponent;
  let fixture: ComponentFixture<ContactProviderDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MatDialogModule],
      declarations: [ContactProviderDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: {} },
        { provide: MatDialog, useValue: {} }
      ]
    });
    fixture = TestBed.createComponent(ContactProviderDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
