import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Observable } from 'rxjs';
import { EnumService } from 'src/app/services/enumService/enum.service';
import { Offering } from 'src/shared/models/offering.model';
import { User } from 'src/shared/models/user.model';

@Component({
  selector: 'offering-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {
  @Input({ required: true }) offering!: Offering;
  @Input({ required: true }) loggedUser?: User;
  @Input({ required: true }) isLiked?: boolean;

  @Output() deleteOfferingEvent = new EventEmitter();
  @Output() editOfferingEvent = new EventEmitter();

  @Output() likeOffering: EventEmitter<Offering> = new EventEmitter<Offering>();
  @Output() deleteLike: EventEmitter<Offering> = new EventEmitter<Offering>();

  constructor(private enumService: EnumService) { }


  deleteOffering() {
    this.deleteOfferingEvent.emit();
  }

  editOffering() {
    this.editOfferingEvent.emit();
  }

  onLikeClick() {
    if (this.isLiked) {
      this.deleteLike.emit(this.offering);
    } else {
      this.likeOffering.emit(this.offering);
    }
  }

  getCategoryValue(): Observable<string | undefined> {
    return this.enumService.getCategoryValue(this.offering.category);
  }

  getPriceTypeValue(priceType: string): Observable<string | undefined> {
    return this.enumService.getPriceTypeValue(priceType);
  }

  priceTypes$ = this.enumService.getPriceTypes();


  getDistrictValue(district: string): Observable<string | undefined> {
    return this.enumService.getDistrictValue(district);
  }

}
