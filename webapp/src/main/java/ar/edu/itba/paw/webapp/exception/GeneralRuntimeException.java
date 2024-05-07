package ar.edu.itba.paw.webapp.exception;

public class GeneralRuntimeException extends RuntimeException {

    private final int statusCode;

    private final String messageCode;

    private final Object[] args;

    public GeneralRuntimeException(int statusCode, String messageCode, Object... args) {
        super();
        this.statusCode = statusCode;
        this.messageCode = messageCode;
        this.args = args;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public Object[] getArgs() {
        return args;
    }


}
