import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, catchError, map, of, switchMap, throwError } from "rxjs";
import { environment } from "src/environments/environment";
import { VndType } from "src/shared/VndType";
import { Links, parseLinks } from "src/shared/models/pagination-utils.model";
import { Review, Reviews } from "src/shared/models/review.model";
import { URI } from "src/shared/types";

@Injectable({
  providedIn: "root",
})
export class ReviewService {
  apiURL = `${environment.apiUrl}/reviews`;

  constructor(private http: HttpClient) { }

  createReview(review: Review): Observable<Review> {
    const headers = new HttpHeaders().set(
      "Content-Type",
      VndType.APPLICATION_REVIEW
    );

    return this.http
      .post(this.apiURL, review, { headers, observe: "response" })
      .pipe(
        switchMap((response) => {
          if (response.headers.get("Location")) {
            return this.getReview(response.headers.get("Location")!);
          }
          throw new Error("Location header is null");
        })
      );
  }

  getReviews(uri: URI, pageSize: number = 5): Observable<Reviews> {
    const headers = new HttpHeaders().set(
      "Accept",
      VndType.APPLICATION_REVIEWS
    );

    let params = new HttpParams({ fromString: new URL(uri).searchParams.toString() });
    params = params.set("pageSize", pageSize.toString());

    let uriWithoutParams = uri.split('?')[0];

    return this.http
      .get<Review[]>(uriWithoutParams, { params, headers, observe: "response" })
      .pipe(
        map((response) => {
          const reviews: Review[] = response.body || [];
          const linksHeader = response.headers.get("Link");
          const links = linksHeader ? parseLinks(linksHeader) : {};

          return new Reviews(reviews, new Links(links));
        })
      );
  }

  getAllReviews(uri: URI, reviews: Reviews[] = []): Observable<Reviews> {
    return this.getReviews(uri, 100).pipe(
      switchMap((currentReviews) => {
        reviews.push(currentReviews);
        let next = currentReviews.links.getNextLink();
        if (next) {
          return this.getAllReviews(next, reviews);
        } else {
          let links = new Links({});
          if (reviews.length > 0) {
            if (reviews[0].links.getFirstLink()) {
              links.setFirstLink(reviews[0].links.getFirstLink()!);
            }
            if (reviews[reviews.length - 1].links.getLastLink()) {
              links.setLastLink(
                reviews[reviews.length - 1].links.getLastLink()!
              );
            }
          }
          return of(
            new Reviews(
              reviews.flatMap((reviews) => reviews.reviews),
              links
            )
          );
        }
      })
    );
  }

  getReview(uri: URI): Observable<Review> {
    const headers = new HttpHeaders().set("Accept", VndType.APPLICATION_REVIEW);

    return this.http.get<Review>(uri, { headers });
  }
}
