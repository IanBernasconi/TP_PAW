import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventDataFormComponent } from './event-data-form.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { DateAdapter } from 'angular-calendar';
import { MAT_DATE_FORMATS, MatNativeDateModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('EventDataFormComponent', () => {
  let component: EventDataFormComponent;
  let fixture: ComponentFixture<EventDataFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EventDataFormComponent],
      imports: [HttpClientTestingModule, MatFormFieldModule, MatDatepickerModule, MatNativeDateModule, MatSelectModule, ReactiveFormsModule, MatInputModule, BrowserAnimationsModule],
      providers: [
        { provide: DateAdapter, useClass: MatDatepickerModule },
        { provide: MAT_DATE_FORMATS, useValue: {} }
      ]
    });
    fixture = TestBed.createComponent(EventDataFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
