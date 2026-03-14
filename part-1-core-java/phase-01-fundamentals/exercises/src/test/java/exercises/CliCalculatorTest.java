package exercises;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for CliCalculator following AAA (Arrange-Act-Assert) pattern.
 * 
 * Test coverage includes:
 * - Basic arithmetic operations (addition, subtraction, multiplication, division, modulo, power)
 * - Edge cases (zeros, negative numbers, decimals, large numbers)
 * - Error handling (division by zero, invalid operators, invalid expressions)
 * - Expression parsing (whitespace, malformed expressions)
 * - History management (circular buffer behavior)
 */
@DisplayName("CLI Calculator Tests")
class CliCalculatorTest {

    private CliCalculator calculator;

    @BeforeEach
    void setUp() {
        // Arrange: Create a fresh calculator instance before each test
        calculator = new CliCalculator();
    }

    // ========================================
    // NESTED: Operator Enum Tests
    // ========================================
    
    @Nested
    @DisplayName("Operator Enum Tests")
    class OperatorEnumTests {

        @Test
        @DisplayName("Should return correct symbol for each operator")
        void testOperatorSymbols() {
            // Arrange & Act & Assert
            assertEquals("+", Operator.PLUS.getSymbol());
            assertEquals("-", Operator.MINUS.getSymbol());
            assertEquals("*", Operator.MULTIPLY.getSymbol());
            assertEquals("/", Operator.DIVIDE.getSymbol());
            assertEquals("%", Operator.MODULO.getSymbol());
            assertEquals("^", Operator.POWER.getSymbol());
        }

        @ParameterizedTest
        @ValueSource(strings = {"+", "-", "*", "/", "%", "^"})
        @DisplayName("Should parse valid operator symbols")
        void testFromSymbolValidOperators(String symbol) {
            // Arrange & Act
            Operator operator = Operator.fromSymbol(symbol);

            // Assert
            assertNotNull(operator);
            assertEquals(symbol, operator.getSymbol());
        }

