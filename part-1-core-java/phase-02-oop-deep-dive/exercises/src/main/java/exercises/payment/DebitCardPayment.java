package exercises.payment;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.CreditCardValidator;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DebitCardPayment implements PaymentMethod {
    private String cardNumber;
    private String cardHolderName;
    private Month expirationMonth;
    private Year expirationYear;
    private String pin;

    public DebitCardPayment(String cardNumber, String cardHolderName, Month expirationMonth, Year expirationYear, String pin) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.pin = pin;
    }
    @Override
    public boolean validate() {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isBlank(cardHolderName)) {
            errors.add("Card holder name is required");
        }

        if (StringUtils.isBlank(cardNumber) || cardNumber.length() != 16) {
            errors.add("Invalid card number");
        }

        CreditCardValidator validator = new CreditCardValidator();
        if (!validator.isValid(cardNumber)) {
            errors.add("Invalid card number");
        }

        YearMonth expirationDate = YearMonth.of(expirationYear.getValue(), expirationMonth);
        if (expirationDate.isBefore(YearMonth.now())) {
            errors.add("Card has expired");
        }

        if (StringUtils.isBlank(pin) || !pin.matches("\\d{4}")) {
            errors.add("PIN must be exactly 4 digits");
        }

        return errors.isEmpty();
    }

    @Override
    public PaymentResult process(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0
            || amount.compareTo(BigDecimal.valueOf(5000)) > 0) {
            return PaymentResult.failed(amount, "Amount must be between $0 and $5,000");
        }

        BigDecimal fee = BigDecimal.valueOf(0.5);
        return PaymentResult.completed("DC-" + UUID.randomUUID(), amount, fee);
    }

    @Override
    public String getPaymentType() {
        return getClass().getSimpleName();
    }
}
