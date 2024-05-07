package ar.edu.itba.paw.webapp.auth;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Component
public class ForbiddenRequestHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException {
        httpServletResponse.setStatus(Response.Status.FORBIDDEN.getStatusCode());
        httpServletResponse.setHeader("X-AuthToken", null);
        httpServletResponse.setHeader("X-RefreshToken", null);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON.toString());
        httpServletResponse.getWriter().write(String.format("{\"error\": \"Forbidden: %s\"}", e.getMessage()));
    }
}

