package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.webapp.validation.UriValid;
import ar.edu.itba.paw.webapp.validation.UriValidation;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

public class RelationDTO implements UriValidation {

    private long relationId;

    @NotNull(message = "{invalid.RelationDTO.status}")
    private OfferingStatus status;

    private URI self;

    @NotNull
    @UriValid(type = EventDTO.class)
    private URI event;
    @NotNull
    @UriValid(type = OfferingDTO.class)
    private URI offering;
    private URI review;
    private URI lastMessage;
    private URI messages;
    private URI organizer;
    private URI provider;
    private int providerUnreadMessagesCount;
    private int organizerUnreadMessagesCount;

    public static RelationDTO fromRelation(final EventOfferingRelation eventOfferingRelation, final UriInfo uriInfo) {
        final RelationDTO dto = new RelationDTO();
        dto.relationId = eventOfferingRelation.getRelationId();
        dto.self = getSelfUri(eventOfferingRelation.getRelationId(), uriInfo);
        dto.event = EventDTO.getSelfUri(eventOfferingRelation.getEvent().getId(), uriInfo);
        dto.offering = OfferingDTO.getSelfUri(eventOfferingRelation.getOffering().getId(), uriInfo);
        dto.organizer = UserDTO.getSelfUri(eventOfferingRelation.getEvent().getUser().getId(), uriInfo);
        dto.provider = UserDTO.getSelfUri(eventOfferingRelation.getOffering().getOwner().getId(), uriInfo);
        dto.status = eventOfferingRelation.getStatus();
        if (eventOfferingRelation.getReview() != null){
           dto.review = ReviewDTO.getSelfUri(eventOfferingRelation.getReview().getRelationId(), uriInfo);
        }
        if (eventOfferingRelation.getConversation() != null) {
            dto.lastMessage = ChatMessageDTO.getSelfUri(eventOfferingRelation.getConversation().getLastMessage(), uriInfo);
            dto.providerUnreadMessagesCount = eventOfferingRelation.getConversation().getProviderUnreadMessagesCount();
            dto.organizerUnreadMessagesCount = eventOfferingRelation.getConversation().getOrganizerUnreadMessagesCount();
        }
        dto.messages = getSelfUriBuilder(eventOfferingRelation.getRelationId(), uriInfo).path("messages").build();

        return dto;
    }

    public static URI getSelfUri(final long id, final UriInfo uriInfo){
        return uriInfo.getBaseUriBuilder().path("relations").path(String.valueOf(id)).build();
    }

    public static UriBuilder getSelfUriBuilder(final long id, final UriInfo uriInfo){
        return uriInfo.getBaseUriBuilder().path("relations").path(String.valueOf(id));
    }

    public static List<RelationDTO> fromRelations(final List<EventOfferingRelation> eventOfferingRelations, final UriInfo uriInfo) {
        return eventOfferingRelations.stream().map(r -> fromRelation(r, uriInfo)).collect(Collectors.toList());
    }

    public static long getIdFromUri(final URI uri) {
        return Long.parseLong(uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1));
    }

    @Override
    public boolean isValidUri(URI uri) {
        return isValidUri(uri, "relations");
    }

    public long getRelationId() {
        return relationId;
    }

    public void setRelationId(long relationId) {
        this.relationId = relationId;
    }

    public Long getEventId(){
        return EventDTO.getIdFromUri(event);
    }

    public Long getOfferingId(){
        return OfferingDTO.getIdFromUri(offering);
    }

    public OfferingStatus getStatus() {
        return status;
    }

    public void setStatus(OfferingStatus status) {
        this.status = status;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getEvent() {
        return event;
    }

    public void setEvent(URI event) {
        this.event = event;
    }

    public URI getOffering() {
        return offering;
    }

    public void setOffering(URI offering) {
        this.offering = offering;
    }

    public URI getReview() {
        return review;
    }

    public void setReview(URI review) {
        this.review = review;
    }

    public URI getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(URI lastMessage) {
        this.lastMessage = lastMessage;
    }

    public URI getMessages() {
        return messages;
    }

    public void setMessages(URI messages) {
        this.messages = messages;
    }

    public URI getOrganizer() {
        return organizer;
    }

    public void setOrganizer(URI organizer) {
        this.organizer = organizer;
    }

    public URI getProvider() {
        return provider;
    }

    public void setProvider(URI provider) {
        this.provider = provider;
    }

    public int getProviderUnreadMessagesCount() {
        return providerUnreadMessagesCount;
    }

    public void setProviderUnreadMessagesCount(int providerUnreadMessagesCount) {
        this.providerUnreadMessagesCount = providerUnreadMessagesCount;
    }

    public int getOrganizerUnreadMessagesCount() {
        return organizerUnreadMessagesCount;
    }

    public void setOrganizerUnreadMessagesCount(int organizerUnreadMessagesCount) {
        this.organizerUnreadMessagesCount = organizerUnreadMessagesCount;
    }
}
