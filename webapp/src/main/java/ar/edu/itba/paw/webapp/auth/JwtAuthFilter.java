package ar.edu.itba.paw.webapp.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String BEARER_AUTHENTICATION = "Bearer";
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthFilter.class.getName());

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private PawUserDetailsService userDetailsService;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!authUtils.isTokenBasedAuthentication(authorizationHeader, BEARER_AUTHENTICATION)) {
            chain.doFilter(request, response);
            return;
        }

        String token = authUtils.getToken(authorizationHeader, BEARER_AUTHENTICATION);

        try {
            if (!jwtUtils.validateToken(token)) {
                throw new AuthenticationCredentialsNotFoundException("Invalid token");
            }
            String email = jwtUtils.extractEmail(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            final boolean isOneTimeToken = jwtUtils.isOneTimeToken(token);
            if (isOneTimeToken) {
                userDetails = userDetailsService.addAuthorities(userDetails, Collections.singletonList(Role.ONE_TIME_TOKEN));
                ((PawAuthUser) userDetails).setToken(token);
            }

            final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (jwtUtils.isTokenRefresh(token) || isOneTimeToken) {
                response = authUtils.addAuthHeaders(request, response, ((PawAuthUser) userDetails).getUser());
            }
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, e);
            return;
        }

        chain.doFilter(request, response);
    }


}
