import { URI } from '../types';
import { Message } from './message.model';
import { Events } from './event.model';
import { Links } from './pagination-utils.model';
import { Relations } from './relation.model';
import { Reviews } from './review.model';
import { User } from './user.model';
import { District, OfferingCategory } from './utils.model';

export enum SortTypeKeys {
  RATING_DESC = "RATING_DESC",
  REVIEW_COUNT_DESC = "REVIEW_COUNT_DESC",
  POPULARITY_DESC = "POPULARITY_DESC",
}

export const SortType = {
  RATING_DESC: $localize`Highest rating`,
  REVIEW_COUNT_DESC: $localize`Most reviewed`,
  POPULARITY_DESC: $localize`Most popular`,
}

export const offeringCategoryPriceTypes = new Map([
  ["ALL", "OTHER"],
  ["PHOTOGRAPHY", "PER_EVENT"],
  ["VIDEO", "PER_EVENT"],
  ["MUSIC", "PER_EVENT"],
  ["CATERING", "PER_PERSON"],
  ["DECORATION", "PER_EVENT"],
  ["FLOWERS", "PER_EVENT"],
  ["VENUE", "PER_DAY"],
  ["OTHER", "OTHER"],
]);



export class Offerings {
  offerings: Offering[];
  offeringsByURI: Map<URI, Offering>;
  links: Links;

  constructor(offerings: Offering[], links: Links) {
    this.offerings = offerings;
    this.links = links;
    this.offeringsByURI = new Map();
    for (let offering of offerings) {
      this.offeringsByURI.set(offering.self, offering);
    }
  }
}

export class OfferingsWithOwners extends Offerings {
  ownersByUri?: Map<URI, User>;

  constructor(offerings: Offering[], links: Links, ownersByUri?: Map<URI, User>) {
    super(offerings, links);
    this.ownersByUri = ownersByUri;
  }

  setOwnersByUri(ownersByUri: Map<URI, User>): OfferingsWithOwners {
    return new OfferingsWithOwners(this.offerings, this.links, ownersByUri);
  }
}


export interface Offering extends OfferingToCreate {
  id?: number;
  deleted: boolean;
  likes: number;
  rating: number;
  self: URI;
  reviews: URI;
}

export interface OfferingToCreate {
  name: string;
  description: string;
  category: keyof OfferingCategory;
  minPrice: number;
  maxPrice: number;
  priceType: string;
  maxGuests: number;
  district: keyof District;
  images: URI[];
  owner: URI;
}

export interface OfferingRelated {
  offering: Offering;
  owner?: User;
  reviews?: Reviews;
  eventDates?: Date[];
  isLiked?: boolean;
  recommendations?: Offering[];
}

export interface ProviderCalendarData {
  relations: Relations;
  events: Events;
  selectedOffering?: Offering;
}

export interface ProviderChatData {
  offering: Offering;
  relations: Relations;
  events: Events;
  lastMessagesByRelation: Map<URI, Message>;
}

export const defaultOfferingFilter = {
  category: "ALL" as keyof OfferingCategory,
  minPrice: 1,
  maxPrice: 1000000,
  attendees: 0,
  districts: ['ALL'],
  likedBy: null,
  sortType: SortTypeKeys.RATING_DESC,
  search: '',
  availableOn: null
};


export class OfferingFilter {
  category: keyof OfferingCategory;
  minPrice: number;
  maxPrice: number;
  attendees: number;
  districts: string[];
  likedBy?: number | null;
  sortType: SortTypeKeys;
  search: string;
  availableOn?: Date | null;

  constructor(category?: keyof OfferingCategory, minPrice?: number, maxPrice?: number, attendees?: number, districts?: string[], likedBy?: number, sortType?: SortTypeKeys, search?: string, availableOn?: Date) {
    this.category = category ?? defaultOfferingFilter.category;
    this.minPrice = minPrice ?? defaultOfferingFilter.minPrice;
    this.maxPrice = maxPrice ?? defaultOfferingFilter.maxPrice;
    this.attendees = attendees ?? defaultOfferingFilter.attendees;
    this.districts = districts ?? defaultOfferingFilter.districts;
    this.likedBy = likedBy ?? defaultOfferingFilter.likedBy;
    this.sortType = sortType ?? defaultOfferingFilter.sortType;
    this.search = search ?? defaultOfferingFilter.search;
    this.availableOn = availableOn ?? defaultOfferingFilter.availableOn;
  }

  validate(): OfferingFilter {
    this.validateMinPrice();
    this.validateMaxPrice();
    this.validateAttendees();
    this.validateSortType();
    if (this.category == "ALL" as keyof OfferingCategory) {
      this.minPrice = defaultOfferingFilter.minPrice;
      this.maxPrice = defaultOfferingFilter.maxPrice;
    }
    return this;
  }

  private validateMinPrice(): number {
    if (this.minPrice < 0) {
      this.minPrice = 0;
    }
    if (this.minPrice > this.maxPrice) {
      this.minPrice = this.maxPrice;
    }
    return this.minPrice;
  }

  private validateMaxPrice(): number {
    if (this.maxPrice < 0) {
      this.maxPrice = 0;
    }
    if (this.maxPrice < this.minPrice) {
      this.maxPrice = this.minPrice;
    }
    return this.maxPrice;
  }

  private validateAttendees(): number {
    if (this.attendees < 0) {
      this.attendees = 0;
    }
    return this.attendees;
  }

  private validateSortType(): string {
    if (!(this.sortType in SortTypeKeys)) {
      this.sortType = defaultOfferingFilter.sortType;
    }
    return this.sortType;
  }

  setSortType(sortType: SortTypeKeys): OfferingFilter {
    this.sortType = sortType;
    return this;
  }
}

export interface OfferingLike {
  self: URI;
  offering: URI;
  user: URI;
  liked: boolean;
}