package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.SortType;
import ar.edu.itba.paw.models.offering.OfferingCategory;
import ar.edu.itba.paw.models.offering.OfferingFilter;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.QueryParam;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class FilterForm {

    @NotNull
    @QueryParam("category")
    private OfferingCategory category = null;

    @NotNull
    @Min(0)
    @QueryParam("minPrice")
    private Integer minPrice = null;

    @NotEmpty
    @NotNull
    @Min(1)
    @QueryParam("maxPrice")
    private Integer maxPrice = null;

    @NotNull
    @Min(0)
    @QueryParam("attendees")
    private Integer attendees = null;

    @NotEmpty
    @QueryParam("districts")
    private List<String> districts = null;

    @QueryParam("likedBy")
    private Long likedBy = null;

    @NotNull
    @QueryParam("sortType")
    private SortType sortType = null;

    @QueryParam("search")
    private String search = null;

    @QueryParam("availableOn")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private String availableOn = null;

    @QueryParam("createdBy")
    private Long createdBy = null;

    @QueryParam("event")
    private Long inEvent = null;

    @QueryParam("includeDeleted")
    private Boolean includeDeleted = null;

    public void setCategory(String category) {
        this.category = (category == null || category.isEmpty() || OfferingCategory.fromString(category) == null) ? OfferingCategory.ALL : OfferingCategory.fromString(category);
    }

    public String getCategory() {
        return category.getName();
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public int getAttendees() {
        return attendees;
    }

    public void setAttendees(Integer attendees) {
        this.attendees = attendees;
    }

    public String getAvailableOn() {
        return availableOn;
    }

    public void setAvailableOn(String availableOn) {
        this.availableOn = availableOn;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getInEvent() {
        return inEvent;
    }

    public void setInEvent(Long inEvent) {
        this.inEvent = inEvent;
    }

    public void setCategory(OfferingCategory category) {
        this.category = category;
    }

    public Long getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(Long likedBy) {
        this.likedBy = likedBy;
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setDistricts(List<String> districts) {
        if (!(districts == null || districts.isEmpty())) {
            this.districts = districts;
        }
    }

    public List<String> getDistricts() {
        return districts;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        try {
            this.search = URLDecoder.decode(search, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            this.search = search;
        }
    }

    public Boolean getIncludeDeleted() {
        return includeDeleted;
    }

    public void setIncludeDeleted(Boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    public OfferingFilter buildFilter() {
        OfferingFilter offeringFilter = new OfferingFilter()
                .category(category)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .attendees(attendees)
                .districts(districts.stream().map(District::getDistrictByName).collect(Collectors.toList()))
                .likedBy(likedBy)
                .search(search)
                .createdBy(createdBy)
                .inEvent(inEvent)
                .includeDeleted(includeDeleted);

        if (sortType != null){
            offeringFilter.sortType(sortType);
        }

        if (availableOn != null) {
            ZonedDateTime availableOnLocalDateTime = ZonedDateTime.parse(availableOn);
            offeringFilter.availableOn(new Date(availableOnLocalDateTime.toInstant().toEpochMilli()));
        }

        return offeringFilter;
    }

    public boolean hasNotNullFields() {
        return category != null || minPrice != null || maxPrice != null || attendees != null || (districts != null && !districts.isEmpty() )  || likedBy != null || sortType != null || search != null || availableOn != null || createdBy != null || inEvent != null || includeDeleted != null;
    }

}
