package ar.edu.itba.paw.models.exception.notFound;

public class MessageNotFoundException extends EntityNotFoundException {

    public static final String ENTITY_NAME = "message";


    public MessageNotFoundException(Long relationId) {
        super(ENTITY_NAME, relationId);
    }

}
