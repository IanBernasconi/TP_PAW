package ar.edu.itba.paw.models;

import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User implements Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(sequenceName = "users_id_seq", name = "users_id_seq", allocationSize = 1)
    private long id;

    @Column(name = "username")
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "description", length = 1023)
    private String description;

    @Column(name = "profile_image_id")
    private Long profilePictureId;

    @Column(name = "is_provider", nullable = false, columnDefinition = "boolean default false")
    private boolean isProvider;

    @Column(name = "language", length = 5)
    private String language;

    @Column(name = "registered", nullable = false, columnDefinition = "boolean default true")
    private boolean registered;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT now()")
    private LocalDateTime registrationDate;

    @Column(name = "verified", nullable = false, columnDefinition = "boolean default false")
    private boolean verified;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verification_token_created_at", columnDefinition = "TIMESTAMP DEFAULT now()")
    private LocalDateTime verificationTokenDate;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_token_created_at", columnDefinition = "TIMESTAMP DEFAULT now()")
    private LocalDateTime resetPasswordTokenDate;

    @Formula("(SELECT COALESCE(COUNT(*),0) FROM liked_offerings lo JOIN offerings o ON lo.offering_id = o.id WHERE o.user_id = id)")
    private int totalLikes;

    @Formula("(SELECT COALESCE(AVG(r.rating),0) FROM reviews r JOIN events_offerings eo ON eo.relation_id = r.relation_id JOIN offerings o ON eo.offering_id = o.id WHERE o.user_id = id)")
    private float averageRating;

    @Formula("(SELECT COALESCE(COUNT(DISTINCT eo.event_id),0) FROM events_offerings eo JOIN offerings o ON eo.offering_id = o.id WHERE o.user_id = id)")
    private int eventsAttended;

    protected User() {
    }

    public User(String password, String name, String email, boolean isProvider, EmailLanguage language) {
        this.password = password;
        this.name = name;
        this.email = email;
        this.isProvider = isProvider;
        this.language = language.getLanguage();
        this.verificationToken = null;
        this.verified = false;
        this.registered = true;
        this.registrationDate = LocalDateTime.now();
        this.verificationTokenDate = LocalDateTime.now();
    }

    public User(long id, String password, String name, String email,boolean isProvider, EmailLanguage language) {
        this(password, name, email, isProvider, language);
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return name;
    }

    // For Principal, the unique identifier is the email
    @Override
    public String getName() {
        return email;
    }

    public String getEmail() {
        return email;
    }

    public long getId() {
        return id;
    }

    public boolean isProvider() {
        return isProvider;
    }

    public String getDescription() {
        return description;
    }

    public Long getProfilePictureId() {
        return profilePictureId;
    }

    public EmailLanguage getLanguage() {
        return EmailLanguages.fromString(language);
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProfilePictureId(Long profilePictureId) {
        this.profilePictureId = profilePictureId;
    }

    public void setProvider(boolean provider) {
        isProvider = provider;
    }

    public void setLanguage(EmailLanguage language) {
        this.language = language.getLanguage();
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public LocalDateTime getVerificationTokenDate() {
        return verificationTokenDate;
    }

    public void setVerificationTokenDate(LocalDateTime verificationTokenDate) {
        this.verificationTokenDate = verificationTokenDate;
    }

    public String getResetPasswordToken() {
        return resetPasswordToken;
    }

    public void setResetPasswordToken(String resetPasswordToken) {
        this.resetPasswordToken = resetPasswordToken;
    }

    public LocalDateTime getResetPasswordTokenDate() {
        return resetPasswordTokenDate;
    }

    public void setResetPasswordTokenDate(LocalDateTime resetPasswordTokenDate) {
        this.resetPasswordTokenDate = resetPasswordTokenDate;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public int getEventsAttended() {
        return eventsAttended;
    }

    public String toString() {
        return "User: " + name + " (" + email + ")" + " - " + isProvider;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;
        return id == user.id;
    }
}
