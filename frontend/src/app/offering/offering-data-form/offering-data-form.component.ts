import {
  Component,
  EventEmitter,
  Input,
  Output,
  ViewChild,
} from "@angular/core";
import {
  OfferingToCreate,
  offeringCategoryPriceTypes,
} from "src/shared/models/offering.model";
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from "@angular/forms";
import { UserService } from "src/app/services/userService/user.service";
import { NumberInput } from "@angular/cdk/coercion";
import { MatSlider } from "@angular/material/slider";
import { URI } from "src/shared/types";
import {
  ImageResponseType,
  ImageService,
} from "src/app/services/imageService/image.service";
import { Observable, forkJoin, map } from "rxjs";
import { ImageWithStatus } from "./image-upload/image-upload.component";
import { User } from "src/shared/models/user.model";
import { EnumService } from "src/app/services/enumService/enum.service";
import {
  District,
  OfferingCategory,
  PriceType,
} from "src/shared/models/utils.model";

@Component({
  selector: "offering-data-form",
  templateUrl: "./offering-data-form.component.html",
  styleUrls: ["./offering-data-form.component.scss"],
})
export class OfferingDataFormComponent {
  @Input({ required: true }) loggedUser!: User;
  @Input() offering: OfferingToCreate = {
    name: "",
    description: "",
    category: "OTHER" as keyof OfferingCategory,
    minPrice: 1,
    maxPrice: 100,
    priceType: "OTHER" as keyof PriceType,
    maxGuests: 0,
    district: "ALL" as keyof District,
    images: [],
    owner: "",
  };
  @Output() createOfferingEvent = new EventEmitter<OfferingToCreate>();

  constructor(
    private imageService: ImageService,
    private enumService: EnumService
  ) { }

  OfferingCategory$ = this.enumService.getOfferingCategories();
  filteredCategories = this.enumService.getOfferingCategories().pipe(
    map((categories) => {
      return categories.filter((category) => category.name != "ALL");
    })
  );

  districts$ = this.enumService
    .getDistricts()
    .pipe(
      map((districts) =>
        districts.filter(
          (d) =>
            d.name !== "ALL" &&
            d.name !== "NOT_SPECIFIED" &&
            d.name !== "NOT_YET_DEFINED"
        )
      )
    );

  priceTypes$ = this.enumService.getPriceTypes();
  // District = District;

  createForm!: FormGroup;
  priceInputMock!: FormControl;
  priceMock = 1;

  priceMinRange: NumberInput = 1;
  priceMaxRange: NumberInput = 110;

  selectedFiles?: FileList;

  unlimitedGuestsValue!: boolean;

  imagesWithStatus: ImageWithStatus[] = [];

  priceValidator: ValidatorFn = (
    control: AbstractControl
  ): ValidationErrors | null => {
    const min = control.get("minPrice");
    const max = control.get("maxPrice");
    return min?.value && max?.value && +max.value < +min.value
      ? { overlap: true }
      : null;
  };

  ngOnInit(): void {
    this.imagesWithStatus = this.offering.images
      .map((image: URI) => new ImageWithStatus(image, true))
      .sort((a, b) => a.image.localeCompare(b.image));

    this.unlimitedGuestsValue = this.offering.maxGuests == 0;

    this.createForm = new FormGroup(
      {
        name: new FormControl(this.offering.name, [
          Validators.required,
          Validators.minLength(4),
          Validators.maxLength(32)
        ]),
        description: new FormControl(this.offering.description),
        category: new FormControl(this.offering.category, [
          Validators.required,
        ]),
        minPrice: new FormControl(this.offering.minPrice, [
          Validators.required,
          Validators.min(1),
          Validators.max(10000000),
        ]),
        maxPrice: new FormControl(this.offering.maxPrice, [
          Validators.required,
          Validators.min(1),
          Validators.max(10000000),
        ]),
        priceType: new FormControl(this.offering.priceType, [
          Validators.required,
        ]),
        maxGuests: new FormControl(this.offering.maxGuests, [
          Validators.required,
          Validators.min(1),
          Validators.max(10000000),
        ]),
        unlimitedGuests: new FormControl(this.unlimitedGuestsValue),
        district: new FormControl(this.offering.district, [
          Validators.required,
        ]),
      },
      { validators: this.priceValidator }
    );

    this.priceInputMock = new FormControl(this.priceMock);
    this.minPrice?.valueChanges.subscribe((value) => {
      this.priceMinRange =
        Math.floor(value * 0.95) < 1 ? 1 : Math.floor(value * 0.95);

      this.createForm.updateValueAndValidity();

      if (this.createForm.invalid && this.createForm.hasError("overlap")) {
        this.priceInputMock.setErrors({ valid: false });
        this.priceInputMock.markAllAsTouched();
      } else if (this.minPrice?.invalid || this.maxPrice?.invalid) {
        this.priceInputMock.setErrors({ valid: false });
        this.priceInputMock.markAllAsTouched();
      } else {
        this.priceInputMock.setErrors(null);
      }
    });
    this.maxPrice?.valueChanges.subscribe((value) => {
      this.priceMaxRange =
        Math.ceil(value * 1.05) > 10000000 ? 10000000 : Math.ceil(value * 1.05);
      this.createForm.updateValueAndValidity();

      if (this.createForm.invalid && this.createForm.hasError("overlap")) {
        this.priceInputMock.setErrors({ valid: false });
        this.priceInputMock.markAllAsTouched();
      } else if (this.minPrice?.invalid || this.maxPrice?.invalid) {
        this.priceInputMock.setErrors({ valid: false });
        this.priceInputMock.markAllAsTouched();
      } else {
        this.priceInputMock.setErrors(null);
      }
    });

    this.category?.valueChanges.subscribe((value) => {
      if (this.priceType) {
        this.priceType.setValue(offeringCategoryPriceTypes.get(value));
        if (value != "OTHER") {
          this.priceType.disable();
        } else {
          this.priceType.enable();
        }
      }
    });

    this.updateMaxGuests(this.unlimitedGuestsValue);

    this.unlimitedGuests?.valueChanges.subscribe((value) => {
      this.updateMaxGuests(value);
    });
  }

