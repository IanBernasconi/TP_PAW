import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { HTTP_INTERCEPTORS, HttpClient, HttpClientModule } from '@angular/common/http';

import { MatCardModule } from '@angular/material/card';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS, MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms'
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatSliderModule } from '@angular/material/slider';
import { MatButtonModule } from '@angular/material/button'
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatRadioModule } from '@angular/material/radio';

import { RouterModule, Routes } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { EventModule } from './event/event.module';
import { OfferingModule } from './offering/offering.module';
import { AuthInterceptor, AuthorizedResponseInterceptor, ForbiddenResponseInterceptor, UnauthorizedResponseInterceptor } from "./auth.interceptor";
import { UserModule } from "./user/user.module";
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { UtilsModule } from './utils/utils.module';
import { NavbarComponent } from './components/navbar/navbar.component';
import { CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { ErrorPageComponent } from './components/error-pages/error-page/error-page.component';
import { NotFoundPageComponent } from './components/error-pages/not-found-page/not-found-page.component';
import { ToastsComponent } from './components/toasts/toasts.component';
import { StoreModule } from '@ngrx/store';
import { userReducer } from './store/user/user.reducer';

import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { environment } from '../environments/environment';
import { EffectsModule } from '@ngrx/effects';
import * as UserEffects from './store/user/user.effects';
import * as EventsEffects from './store/events/events.effects';
import * as OfferingsEffects from './store/offerings/offerings.effects';
import * as UserOfferingsEffects from './store/offerings/user-offerings/user-offerings.effects';
import { offeringsReducer } from './store/offerings/offerings.reducer';
import { userOfferingsReducer } from './store/offerings/user-offerings/user-offerings.reducer';
import { logoutMetaReducer } from './store/meta-reducers';
import { eventsReducer } from './store/events/events.reducer';
import { APP_BASE_HREF } from '@angular/common';
import { MatDatepickerModule } from '@angular/material/datepicker';

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

const routes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'services', loadChildren: () => import('./offering/offering.module').then(m => m.OfferingModule) },
  { path: 'events', loadChildren: () => import('./event/event.module').then(m => m.EventModule) },
  { path: 'user', loadChildren: () => import('./user/user.module').then(m => m.UserModule) },
  { path: '**', component: NotFoundPageComponent }
];

export const reducers: any = {
  user: userReducer,
  offerings: offeringsReducer,
  userOfferings: userOfferingsReducer,
  events: eventsReducer
};


@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    LandingPageComponent,
    NotFoundPageComponent,
    ToastsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MatIconModule,
    MatCardModule,
    MatGridListModule,
    MatFormFieldModule,
    FormsModule,
    MatSelectModule,
    MatInputModule,
    MatSliderModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatRadioModule,
    ReactiveFormsModule,
    RouterModule.forRoot(routes),
    //created modules
    OfferingModule,
    EventModule,
    UserModule,
    NgbModule,
    UtilsModule,
    CalendarModule.forRoot({ provide: DateAdapter, useFactory: adapterFactory }),
    ErrorPageComponent,
    StoreModule.forRoot(reducers, { metaReducers: [logoutMetaReducer] }),
    StoreDevtoolsModule.instrument({
      maxAge: 25,
      logOnly: environment.production,
      // serialize: {
      //   replacer: (key, value) => {
      //     // To allow Map serialization in Redux DevTools
      //     if (value instanceof Map) {
      //       let obj: any = {};
      //       for (let [keyName, val] of value.entries()) {
      //         obj[keyName] = val;
      //       }
      //       return obj;
      //     } else {
      //       return value;
      //     }
      //   }
      // }
    }),

    EffectsModule.forRoot(UserEffects, EventsEffects, OfferingsEffects, UserOfferingsEffects)
  ],
  exports: [
    RouterModule
  ],
  providers: [
    // https://github.com/angular/angular-cli/issues/23765
    { provide: APP_BASE_HREF, useValue: environment.baseHref },
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: UnauthorizedResponseInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ForbiddenResponseInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: AuthorizedResponseInterceptor, multi: true },
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: { appearance: 'outline' }
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
