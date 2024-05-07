package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.eventOfferingRelation.ChatMessage;
import ar.edu.itba.paw.webapp.validation.UriValid;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ChatMessageDTO {

    private URI self;

    private URI relation;
    @NotNull
    @UriValid(type = UserDTO.class)
    private URI sender;
    private URI receiver;
    private String message;
    private LocalDateTime timestamp;
    private boolean isRead;

    public ChatMessageDTO() {
    }

    public static ChatMessageDTO fromMessage(final ChatMessage chatMessage, final UriInfo uriInfo){
        final ChatMessageDTO dto = new ChatMessageDTO();
        dto.self = getSelfUri(chatMessage, uriInfo);
        dto.relation = RelationDTO.getSelfUri(chatMessage.getRelation().getRelationId(), uriInfo);
        dto.sender = UserDTO.getSelfUri(chatMessage.getSender().getId(), uriInfo);
        dto.receiver = UserDTO.getSelfUri(chatMessage.getReceiver().getId(), uriInfo);
        dto.message = chatMessage.getMessage();
        dto.timestamp = chatMessage.getTimestamp();
        dto.isRead = chatMessage.isRead();
        return dto;
    }

    public static URI getSelfUri(final ChatMessage chatMessage, final UriInfo uriInfo){
        return RelationDTO.getSelfUriBuilder(chatMessage.getRelation().getRelationId(), uriInfo).path("messages").path(String.valueOf(chatMessage.getMessageId())).build();
    }

    public static List<ChatMessageDTO> fromMessages(final List<ChatMessage> messages, final UriInfo uriInfo){
        return messages.stream().map(m -> fromMessage(m, uriInfo)).collect(Collectors.toList());
    }

    public long getSenderId(){
        return UserDTO.getIdFromUri(sender);
    }

    public long getRelationId(){
        return RelationDTO.getIdFromUri(relation);
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

    public URI getSender() {
        return sender;
    }

    public void setSender(URI sender) {
        this.sender = sender;
    }

    public URI getReceiver() {
        return receiver;
    }

    public void setReceiver(URI receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
