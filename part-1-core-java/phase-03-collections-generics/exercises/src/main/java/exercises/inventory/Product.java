package exercises.inventory;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Product — represents an item in the retail inventory.
 *
 * <p>Natural ordering: by price ascending, then by name alphabetically.
 *
 * <p>TODO: After completing this class, create an {@code InventoryManager} class that uses:
 * <ul>
 *   <li>{@code HashMap<String, Product>} for O(1) lookup by SKU</li>
 *   <li>{@code TreeMap<BigDecimal, List<Product>>} for sorted price browsing</li>
 *   <li>{@code PriorityQueue<Product>} for low-stock alerts (min-heap by stockQuantity)</li>
 * </ul>
 */
public class Product implements Comparable<Product> {

    private final String sku;
    private final String name;
    private final String category;
    private final BigDecimal price;
    private int stockQuantity;

    public Product(String sku, String name, String category, BigDecimal price, int stockQuantity) {
        this.sku = Objects.requireNonNull(sku, "sku must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.category = Objects.requireNonNull(category, "category must not be null");
        this.price = Objects.requireNonNull(price, "price must not be null");
        this.stockQuantity = stockQuantity;
    }

    /**
     * Compare products by price ascending, then by name alphabetically.
     *
     * @param other the product to compare to
     * @return a negative, zero, or positive integer
     */
    @Override
    public int compareTo(Product other) {
        // TODO: 1. Compare by price using BigDecimal.compareTo().
        //       2. If prices are equal, compare by name using String.compareTo().

        throw new UnsupportedOperationException("TODO: implement compareTo()");
    }

    public String getSku() { return sku; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product other)) return false;
        return Objects.equals(sku, other.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }

    @Override
    public String toString() {
        return "Product{" +
                "sku='" + sku + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", stock=" + stockQuantity +
                '}';
    }
}
