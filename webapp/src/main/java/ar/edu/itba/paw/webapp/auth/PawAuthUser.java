package ar.edu.itba.paw.webapp.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class PawAuthUser extends User {

    private final long id;

    private String token;

    private final ar.edu.itba.paw.models.User user;

    public PawAuthUser(String username, String password, long id, ar.edu.itba.paw.models.User user, String token, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.user = user;
        this.token = token;
    }

    public long getId() {
        return id;
    }

    public ar.edu.itba.paw.models.User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
