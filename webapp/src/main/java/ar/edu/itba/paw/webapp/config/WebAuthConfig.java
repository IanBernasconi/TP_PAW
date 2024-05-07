package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@ComponentScan({"ar.edu.itba.paw.webapp.auth", "ar.edu.itba.paw.services", "ar.edu.itba.paw.persistence"})
@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebAuthConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private PawUserDetailsService userDetailsService;


    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private BasicAuthFilter basicAuthFilter;

    @Autowired
    private Environment env;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityAccessFunctions customSecurityExpressionFunctions() {
        return new SecurityAccessFunctions();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new UnauthorizedRequestHandler();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new ForbiddenRequestHandler();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        // configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);

        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Accept", "Content-Type", "If-None-Match"));
        configuration.setExposedHeaders(Arrays.asList("Link", "Location", "Www-Authenticate", "X-AuthToken", "X-RefreshToken"));

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }


    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
                .and().headers().cacheControl().disable()
                .and().authorizeRequests()

                /*
                 * Enum endpoints
                 */
                .antMatchers(HttpMethod.GET, "/api/categories", "/api/priceTypes", "/api/districts").permitAll()


                /*
                 * User endpoints
                 */
                .antMatchers(HttpMethod.GET, "/api/users").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users").anonymous()
                .antMatchers(HttpMethod.PATCH, "/api/users/{id:\\d+}").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/{id:\\d+}/occupiedDates").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/{id:\\d+}").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/users/{id:\\d+}").access("hasRole('USER') and @securityAccessFunctions.hasUserAccess(authentication, #id)")

                /*
                 * Service endpoints
                 */
                .antMatchers(HttpMethod.POST, "/api/services").hasRole("PROVIDER")
                .antMatchers(HttpMethod.GET, "/api/services").permitAll()
                .antMatchers(HttpMethod.POST, "/api/services/{id:\\d+}/likes").hasRole("USER")
                .antMatchers("/api/services/{id:\\d+}/likes/{userId:\\d+}").access("hasRole('USER') and @securityAccessFunctions.hasUserAccess(authentication, #userId)")
                .antMatchers(HttpMethod.PUT, "/api/services/{id:\\d+}").access("hasRole('PROVIDER') and @securityAccessFunctions.isOfferingOwner(authentication, #id)")
                .antMatchers(HttpMethod.DELETE, "/api/services/{id:\\d+}").access("hasRole('PROVIDER') and @securityAccessFunctions.isOfferingOwner(authentication, #id)")
                .antMatchers(HttpMethod.GET, "/api/services/{id:\\d+}").permitAll()

                /*
                 * Event endpoints
                 */
                .antMatchers(HttpMethod.GET, "/api/events/{id:\\d+}").access("@securityAccessFunctions.hasEventAccess(authentication, #id)")
                .antMatchers(HttpMethod.PATCH, "/api/events/{id:\\d+}/guests/{guestId:\\d+}").permitAll()
                .antMatchers("/api/events/{id:\\d+}/**").access("@securityAccessFunctions.isEventOwner(authentication, #id)")
                .antMatchers(HttpMethod.GET, "/api/events").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/api/events").access("hasRole('USER')")


                /*
                 * Relation endpoints
                 */
                .antMatchers("/api/relations/{id:\\d+}/**").access("hasRole('USER') and @securityAccessFunctions.hasRelationAccess(authentication, #id)")
                .antMatchers(HttpMethod.GET, "/api/relations").access("hasRole('USER')")
                .antMatchers(HttpMethod.POST, "/api/relations").access("hasRole('USER')")

                /*
                 * Review endpoints
                 */
                .antMatchers(HttpMethod.GET, "/api/reviews/{id:\\d+}").permitAll()
                .antMatchers(HttpMethod.POST, "/api/reviews").access("hasRole('USER')")
                .antMatchers(HttpMethod.GET, "/api/reviews").permitAll()

                /*
                 * Image endpoints
                 */
                .antMatchers(HttpMethod.POST, "/api/images").access("hasRole('USER')")
                .antMatchers(HttpMethod.GET, "/api/images/{id:\\d+}").permitAll()

                .antMatchers("/**").denyAll()
                .and().cors()
                .and().csrf().disable()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(basicAuthFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/static/**");
    }

}