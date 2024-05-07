import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfferingListComponent } from './offering-list.component';

describe('OfferingListComponent', () => {
  let component: OfferingListComponent;
  let fixture: ComponentFixture<OfferingListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OfferingListComponent]
    });
    fixture = TestBed.createComponent(OfferingListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
