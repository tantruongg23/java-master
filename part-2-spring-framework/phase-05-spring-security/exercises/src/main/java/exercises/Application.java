package exercises;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Phase 05 — Spring Security exercises.
 *
 * <p>Default credentials for the in-memory user (if configured):
 * {@code user / password}. Override via {@code application.yml} or the
 * custom {@code UserDetailsService} in the security package.</p>
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
