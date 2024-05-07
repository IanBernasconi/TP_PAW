package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.eventOfferingRelation.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewDao {


    Review createReview(long relation_id, int rating, String content);

    Optional<Review> getReviewById(long relationId);

    List<Review> getReviews(Long eventId, Long offeringId, int page, int pageSize);

    int getReviewsCount(Long eventId, Long offeringId);

}
