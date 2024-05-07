import { Component, ElementRef, EventEmitter, Input, Output, SimpleChanges, ViewChild } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MessageService } from 'src/app/services/messageService/message.service';
import { UserService } from 'src/app/services/userService/user.service';
import { ScrollDirective } from 'src/shared/directives/scroll.directive';
import { Message, Messages } from 'src/shared/models/message.model';
import { Offering } from 'src/shared/models/offering.model';
import { Relation } from 'src/shared/models/relation.model';
import { User } from 'src/shared/models/user.model';

@Component({
  selector: 'messages-container',
  templateUrl: './messages-container.component.html',
  styleUrls: ['./messages-container.component.scss']
})
export class MessagesContainerComponent {
  @Input({ required: true }) offering!: Offering;
  @Input({ required: true }) relation!: Relation;
  @Input({ required: true }) currentUser?: User;

  @Output() onMessageSent = new EventEmitter<Message>();
  @Output() markAsRead = new EventEmitter<Relation>();

  @ViewChild('messagesArea') messagesArea!: ElementRef;
  @ViewChild(ScrollDirective) scrollDirective!: ScrollDirective;


  constructor(private messageService: MessageService) {
  }

  messages!: Messages;

  loading = true;

  ngAfterViewChecked(): void {
    if (this.justSentMessage) {
      this.scrollDirective.reset();
      this.justSentMessage = false;
    }
  }

  ngAfterViewInit(): void {
    this.fetchMessages();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['relation'] && this.scrollDirective) {
      this.scrollDirective.reset();
      this.fetchMessages();
    }
  }

  fetchMoreMessages() {
    if (this.messages.links.getNextLink()) {
      this.loading = true;
      this.messageService.getMessages(this.messages.links.getNextLink()!, this.messages).subscribe(messages => {
        this.scrollDirective.prepareFor('up');
        this.messages = messages;
        setTimeout(() => {
          this.scrollDirective.restore();
          this.loading = false;
        });
      });
    }
  }

  fetchMessages() {
    this.loading = true;
    this.messageService.getMessages(this.relation?.messages).subscribe(messages => {
      this.messages = messages;
      setTimeout(() => {
        this.scrollDirective.reset();
        this.loading = false;
      });
      if ((this.relation.provider == this.currentUser?.self && this.relation.providerUnreadMessagesCount > 0)
        || (this.relation.organizer == this.currentUser?.self && this.relation.organizerUnreadMessagesCount > 0)) {
        this.markAsRead.emit(this.relation);
      }
    });
  }

  messageControl = new FormControl('', [Validators.required]);
  justSentMessage = false;

  sendMessage() {
    if (!this.messageControl.valid) return;
    const message: Message = {
      self: "",
      relation: this.relation.self,
      sender: this.currentUser!.self,
      receiver: "",
      message: this.messageControl.value!,
      timestamp: new Date(),
      isRead: false
    }
    this.messageService.sendMessage(message).subscribe(message => {
      this.messages.addMessage(message);
      this.justSentMessage = true;
      this.onMessageSent.emit(message);
    });
    this.messageControl.reset();
  }

}
