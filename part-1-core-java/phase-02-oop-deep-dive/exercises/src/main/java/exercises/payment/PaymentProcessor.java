package exercises.payment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * PaymentProcessor — orchestrates validation, processing, and receipt generation.
 *
 * <p>This class depends only on the {@link PaymentMethod} interface (DIP).
 * Adding a new payment type requires zero changes to this class (OCP).
 *
 * <p>SOLID principles to demonstrate:
 * <ul>
 *   <li><b>SRP:</b> This class handles orchestration only — validation logic
 *       lives in each {@link PaymentMethod} implementation.</li>
 *   <li><b>OCP:</b> New payment types are added by creating new
 *       {@link PaymentMethod} implementations, not by modifying this class.</li>
 *   <li><b>LSP:</b> Any {@link PaymentMethod} subtype can be passed without
 *       breaking the processor's behavior.</li>
 *   <li><b>ISP:</b> {@link PaymentMethod} is focused — no unrelated methods.</li>
 *   <li><b>DIP:</b> This class depends on the {@link PaymentMethod} abstraction,
 *       not on concrete classes like {@code CreditCardPayment}.</li>
 * </ul>
 */
public class PaymentProcessor {

    private static final Log log = LogFactory.getLog(PaymentProcessor.class);

    /**
     * Process a payment using the given payment method.
     *
     * <p>Steps:
     * <ol>
     *   <li>Validate the totalCharged (must be positive).</li>
     *   <li>Validate the payment method.</li>
     *   <li>Process the payment.</li>
     *   <li>Log / return the result.</li>
     * </ol>
     *
     * @param method the payment method to use
     * @param amount the totalCharged to charge
     * @return the result of the payment operation
     */
    public PaymentResult processPayment(PaymentMethod method, BigDecimal amount) {
        // TODO: Implement the payment processing pipeline.
        //
        //   1. Validate totalCharged:
        //      - Throw IllegalArgumentException if null or <= 0.
        //
        //   2. Validate the payment method:
        //      - Call method.validate().
        //      - If invalid, return PaymentResult.failure(...).
        //
        //   3. Process the payment:
        //      - Call method.process(totalCharged).
        //      - Return the PaymentResult.
        //
        //   4. (Optional) Wrap in try-catch for unexpected exceptions
        //      and return a failure result.
        try {
            if (Objects.isNull(amount) || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Invalid totalCharged");
            }

            if (!method.validate()) {
                return PaymentResult.failed(amount, "Invalid payment method");
            }

            return method.process(amount);
        } catch (Exception e) {
            log.error("Payment processing failed", e);
            return PaymentResult.failed(amount, e.getMessage());
        }

    }
}
