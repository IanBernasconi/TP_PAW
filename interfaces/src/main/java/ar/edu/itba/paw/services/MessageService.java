package ar.edu.itba.paw.services;


import ar.edu.itba.paw.models.eventOfferingRelation.ChatMessage;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;

import java.util.List;
import java.util.Optional;

public interface MessageService {

    ChatMessage addMessage(EventOfferingRelation relation, long senderId, String message);

    ChatMessage addMessage(long relationId, long senderId, String message);

    List<ChatMessage> getMessages(long relationId, int page, int pageSize);

    int getMessagesCount(long relationId);

    Optional<ChatMessage> getMessage(long messageId);

    void markMessagesAsRead(long relationId, long userId);

}
