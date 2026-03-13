package exercises.payment;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Immutable record capturing the result of a payment operation.
 *
 * @param success       whether the payment was processed successfully
 * @param transactionId a unique identifier for the transaction (null if failed)
 * @param amount        the amount that was charged
 * @param message       a human-readable status message
 * @param timestamp     when the payment was processed
 */
public record PaymentResult(
        boolean success,
        String transactionId,
        BigDecimal amount,
        String message,
        Instant timestamp
) {

    public static PaymentResult success(String transactionId, BigDecimal amount) {
        return new PaymentResult(true, transactionId, amount, "Payment processed successfully", Instant.now());
    }

    public static PaymentResult failure(BigDecimal amount, String reason) {
        return new PaymentResult(false, null, amount, reason, Instant.now());
    }
}
