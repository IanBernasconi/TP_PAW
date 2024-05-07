package ar.edu.itba.paw.webapp.exception.httpExceptions;

import ar.edu.itba.paw.webapp.exception.GeneralRuntimeException;

import javax.ws.rs.core.Response;

public class HttpBadRequestException extends GeneralRuntimeException {

    public HttpBadRequestException(String messageCode, Object... args) {
        super(Response.Status.BAD_REQUEST.getStatusCode(), messageCode, args);
    }
}
