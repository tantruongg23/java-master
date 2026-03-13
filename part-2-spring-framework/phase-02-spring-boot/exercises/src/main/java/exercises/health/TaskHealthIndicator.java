package exercises.health;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

/**
 * Custom Actuator health indicator that monitors task capacity.
 *
 * <p>Reports {@code UP} when the current task count is below the
 * configured maximum, and {@code DOWN} (or {@code WARNING}) when
 * the system is at or near capacity.
 *
 * <p>Accessible at {@code /actuator/health} (and the detail view
 * at {@code /actuator/health/task}).
 *
 * <p><b>TODO:</b>
 * <ol>
 *   <li>Inject {@code AppProperties} to read {@code app.tasks.max-count}.</li>
 *   <li>Inject a {@code TaskService} (or repository) to get the current task count.</li>
 *   <li>Implement the health check logic in {@link #doHealthCheck(Health.Builder)}.</li>
 * </ol>
 */
@Component
public class TaskHealthIndicator extends AbstractHealthIndicator {

    // TODO: Inject dependencies
    //
    // private final AppProperties properties;
    // private final TaskService taskService;
    //
    // public TaskHealthIndicator(AppProperties properties, TaskService taskService) {
    //     this.properties = properties;
    //     this.taskService = taskService;
    // }

    /**
     * Performs the health check.
     *
     * @param builder the health builder to report status and details
     */
    @Override
    protected void doHealthCheck(Health.Builder builder) {
        // TODO: Implement health check logic
        //
        // int currentCount = taskService.getTaskCount();
        // int maxCount = properties.getTasks().getMaxCount();
        //
        // builder.withDetail("currentTaskCount", currentCount)
        //        .withDetail("maxTaskCount", maxCount);
        //
        // if (currentCount >= maxCount) {
        //     builder.down()
        //            .withDetail("reason", "Task limit reached");
        // } else if (currentCount >= maxCount * 0.9) {
        //     builder.up()
        //            .withDetail("warning", "Approaching task limit (>90%)");
        // } else {
        //     builder.up();
        // }

        builder.up().withDetail("status", "TODO — implement health check");
    }
}
