package ar.edu.itba.paw.webapp.exception.httpExceptions;

import ar.edu.itba.paw.webapp.exception.GeneralRuntimeException;

import javax.ws.rs.core.Response;

public class ServerErrorException extends GeneralRuntimeException {

    public ServerErrorException(String messageCode, Object... args) {
        super(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), messageCode, args);
    }
}
