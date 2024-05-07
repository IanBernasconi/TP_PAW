import { Component, ViewChild, ElementRef } from "@angular/core";
import { UserUpdate } from "src/shared/models/user.model";
import { FormControl, FormGroup, Validators } from "@angular/forms";
import { Store } from "@ngrx/store";
import {
  selectUser,
  selectUserFinishUpdate,
  selectUserUpdateError,
} from "src/app/store/user/user.selector";
import { UserUpdateActions } from "src/app/store/user/user.actions";
import { BaseComponent } from "src/app/utils/base-component.component";
import {
  ToastMessages,
  ToastService,
} from "src/app/services/toastService/toast.service";
import { filter, take } from "rxjs";
import { UserService } from "src/app/services/userService/user.service";

@Component({
  selector: "app-profile-page",
  templateUrl: "./profile-page.component.html",
  styleUrls: ["./profile-page.component.scss"],
})
export class ProfilePageComponent extends BaseComponent {


  editMode = false;
  fileToUpload: File | undefined;

  updateError?: string;

  loggedUser$ = this.store.select(selectUser);

  ngOnInit(): void {
    this.loggedUser$
      .pipe(
        filter((user) => user !== undefined),
        take(1)
      )
      .subscribe((user) => {
        if (user) {
          this.profileForm.patchValue({
            name: user.name,
            description: user.description,
            language: user.language,
          });
        }
      });
  }

  profileForm = new FormGroup({
    description: new FormControl(""),
    name: new FormControl("", [
      Validators.required,
      Validators.minLength(6),
      Validators.maxLength(40),
    ]),
    language: new FormControl(""),
  });

  selectedFile?: File;
  profilePicturePreview?: string;
  isPictureRemoved = false;

  constructor(
    store: Store,
    toastService: ToastService,
  ) {
    super(store, toastService);
  }

  @ViewChild("fileInput") fileInput!: ElementRef;

  toggleEditMode() {
    this.editMode = !this.editMode;
  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
    if (this.selectedFile) {
      this.isPictureRemoved = true;
      this.profilePicturePreview = URL.createObjectURL(this.selectedFile);
      this.profileForm.markAsDirty();
    }
  }

  removeProfilePicture() {
    this.isPictureRemoved = true;
    this.profileForm.markAsDirty();
    this.selectedFile = undefined;
  }

  saveChanges() {
    this.loggedUser$.pipe(take(1)).subscribe((user) => {
      if (user) {
        if (this.profileForm.valid) {
          //create the update user with the form values if they were written
          const updatedUser: UserUpdate = {
            name: this.name?.value ? this.name.value : user.name,
            email: user.email,
            language: this.language?.value
              ? this.language.value
              : user.language,
            description: this.description?.value
              ? this.description?.value
              : "",
            provider: user.provider,
            profilePicture: user.profilePicture,
          };

          this.subscribeToSuccess(
            selectUserFinishUpdate,
            ToastMessages.user.update.success,
            UserUpdateActions.resetUpdate(),
            this.cancelEdit.bind(this)
          );
          this.subscribeToError(
            selectUserUpdateError,
            ToastMessages.user.update.error,
            UserUpdateActions.resetUpdate(),
            this.cancelEdit.bind(this)
          );

          this.store.dispatch(
            UserUpdateActions.updateUser({
              uri: user.self,
              user: updatedUser,
              profileImageData: {
                profileImage: this.selectedFile,
                removeProfilePicture: this.isPictureRemoved,
              },
            })
          );
        }
      }
    });
  }

  cancelEdit() {
    this.editMode = false;
    this.profileForm?.reset();
    this.isPictureRemoved = false;
    this.profilePicturePreview = undefined;
    this.selectedFile = undefined;
    this.loggedUser$.pipe(take(1)).subscribe((user) => {
      if (user) {
        this.profileForm.patchValue({
          name: user.name,
          description: user.description,
          language: user.language,
        });
      }
    });
  }

  getLanguageName(language: string) {
    if (this.languageOptions.hasOwnProperty(language)) {
      return this.languageOptions[language as keyof typeof this.languageOptions];
    }
    return $localize`Unknown`;
  }

  languageOptions = {
    en: $localize`English`,
    es: $localize`Spanish`,
  };


  becomeProvider() {
    this.loggedUser$.pipe(take(1)).subscribe((user) => {
      if (user) {
        this.subscribeToSuccess(
          selectUserFinishUpdate,
          ToastMessages.user.becomeProvider.success,
          UserUpdateActions.resetUpdate()
        );
        this.subscribeToError(
          selectUserUpdateError,
          ToastMessages.user.becomeProvider.error,
          UserUpdateActions.resetUpdate()
        );
        this.store.dispatch(UserUpdateActions.userBecomeProvider({ uri: user.self }));
      }
    });
  }

  get name() {
    return this.profileForm.get("name");
  }
  get description() {
    return this.profileForm.get("description");
  }
  get language() {
    return this.profileForm.get("language");
  }
}
