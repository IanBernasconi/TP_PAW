package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.eventOfferingRelation.ChatMessage;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;

import java.util.List;
import java.util.Optional;

public interface MessagesDao {
    ChatMessage addMessage(EventOfferingRelation relation, long senderId, String message);

    void markMessagesAsRead(long relationId, long userId);

    List<ChatMessage> getMessages(long relationId, int page, int pageSize);

    int getMessagesCount(long relationId);

    Optional<ChatMessage> getMessage(long messageId);

}
