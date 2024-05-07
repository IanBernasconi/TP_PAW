import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { OfferingsFetchActions } from 'src/app/store/offerings/offerings.actions';
import { selectOfferings, selectOfferingsLoading } from 'src/app/store/offerings/offerings.selector';
import { environment } from 'src/environments/environment';
import { OfferingFilter, Offerings, SortTypeKeys } from 'src/shared/models/offering.model';

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.scss']
})
export class LandingPageComponent {

  constructor(private store: Store) { }

  offerings$ = this.store.select(selectOfferings);
  isLoading$ = this.store.select(selectOfferingsLoading);

  baseHref = environment.baseHref;


  ngOnInit(): void {
    this.store.dispatch(OfferingsFetchActions.fetchOfferings({ uri: undefined, filter: new OfferingFilter().setSortType(SortTypeKeys.POPULARITY_DESC) }))
  }

}
