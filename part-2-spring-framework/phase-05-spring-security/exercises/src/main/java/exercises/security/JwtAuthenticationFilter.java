package exercises.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT authentication filter that runs once per request.
 *
 * <p>Extracts the Bearer token from the {@code Authorization} header,
 * validates it, and populates the {@link SecurityContextHolder} with
 * the authenticated user's details.</p>
 *
 * <p>Register this filter in {@link SecurityConfig} using
 * {@code addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)}.</p>
 *
 * TODO:
 * <ul>
 *   <li>Inject {@link JwtTokenProvider} and a {@code UserDetailsService}.</li>
 *   <li>In {@link #doFilterInternal}, extract the token from the header.</li>
 *   <li>Validate the token with {@code jwtTokenProvider.validateToken(token)}.</li>
 *   <li>Load the user from {@code UserDetailsService} and create a
 *       {@code UsernamePasswordAuthenticationToken}.</li>
 *   <li>Set the authentication in the {@code SecurityContextHolder}.</li>
 *   <li>Handle the token-blacklist check for logout support (Exercise 1).</li>
 * </ul>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    // TODO: inject JwtTokenProvider
    // TODO: inject UserDetailsService

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // TODO: implement the following steps:
        //
        // 1. Extract the JWT from the Authorization header
        // String token = extractToken(request);
        //
        // 2. Validate the token
        // if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
        //
        //     // 3. Check if token is blacklisted (for logout support)
        //     // if (tokenBlacklistService.isBlacklisted(token)) { ... }
        //
        //     // 4. Load user details
        //     String username = jwtTokenProvider.getUsernameFromToken(token);
        //     UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        //
        //     // 5. Create authentication token and set in context
        //     UsernamePasswordAuthenticationToken authentication =
        //             new UsernamePasswordAuthenticationToken(
        //                     userDetails, null, userDetails.getAuthorities());
        //     authentication.setDetails(
        //             new WebAuthenticationDetailsSource().buildDetails(request));
        //     SecurityContextHolder.getContext().setAuthentication(authentication);
        // }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT from the {@code Authorization: Bearer <token>} header.
     *
     * @param request the HTTP request
     * @return the token string, or {@code null} if absent
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
