package ar.edu.itba.paw.webapp.exceptionMappers;

import ar.edu.itba.paw.webapp.dto.FieldErrorDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;

@Component
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConstraintViolationExceptionMapper.class);

    public Response toResponse(ConstraintViolationException e) {
        List<FieldErrorDTO> errors = new ArrayList<>();

        e.getConstraintViolations().forEach(violation -> errors.add(new FieldErrorDTO(getPropertyName(violation), violation.getMessage())));

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new GenericEntity<List<FieldErrorDTO>>(errors) {
                })
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private String getPropertyName(ConstraintViolation<?> violation) {
        final String propertyPath = violation.getPropertyPath().toString();
        return propertyPath.substring(propertyPath.lastIndexOf(".") + 1);
    }

}
