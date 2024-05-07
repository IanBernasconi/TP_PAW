package ar.edu.itba.paw.services;

import ar.edu.itba.paw.models.EmailLanguage;
import ar.edu.itba.paw.models.RangeFilter;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.exception.DuplicateEmailException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public interface UserService {

    void changePassword(String username, String password);

    Optional<User> createUser(String name, String password, String email, boolean isProvider, String language) throws DuplicateEmailException;

    void updateUser(User user, String username, String description, EmailLanguage language);

    void updateUser(User user, String username, Long profilePictureId, String description, EmailLanguage language);

    void setUserVerificationToken(User user, String seeDetailsUrl, String token);

    void setUserResetPasswordToken(User user, String seeDetailsUrl, String resetPasswordToken);

    void resetUserPassword(long id, String password);

    Optional<User> findByEmail(String email);

    Optional<User> findById(long id);

    void verifyUser(User user) ;

    User setProvider(long userId, boolean isProvider);

    List<Date> getUserOccupiedDates(User user, RangeFilter rangeFilter);

}
