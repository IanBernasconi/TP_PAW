import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfferingFilterComponent } from './offering-filter.component';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ReactiveFormsModule } from '@angular/forms';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSelectModule } from '@angular/material/select';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('OfferingFilterComponent', () => {
  let component: OfferingFilterComponent;
  let fixture: ComponentFixture<OfferingFilterComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        MatIconModule,
        MatFormFieldModule,
        ReactiveFormsModule,
        MatTabsModule,
        MatCheckboxModule,
        MatSelectModule,
        BrowserAnimationsModule,
        HttpClientTestingModule
      ],
      declarations: [OfferingFilterComponent]
    });
    fixture = TestBed.createComponent(OfferingFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
