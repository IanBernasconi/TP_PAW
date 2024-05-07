import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProfilePageComponent } from './profile-page.component';
import { provideMockStore } from '@ngrx/store/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ProfilePageComponent', () => {
  let component: ProfilePageComponent;
  let fixture: ComponentFixture<ProfilePageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ProfilePageComponent],
      providers: [provideMockStore({
        initialState: {
          user: {}
        }
      })]
    });
    fixture = TestBed.createComponent(ProfilePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('#ngOnInit() should initialize profileForm', () => {
    component.ngOnInit();
    expect(component.profileForm).toBeTruthy();
  });

  it('#toggleEditMode() should toggle editMode', () => {
    component.editMode = false;
    component.toggleEditMode();
    expect(component.editMode).toBe(true);
    component.toggleEditMode();
    expect(component.editMode).toBe(false);
  });

});
