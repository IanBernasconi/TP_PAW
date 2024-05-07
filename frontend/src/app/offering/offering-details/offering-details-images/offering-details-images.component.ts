import { Component, Input } from '@angular/core';

@Component({
  selector: 'offering-details-images',
  templateUrl: './offering-details-images.component.html',
  styleUrls: ['./offering-details-images.component.scss']
})
export class OfferingDetailsImagesComponent {
  @Input() images!: string[];


}
