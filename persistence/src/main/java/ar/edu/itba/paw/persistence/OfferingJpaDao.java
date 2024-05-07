package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.exception.notFound.OfferingNotFoundException;
import ar.edu.itba.paw.models.exception.notFound.UserNotFoundException;
import ar.edu.itba.paw.models.offering.*;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.persistence.tablesInformation.EventOfferingTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.EventTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.OfferingTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.ReviewTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.UserTableInfo.*;


@Repository
public class OfferingJpaDao implements OfferingDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(OfferingJpaDao.class);

    private static final String AVG_RATING = "average_review";
    private static final String REVIEW_COUNT = "review_count";
    private static final String LIKE_COUNT = "like_count";

    @PersistenceContext
    private EntityManager em;


    @Autowired
    private UserDao userDao;

    @Autowired
    private ImageDao imageDao;

    private static final String RATING_SORTING_JOIN_QUERY = " LEFT JOIN " +
            "   (SELECT AVG(CAST(" + REVIEW_TABLE + "." + REVIEW_RATING + " AS DOUBLE PRECISION)) AS " + AVG_RATING + ", " + EVENT_OFFERING_TABLE + "." + EVENT_OFFERING_OFFERING_ID +
            "       FROM " + REVIEW_TABLE + " JOIN " + EVENT_OFFERING_TABLE +
            "           ON " + REVIEW_TABLE + "." + REVIEW_RELATION_ID + " = " + EVENT_OFFERING_TABLE + "." + EVENT_OFFERING_RELATION_ID +
            "               GROUP BY " + EVENT_OFFERING_TABLE + "." + REVIEW_OFFERING_ID + ") AS " + AVG_RATING + " ON " + OFFERING_TABLE + "." + OFFERING_ID + " = " + AVG_RATING + "." + EVENT_OFFERING_OFFERING_ID;

    private static final String REVIEW_COUNT_SORTING_JOIN_QUERY = " LEFT JOIN " +
            "   (SELECT COUNT(1) AS " + REVIEW_COUNT + ", " + EVENT_OFFERING_TABLE + "." + EVENT_OFFERING_OFFERING_ID +
            "       FROM " + REVIEW_TABLE + " JOIN " + EVENT_OFFERING_TABLE +
            "           ON " + REVIEW_TABLE + "." + REVIEW_RELATION_ID + " = " + EVENT_OFFERING_TABLE + "." + EVENT_OFFERING_RELATION_ID +
            "               GROUP BY " + EVENT_OFFERING_TABLE + "." + REVIEW_OFFERING_ID + ") AS " + REVIEW_COUNT + " ON " + OFFERING_TABLE + "." + OFFERING_ID + " = " + REVIEW_COUNT + "." + EVENT_OFFERING_OFFERING_ID;
    private static final String LIKE_COUNT_SORTING_JOIN_QUERY = " LEFT JOIN " +
            "   (SELECT COUNT(" + LIKED_OFFERING_OFFERING_ID + ") AS " + LIKE_COUNT + ", " + LIKED_OFFERING_TABLE + "." + LIKED_OFFERING_OFFERING_ID +
            "       FROM " + LIKED_OFFERING_TABLE +
            "           GROUP BY " + LIKED_OFFERING_TABLE + "." + LIKED_OFFERING_OFFERING_ID + ") AS " + LIKE_COUNT + " ON " + OFFERING_TABLE + "." + OFFERING_ID + " = " + LIKE_COUNT + "." + LIKED_OFFERING_OFFERING_ID;


    @Override
    @Transactional
    public Offering create(long userId, String name, OfferingCategory offeringCategory, String description, float minPrice, float maxPrice, PriceType priceType, int maxGuests, District district, List<Long> images) {
        User user = userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        Offering offering = new Offering(name, user, offeringCategory, description, minPrice, maxPrice, priceType, maxGuests, district);
        offering.setImageIds(images);
        em.persist(offering);

        return offering;
    }
    @Override
    @Transactional
    public void update(long offeringId, String name, String description, int maxGuests, District district, OfferingCategory category, float minPrice, float maxPrice, PriceType priceType) {
        Offering oldOffering = em.find(Offering.class, offeringId);
        if (oldOffering == null) {
            LOGGER.warn("Offering with id {} not found", offeringId);
            throw new OfferingNotFoundException(offeringId);
        }

        oldOffering.setName(name);
        oldOffering.setDescription(description);
        oldOffering.setMaxGuests(maxGuests);
        oldOffering.setDistrict(district);
        oldOffering.setOfferingCategory(category);
        oldOffering.setMinPrice(minPrice);
        oldOffering.setMaxPrice(maxPrice);
        oldOffering.setPriceType(priceType);

        em.persist(oldOffering);
    }

    @Override
    @Transactional
    public void updateImages(long offeringId, List<Long> images, List<Long> removeImagesIds) {
        Offering offering = em.find(Offering.class, offeringId);

        if (offering == null) {
            LOGGER.warn("Offering with id {} not found", offeringId);
            return;
        }
        offering.setImageIds(images);
        em.persist(offering);


        if (removeImagesIds != null && !removeImagesIds.isEmpty()) {
            imageDao.deleteImages(removeImagesIds);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Offering> getById(long id, Long loggedUserId) {
        Offering offering = em.find(Offering.class, id);
        if (offering == null) {
            return Optional.empty();
        }
        return Optional.of(offering);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Offering> getById(long id) {
        return getById(id, null);
    }


    private List<Offering> getByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        TypedQuery<Offering> query = em.createQuery("from Offering where id in (:ids)", Offering.class);
        query.setParameter("ids", ids);
        List<Offering> offerings = query.getResultList();
        // Order by id order in ids list
        offerings.sort(Comparator.comparingInt(o -> ids.indexOf(o.getId())));
        return offerings;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Offering> getFilteredOfferings(OfferingFilter filter, int page, int pageSize) {
        if (filter == null) {
            return getFilteredOfferings(new OfferingFilter(), page, pageSize);
        }

        StringBuilder query = new StringBuilder("SELECT " + OFFERING_ID + " FROM " + OFFERING_TABLE);
        addSortingJoinQuery(filter, query);
        query.append(" WHERE true ");
        Map<String, Object> params = addRequestFilters(filter, query);
        addRequestSorting(filter, query);

        return getOfferingsByQuery(query.toString(), params, page, pageSize);
    }

    private List<Offering> getOfferingsByQuery(String query, Map<String, Object> params, int page, int pageSize) {
        List<Long> idList = UtilitiesFunctions.getEntitiesIdListByQuery(em, query, params, page, pageSize);
        return getByIds(idList);
    }

    @Override
    @Transactional(readOnly = true)
    public int getFilteredOfferingsCount(OfferingFilter filter) {
        if (filter == null) {
            return getFilteredOfferingsCount(new OfferingFilter());
        }

        StringBuilder mySqlQuery = new StringBuilder("SELECT COUNT(DISTINCT " + OFFERING_ID + ") FROM " + OFFERING_TABLE + " WHERE true ");
        Map<String, Object> params = addRequestFilters(filter, mySqlQuery);

        Query query = em.createNativeQuery(mySqlQuery.toString());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return ((Number) query.getSingleResult()).intValue();
    }

    @Override
    @Transactional
    public boolean setLike(Offering offering, User user, boolean liked) {
        if (user == null || offering == null) {
            LOGGER.warn("Invalid user id or offering id");
            return false;
        }
        Like like  = em.find(Like.class, new LikeId(user.getId(), offering.getId()));
        if(like == null && liked) {
            like = new Like(user, offering);
            em.persist(like);
            offering.setLikes(offering.getLikes() + 1);
            return true;
        }
        if(like != null && !liked){
            em.remove(like);
            offering.setLikes(offering.getLikes() - 1);
            return false;
        }
        return liked;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userLikedOffering(long userId, long offeringId) {
        if (userId <= 0 || offeringId <= 0) {
            LOGGER.warn("Invalid user id or offering id");
            return false;
        }
        Like like  = em.find(Like.class, new LikeId(userId, offeringId));
        return like != null;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOwner(long offeringId, long userId) {
        if (userId <= 0 || offeringId <= 0) {
            LOGGER.warn("Invalid user id or offering id");
            return false;
        }
        Offering offering = em.find(Offering.class, offeringId);
        return offering != null && offering.getOwner().getId() == userId;
    }

    @Override
    @Transactional
    public void deleteOffering(long offeringId) {

        Offering offering = getById(offeringId).orElseThrow(() -> new ObjectNotFoundException(offeringId, Offering.class.getName()));
        offering.setDeleted(true);
        em.persist(offering);
    }

    @Override
    @Transactional
    public List<Offering> getRecommendationsForOffering(OfferingFilter filter, int baseQty, int qty, long offeringId) {
        if (filter == null || qty <= 0 || offeringId <= 0) {
            return Collections.emptyList();
        }
        StringBuilder query = new StringBuilder("SELECT " + OFFERING_ID + " FROM " + OFFERING_TABLE);
        addSortingJoinQuery(filter, query);
        query.append(" WHERE true ");
        Map<String, Object> params = addRequestFilters(filter, query);
        query.append("AND " + OFFERING_ID + " NOT IN (:offeringId)");
        params.put("offeringId", offeringId);
        addRequestSorting(filter, query);

        final Query nativeQuery = em.createNativeQuery(query.toString());
        nativeQuery.setMaxResults(baseQty);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            nativeQuery.setParameter(entry.getKey(), entry.getValue());
        }

        List<Long> idList = UtilitiesFunctions.getIdsFromNativeQuery(nativeQuery);
//        get randomly qty offerings from the baseQty offerings
        Collections.shuffle(idList);
        idList = idList.subList(0, Math.min(qty, idList.size()));
        return getByIds(idList);
    }

    @Override
    @Transactional
    public List<Offering> getRecommendations(List<OfferingCategory> categories, int baseQty, OfferingFilter filter) {
        if (categories == null || categories.isEmpty()) {
            return Collections.emptyList();
        }

        StringBuilder query = new StringBuilder("SELECT " + OFFERING_ID + " FROM (");
        query.append(" SELECT " + OFFERING_ID + ", ROW_NUMBER() OVER (PARTITION BY " + OFFERING_CATEGORY + " ORDER BY RANDOM()) AS row_num_ext");
        query.append(" FROM ( SELECT " + OFFERING_ID + ", " + OFFERING_CATEGORY + ", ROW_NUMBER() OVER (PARTITION BY " + OFFERING_CATEGORY + " ORDER BY " + AVG_RATING + " DESC NULLS LAST) AS row_num");
        query.append(" FROM " + OFFERING_TABLE);

        addSortingJoinQuery(filter, query);
        query.append(" WHERE " + OFFERING_CATEGORY + " IN (:categories)");
        Map<String, Object> params = addRequestFilters(filter, query);
        query.append(") AS topOfferings");
        query.append(" WHERE row_num <= :baseQty) AS topOfferingsByCategory");
        query.append(" WHERE row_num_ext = 1");


        params.put("baseQty", baseQty);
        params.put("categories", categories.stream().map(OfferingCategory::name).collect(Collectors.toList()));
        final Query nativeQuery = em.createNativeQuery(query.toString());
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            nativeQuery.setParameter(entry.getKey(), entry.getValue());
        }
        List<Long> idList = UtilitiesFunctions.getIdsFromNativeQuery(nativeQuery);

        return getByIds(idList);
    }

    private Map<String, Object> addRequestFilters(OfferingFilter filter, StringBuilder sql) {
        Map<String, Object> params = new HashMap<>();

        if (!filter.getIncludeDeleted()) {
            sql.append(" AND " + OFFERING_DELETED + " = false ");
        }

        if (filter.getLikedBy() != null) {
            sql.append(" AND " + OFFERING_TABLE + "." + OFFERING_ID + " IN (SELECT " + LIKED_OFFERING_OFFERING_ID + " FROM " + LIKED_OFFERING_TABLE + " WHERE " + LIKED_OFFERING_USER_ID + " = :userId)");
            params.put("userId", filter.getLikedBy());
        }

        if (filter.getCategory() != null && filter.getCategory() != OfferingCategory.ALL) {
            sql.append(" AND " + OFFERING_CATEGORY + " = :category ");
            params.put("category", filter.getCategory().name());
        }

        if (filter.getMinPrice() != null) {
            sql.append(" AND " + OFFERING_MAX_PRICE + " >= :minPrice ");
            params.put("minPrice", filter.getMinPrice());
        }

        if (filter.getMaxPrice() != null) {
            sql.append(" AND " + OFFERING_MAX_PRICE + " <= :maxPrice");
            params.put("maxPrice", filter.getMaxPrice());
        }

        if (filter.getAttendees() != null) {
            sql.append(" AND (" + OFFERING_MAX_GUESTS + " >= :minGuests OR " + OFFERING_MAX_GUESTS + " = 0) ");
            params.put("minGuests", filter.getAttendees());
        }

        if (filter.getDistricts() != null && !filter.getDistricts().isEmpty() && !filter.getDistricts().contains(District.ALL)) {
            sql.append(" AND " + OFFERING_DISTRICT + " IN (:districts) ");
            params.put("districts", filter.getDistricts().stream().map(District::name).collect(Collectors.toList()));
        }

        if (filter.getSearch() != null && !filter.getSearch().isEmpty()) {
            sql.append(" AND (" + OFFERING_NAME + " ILIKE :search OR " +
                    OFFERING_DESCRIPTION + " ILIKE :search OR " +
                    OFFERING_TABLE + "." + OFFERING_USER_ID + " IN " +
                    "(SELECT " + USER_ID +
                    " FROM " + USER_TABLE +
                    " WHERE " + USER_USERNAME + " ILIKE :search)) ");

            params.put("search", "%" + filter.getSearch() + "%");
        }

        if (filter.getAvailableOn() != null) {
            sql.append(" AND :date NOT IN (SELECT " + EVENT_DATE + " FROM " + EVENT_OFFERING_TABLE +
                    " JOIN " + EVENT_TABLE + " ON " + EVENT_OFFERING_TABLE + "." + EVENT_OFFERING_EVENT_ID + " = " + EVENT_TABLE + "." + EVENT_ID +
                    " WHERE " + EVENT_OFFERING_TABLE + "." + EVENT_OFFERING_OFFERING_ID + " = " + OFFERING_TABLE + "." + OFFERING_ID +
                    " AND " + EVENT_OFFERING_TABLE + "." + EVENT_OFFERING_STATUS + " = 'ACCEPTED')");
            params.put("date", filter.getAvailableOn());
        }

        if (filter.getCreatedBy() != null) {
            sql.append(" AND " + OFFERING_TABLE + "." + OFFERING_USER_ID + " = :userId");
            params.put("userId", filter.getCreatedBy());
        }

        if (filter.getInEvent() != null) {
            sql.append(" AND " + OFFERING_TABLE + "." + OFFERING_ID + " IN (SELECT " + EVENT_OFFERING_OFFERING_ID + " FROM " + EVENT_OFFERING_TABLE + " WHERE " + EVENT_OFFERING_EVENT_ID + " = :eventId )");
            params.put("eventId", filter.getInEvent());
        }

        return params;
    }

    private void addSortingJoinQuery(OfferingFilter filter, StringBuilder sql) {
        if (filter.getSortType() != null) {
            switch (filter.getSortType()) {
                case RATING_DESC:
                    sql.append(RATING_SORTING_JOIN_QUERY);
                    break;
                case REVIEW_COUNT_DESC:
                    sql.append(REVIEW_COUNT_SORTING_JOIN_QUERY);
                    break;
                case POPULARITY_DESC:
                    sql.append(LIKE_COUNT_SORTING_JOIN_QUERY);
                    break;
            }
        }
    }

    private void addRequestSorting(OfferingFilter filter, StringBuilder sql) {
        if (filter.getSortType() != null) {
            sql.append(" ORDER BY ");
            switch (filter.getSortType()) {
                case RATING_DESC:
                    sql.append(AVG_RATING + " DESC");
                    break;
                case REVIEW_COUNT_DESC:
                    sql.append(REVIEW_COUNT + " DESC");
                    break;
                case POPULARITY_DESC:
                    sql.append(LIKE_COUNT + " DESC");
                    break;
            }
            sql.append(" NULLS LAST ");
        }
    }

}
