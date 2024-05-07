package ar.edu.itba.paw.webapp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.URI;


public class UriValidValidator implements ConstraintValidator<UriValid, URI> {

    Class<? extends UriValidation> type;

    @Override
    public void initialize(UriValid constraintAnnotation) {
        type = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(URI uri, ConstraintValidatorContext context) {
        try {
            UriValidation instance = type.getDeclaredConstructor().newInstance();
            return instance.isValidUri(uri);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

}