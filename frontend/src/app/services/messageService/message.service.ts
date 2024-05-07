import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserService } from '../userService/user.service';
import { Observable, catchError, forkJoin, map, of, switchMap, throwError } from 'rxjs';
import { Message, Messages } from 'src/shared/models/message.model';
import { URI } from 'src/shared/types';
import { Links, parseLinks } from 'src/shared/models/pagination-utils.model';
import { VndType } from 'src/shared/VndType';
import { User } from 'src/shared/models/user.model';
import { Offering, ProviderChatData } from 'src/shared/models/offering.model';
import { RelationService } from '../relationService/relation.service';
import { EventService } from '../eventService/event-service.service';
import { Relation, RelationReadStatus, Relations, RelationsFilter } from 'src/shared/models/relation.model';
import { Event, Events } from 'src/shared/models/event.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  conversatioUrl = `${environment.apiUrl}/conversations`;

  constructor(private http: HttpClient, private userService: UserService,
    private relationService: RelationService,
    private eventService: EventService
  ) {
  }

  // Messages
  getMessages(uri: URI, previousMessages?: Messages): Observable<Messages> {
    const headers = new HttpHeaders().set('Accept', VndType.APPLICATION_MESSAGES);

    return this.http.get<Message[]>(uri, { headers, observe: 'response' }).pipe(
      map(response => {
        const messages: Message[] = response.body || [];
        const linksHeader = response.headers.get('Link');
        const links = linksHeader ? parseLinks(linksHeader) : {};
        if (previousMessages) {
          previousMessages.addMessages(messages, new Links(links));
          return previousMessages;
        }
        return new Messages(messages, new Links(links));
      })
    );
  }

  getMessage(uri: URI): Observable<Message> {
    const headers = new HttpHeaders().set('Accept', VndType.APPLICATION_MESSAGE);

    return this.http.get<Message>(uri, { headers });
  }

  sendMessage(message: Message): Observable<Message> {
    const headers = new HttpHeaders().set('Content-Type', VndType.APPLICATION_MESSAGE);
    return this.http.post(message.relation + "/messages", message, { headers, observe: 'response' }).pipe(
      switchMap(response => {
        const location = response.headers.get('Location');
        if (location) {
          return this.getMessage(location);
        } else {
          throw new Error('Location header not found');
        }
      })
    );
  }

  markConversationAsRead(relation: Relation, currentUser: URI): Observable<Relation> {
    const headers = new HttpHeaders().set('Accept', VndType.APPLICATION_RELATION)
      .set('Content-Type', VndType.APPLICATION_RELATION_READ_STATUS);

    const status = new RelationReadStatus(currentUser, true);
    return this.http.patch<Relation>(relation.self, status, { headers });
  }

  getLastMessagesByRelations(relations: Relation[]): Observable<Map<URI, Message>> {
    const lastMessagesByConversation = new Map<URI, Message>();
    return forkJoin(relations
      .filter(relation => !!relation.lastMessage).map(relation => {
        return this.getMessage(relation.lastMessage!).pipe(
          map(message => {
            lastMessagesByConversation.set(relation.self, message);
          })
        );
      })
    ).pipe(
      map(() => lastMessagesByConversation)
    );
  }

  getProviderChatData(uri: URI, offering: Offering, filter?: RelationsFilter): Observable<ProviderChatData> {
    if (!filter) {
      filter = {
        offering: offering.id
      };
    } else {
      filter.offering = offering.id;
    }
    return this.relationService.getAllRelations(uri, filter).pipe(
      switchMap(relations => {
        if (relations.relations.length === 0) {
          return of({
            offering: offering,
            relations: relations,
            events: new Events([], new Links({})),
            lastMessagesByRelation: new Map<URI, Message>()
          });
        }
        return forkJoin([
          this.eventService.getEventsByURIs(relations.relations.map(relation => relation.event)),
          this.getRelationsWithLastMessages(relations.relations).pipe(
            map(conversationsWithLastMessages => {
              return {
                relations: relations,
                lastMessagesByRelation: conversationsWithLastMessages.lastMessagesByUri
              };
            })
          )
        ]).pipe(
          map(([events, relations]) => {
            return {
              offering: offering,
              events: new Events(events, new Links({})),
              ...relations
            };
          })
        );
      })
    );
  }

  getRelationsWithLastMessages(relations: Relation[]): Observable<{ relations: Relation[], lastMessagesByUri: Map<URI, Message> }> {
    return this.getLastMessagesByRelations(relations).pipe(
      map(lastMessagesByUri => {
        return {
          relations: relations,
          lastMessagesByUri: lastMessagesByUri
        };
      })
    );
  }
}
