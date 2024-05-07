package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.eventOfferingRelation.ChatMessage;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.exception.notFound.RelationNotFoundException;
import ar.edu.itba.paw.persistence.EventOfferingDao;
import ar.edu.itba.paw.persistence.MessagesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessagesDao messagesDao;

    @Autowired
    private EventOfferingDao eventOfferingDao;

//    @Override
//    public List<Conversation> getConversations(Long offeringUserId, Long eventUserId, boolean past, boolean rejected){
//        return messagesDao.getConversations(offeringUserId, eventUserId, past, rejected);
//    }

//    @Override
//    public Conversation getConversation(long relationId) {
//        return messagesDao.getConversation(relationId);
//    }

    @Override
    public ChatMessage addMessage(EventOfferingRelation relation, long senderId, String message) {
        return messagesDao.addMessage(relation, senderId, message);
    }

    @Override
    public ChatMessage addMessage(long relationId, long senderId, String message) {
        final EventOfferingRelation relation = eventOfferingDao.getRelation(relationId).orElseThrow(() -> new RelationNotFoundException(relationId));

        return messagesDao.addMessage(relation, senderId, message);
    }

    @Override
    public List<ChatMessage> getMessages(long relationId, int page, int pageSize) {
        return messagesDao.getMessages(relationId, page, pageSize);
    }

    @Override
    public int getMessagesCount(long relationId) {
        return messagesDao.getMessagesCount(relationId);
    }

    @Override
    public Optional<ChatMessage> getMessage(long messageId) {
        return messagesDao.getMessage(messageId);
    }

    @Override
    public void markMessagesAsRead(long relationId, long userId) {
        messagesDao.markMessagesAsRead(relationId, userId);
    }
}
