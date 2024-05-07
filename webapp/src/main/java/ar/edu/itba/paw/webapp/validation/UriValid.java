package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UriValidValidator.class, UriListValidValidator.class})
public @interface UriValid {

    Class<? extends UriValidation> type(); 

    String message() default "{ar.edu.itba.paw.webapp.validation.UriValid.message}";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
