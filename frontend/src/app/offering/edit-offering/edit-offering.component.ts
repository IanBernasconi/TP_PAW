import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { OfferingService } from "../../services/offeringService/offering.service";
import { ActivatedRoute, Router } from "@angular/router";
import { OfferingToCreate } from 'src/shared/models/offering.model';
import { ToastMessages, ToastService } from 'src/app/services/toastService/toast.service';
import { selectUser } from 'src/app/store/user/user.selector';
import { OfferingSelectActions, OfferingUpdateActions } from 'src/app/store/offerings/offerings.actions';
import { selectOfferingUpdateError, selectOfferingUpdateFlag, selectOfferingsError, selectOfferingsLoading, selectSelectedOffering } from 'src/app/store/offerings/offerings.selector';
import { URI } from 'src/shared/types';
import { BaseComponent } from 'src/app/utils/base-component.component';
import { NavigationService } from 'src/app/services/navigationService/navigation.service';

@Component({
  selector: 'edit-offering',
  templateUrl: './edit-offering.component.html',
  styleUrls: ['./edit-offering.component.scss']
})
export class EditOfferingComponent extends BaseComponent {

  constructor(
    store: Store,
    toastService: ToastService,
    private router: Router,
    private route: ActivatedRoute,
  ) {
    super(store, toastService);
  }

  offeringId!: number;

  offering$ = this.store.select(selectSelectedOffering);
  error$ = this.store.select(selectOfferingsError);

  loggedUser$ = this.store.select(selectUser);
  ngOnInit() {
    // Subscribe to route parameter changes to get the ID
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.offeringId = parseInt(idParam, 10);
        this.store.dispatch(OfferingSelectActions.selectOffering({ offering: OfferingService.getUriFromId(this.offeringId) }));
      }
    });
  }

  actionMessage = $localize`Go back`


  updateOffering(uri: URI, offering: OfferingToCreate) {
    this.subscribeToSuccess(selectOfferingUpdateFlag, ToastMessages.offering.update.success, OfferingUpdateActions.resetUpdateOfferingFlags(), () => this.router.navigate(['/my-services']));
    this.subscribeToError(selectOfferingUpdateError, ToastMessages.offering.update.error, OfferingUpdateActions.resetUpdateOfferingFlags());

    this.store.dispatch(OfferingUpdateActions.updateOffering({ uri, updatedOffering: offering }));
  }

  goBackToDetails() {
    this.router.navigate(['/services', this.offeringId]);
  }

}
