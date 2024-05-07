package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.eventOfferingRelation.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {

    Review createReview(long relationId, int rating, String content);

    Optional<Review> getReviewById(long relationId);

    List<Review> getReviews(Long eventId, Long offeringId, int page, int pageSize);

    int getReviewsCount(Long eventId, Long offeringId);


}
