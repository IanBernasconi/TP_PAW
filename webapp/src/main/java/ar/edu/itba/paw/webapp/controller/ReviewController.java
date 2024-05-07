package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.eventOfferingRelation.Review;
import ar.edu.itba.paw.models.exception.notFound.ReviewNotFoundException;
import ar.edu.itba.paw.services.ReviewService;
import ar.edu.itba.paw.webapp.dto.ReviewDTO;
import ar.edu.itba.paw.webapp.exception.httpExceptions.HttpBadRequestException;
import ar.edu.itba.paw.webapp.mediaType.VndType;
import ar.edu.itba.paw.webapp.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

import static ar.edu.itba.paw.webapp.utils.PaginationUtils.*;

@Path("reviews")
@Component
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = {VndType.APPLICATION_REVIEWS})
    @PreAuthorize("#eventId == null || @securityAccessFunctions.isEventOwner(authentication, #eventId)")
    public Response getReviews(@QueryParam("event") final Long eventId,
                               @QueryParam("offering") final Long offeringId,
                               @QueryParam("page") @DefaultValue("0") final int page,
                               @QueryParam("pageSize") @DefaultValue(DEFAULT_PAGE_SIZE_STRING) final int pageSize) {

        if (eventId == null && offeringId == null) {
            throw new HttpBadRequestException("exception.review.missingEventOrOffering");
        }

        PaginationUtils.PaginationInfo paginationInfo = calculatePage(page, pageSize, reviewService.getReviewsCount(eventId, offeringId));
        List<ReviewDTO> reviews = ReviewDTO.fromReviews(reviewService.getReviews(eventId, offeringId, paginationInfo.getPage(), paginationInfo.getPageSize()), uriInfo);
        Response.ResponseBuilder response = Response.ok(new GenericEntity<List<ReviewDTO>>(reviews) {
        });
        PaginationUtils.addLinks(response, paginationInfo, uriInfo);
        return response.build();
    }

    @POST
    @Consumes(value = {VndType.APPLICATION_REVIEW})
    @PreAuthorize("@securityAccessFunctions.canReviewRelation(authentication, #reviewDTO.relationId)")
    public Response createReview(@Valid final ReviewDTO reviewDTO) {
        final Review review = reviewService.createReview(reviewDTO.getRelationId(), reviewDTO.getRating(), reviewDTO.getReview());
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(review.getRelationId())).build()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {VndType.APPLICATION_REVIEW})
    public Response getReview(@PathParam("id") final long id) {
        final Review review = reviewService.getReviewById(id).orElseThrow(() -> new ReviewNotFoundException(id));
        return Response.ok(ReviewDTO.fromReview(review, uriInfo)).build();
    }

}
