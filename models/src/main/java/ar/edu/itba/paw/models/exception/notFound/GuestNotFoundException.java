package ar.edu.itba.paw.models.exception.notFound;

public class GuestNotFoundException extends EntityNotFoundException {

    public static final String ENTITY_NAME = "guest";

    public GuestNotFoundException(Long offeringId) {
        super(ENTITY_NAME, offeringId);
    }

}
