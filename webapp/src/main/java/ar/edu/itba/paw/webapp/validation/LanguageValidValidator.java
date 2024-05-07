package ar.edu.itba.paw.webapp.validation;

import ar.edu.itba.paw.models.EmailLanguages;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LanguageValidValidator implements ConstraintValidator<LanguageValid, String> {

    @Override
    public void initialize(LanguageValid language) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return EmailLanguages.isImplemented(value);
    }
}
