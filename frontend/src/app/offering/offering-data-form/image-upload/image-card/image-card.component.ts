import { Component, EventEmitter, Input, Output } from '@angular/core';
import { URI } from 'src/shared/types';
import { ImageWithStatus } from '../image-upload.component';

@Component({
  selector: 'image-card',
  templateUrl: './image-card.component.html',
  styleUrls: ['./image-card.component.scss']
})
export class ImageCardComponent {
  @Input() imageWithStatus?: ImageWithStatus | string;
  @Output() deleteImage = new EventEmitter<ImageWithStatus>();
  @Output() addImage = new EventEmitter<ImageWithStatus>();

  isImageWithStatus() {
    return (typeof this.imageWithStatus !== 'string');
  }

  getImageSrc() {
    if (this.isImageWithStatus()) {
      return (this.imageWithStatus as ImageWithStatus)?.image;
    } else {
      return this.imageWithStatus;
    }
  }

  getImageStatus() {
    if (this.isImageWithStatus()) {
      return (this.imageWithStatus as ImageWithStatus)?.isPresent;
    } else {
      return undefined;
    }
  }

  getImageWithStatus() {
    if (this.isImageWithStatus()) {
      return this.imageWithStatus as ImageWithStatus;
    } else {
      return undefined;
    }
  }
}
