import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfferingDetailsComponent } from './offering-details.component';
import { RouterTestingModule } from '@angular/router/testing';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { provideMockStore } from '@ngrx/store/testing';

describe('OfferingDetailsComponent', () => {
  let component: OfferingDetailsComponent;
  let fixture: ComponentFixture<OfferingDetailsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      declarations: [OfferingDetailsComponent],
      providers: [
        provideMockStore({}),
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: {} },
        { provide: MatDialog, useValue: {} }
      ]
    });
    fixture = TestBed.createComponent(OfferingDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
