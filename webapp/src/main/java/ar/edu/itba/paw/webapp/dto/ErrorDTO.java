package ar.edu.itba.paw.webapp.dto;

public class ErrorDTO {

    private String message;

    public ErrorDTO(String message) {
        this.message = message;
    }

    public ErrorDTO() {}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ErrorDTO withMessage(String message) {
        return new ErrorDTO(message);
    }
}
