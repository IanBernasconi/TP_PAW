package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.EmailLanguages;
import ar.edu.itba.paw.models.PriceType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.eventOfferingRelation.RelationFilter;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.offering.Offering;
import ar.edu.itba.paw.models.offering.OfferingCategory;
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
import java.time.ZoneOffset;
import java.util.*;

import static ar.edu.itba.paw.persistence.tablesInformation.EventOfferingTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.EventTableInfo.EVENT_TABLE;
import static ar.edu.itba.paw.persistence.tablesInformation.OfferingTableInfo.OFFERING_TABLE;
import static ar.edu.itba.paw.persistence.tablesInformation.UserTableInfo.USER_TABLE;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
public class EventOfferingJpaDaoTest {


    @Autowired
    private EventOfferingDao eventOfferingDao;

    private JdbcTemplate jdbcTemplate;


    @Autowired
    private DataSource ds;

    @PersistenceContext
    private EntityManager em;

    private User user1;


    private Event event1;
    private Event event2;
    private Event event3;

    private Offering offering1;


    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);

        em.createNativeQuery("TRUNCATE TABLE "+USER_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE "+EVENT_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE "+OFFERING_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE "+EVENT_OFFERING_TABLE).executeUpdate();

        em.createNativeQuery("ALTER SEQUENCE users_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE events_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE offerings_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE events_offerings_relation_id_seq RESTART WITH 1").executeUpdate();


        user1= new User("testpassword", "testuser1", "testemail1@test.com", true, EmailLanguages.getDefaultLanguage());
        User user2 = new User("testpassword", "testuser2", "testemail2@test.com", true, EmailLanguages.getDefaultLanguage());
        event1=new Event(user2,"name1", "description1", Date.from(LocalDate.of(2024,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)), 10, District.getDistrictByName("Recoleta"));
        event2=new Event(user1,"name2", "description2", Date.from(LocalDate.of(2024,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Colegiales"));
        event3=new Event(user1,"name3", "description3", Date.from(LocalDate.of(2024,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Flores"));
        offering1 = new Offering("name1", user1, OfferingCategory.PHOTOGRAPHY, "description1",  10, 20, PriceType.PER_HOUR, 10, District.getDistrictByName("Recoleta"));

        em.persist(user1);
        em.persist(user2);
        em.persist(event1);
        em.persist(event2);
        em.persist(event3);
        em.persist(offering1);
    }

    @Test
    public void testCreateRelation(){
        Event event = new Event(user1,"name4", "description4", Date.from(LocalDate.of(2023,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Flores"));
        Offering offering = new Offering("name4", user1, OfferingCategory.PHOTOGRAPHY, "description4", 10, 20, PriceType.PER_HOUR, 10, District.getDistrictByName("Recoleta"));

        em.persist(event);
        em.persist(offering);

        EventOfferingRelation relation = new EventOfferingRelation(event, offering, OfferingStatus.ACCEPTED);

        EventOfferingRelation createdRelation = eventOfferingDao.createRelation(event,offering);

        assertNotNull(createdRelation);

        assertEquals(relation.getEvent().getId(), createdRelation.getEvent().getId());

        assertEquals(relation.getOffering().getId(), createdRelation.getOffering().getId());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateRelationInvalidEventId(){
        Event event = new Event(user1,"name4", "description4", Date.from(LocalDate.of(2023,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Flores"));
        Offering offering = new Offering("name4", user1, OfferingCategory.PHOTOGRAPHY, "description4", 10, 20, PriceType.PER_HOUR, 10, District.getDistrictByName("Recoleta"));

        em.persist(event);
        em.persist(offering);

        EventOfferingRelation createdRelation = eventOfferingDao.createRelation(null,offering);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateRelationInvalidOfferingId(){
        Event event = new Event(user1,"name4", "description4", Date.from(LocalDate.of(2023,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Flores"));
        Offering offering = new Offering("name4", user1, OfferingCategory.PHOTOGRAPHY, "description4", 10, 20, PriceType.PER_HOUR, 10, District.getDistrictByName("Recoleta"));

        em.persist(event);
        em.persist(offering);

        EventOfferingRelation createdRelation = eventOfferingDao.createRelation(event,null);

    }

    @Test
    public void testGetRelationsCount(){
        EventOfferingRelation relation1 = new EventOfferingRelation(event1, offering1, OfferingStatus.ACCEPTED);
        EventOfferingRelation relation2 = new EventOfferingRelation(event2, offering1, OfferingStatus.ACCEPTED);
        EventOfferingRelation relation3 = new EventOfferingRelation(event3, offering1, OfferingStatus.PENDING);
        em.persist(relation1);
        em.persist(relation2);
        em.persist(relation3);

        RelationFilter filter = new RelationFilter();

        int count = eventOfferingDao.getRelationsCount(filter);

        assertEquals(3, count);
    }

    @Test
    public void testGetRelations(){
        EventOfferingRelation relation1 = new EventOfferingRelation(event1, offering1, OfferingStatus.ACCEPTED);
        EventOfferingRelation relation2 = new EventOfferingRelation(event2, offering1, OfferingStatus.ACCEPTED);
        EventOfferingRelation relation3 = new EventOfferingRelation(event3, offering1, OfferingStatus.PENDING);
        em.persist(relation1);
        em.persist(relation2);
        em.persist(relation3);

        RelationFilter filter = new RelationFilter();

        List<EventOfferingRelation> relations = eventOfferingDao.getRelations(filter, 0, 10);

        assertEquals(3, relations.size());
        assertTrue(relations.contains(relation1));
        assertTrue(relations.contains(relation2));
        assertTrue(relations.contains(relation3));
    }

    @Test
    public void testGetRelationsNoRelations(){
        RelationFilter filter = new RelationFilter();

        List<EventOfferingRelation> relations = eventOfferingDao.getRelations(filter, 0, 10);

        assertEquals(0, relations.size());
    }

    @Test
    public void testDeleteRelation(){
        EventOfferingRelation relation = new EventOfferingRelation(event1, offering1, OfferingStatus.ACCEPTED);

        em.persist(relation);

        eventOfferingDao.deleteRelation(relation.getRelationId());

        em.flush();

        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, EVENT_OFFERING_TABLE,
                EVENT_OFFERING_RELATION_ID + " = " + relation.getRelationId()));
    }

    @Test
    public void testDeleteRelationNoRelation(){
        eventOfferingDao.deleteRelation(-1);

        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_OFFERING_TABLE));
    }



    @Test
    public void testChangeEventOfferingStatusToPending() {
        EventOfferingRelation relation = new EventOfferingRelation(event1, offering1, OfferingStatus.NEW);
        em.persist(relation);

        OfferingStatus newStatus = eventOfferingDao.changeEventOfferingStatus(relation, OfferingStatus.PENDING);

        assertEquals(OfferingStatus.PENDING, newStatus);


    }

    @Test
    public void testChangeEventOfferingStatusAccept() {
        EventOfferingRelation relation = new EventOfferingRelation(event1, offering1, OfferingStatus.PENDING);
        em.persist(relation);

        OfferingStatus newStatus = eventOfferingDao.changeEventOfferingStatus(relation, OfferingStatus.ACCEPTED);

        assertEquals(OfferingStatus.ACCEPTED, newStatus);


    }

    @Test
    public void testChangeEventOfferingStatusReject() {

        EventOfferingRelation relation = new EventOfferingRelation(event1, offering1, OfferingStatus.PENDING);
        em.persist(relation);

        OfferingStatus newStatus = eventOfferingDao.changeEventOfferingStatus(relation, OfferingStatus.REJECTED);


        assertEquals(OfferingStatus.REJECTED, newStatus);


    }

    @Test
    public void testChangeEventOfferingStatusFromRelationIdInvalidRelationId() {
        OfferingStatus newStatus = eventOfferingDao.changeEventOfferingStatus(null, OfferingStatus.REJECTED);

        assertNull(newStatus);
    }


    @Test
    public void testRemoveOfferingFromEvent() {
        EventOfferingRelation relation = new EventOfferingRelation(event1, offering1, OfferingStatus.ACCEPTED);
        em.persist(relation);

        int rowCount = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, EVENT_OFFERING_TABLE,
                EVENT_OFFERING_EVENT_ID + " = " + event1.getId() + " AND " + EVENT_OFFERING_OFFERING_ID + " = " + offering1.getId());
        assertEquals(0, rowCount);

    }

    @Test
    public void testRemoveOfferingFromEventInvalidEventId() {
        EventOfferingRelation relation = new EventOfferingRelation(event1, offering1, OfferingStatus.ACCEPTED);
        em.persist(relation);

        eventOfferingDao.removeOfferingFromEvent(offering1.getId(), -1);

        int rowCount = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, EVENT_OFFERING_TABLE,
                EVENT_OFFERING_EVENT_ID + " = " + event1.getId() + " AND " + EVENT_OFFERING_OFFERING_ID + " = " + offering1.getId());
        assertEquals(1, rowCount);
    }

    @Test
    public void testGetRelation(){
        EventOfferingRelation relation = new EventOfferingRelation(event1, offering1, OfferingStatus.ACCEPTED);
        em.persist(relation);

        assertEquals(1, relation.getRelationId());

    }



    @Test
    public void testGetRelationFromRelationId(){
        EventOfferingRelation relation = new EventOfferingRelation(event1, offering1, OfferingStatus.ACCEPTED);
        em.persist(relation);

        EventOfferingRelation relationFromId = eventOfferingDao.getRelation(relation.getRelationId()).orElse(null);

        assertEquals(relation, relationFromId);
    }

    @Test
    public void testGetRelationFromRelationIdInvalidRelationId(){
        EventOfferingRelation relationFromId = eventOfferingDao.getRelation(-1).orElse(null);

        assertNull(relationFromId);
    }

    @Test
    public void testUserIsInRelation(){
        EventOfferingRelation relation = new EventOfferingRelation(event1, offering1, OfferingStatus.ACCEPTED);
        em.persist(relation);

        boolean userIsInRelation = eventOfferingDao.userIsInRelation(relation.getRelationId(), user1.getId());


        assertTrue(userIsInRelation);
    }

    @Test
    public void testUserNotInRelation(){
        EventOfferingRelation relation = new EventOfferingRelation(event1, offering1, OfferingStatus.ACCEPTED);
        em.persist(relation);

        boolean userIsInRelation = eventOfferingDao.userIsInRelation(relation.getRelationId(), 3);

        assertFalse(userIsInRelation);
    }

    @Test
    public void testUserInRelationInvalidRelationId(){
        boolean userIsInRelation = eventOfferingDao.userIsInRelation(-1, user1.getId());

        assertFalse(userIsInRelation);
    }

    @Test
    public void testGetEventOfferingStatus() {
        EventOfferingRelation relation = new EventOfferingRelation(event1, offering1, OfferingStatus.DONE);
        em.persist(relation);

        OfferingStatus status = eventOfferingDao.getEventOfferingStatus(event1.getId(), offering1.getId());
        assertEquals(OfferingStatus.DONE, status);
    }

    @Test
    public void testMarkOfferingsAsDone(){
        Event event4 = new Event(user1,"name4", "description4", Date.from(LocalDate.of(2023,6,30).atStartOfDay().toInstant(ZoneOffset.UTC)), 20, District.getDistrictByName("Flores"));
        em.persist(event4);

        EventOfferingRelation relation = new EventOfferingRelation(event4, offering1, OfferingStatus.ACCEPTED);
        em.persist(relation);
        em.flush();
        eventOfferingDao.markOfferingsAsDone();

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, EVENT_OFFERING_TABLE,
                EVENT_OFFERING_STATUS + " = '" + OfferingStatus.DONE + "'"));
    }

}
