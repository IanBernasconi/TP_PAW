import {
  Component,
  EventEmitter,
  HostListener,
  Input,
  Output,
  SimpleChanges,
} from "@angular/core";
import { FormControl, FormGroup } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { Store } from "@ngrx/store";
import { Observable, map, of, switchMap, take } from "rxjs";
import {
  ProviderLastMessageUpdateActions,
  ProviderMarkConversationAsReadActions,
  ProviderOfferingConversationsFetchActions,
  ProviderRelationStatusUpdateActions,
} from "src/app/store/offerings/user-offerings/user-offerings.actions";
import {
  selectOfferingChatData,
  selectOfferingChatEvents,
  selectOfferingChatLastMessages,
} from "src/app/store/offerings/user-offerings/user-offerings.selector";
import { Message } from "src/shared/models/message.model";
import { OfferingStatus } from "src/shared/models/offering-status.model";
import { Offering } from "src/shared/models/offering.model";
import { Relation } from "src/shared/models/relation.model";
import { User } from "src/shared/models/user.model";
import { MatSelectChange } from "@angular/material/select";
import { RelationService } from "src/app/services/relationService/relation.service";
import { OfferingService } from "src/app/services/offeringService/offering.service";
import { Event } from "src/shared/models/event.model";
import { URI } from "src/shared/types";
import { EnumService } from "src/app/services/enumService/enum.service";

const enum Status {
  CURRENT = "CURRENT",
  PAST = "PAST",
}

@Component({
  selector: "offerings-chat",
  templateUrl: "./offerings-chat.component.html",
  styleUrls: ["./offerings-chat.component.scss"],
})
export class OfferingsChatComponent {
  @Input({ required: true }) offering!: Offering;
  @Input({ required: true }) loggedUser!: User;

  @Input() hideStatus = false;
  @Input() onlyDone = false;

  @Output() relationAccepted = new EventEmitter<Relation>();
  @Output() relationRejected = new EventEmitter<Relation>();

  @Output() exitChat = new EventEmitter<void>();

  Status = {
    CURRENT: $localize`Show current`,
    PAST: $localize`Show past`,
  };

  constructor(
    private store: Store,
    private route: ActivatedRoute,
    private router: Router,
    private enumService: EnumService
  ) {
    this.chatStatusForm = new FormGroup({
      status: new FormControl(Status.CURRENT),
    });
  }

  ngOnInit() {
    this.chatStatusForm.get("status")?.valueChanges.subscribe((value) => {
      this.currentRelation = undefined;
      if (
        this.offering.self ===
        OfferingService.getUriFromId(this.route.snapshot.queryParams["service"])
      ) {
        this.fetchConversations();
      }
    });

    this.route.queryParams
      .pipe(
        switchMap((params) => {
          if (params["conversation"]) {
            if (params["status"]) {
              if (params["status"].toUpperCase() in this.Status) {
                if (
                  this.chatStatusForm.get("status")?.value !=
                  params["status"].toUpperCase()
                ) {
                  this.chatStatusForm
                    .get("status")
                    ?.setValue(params["status"].toUpperCase());
                }
              }
            } else {
              if (this.chatStatusForm.get("status")?.value != Status.CURRENT) {
                this.chatStatusForm.get("status")?.setValue(Status.CURRENT);
              }
            }
            return this.chatData$.pipe(
              map((chatData) =>
                chatData?.relations?.relationsByURIMap.get(
                  RelationService.getUriFromId(params["conversation"])
                )
              )
            );
          } else {
            return of(undefined);
          }
        })
      )
      .subscribe((selectedRelation) => {
        this.currentRelation = selectedRelation;
      });

    this.chatData$.subscribe(
      (chatData) =>
        (this.sortedRelations$ = this.sortRelations(
          chatData?.relations?.relations ?? []
        ))
    );
  }

  sortedRelations$: Observable<Relation[]> = of([]);
  lastMessagesByConversation$ = this.store.select(
    selectOfferingChatLastMessages
  );
  chatData$ = this.store.select(selectOfferingChatData);
  events$ = this.store.select(selectOfferingChatEvents);

  currentRelation?: Relation;

  OfferingStatus = OfferingStatus;

  chatStatusForm: FormGroup;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes["offering"]) {
      this.fetchConversations();
    }
  }

  changeStatus(change: MatSelectChange) {
    this.router.navigate([], {
      queryParamsHandling: "merge",
      queryParams: { status: change.value.toString().toLowerCase() },
      replaceUrl: true,
    });
  }

  fetchConversations() {
    this.store.dispatch(
      ProviderOfferingConversationsFetchActions.fetchOfferingProviderConversations(
        {
          offering: this.offering,
          past: this.chatStatusForm.get("status")?.value == Status.PAST,
        }
      )
    );
  }

  @HostListener("document:keydown.escape", ["$event"])
  onKeydownHandler(event: KeyboardEvent) {
    this.persistToURL(undefined);
  }

  selectRelation(relation: Relation) {
    this.persistToURL(relation);
  }

  updateLastMessage(message: Message) {
    this.store.dispatch(
      ProviderLastMessageUpdateActions.updateProviderLastMessage({ message })
    );
  }

  markConversationAsRead(relation: Relation) {
    this.store.dispatch(
      ProviderMarkConversationAsReadActions.markConversationAsRead({ relation })
    );
  }

  acceptRelation(relation: Relation) {
    this.store.dispatch(
      ProviderRelationStatusUpdateActions.acceptProviderRelation({
        uri: relation.self,
      })
    );
  }

  rejectRelation(relation: Relation) {
    this.store.dispatch(
      ProviderRelationStatusUpdateActions.rejectProviderRelation({
        uri: relation.self,
      })
    );
  }

  relationIsPending(relation: Relation): boolean {
    return relation.status == "PENDING";
  }

  persistToURL(relation?: Relation) {
    const queryParams = relation
      ? { conversation: relation.relationId }
      : { conversation: null };
    this.router.navigate([], {
      queryParamsHandling: "merge",
      queryParams: queryParams,
      replaceUrl: true,
    });
  }

  isEventDateOccupied(event: Event, offering: URI): Observable<boolean> {
    return this.chatData$.pipe(
      take(1),
      map((chatData) => {
        const relationsForOffering = chatData?.relations?.relations.filter(
          (relation) => relation.offering == offering
        );
        const eventsForOfferingDates = chatData?.events?.events
          .filter((event) =>
            relationsForOffering?.some(
              (relation) =>
                relation.event == event.self && relation.status == "ACCEPTED"
            )
          )
          .map((event) => event.date);
        return !!eventsForOfferingDates?.some(
          (date) =>
            RelationService.getStartOf(date).toISOString() ===
            RelationService.getStartOf(event.date).toISOString()
        );
      })
    );
  }

  sortRelations(relations: Relation[]): Observable<Relation[]> {
    return this.lastMessagesByConversation$.pipe(
      take(1),
      map((lastMessagesByConversation) => {
        return relations.sort((a, b) => {
          const aTimestamp =
            lastMessagesByConversation?.get(a.self)?.timestamp ?? 0;
          const bTimestamp =
            lastMessagesByConversation?.get(b.self)?.timestamp ?? 0;
          const aDate = new Date(aTimestamp);
          const bDate = new Date(bTimestamp);
          return bDate.getTime() - aDate.getTime();
        });
      })
    );
  }

  getDistrictValue(district: string): Observable<string | undefined> {
    return this.enumService.getDistrictValue(district);
  }
}