  updateMaxGuests(isUnlimited: boolean) {
    if (isUnlimited) {
      this.maxGuests?.disable();
      this.maxGuests?.setValue(0);
    } else {
      this.maxGuests?.enable();
      this.maxGuests?.setValue(this.offering.maxGuests);
    }
  }

  handleSelectedFilesChanged(files: FileList) {
    if (files.length > 10) {
      this.createForm.get("images")?.setErrors({ tooManyImages: true });
    } else {
      this.createForm.get("images")?.setErrors(null);
      this.selectedFiles = files;
    }
  }

  get name() {
    return this.createForm.get("name");
  }

  get description() {
    return this.createForm.get("description");
  }

  get category() {
    return this.createForm.get("category");
  }

  get minPrice() {
    return this.createForm.get("minPrice");
  }

  get maxPrice() {
    return this.createForm.get("maxPrice");
  }

  get priceType() {
    return this.createForm.get("priceType");
  }

  get maxGuests() {
    return this.createForm.get("maxGuests");
  }

  get unlimitedGuests() {
    return this.createForm.get("unlimitedGuests");
  }

  get district() {
    return this.createForm.get("district");
  }

  handleAddImage(imageWithStatus: ImageWithStatus) {
    this.imagesWithStatus = this.imagesWithStatus.filter(
      (imageWithStatusInArray) =>
        imageWithStatusInArray.image != imageWithStatus.image
    );
    this.imagesWithStatus.push(
      new ImageWithStatus(imageWithStatus.image, true)
    );
    this.imagesWithStatus.sort((a, b) => a.image.localeCompare(b.image));
  }

  handleDeleteImage(imageWithStatus: ImageWithStatus) {
    this.imagesWithStatus = this.imagesWithStatus.filter(
      (imageWithStatusInArray) =>
        imageWithStatusInArray.image != imageWithStatus.image
    );
    this.imagesWithStatus.push(
      new ImageWithStatus(imageWithStatus.image, false)
    );
    this.imagesWithStatus.sort((a, b) => a.image.localeCompare(b.image));
  }

  onSubmit() {
    if (this.unlimitedGuests?.value) {
      this.maxGuests?.setValue(0);
    }

    if (this.selectedFiles) {
      const uploadObservables = Array.from(this.selectedFiles)
        .filter((file: File) => file)
        .map((file: File) => this.imageService.uploadImage(file));
      forkJoin(uploadObservables).subscribe((responses) => {
        for (let response of responses) {
          this.imagesWithStatus.push(new ImageWithStatus(response, true));
        }
        this.createOffering();
      });
    } else {
      this.createOffering();
    }
  }

  createOffering() {
    this.priceType?.enable();
    let offering: OfferingToCreate = {
      name: this.createForm.value.name,
      description: this.createForm.value.description,
      category: this.createForm.value.category,
      minPrice: this.createForm.value.minPrice,
      maxPrice: this.createForm.value.maxPrice,
      priceType: this.createForm.value.priceType ?? "OTHER",
      maxGuests: this.createForm.value.maxGuests,
      district: this.createForm.value.district,
      images: this.imagesWithStatus
        .filter((imageWithStatus) => imageWithStatus.isPresent)
        .map((imageWithStatus) => imageWithStatus.image),
      owner: this.loggedUser.self,
    };

    this.createOfferingEvent.emit(offering);
  }
}
