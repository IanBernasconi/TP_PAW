import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { OfferingExplorerComponent } from './offering-explorer.component';
import { OfferingFilterComponent } from './offering-filter/offering-filter.component';
import { OfferingListComponent } from '../offering-list/offering-list.component';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule, MatLabel } from '@angular/material/form-field';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSelectModule } from '@angular/material/select';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('OfferingExplorerComponent', () => {
  let component: OfferingExplorerComponent;
  let fixture: ComponentFixture<OfferingExplorerComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        MatIconModule,
        MatFormFieldModule,
        ReactiveFormsModule,
        RouterTestingModule,
        MatTabsModule,
        MatCheckboxModule,
        MatSelectModule,
        BrowserAnimationsModule,
        HttpClientTestingModule
      ],
      declarations: [OfferingExplorerComponent, OfferingFilterComponent, OfferingListComponent],
      providers: [provideMockStore({})]
    });
    fixture = TestBed.createComponent(OfferingExplorerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
