package exercises.repository;

import exercises.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Product} entities.
 *
 * <p>Extends:
 * <ul>
 *   <li>{@link JpaRepository} — standard CRUD + batch + flush</li>
 *   <li>{@link JpaSpecificationExecutor} — dynamic queries via {@code Specification<Product>}</li>
 * </ul>
 *
 * <p><b>Exercise 3 — Inventory System with Optimistic Locking</b></p>
 *
 * <p><b>TODO:</b>
 * <ol>
 *   <li>Add derived queries as needed for reporting (Exercise 2).</li>
 *   <li>Add {@code @EntityGraph} methods to avoid N+1 if relationships are added later.</li>
 *   <li>(Bonus) Add a pessimistic locking query for comparison with optimistic locking.</li>
 * </ol>
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // ── Derived queries ──────────────────────────────────────────────

    /** Find a product by its unique SKU code. */
    Optional<Product> findBySku(String sku);

    /** Find all products whose name contains the given text (case-insensitive). */
    List<Product> findByNameContainingIgnoreCase(String name);

    /** Find products with stock below a given threshold, ordered ascending. */
    List<Product> findByStockQuantityLessThanOrderByStockQuantityAsc(int threshold);

    /** Find all products within a price range, paginated. */
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    // ── JPQL queries ─────────────────────────────────────────────────

    /**
     * Returns products that are low on stock (below the given threshold).
     * Ordered by stock quantity ascending so the most critical appear first.
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity < :threshold ORDER BY p.stockQuantity ASC")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);

    /**
     * Calculates the total inventory value (SUM of price * stockQuantity).
     *
     * TODO: Implement this query.
     * Hint: {@code SELECT SUM(p.price * p.stockQuantity) FROM Product p}
     */
    @Query("SELECT COALESCE(SUM(p.price * p.stockQuantity), 0) FROM Product p")
    BigDecimal calculateTotalInventoryValue();

    /**
     * TODO: Add a native query for a complex report, e.g.:
     *
     * <pre>{@code
     * @Query(value = """
     *     SELECT p.sku, p.name, p.stock_quantity, p.price,
     *            (p.price * p.stock_quantity) AS inventory_value
     *     FROM products p
     *     ORDER BY inventory_value DESC
     *     LIMIT :limit
     *     """, nativeQuery = true)
     * List<Object[]> findTopProductsByInventoryValue(@Param("limit") int limit);
     * }</pre>
     */

    // ── Pessimistic locking (Bonus) ──────────────────────────────────

    /**
     * Finds a product by ID with a pessimistic write lock.
     *
     * <p>Use this for comparison with optimistic locking in Exercise 3 bonus.
     * The database row is locked until the transaction commits, preventing
     * any concurrent modification.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithPessimisticLock(@Param("id") Long id);
}
