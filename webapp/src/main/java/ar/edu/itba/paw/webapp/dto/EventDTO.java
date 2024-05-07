package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.events.Event;
import ar.edu.itba.paw.webapp.validation.UriValidation;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Date;

public class EventDTO implements UriValidation {
    public static final String ISO_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final String PATH = "events";

    private long id;

    private String name;

    private String description;

    private Date date;

    private int numberOfGuests;

    private District district;

    private URI self;

    private URI owner;

    private URI relations;

    private URI relatedOfferings;

    private URI reviews;

    private URI guests;

    public EventDTO() {
    }

    public static EventDTO fromEvent(final Event event, final UriInfo uriInfo) {
        final EventDTO dto = new EventDTO();
        dto.id = event.getId();
        dto.name = event.getName();
        dto.description = event.getDescription();
        dto.date = event.getDate();
        dto.numberOfGuests = event.getNumberOfGuests();
        dto.district = event.getDistrict();
        dto.self = getSelfUri(event.getId(), uriInfo);
        dto.owner = UserDTO.getSelfUri(event.getUser().getId(), uriInfo);
        dto.relations = uriInfo.getBaseUriBuilder().path("relations").queryParam("event", event.getId()).build();
        dto.relatedOfferings = uriInfo.getBaseUriBuilder().path("services").queryParam("event", event.getId()).queryParam("includeDeleted", true).build();
        dto.reviews = uriInfo.getBaseUriBuilder().path("reviews").queryParam("event", event.getId()).build();
        dto.guests = uriInfo.getBaseUriBuilder().path(PATH).path(String.valueOf(event.getId())).path("guests").build();
        return dto;
    }

    public static URI getSelfUri(final long id, final UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path(PATH).path(String.valueOf(id)).build();
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
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

    public URI getRelations() {
        return relations;
    }

    public void setRelations(URI relations) {
        this.relations = relations;
    }

    public URI getRelatedOfferings() {
        return relatedOfferings;
    }

    public void setRelatedOfferings(URI relatedOfferings) {
        this.relatedOfferings = relatedOfferings;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public URI getReviews() {
        return reviews;
    }

    public void setReviews(URI reviews) {
        this.reviews = reviews;
    }

    public URI getGuests() {
        return guests;
    }

    public void setGuests(URI guests) {
        this.guests = guests;
    }
}
