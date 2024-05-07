package ar.edu.itba.paw.webapp.exception.httpExceptions;

import ar.edu.itba.paw.webapp.exception.GeneralRuntimeException;

import javax.ws.rs.core.Response;

public class StatusConflictException extends GeneralRuntimeException {

  public StatusConflictException(String messageCode, Object... args) {
    super(Response.Status.CONFLICT.getStatusCode(), messageCode, args);
  }
}
