import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';

@Component({
  selector: 'paginator',
  templateUrl: './paginator.component.html',
  styleUrls: ['./paginator.component.scss']
})
export class PaginatorComponent {
  @Input({ required: true }) currentPage!: number;
  @Input({ required: true }) totalPages!: number;
  @Output() changeToNextPage = new EventEmitter<Event>();
  @Output() changeToPreviousPage = new EventEmitter<Event>();

  ngOnChanges(changes: SimpleChanges) {
    if (changes["currentPage"]) {
      if (this.currentPage < 0) {
        this.currentPage = 0;
      }
    }
    if (changes["totalPages"]) {
      if (this.totalPages < 0) {
        this.totalPages = 0;
      }
    }
  }

}
