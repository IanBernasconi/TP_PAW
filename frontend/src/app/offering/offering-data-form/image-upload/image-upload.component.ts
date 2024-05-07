import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { FormControl } from "@angular/forms";
import { Observable } from "rxjs";
import {
  ImageResponseType,
  ImageService,
} from "src/app/services/imageService/image.service";
import { URI } from "src/shared/types";

@Component({
  selector: "image-upload",
  templateUrl: "./image-upload.component.html",
  styleUrls: ["./image-upload.component.scss"],
})
export class ImageUploadComponent {
  @Input() selectedFiles?: FileList;
  @Input() currentImages?: ImageWithStatus[];
  @Input() maxNumberOfImages: number = 10;
  @Output() changeCurrentImages = new EventEmitter<URI[]>();
  @Output() selectedFilesChanged = new EventEmitter<FileList>();
  @Output() deleteImage = new EventEmitter<ImageWithStatus>();
  @Output() addImage = new EventEmitter<ImageWithStatus>();

  progressInfos: any[] = [];
  message: string[] = [];

  previews: string[] = [];

  images = new FormControl($localize`Select Images`);

  constructor() {}

  selectFiles(event: any): void {
    this.message = [];
    this.progressInfos = [];
    const selectedFileNames = [];
    this.selectedFiles = event.target.files;

    this.previews = [];
    if (this.selectedFiles && this.selectedFiles[0]) {
      const numberOfFiles = this.selectedFiles.length;
      const totalNumberOfFiles =
        numberOfFiles + (this.currentImages ? this.currentImages.length : 0);
      if (totalNumberOfFiles > this.maxNumberOfImages) {
        this.images.setValue("Select images");
        this.images.setErrors({ maxNumberOfImages: true });
        return;
      }
      this.images.setErrors(null);
      for (let i = 0; i < numberOfFiles; i++) {
        const reader = new FileReader();
        reader.onload = (e: any) => {
          this.previews.push(e.target.result);
        };

        reader.readAsDataURL(this.selectedFiles[i]);

        selectedFileNames.push(this.selectedFiles[i].name);
        this.images.setValue(selectedFileNames.join(", "));
      }
      this.selectedFilesChanged.emit(this.selectedFiles);
    } else {
      this.images.setValue("Select images");
    }
  }
}

export class ImageWithStatus {
  image: URI;
  isPresent?: boolean;
  constructor(image: URI, isPresent: boolean) {
    this.image = image;
    this.isPresent = isPresent;
  }
}
