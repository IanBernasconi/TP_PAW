package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.eventOfferingRelation.RelationFilter;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.offering.Offering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.persistence.tablesInformation.EventOfferingTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.EventTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.OfferingTableInfo.*;

@Repository
public class EventOfferingJpaDao implements EventOfferingDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(EventOfferingJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public EventOfferingRelation createRelation(Event event, Offering offering) {
        if (event == null || offering == null) {
            LOGGER.error("Event or offering is null");
            throw new IllegalArgumentException("Event or offering is null");
        }
        EventOfferingRelation relation = new EventOfferingRelation(event, offering, OfferingStatus.NEW);
        em.persist(relation);
        return relation;
    }

    @Override
    @Transactional
    public OfferingStatus changeEventOfferingStatus(EventOfferingRelation relation, OfferingStatus status) {
        if (relation == null) {
            LOGGER.error("Relation not found");
            return null;
        }
        switch (relation.getStatus()) {
            case NEW:
                if (status != OfferingStatus.PENDING) {
                    LOGGER.error("Relation with id {} has status {} but required status is {}", relation.getRelationId(), relation.getStatus(), OfferingStatus.PENDING);
                    return null;
                }
                break;
            case PENDING:
                if (status != OfferingStatus.ACCEPTED && status != OfferingStatus.REJECTED) {
                    LOGGER.error("Relation with id {} has status {} but required status is {} or {}", relation.getRelationId(), relation.getStatus(), OfferingStatus.ACCEPTED, OfferingStatus.REJECTED);
                    return null;
                }
                break;
            case ACCEPTED:
                if (status != OfferingStatus.DONE && status != OfferingStatus.REJECTED) {
                    LOGGER.error("Relation with id {} has status {} but required status is {}", relation.getRelationId(), relation.getStatus(), OfferingStatus.DONE);
                    return null;
                }
                break;
            case REJECTED:
                if (status != OfferingStatus.DONE) {
                    LOGGER.error("Relation with id {} has status {} but required status is {}", relation.getRelationId(), relation.getStatus(), OfferingStatus.DONE);
                    return null;
                }
                break;
            case DONE:
                LOGGER.error("Relation with id {} has status {}, and it can't be changed", relation.getRelationId(), relation.getStatus());
                return null;
        }

        relation.setStatus(status);
        em.persist(relation);

        return status;
    }

    @Override
    @Transactional
    public void removeOfferingFromEvent(long offeringId, long eventId) {
        em.createQuery("delete from EventOfferingRelation where event.id = :eventId and offering.id = :offeringId")
                .setParameter("eventId", eventId)
                .setParameter("offeringId", offeringId)
                .executeUpdate();
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<EventOfferingRelation> getRelation(long relationId) {
        return Optional.ofNullable(em.find(EventOfferingRelation.class, relationId));
    }

    private final static String RELATIONS_QUERY = " FROM " + EVENT_OFFERING_TABLE +
            " JOIN " + EVENT_TABLE + " ON " + EVENT_OFFERING_TABLE + "." + EVENT_OFFERING_EVENT_ID + " = " + EVENT_TABLE + "." + EVENT_ID +
            " JOIN " + OFFERING_TABLE + " ON " + EVENT_OFFERING_TABLE + "." + EVENT_OFFERING_OFFERING_ID + " = " + OFFERING_TABLE + "." + OFFERING_ID +
            " WHERE true ";

    @Override
    public int getRelationsCount(RelationFilter relationFilter) {
        StringBuilder queryBuilder = new StringBuilder("SELECT COUNT(" + EVENT_OFFERING_TABLE + "." + EVENT_OFFERING_RELATION_ID + ") " + RELATIONS_QUERY);
        Map<String, Object> parameters = new HashMap<>();
        addRelationsFilter(queryBuilder, relationFilter, parameters);
        Query query = em.createNativeQuery(queryBuilder.toString());
        parameters.forEach(query::setParameter);
        return ((Number) query.getSingleResult()).intValue();
    }

    @Override
    public List<EventOfferingRelation> getRelations(RelationFilter relationFilter, int page, int pageSize) {
        StringBuilder queryBuilder = new StringBuilder("SELECT(" + EVENT_OFFERING_TABLE + "." + EVENT_OFFERING_RELATION_ID + ") " + RELATIONS_QUERY);
        Map<String, Object> parameters = new HashMap<>();
        addRelationsFilter(queryBuilder, relationFilter, parameters);

        return getRelationsByQuery(queryBuilder.toString(), parameters, page, pageSize);
    }

    private void addRelationsFilter(StringBuilder queryBuilder, RelationFilter relationFilter, Map<String, Object> parameters) {
        if (relationFilter != null) {
            if (relationFilter.getProviderId() != null) {
                queryBuilder.append(" AND " + OFFERING_TABLE + "." + OFFERING_USER_ID + "  = :userId ");
                parameters.put("userId", relationFilter.getProviderId());
            }
            if (relationFilter.getEventId() != null) {
                queryBuilder.append(" AND " + EVENT_TABLE + "." + EVENT_ID + " = :eventId");
                parameters.put("eventId", relationFilter.getEventId());
            }
            if (relationFilter.getStatus() != null && !relationFilter.getStatus().isEmpty()) {
                queryBuilder.append(" AND " + EVENT_OFFERING_TABLE + "." + EVENT_OFFERING_STATUS + " IN (:statuses)");
                List<String> statuses = relationFilter.getStatus().stream().map(Enum::toString).collect(Collectors.toList());
                parameters.put("statuses", statuses);
            }
            if (relationFilter.getStart() != null) {
                queryBuilder.append(" AND " + EVENT_TABLE + "." + EVENT_DATE + " >= :start");
                parameters.put("start", relationFilter.getStart());
            }
            if (relationFilter.getEnd() != null) {
                queryBuilder.append(" AND " + EVENT_TABLE + "." + EVENT_DATE + " <= :end");
                parameters.put("end", relationFilter.getEnd());
            }
            if (relationFilter.getOfferingId() != null) {
                queryBuilder.append(" AND " + OFFERING_TABLE + "." + OFFERING_ID + " = :offeringId");
                parameters.put("offeringId", relationFilter.getOfferingId());
            }
        }
    }

    private List<EventOfferingRelation> getRelationsByQuery(String query, Map<String, Object> params, int page, int pageSize) {

        List<Long> idList = UtilitiesFunctions.getEntitiesIdListByQuery(em, query, params, page, pageSize);
        if (idList.isEmpty()) {
            return Collections.emptyList();
        }

        return em.createQuery("from EventOfferingRelation where relationId in (:ids)", EventOfferingRelation.class)
                .setParameter("ids", idList)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userIsInRelation(long relationId, long userId) {
        EventOfferingRelation relation = em.find(EventOfferingRelation.class, relationId);
        return relation != null && (relation.getEvent().getUser().getId() == userId || relation.getOffering().getOwner().getId() == userId);
    }

    @Async
    @Override
    @Transactional
    public void markOfferingsAsDone() {
        em.createQuery("update EventOfferingRelation e set e.status = :doneStatus where e.status = :acceptedStatus and" +
                        " e.id in (select eor.id from EventOfferingRelation eor join eor.event e where e.date < current_date)")
                .setParameter("doneStatus", OfferingStatus.DONE)
                .setParameter("acceptedStatus", OfferingStatus.ACCEPTED)
                .executeUpdate();
    }

    @Override
    @Transactional(readOnly = true)
    public OfferingStatus getEventOfferingStatus(long offeringId, long eventId) {

        return em.createQuery("select status from EventOfferingRelation where event.id = :eventId and offering.id = :offeringId", OfferingStatus.class)
                .setParameter("eventId", eventId)
                .setParameter("offeringId", offeringId)
                .getSingleResult();
    }

    @Override
    @Transactional
    public void deleteRelation(long relationId) {
        em.createQuery("delete from EventOfferingRelation where relationId = :relationId")
                .setParameter("relationId", relationId)
                .executeUpdate();
    }
}
