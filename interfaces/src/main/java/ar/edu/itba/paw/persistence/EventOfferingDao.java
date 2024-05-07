package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.eventOfferingRelation.RelationFilter;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.offering.Offering;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Optional;

public interface EventOfferingDao {

    EventOfferingRelation createRelation(Event event, Offering offering);

    OfferingStatus changeEventOfferingStatus(EventOfferingRelation relation, OfferingStatus status);

    void removeOfferingFromEvent(long offeringId, long eventId);

    Optional<EventOfferingRelation> getRelation(long relationId);

    int getRelationsCount(RelationFilter relationFilter);

    List<EventOfferingRelation> getRelations(RelationFilter relationFilter, int page, int pageSize);


    boolean userIsInRelation(long relationId, long userId);

    @Async
    void markOfferingsAsDone();

    OfferingStatus getEventOfferingStatus(long offeringId, long eventId);

    void deleteRelation(long relationId);
}