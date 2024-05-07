package ar.edu.itba.paw.webapp.dto;

public class FieldErrorDTO {

    private String fieldName;
    private String message;

    public FieldErrorDTO() {
        // Empty constructor needed by JAX-RS
    }

    public FieldErrorDTO(final String fieldName, final String message) {
        this.fieldName = fieldName;
        this.message = message;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(final String fieldName) {
        this.fieldName = fieldName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }


}
