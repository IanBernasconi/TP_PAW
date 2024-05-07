package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.validation.UriValid;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class LikeDTO {

    private URI self;
    private URI offering;

    @NotNull
    @UriValid(type = UserDTO.class)
    private URI user;
    private boolean liked;

    public LikeDTO() {
    }

    public LikeDTO(long userId, long offeringId, boolean liked, UriInfo uriInfo) {
        this.user = UserDTO.getSelfUri(userId, uriInfo);
        this.offering = OfferingDTO.getSelfUriBuilder(offeringId, uriInfo).build();
        this.self = OfferingDTO.getSelfUriBuilder(offeringId, uriInfo).path("likes").path(String.valueOf(userId)).build();
        this.liked = liked;
    }

    public Long getUserId(){
        return UserDTO.getIdFromUri(user);
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public URI getOffering() {
        return offering;
    }

    public void setOffering(URI offering) {
        this.offering = offering;
    }

    public URI getUser() {
        return user;
    }

    public void setUser(URI user) {
        this.user = user;
    }
}
