import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChatSelectorCardComponent } from './chat-selector-card.component';

describe('ChatSelectorCardComponent', () => {
  let component: ChatSelectorCardComponent;
  let fixture: ComponentFixture<ChatSelectorCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ChatSelectorCardComponent]
    });
    fixture = TestBed.createComponent(ChatSelectorCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
