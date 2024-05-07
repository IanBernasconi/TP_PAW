package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.RelationFilter;
import ar.edu.itba.paw.models.exception.notFound.RelationNotFoundException;
import ar.edu.itba.paw.services.EventOfferingService;
import ar.edu.itba.paw.services.MessageService;
import ar.edu.itba.paw.webapp.dto.RelationDTO;
import ar.edu.itba.paw.webapp.dto.input.RelationReadStatusDTO;
import ar.edu.itba.paw.webapp.dto.input.RelationStatusDTO;
import ar.edu.itba.paw.webapp.exception.httpExceptions.HttpBadRequestException;
import ar.edu.itba.paw.webapp.form.DateRangeFilter;
import ar.edu.itba.paw.webapp.form.ProviderRelationsFilter;
import ar.edu.itba.paw.webapp.mediaType.VndType;
import ar.edu.itba.paw.webapp.utils.PaginationUtils;
import ar.edu.itba.paw.webapp.utils.UrisInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import static ar.edu.itba.paw.webapp.utils.PaginationUtils.*;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

@Path("relations")
@Controller
public class RelationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelationController.class);

    @Autowired
    private EventOfferingService eventOfferingService;

    @Autowired
    private MessageService messageService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    Environment env;

    @GET
    @Path("/{id}")
    @Produces(value = {VndType.APPLICATION_RELATION})
    public Response getById(@PathParam("id") final long id) {
        final EventOfferingRelation relation = eventOfferingService.getRelationById(id).orElseThrow(() -> new RelationNotFoundException(id));
        return Response.ok(RelationDTO.fromRelation(relation, uriInfo)).build();
    }

    @GET
    @Produces(value = {VndType.APPLICATION_RELATIONS})
    @Transactional
    @PreAuthorize("(#providerId == null || @securityAccessFunctions.hasUserAccess(authentication, #providerId)) and " +
            "(#eventId == null || @securityAccessFunctions.isEventOwner(authentication, #eventId)) and " +
            "(#providerRelationsFilter.offeringId == null || @securityAccessFunctions.isOfferingOwner(authentication, #providerRelationsFilter.offeringId))")
    public Response getRelations(@QueryParam("provider") final Long providerId,
                                 @BeanParam final ProviderRelationsFilter providerRelationsFilter,
                                 @BeanParam final DateRangeFilter dateRangeFilter,
                                 @QueryParam("event") final Long eventId,
                                 @QueryParam("page") @DefaultValue("0") final int page,
                                 @QueryParam("pageSize") @DefaultValue(DEFAULT_PAGE_SIZE_STRING) final int pageSize) {

        if (providerId == null && eventId == null) {
            throw new HttpBadRequestException("exception.relation.missingProviderOrEvent");
        }

        RelationFilter filter = new RelationFilter();
        try {
            dateRangeFilter.addRange(filter);
        } catch (DateTimeParseException e) {
            throw new HttpBadRequestException("exception.date.invalid");
        }
        providerRelationsFilter.addToRelationFilter(filter);

        if (providerId != null) {
            filter.withProviderId(providerId);
        }
        if (eventId != null) {
            filter.withEventId(eventId);
        }

        final int relationCount = eventOfferingService.getRelationsCount(filter);
        final PaginationUtils.PaginationInfo paginationInfo = PaginationUtils.calculatePage(page, pageSize, relationCount);
        final List<RelationDTO> relations = RelationDTO.fromRelations(eventOfferingService.getRelations(filter, paginationInfo.getPage(), paginationInfo.getPageSize()), uriInfo);

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<RelationDTO>>(relations) {
        });
        PaginationUtils.addLinks(responseBuilder, paginationInfo, uriInfo);
        return responseBuilder.build();
    }

    @DELETE
    @Path("/{id}")
    public Response removeOfferingFromEvent(@PathParam("id") final long id) {
        EventOfferingRelation relation = eventOfferingService.getRelationById(id).orElseThrow(() -> new RelationNotFoundException(id));
        eventOfferingService.deleteRelation(relation, env.getProperty("base_path") + UrisInfo.MY_SERVICES);
        return Response.noContent().build();
    }

    @POST
    @Produces(value = {VndType.APPLICATION_RELATION})
    @Consumes(value = {VndType.APPLICATION_RELATION})
    @PreAuthorize("@securityAccessFunctions.hasEventAccess(authentication, #relationDTO.eventId)")
    public Response createRelation(@Valid final RelationDTO relationDTO) {
        if (relationDTO.getEventId() == null || relationDTO.getOfferingId() == null) {
            throw new HttpBadRequestException("exception.relation.create.invalidEventOrOffering");
        }
        try {
            EventOfferingRelation relation = eventOfferingService.createRelation(relationDTO.getEventId(), relationDTO.getOfferingId());
            return Response.created(RelationDTO.getSelfUri(relation.getRelationId(), uriInfo)).build();
        } catch (IllegalArgumentException e) {
            throw new HttpBadRequestException("exception.relation.create");
        }
    }


    @PATCH
    @Path("/{id}")
    @Produces(value = {VndType.APPLICATION_RELATION})
    @Consumes(value = {VndType.APPLICATION_RELATION_STATUS})
    public Response updateRelation(@PathParam("id") final long id, @Valid final RelationStatusDTO relationStatusDTO) {
        try {
            EventOfferingRelation relation = eventOfferingService.getRelationById(id).orElseThrow(() -> new RelationNotFoundException(id));
            String seeDetailsUrlForProvider = env.getProperty("base_path") + UrisInfo.MY_SERVICES + "?tab=conversations&service=" + relation.getOffering().getId() + "&conversation=" + relation.getRelationId();
            eventOfferingService.changeOfferingStatus(id, relationStatusDTO.getStatus(), seeDetailsUrlForProvider, env.getProperty("base_path") + UrisInfo.EVENTS + "/" + relation.getEvent().getId());
            return Response.ok(new GenericEntity<RelationDTO>(RelationDTO.fromRelation(relation, uriInfo)) {
            }).build();
        } catch (IllegalArgumentException e) {
            throw new HttpBadRequestException("exception.relation.status.notUpdated");
        }
    }

    @PATCH
    @Path("/{id}")
    @Produces(value = {VndType.APPLICATION_RELATION})
    @Consumes(value = {VndType.APPLICATION_RELATION_READ_STATUS})
    @PreAuthorize("@securityAccessFunctions.hasUserAccess(authentication, #readStatusDTO.userId)")
    public Response markConversationAsRead(@PathParam("id") final long id, @Valid final RelationReadStatusDTO readStatusDTO) {

        EventOfferingRelation relation = eventOfferingService.getRelationById(id).orElseThrow(() -> new RelationNotFoundException(id));

        if (readStatusDTO.isRead()) {
            messageService.markMessagesAsRead(id, readStatusDTO.getUserId());
        }

        RelationDTO updatedRelation = RelationDTO.fromRelation(relation, uriInfo);
        if (Objects.equals(relation.getProviderId(), readStatusDTO.getUserId())) {
            updatedRelation.setProviderUnreadMessagesCount(0);
        } else {
            updatedRelation.setOrganizerUnreadMessagesCount(0);
        }
        return Response.ok(updatedRelation).build();
    }


}
