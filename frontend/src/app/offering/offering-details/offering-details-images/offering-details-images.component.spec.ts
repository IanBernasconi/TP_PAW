import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfferingDetailsImagesComponent } from './offering-details-images.component';

describe('OfferingDetailsImagesComponent', () => {
  let component: OfferingDetailsImagesComponent;
  let fixture: ComponentFixture<OfferingDetailsImagesComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OfferingDetailsImagesComponent]
    });
    fixture = TestBed.createComponent(OfferingDetailsImagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
