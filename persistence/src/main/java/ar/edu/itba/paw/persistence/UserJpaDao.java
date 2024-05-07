package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.EmailLanguage;
import ar.edu.itba.paw.models.RangeFilter;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.eventOfferingRelation.OfferingStatus;
import ar.edu.itba.paw.models.exception.DuplicateEmailException;
import ar.edu.itba.paw.models.exception.notFound.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class UserJpaDao implements UserDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserJpaDao.class);

    @Autowired
    private ImageDao imageDao;

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public Optional<User> create(String username, String password, String email, boolean isProvider, EmailLanguage language) throws DuplicateEmailException {
        if (password == null || password.trim().isEmpty() || email == null || email.trim().isEmpty()) {
            LOGGER.warn("Trying to create user with null or empty password or email");
            return Optional.empty();
        }

        User oldUser = findByEmail(email).orElse(null);
        if (oldUser != null && oldUser.isRegistered()) {
            LOGGER.info("Trying to create user with already registered email: " + email);
            throw new DuplicateEmailException("Email already exists: " + email);
        } else if (oldUser != null) {
            oldUser.setPassword(password);
            oldUser.setName(username);
            oldUser.setProvider(isProvider);
            oldUser.setLanguage(language);
            em.persist(oldUser);
            return Optional.of(oldUser);
        }

        User user = new User(password, username, email.toLowerCase(),  isProvider, language);
        em.persist(user);
        return findByEmail(email);
    }

    @Override
    @Transactional
    public void update(User user, String username, Long profilePictureId, String description, EmailLanguage language) {
        user = updateUserValues(user, username, description, language);

        if (user.getProfilePictureId() != null) {
            imageDao.deleteImage(user.getProfilePictureId());
        }

        user.setProfilePictureId(profilePictureId);
        em.persist(user);
    }

    @Override
    @Transactional
    public void update(User user, String username, String description, EmailLanguage language) {
        user = updateUserValues(user, username, description, language);
        em.persist(user);
    }

    private User updateUserValues(User user, String username, String description, EmailLanguage language) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        user.setName(username);
        user.setDescription(description);
        user.setLanguage(language);

        return user;
    }

    @Override
    @Transactional
    public void verifyUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        findById(user.getId()).orElseThrow(() -> new UserNotFoundException(user.getId()));
        user.setVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenDate(null);
        em.persist(user);
    }

    @Override
    @Transactional
    public void changePassword(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Email or password cannot be null or empty");
        }
        User user = findByEmail(email).orElseThrow(() -> new UserNotFoundException(null));
        user.setPassword(password);
        em.persist(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(long id) {
        if (id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return em.createQuery("from User where email = :email", User.class)
                .setParameter("email", email.toLowerCase())
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    @Transactional
    public void updateVerificationToken(User user, String verificationToken) {
        if (user == null){
            throw new IllegalArgumentException("User cannot be null");
        }
        if (verificationToken == null || verificationToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Verification token cannot be empty");
        }
        if (user.isVerified()) {
            throw new IllegalArgumentException("User is already verified");
        }
        findById(user.getId()).orElseThrow(() -> new UserNotFoundException(user.getId()));
        user.setVerificationToken(verificationToken);
        em.persist(user);
    }

    @Override
    @Transactional
    public void updateResetPasswordToken(User user, String resetPasswordToken) {
        if (user == null){
            throw new IllegalArgumentException("User cannot be null");
        }
        if (resetPasswordToken == null || resetPasswordToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Email and reset password token cannot be empty");
        }
        user.setResetPasswordToken(resetPasswordToken);
        user.setResetPasswordTokenDate(LocalDateTime.now());
        em.persist(user);
    }

    @Override
    @Transactional
    public void resetPassword(long id, String password) {
        User user = findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setPassword(password);
        user.setResetPasswordToken(null);
        em.persist(user);
    }

    @Override
    @Transactional
    public User setProvider(long userId, boolean isProvider) throws UserNotFoundException {
        if (userId <= 0) {
            throw new IllegalArgumentException("User id must be greater than 0");
        }
        User user = findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setProvider(isProvider);
        em.persist(user);
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Date> getUserOccupiedDates(User user, RangeFilter rangeFilter) {
        String query = "select rel.event.date from EventOfferingRelation rel where rel.offering.owner.id = :userId and rel.status = :acceptedStatus";
        Map<String, Object> params = new HashMap<>();

        if (rangeFilter.getStart() != null) {
            query += " and rel.event.date >= :startDate";
            params.put("startDate", rangeFilter.getStart());
        }

        if (rangeFilter.getEnd() != null) {
            query += " and rel.event.date <= :endDate";
            params.put("endDate", rangeFilter.getEnd());
        }

        TypedQuery<Date> typedQuery = em.createQuery(query, Date.class)
                .setParameter("userId", user.getId())
                .setParameter("acceptedStatus", OfferingStatus.ACCEPTED);

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            typedQuery.setParameter(entry.getKey(), entry.getValue());
        }

        return typedQuery.getResultList();
    }

}