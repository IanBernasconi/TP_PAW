import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChatListComponentComponent } from './chat-list-component.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('ChatListComponentComponent', () => {
  let component: ChatListComponentComponent;
  let fixture: ComponentFixture<ChatListComponentComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ChatListComponentComponent]
    });
    fixture = TestBed.createComponent(ChatListComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
