package exercises.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CryptoPayment Tests")
class CryptoPaymentTest {

    private static final String VALID_ETH_ADDRESS = "0x1234567890123456789012345678901234567890"; // 42 chars
    private static final String VALID_BTC_ADDRESS = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa"; // 34 chars
    private static final String VALID_BTC_ADDRESS_3 = "3J98t1WpEZ73CNmYviecrnyiWrnqRhWNLy"; // 34 chars
    private static final String VALID_BTC_SEGWIT = "bc1qar0srrr7xfkvy5l643lydnw9re59gtzzwf5mdq"; // 42 chars

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate successfully with Ethereum address (0x prefix)")
        void shouldValidateWithEthereumAddress() {
            // Given
            CryptoPayment payment = new CryptoPayment(VALID_ETH_ADDRESS, "ETH");

            // When
            boolean isValid = payment.validate();

            // Then
            assertTrue(isValid, "Payment should be valid with Ethereum address");
        }

        @Test
        @DisplayName("Should validate successfully with Bitcoin address (1 prefix)")
        void shouldValidateWithBitcoinAddress1() {
            // Given
            CryptoPayment payment = new CryptoPayment(VALID_BTC_ADDRESS, "BTC");

            // When
            boolean isValid = payment.validate();

            // Then
            assertTrue(isValid, "Payment should be valid with Bitcoin address starting with 1");
        }

        @Test
        @DisplayName("Should validate successfully with Bitcoin address (3 prefix)")
        void shouldValidateWithBitcoinAddress3() {
            // Given
            CryptoPayment payment = new CryptoPayment(VALID_BTC_ADDRESS_3, "BTC");

            // When
            boolean isValid = payment.validate();

            // Then
            assertTrue(isValid, "Payment should be valid with Bitcoin address starting with 3");
        }

        @Test
        @DisplayName("Should validate successfully with Bitcoin SegWit address (bc1 prefix)")
        void shouldValidateWithBitcoinSegWit() {
            // Given
            CryptoPayment payment = new CryptoPayment(VALID_BTC_SEGWIT, "BTC");

            // When
            boolean isValid = payment.validate();

            // Then
            assertTrue(isValid, "Payment should be valid with Bitcoin SegWit address");
        }

        @Test
        @DisplayName("Should validate successfully with BTC cryptocurrency")
        void shouldValidateWithBTC() {
            // Given
            CryptoPayment payment = new CryptoPayment(VALID_BTC_ADDRESS, "BTC");

            // When
            boolean isValid = payment.validate();

            // Then
            assertTrue(isValid, "Payment should be valid with BTC");
        }

        @Test
        @DisplayName("Should validate successfully with ETH cryptocurrency")
        void shouldValidateWithETH() {
            // Given
            CryptoPayment payment = new CryptoPayment(VALID_ETH_ADDRESS, "ETH");

            // When
            boolean isValid = payment.validate();

            // Then
            assertTrue(isValid, "Payment should be valid with ETH");
        }

        @Test
        @DisplayName("Should validate successfully with USDT cryptocurrency")
        void shouldValidateWithUSDT() {
            // Given
            CryptoPayment payment = new CryptoPayment(VALID_ETH_ADDRESS, "USDT");

            // When
            boolean isValid = payment.validate();

            // Then
            assertTrue(isValid, "Payment should be valid with USDT");
        }

        @Test
        @DisplayName("Should fail validation when wallet address is too short (< 26 chars)")
        void shouldFailWhenWalletAddressTooShort() {
            // Given
            CryptoPayment payment = new CryptoPayment("0x12345678901234567890123", "ETH"); // 25 chars

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when wallet address < 26 chars");
        }

        @Test
        @DisplayName("Should fail validation when wallet address is too long (> 62 chars)")
        void shouldFailWhenWalletAddressTooLong() {
            // Given
            String longAddress = "0x" + "1".repeat(62); // 64 chars total
            CryptoPayment payment = new CryptoPayment(longAddress, "ETH");

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when wallet address > 62 chars");
        }

        @Test
        @DisplayName("Should fail validation when wallet address is null")
        void shouldFailWhenWalletAddressIsNull() {
            // Given
            CryptoPayment payment = new CryptoPayment(null, "BTC");

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when wallet address is null");
        }

        @Test
        @DisplayName("Should fail validation when wallet address is blank")
        void shouldFailWhenWalletAddressIsBlank() {
            // Given
            CryptoPayment payment = new CryptoPayment("", "BTC");

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when wallet address is blank");
        }

        @Test
        @DisplayName("Should fail validation when wallet address has invalid prefix")
        void shouldFailWhenWalletAddressHasInvalidPrefix() {
            // Given - Address with valid length but invalid prefix
            CryptoPayment payment = new CryptoPayment("9x1234567890123456789012345678901234567890", "BTC");

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when wallet address has invalid prefix");
        }

        @Test
        @DisplayName("Should fail validation when cryptocurrency is not supported")
        void shouldFailWhenCryptocurrencyNotSupported() {
            // Given
            CryptoPayment payment = new CryptoPayment(VALID_BTC_ADDRESS, "DOGE");

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when cryptocurrency is not in whitelist");
        }

        @Test
        @DisplayName("Should fail validation when cryptocurrency is null")
        void shouldFailWhenCryptocurrencyIsNull() {
            // Given
            CryptoPayment payment = new CryptoPayment(VALID_BTC_ADDRESS, null);

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when cryptocurrency is null");
        }

