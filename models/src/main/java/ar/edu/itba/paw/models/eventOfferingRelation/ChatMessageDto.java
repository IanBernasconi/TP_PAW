package ar.edu.itba.paw.models.eventOfferingRelation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ChatMessageDto {

    private final long messageId;

    private final long relationId;

    private final long senderId;

    private final long receiverId;

    private final String message;

    private final LocalDateTime timestamp;

    public ChatMessageDto(long messageId, long relationId, long senderId, long receiverId, String message, LocalDateTime timestamp) {
        this.messageId = messageId;
        this.relationId = relationId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public ChatMessageDto(ChatMessage chatMessage) {
        this.messageId = chatMessage.getMessageId();
        this.relationId = chatMessage.getRelation().getRelationId();
        this.senderId = chatMessage.getSender().getId();
        this.receiverId = chatMessage.getReceiver().getId();
        this.message = chatMessage.getMessage();
        this.timestamp = chatMessage.getTimestamp();
    }

    public long getMessageId() {
        return messageId;
    }

    public long getRelationId() {
        return relationId;
    }

    public long getSenderId() {
        return senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return timestamp.format(formatter);
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return timestamp.format(formatter);
    }

    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return timestamp.format(formatter);
    }

    public String getTimeOrDate() {
        LocalDateTime now = LocalDateTime.now();
        if (timestamp.getYear() == now.getYear() && timestamp.getMonth() == now.getMonth() && timestamp.getDayOfMonth() == now.getDayOfMonth()) {
            return getFormattedTime();
        } else {
            return getFormattedDate();
        }
    }
}
