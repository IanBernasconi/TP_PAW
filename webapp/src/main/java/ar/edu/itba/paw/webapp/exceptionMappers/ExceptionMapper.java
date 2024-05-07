package ar.edu.itba.paw.webapp.exceptionMappers;

import ar.edu.itba.paw.webapp.dto.ErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Exception> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionMapper.class);

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(Exception exception) {

        LOGGER.error("Exception caught", exception);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorDTO.withMessage(messageSource.getMessage("exception.internal.server.error", null, LocaleContextHolder.getLocale())))
                .build();
    }
}
