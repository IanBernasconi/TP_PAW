package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.PriceType;

import javax.validation.ConstraintValidator;

public class PriceTypeValidator implements ConstraintValidator<PriceTypeValid, String> {

    @Override
    public void initialize(PriceTypeValid priceTypeValid) {}

    @Override
    public boolean isValid(String value, javax.validation.ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return PriceType.fromString(value) != null;
    }
}
