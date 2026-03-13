package exercises.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility for generating and validating JSON Web Tokens.
 *
 * <p>Uses the <a href="https://github.com/jwtk/jjwt">jjwt</a> library.
 * The signing key is derived from a base64-encoded secret configured in
 * {@code application.yml} as {@code app.jwt.secret}.</p>
 *
 * TODO:
 * <ul>
 *   <li>Read {@code app.jwt.secret} and {@code app.jwt.expiration-ms} from config.</li>
 *   <li>Implement {@link #generateToken(String)} — set subject, issuedAt, expiration, sign.</li>
 *   <li>Implement {@link #validateToken(String)} — parse claims, catch exceptions.</li>
 *   <li>Implement {@link #getUsernameFromToken(String)} — extract the subject claim.</li>
 *   <li>Add a {@code generateRefreshToken} method with a longer TTL.</li>
 *   <li>Consider adding custom claims (roles, userId) to the payload.</li>
 * </ul>
 */
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret:defaultSecretKeyThatShouldBeOverriddenInProduction}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms:3600000}")
    private long jwtExpirationMs;

    @Value("${app.jwt.refresh-expiration-ms:604800000}")
    private long refreshExpirationMs;

    /**
     * Generates an access token for the given username.
     *
     * @param username the authenticated user's username
     * @return a signed JWT string
     */
    public String generateToken(String username) {
        // TODO: build and sign the JWT
        //
        // Date now = new Date();
        // Date expiry = new Date(now.getTime() + jwtExpirationMs);
        // SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        //
        // return Jwts.builder()
        //         .subject(username)
        //         .issuedAt(now)
        //         .expiration(expiry)
        //         .signWith(key)
        //         .compact();

        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Validates the given JWT and returns {@code true} if the token
     * is well-formed, correctly signed, and not expired.
     *
     * @param token the JWT string
     * @return {@code true} if valid
     */
    public boolean validateToken(String token) {
        // TODO: parse the token and catch JwtException / IllegalArgumentException
        //
        // try {
        //     SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        //     Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
        //     return true;
        // } catch (JwtException | IllegalArgumentException e) {
        //     return false;
        // }

        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Extracts the username (subject claim) from a valid JWT.
     *
     * @param token the JWT string
     * @return the username stored in the token
     */
    public String getUsernameFromToken(String token) {
        // TODO: parse claims and return the subject
        //
        // SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        // Claims claims = Jwts.parser()
        //         .verifyWith(key)
        //         .build()
        //         .parseSignedClaims(token)
        //         .getPayload();
        // return claims.getSubject();

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
