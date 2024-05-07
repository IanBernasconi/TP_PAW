import { ComponentFixture, TestBed } from "@angular/core/testing";
import { AnswerGuestInvitationComponent } from "./answer-guest-invitation.component";
import { Store, StoreModule } from "@ngrx/store";
import { provideMockStore } from "@ngrx/store/testing";
import { ActivatedRoute } from "@angular/router";
import { HttpClientTestingModule } from "@angular/common/http/testing";

describe("AnswertGuestInvitationComponent", () => {
  let component: AnswerGuestInvitationComponent;
  let fixture: ComponentFixture<AnswerGuestInvitationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        StoreModule.forRoot({}),
        HttpClientTestingModule,
      ],
      declarations: [AnswerGuestInvitationComponent],
      providers: [
        provideMockStore({ initialState: {} }),
        { provide: ActivatedRoute, useValue: { snapshot: {} } }
      ],
    });
    fixture = TestBed.createComponent(AnswerGuestInvitationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
