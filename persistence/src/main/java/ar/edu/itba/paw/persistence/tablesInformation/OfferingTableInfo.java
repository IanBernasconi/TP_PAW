package ar.edu.itba.paw.persistence.tablesInformation;

public final class OfferingTableInfo {

    public static final String OFFERING_TABLE = "offerings";
    public static final String OFFERING_IMAGE_TABLE = "offering_images";
    public static final String OFFERING_ID = "id";
    public static final String OFFERING_USER_ID = "user_id";
    public static final String OFFERING_NAME = "name";
    public static final String OFFERING_CATEGORY = "category";
    public static final String OFFERING_DESCRIPTION = "description";
    public static final String OFFERING_MIN_PRICE = "min_price";
    public static final String OFFERING_MAX_PRICE = "max_price";
    public static final String OFFERING_PRICE_TYPE = "price_type";
    public static final String OFFERING_MAX_GUESTS = "max_guests";
    public static final String OFFERING_DISTRICT = "district";
    public static final String OFFERING_DELETED = "deleted";
    public static final String OFFERING_IMAGE_IMAGES_IDS = "images_ids";
    public static final String OFFERING_IMAGE_IMAGE_ID = "image_id";
    public static final String OFFERING_IMAGE_OFFERING_ID = "offering_id";
    public static final String LIKED_OFFERING_TABLE = "liked_offerings";
    public static final String LIKED_OFFERING_USER_ID = "user_id";
    public static final String LIKED_OFFERING_OFFERING_ID = "offering_id";
    public static final String LIKED_OFFERING_LIKED = "liked";
    public static final String RETRIEVED_OFFERING_LIKES = OFFERING_TABLE + "_likes";


    private OfferingTableInfo() {}


}
