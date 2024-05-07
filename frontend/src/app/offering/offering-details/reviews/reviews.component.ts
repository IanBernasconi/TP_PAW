import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Reviews } from 'src/shared/models/review.model';
import { URI } from 'src/shared/types';


@Component({
  selector: 'reviews',
  templateUrl: './reviews.component.html',
  styleUrls: ['./reviews.component.scss']
})
export class ReviewsComponent {
  @Input({ required: true }) reviews!: Reviews;
  @Output() pageChange = new EventEmitter<URI>();

}
