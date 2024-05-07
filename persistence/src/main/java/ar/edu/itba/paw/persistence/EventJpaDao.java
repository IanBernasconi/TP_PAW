package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.events.EventsFilter;
import ar.edu.itba.paw.models.events.Guest;
import ar.edu.itba.paw.models.events.GuestStatus;
import ar.edu.itba.paw.models.exception.DuplicateEmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

import static ar.edu.itba.paw.persistence.tablesInformation.EventGuestTableInfo.*;
import static ar.edu.itba.paw.persistence.tablesInformation.EventTableInfo.*;

@Repository
public class EventJpaDao implements EventDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Event create(User user, String name, String description, int numberOfGuests, Date date, District district) {
        if (user == null) {
            LOGGER.error("User was null");
            throw new IllegalArgumentException("User was null");
        }
        final Event event = new Event(user, name, description, date, numberOfGuests, district);
        em.persist(event);
        return event;
    }

    @Override
    public int getEventsByUserIdCount(long userId, EventsFilter eventsFilter) {
        StringBuilder queryBuilder = new StringBuilder("SELECT count(*) FROM " + EVENT_TABLE + " WHERE " + EVENT_USER_ID + " = :userId ");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userId", userId);
        addFilter(queryBuilder, eventsFilter, parameters);

        final Query query = em.createNativeQuery(queryBuilder.toString());
        parameters.forEach(query::setParameter);
        return ((Number) query.getSingleResult()).intValue();
    }

    @Override
    public List<Event> getEventsByUserId(long userId, int page, int pageSize, EventsFilter eventsFilter) {
        StringBuilder queryBuilder = new StringBuilder("SELECT " + EVENT_ID + " FROM " + EVENT_TABLE + " WHERE " + EVENT_USER_ID + " = :userId ");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userId", userId);
        addFilter(queryBuilder, eventsFilter, parameters);

        List<Long> idList = UtilitiesFunctions.getEntitiesIdListByQuery(em, queryBuilder.toString(), parameters, page, pageSize);

        if (idList.isEmpty()) {
            return Collections.emptyList();
        }

        return em.createQuery("from Event as e where e.id in (:ids)", Event.class)
                .setParameter("ids", idList)
                .getResultList();
    }

    private void addFilter(StringBuilder query, EventsFilter eventsFilter,  Map<String, Object> parameters) {
        if (eventsFilter.getStart() != null) {
            query.append("AND " + EVENT_DATE + " >= :startDate ");
            parameters.put("startDate", eventsFilter.getStart());
        }
        if (eventsFilter.getEnd() != null) {
            query.append("AND " + EVENT_DATE + " <= :endDate ");
            parameters.put("endDate", eventsFilter.getEnd());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Event> getById(long id) {
        return em.createQuery("from Event as e where e.id = :id", Event.class)
                .setParameter("id", id)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    @Transactional
    public void update(Event event, String name, String description, int numberOfGuests, Date date, District district) {
        if (event == null) {
            LOGGER.error("Event not found");
            throw new IllegalArgumentException("Event not found");
        }
        event.setName(name);
        event.setDescription(description);
        event.setNumberOfGuests(numberOfGuests);
        event.setDate(date);
        event.setDistrict(district);
        em.persist(event);
    }

    @Override
    @Transactional
    public void updateDistrict(Event event, District district) {
        if (event == null) {
            LOGGER.error("Event not found");
            throw new IllegalArgumentException("Event not found");
        }

        event.setDistrict(district);
        em.persist(event);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOwner(long eventId, long userId) {
        Event event = em.find(Event.class, eventId);
        return event != null && event.getUser().getId() == userId;
    }

    @Override
    @Transactional
    public void delete(long eventId) {
        final Event event = em.find(Event.class, eventId);
        em.remove(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Guest> getGuestsByEventId(long eventId, int page, int pageSize) {
        final Query nativeQuery = em.createNativeQuery("SELECT " + EVENT_GUESTS_ID + " FROM " + EVENT_GUESTS_TABLE + " WHERE " + EVENT_GUESTS_EVENT_ID + " = :eventId")
                .setParameter("eventId", eventId)
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize);

        List<Long> idList = UtilitiesFunctions.getIdsFromNativeQuery(nativeQuery);
        if (idList.isEmpty()) {
            return Collections.emptyList();
        }

        return em.createQuery("from Guest as g where g.id in (:ids)", Guest.class)
                .setParameter("ids", idList)
                .getResultList();
    }

    @Override
    public int getGuestsByEventIdCount(long eventId) {
        return ((Number) em.createNativeQuery("SELECT count(*) FROM " + EVENT_GUESTS_TABLE + " WHERE " + EVENT_GUESTS_EVENT_ID + " = :eventId")
                .setParameter("eventId", eventId)
                .getSingleResult()).intValue();
    }

    @Override
    @Transactional
    public void markGuestAsInvited(long guestId, String token) {
        Guest guest = em.find(Guest.class, guestId);
        if (guest == null) {
            LOGGER.error("Guest with id {} not found", guestId);
            throw new IllegalArgumentException("Guest with id " + guestId + " not found");
        }
        guest.setStatus(GuestStatus.PENDING);
        guest.setToken(token);
        em.persist(guest);
    }

    @Override
    @Transactional
    public Guest addGuest(Event event, String email) throws DuplicateEmailException {
        if (event == null) {
            LOGGER.error("Event was null");
            throw new IllegalArgumentException("Event was null");
        }
        Guest guest = em.createQuery("from Guest as g where g.email = :email and g.event.id = :eventId", Guest.class)
                .setParameter("email", email)
                .setParameter("eventId", event.getId())
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
        if (guest != null) {
            LOGGER.error("Guest with email {} already exists", email);
            throw new DuplicateEmailException("Guest with email " + email + " already exists");
        }
        guest = new Guest(email, event, GuestStatus.NEW);
        em.persist(guest);
        return guest;
    }

    @Override
    @Transactional
    public Optional<Guest> getGuestById(long guestId) {
        return em.createQuery("from Guest as g where g.id = :guestId", Guest.class)
                .setParameter("guestId", guestId)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    @Transactional
    public void updateGuestStatus(long eventId, long guestId, GuestStatus status) {
        Guest guest = em.find(Guest.class, guestId);
        if (guest == null) {
            LOGGER.error("Guest with id {} not found", guestId);
            throw new IllegalArgumentException("Guest with id " + guestId + " not found");
        }
        guest.setStatus(status);
        em.persist(guest);
    }

    @Override
    @Transactional
    public void removeGuest(long eventId, long guestId) {
        Guest guest = em.find(Guest.class, guestId);
        if (guest == null) {
            LOGGER.error("Guest with id {} not found", guestId);
            throw new IllegalArgumentException("Guest with id " + guestId + " not found");
        }
        em.remove(guest);
    }

}
