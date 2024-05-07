package ar.edu.itba.paw.webapp.exception.httpExceptions;

import ar.edu.itba.paw.webapp.exception.GeneralRuntimeException;

import javax.ws.rs.core.Response;

public class GuestAlreadyExistsException extends GeneralRuntimeException {

    public GuestAlreadyExistsException(String guestEmail) {
        super(Response.Status.CONFLICT.getStatusCode(), "exception.event.guest.alreadyExists", guestEmail);
    }
}
