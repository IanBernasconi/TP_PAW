package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.exception.EmailNotVerifiedException;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class BasicAuthFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private AuthUtils authUtils;

    public static final String REALM = "PartyPicker";
    public static final String BASIC_AUTHENTICATION = "Basic";

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (!authUtils.isTokenBasedAuthentication(authorizationHeader, BASIC_AUTHENTICATION)) {
            chain.doFilter(request, response);
            return;
        }

        String token = authUtils.getToken(authorizationHeader, BASIC_AUTHENTICATION);

        try {
            byte[] decoded = Base64.decode(token);
            if (decoded == null || decoded.length == 0) {
                throw new AuthenticationException("Invalid token") {
                };
            }

            String[] credentials = new String(decoded).split(":");
            String email = credentials[0];
            String password = credentials[1];

            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            Optional<User> maybeUser = userService.findByEmail(email);
            if (!maybeUser.isPresent()) {
                throw new AuthenticationException("Invalid token") {
                };
            }
            User user = maybeUser.get();

            if (!user.isVerified()) {
                throw new EmailNotVerifiedException("User " + email + " is not verified");
            }

            response = authUtils.addAuthHeaders(request, response, user);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, e);
            return;
        }

        chain.doFilter(request, response);
    }



}

