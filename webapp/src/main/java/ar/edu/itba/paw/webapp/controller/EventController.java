package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.events.EventsFilter;
import ar.edu.itba.paw.models.events.Guest;
import ar.edu.itba.paw.models.events.GuestStatus;
import ar.edu.itba.paw.models.exception.DuplicateEmailException;
import ar.edu.itba.paw.models.exception.notFound.EventNotFoundException;
import ar.edu.itba.paw.models.exception.notFound.GuestNotFoundException;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.dto.EventDTO;
import ar.edu.itba.paw.webapp.dto.GuestDTO;
import ar.edu.itba.paw.webapp.dto.UserDTO;
import ar.edu.itba.paw.webapp.dto.input.EventCreateDTO;
import ar.edu.itba.paw.webapp.dto.input.GuestStatusDTO;
import ar.edu.itba.paw.webapp.exception.httpExceptions.HttpBadRequestException;
import ar.edu.itba.paw.webapp.exception.httpExceptions.StatusConflictException;
import ar.edu.itba.paw.webapp.exception.httpExceptions.GuestAlreadyExistsException;
import ar.edu.itba.paw.webapp.form.DateRangeFilter;
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
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.webapp.utils.PaginationUtils.*;

@Path("events")
@Component
public class EventController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventService eventService;

    @Autowired
    private Environment env;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces(value = {VndType.APPLICATION_EVENTS})
    @PreAuthorize("@securityAccessFunctions.hasUserAccess(authentication, #userId)")
    public Response getEvents(@QueryParam("user") final Long userId,
                              @QueryParam("page") @DefaultValue("0") final Integer page,
                              @QueryParam("pageSize") @DefaultValue(DEFAULT_PAGE_SIZE_STRING) final Integer pageSize,
                              @BeanParam final DateRangeFilter dateRangeFilter) {

        if (userId == null) {
            throw new HttpBadRequestException("exception.event.user.null");
        }
        EventsFilter eventsFilter = new EventsFilter();
        try {
            dateRangeFilter.addRange(eventsFilter);
        } catch (DateTimeParseException e) {
            throw new HttpBadRequestException("exception.date.invalid");
        }

        final int eventCount = eventService.getEventsByUserIdCount(userId, eventsFilter);
        final PaginationUtils.PaginationInfo paginationInfo = PaginationUtils.calculatePage(page, pageSize, eventCount);
        final List<Event> events = eventService.getEventsByUserId(userId, paginationInfo.getPage(), paginationInfo.getPageSize(), eventsFilter);

        final List<EventDTO> eventDTOS = events.stream().map(event -> EventDTO.fromEvent(event, uriInfo)).collect(Collectors.toList());
        Response.ResponseBuilder response = Response.ok(new GenericEntity<List<EventDTO>>(eventDTOS) {
        });
        PaginationUtils.addLinks(response, paginationInfo, uriInfo);

        return response.build();

    }

    @POST
    @Consumes(value = {VndType.APPLICATION_EVENT})
    public Response createEvent(@Valid final EventCreateDTO eventDTO) {
        final Event event = eventService.createEvent(UserDTO.getIdFromUri(eventDTO.getOwner()), eventDTO.getName(), eventDTO.getDescription(), eventDTO.getNumberOfGuests(), eventDTO.getDate(), eventDTO.getDistrict());
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(event.getId())).build()).build();
    }


    @GET
    @Path("/{id}")
    @Produces(value = {VndType.APPLICATION_EVENT})
    public Response getEvent(@PathParam("id") final long id) {
        final Event event = eventService.getById(id).orElseThrow(() -> new EventNotFoundException(id));
        return Response.ok(EventDTO.fromEvent(event, uriInfo)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteEvent(@PathParam("id") final long id) {
        final Event event = eventService.getById(id).orElseThrow(() -> new EventNotFoundException(id));
        eventService.deleteEvent(event, env.getProperty("base_path") + UrisInfo.MY_SERVICES);

        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}")
    @Produces(value = {VndType.APPLICATION_EVENT})
    @Consumes(value = {VndType.APPLICATION_EVENT})
    public Response updateEvent(@PathParam("id") final long id, @Valid final EventCreateDTO eventDTO) {

        Event event = eventService.getById(id).orElseThrow(() -> new EventNotFoundException(id));

        eventService.updateMyEvent(event,
                eventDTO.getName(),
                eventDTO.getDescription(),
                eventDTO.getNumberOfGuests(),
                eventDTO.getDate(),
                eventDTO.getDistrict(), env.getProperty("base_path") + UrisInfo.MY_SERVICES);
        return Response.ok(EventDTO.fromEvent(event, uriInfo)).build();
    }


    @GET
    @Path("/{id}/guests")
    @Produces(value = {VndType.APPLICATION_GUESTS})
    @Transactional
    public Response getGuests(@PathParam("id") final long id,
                              @QueryParam("page") @DefaultValue("0") final int page,
                              @QueryParam("pageSize") @DefaultValue(DEFAULT_PAGE_SIZE_STRING) final int pageSize) {
        Event event = eventService.getById(id).orElseThrow(() -> new EventNotFoundException(id));

        PaginationUtils.PaginationInfo paginationInfo = calculatePage(page, pageSize, eventService.getGuestsByEventIdCount(id));

        final List<Guest> guests = eventService.getGuestsByEventId(event.getId(), paginationInfo.getPage(), paginationInfo.getPageSize());
        final List<GuestDTO> guestDTOs = guests.stream().map(guest -> GuestDTO.fromGuest(guest, uriInfo)).collect(java.util.stream.Collectors.toList());

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<GuestDTO>>(guestDTOs) {
        });
        PaginationUtils.addLinks(responseBuilder, paginationInfo, uriInfo);

        return responseBuilder.build();
    }

    @POST
    @Path("/{id}/guests")
    @Consumes(value = {VndType.APPLICATION_GUEST})
    public Response addGuest(@PathParam("id") final long id, @Valid final GuestDTO guestDTO) {
        Event event = eventService.getById(id).orElseThrow(() -> new EventNotFoundException(id));

        String email = guestDTO.getEmail();

        try {
            final Guest guest = eventService.addGuest(event, email);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(event.getId())).path("guests").path(String.valueOf(guest.getId())).build()).build();
        } catch (DuplicateEmailException e) {
            throw new GuestAlreadyExistsException(email);
        } catch (IllegalArgumentException e) {
            throw new HttpBadRequestException("exception.event.past.guest", id);
        }
    }

    @PATCH
    @Path("/{id}/guests/{guestId}")
    @Consumes(value = {VndType.APPLICATION_GUEST_STATUS})
    public Response updateGuest(@PathParam("id") final long event, @PathParam("guestId") final long guestId, @Valid final GuestStatusDTO guestDTO) {

        Guest guest = eventService.getGuestById(guestId).orElseThrow(() -> new GuestNotFoundException(guestId));
        if (guest.getEvent().getId() != event) {
            throw new HttpBadRequestException("exception.event.guest.mismatch", guestId, event);
        }

        if (guest.getStatus() == GuestStatus.NEW && guestDTO.getStatus() == GuestStatus.PENDING) {
            String baseUrl = env.getProperty("base_path") + UrisInfo.EVENTS + "/" + event + "/" + UrisInfo.GUESTS + "/" + UrisInfo.INVITE + "/";
            eventService.sendInvite(guest, baseUrl, env.getProperty("base_path"));
            return Response.ok().build();
        } else if (guest.getStatus() == GuestStatus.PENDING) {
            if (guestDTO.getStatus() == GuestStatus.ACCEPTED) {
                eventService.answerGuestInvitation(event, guestId, GuestStatus.ACCEPTED, guest.getToken());
                return Response.ok().build();
            } else if (guestDTO.getStatus() == GuestStatus.REJECTED) {
                eventService.answerGuestInvitation(event, guestId, GuestStatus.REJECTED, guest.getToken());
                return Response.ok().build();
            } else {
                throw new HttpBadRequestException("exception.event.guest.status.mismatch", guestId, event);
            }
        } else if (guest.getStatus() == GuestStatus.ACCEPTED || guest.getStatus() == GuestStatus.REJECTED) {
            throw new StatusConflictException("exception.event.guest.status.conflict", guestId, event);
        } else {
            throw new HttpBadRequestException("exception.event.guest.status.mismatch", guestId, event);
        }
    }

    @DELETE
    @Path("/{id}/guests/{guestId}")
    public Response removeGuest(@PathParam("id") final long id, @PathParam("guestId") final long guestId) {

        Guest guest = eventService.getGuestById(guestId).orElseThrow(() -> new GuestNotFoundException(guestId));
        if (guest.getEvent().getId() != id) {
            throw new HttpBadRequestException("exception.event.guest.mismatch", guestId, id);
        }

        eventService.removeGuest(guest.getEvent().getId(), guest.getId());

        return Response.noContent().build();
    }

    @GET
    @Path("/{id}/guests/{guestId}")
    @Produces(value = {VndType.APPLICATION_GUEST})
    public Response getGuest(@PathParam("id") final long id, @PathParam("guestId") final long guestId) {
        Guest guest = eventService.getGuestById(guestId).orElseThrow(() -> new GuestNotFoundException(guestId));
        if (guest.getEvent().getId() != id) {
            throw new HttpBadRequestException("exception.event.guest.mismatch", guestId, id);
        }
        return Response.ok(GuestDTO.fromGuest(guest, uriInfo)).build();
    }


}