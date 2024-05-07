package ar.edu.itba.paw.models.eventOfferingRelation;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @Column(name = "relation_id")
    private long relationId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "relation_id")
    private EventOfferingRelation relation;

    @Column(name = "content", length = 4095)
    private String review;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "created_at", nullable = false,columnDefinition = "TIMESTAMP DEFAULT now()")
    private LocalDateTime date;


    protected Review() {}

    public Review(String review, int rating, EventOfferingRelation relation) {
        this.review = review;
        this.rating = rating;
        this.relation = relation;
        this.date = LocalDateTime.now();
    }

    public String getReview() {
        return review;
    }

    public int getRating() {
        return rating;
    }

    public EventOfferingRelation getRelation() {
        return relation;
    }

    public LocalDateTime getDate() {
        return date;
    }
    public void setReview(String review) {
        this.review = review;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }


    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public long getRelationId() {
        return relationId;
    }

    public String getFormattedDate(){
        return date.format(DateTimeFormatter.ofPattern("dd MMM. yyyy"));
    }

}
