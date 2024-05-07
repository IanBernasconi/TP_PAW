package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.UserService;
import ar.edu.itba.paw.webapp.exception.EmailNotVerifiedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class PawUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    private final Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException, EmailNotVerifiedException {

        final User user = userService.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));

        if (!BCRYPT_PATTERN.matcher(user.getPassword()).matches()) {
            userService.changePassword(email, user.getPassword());
            return loadUserByUsername(email);
        }

        final Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(Role.ROLE_USER.toString()));
        if (user.isProvider()) {
            authorities.add(new SimpleGrantedAuthority(Role.ROLE_PROVIDER.toString()));
        }

        return new PawAuthUser(user.getEmail(), user.getPassword(), user.getId(), user, null, authorities);
    }

    public UserDetails addAuthorities(UserDetails userDetails, List<Role> roles) {
        final Set<GrantedAuthority> authorities = new HashSet<>(userDetails.getAuthorities());
        authorities.addAll(roles.stream().map(role -> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toList()));
        return new PawAuthUser(userDetails.getUsername(), userDetails.getPassword(), ((PawAuthUser) userDetails).getId(),
                ((PawAuthUser) userDetails).getUser(),
                ((PawAuthUser) userDetails).getToken(),
                authorities);
    }
}
