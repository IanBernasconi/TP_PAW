package ar.edu.itba.paw.models.exception.notFound;

public class EventNotFoundException extends EntityNotFoundException {

    public static final String ENTITY_NAME = "event";
    public EventNotFoundException(Long eventId) {
        super(ENTITY_NAME, eventId);
    }

}
