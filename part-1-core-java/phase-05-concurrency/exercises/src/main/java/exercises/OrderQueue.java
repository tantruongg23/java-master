package exercises;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * Exercise 1 — Producer-Consumer Order System
 *
 * <p>Multi-threaded order processing pipeline using a {@link BlockingQueue}.
 * Producers generate orders; consumers validate, charge, and fulfill them.
 * The system shuts down gracefully via the poison-pill pattern.</p>
 *
 * <h3>Bonus</h3>
 * Implement back-pressure — producers block when the queue exceeds a capacity threshold.
 */
public class OrderQueue {

    // ──────────────────────────────────────────────────────────────
    // Data model
    // ──────────────────────────────────────────────────────────────

    /**
     * Represents a customer order. A record with {@code id == -1} serves as the poison pill.
     */
    public record Order(long id, String item, int quantity, double price, Instant createdAt) {
        /** Sentinel value that signals consumers to shut down. */
        public static Order poisonPill() {
            return new Order(-1, "POISON", 0, 0.0, Instant.now());
        }

        public boolean isPoisonPill() {
            return id == -1;
        }
    }

    // ──────────────────────────────────────────────────────────────
    // Fields
    // ──────────────────────────────────────────────────────────────

    private final BlockingQueue<Order> queue;
    private final int numProducers;
    private final int numConsumers;

    private final LongAdder totalProduced = new LongAdder();
    private final LongAdder totalConsumed = new LongAdder();
    private final LongAdder totalProcessingTimeNanos = new LongAdder();

    // ──────────────────────────────────────────────────────────────
    // Constructor
    // ──────────────────────────────────────────────────────────────

    /**
     * @param capacity     maximum queue size (enables back-pressure)
     * @param numProducers number of producer threads
     * @param numConsumers number of consumer threads
     */
    public OrderQueue(int capacity, int numProducers, int numConsumers) {
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.numProducers = numProducers;
        this.numConsumers = numConsumers;
    }

    // ──────────────────────────────────────────────────────────────
    // Producer
    // ──────────────────────────────────────────────────────────────

    /**
     * Produce {@code count} random orders and put them on the queue.
     *
     * <p>After generating all orders, put one poison pill per consumer.</p>
     *
     * @param count number of orders to produce
     */
    public void produce(int count) {
        // TODO: Generate random orders in a loop.
        // TODO: Use queue.put() (blocks when full → back-pressure).
        // TODO: Increment totalProduced for each order.
        // TODO: After the loop, add poison pills for each consumer.
        throw new UnsupportedOperationException("TODO — implement produce");
    }

    // ──────────────────────────────────────────────────────────────
    // Consumer
    // ──────────────────────────────────────────────────────────────

    /**
     * Consume orders from the queue until a poison pill is received.
     */
    public void consume() {
        // TODO: Loop: take an order from the queue.
        // TODO: If it is a poison pill, break.
        // TODO: Process the order: validate → charge → fulfill (simulate with small sleep).
        // TODO: Track processing time and increment totalConsumed.
        throw new UnsupportedOperationException("TODO — implement consume");
    }

    /**
     * Validate an order (non-null item, positive quantity and price).
     */
    boolean validate(Order order) {
        // TODO: Return true if order fields are valid.
        throw new UnsupportedOperationException("TODO — implement validate");
    }

    /**
     * Simulate charging for an order.
     */
    void charge(Order order) {
        // TODO: Simulate with Thread.sleep(5–20ms).
        throw new UnsupportedOperationException("TODO — implement charge");
    }

    /**
     * Simulate fulfilling an order.
     */
    void fulfill(Order order) {
        // TODO: Simulate with Thread.sleep(10–50ms).
        throw new UnsupportedOperationException("TODO — implement fulfill");
    }

    // ──────────────────────────────────────────────────────────────
    // Orchestration
    // ──────────────────────────────────────────────────────────────

    /**
     * Start the producer-consumer system, wait for completion, print metrics.
     *
     * @param ordersPerProducer number of orders each producer generates
     */
    public void run(int ordersPerProducer) {
        // TODO: Create an ExecutorService for producers and consumers.
        // TODO: Submit numProducers tasks calling produce(ordersPerProducer).
        // TODO: Submit numConsumers tasks calling consume().
        // TODO: Shutdown and await termination.
        // TODO: Print metrics.
        throw new UnsupportedOperationException("TODO — implement run");
    }

    // ──────────────────────────────────────────────────────────────
    // Metrics
    // ──────────────────────────────────────────────────────────────

    public long getTotalProduced() { return totalProduced.sum(); }
    public long getTotalConsumed() { return totalConsumed.sum(); }

    public Duration getAverageProcessingTime() {
        long consumed = totalConsumed.sum();
        if (consumed == 0) return Duration.ZERO;
        return Duration.ofNanos(totalProcessingTimeNanos.sum() / consumed);
    }

    // ──────────────────────────────────────────────────────────────
    // Main
    // ──────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        OrderQueue system = new OrderQueue(100, 3, 5);
        system.run(1000);

        System.out.println("Produced : " + system.getTotalProduced());
        System.out.println("Consumed : " + system.getTotalConsumed());
        System.out.println("Avg time : " + system.getAverageProcessingTime().toMillis() + " ms");
    }
}
