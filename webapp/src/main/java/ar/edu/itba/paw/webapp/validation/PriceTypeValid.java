package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PriceTypeValidator.class)
public @interface PriceTypeValid {

    String message() default "{ar.edu.itba.paw.webapp.validation.PriceType.message}";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}