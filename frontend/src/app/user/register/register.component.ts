import { Component } from "@angular/core";
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from "@angular/forms";
import { UserService } from "src/app/services/userService/user.service";
import { UserUpdate } from "src/shared/models/user.model";
import { UniqueEmailValidator } from "./UniqueEmailValidator";
import { Router } from "@angular/router";

export const checkPasswords: ValidatorFn = (
  control: AbstractControl
): ValidationErrors | null => {
  const password = control.get("password")?.value;
  const confirmPassword = control.get("confirmPassword")?.value;
  const retVal =
    password === confirmPassword ? null : { passwordMismatch: true };

  if (retVal) {
    control.get("confirmPassword")?.setErrors(retVal);
  } else {
    control.get("confirmPassword")?.setErrors(null);
  }
  return password === confirmPassword ? null : { passwordMismatch: true };
};

@Component({
  selector: "app-register",
  templateUrl: "./register.component.html",
  styleUrls: ["./register.component.scss"],
})
export class RegisterComponent {
  constructor(
    private userService: UserService,
    private uniqueEmailValidator: UniqueEmailValidator,
    private router: Router
  ) {}
  registerForm = new FormGroup(
    {
      email: new FormControl("", {
        validators: [Validators.required, Validators.email],
        asyncValidators: this.uniqueEmailValidator.validate.bind(
          this.uniqueEmailValidator
        ),
        updateOn: "blur",
      }),
      name: new FormControl("", [
        Validators.required,
        Validators.minLength(4),
        Validators.maxLength(40),
        Validators.pattern("[a-zA-Z0-9 ]+"),
      ]),
      password: new FormControl("", [
        Validators.required,
        Validators.minLength(8),
      ]),
      confirmPassword: new FormControl("", [
        Validators.required,
        Validators.minLength(8),
      ]),
      provider: new FormControl(false),
    },
    { validators: checkPasswords }
  );

  registerComplete = false;

  hidePassword = true;
  hideConfirmPassword = true;

  register() {
    const email = this.registerForm.get("email")?.value as string;
    const password = this.registerForm.get("password")?.value as string;
    const name = this.registerForm.get("name")?.value as string;
    const provider = this.registerForm.get("provider")?.value as boolean;

    const user: UserUpdate = {
      name: name,
      email: email,
      language: navigator.language,
      description: "",
      provider: provider,
      profilePicture: null,
      password: password,
    };

    this.userService.register(user).subscribe({
      next: (response) => {
        this.registerComplete = true;
      },
      error: (error) => {
        console.error("Login failed:", error);
        this.router.navigate(["/login"]);
      },
    });
  }

  get email() {
    return this.registerForm.get("email");
  }
  get password() {
    return this.registerForm.get("password");
  }
  get confirmPassword() {
    return this.registerForm.get("confirmPassword");
  }
  get name() {
    return this.registerForm.get("name");
  }
  get provider() {
    return this.registerForm.get("provider");
  }
}
