import { Component, HostListener } from '@angular/core';
import { NavigationEnd, Router } from "@angular/router";
import { AuthService } from './services/authService/auth-service.service';
import { UserService } from './services/userService/user.service';
import { Store } from '@ngrx/store';
import { UserFetchActions, UserLoginActions, UserLogoutActions } from 'src/app/store/user/user.actions';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'paw2023b04';
  isLandingPage = true;

  constructor(private router: Router, private authService: AuthService, private userService: UserService, private store: Store) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        const urlWithoutFragment = event.url.split('#')[0];
        this.isLandingPage = urlWithoutFragment === '/';
      }
    });
  }

  ngOnInit(): void {
    this.store.dispatch(UserFetchActions.fetchUser());
  }
}
