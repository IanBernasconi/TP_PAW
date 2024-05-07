package ar.edu.itba.paw.webapp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class FieldsMustMatchValidator implements ConstraintValidator<FieldsMustMatch, Object> {

    private String[] fields;

    @Override
    public void initialize(FieldsMustMatch fieldsMustMatch) {
        this.fields = fieldsMustMatch.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context){
        try {
            Field firstField = value.getClass().getDeclaredField(fields[0]);
            firstField.setAccessible(true);
            Object firstFieldValue = firstField.get(value);
            for (int i = 1; i < fields.length; i++) {
                Field field = value.getClass().getDeclaredField(fields[i]);
                field.setAccessible(true);
                Object fieldValue = field.get(value);
                if (firstFieldValue == null || !firstFieldValue.equals(fieldValue)) {
                    return false;
                }
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return false;
        }
        return true;
    }
}
