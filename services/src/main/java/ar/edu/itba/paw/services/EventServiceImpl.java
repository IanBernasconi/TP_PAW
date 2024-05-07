package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.events.*;
import ar.edu.itba.paw.models.exception.DuplicateEmailException;
import ar.edu.itba.paw.models.exception.notFound.UserNotFoundException;
import ar.edu.itba.paw.models.offering.Offering;
import ar.edu.itba.paw.persistence.EventDao;
import ar.edu.itba.paw.persistence.UserDao;
import ar.edu.itba.paw.services.functions.TokenGenerator;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@Service
public class EventServiceImpl implements EventService {

    Logger LOGGER = Logger.getLogger(EventServiceImpl.class.getName());

    @Autowired
    private EventDao eventDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private EventOfferingService eventOfferingService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MessageService messageService;

    @Override
    public Event createEvent(long userId, String name, String description, int numberOfGuests, Date date, District district) {
        User user = userDao.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        return eventDao.create(user, name, description, numberOfGuests, date, district);
    }

    @Override
    public void deleteEvent(Event event, String seeDetailsUrl) {

        List<EventOfferingRelation> eventOfferingRelations = event.getEventOfferingRelations();
        for (EventOfferingRelation eventOfferingRelation : eventOfferingRelations) {
            Offering offering = eventOfferingRelation.getOffering();
            User provider = offering.getOwner();
            emailService.buildAndSendRemoveOfferingFromEventEmailToProvider(provider.getEmail(), event.getName(), event.getDate(), offering.getName(), provider.getUsername(), provider.getLanguage(), seeDetailsUrl);
        }

        eventDao.delete(event.getId());

    }

    @Override
    public int getEventsByUserIdCount(long userId, EventsFilter eventsFilter) {
        return eventDao.getEventsByUserIdCount(userId, eventsFilter);
    }

    @Override
    public List<Event> getEventsByUserId(long userId, int page, int pageSize, EventsFilter eventsFilter) {
        return eventDao.getEventsByUserId(userId, page, pageSize, eventsFilter);
    }

    @Override
    public Optional<Event> getById(long id) {
        return eventDao.getById(id);
    }


    @Override
    public void updateMyEvent(Event event, String name, String description, int numberOfGuests, Date date, District district, String seeDetailsUrl) {
        sendUpdateEventMessageAndEmailToProviders(event, date, numberOfGuests, district, seeDetailsUrl, -1);

        eventDao.update(event, name, description, numberOfGuests, date, district);
    }

    @Override
    public void updateMyEventDistrict(Event event, District district, long providerId, String seeDetailsUrl) {
        sendUpdateEventMessageAndEmailToProviders(event, event.getDate(), event.getNumberOfGuests(), district, seeDetailsUrl, providerId);
        eventDao.updateDistrict(event, district);
    }

    private void sendUpdateEventMessageAndEmailToProviders(Event event, Date newDate, int newNumberOfGuests, District newDistrict, String seeDetailsUrl, long dontSentToProviderId) {
        List<EventOfferingRelation> relations = event.getEventOfferingRelations();
        List<Long> providersIds = new ArrayList<>();
        for (EventOfferingRelation relation : relations) {
            if (relation.getStatus() != OfferingStatus.NEW && relation.getStatus() != OfferingStatus.REJECTED) {
                Locale localeWithProviderLanguage = new Locale(relation.getOffering().getOwner().getLanguage().getLanguage());
                StringBuilder message = new StringBuilder();
                if (!newDate.equals(event.getDate())) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    message.append(" ");
                    message.append(messageSource.getMessage("event.update.messageToProvider.newDate", new Object[]{dateFormat.format(event.getDate()), dateFormat.format(newDate)}, localeWithProviderLanguage));
                }
                if (newNumberOfGuests != event.getNumberOfGuests()) {
                    message.append(" ");
                    message.append(messageSource.getMessage("event.update.messageToProvider.newNumberOfGuests", new Object[]{event.getNumberOfGuests(), newNumberOfGuests}, localeWithProviderLanguage));
                }
                if (!newDistrict.equals(event.getDistrict())) {
                    message.append(" ");
                    String districtName = messageSource.getMessage("district." + event.getDistrict(), null, localeWithProviderLanguage);
                    String newDistrictName = messageSource.getMessage("district." + newDistrict, null, localeWithProviderLanguage);
                    message.append(messageSource.getMessage("event.update.messageToProvider.newLocation", new Object[]{districtName, newDistrictName}, localeWithProviderLanguage));
                }
                if (message.length() == 0) {
                    return;
                }
                String fullMessage = messageSource.getMessage("event.update.messageToProvider", new Object[]{message.toString()}, localeWithProviderLanguage);
                if (relation.getProviderId() != dontSentToProviderId) {
                    if (!providersIds.contains(relation.getProviderId())) {
                        providersIds.add(relation.getOffering().getOwner().getId());
                        emailService.buildAndSendModifyEventEmailToProvider(relation.getOffering().getOwner().getEmail(), event.getName(), relation.getOffering().getName(), relation.getOffering().getOwner().getUsername(), relation.getOffering().getOwner().getLanguage(), seeDetailsUrl, message.toString());
                    }
                    messageService.addMessage(relation, event.getUser().getId(), fullMessage);
                    relation.setStatus(OfferingStatus.PENDING);
                }
            }
        }

    }

    @Override
    public boolean isOwner(long eventId, long userId) {
        return eventDao.isOwner(eventId, userId);
    }

    @Override
    public List<Guest> getGuestsByEventId(long eventId, int page, int pageSize) {
        return eventDao.getGuestsByEventId(eventId, page, pageSize);
    }

    @Override
    public int getGuestsByEventIdCount(long eventId) {
        return eventDao.getGuestsByEventIdCount(eventId);
    }

    @Override
    public void answerGuestInvitation(long eventId, long guestId, GuestStatus status, String token) {
        Optional<Guest> maybeGuest = eventDao.getGuestById(guestId);
        if (maybeGuest.isPresent()) {
            Guest guest = maybeGuest.get();
            if (guest.getStatus() != GuestStatus.PENDING) {
                throw new IllegalArgumentException("Guest already answered");
            }
            if (guest.getToken().equals(token)) {
                eventDao.updateGuestStatus(eventId, guestId, status);
            } else {
                throw new IllegalArgumentException("Invalid token");
            }
        } else {
            throw new ObjectNotFoundException(guestId, "Guest not found");
        }
    }

    @Override
    public Guest addGuest(Event event, String email) throws DuplicateEmailException {
        if (event.getDate() != null && event.getDate().before(new Date())) {
            throw new IllegalArgumentException("Event already happened");
        }
        return eventDao.addGuest(event, email);
    }

    @Override
    public void removeGuest(long eventId, long guestId) {
        eventDao.removeGuest(eventId, guestId);
    }

    @Override
    public void sendInvite(Guest guest, String baseUrl, String detailsUrl) {
        String token = TokenGenerator.generateNewToken();
        String acceptUrl = baseUrl + guest.getId() + "/accept" + "?token=" + token;
        String rejectUrl = baseUrl + guest.getId() + "/reject" + "?token=" + token;
        Event event = guest.getEvent();
        emailService.buildAndSendInviteEmail(guest.getEmail(), event.getName(), event.getUser().getUsername(), event.getDate(), event.getDistrict(), event.getDescription(), event.getUser().getLanguage(), acceptUrl, rejectUrl, detailsUrl);

        eventDao.markGuestAsInvited(guest.getId(), token);
    }

    @Override
    public Optional<Guest> getGuestById(long guestId) {
        return eventDao.getGuestById(guestId);
    }
}
