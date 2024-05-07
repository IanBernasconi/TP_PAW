import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OfferingsChatComponent } from './offerings-chat.component';
import { provideMockStore } from '@ngrx/store/testing';
import { UtilsModule } from 'src/app/utils/utils.module';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('OfferingsChatComponent', () => {
  let component: OfferingsChatComponent;
  let fixture: ComponentFixture<OfferingsChatComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [UtilsModule, MatFormFieldModule, MatSelectModule, ReactiveFormsModule, BrowserAnimationsModule, RouterTestingModule, HttpClientTestingModule],
      declarations: [OfferingsChatComponent],
      providers: [provideMockStore({})]
    });
    fixture = TestBed.createComponent(OfferingsChatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
