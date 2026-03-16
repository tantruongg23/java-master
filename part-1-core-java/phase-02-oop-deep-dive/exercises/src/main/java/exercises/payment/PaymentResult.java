package exercises.payment;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Immutable record capturing the result of a payment operation.
 *
 * @param success       whether the payment was processed successfully
 * @param transactionId a unique identifier for the transaction (null if failed)
 * @param totalCharged        the totalCharged that was charged
 * @param message       a human-readable status message
 * @param timestamp     when the payment was processed
 */
public record PaymentResult(
        PaymentStatus status,
        String transactionId,
        BigDecimal originalAmount,
        BigDecimal fee,
        BigDecimal totalCharged,
        String message,
        Instant timestamp
) {

    public static PaymentResult completed(String txId, BigDecimal amount, BigDecimal fee) {
        return new PaymentResult(
                PaymentStatus.COMPLETED, txId, amount, fee,
                amount.add(fee), "Payment completed", Instant.now()
        );
    }

    public static PaymentResult pending(String txId, BigDecimal amount, BigDecimal fee, String message) {
        return new PaymentResult(
                PaymentStatus.PENDING, txId, amount, fee,
                amount.add(fee), message, Instant.now()
        );
    }

    public static PaymentResult failed(BigDecimal amount, String reason) {
        return new PaymentResult(
                PaymentStatus.FAILED, null, amount, BigDecimal.ZERO,
                amount, reason, Instant.now()
        );
    }
}
