package ar.edu.itba.paw.models;

import ar.edu.itba.paw.models.eventOfferingRelation.ChatMessage;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;

import javax.persistence.*;

// READ ONLY CLASS
@Entity
@Table(name = "conversation")
public class Conversation {

    @Id
    @Column(name = "relation_id")
    private long relationId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "relation_id")
    private EventOfferingRelation relation;

    @OneToOne
    @JoinColumn(name = "last_message")
    private ChatMessage lastMessage;

    @Column(name = "provider_unread_messages")
    private int providerUnreadMessagesCount;

    @Column(name = "organizer_unread_messages")
    private int organizerUnreadMessagesCount;

    protected Conversation() {}

    public ChatMessage getLastMessage() {
        return lastMessage;
    }

    public int getProviderUnreadMessagesCount() {
        return providerUnreadMessagesCount;
    }

    public int getOrganizerUnreadMessagesCount() {
        return organizerUnreadMessagesCount;
    }

    public EventOfferingRelation getRelation() {
        return relation;
    }

    public long getRelationId() {
        return relationId;
    }
}
