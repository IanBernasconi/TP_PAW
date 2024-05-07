package ar.edu.itba.paw.models.exception.notFound;

public class RelationNotFoundException extends EntityNotFoundException {

    public static final String ENTITY_NAME = "relation";


    public RelationNotFoundException(Long relationId) {
        super(ENTITY_NAME, relationId);
    }

}
