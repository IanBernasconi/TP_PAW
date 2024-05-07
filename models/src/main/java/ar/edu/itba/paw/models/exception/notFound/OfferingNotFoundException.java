package ar.edu.itba.paw.models.exception.notFound;

public class OfferingNotFoundException extends EntityNotFoundException {

    public static final String ENTITY_NAME = "service";

    public OfferingNotFoundException(Long offeringId) {
        super(ENTITY_NAME, offeringId);
    }

}
