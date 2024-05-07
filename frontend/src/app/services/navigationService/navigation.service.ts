import { Injectable } from '@angular/core';
import { Location } from "@angular/common";
import { Router, NavigationEnd } from "@angular/router";

@Injectable({ providedIn: "root" })
export class NavigationService {
  private history: string[] = [];
  private addNextToHistory = true;

  constructor(private router: Router, private location: Location) {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        if (this.addNextToHistory) {
          this.history.push(event.urlAfterRedirects);
        }
        this.addNextToHistory = true;
      }
    });
  }

  back(currentUrl: string): void {
    let url = this.history.pop();
    while (url === currentUrl) {
      url = this.history.pop();
    }
    if (url) {
      this.router.navigateByUrl(url);
    } else {
      this.router.navigateByUrl("/services");
    }
  }

  navigateWithoutHistory(commands: any[], extras?: any): void {
    this.addNextToHistory = false;
    this.router.navigate(commands, extras);
  }
}