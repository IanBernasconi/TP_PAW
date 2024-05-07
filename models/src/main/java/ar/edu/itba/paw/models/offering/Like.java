package ar.edu.itba.paw.models.offering;


import ar.edu.itba.paw.models.User;

import javax.persistence.*;

@Entity
@Table(name = "liked_offerings")
@IdClass(LikeId.class)
public class Like {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "offering_id")
    private Offering offering;

    protected Like() {}

    public Like(User user, Offering offering) {
        this.user = user;
        this.offering = offering;
    }

    public User getUser() {
        return user;
    }

    public Offering getOffering() {
        return offering;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setOffering(Offering offering) {
        this.offering = offering;
    }
}
