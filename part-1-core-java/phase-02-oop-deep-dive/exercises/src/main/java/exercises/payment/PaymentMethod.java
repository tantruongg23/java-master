package exercises.payment;

import java.math.BigDecimal;

/**
 * PaymentMethod — defines the contract for all payment methods.
 *
 * <p>This interface follows the Strategy pattern: each implementation
 * encapsulates a different payment algorithm. The {@link PaymentProcessor}
 * works with this abstraction (Dependency Inversion Principle).
 *
 * <p>TODO: Create the following implementations in separate files:
 * <ul>
 *   <li>{@code CreditCardPayment} — validates card number (Luhn), expiry, CVV</li>
 *   <li>{@code DebitCardPayment} — validates card number, PIN, sufficient balance</li>
 *   <li>{@code PayPalPayment} — validates email format and account status</li>
 *   <li>{@code BankTransferPayment} — validates IBAN format and routing number</li>
 *   <li>(Bonus) {@code CryptoPayment} — validates wallet address format</li>
 * </ul>
 */
public interface PaymentMethod {

    /**
     * Validate that the payment details are correct and sufficient.
     *
     * @return {@code true} if the payment method is valid and ready to process
     */
    boolean validate();

    /**
     * Execute the payment for the given amount.
     *
     * @param amount the amount to charge (must be positive)
     * @return a {@link PaymentResult} indicating success or failure with details
     * @throws IllegalArgumentException if amount is null or non-positive
     * @throws IllegalStateException    if {@link #validate()} has not been called or returned false
     */
    PaymentResult process(BigDecimal amount);

    /**
     * @return a human-readable name for this payment type (e.g., "Credit Card", "PayPal")
     */
    String getPaymentType();
}
