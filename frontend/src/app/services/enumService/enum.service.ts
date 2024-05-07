import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, shareReplay, switchMap, tap } from 'rxjs';
import { environment } from 'src/environments/environment';
import { VndType } from 'src/shared/VndType';
import { District, OfferingCategory, PriceType } from 'src/shared/models/utils.model';

const apiUrl = `${environment.apiUrl}`;


@Injectable({
  providedIn: 'root'
})
export class EnumService {

  constructor(private http: HttpClient) { }

  offeringCategories: OfferingCategory[] = [];
  priceTypes: PriceType[] = [];
  districts: District[] = [];

  private offeringCategories$: Observable<OfferingCategory[]> | null = null;
  private priceTypes$: Observable<PriceType[]> | null = null;
  private districts$: Observable<District[]> | null = null;

  getOfferingCategories(): Observable<OfferingCategory[]> {
    if (this.offeringCategories.length > 0) {
      return of(this.offeringCategories);
    } else if (this.offeringCategories$) {
      return this.offeringCategories$;
    } else {
      const headers = new HttpHeaders().set('Accept', VndType.APPLICATION_OFFERING_CATEGORIES);

      this.offeringCategories$ = this.http.get<OfferingCategory[]>(`${apiUrl}/categories`, { headers }).pipe(
        tap(categories => {
          this.offeringCategories = categories;
        }),
        shareReplay(1)
      );
      return this.offeringCategories$;
    }
  }

  getCategoryValue(categoryName: string): Observable<string | undefined> {
    if (this.offeringCategories.length > 0) {
      return of(this.offeringCategories.find(category => category.name === categoryName)?.value);
    } else {
      return this.getOfferingCategories().pipe(
        switchMap(categories => {
          return of(categories.find(category => category.name === categoryName)?.value);
        })
      );
    }
  }

  getPriceTypes(): Observable<PriceType[]> {
    if (this.priceTypes.length > 0) {
      return of(this.priceTypes);
    } else if (this.priceTypes$) {
      return this.priceTypes$;
    } else {
      const headers = new HttpHeaders().set('Accept', VndType.APPLICATION_OFFERING_PRICE_TYPES);
      this.priceTypes$ = this.http.get<PriceType[]>(`${apiUrl}/priceTypes`, { headers }).pipe(
        tap(priceTypes => {
          this.priceTypes = priceTypes;
        }),
        shareReplay(1)
      );
      return this.priceTypes$;
    }
  }

  getPriceTypeValue(priceTypeName: string): Observable<string | undefined> {
    if (this.priceTypes.length > 0) {
      return of(this.priceTypes.find(priceType => priceType.name === priceTypeName)?.value);
    } else {
      return this.getPriceTypes().pipe(
        switchMap(priceTypes => {
          return of(priceTypes.find(priceType => priceType.name === priceTypeName)?.value);
        })
      );
    }
  }

  getDistricts(): Observable<District[]> {
    if (this.districts.length > 0) {
      return of(this.districts);
    } else if (this.districts$) {
      return this.districts$;
    } else {
      const headers = new HttpHeaders().set('Accept', VndType.APPLICATION_DISTRICTS);
      this.districts$ = this.http.get<District[]>(`${apiUrl}/districts`, { headers }).pipe(
        tap(districts => {
          this.districts = districts;
        }),
        shareReplay(1)
      );
      return this.districts$;
    }
  }

  getDistrictValue(districtName: string): Observable<string | undefined> {
    if (this.districts.length > 0) {
      return of(this.districts.find(district => district.name === districtName)?.value);
    } else {
      return this.getDistricts().pipe(
        switchMap(districts => {
          return of(districts.find(district => district.name === districtName)?.value);
        })
      );
    }
  }

}
