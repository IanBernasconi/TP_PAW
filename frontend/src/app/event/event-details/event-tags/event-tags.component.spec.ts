import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventTagsComponent } from './event-tags.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('EventTagsComponent', () => {
  let component: EventTagsComponent;
  let fixture: ComponentFixture<EventTagsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [EventTagsComponent]
    });
    fixture = TestBed.createComponent(EventTagsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
