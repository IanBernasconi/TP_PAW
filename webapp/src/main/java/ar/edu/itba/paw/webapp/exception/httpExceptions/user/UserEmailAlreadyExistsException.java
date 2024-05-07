package ar.edu.itba.paw.webapp.exception.httpExceptions.user;

import ar.edu.itba.paw.webapp.exception.GeneralRuntimeException;

import javax.ws.rs.core.Response;

public class UserEmailAlreadyExistsException extends GeneralRuntimeException {

    public UserEmailAlreadyExistsException(String email) {
        super(Response.Status.CONFLICT.getStatusCode(), "exception.user.email.duplicate", email);
    }
}
