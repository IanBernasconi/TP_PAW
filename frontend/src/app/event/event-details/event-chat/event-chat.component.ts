import { Component, EventEmitter, HostListener, Input, Output, SimpleChanges } from '@angular/core';
import { Message } from 'src/shared/models/message.model';
import { Event, RelationInfo } from 'src/shared/models/event.model';

import { OfferingStatus } from 'src/shared/models/offering-status.model';
import { User } from 'src/shared/models/user.model';
import { Relation } from 'src/shared/models/relation.model';
import { ActivatedRoute, Router } from '@angular/router';
import { RelationService } from 'src/app/services/relationService/relation.service';
import { Observable, Subject, delay, filter, takeUntil } from 'rxjs';

@Component({
  selector: 'event-chat',
  templateUrl: './event-chat.component.html',
  styleUrls: ['./event-chat.component.scss']
})
export class EventChatComponent {
  @Input({ required: true }) event!: Event;
  @Input({ required: true }) relations!: RelationInfo[];
  @Input({ required: true }) loggedUser!: User;
  @Input({ required: true }) selected?: Observable<boolean>;
  @Input({ required: true }) active: boolean = true;

  @Output() markConversationAsRead = new EventEmitter<Relation>();
  @Output() updateLastMessage = new EventEmitter<Message>();

  constructor(private route: ActivatedRoute, private router: Router) { }

  currentRelation?: RelationInfo;

  OfferingStatus = OfferingStatus;

  destroyed$ = new Subject<void>();

  filteredRelations: RelationInfo[] = [];

  ngOnInit() {
    let relationId = this.route.snapshot.queryParams['conversation'];
    if (relationId) {
      this.currentRelation = this.relations.find(relation => relation.relation.self === RelationService.getUriFromId(relationId));
    } else {
      this.currentRelation = undefined;
    }
    this.selected?.pipe(
      takeUntil(this.destroyed$),
      filter(selected => selected),
      delay(10)
    ).subscribe(() => {
      this.persistToURL(this.currentRelation);
    })
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes["relations"]) {
      this.filteredRelations = this.relations.filter(relationInfo => !relationInfo.offering.deleted &&
        (relationInfo.relation.status === "ACCEPTED" || relationInfo.relation.status === "PENDING"));
    }
  }

  @HostListener('document:keydown.escape', ['$event'])
  onKeydownHandler(event: KeyboardEvent) {
    this.currentRelation = undefined;
    this.persistToURL();
  }

  selectRelation(relation: RelationInfo) {
    this.currentRelation = relation;
    this.persistToURL(relation);
  }

  persistToURL(relation?: RelationInfo) {
    if (relation) {
      this.router.navigate([], {
        queryParamsHandling: 'merge',
        queryParams: {
          conversation: relation.relation.relationId
        },
        replaceUrl: true
      });
    } else {
      this.router.navigate([], {
        queryParamsHandling: 'merge',
        queryParams: { conversation: null },
        replaceUrl: true
      });
    }
  }

  ngOnDestroy() {
    this.destroyed$.next();
    this.destroyed$.complete();
  }

}
