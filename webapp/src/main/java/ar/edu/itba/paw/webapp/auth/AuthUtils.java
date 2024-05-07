package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthUtils {

    @Autowired
    private JwtUtils jwtUtils;

    HttpServletResponse addAuthHeaders(HttpServletRequest request, HttpServletResponse response, User user) {
        String baseUri = request.getRequestURL().toString()
                .replace(request.getPathInfo(), "");
        response.setHeader("X-AuthToken", jwtUtils.generateAuthToken(user.getEmail(), user, baseUri));
        response.setHeader("X-RefreshToken", jwtUtils.generateRefreshToken(user.getEmail()));
        response.setHeader("Cache-Control", "no-store");
        return response;
    }

    boolean isTokenBasedAuthentication(String authorizationHeader, String scheme) {
        return authorizationHeader != null && authorizationHeader.toLowerCase().startsWith(scheme.toLowerCase() + " ");
    }

    String getToken(String authorizationHeader, String scheme) {
        return authorizationHeader.substring(scheme.length()).trim();
    }

}
