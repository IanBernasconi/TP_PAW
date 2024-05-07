import { URI } from "../types";
import { Offering } from "./offering.model";
import { Links } from "./pagination-utils.model";
import { Relation } from "./relation.model";
import { Review } from "./review.model";
import { User } from "./user.model";
import { Message } from "./message.model";
import { District, OfferingCategory } from "./utils.model";

export class Events {
  events: Event[];
  eventsByURI: Map<URI, Event>;
  links: Links;

  constructor(events: Event[], links: Links) {
    this.events = events;
    this.links = links;
    this.eventsByURI = new Map();
    for (let event of events) {
      this.eventsByURI.set(event.self, event);
    }
  }
}

export class EventToCreate {
  name: string;
  description: string;
  date: string;
  numberOfGuests: number;
  district: keyof District;
  owner: URI;

  constructor(name: string, description: string, date: string, numberOfGuests: number, district: keyof District, owner: URI) {
    this.name = name;
    this.description = description;
    this.date = date;
    this.numberOfGuests = numberOfGuests;
    this.district = district;
    this.owner = owner;
  }
}


export class Event extends EventToCreate {
  id: number;
  self: URI;
  relations: URI;
  relatedOfferings: URI;
  reviews: URI;
  guests: URI;

  constructor(id: number, name: string, description: string, date: string, numberOfGuests: number, self: string, owner: string, district: keyof District, relations: URI, relatedOfferings: URI, reviews: URI, guests: URI) {
    super(name, description, date, numberOfGuests, district, owner);
    this.id = id;
    this.self = self;
    this.relations = relations;
    this.relatedOfferings = relatedOfferings;
    this.reviews = reviews;
    this.guests = guests;
  }
}


export class EventRelated {
  event: Event;

  relationsByURI: Map<URI, RelationInfo>;
  relationsByCategory: Map<keyof OfferingCategory, URI[]>;

  recommendations?: Offering[];

  constructor(event: Event, relations: RelationInfo[], recommendations?: Offering[]) {
    this.event = event;
    this.recommendations = recommendations;

    this.relationsByCategory = new Map();
    this.relationsByURI = new Map();
    for (let relation of relations) {
      this.relationsByURI.set(relation.relation.self, relation);
      let category = relation.offering.category;
      if (!this.relationsByCategory.has(category)) {
        this.relationsByCategory.set(category, []);
      }
      this.relationsByCategory.get(category)?.push(relation.relation.self);
    }

    for (let category of defaultOfferingCategoriesForEvent) {
      if (!this.relationsByCategory.has(category as keyof OfferingCategory)) {
        this.relationsByCategory.set(category as keyof OfferingCategory, []);
      }
    }
  }

  removeRecommendation(uri: URI): EventRelated {
    let recommendationsCopy = this.recommendations?.slice() || [];
    recommendationsCopy = recommendationsCopy.filter((offering) => offering.self !== uri);
    return new EventRelated(this.event, this.relations, recommendationsCopy);
  }

  updateRelation(relation: Relation): EventRelated {
    let relationsCopy = this.relations.slice();
    let relationInfo = relationsCopy.find((relationInfo) => relationInfo.relation.self === relation.self);
    if (relationInfo) {
      relationInfo.relation = relation;
    }
    return new EventRelated(this.event, relationsCopy, this.recommendations);
  }

  removeRelation(uri: URI): EventRelated {
    let relationsCopy = this.relations.slice();
    relationsCopy = relationsCopy.filter((relationInfo) => relationInfo.relation.self !== uri);
    return new EventRelated(this.event, relationsCopy, this.recommendations);
  }

  addRelation(relation: Relation, offering: Offering, provider: User): EventRelated {
    let relationsCopy = this.relations.slice();
    relationsCopy.push({
      relation: relation,
      offering: offering,
      review: null,
      provider: provider,
      alreadyReviewed: false
    });
    return new EventRelated(this.event, relationsCopy, this.recommendations);
  }

  updatedEvent(event: Event): EventRelated {
    return new EventRelated(event, this.relations, this.recommendations);
  }

  addReview(review: Review): EventRelated {
    let relationsCopy = this.relations.slice();
    let relation = relationsCopy.find((relation) => relation.relation.self === review.relation);
    if (relation) {
      relation.review = review;
      relation.alreadyReviewed = true;
    }
    return new EventRelated(this.event, relationsCopy, this.recommendations);
  }

  updateLastMessages(messages: Map<URI, Message>): EventRelated {
    let relationsCopy = this.relations.slice();
    for (let [uri, message] of messages.entries()) {
      let relation = relationsCopy.find((relation) => relation.relation.self === uri);
      if (relation) {
        relation.lastMessage = message;
      }
    }
    return new EventRelated(this.event, relationsCopy, this.recommendations);
  }

  setRecommendations(recommendations: Offering[]): EventRelated {
    const copy = new EventRelated(this.event, this.relations);
    copy.recommendations = recommendations;
    return copy;
  }

  updateLastMessage(message: Message): EventRelated {
    let relationsCopy = this.relations.slice();
    let relation = relationsCopy.find((relation) => relation.relation.self === message.relation);
    if (relation) {
      relation.lastMessage = message;
    }
    return new EventRelated(this.event, relationsCopy, this.recommendations);
  }

  markRelationAsRead(relation: Relation): EventRelated {
    let relationsCopy = this.relations.slice();
    let relationCopy = relationsCopy.find((relationInfo) => relationInfo.relation.self === relation.self);
    if (relationCopy) {
      relationCopy.relation = relation;
    }
    return new EventRelated(this.event, relationsCopy, this.recommendations);
  }

  get relations(): RelationInfo[] {
    return Array.from(this.relationsByCategory.values()).flat()
      .map(uri => this.relationsByURI.get(uri)) as RelationInfo[];
  }
}

export class EventWithOffering extends Event {
  offeringsByURI?: Map<URI, Offering>;

  constructor(event: Event, offerings: Offering[]) {
    super(event.id, event.name, event.description, event.date, event.numberOfGuests, event.self, event.owner, event.district, event.relations, event.relatedOfferings, event.reviews, event.guests);

    this.offeringsByURI = new Map();
    for (let offering of offerings) {
      this.offeringsByURI.set(offering.self, offering);
    }
  }
}

export interface RelationInfo {
  relation: Relation;
  offering: Offering;
  review: Review | null;
  lastMessage?: Message;
  provider: User;
  alreadyReviewed: boolean;
}

export const defaultOfferingCategoriesForEvent = ["PHOTOGRAPHY", "VENUE", "CATERING", "MUSIC"];

export interface RangeFilter {
  from?: string | null; // Initial date. ISO 8601 format.
  to?: string | null; // Final date. ISO 8601 format.

}