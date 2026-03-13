package exercises.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Inventory product with optimistic locking support.
 *
 * <p>The {@link #version} field is managed by JPA. On every
 * {@code UPDATE}, Hibernate includes a {@code WHERE version = ?}
 * clause. If another transaction has already modified the row,
 * an {@code OptimisticLockException} is thrown.
 *
 * <p><b>Exercise 3 — Inventory System with Optimistic Locking</b></p>
 *
 * <p><b>TODO:</b>
 * <ol>
 *   <li>Implement {@link #addStock(int)} — increases {@code stockQuantity}.</li>
 *   <li>Implement {@link #reserveStock(int)} — decreases stock, throws
 *       {@code IllegalArgumentException} if insufficient.</li>
 *   <li>Implement {@link #releaseStock(int)} — returns reserved stock.</li>
 *   <li>Create a {@code StockService} with retry logic for
 *       {@code OptimisticLockException} (max 3 attempts).</li>
 *   <li>Publish a {@code StockChangedEvent} (Spring application event)
 *       on every successful stock change.</li>
 * </ol>
 */
@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String name;

    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Min(0)
    @Column(nullable = false)
    private int stockQuantity = 0;

    /**
     * Optimistic lock version. JPA auto-increments this on every update.
     * Do <b>not</b> set this manually.
     */
    @Version
    private Long version;

    @CreatedDate
    @Column(updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    // ── Constructors ─────────────────────────────────────────────────

    protected Product() {}

    public Product(String name, String sku, BigDecimal price, int initialStock) {
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.stockQuantity = initialStock;
    }

    // ── Stock Operations (TODO) ──────────────────────────────────────

    /**
     * Increases stock by the given quantity.
     *
     * @param quantity positive number of units to add
     * @throws IllegalArgumentException if quantity is not positive
     *
     * TODO: Implement this method.
     * <pre>{@code
     * if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
     * this.stockQuantity += quantity;
     * }</pre>
     */
    public void addStock(int quantity) {
        // TODO: implement
        throw new UnsupportedOperationException("Implement addStock");
    }

    /**
     * Reserves (decreases) stock by the given quantity.
     *
     * @param quantity positive number of units to reserve
     * @throws IllegalArgumentException if quantity is not positive or exceeds available stock
     *
     * TODO: Implement this method.
     * <pre>{@code
     * if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
     * if (quantity > this.stockQuantity) throw new IllegalArgumentException("Insufficient stock");
     * this.stockQuantity -= quantity;
     * }</pre>
     */
    public void reserveStock(int quantity) {
        // TODO: implement
        throw new UnsupportedOperationException("Implement reserveStock");
    }

    /**
     * Releases previously reserved stock.
     *
     * @param quantity positive number of units to release back
     * @throws IllegalArgumentException if quantity is not positive
     *
     * TODO: Implement this method.
     */
    public void releaseStock(int quantity) {
        // TODO: implement
        throw new UnsupportedOperationException("Implement releaseStock");
    }

    // ── Getters & Setters ────────────────────────────────────────────

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public Long getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Product{id=%d, sku='%s', name='%s', stock=%d, version=%d}"
                .formatted(id, sku, name, stockQuantity, version);
    }
}
