import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AddToEventModalComponent } from './add-to-event-modal.component';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';

describe('AddToEventModalComponent', () => {
  let component: AddToEventModalComponent;
  let fixture: ComponentFixture<AddToEventModalComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [MatDialogModule],
      declarations: [AddToEventModalComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: {} }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AddToEventModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
