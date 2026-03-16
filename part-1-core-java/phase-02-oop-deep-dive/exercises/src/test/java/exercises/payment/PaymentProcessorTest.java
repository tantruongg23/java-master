package exercises.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PaymentProcessor Tests")
class PaymentProcessorTest {

    private PaymentProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new PaymentProcessor();
    }

    @Nested
    @DisplayName("SOLID Principles Tests")
    class SolidPrinciplesTests {

        @Test
        @DisplayName("DIP: Processor should work with PaymentMethod interface")
        void shouldWorkWithPaymentMethodInterface() {
            // Given - Different payment method implementations
            PaymentMethod creditCard = new CreditCardPayment(
                    "4111111111111111", "John Doe", Month.DECEMBER, Year.of(2026), "123"
            );
            PaymentMethod paypal = new PaypalPayment("user@example.com", "token123456789");
            PaymentMethod crypto = new CryptoPayment(
                    "0x1234567890123456789012345678901234567890", "ETH"
            );

            BigDecimal amount = BigDecimal.valueOf(100);

            // When - Process different payment types through same interface
            PaymentResult ccResult = processor.processPayment(creditCard, amount);
            PaymentResult ppResult = processor.processPayment(paypal, amount);
            PaymentResult cryptoResult = processor.processPayment(crypto, amount);

            // Then - All should process successfully
            assertAll("All payment methods should work",
                    () -> assertEquals(PaymentStatus.COMPLETED, ccResult.status()),
                    () -> assertEquals(PaymentStatus.COMPLETED, ppResult.status()),
                    () -> assertEquals(PaymentStatus.PENDING, cryptoResult.status())
            );
        }

        @Test
        @DisplayName("OCP: Adding new payment type doesn't modify processor")
        void shouldSupportNewPaymentTypeWithoutModification() {
            // Given - CryptoPayment was added as bonus (OCP demonstration)
            PaymentMethod crypto = new CryptoPayment(
                    "0x1234567890123456789012345678901234567890", "ETH"
            );
            BigDecimal amount = BigDecimal.valueOf(1000);

            // When
            PaymentResult result = processor.processPayment(crypto, amount);

            // Then - Processor works without any code changes
            assertAll("Crypto payment should work",
                    () -> assertEquals(PaymentStatus.PENDING, result.status()),
                    () -> assertNotNull(result.transactionId()),
                    () -> assertTrue(result.transactionId().startsWith("CRYPTO-"))
            );
        }

        @Test
        @DisplayName("LSP: All PaymentMethod subtypes should be substitutable")
        void shouldSubstituteAllPaymentMethodTypes() {
            // Given - Array of different payment method types
            PaymentMethod[] paymentMethods = {
                    new CreditCardPayment("4111111111111111", "John", Month.JUNE, Year.of(2027), "123"),
                    new DebitCardPayment("4111111111111111", "Jane", Month.JUNE, Year.of(2027), "1234"),
                    new PaypalPayment("user@example.com", "token123456789"),
                    new BankTransferPayment("ABCD1234", "1234567890", "Company Inc"),
                    new CryptoPayment("0x1234567890123456789012345678901234567890", "ETH")
            };

            BigDecimal amount = BigDecimal.valueOf(100);

            // When/Then - All should process without breaking processor behavior
            for (PaymentMethod method : paymentMethods) {
                PaymentResult result = processor.processPayment(method, amount);
                assertNotNull(result, "Result should not be null for " + method.getPaymentType());
                assertNotNull(result.status(), "Status should not be null for " + method.getPaymentType());
            }
        }
    }

    @Nested
    @DisplayName("Amount Validation Tests")
    class AmountValidationTests {

        private PaymentMethod validPaymentMethod;

        @BeforeEach
        void setUp() {
            validPaymentMethod = new CreditCardPayment(
                    "4111111111111111", "John Doe", Month.DECEMBER, Year.of(2026), "123"
            );
        }

        @Test
        @DisplayName("Should fail when amount is null")
        void shouldFailWhenAmountIsNull() {
            // When
            PaymentResult result = processor.processPayment(validPaymentMethod, null);

            // Then
            assertAll("Null amount result",
                    () -> assertEquals(PaymentStatus.FAILED, result.status(), "Status should be FAILED"),
                    () -> assertTrue(result.message().contains("Invalid") || result.message().contains("amount"),
                            "Error message should mention invalid amount")
            );
        }

        @Test
        @DisplayName("Should fail when amount is zero")
        void shouldFailWhenAmountIsZero() {
            // When
            PaymentResult result = processor.processPayment(validPaymentMethod, BigDecimal.ZERO);

            // Then
            assertEquals(PaymentStatus.FAILED, result.status(), "Status should be FAILED for zero amount");
        }

        @Test
        @DisplayName("Should fail when amount is negative")
        void shouldFailWhenAmountIsNegative() {
            // When
            PaymentResult result = processor.processPayment(validPaymentMethod, BigDecimal.valueOf(-100));

            // Then
            assertEquals(PaymentStatus.FAILED, result.status(), "Status should be FAILED for negative amount");
        }

        @Test
        @DisplayName("Should process when amount is positive")
        void shouldProcessWhenAmountIsPositive() {
            // When
            PaymentResult result = processor.processPayment(validPaymentMethod, BigDecimal.valueOf(100));

            // Then
            assertEquals(PaymentStatus.COMPLETED, result.status(), "Status should be COMPLETED for positive amount");
        }
    }

    @Nested
    @DisplayName("Payment Method Validation Tests")
    class PaymentMethodValidationTests {

        @Test
        @DisplayName("Should fail when payment method validation fails")
        void shouldFailWhenPaymentMethodValidationFails() {
            // Given - Invalid payment method (expired card)
            PaymentMethod invalidPayment = new CreditCardPayment(
                    "4111111111111111", "John Doe", Month.JANUARY, Year.of(2020), "123"
            );

            // When
            PaymentResult result = processor.processPayment(invalidPayment, BigDecimal.valueOf(100));

            // Then
            assertAll("Invalid payment method result",
                    () -> assertEquals(PaymentStatus.FAILED, result.status(), "Status should be FAILED"),
                    () -> assertTrue(result.message().contains("Invalid payment method"),
                            "Error message should indicate invalid payment method")
            );
        }

        @Test
        @DisplayName("Should process when payment method is valid")
        void shouldProcessWhenPaymentMethodIsValid() {
            // Given
            PaymentMethod validPayment = new CreditCardPayment(
                    "4111111111111111", "John Doe", Month.DECEMBER, Year.of(2026), "123"
            );

            // When
            PaymentResult result = processor.processPayment(validPayment, BigDecimal.valueOf(100));

            // Then
            assertEquals(PaymentStatus.COMPLETED, result.status(), 
                    "Status should be COMPLETED when payment method is valid");
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should process complete CreditCard payment flow")
        void shouldProcessCompleteCreditCardFlow() {
            // Given
            PaymentMethod creditCard = new CreditCardPayment(
                    "4111111111111111", "John Doe", Month.DECEMBER, Year.of(2026), "123"
            );
            BigDecimal amount = BigDecimal.valueOf(500);

            // When
            PaymentResult result = processor.processPayment(creditCard, amount);

            // Then
            assertAll("Complete credit card flow",
                    () -> assertEquals(PaymentStatus.COMPLETED, result.status()),
                    () -> assertTrue(result.transactionId().startsWith("CC-")),
                    () -> assertEquals(amount, result.originalAmount()),
                    () -> assertEquals(BigDecimal.valueOf(10.0), result.fee(), "2% of 500"),
                    () -> assertEquals(BigDecimal.valueOf(510.0), result.totalCharged()),
                    () -> assertNotNull(result.timestamp())
            );
        }

        @Test
        @DisplayName("Should process complete DebitCard payment flow")
        void shouldProcessCompleteDebitCardFlow() {
            // Given
            PaymentMethod debitCard = new DebitCardPayment(
                    "4111111111111111", "Jane Smith", Month.JUNE, Year.of(2027), "1234"
            );
            BigDecimal amount = BigDecimal.valueOf(200);

            // When
            PaymentResult result = processor.processPayment(debitCard, amount);

            // Then
            assertAll("Complete debit card flow",
                    () -> assertEquals(PaymentStatus.COMPLETED, result.status()),
                    () -> assertTrue(result.transactionId().startsWith("DC-")),
                    () -> assertEquals(amount, result.originalAmount()),
                    () -> assertEquals(BigDecimal.valueOf(0.5), result.fee()),
                    () -> assertEquals(BigDecimal.valueOf(200.5), result.totalCharged())
            );
        }

        @Test
        @DisplayName("Should process complete PayPal payment flow")
        void shouldProcessCompletePayPalFlow() {
            // Given
            PaymentMethod paypal = new PaypalPayment("user@example.com", "token123456789");
            BigDecimal amount = BigDecimal.valueOf(100);

            // When
            PaymentResult result = processor.processPayment(paypal, amount);

            // Then
            assertAll("Complete PayPal flow",
                    () -> assertEquals(PaymentStatus.COMPLETED, result.status()),
                    () -> assertTrue(result.transactionId().startsWith("PP-")),
                    () -> assertEquals(amount, result.originalAmount()),
                    () -> assertEquals(new BigDecimal("3.20"), result.fee(), "2.9% + $0.30"),
                    () -> assertEquals(new BigDecimal("103.20"), result.totalCharged())
            );
        }

        @Test
        @DisplayName("Should process complete BankTransfer payment flow with PENDING status")
        void shouldProcessCompleteBankTransferFlow() {
            // Given
            PaymentMethod bankTransfer = new BankTransferPayment(
                    "ABCD1234", "1234567890", "Company Inc"
            );
            BigDecimal amount = BigDecimal.valueOf(10000);

            // When
            PaymentResult result = processor.processPayment(bankTransfer, amount);

            // Then
            assertAll("Complete bank transfer flow",
                    () -> assertEquals(PaymentStatus.PENDING, result.status(), 
                            "Bank transfers should return PENDING"),
                    () -> assertTrue(result.transactionId().startsWith("BT-")),
                    () -> assertEquals(amount, result.originalAmount()),
                    () -> assertEquals(BigDecimal.valueOf(5), result.fee()),
                    () -> assertEquals(BigDecimal.valueOf(10005), result.totalCharged()),
                    () -> assertTrue(result.message().contains("pending") || result.message().contains("business"))
            );
        }

        @Test
        @DisplayName("Should process complete Crypto payment flow with PENDING status")
        void shouldProcessCompleteCryptoFlow() {
            // Given
            PaymentMethod crypto = new CryptoPayment(
                    "0x1234567890123456789012345678901234567890", "ETH"
            );
            BigDecimal amount = BigDecimal.valueOf(5000);

            // When
            PaymentResult result = processor.processPayment(crypto, amount);

            // Then
            assertAll("Complete crypto flow",
                    () -> assertEquals(PaymentStatus.PENDING, result.status(), 
                            "Crypto payments should return PENDING"),
                    () -> assertTrue(result.transactionId().startsWith("CRYPTO-")),
                    () -> assertEquals(amount, result.originalAmount()),
                    () -> assertEquals(BigDecimal.valueOf(50.0), result.fee(), "1% of 5000"),
                    () -> assertEquals(BigDecimal.valueOf(5050.0), result.totalCharged()),
                    () -> assertTrue(result.message().contains("blockchain") || result.message().contains("confirmation"))
            );
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle exceptions gracefully")
        void shouldHandleExceptionsGracefully() {
            // Given - This will trigger validation failure
            PaymentMethod invalidPayment = new CreditCardPayment(
                    null, null, Month.JANUARY, Year.of(2020), null
            );

            // When
            PaymentResult result = processor.processPayment(invalidPayment, BigDecimal.valueOf(100));

            // Then
            assertAll("Exception handling",
                    () -> assertEquals(PaymentStatus.FAILED, result.status(), 
                            "Should return FAILED status on exception"),
                    () -> assertNotNull(result.message(), "Should have error message"),
                    () -> assertNull(result.transactionId(), "Transaction ID should be null on failure")
            );
        }

        @Test
        @DisplayName("Should handle payment method specific amount limits")
        void shouldHandlePaymentMethodSpecificLimits() {
            // Given
            PaymentMethod creditCard = new CreditCardPayment(
                    "4111111111111111", "John", Month.JUNE, Year.of(2027), "123"
            );
            PaymentMethod debitCard = new DebitCardPayment(
                    "4111111111111111", "Jane", Month.JUNE, Year.of(2027), "1234"
            );

            // When - Amount exceeds credit card limit but within debit card limit
            PaymentResult ccResult = processor.processPayment(creditCard, BigDecimal.valueOf(15000));
            // Amount exceeds debit card limit
            PaymentResult dcResult = processor.processPayment(debitCard, BigDecimal.valueOf(6000));

            // Then
            assertAll("Payment method specific limits",
                    () -> assertEquals(PaymentStatus.FAILED, ccResult.status(), 
                            "Credit card should fail over $10k"),
                    () -> assertEquals(PaymentStatus.FAILED, dcResult.status(), 
                            "Debit card should fail over $5k")
            );
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle very small amounts correctly")
        void shouldHandleVerySmallAmounts() {
            // Given
            PaymentMethod paypal = new PaypalPayment("user@example.com", "token123456789");
            BigDecimal smallAmount = new BigDecimal("0.01");

            // When
            PaymentResult result = processor.processPayment(paypal, smallAmount);

            // Then
            assertAll("Small amount handling",
                    () -> assertEquals(PaymentStatus.COMPLETED, result.status()),
                    () -> assertEquals(smallAmount, result.originalAmount()),
                    () -> assertTrue(result.totalCharged().compareTo(smallAmount) > 0, 
                            "Total should be greater than original due to fee")
            );
        }

        @Test
        @DisplayName("Should handle amounts at exact limits")
        void shouldHandleAmountsAtExactLimits() {
            // Test all payment methods at their exact limits
            assertAll("At exact limits",
                    () -> {
                        PaymentMethod cc = new CreditCardPayment(
                                "4111111111111111", "John", Month.JUNE, Year.of(2027), "123");
                        PaymentResult result = processor.processPayment(cc, BigDecimal.valueOf(10000));
                        assertEquals(PaymentStatus.COMPLETED, result.status(), 
                                "Credit card should process at $10k");
                    },
                    () -> {
                        PaymentMethod dc = new DebitCardPayment(
                                "4111111111111111", "Jane", Month.JUNE, Year.of(2027), "1234");
                        PaymentResult result = processor.processPayment(dc, BigDecimal.valueOf(5000));
                        assertEquals(PaymentStatus.COMPLETED, result.status(), 
                                "Debit card should process at $5k");
                    },
                    () -> {
                        PaymentMethod pp = new PaypalPayment("user@example.com", "token123456789");
                        PaymentResult result = processor.processPayment(pp, BigDecimal.valueOf(25000));
                        assertEquals(PaymentStatus.COMPLETED, result.status(), 
                                "PayPal should process at $25k");
                    },
                    () -> {
                        PaymentMethod bt = new BankTransferPayment("ABCD1234", "1234567890", "Company");
                        PaymentResult result = processor.processPayment(bt, BigDecimal.valueOf(100000));
                        assertEquals(PaymentStatus.PENDING, result.status(), 
                                "Bank transfer should process at $100k");
                    }
            );
        }
    }
}
