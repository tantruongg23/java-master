package exercises;

import java.util.Objects;
import java.util.Scanner;
import java.util.Stack;

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

enum Operator {
    PLUS("+"), MINUS("-"), MULTIPLY("*"), DIVIDE("/"), MODULO("%"), POWER("^");
    private final String symbol;
    Operator(String symbol) {
        this.symbol = symbol;
    }
    public String getSymbol() {
        return symbol;
    }
    public static Operator fromSymbol(String symbol) {
        for (Operator op : Operator.values()) {
            if (op.getSymbol().equals(symbol)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown operator: " + symbol);
    }
}

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
        try {
            expression = expression.trim();
            Stack<String> stack = new Stack<>();
            int i = 0;
            StringBuilder sub = new StringBuilder();
            while (i < expression.length()) {
                if (expression.charAt(i) == '(') {
                    stack.push("(");
                    while (i < expression.length() && expression.charAt(i) != ')') {
                        sub.append(expression.charAt(i));
                        i++;
                    }
                }
                else if (expression.charAt(i) == ')') {

                }
                else if (expression.charAt(i) == '+') {

                }
                else if (expression.charAt(i) == '-') {

                }

            }
            String[] parts = expression.split(" ");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid expression");
            }
            double left = Double.parseDouble(parts[0]);
            String operator = parts[1];
            double right = Double.parseDouble(parts[2]);
            return calculate(left, operator, right);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format");
        }
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
        return switch (Operator.fromSymbol(operator)) {
            case Operator.PLUS -> left + right;
            case Operator.MINUS -> left - right;
            case Operator.MULTIPLY -> left * right;
            case Operator.DIVIDE -> {
                if (right == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                yield left / right;
            }
            case Operator.MODULO -> {
                if (right == 0) {
                    throw new ArithmeticException("Modulo by zero");
                }
                yield left % right;
            }
            case Operator.POWER -> Math.pow(left, right);
        };
    }

    /**
     * Add a result entry to the circular history buffer.
     *
     * @param entry formatted as "expression = result"
     */
    private void addToHistory(String entry) {
        history[historyCount % MAX_HISTORY] = entry;
        historyCount++;
        if (historyCount == MAX_HISTORY) {
            historyCount = 0;
        }
    }

    /**
     * Print all stored history entries, oldest first.
     */
    private void printHistory() {
        // Format: [1] 12.5 + 3 = 15.5
        for (int i = 0; i < history.length; i++) {
            if (Objects.nonNull(history[i])) {
                System.out.println("[" + i + 1 + "] " + history[i]);
            }
        }
    }
}
