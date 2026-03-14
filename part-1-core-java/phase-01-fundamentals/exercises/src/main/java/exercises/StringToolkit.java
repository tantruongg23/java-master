package exercises;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * StringToolkit — a null-safe, performance-oriented string utility library.
 *
 * <p>All methods handle {@code null} and empty inputs gracefully.
 * Implementations must use {@link StringBuilder} — no {@code String}
 * concatenation in loops.
 */
public final class StringToolkit {

    private StringToolkit() {
        // Utility class — prevent instantiation
    }

    /**
     * Reverse the given string.
     *
     * @param input the string to reverse
     * @return the reversed string, {@code null} if input is {@code null},
     *         {@code ""} if input is empty
     */
    public static String reverse(String input) {
        // TODO: Use StringBuilder.reverse() or manual iteration.
        //       Remember to handle null and empty inputs first.
        if (isEmpty(input)) return input;
        StringBuilder output = new StringBuilder();
        for (int i = input.length() - 1; i >= 0; i--) {
            output.append(input.charAt(i));
        }
        return output.toString();
    }

    /**
     * Check whether the input is a palindrome (case-insensitive, ignoring
     * non-alphanumeric characters).
     *
     * @param input the string to check
     * @return {@code true} if palindrome, {@code false} if {@code null} or not a palindrome
     */
    public static boolean isPalindrome(String input) {
        // TODO: 1. Return false for null.
        //       2. Strip non-alphanumeric chars and convert to lowercase.
        //       3. Compare with its reverse (or use two-pointer approach).

        if (input == null || input.isEmpty()) {
            return false;
        }

        int left = 0;
        int right = input.length() - 1;

        while (left < right) {

            while (left < right && !Character.isLetterOrDigit(input.charAt(left))) {
                left++;
            }

            while (left < right && !Character.isLetterOrDigit(input.charAt(right))) {
                right--;
            }

            char l = Character.toLowerCase(input.charAt(left));
            char r = Character.toLowerCase(input.charAt(right));

            if (l != r) {
                return false;
            }

            left++;
            right--;
        }

        return true;
    }

    /**
     * Count the number of vowels (a, e, i, o, u) in the input, case-insensitive.
     *
     * @param input the string to scan
     * @return vowel count, {@code 0} if input is {@code null}
     */
    public static int countVowels(String input) {
        // TODO: Iterate over each char, check if it's a vowel.
        //       Use a Set or a String "aeiouAEIOU".contains(...) approach.
        if (isEmpty(input)) return 0;

        Set<Character> vowels = Set.of('a', 'e', 'i', 'o', 'u');
        int count = 0;
        for (char c : input.toCharArray()) {
            if (vowels.contains(Character.toLowerCase(c))) {
                count++;
            }
        }
        return count;
    }

    /**
     * Convert the input to Title Case (capitalize the first letter of each word).
     *
     * <p>Example: {@code "hello world"} → {@code "Hello World"}
     *
     * @param input the string to convert
     * @return title-cased string, or {@code null}/{@code ""} for null/empty input
     */
    public static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder output = new StringBuilder(input.length());
        boolean capitalizeNext = true;

        for (char c : input.toCharArray()) {

            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                output.append(c);
            } else if (capitalizeNext) {
                output.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                output.append(c);
            }
        }

        return output.toString();
    }

    public static boolean isEmpty(String input) {
        return input == null || input.isBlank();
    }

    /**
     * Compress the string using run-length encoding.
     *
     * <p>Example: {@code "aaabbc"} → {@code "a3b2c1"}
     *
     * <p>If the compressed form is not shorter than the original, return the original.
     *
     * @param input the string to compress
     * @return compressed string, or the original if compression doesn't save space;
     *         {@code null} if input is {@code null}
     */
    public static String compress(String input) {
        // TODO: 1. Walk through the string tracking current char and its count.
        //       2. Append char + count to a StringBuilder when the char changes.
        //       3. Compare lengths; return the shorter one.
        if (isEmpty(input)) return input;

        StringBuilder output = new StringBuilder();
        int count = 1;
        for (int i = 1; i < input.length(); i++) {

            if (input.charAt(i) == input.charAt(i - 1)) {
                count++;
            } else {
                output.append(input.charAt(i - 1)).append(count);
                count = 1;
            }
        }

        // append last character group
        output.append(input.charAt(input.length() - 1)).append(count);

        return output.length() < input.length() ? output.toString() : input;
    }

    /**
     * Find the most frequently occurring character in the string.
     *
     * <p>In case of a tie, return the character that appears first.
     *
     * @param input the string to analyze
     * @return the most frequent character
     * @throws IllegalArgumentException if input is {@code null} or empty
     */
    public static char mostFrequentChar(String input) {
        // TODO: 1. Validate input (throw IllegalArgumentException if null/empty).
        //       2. Count occurrences of each char (array of size 128 or a Map).
        //       3. Track the max count AND the earliest index of the max char.
        if (isEmpty(input)) throw new IllegalArgumentException("Input cannot be empty");
        LinkedHashMap<Character, Integer> counts = new LinkedHashMap<>();
        for (char c : input.toCharArray()) {
            counts.put(c, counts.getOrDefault(c, 0) + 1);
        }
        int maxCount = 0;
        char maxChar = ' ';
        for (char c : counts.keySet()) {
            if (counts.get(c) > maxCount) {
                maxCount = counts.get(c);
                maxChar = c;
            }
        }
        return maxChar;
    }

    /**
     * Benchmark String concatenation ({@code +=}) vs {@link StringBuilder#append}.
     *
     * <p>Prints elapsed time in milliseconds for each approach.
     *
     * @param iterations number of concatenations to perform
     */
    public static void benchmarkConcat(int iterations) {
        // BONUS TODO:
        //   1. Time String += in a loop for 'iterations' rounds.
        //   2. Time StringBuilder.append() for the same number.
        //   3. Print both durations and the ratio.
        Instant start = Instant.now();
        String str = "";
        for (int i = 0; i < iterations; i++) {
            str += 'a';
        }
        Instant end = Instant.now();
        System.out.println("String +=: " + (end.toEpochMilli() - start.toEpochMilli()) + " ms");

        start = Instant.now();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < iterations; i++) {
            builder.append('a');
        }
        end = Instant.now();
        System.out.println("String +=: " + (end.toEpochMilli() - start.toEpochMilli()) + " ms");
    }
}
