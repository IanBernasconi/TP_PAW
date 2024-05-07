package ar.edu.itba.paw.webapp.exception.httpExceptions.user;

import ar.edu.itba.paw.webapp.exception.GeneralRuntimeException;

import javax.ws.rs.core.Response;

public class UserVerificationException extends GeneralRuntimeException {

  public UserVerificationException(String email) {
    super(Response.Status.CONFLICT.getStatusCode(), "exception.user.verification.error", email);
  }
}