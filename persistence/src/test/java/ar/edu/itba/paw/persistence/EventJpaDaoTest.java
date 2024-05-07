package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.EmailLanguages;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.events.EventsFilter;
import ar.edu.itba.paw.models.events.Guest;
import ar.edu.itba.paw.models.events.GuestStatus;
import ar.edu.itba.paw.models.exception.DuplicateEmailException;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static ar.edu.itba.paw.persistence.tablesInformation.EventTableInfo.EVENT_TABLE;
import static ar.edu.itba.paw.persistence.tablesInformation.UserTableInfo.USER_TABLE;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Transactional
@Rollback
public class EventJpaDaoTest {

    private static final String GUEST_TABLE = "event_guests";
    private JdbcTemplate jdbcTemplate;


    @Autowired
    private DataSource ds;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private EventDao eventDao;

    private User user1;
    private User user2;

    private Event event1;
    private Event event2;
    private Event event3;

    @Before
    public void setUp() {
        jdbcTemplate = new JdbcTemplate(ds);

        em.createNativeQuery("TRUNCATE TABLE " + USER_TABLE).executeUpdate();
        em.createNativeQuery("TRUNCATE TABLE " + EVENT_TABLE).executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE users_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE events_id_seq RESTART WITH 1").executeUpdate();

        user1 = new User("testpassword", "testuser1", "testemail1@test.com", true, EmailLanguages.getDefaultLanguage());
        user2 = new User("testpassword", "testuser2", "testemail2@test.com", true, EmailLanguages.getDefaultLanguage());
        em.persist(user1);
        em.persist(user2);

    }

