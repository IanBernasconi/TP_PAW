package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;

import javax.validation.constraints.NotNull;

public class RelationStatusDTO {

    @NotNull(message = "{invalid.RelationDTO.status}")
    private OfferingStatus status;

    public RelationStatusDTO() {}

    public OfferingStatus getStatus() {
        return status;
    }

    public void setStatus(OfferingStatus status) {
        this.status = status;
    }


}
