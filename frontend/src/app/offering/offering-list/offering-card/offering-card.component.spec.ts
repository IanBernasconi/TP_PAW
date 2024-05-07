import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfferingCardComponent } from './offering-card.component';
import { NgxSkeletonLoaderModule } from 'ngx-skeleton-loader';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('OfferingCardComponent', () => {
  let component: OfferingCardComponent;
  let fixture: ComponentFixture<OfferingCardComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [NgxSkeletonLoaderModule, HttpClientTestingModule],
      declarations: [OfferingCardComponent]
    });
    fixture = TestBed.createComponent(OfferingCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
