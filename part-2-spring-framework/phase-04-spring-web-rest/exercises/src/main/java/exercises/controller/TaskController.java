package exercises.controller;

import exercises.dto.TaskRequest;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for Task management (Exercise 1).
 *
 * <p>Provides full CRUD, pagination, filtering and search.
 * Each endpoint returns HATEOAS-enriched responses.</p>
 *
 * TODO — Implement the body of every endpoint:
 * <ul>
 *   <li>Wire a {@code TaskService} (to be created) for business logic.</li>
 *   <li>Return {@code EntityModel<TaskResponse>} or {@code PagedModel} for collections.</li>
 *   <li>Add HATEOAS links with {@code WebMvcLinkBuilder}.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task management endpoints")
public class TaskController {

    // TODO: inject TaskService

    /**
     * Create a new task.
     *
     * @param request validated task payload
     * @return the created task with HTTP 201
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new task")
    public ResponseEntity<?> create(@Valid @RequestBody TaskRequest request) {
        // TODO: delegate to service, wrap result in EntityModel, return 201 with Location header
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Retrieve a single task by ID.
     *
     * @param id task identifier
     * @return the task with HATEOAS links
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a task by ID")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        // TODO: fetch from service, return EntityModel with self/update/delete links
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * List tasks with pagination, filtering and search.
     *
     * @param page     zero-based page index (default 0)
     * @param size     page size (default 20)
     * @param sort     sort field and direction, e.g. "dueDate,asc"
     * @param status   optional filter by status (PENDING, IN_PROGRESS, COMPLETED, CANCELLED)
     * @param priority optional filter by priority (LOW, MEDIUM, HIGH, CRITICAL)
     * @param assignee optional filter by assignee username
     * @param title    optional search — case-insensitive "contains" on title
     * @return paged collection of tasks
     */
    @GetMapping
    @Operation(summary = "List tasks with pagination, filtering, and search")
    public ResponseEntity<?> list(
            @RequestParam(defaultValue = "0") @Parameter(description = "Page index") int page,
            @RequestParam(defaultValue = "20") @Parameter(description = "Page size") int size,
            @RequestParam(defaultValue = "id,asc") @Parameter(description = "Sort field,direction") String sort,
            @RequestParam(required = false) @Parameter(description = "Filter by status") String status,
            @RequestParam(required = false) @Parameter(description = "Filter by priority") String priority,
            @RequestParam(required = false) @Parameter(description = "Filter by assignee") String assignee,
            @RequestParam(required = false) @Parameter(description = "Search by title (contains)") String title) {
        // TODO: build Specification from filters, query with Pageable, return PagedModel
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Fully update an existing task.
     *
     * @param id      task identifier
     * @param request validated task payload
     * @return the updated task
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a task (full replacement)")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody TaskRequest request) {
        // TODO: delegate to service, return updated EntityModel
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Partially update a task (e.g., change status only).
     *
     * @param id      task identifier
     * @param request partial task payload (validation groups can relax constraints)
     * @return the patched task
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a task")
    public ResponseEntity<?> patch(@PathVariable Long id,
                                   @RequestBody TaskRequest request) {
        // TODO: apply non-null fields from request, return updated EntityModel
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Delete a task.
     *
     * @param id task identifier
     * @return HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // TODO: delegate to service, return 204
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
