package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CategoryValidator.class)
public @interface Category {

    String message() default "{ar.edu.itba.paw.webapp.validation.Category.message}";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}