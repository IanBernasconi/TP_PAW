import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { Router } from "@angular/router";
import { OfferingToCreate } from 'src/shared/models/offering.model';
import { ToastMessages, ToastService } from 'src/app/services/toastService/toast.service';
import { selectUser } from 'src/app/store/user/user.selector';
import { OfferingCreateActions } from 'src/app/store/offerings/offerings.actions';
import { BaseComponent } from 'src/app/utils/base-component.component';
import { selectOfferingCreateError, selectOfferingCreateFlag } from 'src/app/store/offerings/offerings.selector';

@Component({
  selector: 'offering-create',
  templateUrl: './offering-create.component.html',
  styleUrls: ['./offering-create.component.scss']
})
export class OfferingCreateComponent extends BaseComponent {

  constructor(toastService: ToastService, private router: Router, store: Store) {
    super(store, toastService);
  }


  loggedUser$ = this.store.select(selectUser);

  createOffering(offering: OfferingToCreate) {
    this.subscribeToSuccess(selectOfferingCreateFlag, ToastMessages.offering.create.success, OfferingCreateActions.resetCreateOfferingFlags(), () => this.router.navigate(['/my-services']));
    this.subscribeToError(selectOfferingCreateError, ToastMessages.offering.create.error, OfferingCreateActions.resetCreateOfferingFlags());

    this.store.dispatch(OfferingCreateActions.createOffering({ offering }));
  }

  goBack() {
    this.router.navigate(['/my-services']);
  }
}
