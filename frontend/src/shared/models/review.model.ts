import { URI } from "../types";
import { Links, linkTypes } from "./pagination-utils.model";


export class Reviews {
    reviews: Review[];
    reviewByURI: Map<URI, Review>;
    links: Links;

    constructor(reviews: Review[], links: Links) {
        this.reviews = reviews;
        this.links = links;
        this.reviewByURI = new Map();
        for (let review of reviews) {
            this.reviewByURI.set(review.self, review);
        }
    }
}

export class Review {
    review: string;
    rating: number;
    date: string;
    self: URI;
    relation: URI;

    constructor(review: string, rating: number, date: string, self: URI, relation: URI) {
        this.review = review;
        this.rating = rating;
        this.date = date;
        this.self = self;
        this.relation = relation;
    }
}