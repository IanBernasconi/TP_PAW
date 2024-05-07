import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ContactInformationComponent } from './contact-information.component';
import { NgxSkeletonLoaderModule } from 'ngx-skeleton-loader';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDialog } from '@angular/material/dialog';

describe('ContactInformationComponent', () => {
  let component: ContactInformationComponent;
  let fixture: ComponentFixture<ContactInformationComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [NgxSkeletonLoaderModule, MatFormFieldModule],
      declarations: [ContactInformationComponent],
      providers: [
        { provide: MatDialog, useValue: {} }
      ]
    });
    fixture = TestBed.createComponent(ContactInformationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
