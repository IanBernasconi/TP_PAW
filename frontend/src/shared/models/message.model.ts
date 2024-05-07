import { URI } from "../types";
import { Links } from "./pagination-utils.model";


export class Messages {
    messages: Message[];
    private messagesByDate?: Map<string, Message[]>;

    links: Links;


    constructor(messages: Message[], links: Links) {
        this.messages = messages;
        this.links = links;
    }

    get messagesByDateMap(): Map<string, Message[]> {
        if (!this.messagesByDate) {
            this.messagesByDate = new Map();
            for (let message of this.messages) {
                const date = new Date(message.timestamp);
                date.setHours(0, 0, 0, 0);
                const dateString = date.toISOString().split('T')[0];
                if (!this.messagesByDate.has(dateString)) {
                    this.messagesByDate.set(dateString, []);
                }
                this.messagesByDate.get(dateString)?.push(message);
            }
            for (let messages of this.messagesByDate.values()) {
                messages.sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime());
            }

            this.messagesByDate = new Map([...this.messagesByDate.entries()].sort((a, b) => new Date(a[0]).getTime() - new Date(b[0]).getTime()));
        }

        return this.messagesByDate;
    }

    addMessages(messages: Message[], links: Links) {
        const messageSet = new Set(this.messages.map(message => message.self));
        messages.forEach(newMessage => {
            if (!messageSet.has(newMessage.self)) {
                this.messages.push(newMessage);
                messageSet.add(newMessage.self);
            }
        });

        this.links = links;
        this.messagesByDate = undefined;
    }

    addMessage(message: Message) {
        this.messages.push(message);
        const date = new Date(message.timestamp);
        date.setHours(0, 0, 0, 0);
        const dateString = date.toISOString().split('T')[0];
        if (!this.messagesByDate) {
            this.messagesByDate = new Map();
        }
        if (!this.messagesByDate.has(dateString)) {
            this.messagesByDate.set(dateString, []);
        }
        this.messagesByDate.get(dateString)?.push(message);
    }
}

export interface Message {
    self: URI;
    relation: URI;
    sender: URI;
    receiver: URI;
    message: string;
    timestamp: Date;
    isRead: boolean;
}