package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.webapp.validation.LanguageValid;
import ar.edu.itba.paw.webapp.validation.UriValid;
import ar.edu.itba.paw.webapp.validation.UriValidation;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

public class UserDTO implements UriValidation {

    private Long userId;

    @NotNull
    @Size(min = 4, max = 40)
    private String name;

    @Size(min = 8)
    private String password;

    private String resetPasswordToken;

    @Email
    private String email;

    private boolean provider;

    private float averageRating;

    private int totalLikes;

    private int totalEventsWorkedOn;

    @LanguageValid
    private String language;

    private String description;

    @UriValid(type = ImageDTO.class)
    private URI profilePicture;
    private URI self;
    private URI createdEvents;
    private URI createdOfferings;
    private URI providerRelations;
    private URI occupiedDates;

    public static UserDTO fromUser(final User user, final UriInfo uriInfo) {
        final UserDTO dto = new UserDTO();
        dto.userId = user.getId();
        dto.name = user.getUsername();
        dto.email = user.getEmail();
        dto.provider = user.isProvider();
        dto.language = user.getLanguage().getLanguage();
        dto.description = user.getDescription();

        if (user.getProfilePictureId() != null) {
            dto.profilePicture = uriInfo.getBaseUriBuilder().path("images").path(String.valueOf(user.getProfilePictureId())).build();
        }
        dto.self = getSelfUri(user.getId()  , uriInfo);
        dto.createdEvents = uriInfo.getBaseUriBuilder().path("events").queryParam("user", user.getId()).build();
        if (user.isProvider()) {
            dto.createdOfferings = uriInfo.getBaseUriBuilder().path("services").queryParam("createdBy", user.getId()).build();
        }
        dto.providerRelations = uriInfo.getBaseUriBuilder().path("relations").queryParam("provider", user.getId()).build();

        dto.averageRating = user.getAverageRating();
        dto.totalLikes = user.getTotalLikes();
        dto.totalEventsWorkedOn = user.getEventsAttended();
        dto.occupiedDates = getSelfUriBuilder(user.getId(), uriInfo).path("occupiedDates").build();
        return dto;
    }

    public static URI getSelfUri(final long userId, final UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path("users").path(String.valueOf(userId)).build();
    }

    public static UriBuilder getSelfUriBuilder(final long userId, final UriInfo uriInfo) {
        return uriInfo.getBaseUriBuilder().path("users").path(String.valueOf(userId));
    }

    @Override
    public boolean isValidUri(URI uri) {
        return isValidUri(uri, "users");
    }

    public static long getIdFromUri(final URI uri) {
        return Long.parseLong(uri.getPath().substring(uri.getPath().lastIndexOf('/') + 1));
    }

    public UserDTO() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public URI getSelf() {
        return self;
    }

    public void setSelf(URI self) {
        this.self = self;
    }

    public URI getCreatedEvents() {
        return createdEvents;
    }

    public void setCreatedEvents(URI createdEvents) {
        this.createdEvents = createdEvents;
    }

    public URI getCreatedOfferings() {
        return createdOfferings;
    }

    public void setCreatedOfferings(URI createdOfferings) {
        this.createdOfferings = createdOfferings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isProvider() {
        return provider;
    }

    public void setProvider(boolean provider) {
        this.provider = provider;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public URI getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(URI profilePicture) {
        this.profilePicture = profilePicture;
    }

    public URI getProviderRelations() {
        return providerRelations;
    }

    public void setProviderRelations(URI providerRelations) {
        this.providerRelations = providerRelations;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public int getTotalEventsWorkedOn() {
        return totalEventsWorkedOn;
    }

    public void setTotalEventsWorkedOn(int totalEventsWorkedOn) {
        this.totalEventsWorkedOn = totalEventsWorkedOn;
    }

    public URI getOccupiedDates() {
        return occupiedDates;
    }

    public void setOccupiedDates(URI occupiedDates) {
        this.occupiedDates = occupiedDates;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }
}
