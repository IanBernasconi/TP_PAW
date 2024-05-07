package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.EmailLanguage;
import ar.edu.itba.paw.models.EmailLanguages;
import ar.edu.itba.paw.models.RangeFilter;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exception.DuplicateEmailException;
import ar.edu.itba.paw.persistence.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public User setProvider(long userId, boolean isProvider) {
        return userDao.setProvider(userId, isProvider);
    }

    @Override
    public Optional<User> createUser(String name, String password, String email, boolean isProvider, String language) throws DuplicateEmailException {
        return userDao.create(name, passwordEncoder.encode(password), email, isProvider, EmailLanguages.fromString(language));
    }

    @Override
    public void updateUser(User user, String username, String description, EmailLanguage language) {
        userDao.update(user, username, description, language);
    }

    @Override
    public void updateUser(User user, String username, Long profilePicture, String description, EmailLanguage language) {
        userDao.update(user, username, profilePicture, description, language);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        return userDao.findByEmail(email.toLowerCase());
    }

    @Override
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

    @Override
    public void verifyUser(User user) {
        userDao.verifyUser(user);
    }

    @Transactional
    @Override
    public void changePassword(String username, String password) {
        userDao.changePassword(username, passwordEncoder.encode(password));
    }


    @Override
    @Transactional
    public void setUserVerificationToken(User user, String seeDetailsUrl, String verificationToken) {
        userDao.updateVerificationToken(user, verificationToken);
        emailService.buildAndSendVerificationEmail(user.getEmail(), user.getUsername(), seeDetailsUrl, user.getLanguage());

    }

    @Override
    @Transactional
    public void setUserResetPasswordToken(User user, String seeDetailsUrl, String resetPasswordToken) {
        userDao.updateResetPasswordToken(user, resetPasswordToken);
        emailService.buildAndSendResetPasswordEmail(user.getEmail(), user.getUsername(), seeDetailsUrl, user.getLanguage());
    }

    @Override
    public void resetUserPassword(long id, String password) {
        userDao.resetPassword(id, passwordEncoder.encode(password));
    }

    @Override
    public List<Date> getUserOccupiedDates(User user, RangeFilter rangeFilter) {
        return userDao.getUserOccupiedDates(user, rangeFilter);
    }
}
