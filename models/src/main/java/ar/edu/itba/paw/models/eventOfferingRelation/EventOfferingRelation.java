package ar.edu.itba.paw.models.eventOfferingRelation;

import ar.edu.itba.paw.models.Conversation;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.offering.Offering;

import javax.persistence.*;

@Entity
@Table(name = "events_offerings")
public class EventOfferingRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "events_offerings_relation_id_seq")
    @SequenceGenerator(sequenceName = "events_offerings_relation_id_seq", name = "events_offerings_relation_id_seq", allocationSize = 1)
    @Column(name = "relation_id")
    private long relationId;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "offering_id")
    private Offering offering;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OfferingStatus status;

    @OneToOne(mappedBy = "relation")
    private Conversation conversation;

    @OneToOne(mappedBy = "relation")
    private Review review;

    protected EventOfferingRelation() {}

    public EventOfferingRelation(Event event, Offering offering, OfferingStatus status) {
        this.event = event;
        this.offering = offering;
        this.status = status;
    }

    public long getRelationId() {
        return relationId;
    }

    public void setRelationId(long relationId) {
        this.relationId = relationId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Offering getOffering() {
        return offering;
    }

    public void setOffering(Offering offering) {
        this.offering = offering;
    }

    public OfferingStatus getStatus() {
        return status;
    }

    public void setStatus(OfferingStatus status) {
        this.status = status;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public Long getProviderId() {
        return offering.getOwner().getId();
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}
