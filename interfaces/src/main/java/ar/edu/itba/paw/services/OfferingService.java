package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.PriceType;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.models.offering.*;

import java.util.List;
import java.util.Optional;

public interface OfferingService {

    Offering createOffering(long userId, String name, OfferingCategory offeringCategory, String description,float minPrice, float maxPrice, PriceType priceType, int maxGuests, District district, List<Long> imagesIds);

    void updateMyOffering(long offeringId, String name, String description, int maxGuests, District district, OfferingCategory offeringCategory, float minPrice, float maxPrice, PriceType priceType, List<Long> imagesIds, List<Long> removeImagesIds);

    Optional<Offering> getById(long id, Long loggedUserId);

    Optional<Offering> getById(long id);

    List<Offering> getFilteredOfferings(OfferingFilter filter, int page, int pageSize);

    int getFilteredOfferingsCount(OfferingFilter filter);
    boolean setOfferingLike(Offering offering, User loggedUser, boolean liked);

    boolean userLikedOffering(long userId,long offeringId);

    boolean isOwner(long offeringId, long userId);

    void deleteOffering(long offeringId, String seeDetailsUrl);

    List<Offering> getRecommendationsForEvent(Event event);

    List<Offering> getRecommendationsForOffering(int baseQty, int qty, Offering offering);


}
