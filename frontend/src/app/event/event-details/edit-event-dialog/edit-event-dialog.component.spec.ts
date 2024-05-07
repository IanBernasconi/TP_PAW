import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditEventDialogComponent } from './edit-event-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { DateAdapter, MAT_DATE_FORMATS, MatNativeDateModule } from '@angular/material/core';
import { EventDataFormComponent } from '../../event-data-form/event-data-form.component';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('EditEventDialogComponent', () => {
  let component: EditEventDialogComponent;
  let fixture: ComponentFixture<EditEventDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MatFormFieldModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatIconModule,
        MatDialogModule,
        MatSelectModule,
        ReactiveFormsModule,
        MatInputModule,
        HttpClientTestingModule
      ],
      declarations: [EditEventDialogComponent, EventDataFormComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        {
          provide: MAT_DIALOG_DATA, useValue: {
            event: {
              name: "test",
              description: "test",
              date: new Date(),
              numberOfGuests: 1,
              district: "test"
            },

          }
        },
        { provide: MatDatepickerModule, useClass: MatDatepickerModule },
        { provide: MAT_DATE_FORMATS, useValue: {} }
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(EditEventDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
