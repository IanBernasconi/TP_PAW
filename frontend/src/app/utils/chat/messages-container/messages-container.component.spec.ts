import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MessagesContainerComponent } from './messages-container.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

describe('MessagesContainerComponent', () => {
  let component: MessagesContainerComponent;
  let fixture: ComponentFixture<MessagesContainerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, InfiniteScrollModule, MatProgressSpinnerModule],
      declarations: [MessagesContainerComponent]
    });
    fixture = TestBed.createComponent(MessagesContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
