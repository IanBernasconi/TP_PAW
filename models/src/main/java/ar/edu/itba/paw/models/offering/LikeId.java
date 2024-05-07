package ar.edu.itba.paw.models.offering;

import java.io.Serializable;
import java.util.Objects;

public class LikeId implements Serializable {

    private Long user;
    private Long offering;

    public LikeId() {}

    public LikeId(Long user, Long offering) {
        this.user = user;
        this.offering = offering;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public Long getOffering() {
        return offering;
    }

    public void setOffering(Long offering) {
        this.offering = offering;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LikeId)) return false;

        LikeId likeId = (LikeId) o;

        if (getUser() != null ? !getUser().equals(likeId.getUser()) : likeId.getUser() != null) return false;
        return getOffering() != null ? getOffering().equals(likeId.getOffering()) : likeId.getOffering() == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser(), getOffering());
    }
}