package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.eventOfferingRelation.ChatMessage;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.exception.notFound.MessageNotFoundException;
import ar.edu.itba.paw.models.exception.notFound.RelationNotFoundException;
import ar.edu.itba.paw.services.EventOfferingService;
import ar.edu.itba.paw.services.MessageService;
import ar.edu.itba.paw.webapp.dto.ChatMessageDTO;
import ar.edu.itba.paw.webapp.mediaType.VndType;
import ar.edu.itba.paw.webapp.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

import static ar.edu.itba.paw.webapp.utils.PaginationUtils.*;

@Path("relations/{relationId}/messages")
@Controller
public class MessagesController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private EventOfferingService eventOfferingService;

    @Context
    private UriInfo uriInfo;

    @POST
    @Consumes(value = {VndType.APPLICATION_MESSAGE})
    @PreAuthorize("@securityAccessFunctions.hasUserAccess(authentication, #messageDTO.getSenderId())")
    public Response createMessage(@PathParam("relationId") final long id, @Valid final ChatMessageDTO messageDTO) {

        ChatMessage message = messageService.addMessage(id, messageDTO.getSenderId(), messageDTO.getMessage());
        ChatMessageDTO dto = ChatMessageDTO.fromMessage(message, uriInfo);
        return Response.created(dto.getSelf()).build();
    }

    @GET
    @Produces(value = {VndType.APPLICATION_MESSAGES})
    public Response getMessages(@PathParam("relationId") final long id,
                                @QueryParam("page") @DefaultValue("0") final int page,
                                @QueryParam("pageSize") @DefaultValue(DEFAULT_PAGE_SIZE_STRING) final int pageSize) {

        PaginationUtils.PaginationInfo paginationInfo = calculatePage(page, pageSize, messageService.getMessagesCount(id));
        List<ChatMessageDTO> messages = ChatMessageDTO.fromMessages(messageService.getMessages(id, paginationInfo.getPage(), paginationInfo.getPageSize()), uriInfo);

        Response.ResponseBuilder response = Response.ok(new GenericEntity<List<ChatMessageDTO>>(messages) {
        });
        PaginationUtils.addLinks(response, paginationInfo, uriInfo);
        return response.build();
    }

    @GET
    @Path("/{messageId}")
    @Produces(value = {VndType.APPLICATION_MESSAGE})
    public Response getMessage(@PathParam("relationId") final long id, @PathParam("messageId") final long messageId) {

        EventOfferingRelation relation = eventOfferingService.getRelationById(id).orElseThrow(() -> new RelationNotFoundException(id));
        ChatMessage message = messageService.getMessage(messageId).orElseThrow(() -> new MessageNotFoundException(messageId));

        if (message.getRelation().getRelationId() != relation.getRelationId()) {
            throw new MessageNotFoundException(messageId);
        }

        return Response.ok(new GenericEntity<ChatMessageDTO>(ChatMessageDTO.fromMessage(message, uriInfo)) {
        }).build();
    }


}
