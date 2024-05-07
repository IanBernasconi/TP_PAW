import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventsFilterComponent } from './events-filter.component';
import { FormsModule } from '@angular/forms';

describe('EventsFilterComponent', () => {
  let component: EventsFilterComponent;
  let fixture: ComponentFixture<EventsFilterComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [EventsFilterComponent]
    });
    fixture = TestBed.createComponent(EventsFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
