package exercises;

/**
 * <h2>Phase 06 — Microservices E-Commerce System</h2>
 *
 * <p><strong>This phase uses a multi-module Maven project.</strong>
 * Actual service implementations go in their own submodules
 * (see the parent {@code pom.xml} for the module list).</p>
 *
 * <p>This file exists as a <em>reference placeholder</em> that describes
 * the Order Service — the central service in the e-commerce saga.</p>
 *
 * <hr/>
 *
 * <h3>Order Service — Key Classes</h3>
 *
 * <pre>
 * exercises.order/
 * ├── OrderServiceApplication.java          — @SpringBootApplication entry point
 * ├── config/
 * │   └── KafkaConfig.java                  — Kafka topic and producer configuration
 * ├── controller/
 * │   └── OrderController.java              — REST endpoints (POST /api/orders, GET /api/orders/{id})
 * ├── dto/
 * │   ├── OrderRequest.java                 — Inbound DTO (items, userId, shippingAddress)
 * │   └── OrderResponse.java                — Outbound DTO (id, status, items, total, timestamps)
 * ├── entity/
 * │   ├── Order.java                        — JPA entity (id, userId, status, total, createdAt)
 * │   └── OrderItem.java                    — JPA entity (id, sku, quantity, unitPrice)
 * ├── event/
 * │   ├── OrderPlacedEvent.java             — Published when a new order is created
 * │   ├── StockReservedEvent.java           — Consumed: inventory has reserved stock
 * │   ├── StockInsufficientEvent.java       — Consumed: not enough stock → cancel order
 * │   ├── PaymentCompletedEvent.java        — Consumed: payment succeeded → confirm order
 * │   └── PaymentFailedEvent.java           — Consumed: payment failed → cancel order
 * ├── listener/
 * │   └── OrderEventListener.java           — @KafkaListener for inventory and payment events
 * ├── repository/
 * │   └── OrderRepository.java              — JpaRepository&lt;Order, Long&gt;
 * └── service/
 *     └── OrderService.java                 — Business logic: create order, update status, saga steps
 * </pre>
 *
 * <h3>How to Create the Submodule</h3>
 *
 * <ol>
 *   <li>Create directory {@code order-service/} next to this parent.</li>
 *   <li>Add a {@code pom.xml} that declares this project as its parent.</li>
 *   <li>Add dependencies: {@code spring-boot-starter-web}, {@code spring-boot-starter-data-jpa},
 *       {@code spring-kafka}, H2 (dev), PostgreSQL (docker).</li>
 *   <li>Repeat for {@code inventory-service}, {@code payment-service}, {@code notification-service},
 *       {@code api-gateway}, {@code discovery-server}, {@code config-server}.</li>
 *   <li>Uncomment the {@code <modules>} block in the parent POM.</li>
 * </ol>
 *
 * TODO: scaffold each submodule following the structure above.
 */
public class OrderService {

    private OrderService() {
        // Reference-only class — not meant to be instantiated.
        // See the Javadoc above for the intended module structure.
    }
}
