import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfferingCreateComponent } from './offering-create.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { GoBackButtonComponent } from 'src/app/utils/go-back-button/go-back-button.component';
import { MatIconModule } from '@angular/material/icon';

describe('OfferingCreateComponent', () => {
  let component: OfferingCreateComponent;
  let fixture: ComponentFixture<OfferingCreateComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        MatIconModule
      ],
      declarations: [OfferingCreateComponent, GoBackButtonComponent],
      providers: [
        provideMockStore({}),
      ]
    }).compileComponents();
    fixture = TestBed.createComponent(OfferingCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
