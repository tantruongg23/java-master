package exercises.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * Inbound DTO for creating or updating a Task.
 *
 * <p>Uses Bean Validation annotations so that {@code @Valid} on the controller
 * parameter triggers automatic validation before the handler method executes.</p>
 *
 * TODO:
 * <ul>
 *   <li>Consider adding validation groups ({@code OnCreate}, {@code OnUpdate})
 *       so that PATCH requests can skip required-field checks.</li>
 *   <li>Add a custom cross-field validator if business rules span multiple fields
 *       (e.g., dueDate must be after today only when status is not COMPLETED).</li>
 *   <li>Create a matching {@code TaskResponse} record for outbound representation.</li>
 * </ul>
 *
 * @param title       task title — required, 1-200 characters
 * @param description optional long description — max 2000 characters
 * @param status      task status — required (PENDING, IN_PROGRESS, COMPLETED, CANCELLED)
 * @param priority    task priority — required (LOW, MEDIUM, HIGH, CRITICAL)
 * @param dueDate     must be a future date (or today)
 * @param assignee    optional username of the assignee
 */
public record TaskRequest(

        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
        String title,

        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,

        @NotNull(message = "Status is required")
        String status,

        @NotNull(message = "Priority is required")
        String priority,

        @Future(message = "Due date must be in the future")
        LocalDate dueDate,

        String assignee
) {
    // TODO: define Status and Priority as enums and use @ValidEnum or @Pattern
    // TODO: add validation group interfaces (OnCreate, OnUpdate) for PATCH support
}
