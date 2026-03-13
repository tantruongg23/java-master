package exercises.hexagonal.domain;

/**
 * Lifecycle states of an {@link Order}.
 *
 * <pre>
 * CREATED ──► CONFIRMED ──► SHIPPED ──► DELIVERED
 *    │                         │
 *    └──────► CANCELLED ◄─────┘
 * </pre>
 *
 * Cancellation is only permitted before shipping.
 */
public enum OrderStatus {

    /** Order has been created but not yet confirmed. */
    CREATED,

    /** Order has been confirmed and is awaiting shipment. */
    CONFIRMED,

    /** Order has been shipped to the customer. */
    SHIPPED,

    /** Order has been delivered to the customer. */
    DELIVERED,

    /** Order has been cancelled (only before shipping). */
    CANCELLED
}