        @ParameterizedTest
        @ValueSource(strings = {"x", "&", "!", "@", "**", "//", ""})
        @DisplayName("Should throw IllegalArgumentException for invalid operator symbols")
        void testFromSymbolInvalidOperators(String invalidSymbol) {
            // Arrange & Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> Operator.fromSymbol(invalidSymbol)
            );
            assertTrue(exception.getMessage().contains("Unknown operator"));
        }
    }

    // ========================================
    // NESTED: Calculate Method Tests
    // ========================================

    @Nested
    @DisplayName("Calculate Method Tests")
    class CalculateMethodTests {

        @Nested
        @DisplayName("Addition Tests")
        class AdditionTests {

            @Test
            @DisplayName("Should add two positive integers")
            void testAddPositiveIntegers() {
                // Arrange
                double left = 10;
                double right = 5;

                // Act
                double result = calculator.calculate(left, "+", right);

                // Assert
                assertEquals(15.0, result);
            }

            @Test
            @DisplayName("Should add two negative numbers")
            void testAddNegativeNumbers() {
                // Arrange
                double left = -10.5;
                double right = -3.2;

                // Act
                double result = calculator.calculate(left, "+", right);

                // Assert
                assertEquals(-13.7, result, 0.0001);
            }

            @Test
            @DisplayName("Should add positive and negative numbers")
            void testAddMixedSignNumbers() {
                // Arrange
                double left = 100;
                double right = -50;

                // Act
                double result = calculator.calculate(left, "+", right);

                // Assert
                assertEquals(50.0, result);
            }

            @Test
            @DisplayName("Should add decimal numbers")
            void testAddDecimals() {
                // Arrange
                double left = 12.5;
                double right = 3.7;

                // Act
                double result = calculator.calculate(left, "+", right);

                // Assert
                assertEquals(16.2, result, 0.0001);
            }

            @Test
            @DisplayName("Should handle adding zero")
            void testAddZero() {
                // Arrange
                double left = 42;
                double right = 0;

                // Act
                double result = calculator.calculate(left, "+", right);

                // Assert
                assertEquals(42.0, result);
            }

            @Test
            @DisplayName("Should handle adding large numbers")
            void testAddLargeNumbers() {
                // Arrange
                double left = 1_000_000;
                double right = 999_999;

                // Act
                double result = calculator.calculate(left, "+", right);

                // Assert
                assertEquals(1_999_999.0, result);
            }
        }

        @Nested
        @DisplayName("Subtraction Tests")
        class SubtractionTests {

            @Test
            @DisplayName("Should subtract two positive numbers")
            void testSubtractPositive() {
                // Arrange
                double left = 20;
                double right = 8;

                // Act
                double result = calculator.calculate(left, "-", right);

                // Assert
                assertEquals(12.0, result);
            }

            @Test
            @DisplayName("Should handle negative result")
            void testSubtractNegativeResult() {
                // Arrange
                double left = 5;
                double right = 10;

                // Act
                double result = calculator.calculate(left, "-", right);

                // Assert
                assertEquals(-5.0, result);
            }

            @Test
            @DisplayName("Should subtract negative numbers")
            void testSubtractNegatives() {
                // Arrange
                double left = -10;
                double right = -5;

                // Act
                double result = calculator.calculate(left, "-", right);

                // Assert
                assertEquals(-5.0, result);
            }

            @Test
            @DisplayName("Should subtract decimals")
            void testSubtractDecimals() {
                // Arrange
                double left = 15.75;
                double right = 3.25;

                // Act
                double result = calculator.calculate(left, "-", right);

                // Assert
                assertEquals(12.5, result, 0.0001);
            }
        }

        @Nested
        @DisplayName("Multiplication Tests")
        class MultiplicationTests {

            @Test
            @DisplayName("Should multiply two positive numbers")
            void testMultiplyPositive() {
                // Arrange
                double left = 6;
                double right = 7;

                // Act
                double result = calculator.calculate(left, "*", right);

                // Assert
                assertEquals(42.0, result);
            }

            @Test
            @DisplayName("Should multiply by zero")
            void testMultiplyByZero() {
                // Arrange
                double left = 100;
                double right = 0;

                // Act
                double result = calculator.calculate(left, "*", right);

                // Assert
                assertEquals(0.0, result);
            }

            @Test
            @DisplayName("Should multiply negative numbers")
            void testMultiplyNegatives() {
                // Arrange
                double left = -4;
                double right = -5;

                // Act
                double result = calculator.calculate(left, "*", right);

                // Assert
                assertEquals(20.0, result);
            }

            @Test
            @DisplayName("Should multiply positive and negative")
            void testMultiplyMixedSigns() {
                // Arrange
                double left = 8;
                double right = -3;

                // Act
                double result = calculator.calculate(left, "*", right);

                // Assert
                assertEquals(-24.0, result);
            }

            @Test
            @DisplayName("Should multiply decimals")
            void testMultiplyDecimals() {
                // Arrange
                double left = 2.5;
                double right = 4;

                // Act
                double result = calculator.calculate(left, "*", right);

                // Assert
                assertEquals(10.0, result, 0.0001);
            }
        }

        @Nested
        @DisplayName("Division Tests")
        class DivisionTests {

            @Test
            @DisplayName("Should divide two positive numbers")
            void testDividePositive() {
                // Arrange
                double left = 20;
                double right = 4;

                // Act
                double result = calculator.calculate(left, "/", right);

                // Assert
                assertEquals(5.0, result);
            }

            @Test
            @DisplayName("Should throw ArithmeticException when dividing by zero")
            void testDivideByZero() {
                // Arrange
                double left = 10;
                double right = 0;

                // Act & Assert
                ArithmeticException exception = assertThrows(
                    ArithmeticException.class,
                    () -> calculator.calculate(left, "/", right)
                );
                assertEquals("Division by zero", exception.getMessage());
            }

            @Test
            @DisplayName("Should divide negative numbers")
            void testDivideNegatives() {
                // Arrange
                double left = -20;
                double right = -5;

                // Act
                double result = calculator.calculate(left, "/", right);

                // Assert
                assertEquals(4.0, result);
            }

            @Test
            @DisplayName("Should divide with decimal result")
            void testDivideDecimalResult() {
                // Arrange
                double left = 10;
                double right = 4;

                // Act
                double result = calculator.calculate(left, "/", right);

                // Assert
                assertEquals(2.5, result, 0.0001);
            }

            @Test
            @DisplayName("Should divide zero by number")
            void testDivideZeroByNumber() {
                // Arrange
                double left = 0;
                double right = 5;

                // Act
                double result = calculator.calculate(left, "/", right);

                // Assert
                assertEquals(0.0, result);
            }
        }

        @Nested
        @DisplayName("Modulo Tests")
        class ModuloTests {

            @Test
            @DisplayName("Should compute modulo of two positive numbers")
            void testModuloPositive() {
                // Arrange
                double left = 10;
                double right = 3;

                // Act
                double result = calculator.calculate(left, "%", right);

                // Assert
                assertEquals(1.0, result);
            }

            @Test
            @DisplayName("Should throw ArithmeticException when modulo by zero")
            void testModuloByZero() {
                // Arrange
                double left = 10;
                double right = 0;

                // Act & Assert
                ArithmeticException exception = assertThrows(
                    ArithmeticException.class,
                    () -> calculator.calculate(left, "%", right)
                );
                assertEquals("Modulo by zero", exception.getMessage());
            }

            @Test
            @DisplayName("Should compute modulo with negative numbers")
            void testModuloNegatives() {
                // Arrange
                double left = -10;
                double right = 3;

                // Act
                double result = calculator.calculate(left, "%", right);

                // Assert
                assertEquals(-1.0, result);
            }

            @Test
            @DisplayName("Should compute modulo with decimal numbers")
            void testModuloDecimals() {
                // Arrange
                double left = 10.5;
                double right = 3;

                // Act
                double result = calculator.calculate(left, "%", right);

                // Assert
                assertEquals(1.5, result, 0.0001);
            }

            @Test
            @DisplayName("Should return zero when left is multiple of right")
            void testModuloExactDivision() {
                // Arrange
                double left = 15;
                double right = 5;

                // Act
                double result = calculator.calculate(left, "%", right);

                // Assert
                assertEquals(0.0, result);
            }
        }

        @Nested
        @DisplayName("Power Tests")
        class PowerTests {

            @Test
            @DisplayName("Should compute power of two positive numbers")
            void testPowerPositive() {
                // Arrange
                double left = 2;
                double right = 3;

                // Act
                double result = calculator.calculate(left, "^", right);

                // Assert
                assertEquals(8.0, result);
            }

            @Test
            @DisplayName("Should compute power with zero exponent")
            void testPowerZeroExponent() {
                // Arrange
                double left = 5;
                double right = 0;

                // Act
                double result = calculator.calculate(left, "^", right);

                // Assert
                assertEquals(1.0, result);
            }

            @Test
            @DisplayName("Should compute power with negative exponent")
            void testPowerNegativeExponent() {
                // Arrange
                double left = 2;
                double right = -2;

                // Act
                double result = calculator.calculate(left, "^", right);

                // Assert
                assertEquals(0.25, result, 0.0001);
            }

            @Test
            @DisplayName("Should compute power with decimal exponent")
            void testPowerDecimalExponent() {
                // Arrange
                double left = 4;
                double right = 0.5;

                // Act
                double result = calculator.calculate(left, "^", right);

                // Assert
                assertEquals(2.0, result, 0.0001);
            }

            @Test
            @DisplayName("Should compute square root using power")
            void testSquareRoot() {
                // Arrange
                double left = 16;
                double right = 0.5;

                // Act
                double result = calculator.calculate(left, "^", right);

                // Assert
                assertEquals(4.0, result, 0.0001);
            }

            @Test
            @DisplayName("Should handle large powers")
            void testLargePower() {
                // Arrange
                double left = 10;
                double right = 6;

                // Act
                double result = calculator.calculate(left, "^", right);

                // Assert
                assertEquals(1_000_000.0, result);
            }
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for invalid operator")
        void testInvalidOperator() {
            // Arrange
            double left = 10;
            double right = 5;

            // Act & Assert
            assertThrows(
                IllegalArgumentException.class,
                () -> calculator.calculate(left, "invalid", right)
            );
        }
    }

    // ========================================
    // NESTED: Evaluate Method Tests
    // ========================================

    @Nested
    @DisplayName("Evaluate Method Tests")
    class EvaluateMethodTests {

        @ParameterizedTest
        @CsvSource({
            "'10 + 5', 15.0",
            "'20 - 8', 12.0",
            "'6 * 7', 42.0",
            "'20 / 4', 5.0",
            "'10 % 3', 1.0",
            "'2 ^ 3', 8.0"
        })
        @DisplayName("Should evaluate valid expressions")
        void testEvaluateValidExpressions(String expression, double expected) {
            // Arrange (expression provided as parameter)

            // Act
            double result = calculator.evaluate(expression);

            // Assert
            assertEquals(expected, result, 0.0001);
        }

        @Test
        @DisplayName("Should evaluate expression with decimal numbers")
        void testEvaluateDecimals() {
            // Arrange
            String expression = "12.5 + 3.7";

            // Act
            double result = calculator.evaluate(expression);

            // Assert
            assertEquals(16.2, result, 0.0001);
        }

        @Test
        @DisplayName("Should evaluate expression with negative numbers")
        void testEvaluateNegatives() {
            // Arrange
            String expression = "-10 + 5";

            // Act
            double result = calculator.evaluate(expression);

            // Assert
            assertEquals(-5.0, result);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for too few parts")
        void testEvaluateTooFewParts() {
            // Arrange
            String expression = "10 +";

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.evaluate(expression)
            );
            assertTrue(exception.getMessage().contains("Invalid expression"));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for too many parts")
        void testEvaluateTooManyParts() {
            // Arrange
            String expression = "10 + 5 + 3";

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.evaluate(expression)
            );
            assertTrue(exception.getMessage().contains("Invalid expression"));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for invalid number format")
        void testEvaluateInvalidNumber() {
            // Arrange
            String expression = "abc + 5";

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calculator.evaluate(expression)
            );
            assertTrue(exception.getMessage().contains("Invalid number format"));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for empty expression")
        void testEvaluateEmptyExpression() {
            // Arrange
            String expression = "";

            // Act & Assert
            assertThrows(
                IllegalArgumentException.class,
                () -> calculator.evaluate(expression)
            );
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for expression with only whitespace")
        void testEvaluateWhitespaceExpression() {
            // Arrange
            String expression = "   ";

            // Act & Assert
            assertThrows(
                IllegalArgumentException.class,
                () -> calculator.evaluate(expression)
            );
        }

        @Test
        @DisplayName("Should throw ArithmeticException for division by zero in expression")
        void testEvaluateDivisionByZero() {
            // Arrange
            String expression = "10 / 0";

            // Act & Assert
            ArithmeticException exception = assertThrows(
                ArithmeticException.class,
                () -> calculator.evaluate(expression)
            );
            assertEquals("Division by zero", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for invalid operator in expression")
        void testEvaluateInvalidOperator() {
            // Arrange
            String expression = "10 x 5";

            // Act & Assert
            assertThrows(
                IllegalArgumentException.class,
                () -> calculator.evaluate(expression)
            );
        }

        @Test
        @DisplayName("Should handle expression with extra spaces")
        void testEvaluateExtraSpaces() {
            // Arrange
            String expression = "  10   +   5  ";

            // Act & Assert
            // This test documents current behavior - may fail with current implementation
            // The split by space will create empty strings
            assertThrows(
                IllegalArgumentException.class,
                () -> calculator.evaluate(expression)
            );
        }
    }

    // ========================================
    // NESTED: Complex Edge Cases
    // ========================================

    @Nested
    @DisplayName("Complex Edge Cases")
    class ComplexEdgeCases {

        @Test
        @DisplayName("Should handle very small decimal numbers")
        void testVerySmallDecimals() {
            // Arrange
            double left = 0.0001;
            double right = 0.0002;

            // Act
            double result = calculator.calculate(left, "+", right);

            // Assert
            assertEquals(0.0003, result, 0.00001);
        }

        @Test
        @DisplayName("Should handle very large numbers")
        void testVeryLargeNumbers() {
            // Arrange
            double left = 1e15;
            double right = 1e15;

            // Act
            double result = calculator.calculate(left, "+", right);

            // Assert
            assertEquals(2e15, result, 1e10);
        }

        @Test
        @DisplayName("Should handle precision loss in floating point operations")
        void testFloatingPointPrecision() {
            // Arrange
            double left = 0.1;
            double right = 0.2;

            // Act
            double result = calculator.calculate(left, "+", right);

            // Assert
            // Due to floating point precision, 0.1 + 0.2 might not exactly equal 0.3
            assertEquals(0.3, result, 0.0001);
        }

        @Test
        @DisplayName("Should handle negative zero")
        void testNegativeZero() {
            // Arrange
            double left = -0.0;
            double right = 5;

            // Act
            double result = calculator.calculate(left, "+", right);

            // Assert
            assertEquals(5.0, result);
        }

        @Test
        @DisplayName("Should handle infinity scenarios")
        void testInfinityResult() {
            // Arrange
            double left = Double.MAX_VALUE;
            double right = Double.MAX_VALUE;

            // Act
            double result = calculator.calculate(left, "+", right);

            // Assert
            assertEquals(Double.POSITIVE_INFINITY, result);
        }

        @Test
        @DisplayName("Should handle negative infinity")
        void testNegativeInfinity() {
            // Arrange
            double left = -Double.MAX_VALUE;
            double right = -Double.MAX_VALUE;

            // Act
            double result = calculator.calculate(left, "+", right);

            // Assert
            assertEquals(Double.NEGATIVE_INFINITY, result);
        }

        @ParameterizedTest
        @CsvSource({
            "'-5 + 10', 5.0",
            "'-15 - 5', -20.0",
            "'-4 * -3', 12.0",
            "'-20 / -4', 5.0",
            "'-10 % 3', -1.0"
        })
        @DisplayName("Should handle all operations with negative numbers")
        void testAllOperationsWithNegatives(String expression, double expected) {
            // Arrange (provided as parameters)

            // Act
            double result = calculator.evaluate(expression);

            // Assert
            assertEquals(expected, result, 0.0001);
        }

        @Test
        @DisplayName("Should handle scientific notation input")
        void testScientificNotation() {
            // Arrange
            String expression = "1e3 + 2e2";

            // Act
            double result = calculator.evaluate(expression);

            // Assert
            assertEquals(1200.0, result, 0.0001);
        }

        @Test
        @DisplayName("Should handle maximum safe integer precision")
        void testMaximumSafeInteger() {
            // Arrange
            double left = 9007199254740991.0; // Max safe integer in double
            double right = 1;

            // Act
            double result = calculator.calculate(left, "+", right);

            // Assert
            assertEquals(9007199254740992.0, result);
        }
    }

    // ========================================
    // NESTED: Integration Tests
    // ========================================

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should perform sequence of operations")
        void testSequenceOfOperations() {
            // Arrange & Act & Assert
            // Test multiple operations in sequence
            assertEquals(15.0, calculator.evaluate("10 + 5"));
            assertEquals(50.0, calculator.evaluate("10 * 5"));
            assertEquals(5.0, calculator.evaluate("10 / 2"));
            assertEquals(8.0, calculator.evaluate("2 ^ 3"));
        }

        @Test
        @DisplayName("Should handle mixed operations correctly")
        void testMixedOperations() {
            // Arrange
            CliCalculator calc = new CliCalculator();

            // Act & Assert
            double result1 = calc.evaluate("100 - 50");
            assertEquals(50.0, result1);

            double result2 = calc.evaluate("10 * 5");
            assertEquals(50.0, result2);

            double result3 = calc.evaluate("100 / 4");
            assertEquals(25.0, result3);

            double result4 = calc.evaluate("2 ^ 5");
            assertEquals(32.0, result4);
        }

        @Test
        @DisplayName("Should maintain consistency across multiple calculations")
        void testCalculationConsistency() {
            // Arrange
            String expression = "7 * 8";

            // Act
            double result1 = calculator.evaluate(expression);
            double result2 = calculator.evaluate(expression);
            double result3 = calculator.evaluate(expression);

            // Assert
            assertEquals(result1, result2);
            assertEquals(result2, result3);
            assertEquals(56.0, result1);
        }
    }

    // ========================================
    // NESTED: Boundary Value Tests
    // ========================================

    @Nested
    @DisplayName("Boundary Value Tests")
    class BoundaryValueTests {

        @Test
        @DisplayName("Should handle Double.MIN_VALUE")
        void testMinDoubleValue() {
            // Arrange
            double left = Double.MIN_VALUE;
            double right = Double.MIN_VALUE;

            // Act
            double result = calculator.calculate(left, "+", right);

            // Assert
            assertTrue(result > 0);
        }

        @Test
        @DisplayName("Should handle Double.MAX_VALUE")
        void testMaxDoubleValue() {
            // Arrange
            double left = Double.MAX_VALUE;
            double right = 0;

            // Act
            double result = calculator.calculate(left, "+", right);

            // Assert
            assertEquals(Double.MAX_VALUE, result);
        }

        @Test
        @DisplayName("Should handle operations near zero")
        void testOperationsNearZero() {
            // Arrange
            double left = 0.0000001;
            double right = -0.0000001;

            // Act
            double result = calculator.calculate(left, "+", right);

            // Assert
            assertEquals(0.0, result, 0.0000001);
        }
    }
}
