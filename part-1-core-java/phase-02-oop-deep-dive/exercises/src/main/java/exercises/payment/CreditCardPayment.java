package exercises.payment;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.CreditCardValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreditCardPayment implements PaymentMethod{
    private String cardNumber;
    private String cardHolderName;
    private Month expirationMonth;
    private Year expirationYear;
    private String cvv;

    public CreditCardPayment(String cardNumber, String cardHolderName, Month expirationMonth, Year expirationYear, String cvv) {
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.cvv = cvv;
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

        if (StringUtils.isBlank(cvv) || !cvv.matches("\\d{3}")) {
            errors.add("CVV must be exactly 3 digits");
        }

        return errors.isEmpty();
    }

    @Override
    public PaymentResult process(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0
            || amount.compareTo(BigDecimal.valueOf(10000)) > 0) {
            return PaymentResult.failed(amount, "Amount must be between $0 and $10,000");
        }

        BigDecimal fee = amount.multiply(BigDecimal.valueOf(0.02))
                .setScale(2, RoundingMode.HALF_UP);
        return PaymentResult.completed("CC-" + UUID.randomUUID(), amount, fee);
    }

    @Override
    public String getPaymentType() {
        return getClass().getSimpleName();
    }
}
