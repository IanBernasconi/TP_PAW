import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { map } from 'rxjs';
import { EnumService } from 'src/app/services/enumService/enum.service';
import { Event, EventToCreate } from 'src/shared/models/event.model';
import { User } from 'src/shared/models/user.model';
import { District } from 'src/shared/models/utils.model';

@Component({
  selector: 'event-data-form',
  templateUrl: './event-data-form.component.html',
  styleUrls: ['./event-data-form.component.scss']
})
export class EventDataFormComponent implements OnInit {
  @Input({ required: true }) loggedUser!: User;
  @Input() event: EventToCreate = new EventToCreate("", "", "", 50, "NOT_YET_DEFINED" as keyof District, "");
  @Input() hideSubmitButton: boolean = false;
  @Output() createdEvent = new EventEmitter<EventToCreate>();
  @Output() editedEvent = new EventEmitter<EventToCreate>();
  @Output() isFormValid = new EventEmitter<boolean>();

  createForm!: FormGroup;

  constructor(private enumService: EnumService) { }

  districts$ = this.enumService.getDistricts().pipe(
    map(districts => districts.filter(d => d.name !== "ALL" && d.name !== "NOT_SPECIFIED"))
  );


  ngOnInit(): void {
    this.createForm = new FormGroup({
      name: new FormControl(this.event.name, [
        Validators.required,
        Validators.minLength(4),
        Validators.maxLength(50)
      ]),
      description: new FormControl(this.event.description),
      date: new FormControl(this.event.date, [
        Validators.required,
        this.futureDateValidator
      ]),
      numberOfGuests: new FormControl(this.event.numberOfGuests, [
        Validators.required,
        Validators.min(1),
        Validators.max(10000000)
      ]
      ),
      district: new FormControl(this.event.district, [
        Validators.required
      ])
    });

    this.createForm.valueChanges.subscribe(value => {
      if (this.createForm.invalid) {
        this.isFormValid.emit(false);
        return;
      }
      this.event = { ...this.event, ...value };
      this.editedEvent.emit(this.event);
      this.isFormValid.emit(true);
    });
  }

  futureDateValidator(control: AbstractControl): ValidationErrors | null {
    if (!control.value) { return null }

    const forbidden = new Date(control.value) < new Date();
    return forbidden ? { forbiddenDate: { value: control.value } } : null;
  }


  onSubmit() {

    let event = new EventToCreate(
      this.createForm.value.name,
      this.createForm.value.description,
      this.createForm.value.date,
      this.createForm.value.numberOfGuests,
      this.createForm.value.district,
      this.event.owner === "" ? this.loggedUser.self : this.event.owner
    );
    this.createdEvent.emit(event);

  }

  get name() {
    return this.createForm.get('name');
  }

  get description() {
    return this.createForm.get('description');
  }

  get date() {
    return this.createForm.get('date');
  }

  get numberOfGuests() {
    return this.createForm.get('numberOfGuests');
  }

  get district() {
    return this.createForm.get('district');
  }

}
