package exercises.payment;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BankTransferPayment implements PaymentMethod {
    private String bankCode;
    private String accountNumber;
    private String accountHolderName;

    public BankTransferPayment(String bankCode, String accountNumber, String accountHolderName) {
        this.bankCode = bankCode;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
    }

    @Override
    public boolean validate() {
        List<String> errors = new ArrayList<>();
        if (StringUtils.isBlank(accountHolderName)) {
            errors.add("Account holder name is required");
        }

        if (StringUtils.isBlank(bankCode) || !(bankCode.length() == 8 || bankCode.length() == 11)) {
            errors.add("Invalid bank code");
        }

        if (StringUtils.isBlank(accountNumber) || accountNumber.length() < 8 || accountNumber.length() > 20) {
            errors.add("Invalid account number");
        }

        return errors.isEmpty();
    }

    @Override
    public PaymentResult process(BigDecimal amount) {
        if (BigDecimal.ZERO.compareTo(amount) >= 0
            || BigDecimal.valueOf(100000).compareTo(amount) < 0) {
            return PaymentResult.failed(amount, "Amount must be between $0 and $100,000");
        }

        BigDecimal fee = BigDecimal.valueOf(5);
        return PaymentResult.pending("BT-" + UUID.randomUUID(), amount, fee, "Transfer pending - 1-3 business days");
    }

    @Override
    public String getPaymentType() {
        return getClass().getSimpleName();
    }
}