        @Test
        @DisplayName("Should fail validation when cryptocurrency is lowercase")
        void shouldFailWhenCryptocurrencyIsLowercase() {
            // Given
            CryptoPayment payment = new CryptoPayment(VALID_BTC_ADDRESS, "btc");

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when cryptocurrency is lowercase (case-sensitive check)");
        }
    }

    @Nested
    @DisplayName("Processing Tests")
    class ProcessingTests {

        private CryptoPayment validPayment;

        @BeforeEach
        void setUp() {
            validPayment = new CryptoPayment(VALID_ETH_ADDRESS, "ETH");
        }

        @Test
        @DisplayName("Should return PENDING status for crypto payments")
        void shouldReturnPendingStatus() {
            // Given
            BigDecimal amount = BigDecimal.valueOf(1000);

            // When
            PaymentResult result = validPayment.process(amount);

            // Then
            assertAll("Payment result",
                    () -> assertEquals(PaymentStatus.PENDING, result.status(), 
                            "Status should be PENDING for crypto payments"),
                    () -> assertNotNull(result.transactionId(), "Transaction ID should not be null"),
                    () -> assertTrue(result.transactionId().startsWith("CRYPTO-"), 
                            "Transaction ID should start with CRYPTO-"),
                    () -> assertTrue(result.message().contains("blockchain") || result.message().contains("confirmation"),
                            "Message should indicate blockchain confirmation needed")
            );
        }

        @Test
        @DisplayName("Should charge 1% network fee")
        void shouldCharge1PercentNetworkFee() {
            // Test cases: amount -> expected fee
            assertAll("1% network fee calculations",
                    () -> {
                        PaymentResult result = validPayment.process(BigDecimal.valueOf(100));
                        assertEquals(BigDecimal.valueOf(1.0), result.fee(), "Fee should be 1% of $100 = $1");
                        assertEquals(BigDecimal.valueOf(101.0), result.totalCharged(), "Total should be $101");
                    },
                    () -> {
                        PaymentResult result = validPayment.process(BigDecimal.valueOf(1000));
                        assertEquals(BigDecimal.valueOf(10.0), result.fee(), "Fee should be 1% of $1000 = $10");
                        assertEquals(BigDecimal.valueOf(1010.0), result.totalCharged(), "Total should be $1010");
                    },
                    () -> {
                        PaymentResult result = validPayment.process(BigDecimal.valueOf(5000));
                        assertEquals(BigDecimal.valueOf(50.0), result.fee(), "Fee should be 1% of $5000 = $50");
                        assertEquals(BigDecimal.valueOf(5050.0), result.totalCharged(), "Total should be $5050");
                    }
            );
        }

        @Test
        @DisplayName("Should process very large amounts (no upper limit)")
        void shouldProcessVeryLargeAmounts() {
            // Given - Crypto has no upper limit
            BigDecimal largeAmount = BigDecimal.valueOf(1_000_000);

            // When
            PaymentResult result = validPayment.process(largeAmount);

            // Then
            assertAll("Large amount processing",
                    () -> assertEquals(PaymentStatus.PENDING, result.status(), 
                            "Should process large amounts successfully"),
                    () -> assertEquals(largeAmount, result.originalAmount()),
                    () -> assertEquals(BigDecimal.valueOf(10_000.0), result.fee(), 
                            "Fee should be 1% of $1,000,000 = $10,000")
            );
        }

        @Test
        @DisplayName("Should fail when amount is zero")
        void shouldFailWhenAmountIsZero() {
            // When
            PaymentResult result = validPayment.process(BigDecimal.ZERO);

            // Then
            assertAll("Failed payment result",
                    () -> assertEquals(PaymentStatus.FAILED, result.status(), "Status should be FAILED"),
                    () -> assertNull(result.transactionId(), "Transaction ID should be null for failed payment")
            );
        }

        @Test
        @DisplayName("Should fail when amount is negative")
        void shouldFailWhenAmountIsNegative() {
            // When
            PaymentResult result = validPayment.process(BigDecimal.valueOf(-100));

            // Then
            assertEquals(PaymentStatus.FAILED, result.status(), "Status should be FAILED for negative amount");
        }

        @Test
        @DisplayName("Should handle small amounts correctly")
        void shouldHandleSmallAmountsCorrectly() {
            // Given - Very small amount
            BigDecimal smallAmount = BigDecimal.valueOf(0.01);

            // When
            PaymentResult result = validPayment.process(smallAmount);

            // Then
            assertAll("Small amount",
                    () -> assertEquals(PaymentStatus.PENDING, result.status(), 
                            "Should process small amounts"),
                    () -> assertEquals(new BigDecimal("0.0001"), result.fee(), 
                            "Fee should be 1% of $0.01 = $0.0001")
            );
        }
    }

    @Nested
    @DisplayName("Payment Type Tests")
    class PaymentTypeTests {

        @Test
        @DisplayName("Should return correct payment type name")
        void shouldReturnCorrectPaymentType() {
            // Given
            CryptoPayment payment = new CryptoPayment(VALID_ETH_ADDRESS, "ETH");

            // When
            String paymentType = payment.getPaymentType();

            // Then
            assertEquals("CryptoPayment", paymentType, "Payment type should be CryptoPayment");
        }
    }
}
