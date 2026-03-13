package exercises.hexagonal.application;

import exercises.hexagonal.domain.Order;
import exercises.hexagonal.ports.inbound.OrderService;
import exercises.hexagonal.ports.outbound.OrderRepository;

import java.util.List;

/**
 * Application service (use case) implementing the inbound {@link OrderService} port.
 *
 * <p>Orchestrates domain logic and delegates persistence to the
 * outbound {@link OrderRepository} port. Contains no business rules
 * itself — those live in the {@link Order} aggregate.
 *
 * <p><strong>Exercise 1:</strong> Implement each method.
 */
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order createOrder(String customerId, List<Order.OrderLine> lines) {
        // TODO: Create a new Order (constructor validates business rules)
        // TODO: Save via repository
        // TODO: Return the persisted order
        throw new UnsupportedOperationException("TODO: implement createOrder");
    }

    @Override
    public Order confirmOrder(String orderId) {
        // TODO: Load order from repository (throw if not found)
        // TODO: Call order.confirm() (domain validates state transition)
        // TODO: Save and return
        throw new UnsupportedOperationException("TODO: implement confirmOrder");
    }

    @Override
    public Order shipOrder(String orderId) {
        // TODO: Load order from repository (throw if not found)
        // TODO: Call order.ship()
        // TODO: Save and return
        throw new UnsupportedOperationException("TODO: implement shipOrder");
    }

    @Override
    public Order cancelOrder(String orderId) {
        // TODO: Load order from repository (throw if not found)
        // TODO: Call order.cancel()
        // TODO: Save and return
        throw new UnsupportedOperationException("TODO: implement cancelOrder");
    }
}
