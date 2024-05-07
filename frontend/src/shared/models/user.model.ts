import { URI } from "../types";

export interface User extends UserUpdate {
  userId: number;
  averageRating: number;
  totalLikes: number;
  totalEventsWorkedOn: number;
  self: URI;
  createdEvents: URI;
  createdOfferings: URI;
  providerRelations: URI;
  occupiedDates: URI;
}

export interface UserUpdate {
  name: string;
  email: string;
  language: string;
  description: string;
  provider: boolean;
  profilePicture: URI | null;
  password?: string | null;
}

export interface UserPasswordUpdate {
  password?: string;
}
