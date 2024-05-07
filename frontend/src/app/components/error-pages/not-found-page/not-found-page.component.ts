import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'not-found-page',
  templateUrl: './not-found-page.component.html',
  styleUrls: ['./not-found-page.component.scss']
})
export class NotFoundPageComponent {


  constructor(private router: Router) { }

  goToExplorer() {
    this.router.navigate(['services']);
  }

  errorDescription = $localize`The page you are looking for does not exist.`;
  actionName = $localize`Go to explorer`;
}
