package exercises.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PaypalPayment Tests")
class PaypalPaymentTest {

    private static final String VALID_EMAIL = "user@example.com";
    private static final String VALID_TOKEN = "token123456789";

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate successfully with valid email and token")
        void shouldValidateSuccessfully() {
            // Given
            PaypalPayment payment = new PaypalPayment(VALID_EMAIL, VALID_TOKEN);

            // When
            boolean isValid = payment.validate();

            // Then
            assertTrue(isValid, "Payment with valid data should be valid");
        }

        @Test
        @DisplayName("Should fail validation when email is invalid")
        void shouldFailWhenEmailIsInvalid() {
            // Given
            PaypalPayment payment = new PaypalPayment("invalid-email", VALID_TOKEN);

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when email format is wrong");
        }

        @Test
        @DisplayName("Should fail validation when email is missing @ symbol")
        void shouldFailWhenEmailMissingAtSymbol() {
            // Given
            PaypalPayment payment = new PaypalPayment("userexample.com", VALID_TOKEN);

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when email missing @ symbol");
        }

        @Test
        @DisplayName("Should fail validation when email is missing domain")
        void shouldFailWhenEmailMissingDomain() {
            // Given
            PaypalPayment payment = new PaypalPayment("user@", VALID_TOKEN);

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when email missing domain");
        }

        @Test
        @DisplayName("Should fail validation when auth token is blank")
        void shouldFailWhenAuthTokenIsBlank() {
            // Given
            PaypalPayment payment = new PaypalPayment(VALID_EMAIL, "");

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when auth token is blank");
        }

        @Test
        @DisplayName("Should fail validation when auth token is null")
        void shouldFailWhenAuthTokenIsNull() {
            // Given
            PaypalPayment payment = new PaypalPayment(VALID_EMAIL, null);

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when auth token is null");
        }

        @Test
        @DisplayName("Should fail validation when auth token is too short (<=10 chars)")
        void shouldFailWhenAuthTokenIsTooShort() {
            // Given
            PaypalPayment payment = new PaypalPayment(VALID_EMAIL, "short");

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when auth token is 10 chars or less");
        }

        @Test
        @DisplayName("Should validate when auth token is exactly 11 chars")
        void shouldValidateWhenAuthTokenIs11Chars() {
            // Given
            PaypalPayment payment = new PaypalPayment(VALID_EMAIL, "12345678901"); // 11 chars

            // When
            boolean isValid = payment.validate();

            // Then
            assertTrue(isValid, "Payment should be valid when auth token is 11+ chars");
        }
    }

    @Nested
    @DisplayName("Processing Tests")
    class ProcessingTests {

        private PaypalPayment validPayment;

        @BeforeEach
        void setUp() {
            validPayment = new PaypalPayment(VALID_EMAIL, VALID_TOKEN);
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
                    () -> assertTrue(result.transactionId().startsWith("PP-"), "Transaction ID should start with PP-"),
                    () -> assertEquals(amount, result.originalAmount(), "Original amount should match")
            );
        }

        @Test
        @DisplayName("Should calculate 2.9% + $0.30 fee correctly")
        void shouldCalculatePayPalFeeCorrectly() {
            // Test cases: amount -> expected fee
            assertAll("PayPal fee calculations",
                    () -> {
                        // $100: (100 * 0.029) + 0.30 = 2.90 + 0.30 = 3.20
                        PaymentResult result = validPayment.process(BigDecimal.valueOf(100));
                        assertEquals(new BigDecimal("3.20"), result.fee(), "$100 fee should be $3.20");
                        assertEquals(new BigDecimal("103.20"), result.totalCharged(), "Total should be $103.20");
                    },
                    () -> {
                        // $1000: (1000 * 0.029) + 0.30 = 29.00 + 0.30 = 29.30
                        PaymentResult result = validPayment.process(BigDecimal.valueOf(1000));
                        assertEquals(new BigDecimal("29.30"), result.fee(), "$1000 fee should be $29.30");
                        assertEquals(new BigDecimal("1029.30"), result.totalCharged(), "Total should be $1029.30");
                    },
                    () -> {
                        // $50: (50 * 0.029) + 0.30 = 1.45 + 0.30 = 1.75
                        PaymentResult result = validPayment.process(BigDecimal.valueOf(50));
                        assertEquals(new BigDecimal("1.75"), result.fee(), "$50 fee should be $1.75");
                        assertEquals(new BigDecimal("51.75"), result.totalCharged(), "Total should be $51.75");
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
        @DisplayName("Should fail when amount exceeds $25,000 limit")
        void shouldFailWhenAmountExceedsLimit() {
            // When
            PaymentResult result = validPayment.process(BigDecimal.valueOf(25001));

            // Then
            assertEquals(PaymentStatus.FAILED, result.status(), "Status should be FAILED when exceeding limit");
        }

        @Test
        @DisplayName("Should process payment at exactly $25,000 limit")
        void shouldProcessAtExactLimit() {
            // When
            PaymentResult result = validPayment.process(BigDecimal.valueOf(25000));

            // Then
            assertAll("At limit result",
                    () -> assertEquals(PaymentStatus.COMPLETED, result.status(), "Should process at exact limit"),
                    () -> assertEquals(BigDecimal.valueOf(25000), result.originalAmount()),
                    () -> assertEquals(new BigDecimal("725.30"), result.fee(), "Fee should be (25000 * 0.029) + 0.30")
            );
        }

        @Test
        @DisplayName("Should handle small amounts correctly")
        void shouldHandleSmallAmountsCorrectly() {
            // When - $1 payment
            PaymentResult result = validPayment.process(BigDecimal.valueOf(1));

            // Then - (1 * 0.029) + 0.30 = 0.029 + 0.30 = 0.329 ≈ 0.33
            assertAll("Small amount",
                    () -> assertEquals(PaymentStatus.COMPLETED, result.status()),
                    () -> assertEquals(new BigDecimal("0.33"), result.fee(), "Fee for $1 should be $0.33")
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
            PaypalPayment payment = new PaypalPayment(VALID_EMAIL, VALID_TOKEN);

            // When
            String paymentType = payment.getPaymentType();

            // Then
            assertEquals("PaypalPayment", paymentType, "Payment type should be PaypalPayment");
        }
    }
}
