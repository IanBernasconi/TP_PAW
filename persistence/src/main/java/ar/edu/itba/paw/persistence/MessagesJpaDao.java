package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.eventOfferingRelation.ChatMessage;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.exception.notFound.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.persistence.tablesInformation.MessageTableInfo.*;

@Repository
public class MessagesJpaDao implements MessagesDao {

    public static final Logger LOGGER = LoggerFactory.getLogger(MessagesJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public ChatMessage addMessage(EventOfferingRelation relation, long senderId, String message) {
        if (relation == null) {
            LOGGER.error("Relation is null");
            return null;
        }
        User eventOwner = relation.getEvent().getUser();
        User offeringOwner = relation.getOffering().getOwner();

        if (eventOwner.getId() != senderId && offeringOwner.getId() != senderId) {
            LOGGER.error("Sender is not part of the relation");
            throw new UserNotFoundException(senderId);
        }

        User receiver = eventOwner.getId() == senderId ? offeringOwner : eventOwner;
        User sender = eventOwner.getId() == senderId ? eventOwner : offeringOwner;

        ChatMessage newMessage = new ChatMessage(relation, sender, receiver, message, LocalDateTime.now(), false);

        em.persist(newMessage);

        return newMessage;
    }

    @Override
    @Transactional
    public void markMessagesAsRead(long relationId, long userId) {
        EventOfferingRelation relation = em.find(EventOfferingRelation.class, relationId);
        User eventOwner = relation.getEvent().getUser();
        User receiver = eventOwner.getId() == userId ? eventOwner : relation.getOffering().getOwner();

        em.createQuery("UPDATE ChatMessage message SET isRead = true WHERE message.relation = :relation AND message.receiver = :receiver")
                .setParameter("relation", relation)
                .setParameter("receiver", receiver)
                .executeUpdate();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessage> getMessages(long relationId, int page, int pageSize) {
        if (page < 0) {
            page = 0;
        }
        if (pageSize < 1) {
            pageSize = 1;
        }

        final Query nativeQuery = em.createNativeQuery("SELECT " + MESSAGE_MESSAGE_ID + " FROM " + MESSAGE_TABLE + " WHERE " + MESSAGE_RELATION_ID + " = :relationId ORDER BY " + MESSAGE_TIMESTAMP + " DESC");
        nativeQuery.setParameter("relationId", relationId);
        nativeQuery.setMaxResults(pageSize);
        nativeQuery.setFirstResult(page * pageSize);

        @SuppressWarnings("unchecked") final List<Long> idList = ((List<Object>) nativeQuery.getResultList()).stream()
                .map(Object::toString)
                .map(Long::valueOf).collect(Collectors.toList());

        return em.createQuery("FROM ChatMessage message WHERE message.messageId IN :idList order by message.timestamp asc", ChatMessage.class)
                .setParameter("idList", idList)
                .getResultList();
    }

    @Override
    public int getMessagesCount(long relationId) {
        return ((Number) em.createQuery("SELECT COUNT(message) FROM ChatMessage message WHERE message.relation.id = :relationId")
                .setParameter("relationId", relationId)
                .getSingleResult()).intValue();
    }

    @Override
    public Optional<ChatMessage> getMessage(long messageId) {
        return Optional.ofNullable(em.find(ChatMessage.class, messageId));
    }
}
