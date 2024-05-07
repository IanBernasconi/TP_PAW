package ar.edu.itba.paw.models.exception.notFound;

public class UserNotFoundException extends EntityNotFoundException {

    public static final String ENTITY_NAME = "user";

    public UserNotFoundException(Long userId) {
        super(ENTITY_NAME, userId);
    }

}
