package exercises.payment;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * CryptoPaymentValidator — validates cryptocurrency payment data.
 *
 * <p><b>Validation Rules:</b>
 * <ul>
 *   <li>Wallet address: 26-62 characters</li>
 *   <li>Must start with valid prefix: 0x (Ethereum), 1/3 (Bitcoin), bc1 (SegWit)</li>
 *   <li>Cryptocurrency must be in whitelist: BTC, ETH, USDT</li>
 * </ul>
 *
 * <p><b>SOLID:</b> Dedicated validator class (SRP) implementing focused interface (ISP).
 */
public class CryptoPaymentValidator implements PaymentValidator {

    private static final List<String> SUPPORTED_CURRENCIES = List.of("BTC", "ETH", "USDT");
    private static final int MIN_ADDRESS_LENGTH = 26;
    private static final int MAX_ADDRESS_LENGTH = 62;

    private final String walletAddress;
    private final String cryptoCurrency;

    public CryptoPaymentValidator(String walletAddress, String cryptoCurrency) {
        this.walletAddress = walletAddress;
        this.cryptoCurrency = cryptoCurrency;
    }

    @Override
    public boolean validate() {
        return validateWithErrors().isEmpty();
    }

    @Override
    public List<String> validateWithErrors() {
        List<String> errors = new ArrayList<>();

        // Wallet address validation
        if (StringUtils.isBlank(walletAddress)) {
            errors.add("Wallet address is required");
            return errors; // Early return to avoid NPE on further checks
        }

        if (walletAddress.length() < MIN_ADDRESS_LENGTH) {
            errors.add("Wallet address must be at least " + MIN_ADDRESS_LENGTH + " characters");
        }

        if (walletAddress.length() > MAX_ADDRESS_LENGTH) {
            errors.add("Wallet address must not exceed " + MAX_ADDRESS_LENGTH + " characters");
        }

        if (!hasValidPrefix(walletAddress)) {
            errors.add("Invalid wallet address prefix (must start with 0x, 1, 3, or bc1)");
        }

        // Cryptocurrency validation
        if (cryptoCurrency == null || !SUPPORTED_CURRENCIES.contains(cryptoCurrency)) {
            errors.add("Unsupported cryptocurrency (supported: " + SUPPORTED_CURRENCIES + ")");
        }

        return errors;
    }

    private boolean hasValidPrefix(String address) {
        return address.startsWith("0x")   // Ethereum
            || address.startsWith("1")    // Bitcoin (legacy)
            || address.startsWith("3")    // Bitcoin (P2SH)
            || address.startsWith("bc1"); // Bitcoin (SegWit)
    }
}
