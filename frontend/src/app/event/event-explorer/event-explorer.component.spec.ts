import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EventExplorerComponent } from './event-explorer.component';
import { provideMockStore } from '@ngrx/store/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { MatTabsModule } from '@angular/material/tabs';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('EventExplorerComponent', () => {
  let component: EventExplorerComponent;
  let fixture: ComponentFixture<EventExplorerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, MatTabsModule, BrowserAnimationsModule],
      declarations: [EventExplorerComponent],
      providers: [provideMockStore({})]
    });
    fixture = TestBed.createComponent(EventExplorerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
