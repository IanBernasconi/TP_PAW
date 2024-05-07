package ar.edu.itba.paw.webapp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class GreaterOrEqualValidator implements ConstraintValidator<GreaterOrEqual, Object> {

    private String firstField;
    private String secondField;

    @Override
    public void initialize(GreaterOrEqual constraintAnnotation) {
        firstField = constraintAnnotation.firstField();
        secondField = constraintAnnotation.secondField();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        try {
            Class<?> clazz = object.getClass();
            Field firstFieldObject = clazz.getDeclaredField(firstField);
            Field secondFieldObject = clazz.getDeclaredField(secondField);
            firstFieldObject.setAccessible(true);
            secondFieldObject.setAccessible(true);

            Object firstFieldValue = firstFieldObject.get(object);
            Object secondFieldValue = secondFieldObject.get(object);

            if (firstFieldValue == null || secondFieldValue == null) {
                return true;
            }

            if (firstFieldValue instanceof Number && secondFieldValue instanceof Number) {
                double firstValue = ((Number) firstFieldValue).doubleValue();
                double secondValue = ((Number) secondFieldValue).doubleValue();
                return firstValue >= secondValue;
            } else {
                return false;
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            return false;
        }
    }

}
