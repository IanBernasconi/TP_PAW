import { Subscription } from 'rxjs';
import { filter } from 'rxjs/operators';
import { Store, Action } from '@ngrx/store';
import { ToastService } from '../services/toastService/toast.service';
import { Directive } from '@angular/core';

@Directive()
export class BaseComponent {
    protected subscriptions: Subscription = new Subscription();

    constructor(protected store: Store, protected toastService: ToastService) {
        this.store = store;
        this.toastService = toastService;
    }

    protected subscribeToSuccess(selector: any, toastMessage: string, resetAction: Action, extraFunction?: () => void) {
        this.subscriptions.add(
            this.store.select(selector).pipe(
                filter(finished => !!finished)
            ).subscribe((finished) => {
                if (extraFunction) {
                    extraFunction();
                }
                this.toastService.success(toastMessage);
                this.store.dispatch(resetAction);
            })
        );
    }

    protected subscribeToError(selector: any, toastMessage: string, resetAction: Action, extraFunction?: () => void) {
        this.subscriptions.add(
            this.store.select(selector).pipe(
                filter(error => !!error)
            ).subscribe((error) => {
                if (extraFunction) {
                    extraFunction();
                }
                console.error(error);
                this.toastService.error(toastMessage);
                this.store.dispatch(resetAction);
            })
        );
    }

    ngOnDestroy() {
        this.subscriptions.unsubscribe();
    }
}