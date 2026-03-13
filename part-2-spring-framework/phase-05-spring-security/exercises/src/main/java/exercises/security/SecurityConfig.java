package exercises.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Central security configuration for all exercises.
 *
 * <p>Defines the {@link SecurityFilterChain} with JWT-based stateless
 * authentication and role-based URL authorization.</p>
 *
 * TODO:
 * <ul>
 *   <li>Inject {@link JwtAuthenticationFilter} and register it with
 *       {@code addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)}.</li>
 *   <li>Configure {@code requestMatchers} for public endpoints
 *       ({@code /api/auth/**}, {@code /h2-console/**}).</li>
 *   <li>Protect {@code /api/admin/**} with {@code hasRole("ADMIN")}.</li>
 *   <li>Configure CORS with allowed origins for your frontend.</li>
 *   <li>Disable CSRF for the stateless API (or enable cookie-based CSRF for session mode).</li>
 *   <li>Add OAuth2 login configuration for Exercise 2.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // TODO: inject JwtAuthenticationFilter and JwtTokenProvider

    /**
     * Configures the HTTP security filter chain.
     *
     * @param http the {@link HttpSecurity} DSL
     * @return the built {@link SecurityFilterChain}
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // TODO: implement the full filter chain configuration
        //
        // http
        //     .csrf(csrf -> csrf.disable())
        //     .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        //     .authorizeHttpRequests(auth -> auth
        //         .requestMatchers("/api/auth/**").permitAll()
        //         .requestMatchers("/api/admin/**").hasRole("ADMIN")
        //         .anyRequest().authenticated()
        //     )
        //     .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Password encoder using BCrypt with default strength (10 rounds).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // TODO: configure AuthenticationManager bean
    // TODO: configure CorsConfigurationSource bean
}
