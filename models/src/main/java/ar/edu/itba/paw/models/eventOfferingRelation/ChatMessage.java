package ar.edu.itba.paw.models.eventOfferingRelation;

import ar.edu.itba.paw.models.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "messages_id_seq")
    @SequenceGenerator(sequenceName = "messages_id_seq", name = "messages_id_seq", allocationSize = 1)
    @Column(name = "id")
    private long messageId;

    @ManyToOne
    @JoinColumn(name = "relation_id")
    private EventOfferingRelation relation;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Column(name = "content")
    private String message;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT now()")
    private LocalDateTime timestamp;

    @Column(name = "read", nullable = false, columnDefinition = "boolean default false")
    private boolean isRead;

    protected ChatMessage() {}


    public ChatMessage(EventOfferingRelation relation, User sender, User receiver, String message, LocalDateTime timestamp, boolean isRead) {
        this.relation = relation;
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public EventOfferingRelation getRelation() {
        return relation;
    }

    public void setRelation(EventOfferingRelation relation) {
        this.relation = relation;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return timestamp.format(formatter);
    }

    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return timestamp.format(formatter);
    }

}
