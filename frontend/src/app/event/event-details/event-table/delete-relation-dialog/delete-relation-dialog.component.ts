import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { RelationInfo } from 'src/shared/models/event.model';
import { Relation } from 'src/shared/models/relation.model';

export class DeleteRelationDialogData {
  relationInfo: RelationInfo;

  constructor(relationInfo: RelationInfo) {
    this.relationInfo = relationInfo;
  }
}


@Component({
  selector: 'delete-relation-dialog',
  templateUrl: './delete-relation-dialog.component.html',
  styleUrls: ['./delete-relation-dialog.component.scss']
})
export class DeleteRelationDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<DeleteRelationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DeleteRelationDialogData) { }

  ngOnInit(): void {
  }

}
