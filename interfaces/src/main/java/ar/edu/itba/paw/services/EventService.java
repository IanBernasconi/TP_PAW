package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.events.*;
import ar.edu.itba.paw.models.exception.DuplicateEmailException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EventService {

    Event createEvent(long userId, String name, String description, int numberOfGuests, Date date, District district);

    void deleteEvent(Event event, String seeDetailsUrl);

    int getEventsByUserIdCount(long userId, EventsFilter eventsFilter);

    List<Event> getEventsByUserId(long userId, int page, int pageSize, EventsFilter eventsFilter);

    Optional<Event> getById(long id);

    void updateMyEvent(Event event, String name, String description, int numberOfGuests, Date date, District district, String seeDetailsUrl);

    void updateMyEventDistrict(Event event, District newDistrict, long providerId, String seeDetailsUrl);

    boolean isOwner(long eventId, long userId);

    List<Guest> getGuestsByEventId(long eventId, int page, int pageSize);

    int getGuestsByEventIdCount(long eventId);

    void answerGuestInvitation(long eventId, long guestId, GuestStatus status, String token);

    Guest addGuest(Event event, String email) throws DuplicateEmailException;

    void removeGuest(long eventId, long guestId);

    void sendInvite(Guest guest, String baseUrl, String detailsUrl);

    Optional<Guest> getGuestById(long guestId);
}
