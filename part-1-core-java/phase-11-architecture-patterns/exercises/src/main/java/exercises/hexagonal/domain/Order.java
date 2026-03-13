package exercises.hexagonal.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate root representing a customer order.
 *
 * <p>Invariants:
 * <ul>
 *   <li>An order must have at least one line.</li>
 *   <li>Minimum order value is $10.</li>
 *   <li>State transitions follow a strict lifecycle — see {@link OrderStatus}.</li>
 * </ul>
 *
 * <p><strong>Exercise 1:</strong> Implement the business rule methods using TDD.
 */
public class Order {

    private static final BigDecimal MINIMUM_ORDER_VALUE = new BigDecimal("10.00");

    private final String orderId;
    private final String customerId;
    private final List<OrderLine> lines;
    private OrderStatus status;
    private final LocalDateTime createdAt;

    /**
     * Creates a new order in CREATED status.
     *
     * @param customerId the customer placing the order
     * @param lines      the order lines (must not be empty)
     * @throws IllegalArgumentException if lines are empty or total is below minimum
     */
    public Order(String customerId, List<OrderLine> lines) {
        // TODO: Validate customerId not blank
        // TODO: Validate lines not empty
        // TODO: Validate total >= MINIMUM_ORDER_VALUE
        this.orderId = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.lines = new ArrayList<>(lines);
        this.status = OrderStatus.CREATED;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Confirms the order. Only valid from CREATED status.
     *
     * @throws IllegalStateException if the order is not in CREATED status
     */
    public void confirm() {
        // TODO: Validate current status is CREATED
        // TODO: Transition to CONFIRMED
        throw new UnsupportedOperationException("TODO: implement confirm");
    }

    /**
     * Marks the order as shipped. Only valid from CONFIRMED status.
     *
     * @throws IllegalStateException if the order is not in CONFIRMED status
     */
    public void ship() {
        // TODO: Validate current status is CONFIRMED
        // TODO: Transition to SHIPPED
        throw new UnsupportedOperationException("TODO: implement ship");
    }

    /**
     * Marks the order as delivered. Only valid from SHIPPED status.
     *
     * @throws IllegalStateException if the order is not in SHIPPED status
     */
    public void deliver() {
        // TODO: Validate current status is SHIPPED
        // TODO: Transition to DELIVERED
        throw new UnsupportedOperationException("TODO: implement deliver");
    }

    /**
     * Cancels the order. Only valid before shipping (CREATED or CONFIRMED).
     *
     * @throws IllegalStateException if the order has already been shipped or delivered
     */
    public void cancel() {
        // TODO: Validate current status is CREATED or CONFIRMED
        // TODO: Transition to CANCELLED
        throw new UnsupportedOperationException("TODO: implement cancel");
    }

    /**
     * @return the total order value (sum of all lines)
     */
    public BigDecimal getTotal() {
        return lines.stream()
                .map(OrderLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<OrderLine> getLines() { return Collections.unmodifiableList(lines); }

    /**
     * An individual line in an order.
     *
     * @param productId the product identifier
     * @param quantity  number of units (must be > 0)
     * @param unitPrice price per unit (must be > 0)
     */
    public record OrderLine(String productId, int quantity, BigDecimal unitPrice) {

        public OrderLine {
            if (productId == null || productId.isBlank())
                throw new IllegalArgumentException("productId must not be blank");
            if (quantity <= 0)
                throw new IllegalArgumentException("quantity must be positive");
            if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0)
                throw new IllegalArgumentException("unitPrice must be positive");
        }

        /** @return quantity * unitPrice */
        public BigDecimal lineTotal() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
