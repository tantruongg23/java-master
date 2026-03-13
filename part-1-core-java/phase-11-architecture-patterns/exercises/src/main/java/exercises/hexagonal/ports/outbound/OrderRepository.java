package exercises.hexagonal.ports.outbound;

import exercises.hexagonal.domain.Order;
import exercises.hexagonal.domain.OrderStatus;

import java.util.List;
import java.util.Optional;

/**
 * Outbound port — defines how the domain persists and retrieves orders.
 *
 * <p>Implementations (adapters) may be in-memory, JDBC, JPA, etc.
 * The domain depends on this interface, never on a concrete adapter.
 */
public interface OrderRepository {

    /**
     * Persists an order (insert or update).
     *
     * @param order the order to save
     * @return the saved order
     */
    Order save(Order order);

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param orderId the order id
     * @return the order, or empty if not found
     */
    Optional<Order> findById(String orderId);

    /**
     * Retrieves all orders for a given customer.
     *
     * @param customerId the customer id
     * @return list of orders (may be empty)
     */
    List<Order> findByCustomer(String customerId);

    /**
     * Retrieves all orders with a given status.
     *
     * @param status the status to filter by
     * @return list of matching orders
     */
    List<Order> findByStatus(OrderStatus status);
}
