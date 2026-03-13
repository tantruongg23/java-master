package exercises;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Entry point for the Phase 03 — Spring Data &amp; JPA exercises.
 *
 * <p>{@link EnableJpaAuditing} activates automatic population of
 * {@code @CreatedDate}, {@code @LastModifiedDate}, {@code @CreatedBy},
 * and {@code @LastModifiedBy} fields on JPA entities annotated with
 * {@code @EntityListeners(AuditingEntityListener.class)}.
 *
 * <p><b>TODO:</b> If you need {@code @CreatedBy} / {@code @LastModifiedBy},
 * provide an {@code AuditorAware<String>} bean that returns the current user.
 */
@SpringBootApplication
@EnableJpaAuditing
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