    @Test
    public void testCreate() {
        // Test data
        String eventName = "EventName";
        String eventDescription = "EventDescription";
        int numberOfGuests = 100;
        Date eventDate = new Date();
        District eventDistrict = District.getDistrictByName("Belgrano");
        // Test execution
        Event createdEvent = eventDao.create(user1, eventName, eventDescription, numberOfGuests, eventDate, eventDistrict);
        em.flush();
        // Assertions
        assertNotNull(createdEvent);
        assertEquals(user1.getId(), createdEvent.getUser().getId());
        assertEquals(eventName, createdEvent.getName());
        assertEquals(eventDescription, createdEvent.getDescription());
        assertEquals(numberOfGuests, createdEvent.getNumberOfGuests());
        assertEquals(eventDate, createdEvent.getDate());
        assertTrue(createdEvent.getId() > 0);
        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_TABLE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidUserId() {
        // Test data
        String eventName = "EventName";
        String eventDescription = "EventDescription";
        int numberOfGuests = 100;
        Date eventDate = new Date();
        District eventDistrict = District.getDistrictByName("Belgrano");

        // Test execution
        eventDao.create(null, eventName, eventDescription, numberOfGuests, eventDate, eventDistrict);

    }

    @Test
    public void testGetById() {
        event1 = new Event(user1, "EventName", "EventDescription", Date.from(LocalDate.of(2024, 6, 30).atStartOfDay().toInstant(ZoneOffset.UTC)), 100, District.getDistrictByName("Belgrano"));
        em.persist(event1);

        Optional<Event> eventOptional = eventDao.getById(event1.getId());

        assertTrue(eventOptional.isPresent());
        Event event = eventOptional.get();
        assertEquals(event1.getId(), event.getId());
        assertEquals(user1.getId(), event.getUser().getId());
    }

    @Test
    public void testGetByIdNoEvent() {
        Optional<Event> eventOptional = eventDao.getById(1);

        assertFalse(eventOptional.isPresent());
    }

    @Test
    public void testGetEventsByUserId() {
        event1 = new Event(user1, "EventName", "EventDescription", new Date(0), 100, District.getDistrictByName("Recoleta"));
        event2 = new Event(user1, "EventName2", "EventDescription2", new Date(0), 100, District.getDistrictByName("Flores"));
        event3 = new Event(user1, "EventName3", "EventDescription3", new GregorianCalendar(2025, Calendar.JUNE, 30).getTime(), 100, District.getDistrictByName("Belgrano"));
        em.persist(event1);
        em.persist(event2);
        em.persist(event3);

        Set<String> expectedEventNames = new HashSet<>(Arrays.asList("EventName", "EventName2"));

        EventsFilter eventsFilter = new EventsFilter();
        eventsFilter.end(new Date());

        List<Event> events = eventDao.getEventsByUserId(user1.getId(), 0, 20, eventsFilter);

        assertNotNull(events);
        assertEquals(2, events.size());

        Set<String> actualEventNames = events.stream()
                .map(Event::getName)
                .collect(Collectors.toSet());

        assertEquals(expectedEventNames, actualEventNames);
    }

    @Test
    public void testGetExpiredEventsByUserIdNoEvents() {
        EventsFilter eventsFilter = new EventsFilter();
        eventsFilter.end(new Date());
        List<Event> events = eventDao.getEventsByUserId(user1.getId(), 0, 20, eventsFilter);

        assertNotNull(events);
        assertTrue(events.isEmpty());
    }

    @Test
    public void testGetEventsByUserIdInvalidUserId() {
        EventsFilter eventsFilter = new EventsFilter();
        eventsFilter.end(new Date());
        List<Event> events = eventDao.getEventsByUserId(-1, 0, 20, eventsFilter);

        assertNotNull(events);
        assertTrue(events.isEmpty());
    }


    @Test
    public void testGetOngoingEventsByUserId() {
        event1 = new Event(user1, "EventName", "EventDescription", new Date(0), 100, District.getDistrictByName("Recoleta"));
        event2 = new Event(user1, "EventName2", "EventDescription2", new Date(0), 100, District.getDistrictByName("Flores"));
        event3 = new Event(user1, "EventName3", "EventDescription3", new GregorianCalendar(2025, Calendar.JUNE, 30).getTime(), 100, District.getDistrictByName("Colegiales"));
        Event event4 = new Event(user1, "EventName4", "EventDescription4", new GregorianCalendar(2025, Calendar.JUNE, 30).getTime(), 100, District.getDistrictByName("Belgrano"));
        em.persist(event1);
        em.persist(event2);
        em.persist(event3);
        em.persist(event4);


        Set<String> expectedEventNames = new HashSet<>(Arrays.asList("EventName3", "EventName4"));

        EventsFilter eventsFilter = new EventsFilter();
        eventsFilter.start(new Date());

        List<Event> events = eventDao.getEventsByUserId(user1.getId(), 0, 20, eventsFilter);

        assertNotNull(events);
        assertEquals(2, events.size());

        Set<String> actualEventNames = events.stream()
                .map(Event::getName)
                .collect(Collectors.toSet());

        assertEquals(expectedEventNames, actualEventNames);


    }

    @Test
    public void testGetOngoingEventsByUserIdNoEvents() {
        EventsFilter eventsFilter = new EventsFilter();
        eventsFilter.start(new Date());
        List<Event> events = eventDao.getEventsByUserId(user1.getId(), 1, 20, eventsFilter);

        assertNotNull(events);
        assertTrue(events.isEmpty());
    }

    @Test
    public void testUpdateEvent() {
        // Test data
        event1 = new Event(user1, "EventName", "EventDescription", Date.from(LocalDate.of(2024, 6, 30).atStartOfDay().toInstant(ZoneOffset.UTC)), 100, District.getDistrictByName("Recoleta"));
        em.persist(event1);
        // New values
        String newName = "NewName";
        String newDescription = "NewDescription";
        int newNumberOfGuests = 50;
        LocalDate localDate = LocalDate.now();
        Date convertedDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        District newEventDistrict = District.getDistrictByName("Palermo");

        // Test execution
        eventDao.update(event1, newName, newDescription, newNumberOfGuests, convertedDate, newEventDistrict);
        em.flush();


        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_TABLE));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, EVENT_TABLE, "name = " + "'" + newName + "'"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, EVENT_TABLE, "description = " + "'" + newDescription + "'"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, EVENT_TABLE, "number_of_guests = " + newNumberOfGuests));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, EVENT_TABLE, "date = " + "'" + sdf.format(convertedDate) + "'"));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, EVENT_TABLE, "district = " + "'" + newEventDistrict + "'"));
    }

    @Test(expected = Exception.class)
    public void testUpdateEventNoEvent() {
        String newName = "NewName";
        String newDescription = "NewDescription";
        int newNumberOfGuests = 50;
        Date newDate = new Date();
        District newDistrict = District.getDistrictByName("Recoleta");

        eventDao.update(null, newName, newDescription, newNumberOfGuests, newDate, newDistrict);


    }

    @Test
    public void testIsOwner() {
        event1 = new Event(user1, "EventName", "EventDescription", Date.from(LocalDate.of(2024, 6, 30).atStartOfDay().toInstant(ZoneOffset.UTC)), 100, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        assertTrue(eventDao.isOwner(event1.getId(), user1.getId()));
    }

    @Test
    public void testIsNotOwner() {
        event1 = new Event(user1, "EventName", "EventDescription", Date.from(LocalDate.of(2024, 6, 30).atStartOfDay().toInstant(ZoneOffset.UTC)), 100, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        assertFalse(eventDao.isOwner(event1.getId(), user2.getId()));
    }

    @Test
    public void testDelete() {
        event1 = new Event(user1, "EventName", "EventDescription", Date.from(LocalDate.of(2024, 6, 30).atStartOfDay().toInstant(ZoneOffset.UTC)), 100, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        eventDao.delete(event1.getId());

        assertEquals(0, JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_TABLE));
    }

    @Test(expected = Exception.class)
    public void testDeleteNotPresent() {
        event1 = new Event(user1, "EventName", "EventDescription", Date.from(LocalDate.of(2024, 6, 30).atStartOfDay().toInstant(ZoneOffset.UTC)), 100, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        eventDao.delete(2);
    }

    @Test
    public void testUpdateDistrict() {
        event1 = new Event(user1, "EventName", "EventDescription", Date.from(LocalDate.of(2024, 6, 30).atStartOfDay().toInstant(ZoneOffset.UTC)), 100, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        eventDao.updateDistrict(event1, District.getDistrictByName("Constitución"));
        em.flush();

        assertEquals(1, JdbcTestUtils.countRowsInTable(jdbcTemplate, EVENT_TABLE));
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, EVENT_TABLE, "district = 'CONSTITUCION'"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateDistrictNoEvent() {
        eventDao.updateDistrict(null, District.getDistrictByName("Constitución"));
    }


    @Test
    public void testGetGuestsByEventId() {
        event1 = new Event(user1, "EventName", "EventDescription", Date.from(LocalDate.of(2024, 6, 30).atStartOfDay().toInstant(ZoneOffset.UTC)), 2, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        Guest guest1 = new Guest("user1@mail.com", event1, GuestStatus.NEW);
        Guest guest2 = new Guest("user2@mail.com", event1, GuestStatus.NEW);
        em.persist(guest1);
        em.persist(guest2);


        List<Guest> guests = eventDao.getGuestsByEventId(event1.getId(), 0, 20);

        assertEquals(2, guests.size());
        assertEquals(guest1.getId(), guests.get(0).getId());
        assertEquals(guest2.getId(), guests.get(1).getId());


    }

    @Test
    public void testGetGuestsByEventIdNoGuests() {
        event1 = new Event(user1, "EventName", "EventDescription", Date.from(LocalDate.of(2024, 6, 30).atStartOfDay().toInstant(ZoneOffset.UTC)), 2, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        List<Guest> guests = eventDao.getGuestsByEventId(event1.getId(), 1, 20);

        assertTrue(guests.isEmpty());
    }

    @Test
    public void testGetEventsByUserIdCount() {
        event1 = new Event(user1, "EventName", "EventDescription", new Date(0), 100, District.getDistrictByName("Recoleta"));
        event2 = new Event(user1, "EventName2", "EventDescription2", new Date(0), 100, District.getDistrictByName("Flores"));
        event3 = new Event(user1, "EventName3", "EventDescription3", new GregorianCalendar(2025, Calendar.JUNE, 30).getTime(), 100, District.getDistrictByName("Colegiales"));
        Event event4 = new Event(user1, "EventName4", "EventDescription4", new GregorianCalendar(2025, Calendar.JUNE, 30).getTime(), 100, District.getDistrictByName("Belgrano"));
        em.persist(event1);
        em.persist(event2);
        em.persist(event3);
        em.persist(event4);

        EventsFilter eventsFilter = new EventsFilter();
        eventsFilter.start(new Date());

        int count = eventDao.getEventsByUserIdCount(user1.getId(), eventsFilter);

        assertEquals(2, count);
    }

    @Test
    public void testGetEventsByUserIdCountNoEvents() {
        EventsFilter eventsFilter = new EventsFilter();
        eventsFilter.start(new Date());

        int count = eventDao.getEventsByUserIdCount(user1.getId(), eventsFilter);

        assertEquals(0, count);
    }

    @Test
    public void testMarkGuestAsInvited() {
        event1 = new Event(user1, "EventName", "EventDescription", new Date(0), 2, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        Guest guest1 = new Guest("test@email.com", event1, GuestStatus.NEW);
        em.persist(guest1);

        eventDao.markGuestAsInvited(guest1.getId(), "token");
        em.flush();
        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, GUEST_TABLE, "invite_status = 'PENDING'"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMarkGuestAsInvitedNoGuest() {
        event1 = new Event(user1, "EventName", "EventDescription", new Date(0), 2, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        eventDao.markGuestAsInvited(1, "token");
    }


    @Test
    public void testGetGuestsByEventIdCount() {
        event1 = new Event(user1, "EventName", "EventDescription", new Date(0), 2, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        Guest guest1 = new Guest("user1@mail.com", event1, GuestStatus.NEW);
        Guest guest2 = new Guest("user2@mail.com", event1, GuestStatus.NEW);
        Guest guest3 = new Guest("user3@mail.com", event1, GuestStatus.PENDING);
        Guest guest4 = new Guest("user4@mail.com", event1, GuestStatus.ACCEPTED);

        em.persist(guest1);
        em.persist(guest2);
        em.persist(guest3);
        em.persist(guest4);

        int count = eventDao.getGuestsByEventIdCount(event1.getId());

        assertEquals(4, count);
        assertEquals(4, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, GUEST_TABLE, "event_id = " + event1.getId()));

    }

    @Test
    public void testGetGuestsByEventIdCountNoGuests() {
        event1 = new Event(user1, "EventName", "EventDescription", new Date(0), 2, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        int count = eventDao.getGuestsByEventIdCount(event1.getId());

        assertEquals(0, count);
        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, GUEST_TABLE, "event_id = " + event1.getId()));

    }

    @Test
    public void testAddGuest() throws DuplicateEmailException {
        event1 = new Event(user1, "EventName", "EventDescription", new Date(0), 2, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        eventDao.addGuest(event1, "user@mail.com");
        em.flush();

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, GUEST_TABLE, "event_id = " + event1.getId() + " AND guest_mail = 'user@mail.com'"));
    }

    @Test(expected = DuplicateEmailException.class)
    public void testAddGuestDuplicateEmail() throws DuplicateEmailException {
        event1 = new Event(user1, "EventName", "EventDescription", new Date(0), 2, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        Guest guest = new Guest("user@mail.com", event1, GuestStatus.NEW);
        em.persist(guest);

        eventDao.addGuest(event1, "user@mail.com");

    }

    @Test
    public void testGetGuestById() {
        event1 = new Event(user1, "EventName", "EventDescription", new Date(0), 2, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        Guest guest1 = new Guest("user1@mail.com", event1, GuestStatus.NEW);
        Guest guest2 = new Guest("user2@mail.com", event1, GuestStatus.NEW);
        em.persist(guest1);
        em.persist(guest2);

        Optional<Guest> guestOptional = eventDao.getGuestById(guest1.getId());

        assertTrue(guestOptional.isPresent());
        assertEquals(guest1.getId(), guestOptional.get().getId());

    }

    @Test
    public void testGetGuestByIdNoGuest() {
        Optional<Guest> guestOptional = eventDao.getGuestById(1);

        assertFalse(guestOptional.isPresent());
    }

    @Test
    public void updateGuestStatus() {
        event1 = new Event(user1, "EventName", "EventDescription", new Date(0), 2, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        Guest guest1 = new Guest("user1@mail.com", event1, GuestStatus.NEW);
        em.persist(guest1);

        eventDao.updateGuestStatus(event1.getId(), guest1.getId(), GuestStatus.PENDING);
        em.flush();

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, GUEST_TABLE, "event_id = " + event1.getId() + " AND id = " + guest1.getId() + " AND invite_status = 'PENDING'"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateGuestStatusNoGuest() {
        event1 = new Event(user1, "EventName", "EventDescription", new Date(0), 2, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        eventDao.updateGuestStatus(event1.getId(), 1, GuestStatus.PENDING);
    }

    @Test
    public void testRemoveGuest() {
        event1 = new Event(user1, "EventName", "EventDescription", new Date(0), 2, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        Guest guest1 = new Guest("user@mail.com", event1, GuestStatus.NEW);

        em.persist(guest1);

        eventDao.removeGuest(event1.getId(), guest1.getId());
        em.flush();

        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, GUEST_TABLE, "event_id = " + event1.getId() + " AND id = " + guest1.getId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveGuestNoGuest() {
        event1 = new Event(user1, "EventName", "EventDescription", new Date(0), 2, District.getDistrictByName("Recoleta"));
        em.persist(event1);

        eventDao.removeGuest(event1.getId(), 1);
    }

}
