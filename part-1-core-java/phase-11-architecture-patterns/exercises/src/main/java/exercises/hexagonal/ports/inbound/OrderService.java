package exercises.hexagonal.ports.inbound;

import exercises.hexagonal.domain.Order;

import java.util.List;

/**
 * Inbound port — defines the operations that the outside world
 * can invoke on the order management domain.
 *
 * <p>Implementations live in the {@code application} package.
 * Adapters (REST controllers, CLI) delegate to this port.
 */
public interface OrderService {

    /**
     * Creates a new order for a customer.
     *
     * @param customerId the customer placing the order
     * @param lines      the items being ordered
     * @return the created order
     */
    Order createOrder(String customerId, List<Order.OrderLine> lines);

    /**
     * Confirms an existing order, making it ready for shipment.
     *
     * @param orderId the order to confirm
     * @return the updated order
     */
    Order confirmOrder(String orderId);

    /**
     * Marks an order as shipped.
     *
     * @param orderId the order to ship
     * @return the updated order
     */
    Order shipOrder(String orderId);

    /**
     * Cancels an order (only if it has not been shipped yet).
     *
     * @param orderId the order to cancel
     * @return the updated order
     */
    Order cancelOrder(String orderId);
}
