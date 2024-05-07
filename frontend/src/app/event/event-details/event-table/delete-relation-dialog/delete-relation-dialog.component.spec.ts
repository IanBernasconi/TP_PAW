import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeleteRelationDialogComponent } from './delete-relation-dialog.component';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';

describe('DeleteRelationDialogComponent', () => {
  let component: DeleteRelationDialogComponent;
  let fixture: ComponentFixture<DeleteRelationDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MatDialogModule],
      declarations: [DeleteRelationDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: {} }
      ]
    });
    fixture = TestBed.createComponent(DeleteRelationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
