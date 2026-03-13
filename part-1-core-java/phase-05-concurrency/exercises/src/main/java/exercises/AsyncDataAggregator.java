package exercises;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Exercise 4 — Async Data Aggregator
 *
 * <p>Fetch data from multiple simulated services in parallel using
 * {@link CompletableFuture}. Handle timeouts, failures, and retries.</p>
 *
 * <h3>Bonus</h3>
 * Implement a simple circuit breaker pattern.
 */
public class AsyncDataAggregator {

    // ──────────────────────────────────────────────────────────────
    // Data model
    // ──────────────────────────────────────────────────────────────

    /**
     * Represents a result from one service.
     */
    public record ServiceResult(String serviceName, String data, Duration latency) {}

    /**
     * Aggregated response combining results from all services.
     */
    public record AggregatedResponse(
            List<ServiceResult> successful,
            List<String> failed,
            Duration totalDuration
    ) {}

    // ──────────────────────────────────────────────────────────────
    // Circuit breaker (Bonus)
    // ──────────────────────────────────────────────────────────────

    /**
     * Simple circuit breaker: after {@code threshold} consecutive failures,
     * the circuit opens for {@code resetTimeout} and fails fast.
     */
    static class CircuitBreaker {
        private final int threshold;
        private final Duration resetTimeout;
        private final AtomicInteger consecutiveFailures = new AtomicInteger(0);
        private volatile long openedAt = 0;

        CircuitBreaker(int threshold, Duration resetTimeout) {
            this.threshold = threshold;
            this.resetTimeout = resetTimeout;
        }

        boolean isOpen() {
            // TODO: Return true if failures >= threshold and reset timeout hasn't elapsed.
            throw new UnsupportedOperationException("TODO — implement isOpen");
        }

        void recordSuccess() {
            // TODO: Reset consecutive failures.
            throw new UnsupportedOperationException("TODO — implement recordSuccess");
        }

        void recordFailure() {
            // TODO: Increment failures; if threshold reached, set openedAt.
            throw new UnsupportedOperationException("TODO — implement recordFailure");
        }
    }

    // ──────────────────────────────────────────────────────────────
    // Fields
    // ──────────────────────────────────────────────────────────────

    private final ExecutorService executor;
    private final Duration perServiceTimeout;
    private final int maxRetries;
    private final Map<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();

    /**
     * @param executor          the executor to run async tasks on
     * @param perServiceTimeout max time to wait for a single service
     * @param maxRetries        max retry attempts per service
     */
    public AsyncDataAggregator(ExecutorService executor, Duration perServiceTimeout, int maxRetries) {
        this.executor = executor;
        this.perServiceTimeout = perServiceTimeout;
        this.maxRetries = maxRetries;
    }

    // ──────────────────────────────────────────────────────────────
    // Simulated services
    // ──────────────────────────────────────────────────────────────

    /**
     * Simulate a service call with a given delay and optional failure.
     *
     * @param name        service name
     * @param delayMillis simulated latency
     * @param failRate    probability of failure (0.0 – 1.0)
     * @return a supplier that simulates the call
     */
    Supplier<ServiceResult> simulateService(String name, long delayMillis, double failRate) {
        return () -> {
            // TODO: Sleep for delayMillis.
            // TODO: With probability failRate, throw RuntimeException.
            // TODO: Return a ServiceResult with the service name, some data, and the latency.
            throw new UnsupportedOperationException("TODO — implement simulateService");
        };
    }

    // ──────────────────────────────────────────────────────────────
    // Core API
    // ──────────────────────────────────────────────────────────────

    /**
     * Fetch data from all services in parallel, combining successes and tracking failures.
     *
     * <p>Implementation tips:
     * <ul>
     *   <li>Create a {@code CompletableFuture} per service.</li>
     *   <li>Apply {@code orTimeout} for per-service timeout.</li>
     *   <li>Use {@code handle} or {@code exceptionally} for error handling.</li>
     *   <li>Combine all futures with {@code CompletableFuture.allOf}.</li>
     * </ul>
     *
     * @param services map of service name → service supplier
     * @return aggregated response
     */
    public AggregatedResponse fetchFromServices(Map<String, Supplier<ServiceResult>> services) {
        // TODO: For each service, create a CompletableFuture with supplyAsync.
        // TODO: Add timeout, retry logic, circuit breaker check.
        // TODO: Wait for all to complete.
        // TODO: Partition into successful and failed.
        // TODO: Return AggregatedResponse.
        throw new UnsupportedOperationException("TODO — implement fetchFromServices");
    }

    /**
     * Retry a supplier up to {@code maxRetries} times with exponential backoff.
     *
     * @param supplier   the operation to retry
     * @param retries    remaining retries
     * @param backoffMs  current backoff in millis
     * @return a CompletableFuture that eventually succeeds or fails
     */
    <T> CompletableFuture<T> retryWithBackoff(Supplier<T> supplier, int retries, long backoffMs) {
        // TODO: Try supplyAsync; on failure, if retries > 0, sleep backoffMs then recurse.
        // TODO: Double the backoff each time.
        throw new UnsupportedOperationException("TODO — implement retryWithBackoff");
    }

    // ──────────────────────────────────────────────────────────────
    // Main
    // ──────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        ExecutorService exec = Executors.newFixedThreadPool(10);
        AsyncDataAggregator aggregator = new AsyncDataAggregator(exec, Duration.ofSeconds(2), 3);

        Map<String, Supplier<ServiceResult>> services = Map.of(
                "UserService", aggregator.simulateService("UserService", 200, 0.0),
                "OrderService", aggregator.simulateService("OrderService", 500, 0.1),
                "InventoryService", aggregator.simulateService("InventoryService", 1000, 0.2),
                "PaymentService", aggregator.simulateService("PaymentService", 300, 0.05),
                "NotificationService", aggregator.simulateService("NotificationService", 2500, 0.3)
        );

        AggregatedResponse response = aggregator.fetchFromServices(services);

        System.out.println("Successful: " + response.successful().size());
        System.out.println("Failed    : " + response.failed());
        System.out.println("Total time: " + response.totalDuration().toMillis() + " ms");

        exec.shutdown();
    }
}
