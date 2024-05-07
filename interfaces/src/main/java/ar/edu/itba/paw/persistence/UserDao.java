package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.EmailLanguage;
import ar.edu.itba.paw.models.RangeFilter;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exception.DuplicateEmailException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserDao {

    Optional<User> create(String name, String password, String email, boolean isProvider, EmailLanguage language) throws DuplicateEmailException;

    void update(User user, String username, Long profilePictureId, String description, EmailLanguage language);

    void update(User user, String username, String description, EmailLanguage language);

    void verifyUser(User user) ;

    void changePassword(String username, String password);

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    void updateVerificationToken(User user, String verificationToken);

    void updateResetPasswordToken(User user, String resetPasswordToken);

    void resetPassword(long id, String password);

    User setProvider(long userId, boolean isProvider);

    List<Date> getUserOccupiedDates(User user, RangeFilter rangeFilter);

}
