import { ComponentFixture, TestBed } from '@angular/core/testing'
import { ResetPasswordComponent } from './reset-password.component';
import { ActivatedRoute } from '@angular/router';
import { StoreModule } from '@ngrx/store';
import { provideMockStore } from '@ngrx/store/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { ReactiveFormsModule } from '@angular/forms';

describe('ResetPasswordComponent', () => {
  let component: ResetPasswordComponent;
  let fixture: ComponentFixture<ResetPasswordComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        StoreModule.forRoot({}),
        HttpClientTestingModule,
        MatFormFieldModule,
        MatIconModule,
        ReactiveFormsModule
      ],
      declarations: [ResetPasswordComponent],
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: {} } },
        provideMockStore({}),
      ]
    });
    fixture = TestBed.createComponent(ResetPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
