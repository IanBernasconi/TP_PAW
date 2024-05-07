import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { RelationInfo } from 'src/shared/models/event.model';

export class ReviewDialogData {
  content: string;
  rating: number;
  relationInfo: RelationInfo;

  constructor(content: string, rating: number, relationInfo: RelationInfo) {
    this.content = content;
    this.rating = rating;
    this.relationInfo = relationInfo;
  }
}


@Component({
  selector: 'review-dialog',
  templateUrl: './review-dialog.component.html',
  styleUrls: ['./review-dialog.component.scss']
})
export class ReviewDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<ReviewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ReviewDialogData) { }

  ngOnInit(): void {
  }


}
