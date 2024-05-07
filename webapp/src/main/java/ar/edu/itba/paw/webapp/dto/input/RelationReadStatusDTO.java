package ar.edu.itba.paw.webapp.dto.input;

import ar.edu.itba.paw.webapp.dto.UserDTO;
import ar.edu.itba.paw.webapp.validation.UriValid;

import javax.validation.constraints.NotNull;
import java.net.URI;

public class RelationReadStatusDTO {

    @NotNull
    @UriValid(type = UserDTO.class)
    private URI user;

    private boolean read;

    public RelationReadStatusDTO() {}

    public URI getUser() {
        return user;
    }

    public Long getUserId(){
        return UserDTO.getIdFromUri(user);
    }

    public void setUser(URI user) {
        this.user = user;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(final boolean read) {
        this.read = read;
    }
}
