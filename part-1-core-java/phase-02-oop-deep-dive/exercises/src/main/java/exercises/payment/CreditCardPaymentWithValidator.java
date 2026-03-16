package exercises.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.time.Year;
import java.util.UUID;

/**
 * CreditCardPayment (Refactored with PaymentValidator)
 *
 * <p>This is an EXAMPLE refactored version showing how to use the PaymentValidator interface.
 * The original CreditCardPayment.java still exists and works with existing tests.
 *
 * <p><b>SOLID Improvements:</b>
 * <ul>
 *   <li><b>SRP:</b> Validation logic moved to CreditCardPaymentValidator</li>
 *   <li><b>DIP:</b> Depends on PaymentValidator interface, not concrete validator</li>
 *   <li><b>ISP:</b> Uses focused validation interface</li>
 *   <li><b>OCP:</b> Can switch validators without modifying this class</li>
 * </ul>
 */
public class CreditCardPaymentWithValidator implements PaymentMethod {

    private final String cardNumber;
    private final String cardHolderName;
    private final Month expiryMonth;
    private final Year expiryYear;
    private final String cvv;
    
    // DIP: Depend on abstraction, not concrete validator
    private final PaymentValidator validator;

    /**
     * Constructor with dependency injection of validator.
     *
     * @param cardNumber Card number
     * @param cardHolderName Card holder name
     * @param expiryMonth Expiry month
     * @param expiryYear Expiry year
     * @param cvv CVV code
     * @param validator Payment validator (injected dependency)
     */
    public CreditCardPaymentWithValidator(String cardNumber, String cardHolderName,
                                          Month expiryMonth, Year expiryYear, String cvv,
                                          PaymentValidator validator) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.cvv = cvv;
        this.validator = validator;
    }

    /**
     * Convenience constructor that creates default validator.
     * This maintains backward compatibility.
     */
    public CreditCardPaymentWithValidator(String cardNumber, String cardHolderName,
                                          Month expiryMonth, Year expiryYear, String cvv) {
        this(cardNumber, cardHolderName, expiryMonth, expiryYear, cvv,
             new CreditCardPaymentValidator(cardNumber, cardHolderName, expiryMonth, expiryYear, cvv));
    }

    @Override
    public boolean validate() {
        // SRP: Delegate to validator
        return validator.validate();
    }

    @Override
    public PaymentResult process(BigDecimal amount) {
        // Business logic validation
        if (amount.compareTo(BigDecimal.ZERO) <= 0
            || amount.compareTo(BigDecimal.valueOf(10000)) > 0) {
            return PaymentResult.failed(amount, "Amount must be between $0 and $10,000");
        }

        // Calculate fee (2%)
        BigDecimal fee = amount.multiply(BigDecimal.valueOf(0.02))
                .setScale(2, RoundingMode.HALF_UP);
        
        return PaymentResult.completed("CC-" + UUID.randomUUID(), amount, fee);
    }

    @Override
    public String getPaymentType() {
        return getClass().getSimpleName();
    }

    /**
     * Additional method to get detailed validation errors.
     * This is enabled by the PaymentValidator interface.
     *
     * @return List of validation error messages
     */
    public java.util.List<String> getValidationErrors() {
        return validator.validateWithErrors();
    }
}
