package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.eventOfferingRelation.RelationFilter;

import javax.ws.rs.QueryParam;
import java.util.List;

public class ProviderRelationsFilter {

    @QueryParam("status")
    private List<OfferingStatus> status = null;

    @QueryParam("offering")
    private Long offeringId = null;

    public ProviderRelationsFilter() {
    }

    public List<OfferingStatus> getStatus() {
        return status;
    }

    public void setStatus(List<OfferingStatus> status) {
        this.status = status;
    }

    public Long getOfferingId() {
        return offeringId;
    }

    public void setOfferingId(Long offeringId) {
        this.offeringId = offeringId;
    }

    public RelationFilter addToRelationFilter(RelationFilter relationFilter) {
        if (status != null) {
            relationFilter.withStatus(status);
        }
        relationFilter.withOfferingId(offeringId);

        return relationFilter;
    }

}
