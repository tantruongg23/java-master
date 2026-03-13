package exercises;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the Phase 07 — Advanced Spring exercises.
 *
 * <p>{@code @EnableScheduling} activates support for {@code @Scheduled}
 * methods used by the batch job scheduling exercise.</p>
 *
 * <p>Note: this application includes both WebFlux (reactive) and
 * Spring Batch (servlet/JDBC). Spring Boot auto-detects WebFlux on the
 * classpath and starts a Netty server by default. If you need to run
 * batch jobs independently, consider using separate profiles.</p>
 */
@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
