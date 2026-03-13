package exercises.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Type-safe configuration properties bound to the {@code app.*} namespace.
 *
 * <p>Maps to YAML like:
 * <pre>{@code
 * app:
 *   name: Task Manager
 *   tasks:
 *     max-count: 100
 *     default-priority: MEDIUM
 *   features:
 *     dark-mode: true
 *     export-csv: false
 * }</pre>
 *
 * <p><b>TODO:</b>
 * <ol>
 *   <li>Register this class — either add {@code @EnableConfigurationProperties(AppProperties.class)}
 *       to a {@code @Configuration} class, or annotate this class with {@code @Component}.</li>
 *   <li>Create an {@code application.yml} with sample values.</li>
 *   <li>Add profile-specific files: {@code application-dev.yml}, {@code application-prod.yml}.</li>
 * </ol>
 */
@ConfigurationProperties(prefix = "app")
@Validated
public class AppProperties {

    /** Display name shown in Actuator /info and logs. */
    @NotBlank
    private String name = "Task Manager";

    /** Task-related configuration. */
    private final Tasks tasks = new Tasks();

    /** Feature flag map — keys are flag names, values are enabled/disabled. */
    private final java.util.Map<String, Boolean> features = new java.util.LinkedHashMap<>();

    // ── Getters & Setters ────────────────────────────────────────────

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Tasks getTasks() {
        return tasks;
    }

    public java.util.Map<String, Boolean> getFeatures() {
        return features;
    }

    // ── Nested class: Tasks ──────────────────────────────────────────

    /**
     * Configuration for task limits and defaults.
     *
     * TODO: Add any additional task-related properties you need for Exercise 1.
     */
    public static class Tasks {

        /** Maximum number of tasks the system will accept. */
        @Min(1)
        private int maxCount = 100;

        /** Default priority assigned to new tasks when none is specified. */
        private String defaultPriority = "MEDIUM";

        public int getMaxCount() {
            return maxCount;
        }

        public void setMaxCount(int maxCount) {
            this.maxCount = maxCount;
        }

        public String getDefaultPriority() {
            return defaultPriority;
        }

        public void setDefaultPriority(String defaultPriority) {
            this.defaultPriority = defaultPriority;
        }
    }
}
