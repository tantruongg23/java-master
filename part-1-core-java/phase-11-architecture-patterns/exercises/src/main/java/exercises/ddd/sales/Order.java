package exercises.ddd.sales;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate root for the Sales bounded context.
 *
 * <p>Invariants enforced by this aggregate:
 * <ul>
 *   <li>An order must have at least one line.</li>
 *   <li>The total must exceed a minimum threshold.</li>
 *   <li>Once placed, lines cannot be modified.</li>
 * </ul>
 *
 * <p>Domain events are collected internally and published after persistence.
 *
 * <p><strong>Exercise 2:</strong> Implement the factory method and
 * domain event raising. Use TDD.
 */
public class Order {

    private final String orderId;
    private final String customerId;
    private final List<OrderLine> lines;
    private final LocalDateTime placedAt;
    private final List<DomainEvent> domainEvents;

    private Order(String customerId, List<OrderLine> lines) {
        this.orderId = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.lines = List.copyOf(lines);
        this.placedAt = LocalDateTime.now();
        this.domainEvents = new ArrayList<>();
    }

    /**
     * Factory method — the only way to create an Order.
     *
     * @param customerId the customer placing the order
     * @param lines      the items in the order
     * @return a new Order with an {@code OrderPlaced} event queued
     * @throws IllegalArgumentException if validation fails
     */
    public static Order place(String customerId, List<OrderLine> lines) {
        // TODO: Validate customerId not blank
        // TODO: Validate lines not empty
        // TODO: Validate total >= minimum
        // TODO: Create order and raise OrderPlaced event
        throw new UnsupportedOperationException("TODO: implement place");
    }

    /**
     * @return the total value of the order
     */
    public BigDecimal getTotal() {
        return lines.stream()
                .map(OrderLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * @return domain events raised by this aggregate (cleared after publishing)
     */
    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * Clears the list of pending domain events (called after persistence + publishing).
     */
    public void clearDomainEvents() {
        domainEvents.clear();
    }

    protected void raise(DomainEvent event) {
        domainEvents.add(event);
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public List<OrderLine> getLines() { return lines; }
    public LocalDateTime getPlacedAt() { return placedAt; }

    /**
     * Value object representing a line in an order.
     */
    public record OrderLine(String productId, int quantity, BigDecimal unitPrice) {

        public OrderLine {
            if (productId == null || productId.isBlank())
                throw new IllegalArgumentException("productId required");
            if (quantity <= 0)
                throw new IllegalArgumentException("quantity must be positive");
            if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0)
                throw new IllegalArgumentException("unitPrice must be positive");
        }

        public BigDecimal lineTotal() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    /**
     * Domain event raised when a new order is placed.
     */
    public record OrderPlaced(
            String eventId,
            String orderId,
            String customerId,
            BigDecimal total,
            LocalDateTime occurredAt
    ) implements DomainEvent {

        @Override
        public String getEventId() { return eventId; }

        @Override
        public LocalDateTime getTimestamp() { return occurredAt; }
    }
}
