package exercises;

import java.util.Scanner;

/**
 * CLI Calculator — a REPL-based arithmetic calculator.
 *
 * <p>Supports operators: +, -, *, /, %, ^ (power).
 * Maintains a history of the last 10 calculations.
 *
 * <p>Usage:
 * <pre>
 *   > 12.5 + 3
 *   = 15.5
 *   > history
 *   [1] 12.5 + 3 = 15.5
 *   > exit
 * </pre>
 */
public class CliCalculator {

    private static final int MAX_HISTORY = 10;
    private final String[] history = new String[MAX_HISTORY];
    private int historyCount = 0;

    public static void main(String[] args) {
        CliCalculator calculator = new CliCalculator();
        calculator.run();
    }

    /**
     * Main REPL loop. Reads user input, dispatches to parse/calculate/history.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("CLI Calculator — type an expression, 'history', or 'exit'");

        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();

            if (line.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye.");
                break;
            }

            if (line.equalsIgnoreCase("history")) {
                printHistory();
                continue;
            }

            try {
                double result = evaluate(line);
                String entry = line + " = " + result;
                addToHistory(entry);
                System.out.println("= " + result);
            } catch (ArithmeticException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid expression: " + e.getMessage());
            }
        }

        scanner.close();
    }

    /**
     * Parse and evaluate an arithmetic expression string.
     *
     * <p>For the basic version, expect the format: {@code operand operator operand}
     * (e.g., {@code 12.5 + 3}).
     *
     * @param expression the raw expression string
     * @return the computed result
     * @throws IllegalArgumentException if the expression cannot be parsed
     * @throws ArithmeticException      if division by zero is attempted
     */
    public double evaluate(String expression) {
        // TODO: Parse the expression into operands and operator.
        //       1. Split by whitespace or use a regex to extract parts.
        //       2. Validate that we have exactly two operands and one operator.
        //       3. Convert operand strings to doubles (handle NumberFormatException).
        //       4. Delegate to calculate(double, String, double).

        // BONUS: Support parenthesized sub-expressions and operator precedence.

        throw new UnsupportedOperationException("TODO: implement evaluate()");
    }

    /**
     * Perform a single arithmetic operation.
     *
     * @param left     the left operand
     * @param operator one of +, -, *, /, %, ^
     * @param right    the right operand
     * @return the result
     * @throws ArithmeticException      if dividing/modding by zero
     * @throws IllegalArgumentException if the operator is unknown
     */
    public double calculate(double left, String operator, double right) {
        // TODO: Implement each operator.
        //       - "+" → left + right
        //       - "-" → left - right
        //       - "*" → left * right
        //       - "/" → check right != 0, then left / right
        //       - "%" → check right != 0, then left % right
        //       - "^" → Math.pow(left, right)
        //       - else → throw IllegalArgumentException

        throw new UnsupportedOperationException("TODO: implement calculate()");
    }

    /**
     * Add a result entry to the circular history buffer.
     *
     * @param entry formatted as "expression = result"
     */
    private void addToHistory(String entry) {
        // TODO: Store entry in the history array.
        //       Use modular arithmetic to wrap around when MAX_HISTORY is reached.

        throw new UnsupportedOperationException("TODO: implement addToHistory()");
    }

    /**
     * Print all stored history entries, oldest first.
     */
    private void printHistory() {
        // TODO: Iterate over the history array and print non-null entries.
        //       Format: [1] 12.5 + 3 = 15.5

        throw new UnsupportedOperationException("TODO: implement printHistory()");
    }
}
