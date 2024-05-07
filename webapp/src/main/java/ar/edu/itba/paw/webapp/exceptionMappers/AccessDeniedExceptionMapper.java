package ar.edu.itba.paw.webapp.exceptionMappers;

import ar.edu.itba.paw.webapp.dto.ErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static ar.edu.itba.paw.webapp.auth.BasicAuthFilter.REALM;
import static ar.edu.itba.paw.webapp.auth.JwtAuthFilter.BEARER_AUTHENTICATION;

@Singleton
@Component
@Provider
public class AccessDeniedExceptionMapper implements ExceptionMapper<AccessDeniedException> {


    private static final Logger LOGGER = LoggerFactory.getLogger(AccessDeniedExceptionMapper.class);

    @Override
    public Response toResponse(AccessDeniedException exception) {
        LOGGER.error("{}: {}", exception.getClass().getName(), exception.getMessage());

        return Response
                .status(Response.Status.FORBIDDEN)
                .header(HttpHeaders.WWW_AUTHENTICATE, BEARER_AUTHENTICATION + " realm=\"" + REALM + "\"")
                .entity(new ErrorDTO(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON.toString())
                .build();
    }
}

