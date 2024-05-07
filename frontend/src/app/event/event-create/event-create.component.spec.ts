import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EventCreateComponent } from './event-create.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { UtilsModule } from 'src/app/utils/utils.module';
import { EventModule } from '../event.module';
import { EventDataFormComponent } from '../event-data-form/event-data-form.component';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MAT_DATE_FORMATS, MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { DateAdapter } from 'angular-calendar';
import { MatSelectModule } from '@angular/material/select';
import { ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute } from '@angular/router';

describe('EventCreateComponent', () => {
  let component: EventCreateComponent;
  let fixture: ComponentFixture<EventCreateComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EventCreateComponent, EventDataFormComponent],
      imports: [
        HttpClientTestingModule,
        UtilsModule,
        MatFormFieldModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatSelectModule,
        ReactiveFormsModule,
        MatInputModule,
        BrowserAnimationsModule
      ],
      providers: [
        provideMockStore({}),
        { provide: DateAdapter, useClass: MatDatepickerModule },
        { provide: MAT_DATE_FORMATS, useValue: {} },
        { provide: ActivatedRoute, useValue: { snapshot: {} } }
      ]
    });
    fixture = TestBed.createComponent(EventCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
