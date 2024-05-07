import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from "@angular/forms";
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { filter, take } from 'rxjs';
import { AuthService } from 'src/app/services/authService/auth-service.service';
import { ToastMessages, ToastService } from 'src/app/services/toastService/toast.service';
import { UserLoginActions, UserLogoutActions, UserUrlActions } from 'src/app/store/user/user.actions';
import { selectUser, selectUserErrorMessage, selectUserGoingToUrl } from 'src/app/store/user/user.selector';


@Component({
  selector: 'login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss']
})
export class LoginPageComponent implements OnInit {


  loggedUser$ = this.store.select(selectUser);
  pendingUrl$ = this.store.select(selectUserGoingToUrl);

  constructor(private formBuilder: FormBuilder, private store: Store, private router: Router, private toastService: ToastService) { }

  ngOnInit(): void {
    this.store.select(selectUserErrorMessage).subscribe({
      next: errorMessage => {
        if (errorMessage) {
          this.loginForm.setErrors({ 'loginError': true });
        }
      }
    });
    this.loggedUser$.pipe(filter(user => !!user), take(1)).subscribe({
      next: user => {
        this.pendingUrl$.pipe(take(1)).subscribe({
          next: url => {
            if (url) {
              this.store.dispatch(UserUrlActions.resetUrl());
              this.router.navigateByUrl(url);
            } else {
              this.router.navigate(['services'])
            }
          }
        });
        this.toastService.success(ToastMessages.login.success)

        this.toastService.success(ToastMessages.login.success)
      },
    });
  }

  loginForm = this.formBuilder.group(
    {
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      rememberMe: [AuthService.getRefreshToken() != null]
    }
  )

  hidePassword = true;

  login() {
    const email = this.loginForm.get('email')?.value as string;
    const password = this.loginForm.get('password')?.value as string;
    const rememberMe = this.loginForm.get('rememberMe')?.value as boolean;

    this.store.dispatch(UserLoginActions.login({ username: email, password, rememberMe }));
  }

  logout() {
    this.store.dispatch(UserLogoutActions.logout());
  }
}
