package ar.edu.itba.paw.webapp.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

@Documented
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Constraint(validatedBy = GreaterOrEqualValidator.class)
public @interface GreaterOrEqual {

    String firstField();
    String secondField();
    String message() default "{ar.edu.itba.paw.webapp.validation.GreaterOrEqual.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
