package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.PriceType;
import ar.edu.itba.paw.models.SortType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.eventOfferingRelation.EventOfferingRelation;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.exception.notFound.OfferingNotFoundException;
import ar.edu.itba.paw.models.offering.Offering;
import ar.edu.itba.paw.models.offering.OfferingCategory;
import ar.edu.itba.paw.models.offering.OfferingFilter;
import ar.edu.itba.paw.persistence.EventOfferingDao;
import ar.edu.itba.paw.persistence.OfferingDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OfferingServiceImpl implements OfferingService {

    private final Logger LOGGER = LoggerFactory.getLogger(OfferingServiceImpl.class);

    private static final int recommendationPoolSize = 10;

    @Autowired
    private OfferingDao offeringDao;

    @Autowired
    private EventOfferingDao eventOfferingDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    MessageService messageService;

    @Override
    public Offering createOffering(long userId, String name, OfferingCategory offeringCategory, String description, float minPrice, float maxPrice, PriceType priceType, int maxGuests, District district, List<Long> images) {
        return offeringDao.create(userId, name, offeringCategory, description, minPrice, maxPrice, priceType, maxGuests, district, images);
    }

    @Override
    @Transactional
    public void updateMyOffering(long offeringId, String name, String description, int maxGuests, District district, OfferingCategory offeringCategory, float minPrice, float maxPrice, PriceType priceType, List<Long> imagesIds, List<Long> removeImagesIds) {
        offeringDao.update(offeringId, name, description, maxGuests, district, offeringCategory, minPrice, maxPrice, priceType);
        offeringDao.updateImages(offeringId, imagesIds, removeImagesIds);
    }

    @Override
    public boolean userLikedOffering(long userId, long offeringId) {
        return offeringDao.userLikedOffering(userId, offeringId);
    }

    @Override
    public Optional<Offering> getById(long id, Long loggedUserId) {
        return offeringDao.getById(id, loggedUserId);
    }

    @Override
    public Optional<Offering> getById(long id) {
        return offeringDao.getById(id);
    }

    @Override
    public List<Offering> getFilteredOfferings(OfferingFilter filter, int page, int pageSize) {
        return offeringDao.getFilteredOfferings(filter, page, pageSize);
    }

    @Override
    public int getFilteredOfferingsCount(OfferingFilter filter) {
        return offeringDao.getFilteredOfferingsCount(filter);
    }

    @Override
    public boolean setOfferingLike(Offering offering, User loggedUser, boolean liked) {
        if (offering == null || loggedUser == null || offering.isDeleted()) {
            return false;
        }
        if (offering.getOwner().getId() == loggedUser.getId()) {
            throw new IllegalArgumentException();
        }
        return offeringDao.setLike(offering, loggedUser, liked);
    }

    @Override
    public boolean isOwner(long offeringId, long userId) {
        return offeringDao.isOwner(offeringId, userId);
    }

    @Override
    @Transactional
    public void deleteOffering(long offeringId, String seeDetailsUrl) {
        Optional<Offering> maybeOffering = getById(offeringId);

        if (maybeOffering.isPresent()) {
            Offering offering = maybeOffering.get();

            List<EventOfferingRelation> eventOfferingRelations = offering.getRelations();
            for (EventOfferingRelation eventOfferingRelation : eventOfferingRelations) {
                if (eventOfferingRelation.getStatus() == OfferingStatus.PENDING || eventOfferingRelation.getStatus() == OfferingStatus.ACCEPTED) {
                    Event event = eventOfferingRelation.getEvent();
                    User eventCreator = event.getUser();

                    if (eventOfferingDao.changeEventOfferingStatus(eventOfferingRelation, OfferingStatus.REJECTED) == OfferingStatus.REJECTED) {
                        emailService.buildAndSendRemoveOfferingFromEventEmailToOrganizer(eventCreator.getEmail(), event.getName(), event.getDate(), offering.getName(), eventCreator.getUsername(), eventCreator.getLanguage(), seeDetailsUrl + "/" + event.getId());
                    }
                }
            }
            offeringDao.deleteOffering(offeringId);
        } else {
            throw new OfferingNotFoundException(offeringId);
        }
    }

    @Override
    public List<Offering> getRecommendationsForEvent(Event event) {
        Map<OfferingCategory, List<EventOfferingRelation>> relationsByOfferingCategory = event.getRelationsByOfferingCategoryWithDefaults();

        List<OfferingCategory> categoriesWithoutOfferings = new ArrayList<>();
        for (Map.Entry<OfferingCategory, List<EventOfferingRelation>> entry : relationsByOfferingCategory.entrySet()) {
            if (entry.getValue().isEmpty()) {
                categoriesWithoutOfferings.add(entry.getKey());
            }
        }

        OfferingFilter filter = new OfferingFilter().attendees(event.getNumberOfGuests()).sortType(SortType.RATING_DESC);

        return offeringDao.getRecommendations(categoriesWithoutOfferings, recommendationPoolSize, filter);
    }

    @Override
    public List<Offering> getRecommendationsForOffering(int baseQty, int qty, Offering offering) {
        return offeringDao.getRecommendationsForOffering(new OfferingFilter().category(offering.getOfferingCategory()), baseQty, qty, offering.getId());
    }
}
