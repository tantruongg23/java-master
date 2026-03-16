package exercises.payment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BankTransferPayment Tests")
class BankTransferPaymentTest {

    private static final String VALID_BANK_CODE_8 = "ABCD1234"; // 8 chars
    private static final String VALID_BANK_CODE_11 = "ABCD1234567"; // 11 chars
    private static final String VALID_ACCOUNT_NUMBER = "1234567890";
    private static final String VALID_ACCOUNT_HOLDER = "Company Inc";

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate successfully with 8-character bank code")
        void shouldValidateSuccessfullyWith8CharBankCode() {
            // Given
            BankTransferPayment payment = new BankTransferPayment(
                    VALID_BANK_CODE_8, VALID_ACCOUNT_NUMBER, VALID_ACCOUNT_HOLDER
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertTrue(isValid, "Payment should be valid with 8-char bank code");
        }

        @Test
        @DisplayName("Should validate successfully with 11-character bank code")
        void shouldValidateSuccessfullyWith11CharBankCode() {
            // Given
            BankTransferPayment payment = new BankTransferPayment(
                    VALID_BANK_CODE_11, VALID_ACCOUNT_NUMBER, VALID_ACCOUNT_HOLDER
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertTrue(isValid, "Payment should be valid with 11-char bank code");
        }

        @Test
        @DisplayName("Should fail validation when bank code is 7 characters")
        void shouldFailWhenBankCodeIs7Chars() {
            // Given
            BankTransferPayment payment = new BankTransferPayment(
                    "ABCD123", VALID_ACCOUNT_NUMBER, VALID_ACCOUNT_HOLDER
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when bank code is 7 chars");
        }

        @Test
        @DisplayName("Should fail validation when bank code is 9 characters")
        void shouldFailWhenBankCodeIs9Chars() {
            // Given
            BankTransferPayment payment = new BankTransferPayment(
                    "ABCD12345", VALID_ACCOUNT_NUMBER, VALID_ACCOUNT_HOLDER
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when bank code is 9 chars");
        }

        @Test
        @DisplayName("Should fail validation when bank code is 12 characters")
        void shouldFailWhenBankCodeIs12Chars() {
            // Given
            BankTransferPayment payment = new BankTransferPayment(
                    "ABCD12345678", VALID_ACCOUNT_NUMBER, VALID_ACCOUNT_HOLDER
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when bank code is 12 chars");
        }

        @Test
        @DisplayName("Should fail validation when bank code is null")
        void shouldFailWhenBankCodeIsNull() {
            // Given
            BankTransferPayment payment = new BankTransferPayment(
                    null, VALID_ACCOUNT_NUMBER, VALID_ACCOUNT_HOLDER
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when bank code is null");
        }

        @Test
        @DisplayName("Should fail validation when bank code is blank")
        void shouldFailWhenBankCodeIsBlank() {
            // Given
            BankTransferPayment payment = new BankTransferPayment(
                    "", VALID_ACCOUNT_NUMBER, VALID_ACCOUNT_HOLDER
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when bank code is blank");
        }

        @Test
        @DisplayName("Should validate with 8-digit account number (minimum)")
        void shouldValidateWith8DigitAccountNumber() {
            // Given
            BankTransferPayment payment = new BankTransferPayment(
                    VALID_BANK_CODE_8, "12345678", VALID_ACCOUNT_HOLDER
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertTrue(isValid, "Payment should be valid with 8-digit account number");
        }

        @Test
        @DisplayName("Should validate with 20-digit account number (maximum)")
        void shouldValidateWith20DigitAccountNumber() {
            // Given
            BankTransferPayment payment = new BankTransferPayment(
                    VALID_BANK_CODE_8, "12345678901234567890", VALID_ACCOUNT_HOLDER
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertTrue(isValid, "Payment should be valid with 20-digit account number");
        }

        @Test
        @DisplayName("Should fail validation when account number is too short (7 digits)")
        void shouldFailWhenAccountNumberTooShort() {
            // Given
            BankTransferPayment payment = new BankTransferPayment(
                    VALID_BANK_CODE_8, "1234567", VALID_ACCOUNT_HOLDER
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when account number < 8 digits");
        }

        @Test
        @DisplayName("Should fail validation when account number is too long (21 digits)")
        void shouldFailWhenAccountNumberTooLong() {
            // Given
            BankTransferPayment payment = new BankTransferPayment(
                    VALID_BANK_CODE_8, "123456789012345678901", VALID_ACCOUNT_HOLDER
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when account number > 20 digits");
        }

        @Test
        @DisplayName("Should fail validation when account number is null")
        void shouldFailWhenAccountNumberIsNull() {
            // Given
            BankTransferPayment payment = new BankTransferPayment(
                    VALID_BANK_CODE_8, null, VALID_ACCOUNT_HOLDER
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when account number is null");
        }

        @Test
        @DisplayName("Should fail validation when account holder name is blank")
        void shouldFailWhenAccountHolderNameIsBlank() {
            // Given
            BankTransferPayment payment = new BankTransferPayment(
                    VALID_BANK_CODE_8, VALID_ACCOUNT_NUMBER, ""
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when account holder name is blank");
        }

        @Test
        @DisplayName("Should fail validation when account holder name is null")
        void shouldFailWhenAccountHolderNameIsNull() {
            // Given
            BankTransferPayment payment = new BankTransferPayment(
                    VALID_BANK_CODE_8, VALID_ACCOUNT_NUMBER, null
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertFalse(isValid, "Payment should be invalid when account holder name is null");
        }
    }

    @Nested
    @DisplayName("Processing Tests")
    class ProcessingTests {

        private BankTransferPayment validPayment;

        @BeforeEach
        void setUp() {
            validPayment = new BankTransferPayment(
                    VALID_BANK_CODE_8, VALID_ACCOUNT_NUMBER, VALID_ACCOUNT_HOLDER
            );
        }

        @Test
        @DisplayName("Should return PENDING status for bank transfers")
        void shouldReturnPendingStatus() {
            // Given
            BigDecimal amount = BigDecimal.valueOf(1000);

            // When
            PaymentResult result = validPayment.process(amount);

            // Then
            assertAll("Payment result",
                    () -> assertEquals(PaymentStatus.PENDING, result.status(), 
                            "Status should be PENDING for bank transfers"),
                    () -> assertNotNull(result.transactionId(), "Transaction ID should not be null"),
                    () -> assertTrue(result.transactionId().startsWith("BT-"), 
                            "Transaction ID should start with BT-"),
                    () -> assertTrue(result.message().contains("pending") || result.message().contains("business days"),
                            "Message should indicate pending status")
            );
        }

        @Test
        @DisplayName("Should charge flat $5 fee for bank transfers")
        void shouldChargeFlatFiveDollarFee() {
            // Test cases: different amounts should all have $5 fee
            assertAll("Flat $5 fee for different amounts",
                    () -> {
                        PaymentResult result = validPayment.process(BigDecimal.valueOf(100));
                        assertEquals(BigDecimal.valueOf(5), result.fee(), "Fee should be $5 for $100");
                        assertEquals(BigDecimal.valueOf(105), result.totalCharged(), "Total should be $105");
                    },
                    () -> {
                        PaymentResult result = validPayment.process(BigDecimal.valueOf(1000));
                        assertEquals(BigDecimal.valueOf(5), result.fee(), "Fee should be $5 for $1000");
                        assertEquals(BigDecimal.valueOf(1005), result.totalCharged(), "Total should be $1005");
                    },
                    () -> {
                        PaymentResult result = validPayment.process(BigDecimal.valueOf(50000));
                        assertEquals(BigDecimal.valueOf(5), result.fee(), "Fee should be $5 for $50000");
                        assertEquals(BigDecimal.valueOf(50005), result.totalCharged(), "Total should be $50005");
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
            PaymentResult result = validPayment.process(BigDecimal.valueOf(-100));

            // Then
            assertEquals(PaymentStatus.FAILED, result.status(), "Status should be FAILED for negative amount");
        }

        @Test
        @DisplayName("Should fail when amount exceeds $100,000 limit")
        void shouldFailWhenAmountExceedsLimit() {
            // When
            PaymentResult result = validPayment.process(BigDecimal.valueOf(100001));

            // Then
            assertAll("Exceeded limit result",
                    () -> assertEquals(PaymentStatus.FAILED, result.status(), "Status should be FAILED"),
                    () -> assertTrue(result.message().contains("100,000"), "Error message should mention limit")
            );
        }

        @Test
        @DisplayName("Should process payment at exactly $100,000 limit")
        void shouldProcessAtExactLimit() {
            // When
            PaymentResult result = validPayment.process(BigDecimal.valueOf(100000));

            // Then
            assertAll("At limit result",
                    () -> assertEquals(PaymentStatus.PENDING, result.status(), 
                            "Should return PENDING at exact limit"),
                    () -> assertEquals(BigDecimal.valueOf(100000), result.originalAmount()),
                    () -> assertEquals(BigDecimal.valueOf(5), result.fee(), "Fee should still be $5")
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
            BankTransferPayment payment = new BankTransferPayment(
                    VALID_BANK_CODE_8, VALID_ACCOUNT_NUMBER, VALID_ACCOUNT_HOLDER
            );

            // When
            String paymentType = payment.getPaymentType();

            // Then
            assertEquals("BankTransferPayment", paymentType, 
                    "Payment type should be BankTransferPayment");
        }
    }
}
