package ar.edu.itba.paw.models.eventOfferingRelation;

import ar.edu.itba.paw.models.RangeFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RelationFilter extends RangeFilter {

    private static final List<OfferingStatus> DEFAULT_STATUS = new ArrayList<>(Arrays.asList(OfferingStatus.PENDING, OfferingStatus.ACCEPTED, OfferingStatus.DONE));
    private List<OfferingStatus> status = DEFAULT_STATUS;
    private Long offeringId = null;
    private Long providerId = null;
    private Long eventId = null;

    public RelationFilter withStatus(List<OfferingStatus> statuses) {
        this.status = statuses;
        return this;
    }

    public RelationFilter withOfferingId(Long offeringId) {
        this.offeringId = offeringId;
        return this;
    }

    public RelationFilter withProviderId(Long providerId) {
        this.providerId = providerId;
        return this;
    }

    public RelationFilter withEventId(Long eventId) {
        this.eventId = eventId;
        return this;
    }

    public List<OfferingStatus> getStatus() {
        return status;
    }

    public Long getOfferingId() {
        return offeringId;
    }

    public Long getProviderId() {
        return providerId;
    }

    public Long getEventId() {
        return eventId;
    }

}
