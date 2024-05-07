import { URI } from "../types";
import { Links } from "./pagination-utils.model";

export const GuestStatus = {
  NEW: $localize`New`,
  PENDING: $localize`Pending`,
  ACCEPTED: $localize`Accepted`,
  REJECTED: $localize`Rejected`,
};

export interface Guest extends GuestStatusUpdate {
  email: string;
  self?: URI;
}

export interface GuestStatusUpdate {
  status: keyof typeof GuestStatus;
  token?: string;
}

export class Guests {
  guests: Guest[];
  links: Links;

  constructor(guests: Guest[], links: Links) {
    this.guests = guests;
    this.links = links;
  }
}
