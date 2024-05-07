import { Component, Input, SimpleChanges } from '@angular/core';
import { Message } from 'src/shared/models/message.model';

@Component({
  selector: 'chat-selector-card',
  templateUrl: './chat-selector-card.component.html',
  styleUrls: ['./chat-selector-card.component.scss']
})
export class ChatSelectorCardComponent {
  @Input({ required: true }) name!: string;
  @Input({ required: true }) lastMessage!: Message;
  @Input({ required: true }) unreadMessages!: number;

  isToday(timestamp: Date) {
    var today = new Date();
    var messageDate = new Date(timestamp);

    return (today.setHours(0, 0, 0, 0) === messageDate.setHours(0, 0, 0, 0));
  };
}
