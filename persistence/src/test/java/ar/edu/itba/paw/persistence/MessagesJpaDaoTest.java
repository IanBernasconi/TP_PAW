package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.eventOfferingRelation.ChatMessage;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.exception.notFound.UserNotFoundException;
import ar.edu.itba.paw.models.offering.Offering;
import ar.edu.itba.paw.models.offering.OfferingCategory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static ar.edu.itba.paw.persistence.tablesInformation.EventOfferingTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.EventTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.MessageTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.OfferingTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.UserTableInfo.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
public class MessagesJpaDaoTest {

    private final static String MESSAGE_1 = "message1";
    private final static String MESSAGE_2 = "message2";

    @Autowired
    private MessagesDao messagesDao;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource ds;

    @PersistenceContext
    private EntityManager em;

    private User user1;
    private User user2;


    private EventOfferingRelation eventOfferingRelation1;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);

        em.createNativeQuery("TRUNCATE TABLE "+USER_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE "+MESSAGE_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE "+EVENT_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE "+OFFERING_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE "+EVENT_OFFERING_TABLE).executeUpdate();

        em.createNativeQuery("ALTER SEQUENCE users_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE messages_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE events_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE offerings_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE events_offerings_relation_id_seq RESTART WITH 1").executeUpdate();

        user1= new User("testpassword", "testuser1", "testemail1@test.com", true, EmailLanguages.getDefaultLanguage());
        user2=new User("testpassword", "testuser2", "testemail2@test.com", true, EmailLanguages.getDefaultLanguage());
        Event event1 = new Event(user2, "name1", "description1", Date.from(LocalDate.of(2024, 6, 30).atStartOfDay().toInstant(ZoneOffset.UTC)), 10, District.getDistrictByName("Recoleta"));
        Offering offering1 = new Offering("name1", user1, OfferingCategory.PHOTOGRAPHY, "description1", 10, 20, PriceType.PER_HOUR, 10, District.getDistrictByName("Recoleta"));
        eventOfferingRelation1 = new EventOfferingRelation(event1, offering1, OfferingStatus.PENDING);

        em.persist(user1);
        em.persist(user2);
        em.persist(event1);
        em.persist(offering1);
        em.persist(eventOfferingRelation1);
    }


    @Test
    public void testAddMessage(){

        messagesDao.addMessage(eventOfferingRelation1, user1.getId(), MESSAGE_1);
        em.flush();
        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, MESSAGE_TABLE, MESSAGE_RELATION_ID+"= " + eventOfferingRelation1.getRelationId() + " AND "+MESSAGE_SENDER_ID+" = " + user1.getId()+ " AND " + MESSAGE_RECEIVER_ID + " = " + user2.getId() + " AND "+MESSAGE_CONTENT+" = '" + MESSAGE_1 + "'"));
    }

    @Test(expected = Exception.class)
    public void testAddMessageInvalidRelation(){

        messagesDao.addMessage(new EventOfferingRelation(null,null, OfferingStatus.ACCEPTED), user1.getId(), MESSAGE_1);

        Assert.assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, MESSAGE_TABLE));

    }

    @Test
    public void testAddMessageInvalidRelationId(){

            messagesDao.addMessage(null, user1.getId(), MESSAGE_1);

            Assert.assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, MESSAGE_TABLE));
    }

    @Test(expected = UserNotFoundException.class)
    public void testAddMessageInvalidSenderId(){
        messagesDao.addMessage(eventOfferingRelation1, -1, MESSAGE_1);
    }

    @Test
    public void testMarkMessagesAsRead(){
        em.persist(new ChatMessage(eventOfferingRelation1, user1, user2, MESSAGE_1, LocalDateTime.now(), false));

        messagesDao.markMessagesAsRead(eventOfferingRelation1.getRelationId(), user2.getId()); // marks messages sent to user2 as read

        Assert.assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, MESSAGE_TABLE, MESSAGE_RELATION_ID+" = " + eventOfferingRelation1.getRelationId() + " AND "+MESSAGE_SENDER_ID+" = " + user1.getId()+ " AND "+MESSAGE_RECEIVER_ID+" = " + user2.getId() + " AND "+MESSAGE_CONTENT+" = '" + MESSAGE_1 + "' AND "+MESSAGE_IS_READ+" = true"));

    }

    @Test
    public void testMarkMessagesAsReadNoMessages(){

        messagesDao.markMessagesAsRead(eventOfferingRelation1.getRelationId(), user2.getId()); // marks messages sent to user2 as read

        Assert.assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, MESSAGE_TABLE));

    }

    @Test
    public void testGetMessagesFromRelationId(){

        ChatMessage message1 = new ChatMessage(eventOfferingRelation1, user1, user2, MESSAGE_1, LocalDateTime.now(), false);
        ChatMessage message2 = new ChatMessage(eventOfferingRelation1, user2, user1, MESSAGE_2, LocalDateTime.now(), false);
        em.persist(message1);
        em.persist(message2);

        List<ChatMessage> messages = messagesDao.getMessages(eventOfferingRelation1.getRelationId(),  0, 20);

        Assert.assertEquals(2, messages.size());
        Assert.assertEquals(eventOfferingRelation1.getRelationId(), messages.get(0).getRelation().getRelationId());
        Assert.assertEquals(user1.getId(), messages.get(0).getSender().getId());
        Assert.assertEquals(MESSAGE_1, messages.get(0).getMessage());
        Assert.assertEquals(eventOfferingRelation1.getRelationId(), messages.get(1).getRelation().getRelationId());
        Assert.assertEquals(user2.getId(), messages.get(1).getSender().getId());
        Assert.assertEquals(MESSAGE_2, messages.get(1).getMessage());

    }

    @Test
    public void testGetMessagesCount(){

          ChatMessage message1 = new ChatMessage(eventOfferingRelation1, user1, user2, MESSAGE_1, LocalDateTime.now(), false);
          ChatMessage message2 = new ChatMessage(eventOfferingRelation1, user2, user1, MESSAGE_2, LocalDateTime.now(), false);
          em.persist(message1);
          em.persist(message2);

          long count = messagesDao.getMessagesCount(eventOfferingRelation1.getRelationId());
          Assert.assertEquals(2, count);
    }

    @Test
    public void testGetMessagesCountInvalidRelationId(){

        long count = messagesDao.getMessagesCount(-1);
        Assert.assertEquals(0, count);
    }

    @Test
    public void testGetMessage(){

          ChatMessage message1 = new ChatMessage(eventOfferingRelation1, user1, user2, MESSAGE_1, LocalDateTime.now(), false);
          em.persist(message1);

          Optional<ChatMessage> maybeMessage = messagesDao.getMessage(message1.getMessageId());

          Assert.assertTrue(maybeMessage.isPresent());
          ChatMessage message = maybeMessage.get();
          Assert.assertEquals(message1.getMessageId(), message.getMessageId());
          Assert.assertEquals(message1.getRelation().getRelationId(), message.getRelation().getRelationId());
          Assert.assertEquals(message1.getSender().getId(), message.getSender().getId());
          Assert.assertEquals(message1.getReceiver().getId(), message.getReceiver().getId());
          Assert.assertEquals(message1.getMessage(), message.getMessage());
          Assert.assertEquals(message1.getTimestamp(), message.getTimestamp());
          Assert.assertEquals(message1.isRead(), message.isRead());
    }

    @Test
    public void testGetMessageInvalidMessageId(){

        Optional<ChatMessage> maybeMessage = messagesDao.getMessage(-1);

        Assert.assertFalse(maybeMessage.isPresent());
    }

}
