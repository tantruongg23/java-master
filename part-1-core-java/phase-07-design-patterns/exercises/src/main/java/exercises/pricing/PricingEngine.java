package exercises.pricing;

import java.math.BigDecimal;
import java.util.*;

/**
 * Pricing engine that evaluates and applies {@link DiscountStrategy} instances
 * to a cart total.
 *
 * <p>The engine accepts a list of strategies and applies them in order.
 * Strategies may be simple, decorated, or composite.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * PricingEngine engine = new PricingEngine();
 * engine.addDiscount(new PercentageDiscount(10));
 * engine.addDiscount(new MinPurchaseGuard(BigDecimal.valueOf(50),
 *                        new FixedDiscount(BigDecimal.valueOf(5))));
 *
 * BigDecimal finalPrice = engine.calculate(originalPrice);
 * }</pre>
 *
 * @see DiscountStrategy
 */
public class PricingEngine {

    private final List<DiscountStrategy> discounts = new ArrayList<>();

    /**
     * Add a discount strategy to the engine.
     *
     * @param strategy the discount to register
     */
    public void addDiscount(DiscountStrategy strategy) {
        discounts.add(Objects.requireNonNull(strategy));
    }

    /**
     * Calculate the final price by applying all registered discounts
     * sequentially to the original price.
     *
     * <p>TODO:</p>
     * <ol>
     *   <li>Start with the original price.</li>
     *   <li>For each registered {@link DiscountStrategy}, apply it to the
     *       running total.</li>
     *   <li>Ensure the final price never drops below zero.</li>
     *   <li>Return the final discounted price.</li>
     * </ol>
     *
     * @param originalPrice the pre-discount price
     * @return the price after all applicable discounts
     * @throws IllegalArgumentException if originalPrice is negative
     */
    public BigDecimal calculate(BigDecimal originalPrice) {
        // TODO: implement sequential discount application
        throw new UnsupportedOperationException("TODO: implement calculate");
    }

    /**
     * Return an unmodifiable view of the registered discounts.
     *
     * @return list of discount strategies
     */
    public List<DiscountStrategy> getDiscounts() {
        return Collections.unmodifiableList(discounts);
    }

    // TODO: Implement concrete DiscountStrategy classes (can be inner or separate files):
    //
    //   public class PercentageDiscount implements DiscountStrategy { ... }
    //   public class FixedDiscount       implements DiscountStrategy { ... }
    //   public class BuyNGetMFree        implements DiscountStrategy { ... }
    //
    // TODO: Implement decorators:
    //
    //   public abstract class DiscountDecorator implements DiscountStrategy {
    //       protected final DiscountStrategy wrapped;
    //       ...
    //   }
    //   public class SeasonalBonus      extends DiscountDecorator { ... }
    //   public class LoyaltyMultiplier  extends DiscountDecorator { ... }
    //   public class MinPurchaseGuard   extends DiscountDecorator { ... }
    //
    // TODO: Implement composite:
    //
    //   public class CombinedDiscount implements DiscountStrategy {
    //       private final List<DiscountStrategy> children;
    //       // apply() iterates children and applies each in order
    //   }
    //
    // Bonus: Implement DiscountRuleChain (Chain of Responsibility)
    //   that selects which discounts are applicable based on:
    //   - cart contents
    //   - customer tier
    //   - current date / promotional period
}
