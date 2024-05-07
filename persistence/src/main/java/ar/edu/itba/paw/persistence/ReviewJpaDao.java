package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.Review;
import ar.edu.itba.paw.models.exception.notFound.RelationNotFoundException;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;

import static ar.edu.itba.paw.persistence.tablesInformation.EventOfferingTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.ReviewTableInfo.*;

@Repository
public class ReviewJpaDao implements ReviewDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Review createReview(long review_id, int rating, String content) {
        final EventOfferingRelation relation = em.find(EventOfferingRelation.class, review_id);
        if (relation == null) {
            throw new RelationNotFoundException(review_id);
        }
        final Review newReview = new Review(content, rating, relation);
        em.persist(newReview);
        return newReview;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Review> getReviewById(long relationId) {
        return Optional.ofNullable(em.find(Review.class, relationId));
    }

    @Override
    public List<Review> getReviews(Long eventId, Long offeringId, int page, int pageSize) {
        StringBuilder stringBuilder = new StringBuilder("SELECT "+ REVIEW_TABLE +"." + REVIEW_RELATION_ID +" FROM "+REVIEW_TABLE+" JOIN "+EVENT_OFFERING_TABLE+" ON "+REVIEW_TABLE+"."+REVIEW_RELATION_ID+" = "+EVENT_OFFERING_TABLE +"."+EVENT_OFFERING_RELATION_ID+" WHERE true");
        Map<String, Object> params = new HashMap<>();
        addReviewsFilter(eventId, offeringId, stringBuilder, params);

        List<Long> idList = UtilitiesFunctions.getEntitiesIdListByQuery(em, stringBuilder.toString(), params, page, pageSize);

        if (idList.isEmpty()) {
            return Collections.emptyList();
        }

        final TypedQuery<Review> query = em.createQuery("FROM Review WHERE relation.id IN :ids", Review.class);
        query.setParameter("ids", idList);
        return query.getResultList();
    }

    @Override
    public int getReviewsCount(Long eventId, Long offeringId) {
        StringBuilder stringBuilder = new StringBuilder("SELECT COUNT("+REVIEW_TABLE+"."+REVIEW_RELATION_ID+") FROM "+REVIEW_TABLE+" JOIN "+EVENT_OFFERING_TABLE+" ON "+REVIEW_TABLE+"."+REVIEW_RELATION_ID+" = "+EVENT_OFFERING_TABLE +"."+EVENT_OFFERING_RELATION_ID+" WHERE true");
        Map<String, Object> params = new HashMap<>();
        addReviewsFilter(eventId, offeringId, stringBuilder, params);
        final Query nativeQuery = em.createNativeQuery(stringBuilder.toString());
        params.forEach(nativeQuery::setParameter);
        return ((Number) nativeQuery.getSingleResult()).intValue();
    }

    private void addReviewsFilter(Long eventId, Long offeringId, StringBuilder stringBuilder, Map<String, Object> params) {
        if(eventId != null) {
            stringBuilder.append(" AND "+EVENT_OFFERING_EVENT_ID+" = :eventId");
            params.put("eventId", eventId);
        }
        if(offeringId != null) {
            stringBuilder.append(" AND "+EVENT_OFFERING_OFFERING_ID+" = :offeringId");
            params.put("offeringId", offeringId);
        }
    }

}
