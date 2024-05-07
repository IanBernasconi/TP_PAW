package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.offering.OfferingCategory;

import javax.validation.ConstraintValidator;

public class CategoryValidator implements ConstraintValidator<ar.edu.itba.paw.webapp.validation.Category, String> {

    @Override
    public void initialize(ar.edu.itba.paw.webapp.validation.Category district) {}

    @Override
    public boolean isValid(String value, javax.validation.ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return OfferingCategory.fromString(value) != null;
    }
}
