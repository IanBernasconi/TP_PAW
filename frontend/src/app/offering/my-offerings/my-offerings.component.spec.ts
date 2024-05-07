import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyOfferingsComponent } from './my-offerings.component';
import { provideMockStore } from '@ngrx/store/testing';
import { RouterTestingModule } from '@angular/router/testing';

describe('MyOfferingsComponent', () => {
  let component: MyOfferingsComponent;
  let fixture: ComponentFixture<MyOfferingsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [MyOfferingsComponent],
      providers: [provideMockStore({})]
    });
    fixture = TestBed.createComponent(MyOfferingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
