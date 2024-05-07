import { Component } from "@angular/core";
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { UserService } from "src/app/services/userService/user.service";
import { UniqueEmailValidator } from "../register/UniqueEmailValidator";
import { map } from "rxjs";

@Component({
  selector: "forgot-password",
  templateUrl: "./forgot-password.component.html",
  styleUrls: ["./forgot-password.component.scss"],
})
export class ForgotPasswordComponent {
  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private uniqueEmailValidator: UniqueEmailValidator,
  ) { }

  resetForm = new FormGroup({
    email: new FormControl("", {
      validators: [Validators.required, Validators.email],
      asyncValidators: (control: AbstractControl) => {
        return this.uniqueEmailValidator.validate(control).pipe(
          map(result => result ? null : { uniqueEmail: true })
        );
      },
      updateOn: "blur",
    }),
  });

  sendingResetPasswordEmailComplete: boolean = false;

  resetPassword() {
    const email = this.resetForm.get("email")?.value as string;

    this.userService.requestPasswordReset(email).subscribe({
      next: () => (this.sendingResetPasswordEmailComplete = true),
    });
  }

  get resetEmail() {
    return this.resetForm.get("email");
  }
}
