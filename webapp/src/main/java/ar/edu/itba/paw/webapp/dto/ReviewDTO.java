package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.eventOfferingRelation.Review;
import ar.edu.itba.paw.webapp.validation.UriValid;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewDTO {

    @NotNull
    @Size(max = 4096)
    private String review;

    @NotNull
    @Min(0)
    @Max(5)
    private int rating;

    private LocalDateTime date;

    private URI self;

    @NotNull
    @UriValid(type = RelationDTO.class)
    private URI relation;

    public static ReviewDTO fromReview(final Review review, final UriInfo uriInfo){
        final ReviewDTO dto = new ReviewDTO();
        dto.review = review.getReview();
        dto.rating = review.getRating();
        dto.date = review.getDate();
        dto.self = uriInfo.getBaseUriBuilder().path("reviews").path(String.valueOf(review.getRelationId())).build();
        dto.relation = uriInfo.getBaseUriBuilder().path("relations").path(String.valueOf(review.getRelation().getRelationId())).build();
        return dto;
    }

    public static URI getSelfUri(final long id, final UriInfo uriInfo){
        return uriInfo.getBaseUriBuilder().path("reviews").path(String.valueOf(id)).build();
    }

    public static List<ReviewDTO> fromReviews(final List<Review> reviews, final UriInfo uriInfo){
        return reviews.stream().map(review -> fromReview(review, uriInfo)).collect(Collectors.toList());
    }

    public Long getRelationId(){
        return RelationDTO.getIdFromUri(relation);
    }

    public ReviewDTO(){}

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getRelation() {
        return relation;
    }

    public void setRelation(URI relation) {
        this.relation = relation;
    }
}
