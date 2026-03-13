package exercises.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Global exception handler that translates exceptions into RFC 7807
 * {@link ProblemDetail} responses.
 *
 * <p>Every error response follows a consistent structure so API consumers
 * can rely on a single error contract across all endpoints.</p>
 *
 * TODO:
 * <ul>
 *   <li>Create {@code ResourceNotFoundException} and other domain exceptions.</li>
 *   <li>Add handlers for {@code HttpMessageNotReadableException},
 *       {@code MissingServletRequestParameterException}, etc.</li>
 *   <li>Decide on a URI scheme for {@code ProblemDetail.type}
 *       (e.g., {@code https://api.example.com/errors/validation}).</li>
 *   <li>Consider logging the original exception at WARN/ERROR level.</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Bean Validation failures triggered by {@code @Valid}.
     *
     * @param ex the validation exception containing field errors
     * @return HTTP 400 with a ProblemDetail listing every invalid field
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Validation Failed");
        problem.setType(URI.create("https://api.example.com/errors/validation"));
        problem.setDetail("One or more fields failed validation.");

        // TODO: extract field errors into a structured list
        List<Map<String, String>> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "message", fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value",
                        "rejected", String.valueOf(fe.getRejectedValue())
                ))
                .toList();

        problem.setProperty("errors", fieldErrors);
        problem.setProperty("timestamp", Instant.now());

        return problem;
    }

    /**
     * Handles "resource not found" scenarios.
     *
     * TODO: Create a {@code ResourceNotFoundException} class, e.g.:
     * <pre>{@code
     * public class ResourceNotFoundException extends RuntimeException {
     *     public ResourceNotFoundException(String resource, Object id) {
     *         super(resource + " not found with id " + id);
     *     }
     * }
     * }</pre>
     *
     * @param ex the not-found exception
     * @return HTTP 404 with ProblemDetail
     */
    // TODO: uncomment once ResourceNotFoundException is created
    // @ExceptionHandler(ResourceNotFoundException.class)
    // public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
    //     ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    //     problem.setTitle("Resource Not Found");
    //     problem.setType(URI.create("https://api.example.com/errors/not-found"));
    //     problem.setDetail(ex.getMessage());
    //     problem.setProperty("timestamp", Instant.now());
    //     return problem;
    // }

    /**
     * Catch-all for unhandled exceptions — prevents stack traces from leaking.
     *
     * @param ex any uncaught exception
     * @return HTTP 500 with a generic ProblemDetail
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        // TODO: log the full stack trace at ERROR level
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create("https://api.example.com/errors/internal"));
        problem.setDetail("An unexpected error occurred. Please contact support.");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
