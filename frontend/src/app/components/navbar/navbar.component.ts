import { Component, Input, SimpleChanges } from "@angular/core";
import { Store } from "@ngrx/store";
import { AuthService } from "../../services/authService/auth-service.service";
import { UserService } from "src/app/services/userService/user.service";
import {
  selectUserIsLoggedIn,
  selectUser,
} from "src/app/store/user/user.selector";
import { UserLogoutActions } from "src/app/store/user/user.actions";
import { Router } from "@angular/router";

@Component({
  selector: "navbar",
  templateUrl: "./navbar.component.html",
  styleUrls: ["./navbar.component.scss"],
})
export class NavbarComponent {
  @Input() isLandingPage: boolean = false;

  constructor(
    private authService: AuthService,
    private store: Store,
    private router: Router
  ) {}

  logoSrc: string = "assets/images/logo-black.png";

  isAuthenticated$ = this.store.select(selectUserIsLoggedIn);
  user$ = this.store.select(selectUser);

  ngOnChanges(changes: SimpleChanges): void {
    if (changes["isLandingPage"]) {
      this.logoSrc = this.isLandingPage
        ? "assets/images/logo-black.png"
        : "assets/images/logo-blue.png";
    }
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.store.dispatch(UserLogoutActions.logout());
        this.router.navigate(["user", "login"]);
      },
    });
  }
}
