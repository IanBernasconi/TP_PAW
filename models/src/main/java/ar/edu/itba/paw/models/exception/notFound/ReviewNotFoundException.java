package ar.edu.itba.paw.models.exception.notFound;


public class ReviewNotFoundException extends EntityNotFoundException {

    public static final String ENTITY_NAME = "review";


    public ReviewNotFoundException(Long reviewId) {
        super(ENTITY_NAME, reviewId);
    }

}
