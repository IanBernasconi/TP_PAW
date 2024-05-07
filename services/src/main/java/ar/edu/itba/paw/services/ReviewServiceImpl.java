package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.eventOfferingRelation.Review;
import ar.edu.itba.paw.persistence.ReviewDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService{

    @Autowired
    private ReviewDao reviewDao;

    @Override
    public Review createReview(long relationId, int rating, String content) {
        return reviewDao.createReview(relationId,rating, content);
    }

    @Override
    public Optional<Review> getReviewById(long relationId) {
        return reviewDao.getReviewById(relationId);
    }

    @Override
    public List<Review> getReviews(Long eventId, Long offeringId, int page, int pageSize) {
        return reviewDao.getReviews(eventId, offeringId, page, pageSize);
    }

    @Override
    public int getReviewsCount(Long eventId, Long offeringId) {
        return reviewDao.getReviewsCount(eventId, offeringId);
    }

}
