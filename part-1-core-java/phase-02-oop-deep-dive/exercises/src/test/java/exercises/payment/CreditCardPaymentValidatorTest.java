package exercises.payment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.time.Month;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CreditCardPaymentValidator Tests")
class CreditCardPaymentValidatorTest {

    private static final String VALID_CARD_NUMBER = "4111111111111111";
    private static final String VALID_CARD_HOLDER = "John Doe";
    private static final Month VALID_MONTH = Month.DECEMBER;
    private static final Year VALID_YEAR = Year.of(2026);
    private static final String VALID_CVV = "123";

    @Nested
    @DisplayName("SRP Tests - Validator Only Handles Validation")
    class SRPTests {

        @Test
        @DisplayName("Should validate successfully with all valid data")
        void shouldValidateSuccessfully() {
            // Given
            PaymentValidator validator = new CreditCardPaymentValidator(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_CVV
            );

            // When
            boolean isValid = validator.validate();

            // Then
            assertTrue(isValid, "Validator should return true for valid data");
        }

        @Test
        @DisplayName("Should return empty error list for valid data")
        void shouldReturnEmptyErrorsForValidData() {
            // Given
            PaymentValidator validator = new CreditCardPaymentValidator(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_CVV
            );

            // When
            List<String> errors = validator.validateWithErrors();

            // Then
            assertTrue(errors.isEmpty(), "Should have no validation errors");
        }

        @Test
        @DisplayName("Should return detailed error messages for invalid data")
        void shouldReturnDetailedErrorMessages() {
            // Given - All fields invalid
            PaymentValidator validator = new CreditCardPaymentValidator(
                    null, "", VALID_MONTH, Year.of(2020), "12"
            );

            // When
            List<String> errors = validator.validateWithErrors();

            // Then
            assertAll("Should have multiple specific error messages",
                    () -> assertFalse(errors.isEmpty(), "Should have errors"),
                    () -> assertTrue(errors.stream().anyMatch(e -> e.contains("Card holder")),
                            "Should have card holder error"),
                    () -> assertTrue(errors.stream().anyMatch(e -> e.contains("Card number")),
                            "Should have card number error"),
                    () -> assertTrue(errors.stream().anyMatch(e -> e.contains("expired")),
                            "Should have expiry error"),
                    () -> assertTrue(errors.stream().anyMatch(e -> e.contains("CVV")),
                            "Should have CVV error")
            );
        }
    }

    @Nested
    @DisplayName("ISP Tests - Focused Interface")
    class ISPTests {

        @Test
        @DisplayName("Validator interface only exposes validation methods")
        void shouldOnlyExposeValidationMethods() {
            // Given
            PaymentValidator validator = new CreditCardPaymentValidator(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_CVV
            );

            // When - Check available methods through interface
            // Then - Interface only has validate() and validateWithErrors()
            assertDoesNotThrow(() -> {
                validator.validate();
                validator.validateWithErrors();
            }, "Validator interface should only expose validation methods");

            // Verify no processing methods are exposed
            assertFalse(
                    hasMethod(PaymentValidator.class, "process"),
                    "Validator should not have process() method"
            );
        }

        @Test
        @DisplayName("Different validators can be used interchangeably")
        void shouldAllowDifferentValidators() {
            // Given - Two different validator implementations
            PaymentValidator creditCardValidator = new CreditCardPaymentValidator(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_CVV
            );
            
            PaymentValidator cryptoValidator = new CryptoPaymentValidator(
                    "0x1234567890123456789012345678901234567890", "ETH"
            );

            // When/Then - Both can be used through same interface
            assertAll("Both validators work through same interface",
                    () -> assertDoesNotThrow(() -> creditCardValidator.validate()),
                    () -> assertDoesNotThrow(() -> cryptoValidator.validate()),
                    () -> assertDoesNotThrow(() -> creditCardValidator.validateWithErrors()),
                    () -> assertDoesNotThrow(() -> cryptoValidator.validateWithErrors())
            );
        }
    }

    @Nested
    @DisplayName("DIP Tests - Depend on Abstraction")
    class DIPTests {

        @Test
        @DisplayName("Payment method can work with any PaymentValidator implementation")
        void shouldWorkWithAnyValidator() {
            // Given - Create payment with validator dependency
            PaymentValidator validator = new CreditCardPaymentValidator(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_CVV
            );

            CreditCardPaymentWithValidator payment = new CreditCardPaymentWithValidator(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_CVV,
                    validator // Injected dependency
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertTrue(isValid, "Payment should use injected validator");
        }

        @Test
        @DisplayName("Can inject custom validator implementation")
        void shouldAllowCustomValidatorInjection() {
            // Given - Create custom validator that always passes
            PaymentValidator alwaysValidValidator = new PaymentValidator() {
                @Override
                public boolean validate() {
                    return true;
                }

                @Override
                public List<String> validateWithErrors() {
                    return List.of();
                }
            };

            CreditCardPaymentWithValidator payment = new CreditCardPaymentWithValidator(
                    "invalid", "", VALID_MONTH, VALID_YEAR, "1", // Invalid data
                    alwaysValidValidator // Custom validator
            );

            // When
            boolean isValid = payment.validate();

            // Then
            assertTrue(isValid, "Should use custom validator, ignoring invalid data");
        }
    }

    @Nested
    @DisplayName("Validation Logic Tests")
    class ValidationLogicTests {

        @Test
        @DisplayName("Should detect null card holder name")
        void shouldDetectNullCardHolder() {
            // Given
            PaymentValidator validator = new CreditCardPaymentValidator(
                    VALID_CARD_NUMBER, null, VALID_MONTH, VALID_YEAR, VALID_CVV
            );

            // When
            List<String> errors = validator.validateWithErrors();

            // Then
            assertFalse(errors.isEmpty());
            assertTrue(errors.stream().anyMatch(e -> e.contains("Card holder")));
        }

        @Test
        @DisplayName("Should detect invalid card number")
        void shouldDetectInvalidCardNumber() {
            // Given
            PaymentValidator validator = new CreditCardPaymentValidator(
                    "1234567890123456", VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, VALID_CVV
            );

            // When
            List<String> errors = validator.validateWithErrors();

            // Then
            assertFalse(errors.isEmpty());
            assertTrue(errors.stream().anyMatch(e -> e.contains("Luhn") || e.contains("Card number")));
        }

        @Test
        @DisplayName("Should detect expired card")
        void shouldDetectExpiredCard() {
            // Given
            PaymentValidator validator = new CreditCardPaymentValidator(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, Month.JANUARY, Year.of(2020), VALID_CVV
            );

            // When
            List<String> errors = validator.validateWithErrors();

            // Then
            assertFalse(errors.isEmpty());
            assertTrue(errors.stream().anyMatch(e -> e.contains("expired")));
        }

        @Test
        @DisplayName("Should detect invalid CVV")
        void shouldDetectInvalidCVV() {
            // Given
            PaymentValidator validator = new CreditCardPaymentValidator(
                    VALID_CARD_NUMBER, VALID_CARD_HOLDER, VALID_MONTH, VALID_YEAR, "12A"
            );

            // When
            List<String> errors = validator.validateWithErrors();

            // Then
            assertFalse(errors.isEmpty());
            assertTrue(errors.stream().anyMatch(e -> e.contains("CVV")));
        }
    }

    // Helper method to check if class has a method
    private boolean hasMethod(Class<?> clazz, String methodName) {
        try {
            return java.util.Arrays.stream(clazz.getDeclaredMethods())
                    .anyMatch(m -> m.getName().equals(methodName));
        } catch (Exception e) {
            return false;
        }
    }
}
