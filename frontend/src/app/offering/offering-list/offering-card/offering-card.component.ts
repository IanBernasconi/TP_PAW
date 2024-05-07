import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EnumService } from 'src/app/services/enumService/enum.service';
import { Offering } from 'src/shared/models/offering.model';
import { User } from 'src/shared/models/user.model';



@Component({
  selector: 'offering-card',
  templateUrl: './offering-card.component.html',
  styleUrls: ['./offering-card.component.scss']
})
export class OfferingCardComponent {
  @Input() offering: Offering | undefined;
  @Input() owner?: User;
  @Input() showCategoryIcon: boolean = true;
  @Input() isLoading: boolean = false;
  @Input() isLoadingLike: boolean = false;
  @Input() isLiked: boolean = false;
  @Input() showLikeButton: boolean = false;

  @Output() likeOffering: EventEmitter<Offering> = new EventEmitter<Offering>();
  @Output() deleteLike: EventEmitter<Offering> = new EventEmitter<Offering>();

  constructor(private enumService: EnumService) { }

  priceTypes$ = this.enumService.getPriceTypes();

  onLikeClick() {
    if (this.isLiked) {
      this.deleteLike.emit(this.offering!);
    } else {
      this.likeOffering.emit(this.offering!);
    }
  }

  getPriceTypeValue(priceType: string) {
    return this.enumService.getPriceTypeValue(priceType);
  }

  countWholeDigits(num: number): number {
    return num.toString().length;
  }

  calculatePriceIcons(minPrice: number, maxPrice: number): string {
    let averagePrice = (minPrice + maxPrice) / 2;

    if (averagePrice <= 1) {
      return '$';
    }

    let logPrice = Math.log10(averagePrice);
    let roundedLogPrice = Math.ceil(logPrice);

    return '$'.repeat(roundedLogPrice);
  }

}
