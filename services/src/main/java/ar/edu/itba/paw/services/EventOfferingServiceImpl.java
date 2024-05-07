package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.eventOfferingRelation.RelationFilter;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.exception.notFound.EventNotFoundException;
import ar.edu.itba.paw.models.exception.notFound.OfferingNotFoundException;
import ar.edu.itba.paw.models.exception.notFound.RelationNotFoundException;
import ar.edu.itba.paw.models.offering.Offering;
import ar.edu.itba.paw.models.offering.OfferingCategory;
import ar.edu.itba.paw.persistence.EventOfferingDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EventOfferingServiceImpl implements EventOfferingService {

    Logger LOGGER = LoggerFactory.getLogger(EventOfferingServiceImpl.class);

    @Autowired
    private EventOfferingDao eventOfferingDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EventService eventService;

    @Autowired
    private OfferingService offeringService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private MessageService messageService;


    @Override
    public Optional<EventOfferingRelation> getRelationById(long relationId) {
        return eventOfferingDao.getRelation(relationId);
    }

    @Override
    public EventOfferingRelation createRelation(long eventId, long offeringId) {
        Event event = eventService.getById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        Offering offering = offeringService.getById(offeringId).orElseThrow(() -> new OfferingNotFoundException(offeringId));
        if (event.getDate().before(new Date())) {
            throw new IllegalArgumentException("Cannot add offering to event with date " + event.getDate() + " because it is in the past");
        }
        if (offering.isDeleted()){
            throw new IllegalArgumentException("Cannot add deleted offering to event");
        }
        return eventOfferingDao.createRelation(event, offering);
    }

    @Override
    public void deleteRelation(EventOfferingRelation eventOfferingRelation, String seeDetailsUrl) {
        eventOfferingDao.deleteRelation(eventOfferingRelation.getRelationId());
        Offering offering = eventOfferingRelation.getOffering();
        User owner = offering.getOwner();
        Event event = eventOfferingRelation.getEvent();
        OfferingStatus status = eventOfferingRelation.getStatus();
        if (status != OfferingStatus.NEW && status != OfferingStatus.REJECTED) {
            emailService.buildAndSendRemoveOfferingFromEventEmailToProvider(owner.getEmail(), event.getName(), event.getDate(), offering.getName(), owner.getUsername(), owner.getLanguage(), seeDetailsUrl);
        }
    }

    @Override
    public boolean userIsInRelation(long relationId, long userId) {
        return eventOfferingDao.userIsInRelation(relationId, userId);
    }

    @Scheduled(cron = "0 0 0 * * *")
    private void markOfferingAsDone() {
        eventOfferingDao.markOfferingsAsDone();
    }



    @Override
    public int getRelationsCount(RelationFilter relationFilter) {
        return eventOfferingDao.getRelationsCount(relationFilter);
    }

    @Override
    public List<EventOfferingRelation> getRelations(RelationFilter relationFilter, int page, int pageSize) {
        return eventOfferingDao.getRelations(relationFilter, page, pageSize);
    }

    @Override
    public void changeOfferingStatus(long relationId, OfferingStatus status, String seeDetailsUrlForProvider, String seeDetailsUrlForOrganizer) {

        Optional<EventOfferingRelation> maybeRelation = eventOfferingDao.getRelation(relationId);
        if (!maybeRelation.isPresent()) {
            throw new RelationNotFoundException(relationId);
        }
        OfferingStatus newStatus = eventOfferingDao.changeEventOfferingStatus(maybeRelation.get(), status);
        EventOfferingRelation relation = maybeRelation.get();

        if (newStatus == null) {
            LOGGER.warn("There was an error changing the status of the relation with id {}", relationId);
            throw new IllegalArgumentException("There was an error changing the status of the relation with id " + relationId);
        } else {
            User owner = relation.getOffering().getOwner();
            User eventCreator = relation.getEvent().getUser();

            if (newStatus == OfferingStatus.PENDING) {
                Locale localeWithProviderLanguage = new Locale(owner.getLanguage().getLanguage());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", localeWithProviderLanguage);
                String formattedDate = simpleDateFormat.format(relation.getEvent().getDate());


                String chatMessage = messageSource.getMessage("message.contact-provider.noMessage", new Object[]{eventCreator.getUsername(), relation.getEvent().getName(), formattedDate, relation.getEvent().getNumberOfGuests()}, localeWithProviderLanguage);
                messageService.addMessage(relation, eventCreator.getId(), chatMessage);
                emailService.buildAndSendEmailForContactProvider(owner.getEmail(), owner.getUsername(), chatMessage, seeDetailsUrlForProvider, owner.getLanguage(), eventCreator.getUsername(), relation.getEvent().getName(), formattedDate, relation.getEvent().getNumberOfGuests(), relation.getOffering().getName());
            } else {

                if (status == OfferingStatus.ACCEPTED && relation.getOffering().getOfferingCategory() == OfferingCategory.VENUE && relation.getEvent().getDistrict() != relation.getOffering().getDistrict()) {
                    eventService.updateMyEventDistrict(relation.getEvent(), relation.getOffering().getDistrict(), relation.getOffering().getOwner().getId(), seeDetailsUrlForProvider);
                }
                emailService.buildAndSendEmailForAnswerEventOfferingRequest(eventCreator.getEmail(), owner.getUsername(), eventCreator.getLanguage(), newStatus, seeDetailsUrlForOrganizer);
            }
        }
    }

}

