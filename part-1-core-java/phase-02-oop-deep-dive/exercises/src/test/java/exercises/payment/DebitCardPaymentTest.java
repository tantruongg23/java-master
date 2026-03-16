package exercises.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DebitCardPayment Tests")
class DebitCardPaymentTest {

    private static final String VALID_CARD_NUMBER = "4111111111111111";
    private static final String VALID_CARD_HOLDER = "Jane Smith";
    private static final Month VALID_MONTH = Month.JUNE;
    private static final Year VALID_YEAR = Year.of(2027);
    private static final String VALID_PIN = "1234";

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate successfully with all valid data")
        void shouldValidateSuccessfully() {
            // Given
            DebitCardPayment payment = new DebitCardPayment(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_PIN
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
            DebitCardPayment payment = new DebitCardPayment(
                    VALID_CARD_NUMBER, "", VALID_MONTH, VALID_YEAR, VALID_PIN
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when card holder name is blank");
        }

        @Test
        @DisplayName("Should fail validation when card number is not 16 digits")
        void shouldFailWhenCardNumberIsNot16Digits() {
            // Given
            DebitCardPayment payment = new DebitCardPayment(
                    "411111111111111", VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_PIN
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
            DebitCardPayment payment = new DebitCardPayment(
                    null, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_PIN
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when card number is null");
        }

        @Test
        @DisplayName("Should fail validation when card has expired")
        void shouldFailWhenCardHasExpired() {
            // Given
            DebitCardPayment payment = new DebitCardPayment(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, Month.JANUARY, Year.of(2020), VALID_PIN
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when card has expired");
        }

        @Test
        @DisplayName("Should fail validation when PIN is not 4 digits")
        void shouldFailWhenPinIsNot4Digits() {
            // Given
            DebitCardPayment payment = new DebitCardPayment(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, "123"
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when PIN is not 4 digits");
        }

        @Test
        @DisplayName("Should fail validation when PIN is null")
        void shouldFailWhenPinIsNull() {
            // Given
            DebitCardPayment payment = new DebitCardPayment(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, null
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when PIN is null");
        }

        @Test
        @DisplayName("Should fail validation when PIN contains non-digits")
        void shouldFailWhenPinContainsNonDigits() {
            // Given
            DebitCardPayment payment = new DebitCardPayment(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, "12AB"
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when PIN contains non-digits");
        }

        @Test
        @DisplayName("Should fail validation when PIN has spaces")
        void shouldFailWhenPinHasSpaces() {
            // Given
            DebitCardPayment payment = new DebitCardPayment(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, "12 34"
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when PIN has spaces");
        }
    }

    @Nested
    @DisplayName("Processing Tests")
    class ProcessingTests {

        private DebitCardPayment validPayment;

        @BeforeEach
        void setUp() {
            validPayment = new DebitCardPayment(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_PIN
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
                    () -> assertTrue(result.transactionId().startsWith("DC-"), "Transaction ID should start with DC-"),
                    () -> assertEquals(amount, result.originalAmount(), "Original amount should match"),
                    () -> assertEquals(BigDecimal.valueOf(0.5), result.fee(), "Fee should be flat $0.50"),
                    () -> assertEquals(BigDecimal.valueOf(100.5), result.totalCharged(), "Total should be 100.50")
            );
        }

        @Test
        @DisplayName("Should charge flat $0.50 fee regardless of amount")
        void shouldChargeFlatFeeRegardlessOfAmount() {
            // Test cases: different amounts should all have $0.50 fee
            assertAll("Flat fee for different amounts",
                    () -> {
                        PaymentResult result = validPayment.process(BigDecimal.valueOf(10));
                        assertEquals(BigDecimal.valueOf(0.5), result.fee(), "Fee should be $0.50 for $10");
                        assertEquals(BigDecimal.valueOf(10.5), result.totalCharged(), "Total should be $10.50");
                    },
                    () -> {
                        PaymentResult result = validPayment.process(BigDecimal.valueOf(100));
                        assertEquals(BigDecimal.valueOf(0.5), result.fee(), "Fee should be $0.50 for $100");
                        assertEquals(BigDecimal.valueOf(100.5), result.totalCharged(), "Total should be $100.50");
                    },
                    () -> {
                        PaymentResult result = validPayment.process(BigDecimal.valueOf(1000));
                        assertEquals(BigDecimal.valueOf(0.5), result.fee(), "Fee should be $0.50 for $1000");
                        assertEquals(BigDecimal.valueOf(1000.5), result.totalCharged(), "Total should be $1000.50");
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
                    () -> assertNull(result.transactionId(), "Transaction ID should be null for failed payment")
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
        @DisplayName("Should fail when amount exceeds $5,000 limit")
        void shouldFailWhenAmountExceedsLimit() {
            // When
            PaymentResult result = validPayment.process(BigDecimal.valueOf(5001));

            // Then
            assertAll("Exceeded limit result",
                    () -> assertEquals(PaymentStatus.FAILED, result.status(), "Status should be FAILED"),
                    () -> assertTrue(result.message().contains("5,000"), "Error message should mention limit")
            );
        }

        @Test
        @DisplayName("Should process payment at exactly $5,000 limit")
        void shouldProcessAtExactLimit() {
            // When
            PaymentResult result = validPayment.process(BigDecimal.valueOf(5000));

            // Then
            assertAll("At limit result",
                    () -> assertEquals(PaymentStatus.COMPLETED, result.status(), "Should process at exact limit"),
                    () -> assertEquals(BigDecimal.valueOf(5000), result.originalAmount()),
                    () -> assertEquals(BigDecimal.valueOf(0.5), result.fee(), "Fee should still be $0.50")
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
            DebitCardPayment payment = new DebitCardPayment(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_PIN
            );

            // When
            String paymentType = payment.getPaymentType();

            // Then
            assertEquals("DebitCardPayment", paymentType, "Payment type should be DebitCardPayment");
        }
    }
}
