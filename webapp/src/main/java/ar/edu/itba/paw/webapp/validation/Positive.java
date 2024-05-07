package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PositiveValidator.class)
public @interface Positive {

    String message() default "{ar.edu.itba.paw.webapp.validation.Positive.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}
