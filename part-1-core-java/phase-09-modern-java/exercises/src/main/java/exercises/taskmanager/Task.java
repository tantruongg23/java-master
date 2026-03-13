package exercises.taskmanager;

import java.time.LocalDateTime;

/**
 * Task record for the Modern Java Showcase CLI application.
 *
 * <p>Demonstrates:</p>
 * <ul>
 *   <li><b>Records</b> — immutable data carrier</li>
 *   <li><b>Sealed interface</b> — restricted set of task states</li>
 *   <li><b>Pattern matching</b> — used in switch for command dispatch</li>
 * </ul>
 *
 * @param id          unique task identifier
 * @param title       short description
 * @param priority    task priority
 * @param state       current lifecycle state
 * @param createdAt   creation timestamp
 */
public record Task(
        TaskId id,
        String title,
        Priority priority,
        TaskState state,
        LocalDateTime createdAt
) {

    /** Compact constructor with validation. */
    public Task {
        if (id == null) throw new IllegalArgumentException("id required");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title required");
        if (priority == null) priority = Priority.MEDIUM;
        if (state == null) state = new TaskState.Open();
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    /** Convenience factory for new tasks. */
    public static Task create(String title, Priority priority) {
        return new Task(TaskId.generate(), title, priority, new TaskState.Open(), LocalDateTime.now());
    }

    // ─── Value objects ───────────────────────────────────────────────

    /** Typed wrapper around a task identifier string. */
    public record TaskId(String value) {
        public TaskId {
            if (value == null || value.isBlank()) throw new IllegalArgumentException("TaskId must not be blank");
        }

        public static TaskId generate() {
            return new TaskId(java.util.UUID.randomUUID().toString().substring(0, 8));
        }
    }

    /** Task priority levels. */
    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL;

        /**
         * Parse a priority from a string.
         *
         * TODO: Refactor to a switch expression with arrow syntax.
         */
        public static Priority fromString(String s) {
            return switch (s.toUpperCase()) {
                case "LOW" -> LOW;
                case "MEDIUM", "MED" -> MEDIUM;
                case "HIGH" -> HIGH;
                case "CRITICAL", "CRIT" -> CRITICAL;
                default -> throw new IllegalArgumentException("Unknown priority: " + s);
            };
        }
    }

    // ─── Sealed interface for task states ─────────────────────────────

    /**
     * Sealed interface restricting the set of possible task states.
     *
     * <p>Enables exhaustive pattern matching in switch expressions.</p>
     *
     * TODO: Implement the full state machine with transition methods:
     *   - Open → InProgress (via start())
     *   - InProgress → Done (via complete())
     *   - Open | InProgress → Cancelled (via cancel())
     *   - No other transitions allowed.
     */
    public sealed interface TaskState
            permits TaskState.Open, TaskState.InProgress, TaskState.Done, TaskState.Cancelled {

        /** Task has been created but work has not started. */
        record Open() implements TaskState {}

        /** Work is actively in progress. */
        record InProgress(LocalDateTime startedAt) implements TaskState {
            public InProgress() { this(LocalDateTime.now()); }
        }

        /** Task has been completed. */
        record Done(LocalDateTime completedAt) implements TaskState {
            public Done() { this(LocalDateTime.now()); }
        }

        /** Task was cancelled before completion. */
        record Cancelled(String reason, LocalDateTime cancelledAt) implements TaskState {
            public Cancelled(String reason) { this(reason, LocalDateTime.now()); }
        }
    }

    // TODO: Implement state transitions. Example:
    //
    //   public Task start() {
    //       return switch (state) {
    //           case Open o         -> new Task(id, title, priority, new TaskState.InProgress(), createdAt);
    //           case InProgress ip  -> throw new IllegalStateException("Already in progress");
    //           case Done d         -> throw new IllegalStateException("Already done");
    //           case Cancelled c    -> throw new IllegalStateException("Cancelled");
    //       };
    //   }
    //
    // TODO: Implement describe() using pattern matching switch:
    //
    //   public String describe() {
    //       return switch (state) {
    //           case Open o                          -> "[OPEN] " + title;
    //           case InProgress(var started)         -> "[IN PROGRESS since " + started + "] " + title;
    //           case Done(var completed)             -> "[DONE at " + completed + "] " + title;
    //           case Cancelled(var reason, var when) -> "[CANCELLED: " + reason + "] " + title;
    //       };
    //   }
}
