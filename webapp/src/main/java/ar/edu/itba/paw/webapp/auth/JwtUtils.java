package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.ws.rs.core.UriBuilder;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);

    private final SecretKey key;

    private final Environment env;

    @Autowired
    public JwtUtils(Environment env) {
        this.env = env;
        this.key = Keys.hmacShaKeyFor(env.getProperty("jwt.secret").getBytes());
    }


    public String generateToken(String subject, Map<String, String> contents) {
        final ClaimsBuilder claims = Jwts.claims();
        claims.subject(subject);
        for (Map.Entry<String, String> entry : contents.entrySet()) {
            claims.add(entry.getKey(), entry.getValue());
        }
        Jwts.HeaderBuilder header = Jwts.header();
        header.type("JWT");

        return Jwts.builder()
                .header().type("JWT").and()
                .claims(claims.build())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + Long.parseLong(env.getProperty("jwt.expiration"))))
                .signWith(key)
                .compact();
    }

    public String generateAuthToken(String subject, User user, String baseUri) {
        final ClaimsBuilder claims = Jwts.claims();
        claims.subject(subject);
        claims.add("user", UriBuilder.fromUri(baseUri).path("users").path(String.valueOf(user.getId())).build().toString());

        return generateGenericToken(claims.build(), env.getProperty("jwt.expiration"));
    }

    public String generateRefreshToken(String subject) {
        final ClaimsBuilder claims = Jwts.claims();
        claims.subject(subject);
        claims.add("refresh", true);

        return generateGenericToken(claims.build(), env.getProperty("jwt.refreshExpiration"));
    }


    public String generateOneTimeToken(String subject, String userURI) {
        final ClaimsBuilder claims = Jwts.claims();
        claims.subject(subject);
        claims.add("user", userURI);
        claims.add("oneTime", true);

        return generateGenericToken(claims.build(), null);
    }

    public boolean isOneTimeToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return claims.getPayload().get("oneTime", Boolean.class);
        } catch (JwtException | IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);

            Date expiration = claims.getPayload().getExpiration();
            Date now = new Date();
            return expiration == null || !expiration.before(now);
        } catch (JwtException | IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    public boolean isTokenRefresh(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return claims.getPayload().get("refresh", Boolean.class);
        } catch (JwtException | IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    private String generateGenericToken(Claims claims, String expiration) {

        JwtBuilder builder = Jwts.builder()
                .header().type("JWT").and()
                .claims(claims)
                .issuedAt(new Date());

        if (expiration != null) {
            builder = builder.expiration(new Date(System.currentTimeMillis() + Long.parseLong(expiration)));
        }

        return builder.signWith(key)
                .compact();
    }
}