import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GoBackButtonComponent } from './go-back-button.component';
import { MatIconModule } from '@angular/material/icon';

describe('GoBackButtonComponent', () => {
  let component: GoBackButtonComponent;
  let fixture: ComponentFixture<GoBackButtonComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MatIconModule],
      declarations: [GoBackButtonComponent]
    });
    fixture = TestBed.createComponent(GoBackButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
