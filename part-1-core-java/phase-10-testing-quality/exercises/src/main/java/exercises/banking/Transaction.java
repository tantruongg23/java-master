package exercises.banking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * An immutable record of a single bank account transaction.
 *
 * @param id          unique transaction identifier
 * @param type        the kind of transaction
 * @param amount      the monetary value (always positive)
 * @param timestamp   when the transaction occurred
 * @param description human-readable description
 */
public record Transaction(
        String id,
        TransactionType type,
        BigDecimal amount,
        LocalDateTime timestamp,
        String description
) {

    public Transaction {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Transaction id must not be blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("Transaction type must not be null");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp must not be null");
        }
    }
}
