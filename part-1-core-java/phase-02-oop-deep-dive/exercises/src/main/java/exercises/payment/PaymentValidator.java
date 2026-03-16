package exercises.payment;

import java.util.List;

/**
 * PaymentValidator — validates payment method data.
 *
 * <p><b>SOLID Principle Demonstration:</b>
 * <ul>
 *   <li><b>SRP:</b> Single responsibility — validation only, separated from processing logic</li>
 *   <li><b>ISP:</b> Interface Segregation — focused interface with only validation methods</li>
 *   <li><b>DIP:</b> Payment methods depend on this abstraction, not concrete validators</li>
 * </ul>
 *
 * <p>This interface allows different validation strategies without modifying payment classes.
 */
public interface PaymentValidator {

    /**
     * Validates the payment method data.
     *
     * @return true if all validation rules pass, false otherwise
     */
    boolean validate();

    /**
     * Validates the payment method data and returns detailed error messages.
     *
     * <p>This method provides more granular feedback than {@link #validate()}.
     *
     * @return List of validation error messages (empty if valid)
     */
    List<String> validateWithErrors();
}
