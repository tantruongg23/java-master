package exercises.payment;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CryptoPayment implements PaymentMethod {
    private String walletAddress;
    private String cryptoCurrency;

    public CryptoPayment(String walletAddress, String cryptoCurrency) {
        this.walletAddress = walletAddress;
        this.cryptoCurrency = cryptoCurrency;
    }

    @Override
    public boolean validate() {
        List<String> errors = new ArrayList<>();
        
        // Check if wallet address is null or blank first
        if (StringUtils.isBlank(walletAddress)) {
            errors.add("Wallet address is required");
            return false; // Early return to avoid NPE on length() and startsWith()
        }
        
        if (walletAddress.length() < 26 || walletAddress.length() > 62) {
            errors.add("Wallet address must be between 26 and 62 characters");
        }

        if (!walletAddress.startsWith("0x") &&
                !walletAddress.startsWith("1") &&
                !walletAddress.startsWith("3") &&
                !walletAddress.startsWith("bc1")) {
            errors.add("Invalid wallet address format");
        }
        
        if (cryptoCurrency == null || !List.of("BTC", "ETH", "USDT").contains(cryptoCurrency)) {
            errors.add("Unsupported cryptocurrency");
        }

        return errors.isEmpty();
    }

    @Override
    public PaymentResult process(BigDecimal amount) {
        if (BigDecimal.ZERO.compareTo(amount) >= 0){
            return PaymentResult.failed(amount, "Invalid totalCharged");
        }

        BigDecimal fee = amount.multiply(BigDecimal.valueOf(0.01))
                .setScale(2, RoundingMode.HALF_UP);
        return PaymentResult.pending("CRYPTO-" + UUID.randomUUID(), amount, fee,
                "Transaction submitted to blockchain - awaiting confirmation");
    }

    @Override
    public String getPaymentType() {
        return getClass().getSimpleName();
    }
}
