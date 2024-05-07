import { Component, EventEmitter, HostListener, Input, Output, SimpleChanges } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subject, delay, filter, map, of, switchMap, take, takeUntil, tap } from 'rxjs';
import { OfferingService } from 'src/app/services/offeringService/offering.service';
import { Offering, Offerings } from 'src/shared/models/offering.model';
import { Relation, Relations } from 'src/shared/models/relation.model';
import { User } from 'src/shared/models/user.model';

@Component({
  selector: 'chat-list-component',
  templateUrl: './chat-list-component.component.html',
  styleUrls: ['./chat-list-component.component.scss']
})
export class ChatListComponentComponent {
  @Input({ required: true }) offerings$!: Observable<Offerings>;
  @Input({ required: true }) loggedUser!: User;
  @Input({ required: true }) conversationData?: Map<string, Relations>;
  @Input() isLoading: boolean = false;
  @Input({ required: true }) selected?: Observable<boolean>;


  @Output() changeToNextPage = new EventEmitter<Event>();
  @Output() changeToPreviousPage = new EventEmitter<Event>();

  cols: number = 4; // Number of columns for the grid list

  mockOfferings = Array(this.cols).fill(0).map((x, i) => i); // Dummy array to iterate over in the template

  constructor(private route: ActivatedRoute, private router: Router) { }

  destroyed$ = new Subject<void>();

  selectedOffering?: Offering;

  ngOnInit(): void {
    this.setCols();
    this.route.queryParams.pipe(
      takeUntil(this.destroyed$),
      switchMap(params => {
        if (params['service']) {
          return this.offerings$.pipe(
            map(offerings => offerings.offeringsByURI.get(OfferingService.getUriFromId(params['service'])))
          );
        } else {
          return of(undefined);
        }
      })
    ).subscribe(selectedOffering => {
      this.selectedOffering = selectedOffering;
    });
  }

  persistToURL(offering?: Offering) {
    if (offering) {
      this.router.navigate([], {
        queryParamsHandling: 'merge',
        queryParams: { service: offering.id },
        replaceUrl: true
      });
    } else {
      this.router.navigate([], {
        queryParamsHandling: 'merge',
        queryParams: { service: null, conversation: null, status: null },
        replaceUrl: true
      });
    }
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any): void {
    this.setCols();
  }

  private setCols(): void {
    this.cols = Math.round(window.innerWidth / 300);
    this.mockOfferings = Array(this.cols).fill(0).map((x, i) => i);
  }

  navigateToChat(offering: Offering) {
    this.selectedOffering = offering;
    this.persistToURL(offering);
  }

  exitChat() {
    this.persistToURL(undefined);
  }

  getRelationsPendingCount(relations: Relations): number {
    return relations.relations.filter(relation => relation.status === 'PENDING').length;
  }

  getRelationsAcceptedCount(relations: Relations): number {
    return relations.relations.filter(relation => relation.status === 'ACCEPTED').length;
  }

  getRelationsTotalUnreadSum(relations: Relations): number {
    return relations.relations.reduce((acc, relation) => acc + relation.providerUnreadMessagesCount, 0);
  }

  getUnreadCount(offering: Offering): number | null {
    const conversationData = this.conversationData?.get(offering.self);
    return conversationData ? this.getRelationsTotalUnreadSum(conversationData) : null;
  }

  hasUnreadMessages(offering: Offering): boolean {
    const conversationData = this.conversationData?.get(offering.self);
    return conversationData ? this.getRelationsTotalUnreadSum(conversationData) > 0 : false;
  }
}
