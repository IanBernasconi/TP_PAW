package ar.edu.itba.paw.webapp.validation;

import javax.validation.ConstraintValidator;

public class PositiveValidator implements ConstraintValidator<Positive, Number>{

    @Override
    public void initialize(Positive positive) {}

    @Override
    public boolean isValid(Number value, javax.validation.ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return value.doubleValue() > 0;
    }

}
