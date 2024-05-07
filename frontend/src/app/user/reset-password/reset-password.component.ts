import { Component, OnInit } from "@angular/core";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import {
  ToastMessages,
  ToastService,
} from "src/app/services/toastService/toast.service";
import { checkPasswords } from "../register/register.component";
import { UserPasswordUpdate } from "src/shared/models/user.model";
import { UserService } from "src/app/services/userService/user.service";
import { BaseComponent } from "src/app/utils/base-component.component";
import { Store } from "@ngrx/store";
import { selectUserFinishUpdate, selectUserUpdateError } from "src/app/store/user/user.selector";
import { UserChangePasswordActions, UserUpdateActions } from "src/app/store/user/user.actions";

@Component({
  selector: "app-reset-password",
  templateUrl: "./reset-password.component.html",
  styleUrls: ["./reset-password.component.scss"],
})
export class ResetPasswordComponent extends BaseComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    toastService: ToastService,
    store: Store,
    private userService: UserService,
    private router: Router,
  ) {
    super(store, toastService);
  }

  token!: string;

  hidePassword = true;
  hideConfirmPassword = true;

  resetPasswordForm = new FormGroup(
    {
      password: new FormControl("", [
        Validators.required,
        Validators.minLength(8),
      ]),
      confirmPassword: new FormControl("", [
        Validators.required,
        Validators.minLength(8),
      ]),
    },
    { validators: checkPasswords }
  );

  ngOnInit(): void {
    let token = this.route.snapshot.queryParamMap?.get("token");
    if (token) {
      this.token = token;
    } else {
      this.toastService.error(ToastMessages.token.error);
      this.router.navigate(["services"]);
    }
  }

  resetPassword() {
    const password = this.resetPasswordForm.get("password")?.value as string;

    const user: UserPasswordUpdate = {
      password: password,
    };

    this.subscribeToSuccess(selectUserFinishUpdate, ToastMessages.password.reset.success, UserUpdateActions.resetUpdate(), () => this.router.navigate(["services"]));
    this.subscribeToError(selectUserUpdateError, ToastMessages.password.reset.error, UserUpdateActions.resetUpdate(), () => this.router.navigate(["user", "login"]));


    this.store.dispatch(UserChangePasswordActions.changePassword({ newPassword: password, token: this.token }));

  }

  get password() {
    return this.resetPasswordForm.get("password");
  }
  get confirmPassword() {
    return this.resetPasswordForm.get("confirmPassword");
  }
}
