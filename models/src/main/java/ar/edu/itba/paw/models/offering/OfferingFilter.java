package ar.edu.itba.paw.models.offering;

import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.SortType;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class OfferingFilter {
    private OfferingCategory category = OfferingCategory.ALL;

    private Integer minPrice = null;
    private Integer maxPrice = null;
    private Integer attendees = null;
    private Long likedBy = null;
    private List<District> districts = Collections.singletonList(District.ALL);
    private SortType sortType = SortType.RATING_DESC;
    private String search = null;
    private Date availableOn = null;
    private Long createdBy = null;
    private Long inEvent = null;
    private Boolean includeDeleted = false;

    public OfferingFilter category(OfferingCategory category) {
        this.category = category;
        return this;
    }

    public OfferingFilter minPrice(Integer minPrice) {
        this.minPrice = minPrice;
        return this;
    }

    public OfferingFilter maxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
        return this;
    }

    public OfferingFilter attendees(Integer attendees) {
        this.attendees = attendees;
        return this;
    }

    public OfferingFilter districts(List<District> district) {
        this.districts = district;
        return this;
    }

    public OfferingFilter likedBy(Long userId) {
        this.likedBy = userId;
        return this;
    }

    public OfferingFilter sortType(SortType sortType) {
        this.sortType = sortType;
        return this;
    }

    public OfferingFilter search(String search) {
        this.search = search;
        return this;
    }

    public OfferingFilter availableOn(Date availableOn) {
        this.availableOn = availableOn;
        return this;
    }

    public OfferingFilter createdBy(Long userId) {
        this.createdBy = userId;
        return this;
    }

    public OfferingFilter inEvent(Long eventId) {
        this.inEvent = eventId;
        return this;
    }

    public OfferingFilter includeDeleted(Boolean includeDeleted) {
        if (includeDeleted == null) {
            includeDeleted = false;
        }
        this.includeDeleted = includeDeleted;
        return this;
    }

    public Boolean getIncludeDeleted() {
        return includeDeleted;
    }

    public OfferingCategory getCategory() {
        return category;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public Integer getAttendees() {
        return attendees;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public SortType getSortType() {
        return sortType;
    }

    public String getSearch() {
        return search;
    }

    public Date getAvailableOn() {
        return availableOn;
    }

    public Long getLikedBy() {
        return likedBy;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public Long getInEvent() {
        return inEvent;
    }
}
