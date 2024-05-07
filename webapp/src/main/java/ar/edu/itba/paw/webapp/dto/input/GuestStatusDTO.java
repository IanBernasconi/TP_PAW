package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.models.events.GuestStatus;

import javax.validation.constraints.NotNull;

public class GuestStatusDTO {

    @NotNull(message = "{invalid.GuestDTO.status}")
    private GuestStatus status;

    public GuestStatusDTO() {
    }

    public GuestStatus getStatus() {
        return status;
    }

    public void setStatus(GuestStatus status) {
        this.status = status;
    }
}
