package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.events.Guest;
import ar.edu.itba.paw.models.events.GuestStatus;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class GuestDTO {

    @NotNull
    @Email
    private String email;

    @NotNull(message = "{invalid.GuestDTO.status}")
    private GuestStatus status;

    private URI self;

    public GuestDTO() {
    }

    public static GuestDTO fromGuest(final Guest guest, final UriInfo uriInfo){
        final GuestDTO dto = new GuestDTO();
        dto.email = guest.getEmail();
        dto.status = guest.getStatus();
        dto.self = getSelfUri(guest, uriInfo);
        return dto;
    }

    public static URI getSelfUri(final Guest guest, final UriInfo uriInfo){
        return uriInfo.getBaseUriBuilder().path("events").path(String.valueOf(guest.getEvent().getId())).path("guests").path(String.valueOf(guest.getId())).build();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public GuestStatus getStatus() {
        return status;
    }

    public void setStatus(GuestStatus status) {
        this.status = status;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }
}
