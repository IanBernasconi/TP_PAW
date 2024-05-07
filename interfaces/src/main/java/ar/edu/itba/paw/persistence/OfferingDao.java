package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.models.offering.*;

import java.util.List;
import java.util.Optional;

public interface OfferingDao {

    Offering create(long userId, String name, OfferingCategory offeringCategory, String description, float minPrice, float maxPrice, PriceType priceType, int maxGuests,
                    District district, List<Long> images);

    void update(long offeringId, String name, String description, int maxGuests, District district, OfferingCategory offeringCategory, float minPrice, float maxPrice, PriceType priceType);

    void updateImages(long offeringId, List<Long> images, List<Long> removeImagesIds);

    Optional<Offering> getById(long id, Long loggedUserId);

    Optional<Offering> getById(long id);

    List<Offering> getFilteredOfferings(OfferingFilter filter, int page, int pageSize);

    int getFilteredOfferingsCount(OfferingFilter filter);

    boolean setLike(Offering offering, User user, boolean liked);

    boolean isOwner(long offeringId, long userId);

    void deleteOffering(long offeringId);

    List<Offering> getRecommendations(List<OfferingCategory> categories, int baseQty, OfferingFilter filter);

    List<Offering> getRecommendationsForOffering(OfferingFilter filter, int baseQty, int qty, long offeringId);

  boolean userLikedOffering(long userId, long offeringId);
}
