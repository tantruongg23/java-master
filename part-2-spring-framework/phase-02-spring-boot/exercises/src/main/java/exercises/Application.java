package exercises;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Phase 02 — Spring Boot exercises.
 *
 * <p>This single class bootstraps the entire application thanks to
 * {@link SpringBootApplication}, which combines:
 * <ul>
 *   <li>{@code @Configuration} — marks this class as a source of bean definitions</li>
 *   <li>{@code @EnableAutoConfiguration} — activates Spring Boot's auto-configuration</li>
 *   <li>{@code @ComponentScan} — scans the {@code exercises} package and sub-packages</li>
 * </ul>
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
