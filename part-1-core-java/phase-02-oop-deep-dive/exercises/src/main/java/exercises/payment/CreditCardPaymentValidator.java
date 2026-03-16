package exercises.payment;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.CreditCardValidator;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * CreditCardValidator — validates credit card payment data.
 *
 * <p><b>Validation Rules:</b>
 * <ul>
 *   <li>Card number must be 16 digits and pass Luhn algorithm</li>
 *   <li>Card holder name is required</li>
 *   <li>CVV must be exactly 3 digits</li>
 *   <li>Card must not be expired</li>
 * </ul>
 *
 * <p><b>SOLID:</b> Dedicated validator class (SRP) implementing focused interface (ISP).
 */
public class CreditCardPaymentValidator implements PaymentValidator {

    private final String cardNumber;
    private final String cardHolderName;
    private final Month expiryMonth;
    private final Year expiryYear;
    private final String cvv;

    public CreditCardPaymentValidator(String cardNumber, String cardHolderName,
                                      Month expiryMonth, Year expiryYear, String cvv) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.cvv = cvv;
    }

    @Override
    public boolean validate() {
        return validateWithErrors().isEmpty();
    }

    @Override
    public List<String> validateWithErrors() {
        List<String> errors = new ArrayList<>();

        // Card holder name validation
        if (StringUtils.isBlank(cardHolderName)) {
            errors.add("Card holder name is required");
        }

        // Card number validation
        if (StringUtils.isBlank(cardNumber)) {
            errors.add("Card number is required");
        } else if (cardNumber.length() != 16) {
            errors.add("Card number must be exactly 16 digits");
        } else {
            CreditCardValidator validator = new CreditCardValidator();
            if (!validator.isValid(cardNumber)) {
                errors.add("Card number failed Luhn algorithm check");
            }
        }

        // Expiry date validation
        YearMonth expiry = YearMonth.of(expiryYear.getValue(), expiryMonth);
        YearMonth now = YearMonth.now();
        if (expiry.isBefore(now)) {
            errors.add("Card has expired");
        }

        // CVV validation
        if (StringUtils.isBlank(cvv)) {
            errors.add("CVV is required");
        } else if (cvv.length() != 3) {
            errors.add("CVV must be exactly 3 digits");
        } else if (!cvv.matches("\\d{3}")) {
            errors.add("CVV must contain only digits");
        }

        return errors;
    }
}
