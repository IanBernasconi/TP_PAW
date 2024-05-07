import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { Store } from "@ngrx/store";
import {
  ToastMessages,
  ToastService,
} from "src/app/services/toastService/toast.service";
import { UserService } from "src/app/services/userService/user.service";
import { UserUpdateActions, UserVerifyActions } from "src/app/store/user/user.actions";
import { selectUserFinishUpdate, selectUserUpdateError } from "src/app/store/user/user.selector";
import { BaseComponent } from "src/app/utils/base-component.component";

@Component({
  selector: "app-verify",
  templateUrl: "./verify.component.html",
  styleUrls: ["./verify.component.scss"],
})
export class VerifyComponent extends BaseComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    toastService: ToastService,
    store: Store
  ) {
    super(store, toastService);
  }

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap?.get("token");
    if (token) {


      this.subscribeToSuccess(selectUserFinishUpdate, ToastMessages.register.verify.success, UserUpdateActions.resetUpdate(), () => this.router.navigate(["services"]));
      this.subscribeToError(selectUserUpdateError, ToastMessages.register.verify.error, UserUpdateActions.resetUpdate(), () => this.router.navigate(["services"]));

      this.store.dispatch(UserVerifyActions.verify({ token }));
    } else {
      this.toastService.error(ToastMessages.token.error);
      this.router.navigate(["services"]);
    }
  }
}
