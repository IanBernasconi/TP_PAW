import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventChatComponent } from './event-chat.component';
import { MatDividerModule } from '@angular/material/divider';
import { RouterTestingModule } from '@angular/router/testing';

describe('EventChatComponent', () => {
  let component: EventChatComponent;
  let fixture: ComponentFixture<EventChatComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MatDividerModule, RouterTestingModule],
      declarations: [EventChatComponent]
    });
    fixture = TestBed.createComponent(EventChatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
