package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.District;
import ar.edu.itba.paw.models.offering.OfferingCategory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Objects;

public class VenueDistrictValidator implements ConstraintValidator<VenueDistrict, Object> {

    private String category;
    private String district;

    @Override
    public void initialize(VenueDistrict constraintAnnotation) {
        category = constraintAnnotation.category();
        district = constraintAnnotation.district();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        try {
            Class<?> clazz = object.getClass();
            Field categoryObject = clazz.getDeclaredField(category);
            Field districtObject = clazz.getDeclaredField(district);
            categoryObject.setAccessible(true);
            districtObject.setAccessible(true);

            Object category = categoryObject.get(object);
            Object district = districtObject.get(object);

            if (category == null || district == null) {
                return true;
            }

            if (category instanceof String && district instanceof District) {
                return !(Objects.equals(OfferingCategory.fromString((String) category), OfferingCategory.VENUE) && Objects.equals(district, District.ALL));
            } else {
                return false;
            }
        } catch (NoSuchFieldException |  IllegalArgumentException | IllegalAccessException e) {
            return false;
        }
    }

}
