package ar.edu.itba.paw.persistence;


import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.events.EventsFilter;
import ar.edu.itba.paw.models.events.Guest;
import ar.edu.itba.paw.models.events.GuestStatus;
import ar.edu.itba.paw.models.exception.DuplicateEmailException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventDao {

    Event create(User user, String name, String description, int numberOfGuests, Date date, District district);

    int getEventsByUserIdCount(long userId, EventsFilter eventsFilter);

    List<Event> getEventsByUserId(long userId, int page, int pageSize, EventsFilter eventsFilter);

    Optional<Event> getById(long id);

    void update(Event event, String name, String description, int numberOfGuests, Date date, District district);

    void updateDistrict(Event event, District district);

    boolean isOwner(long eventId, long userId);

    void delete(long eventId);

    List<Guest> getGuestsByEventId(long eventId, int page, int pageSize);

    int getGuestsByEventIdCount(long eventId);

    Guest addGuest(Event event, String email) throws DuplicateEmailException;

    Optional<Guest> getGuestById(long guestId);

    void updateGuestStatus(long eventId, long guestId, GuestStatus status);

    void removeGuest(long eventId, long guestId);

    void markGuestAsInvited(long guestId, String token);
}
