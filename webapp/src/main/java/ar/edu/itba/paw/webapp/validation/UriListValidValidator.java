package ar.edu.itba.paw.webapp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.net.URI;
import java.util.List;


public class UriListValidValidator implements ConstraintValidator<UriValid, List<URI>> {

    Class<? extends UriValidation> type;

    @Override
    public void initialize(UriValid constraintAnnotation) {
        type = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(List<URI> uri, ConstraintValidatorContext context) {
        try {
            UriValidation instance = type.getDeclaredConstructor().newInstance();
            for (URI u : uri) {
                if (!instance.isValidUri(u)) {
                    return false;
                }
            }
            return true;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

}