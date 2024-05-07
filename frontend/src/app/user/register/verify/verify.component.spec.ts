import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VerifyComponent } from './verify.component';
import { ActivatedRoute } from '@angular/router';
import { StoreModule } from '@ngrx/store';
import { provideMockStore } from '@ngrx/store/testing';

describe('VerifyComponent', () => {
  let component: VerifyComponent;
  let fixture: ComponentFixture<VerifyComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        StoreModule.forRoot({}),
      ],
      declarations: [VerifyComponent],
      providers: [
        { provide: ActivatedRoute, useValue: { snapshot: {} } },
        provideMockStore({}),
      ]
    });
    fixture = TestBed.createComponent(VerifyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
