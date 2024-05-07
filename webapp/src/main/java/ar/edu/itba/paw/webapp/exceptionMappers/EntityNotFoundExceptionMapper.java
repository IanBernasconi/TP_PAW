package ar.edu.itba.paw.webapp.exceptionMappers;

import ar.edu.itba.paw.models.exception.notFound.EntityNotFoundException;
import ar.edu.itba.paw.webapp.dto.ErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Locale;

@Component
@Provider
public class EntityNotFoundExceptionMapper implements ExceptionMapper<EntityNotFoundException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityNotFoundException.class);

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(EntityNotFoundException exception) {
        LOGGER.error("{}: {}", exception.getClass().getName(),
                messageSource.getMessage("exception.entity.notFound", new Object[]{exception.getEntityName(), exception.getEntityId()}, Locale.ENGLISH));

        return Response.status(Response.Status.NOT_FOUND)
                .entity(ErrorDTO.withMessage(messageSource.getMessage("exception.entity.notFound",
                        new Object[]{exception.getEntityName(), exception.getEntityId()}, LocaleContextHolder.getLocale())))
                .build();
    }
}