import { URI } from "../types";
import { Event, Events } from "./event.model";
import { OfferingStatus } from "./offering-status.model";
import { Offering, Offerings } from "./offering.model";
import { Links } from "./pagination-utils.model";

export class Relations {
  private relationsByURI: Map<URI, Relation>;
  links: Links;

  constructor(relations: Relation[], links: Links) {
    this.links = links;

    this.relationsByURI = new Map<URI, Relation>();
    relations.forEach(relation => {
      this.relationsByURI!.set(relation.self, relation);
    });
  }

  updateRelation(relation: Relation): Relations {
    this.relationsByURI!.set(relation.self, relation);
    return this;
  }

  removeRelation(relation: Relation) {
    this.relationsByURI!.delete(relation.self);
  }

  get relationsByURIMap(): Map<URI, Relation> {
    return this.relationsByURI;
  }

  get relations(): Relation[] {
    return Array.from(this.relationsByURI.values());
  }

}

export interface Relation {
  relationId: number;
  status: keyof typeof OfferingStatus;
  self: URI;
  event: URI;
  offering: URI;
  review?: URI;
  lastMessage?: URI;
  messages: URI;
  organizer: URI;
  provider: URI;
  providerUnreadMessagesCount: number;
  organizerUnreadMessagesCount: number;
}

export class RelationStatusChange {
  status: keyof typeof OfferingStatus;

  constructor(status: keyof typeof OfferingStatus) {
    this.status = status;
  }
}

export interface RelationsFilter {
  status?: (keyof typeof OfferingStatus)[];
  offering?: number;
  from?: string;
  to?: string;
}

export class RelationReadStatus {
  user: URI;
  read: boolean;

  constructor(user: URI, read: boolean) {
    this.user = user;
    this.read = read;
  }
}