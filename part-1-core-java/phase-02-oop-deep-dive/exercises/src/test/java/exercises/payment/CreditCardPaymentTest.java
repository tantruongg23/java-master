package exercises.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreditCardPayment Tests")
class CreditCardPaymentTest {

    private static final String VALID_CARD_NUMBER = "4111111111111111"; // Visa test card
    private static final String VALID_CARD_HOLDER = "John Doe";
    private static final Month VALID_MONTH = Month.DECEMBER;
    private static final Year VALID_YEAR = Year.of(2026);
    private static final String VALID_CVV = "123";

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate successfully with all valid data")
        void shouldValidateSuccessfully() {
            // Given
            CreditCardPayment payment = new CreditCardPayment(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_CVV
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertTrue(isValid, "Payment with valid data should be valid");
        }

        @Test
        @DisplayName("Should fail validation when card holder name is blank")
        void shouldFailWhenCardHolderNameIsBlank() {
            // Given
            CreditCardPayment payment = new CreditCardPayment(
                    VALID_CARD_NUMBER, "", VALID_MONTH, VALID_YEAR, VALID_CVV
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when card holder name is blank");
        }

        @Test
        @DisplayName("Should fail validation when card holder name is null")
        void shouldFailWhenCardHolderNameIsNull() {
            // Given
            CreditCardPayment payment = new CreditCardPayment(
                    VALID_CARD_NUMBER, null, VALID_MONTH, VALID_YEAR, VALID_CVV
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when card holder name is null");
        }

        @Test
        @DisplayName("Should fail validation when card number is not 16 digits")
        void shouldFailWhenCardNumberIsNot16Digits() {
            // Given
            CreditCardPayment payment = new CreditCardPayment(
                    "411111111111111", VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_CVV
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when card number is not 16 digits");
        }

        @Test
        @DisplayName("Should fail validation when card number is null")
        void shouldFailWhenCardNumberIsNull() {
            // Given
            CreditCardPayment payment = new CreditCardPayment(
                    null, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_CVV
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when card number is null");
        }

        @Test
        @DisplayName("Should fail validation when card number fails Luhn check")
        void shouldFailWhenCardNumberFailsLuhnCheck() {
            // Given - Invalid card number that's 16 digits but fails Luhn algorithm
            CreditCardPayment payment = new CreditCardPayment(
                    "1234567890123456", VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_CVV
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when card number fails Luhn check");
        }

        @Test
        @DisplayName("Should fail validation when card has expired")
        void shouldFailWhenCardHasExpired() {
            // Given
            CreditCardPayment payment = new CreditCardPayment(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, Month.JANUARY, Year.of(2020), VALID_CVV
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when card has expired");
        }

        @Test
        @DisplayName("Should fail validation when CVV is not 3 digits")
        void shouldFailWhenCvvIsNot3Digits() {
            // Given
            CreditCardPayment payment = new CreditCardPayment(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, "12"
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when CVV is not 3 digits");
        }

        @Test
        @DisplayName("Should fail validation when CVV is null")
        void shouldFailWhenCvvIsNull() {
            // Given
            CreditCardPayment payment = new CreditCardPayment(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, null
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when CVV is null");
        }

        @Test
        @DisplayName("Should fail validation when CVV contains non-digits")
        void shouldFailWhenCvvContainsNonDigits() {
            // Given
            CreditCardPayment payment = new CreditCardPayment(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, "12A"
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when CVV contains non-digits");
        }
    }

    @Nested
    @DisplayName("Processing Tests")
    class ProcessingTests {

        private CreditCardPayment validPayment;

        @BeforeEach
        void setUp() {
            validPayment = new CreditCardPayment(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_CVV
            );
        }

        @Test
        @DisplayName("Should process payment successfully with valid amount")
        void shouldProcessPaymentSuccessfully() {
            // Given
            BigDecimal amount = BigDecimal.valueOf(100);

            // When
            PaymentResult result = validPayment.process(amount);

            // Then
            assertAll("Payment result",
                    () -> assertEquals(PaymentStatus.COMPLETED, result.status(), "Status should be COMPLETED"),
                    () -> assertNotNull(result.transactionId(), "Transaction ID should not be null"),
                    () -> assertTrue(result.transactionId().startsWith("CC-"), "Transaction ID should start with CC-"),
                    () -> assertEquals(amount, result.originalAmount(), "Original amount should match"),
                    () -> assertEquals(BigDecimal.valueOf(2.0), result.fee(), "Fee should be 2% (2.00)"),
                    () -> assertEquals(BigDecimal.valueOf(102.0), result.totalCharged(), "Total should be 102.00")
            );
        }

        @Test
        @DisplayName("Should calculate 2% fee correctly for different amounts")
        void shouldCalculate2PercentFeeCorrectly() {
            // Test cases: amount -> expected fee
            assertAll("Fee calculations",
                    () -> {
                        PaymentResult result = validPayment.process(BigDecimal.valueOf(100));
                        assertEquals(BigDecimal.valueOf(2.0), result.fee(), "100 * 2% = 2.00");
                    },
                    () -> {
                        PaymentResult result = validPayment.process(BigDecimal.valueOf(500));
                        assertEquals(BigDecimal.valueOf(10.0), result.fee(), "500 * 2% = 10.00");
                    },
                    () -> {
                        PaymentResult result = validPayment.process(BigDecimal.valueOf(1000));
                        assertEquals(BigDecimal.valueOf(20.0), result.fee(), "1000 * 2% = 20.00");
                    }
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
                    () -> assertNull(result.transactionId(), "Transaction ID should be null for failed payment"),
                    () -> assertTrue(result.message().contains("between"), "Error message should mention range")
            );
        }

        @Test
        @DisplayName("Should fail when amount is negative")
        void shouldFailWhenAmountIsNegative() {
            // When
            PaymentResult result = validPayment.process(BigDecimal.valueOf(-50));

            // Then
            assertEquals(PaymentStatus.FAILED, result.status(), "Status should be FAILED for negative amount");
        }

        @Test
        @DisplayName("Should fail when amount exceeds $10,000 limit")
        void shouldFailWhenAmountExceedsLimit() {
            // When
            PaymentResult result = validPayment.process(BigDecimal.valueOf(10001));

            // Then
            assertAll("Exceeded limit result",
                    () -> assertEquals(PaymentStatus.FAILED, result.status(), "Status should be FAILED"),
                    () -> assertTrue(result.message().contains("10,000"), "Error message should mention limit")
            );
        }

        @Test
        @DisplayName("Should process payment at exactly $10,000 limit")
        void shouldProcessAtExactLimit() {
            // When
            PaymentResult result = validPayment.process(BigDecimal.valueOf(10000));

            // Then
            assertAll("At limit result",
                    () -> assertEquals(PaymentStatus.COMPLETED, result.status(), "Should process at exact limit"),
                    () -> assertEquals(BigDecimal.valueOf(10000), result.originalAmount()),
                    () -> assertEquals(BigDecimal.valueOf(200.0), result.fee(), "Fee should be 10000 * 2%")
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
            CreditCardPayment payment = new CreditCardPayment(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_CVV
            );

            // When
            String paymentType = payment.getPaymentType();

            // Then
            assertEquals("CreditCardPayment", paymentType, "Payment type should be CreditCardPayment");
        }
    }
}
