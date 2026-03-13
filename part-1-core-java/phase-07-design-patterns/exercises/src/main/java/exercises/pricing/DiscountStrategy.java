package exercises.pricing;

import java.math.BigDecimal;

/**
 * Strategy interface for calculating discounts.
 *
 * <p>Implementations encapsulate a single discount algorithm.
 * Multiple strategies can be composed via the Decorator or
 * Composite patterns.</p>
 *
 * <h3>Implementations to create</h3>
 * <ul>
 *   <li>{@code PercentageDiscount} — reduce price by a percentage (e.g. 10 %)</li>
 *   <li>{@code FixedDiscount} — subtract a fixed amount (e.g. $5)</li>
 *   <li>{@code BuyNGetMFree} — buy N items, get M free (adjusts unit price)</li>
 * </ul>
 *
 * <h3>Decorators to create</h3>
 * <ul>
 *   <li>{@code SeasonalBonus} — adds an extra percentage during promotional periods</li>
 *   <li>{@code LoyaltyMultiplier} — multiplies discount for loyal customers</li>
 *   <li>{@code MinPurchaseGuard} — only applies the wrapped discount if the price
 *       exceeds a minimum threshold</li>
 * </ul>
 *
 * <h3>Composite</h3>
 * <ul>
 *   <li>{@code CombinedDiscount} — stacks multiple strategies, applying them
 *       sequentially to the running total</li>
 * </ul>
 *
 * @see PricingEngine
 */
@FunctionalInterface
public interface DiscountStrategy {

    /**
     * Apply this discount to the given price.
     *
     * @param price the original (or running) price; never negative
     * @return the discounted price; must be ≥ {@link BigDecimal#ZERO}
     */
    BigDecimal apply(BigDecimal price);
}
