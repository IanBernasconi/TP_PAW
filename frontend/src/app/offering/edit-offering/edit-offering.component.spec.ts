import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditOfferingComponent } from './edit-offering.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { provideMockStore } from '@ngrx/store/testing';
import { GoBackButtonComponent } from 'src/app/utils/go-back-button/go-back-button.component';
import { MatIconModule } from '@angular/material/icon';

describe('EditOfferingComponent', () => {
  let component: EditOfferingComponent;
  let fixture: ComponentFixture<EditOfferingComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        MatIconModule
      ],
      declarations: [
        EditOfferingComponent,
        GoBackButtonComponent
      ],
      providers: [provideMockStore({})]
    }).compileComponents();
    fixture = TestBed.createComponent(EditOfferingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
