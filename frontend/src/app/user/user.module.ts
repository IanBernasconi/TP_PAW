import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { ProfilePageComponent } from "./profile-page/profile-page.component";
import { LoginPageComponent } from "./login-page/login-page.component";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { RouterModule, Routes } from "@angular/router";
import { UtilsModule } from "../utils/utils.module";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { ForgotPasswordComponent } from "./forgot-password/forgot-password.component";
import { RegisterComponent } from "./register/register.component";
import { MatIconModule } from "@angular/material/icon";
import { MatCardModule } from "@angular/material/card";
import { MatSelectModule } from "@angular/material/select";
import { MatOptionModule } from "@angular/material/core";
import { ErrorPageComponent } from "../components/error-pages/error-page/error-page.component";
import { IsLoggedGuard } from "../guards/is-logged-in/is-logged.guard";
import { VerifyComponent } from "./register/verify/verify.component";
import { ResetPasswordComponent } from "./reset-password/reset-password.component";
import { NotFoundPageComponent } from "../components/error-pages/not-found-page/not-found-page.component";
import { IsNotLoggedGuard } from "../guards/is-not-logged-in/is-not-logged.guard";

const routes: Routes = [
  {
    path: "user/login",
    component: LoginPageComponent,
    canActivate: [IsNotLoggedGuard],
  },
  {
    path: "user/profile",
    component: ProfilePageComponent,
    canActivate: [IsLoggedGuard],
  },
  {
    path: "user/forgot-password",
    component: ForgotPasswordComponent,
    canActivate: [IsNotLoggedGuard],
  },
  {
    path: "user/reset-password",
    component: ResetPasswordComponent,
    canActivate: [IsNotLoggedGuard],
  },
  {
    path: "user/register",
    component: RegisterComponent,
    canActivate: [IsNotLoggedGuard],
  },
  {
    path: "user/verify",
    component: VerifyComponent,
    canActivate: [IsNotLoggedGuard],
  },
  { path: "user", component: NotFoundPageComponent },
];

@NgModule({
  declarations: [
    ProfilePageComponent,
    LoginPageComponent,
    ForgotPasswordComponent,
    RegisterComponent,
    VerifyComponent,
    ResetPasswordComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes),
    ReactiveFormsModule,
    UtilsModule,
    MatCardModule,
    MatFormFieldModule,
    MatSelectModule,
    MatOptionModule,
    MatInputModule,
    MatIconModule,
    ErrorPageComponent,
  ],
})
export class UserModule { }
