import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OfferingDataFormComponent } from './offering-data-form.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { HttpClientModule, provideHttpClient } from '@angular/common/http';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { ImageUploadComponent } from './image-upload/image-upload.component';
import { MatToolbarModule } from '@angular/material/toolbar';
import { ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('OfferingDataFormComponent', () => {
  let component: OfferingDataFormComponent;
  let fixture: ComponentFixture<OfferingDataFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule,MatFormFieldModule, MatSelectModule, MatCheckboxModule, MatToolbarModule, ReactiveFormsModule, MatInputModule, BrowserAnimationsModule],
      declarations: [OfferingDataFormComponent, ImageUploadComponent],
    });
    fixture = TestBed.createComponent(OfferingDataFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
