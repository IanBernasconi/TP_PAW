package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.eventOfferingRelation.RelationFilter;

import java.util.List;
import java.util.Optional;

public interface EventOfferingService {

    Optional<EventOfferingRelation> getRelationById(long relationId);

    EventOfferingRelation createRelation(long eventId, long offeringId);

    boolean userIsInRelation(long relationId, long userId);

    int getRelationsCount(RelationFilter relationFilter);

    List<EventOfferingRelation> getRelations(RelationFilter relationFilter, int page, int pageSize);

    void changeOfferingStatus(long relationId, OfferingStatus status, String seeDetailsUrlForProvider, String seeDetailsUrlForOrganizer);

    void deleteRelation(EventOfferingRelation eventOfferingRelation, String seeDetailsUrl);
}
