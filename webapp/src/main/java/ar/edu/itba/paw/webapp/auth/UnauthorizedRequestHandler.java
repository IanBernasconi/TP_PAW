package ar.edu.itba.paw.webapp.auth;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class UnauthorizedRequestHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException {
        httpServletResponse.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
        httpServletResponse.setHeader("X-AuthToken", null);
        httpServletResponse.setHeader("X-RefreshToken", null);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON.toString());
        httpServletResponse.getWriter().write(String.format("{\"error\": \"Unauthorized: %s\"}", e.getMessage()));
    }
}
