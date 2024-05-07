package ar.edu.itba.paw.webapp.exceptionMappers;

import ar.edu.itba.paw.webapp.dto.ErrorDTO;
import ar.edu.itba.paw.webapp.exception.GeneralRuntimeException;
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
public class GeneralRuntimeExceptionMapper implements ExceptionMapper<GeneralRuntimeException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralRuntimeExceptionMapper.class);

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(GeneralRuntimeException exception) {
        LOGGER.error("{}: {} Status code: {}", exception.getClass().getName(), messageSource.getMessage(exception.getMessageCode(), exception.getArgs(), exception.getMessage(), Locale.ENGLISH), exception.getStatusCode());

        return Response.status(exception.getStatusCode())
                .entity(ErrorDTO.withMessage(messageSource.getMessage(exception.getMessageCode(), exception.getArgs(), LocaleContextHolder.getLocale())))
                .build();
    }
}
