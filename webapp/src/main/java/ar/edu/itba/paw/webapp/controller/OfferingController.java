package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.PriceType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.exception.notFound.EventNotFoundException;
import ar.edu.itba.paw.models.exception.notFound.OfferingNotFoundException;
import ar.edu.itba.paw.models.exception.notFound.UserNotFoundException;
import ar.edu.itba.paw.models.offering.Offering;
import ar.edu.itba.paw.models.offering.OfferingCategory;
import ar.edu.itba.paw.models.offering.OfferingFilter;
import ar.edu.itba.paw.services.EventService;
import ar.edu.itba.paw.services.OfferingService;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.dto.*;
import ar.edu.itba.paw.webapp.exception.httpExceptions.HttpBadRequestException;
import ar.edu.itba.paw.webapp.form.FilterForm;
import ar.edu.itba.paw.webapp.mediaType.VndType;
import ar.edu.itba.paw.webapp.utils.PaginationUtils;
import ar.edu.itba.paw.webapp.utils.UrisInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.utils.PaginationUtils.*;

@Path("services")
@Component
public class OfferingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferingController.class);
    private static final int RECOMMENDATIONS_QTY = 3;
    private static final int RECOMMENDATIONS_BASE_QTY = 10;

    @Autowired
    private OfferingService offeringService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private Environment env;

    @Context
    private UriInfo uriInfo;


    @GET
    @Produces(value = {VndType.APPLICATION_OFFERINGS})
    @Transactional
    @PreAuthorize("(#eventId == null || @securityAccessFunctions.hasEventAccess(authentication, #eventId) and " +
            "(#offeringFilterForm.inEvent == null || @securityAccessFunctions.hasEventAccess(authentication, #eventId))) and " +
            "(#offeringFilterForm.likedBy == null || @securityAccessFunctions.hasUserAccess(authentication, #offeringFilterForm.likedBy))")
    public Response getOfferings(@QueryParam("page") @DefaultValue("0") final int page,
                                 @QueryParam("pageSize") @DefaultValue(DEFAULT_PAGE_SIZE_STRING) final int pageSize,
                                 @BeanParam final FilterForm offeringFilterForm,

                                 @QueryParam("recommendedForEvent") final Long eventId,
                                 @QueryParam("recommendedForOffering") final Long offeringId) {

        int notNullFields = 0;
        if (offeringFilterForm.hasNotNullFields()) {
            notNullFields++;
        }
        if (eventId != null) {
            notNullFields++;
        }
        if (offeringId != null) {
            notNullFields++;
        }
        if (notNullFields > 1) {
            throw new HttpBadRequestException("exception.offerings.badRequest");
        }

        if (eventId != null) {
            return getRecommendationsForEvent(eventId);
        } else if (offeringId != null) {
            return getRecommendationsForOffering(offeringId);
        } else {
            final OfferingFilter filter = offeringFilterForm.buildFilter();
            final PaginationUtils.PaginationInfo paginationInfo = calculatePage(page, pageSize, offeringService.getFilteredOfferingsCount(filter));

            final List<Offering> offerings = offeringService.getFilteredOfferings(filter, paginationInfo.getPage(), paginationInfo.getPageSize());
            final List<OfferingDTO> offeringDTOs = offerings.stream().map(o -> OfferingDTO.fromOffering(o, uriInfo)).collect(Collectors.toList());

            Response.ResponseBuilder response = Response.ok(new GenericEntity<List<OfferingDTO>>(offeringDTOs) {
            });

            PaginationUtils.addLinks(response, paginationInfo, uriInfo);

            return response.build();
        }
    }

    private Response getRecommendationsForEvent(final long eventId) {
        final Event event = eventService.getById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        final List<Offering> offerings = offeringService.getRecommendationsForEvent(event);
        final List<OfferingDTO> offeringDTOs = offerings.stream().map(o -> OfferingDTO.fromOffering(o, uriInfo)).collect(Collectors.toList());
        return Response.ok(new GenericEntity<List<OfferingDTO>>(offeringDTOs) {
        }).build();
    }

    private Response getRecommendationsForOffering(final long offeringId) {
        final Offering offering = offeringService.getById(offeringId).orElseThrow(() -> new OfferingNotFoundException(offeringId));
        final List<Offering> offerings = offeringService.getRecommendationsForOffering(RECOMMENDATIONS_BASE_QTY, RECOMMENDATIONS_QTY, offering);
        final List<OfferingDTO> offeringDTOs = offerings.stream().map(o -> OfferingDTO.fromOffering(o, uriInfo)).collect(Collectors.toList());
        return Response.ok(new GenericEntity<List<OfferingDTO>>(offeringDTOs) {
        }).build();
    }


    @POST
    @Consumes(value = {VndType.APPLICATION_OFFERING})
    public Response createOffering(@Valid final OfferingDTO offeringDTO) {

        List<Long> imagesIds = offeringDTO.getImages().stream().map(ImageDTO::getImageId).collect(Collectors.toList());
        final Offering offering = offeringService.createOffering(UserDTO.getIdFromUri(offeringDTO.getOwner()),
                offeringDTO.getName(), OfferingCategory.fromString(offeringDTO.getCategory()), offeringDTO.getDescription(), offeringDTO.getMinPrice(), offeringDTO.getMaxPrice(),
                PriceType.fromString(offeringDTO.getPriceType()), offeringDTO.getMaxGuests(), offeringDTO.getDistrict(),
                imagesIds);

        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(offering.getId())).build()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(value = {VndType.APPLICATION_OFFERING})
    @Transactional
    public Response getById(@PathParam("id") final long id) {
        final Offering offering = offeringService.getById(id).orElseThrow(() -> new OfferingNotFoundException(id));

        return Response.ok(new GenericEntity<OfferingDTO>(OfferingDTO.fromOffering(offering, uriInfo)) {
        }).build();

    }

    @PUT
    @Path("/{id}")
    @Produces(value = {VndType.APPLICATION_OFFERING})
    @Consumes(value = {VndType.APPLICATION_OFFERING})
    public Response updateOffering(@PathParam("id") final long id, @Valid final OfferingDTO offeringDTO) {
        final Offering offering = offeringService.getById(id).orElseThrow(() -> new OfferingNotFoundException(id));


        List<Long> oldImagesIds = offering.getImageIds();
        List<Long> newImagesIds = offeringDTO.getImages().stream().map(ImageDTO::getImageId).collect(Collectors.toList());

        List<Long> imagesToDelete = oldImagesIds.stream().filter(id1 -> !newImagesIds.contains(id1)).collect(Collectors.toList());

        offeringService.updateMyOffering(id, offeringDTO.getName(), offeringDTO.getDescription(), offeringDTO.getMaxGuests(),
                offeringDTO.getDistrict(), OfferingCategory.fromString(offeringDTO.getCategory()), offeringDTO.getMinPrice(),
                offeringDTO.getMaxPrice(), PriceType.fromString(offeringDTO.getPriceType()), newImagesIds, imagesToDelete);

        return Response.ok(new GenericEntity<OfferingDTO>(OfferingDTO.fromOffering(offering, uriInfo)) {
        }).build();

    }

    @DELETE
    @Path("/{id}")
    public Response deleteOffering(@PathParam("id") final long id) {
        offeringService.deleteOffering(id, env.getProperty("base_path") + UrisInfo.EVENTS);
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/likes")
    @Consumes(value = {VndType.APPLICATION_LIKE})
    @PreAuthorize("@securityAccessFunctions.hasUserAccess(authentication, #likeDTO.userId) and " +
            "@securityAccessFunctions.hasOfferingLikeAccess(authentication, #id)")
    public Response toggleLike(@PathParam("id") final long id, @Valid final LikeDTO likeDTO) {
        final Long userId = likeDTO.getUserId();
        final User user = userService.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        final Offering offering = offeringService.getById(id, userId).orElseThrow(() -> new OfferingNotFoundException(id));
        try {
            final boolean liked = offeringService.setOfferingLike(offering, user, true);
            final LikeDTO response = new LikeDTO(user.getId(), offering.getId(), liked, uriInfo);
            return Response.created(response.getSelf()).entity(response).build();
        } catch (IllegalArgumentException e) {
            throw new HttpBadRequestException("exception.offerings.likes.sameUser");
        }

    }

    @GET
    @Path("/{id}/likes/{userId}")
    @Produces(value = {VndType.APPLICATION_LIKE})
    public Response getLike(@PathParam("id") final long id, @PathParam("userId") final long userId) {
        boolean liked = offeringService.userLikedOffering(userId,id);
        final LikeDTO response = new LikeDTO(userId, id, liked, uriInfo);
        return Response.ok(response).build();
    }

    @DELETE
    @Path("/{id}/likes/{userId}")
    public Response deleteLike(@PathParam("id") final long id, @PathParam("userId") final long userId) {
        Offering offering = offeringService.getById(id, userId).orElseThrow(() -> new OfferingNotFoundException(id));
        User user = userService.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        offeringService.setOfferingLike(offering, user, false);
        return Response.noContent().build();
    }

}
