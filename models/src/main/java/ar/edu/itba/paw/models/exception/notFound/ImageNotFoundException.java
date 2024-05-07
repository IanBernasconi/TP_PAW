package ar.edu.itba.paw.models.exception.notFound;

public class ImageNotFoundException extends EntityNotFoundException {

    public static final String ENTITY_NAME = "image";


    public ImageNotFoundException(Long relationId) {
        super(ENTITY_NAME, relationId);
    }

}
