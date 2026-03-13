package exercises;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Immutable transaction record used across the functional-programming exercises.
 *
 * @param id       unique transaction identifier
 * @param date     date the transaction occurred
 * @param amount   monetary amount (positive)
 * @param category product category (e.g. "Electronics", "Books")
 * @param customer customer name or identifier
 * @param region   geographic region (e.g. "NORTH", "SOUTH")
 */
public record Transaction(
        String id,
        LocalDate date,
        BigDecimal amount,
        String category,
        String customer,
        String region
) {

    /**
     * Compact constructor — validates invariants.
     */
    public Transaction {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("id must not be blank");
        if (date == null) throw new IllegalArgumentException("date must not be null");
        if (amount == null || amount.signum() < 0) throw new IllegalArgumentException("amount must be non-negative");
        if (category == null || category.isBlank()) throw new IllegalArgumentException("category must not be blank");
        if (customer == null || customer.isBlank()) throw new IllegalArgumentException("customer must not be blank");
        if (region == null || region.isBlank()) throw new IllegalArgumentException("region must not be blank");
    }
}
