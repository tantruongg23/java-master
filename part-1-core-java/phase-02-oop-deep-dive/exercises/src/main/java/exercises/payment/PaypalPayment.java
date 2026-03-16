package exercises.payment;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PaypalPayment implements PaymentMethod {
    private String email;
    private String authToken;

    public PaypalPayment(String email, String authToken) {
        this.email = email;
        this.authToken = authToken;
    }

    @Override
    public boolean validate() {
        List<String> errors = new ArrayList<>();
        EmailValidator validator = EmailValidator.getInstance();
        if (!validator.isValid(email)) {
            errors.add("Invalid email address");
        }

        if (StringUtils.isBlank(authToken) || authToken.length() <= 10) {
            errors.add("Invalid auth token");
        }

        return errors.isEmpty();
    }

    @Override
    public PaymentResult process(BigDecimal amount) {
        if (BigDecimal.ZERO.compareTo(amount) >= 0
                || BigDecimal.valueOf(25000).compareTo(amount) < 0) {
            return PaymentResult.failed(amount, "Invalid totalCharged");
        }

        BigDecimal fee = amount.multiply(BigDecimal.valueOf(0.029))
                .add(BigDecimal.valueOf(0.30))
                .setScale(2, RoundingMode.HALF_UP);
        return PaymentResult.completed("PP-" + UUID.randomUUID(), amount, fee);
    }

    @Override
    public String getPaymentType() {
        return getClass().getSimpleName();
    }
}
