package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.offering.Offering;
import ar.edu.itba.paw.webapp.validation.*;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@GreaterOrEqual(firstField = "maxPrice", secondField = "minPrice")
@VenueDistrict(category = "category", district = "district")
public class OfferingDTO implements UriValidation {

    public static final String PATH = "services";

    public static final int MAX_IMAGES = 10;
    private long id;

    @NotNull
    @NotEmpty
    @Size(min = 4, max = 32)
    private String name;

    private String description;

    @NotNull
    @Category
    private String category;

    @Positive
    @Max(10000000)
    private float minPrice;

    @Positive
    @Max(10000000)
    private float maxPrice;

    @NotNull
    @NotEmpty
    @PriceTypeValid
    private String priceType;

    @Min(0)
    @Max(10000000)
    private int maxGuests;

    @NotNull
    @SpecifiedDistrict
    private District district;

    private boolean deleted;

    private long likes;
    private double rating;
    private URI self;

    @NotNull
    @UriValid(type = UserDTO.class)
    private URI owner;

    @Size(max = MAX_IMAGES)
    @UriValid(type = ImageDTO.class)
    private List<URI> images = new ArrayList<>();
    
    private URI reviews;

    public static OfferingDTO fromOffering(final Offering offering, final UriInfo uriInfo) {
        final OfferingDTO dto = new OfferingDTO();
        dto.id = offering.getId();
        dto.name = offering.getName();
        dto.description = offering.getDescription();
        dto.category = offering.getOfferingCategory().toString();
        dto.minPrice = offering.getMinPrice();
        dto.maxPrice = offering.getMaxPrice();
        dto.priceType = offering.getPriceType().toString();
        dto.maxGuests = offering.getMaxGuests();
        dto.district = offering.getDistrict();
        dto.deleted = offering.isDeleted();

        dto.likes = offering.getLikes();
        dto.rating = offering.getAverageRating();
        dto.self = getSelfUri(offering.getId(), uriInfo);
        dto.owner = UserDTO.getSelfUri(offering.getOwner().getId(), uriInfo);


        if (offering.getHasImage()) {
            offering.getImageIds().forEach(image -> dto.images.add(uriInfo.getBaseUriBuilder().path("images").path(String.valueOf(image)).build()));
        }
        dto.reviews = uriInfo.getBaseUriBuilder().path("reviews").queryParam("offering", offering.getId()).build();
        return dto;
    }


    public static URI getSelfUri(final long id, final UriInfo uriInfo){
        return getSelfUriBuilder(id, uriInfo).build();
    }

    public static UriBuilder getSelfUriBuilder(final long id, final UriInfo uriInfo){
        return uriInfo.getBaseUriBuilder().path(PATH).path(String.valueOf(id));
    }

    public static Long getIdFromUri(final URI uri) {
        final String[] segments = uri.getPath().split("/");
        try {
            return Long.valueOf(segments[segments.length - 1]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public boolean isValidUri(URI uri) {
        return isValidUri(uri, PATH);
    }

    public OfferingDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(float minPrice) {
        this.minPrice = minPrice;
    }

    public float getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(float maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getOwner() {
        return owner;
    }

    public void setOwner(URI owner) {
        this.owner = owner;
    }

    public List<URI> getImages() {
        return images;
    }

    public void setImages(List<URI> images) {
        this.images = images;
    }
    public URI getReviews() {
        return reviews;
    }

    public void setReviews(URI reviews) {
        this.reviews = reviews;
    }
}
