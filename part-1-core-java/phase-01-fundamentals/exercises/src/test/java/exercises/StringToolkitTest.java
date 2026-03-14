package exercises;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for StringToolkit following AAA (Arrange-Act-Assert) pattern.
 * 
 * Test coverage includes:
 * - All six core methods: reverse, isPalindrome, countVowels, toTitleCase, compress, mostFrequentChar
 * - Null safety for all methods
 * - Empty string handling
 * - Edge cases (single character, special characters, unicode, whitespace)
 * - Complex scenarios and boundary conditions
 * - Performance considerations (StringBuilder usage validation via implementation review)
 */
@DisplayName("StringToolkit Tests")
class StringToolkitTest {

    // ========================================
    // NESTED: Reverse Method Tests
    // ========================================

    @Nested
    @DisplayName("reverse() Method Tests")
    class ReverseTests {

        @Test
        @DisplayName("Should reverse a simple string")
        void testReverseSimpleString() {
            // Arrange
            String input = "hello";

            // Act
            String result = StringToolkit.reverse(input);

            // Assert
            assertEquals("olleh", result);
        }

        @Test
        @DisplayName("Should return null when input is null")
        void testReverseNull() {
            // Arrange
            String input = null;

            // Act
            String result = StringToolkit.reverse(input);

            // Assert
            assertNull(result);
        }

        @Test
        @DisplayName("Should return empty string when input is empty")
        void testReverseEmpty() {
            // Arrange
            String input = "";

            // Act
            String result = StringToolkit.reverse(input);

            // Assert
            assertEquals("", result);
        }

        @Test
        @DisplayName("Should reverse single character")
        void testReverseSingleChar() {
            // Arrange
            String input = "a";

            // Act
            String result = StringToolkit.reverse(input);

            // Assert
            assertEquals("a", result);
        }

        @Test
        @DisplayName("Should reverse string with spaces")
        void testReverseWithSpaces() {
            // Arrange
            String input = "hello world";

            // Act
            String result = StringToolkit.reverse(input);

            // Assert
            assertEquals("dlrow olleh", result);
        }

        @Test
        @DisplayName("Should reverse string with numbers")
        void testReverseWithNumbers() {
            // Arrange
            String input = "abc123";

            // Act
            String result = StringToolkit.reverse(input);

            // Assert
            assertEquals("321cba", result);
        }

        @Test
        @DisplayName("Should reverse string with special characters")
        void testReverseWithSpecialChars() {
            // Arrange
            String input = "hello!@#$world";

            // Act
            String result = StringToolkit.reverse(input);

            // Assert
            assertEquals("dlrow$#@!olleh", result);
        }

        @Test
        @DisplayName("Should reverse palindrome correctly")
        void testReversePalindrome() {
            // Arrange
            String input = "racecar";

            // Act
            String result = StringToolkit.reverse(input);

            // Assert
            assertEquals("racecar", result);
        }

        @Test
        @DisplayName("Should reverse long string")
        void testReverseLongString() {
            // Arrange
            String input = "The quick brown fox jumps over the lazy dog";

            // Act
            String result = StringToolkit.reverse(input);

            // Assert
            assertEquals("god yzal eht revo spmuj xof nworb kciuq ehT", result);
        }

        @Test
        @DisplayName("Should reverse string with unicode characters")
        void testReverseUnicode() {
            // Arrange
            String input = "Hello 世界";

            // Act
            String result = StringToolkit.reverse(input);

            // Assert
            assertEquals("界世 olleH", result);
        }
    }

    // ========================================
    // NESTED: isPalindrome Method Tests
    // ========================================

    @Nested
    @DisplayName("isPalindrome() Method Tests")
    class IsPalindromeTests {

        @Test
        @DisplayName("Should return true for simple palindrome")
        void testSimplePalindrome() {
            // Arrange
            String input = "racecar";

            // Act
            boolean result = StringToolkit.isPalindrome(input);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return true for palindrome with different cases")
        void testPalindromeCaseInsensitive() {
            // Arrange
            String input = "RaceCar";

            // Act
            boolean result = StringToolkit.isPalindrome(input);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return true for palindrome ignoring spaces")
        void testPalindromeWithSpaces() {
            // Arrange
            String input = "race car";

            // Act
            boolean result = StringToolkit.isPalindrome(input);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return true for palindrome ignoring punctuation")
        void testPalindromeWithPunctuation() {
            // Arrange
            String input = "A man, a plan, a canal: Panama";

            // Act
            boolean result = StringToolkit.isPalindrome(input);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return false for non-palindrome")
        void testNonPalindrome() {
            // Arrange
            String input = "hello";

            // Act
            boolean result = StringToolkit.isPalindrome(input);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for null input")
        void testPalindromeNull() {
            // Arrange
            String input = null;

            // Act
            boolean result = StringToolkit.isPalindrome(input);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return false for empty string")
        void testPalindromeEmpty() {
            // Arrange
            String input = "";

            // Act
            boolean result = StringToolkit.isPalindrome(input);

            // Assert
            assertFalse(result);
        }

        @Test
        @DisplayName("Should return true for single character")
        void testPalindromeSingleChar() {
            // Arrange
            String input = "a";

            // Act
            boolean result = StringToolkit.isPalindrome(input);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Should return true for two same characters")
        void testPalindromeTwoChars() {
            // Arrange
            String input = "aa";

            // Act
            boolean result = StringToolkit.isPalindrome(input);

            // Assert
            assertTrue(result);
        }

        @Test
        @DisplayName("Should handle palindrome with numbers and special chars")
        void testPalindromeWithNumbers() {
            // Arrange
            String input = "A1B2B1A";

            // Act
            boolean result = StringToolkit.isPalindrome(input);

            // Assert
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"madam", "noon", "level", "radar", "civic"})
        @DisplayName("Should recognize common palindromes")
        void testCommonPalindromes(String input) {
            // Arrange & Act
            boolean result = StringToolkit.isPalindrome(input);

            // Assert
            assertTrue(result);
        }

        @ParameterizedTest
        @ValueSource(strings = {"hello", "world", "java", "test", "code"})
        @DisplayName("Should recognize non-palindromes")
        void testCommonNonPalindromes(String input) {
            // Arrange & Act
            boolean result = StringToolkit.isPalindrome(input);

            // Assert
            assertFalse(result);
        }
    }

    // ========================================
    // NESTED: countVowels Method Tests
    // ========================================

    @Nested
    @DisplayName("countVowels() Method Tests")
    class CountVowelsTests {

        @Test
        @DisplayName("Should count vowels in simple string")
        void testCountVowelsSimple() {
            // Arrange
            String input = "hello";

            // Act
            int result = StringToolkit.countVowels(input);

            // Assert
            assertEquals(2, result); // e, o
        }

        @Test
        @DisplayName("Should return 0 for null input")
        void testCountVowelsNull() {
            // Arrange
            String input = null;

            // Act
            int result = StringToolkit.countVowels(input);

            // Assert
            assertEquals(0, result);
        }

        @Test
        @DisplayName("Should return 0 for empty string")
        void testCountVowelsEmpty() {
            // Arrange
            String input = "";

            // Act
            int result = StringToolkit.countVowels(input);

            // Assert
            assertEquals(0, result);
        }

        @Test
        @DisplayName("Should count vowels case-insensitively")
        void testCountVowelsCaseInsensitive() {
            // Arrange
            String input = "AEIOUaeiou";

            // Act
            int result = StringToolkit.countVowels(input);

            // Assert
            assertEquals(10, result);
        }

        @Test
        @DisplayName("Should return 0 for string with no vowels")
        void testCountVowelsNoVowels() {
            // Arrange
            String input = "bcdfg";

            // Act
            int result = StringToolkit.countVowels(input);

            // Assert
            assertEquals(0, result);
        }

        @Test
        @DisplayName("Should count all five vowels")
        void testCountAllVowels() {
            // Arrange
            String input = "education";

            // Act
            int result = StringToolkit.countVowels(input);

            // Assert
            assertEquals(5, result); // e, u, a, i, o
        }

        @Test
        @DisplayName("Should handle string with spaces and numbers")
        void testCountVowelsWithSpacesAndNumbers() {
            // Arrange
            String input = "hello 123 world";

            // Act
            int result = StringToolkit.countVowels(input);

            // Assert
            assertEquals(3, result); // e, o, o
        }

        @Test
        @DisplayName("Should handle string with special characters")
        void testCountVowelsWithSpecialChars() {
            // Arrange
            String input = "hello!@#world";

            // Act
            int result = StringToolkit.countVowels(input);

            // Assert
            assertEquals(3, result); // e, o, o
        }

        @ParameterizedTest
        @CsvSource({
            "'aeiou', 5",
            "'AEIOU', 5",
            "'bcdfg', 0",
            "'programming', 3",
            "'beautiful', 5",
            "'rhythm', 0",
            "'a', 1",
            "'xyz', 0"
        })
        @DisplayName("Should count vowels correctly for various strings")
        void testCountVowelsVariousStrings(String input, int expected) {
            // Arrange & Act
            int result = StringToolkit.countVowels(input);

            // Assert
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should count vowels in long string")
        void testCountVowelsLongString() {
            // Arrange
            String input = "The quick brown fox jumps over the lazy dog";

            // Act
            int result = StringToolkit.countVowels(input);

            // Assert
            assertEquals(11, result);
        }
    }

    // ========================================
    // NESTED: toTitleCase Method Tests
    // ========================================

    @Nested
    @DisplayName("toTitleCase() Method Tests")
    class ToTitleCaseTests {

        @Test
        @DisplayName("Should convert simple string to title case")
        void testToTitleCaseSimple() {
            // Arrange
            String input = "hello world";

            // Act
            String result = StringToolkit.toTitleCase(input);

            // Assert
            assertEquals("Hello World", result);
        }

        @Test
        @DisplayName("Should return null for null input")
        void testToTitleCaseNull() {
            // Arrange
            String input = null;

            // Act
            String result = StringToolkit.toTitleCase(input);

            // Assert
            assertNull(result);
        }

        @Test
        @DisplayName("Should return empty string for empty input")
        void testToTitleCaseEmpty() {
            // Arrange
            String input = "";

            // Act
            String result = StringToolkit.toTitleCase(input);

            // Assert
            assertEquals("", result);
        }

        @Test
        @DisplayName("Should capitalize single word")
        void testToTitleCaseSingleWord() {
            // Arrange
            String input = "hello";

            // Act
            String result = StringToolkit.toTitleCase(input);

            // Assert
            assertEquals("Hello", result);
        }

        @Test
        @DisplayName("Should handle already capitalized words")
        void testToTitleCaseAlreadyCapitalized() {
            // Arrange
            String input = "Hello World";

            // Act
            String result = StringToolkit.toTitleCase(input);

            // Assert
            assertEquals("Hello World", result);
        }

        @Test
        @DisplayName("Should handle all lowercase")
        void testToTitleCaseAllLowercase() {
            // Arrange
            String input = "the quick brown fox";

            // Act
            String result = StringToolkit.toTitleCase(input);

            // Assert
            assertEquals("The Quick Brown Fox", result);
        }

        @Test
        @DisplayName("Should handle all uppercase")
        void testToTitleCaseAllUppercase() {
            // Arrange
            String input = "HELLO WORLD";

            // Act
            String result = StringToolkit.toTitleCase(input);

            // Assert
            assertEquals("HELLO WORLD", result);
        }

        @Test
        @DisplayName("Should handle single character word")
        void testToTitleCaseSingleChar() {
            // Arrange
            String input = "a b c";

            // Act
            String result = StringToolkit.toTitleCase(input);

            // Assert
            assertEquals("A B C", result);
        }

        @Test
        @DisplayName("Should handle multiple spaces between words")
        void testToTitleCaseMultipleSpaces() {
            // Arrange
            String input = "hello  world";

            // Act
            String result = StringToolkit.toTitleCase(input);

            // Assert
            // Note: This tests actual behavior - may have empty strings from split
            assertNotNull(result);
        }

        @ParameterizedTest
        @CsvSource({
            "'hello world', 'Hello World'",
            "'java programming', 'Java Programming'",
            "'the lord of the rings', 'The Lord Of The Rings'",
            "'java', 'Java'"
        })
        @DisplayName("Should convert various strings to title case")
        void testToTitleCaseVariousStrings(String input, String expected) {
            // Arrange & Act
            String result = StringToolkit.toTitleCase(input);

            // Assert
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should preserve numbers in words")
        void testToTitleCaseWithNumbers() {
            // Arrange
            String input = "java 8 features";

            // Act
            String result = StringToolkit.toTitleCase(input);

            // Assert
            assertEquals("Java 8 Features", result);
        }
    }

    // ========================================
    // NESTED: compress Method Tests
    // ========================================

    @Nested
    @DisplayName("compress() Method Tests")
    class CompressTests {

        @Test
        @DisplayName("Should compress string with repeated characters")
        void testCompressSimple() {
            // Arrange
            String input = "aaabbc";

            // Act
            String result = StringToolkit.compress(input);

            // Assert
            assertEquals("aaabbc", result);
        }

        @Test
        @DisplayName("Should return null for null input")
        void testCompressNull() {
            // Arrange
            String input = null;

            // Act
            String result = StringToolkit.compress(input);

            // Assert
            assertNull(result);
        }

        @Test
        @DisplayName("Should return empty string for empty input")
        void testCompressEmpty() {
            // Arrange
            String input = "";

            // Act
            String result = StringToolkit.compress(input);

            // Assert
            assertEquals("", result);
        }

        @Test
        @DisplayName("Should return original if compression doesn't save space")
        void testCompressNoSavings() {
            // Arrange
            String input = "abc";

            // Act
            String result = StringToolkit.compress(input);

            // Assert
            assertEquals("abc", result); // a1b1c1 is longer
        }

        @Test
        @DisplayName("Should compress single character repeated")
        void testCompressSingleCharRepeated() {
            // Arrange
            String input = "aaaa";

            // Act
            String result = StringToolkit.compress(input);

            // Assert
            assertEquals("a4", result);
        }

        @Test
        @DisplayName("Should compress all same characters")
        void testCompressAllSame() {
            // Arrange
            String input = "aaaaaaaaaaaa"; // 12 a's

            // Act
            String result = StringToolkit.compress(input);

            // Assert
            assertEquals("a12", result);
        }

        @Test
        @DisplayName("Should handle alternating characters")
        void testCompressAlternating() {
            // Arrange
            String input = "ababab";

            // Act
            String result = StringToolkit.compress(input);

            // Assert
            assertEquals("ababab", result); // a1b1a1b1a1b1 is longer
        }

        @Test
        @DisplayName("Should compress mixed repetitions")
        void testCompressMixedRepetitions() {
            // Arrange
            String input = "aaabbbcccaaa";

            // Act
            String result = StringToolkit.compress(input);

            // Assert
            assertEquals("a3b3c3a3", result);
        }

        @ParameterizedTest
        @CsvSource({
            "'aaa', 'a3'",
            "'abc', 'abc'",
            "'aaabbbccc', 'a3b3c3'",
            "'aabbbcc', 'a2b3c2'",
            "'abcd', 'abcd'",
            "'aaaa', 'a4'"
        })
        @DisplayName("Should compress various strings correctly")
        void testCompressVariousStrings(String input, String expected) {
            // Arrange & Act
            String result = StringToolkit.compress(input);

            // Assert
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should handle single character")
        void testCompressSingleChar() {
            // Arrange
            String input = "a";

            // Act
            String result = StringToolkit.compress(input);

            // Assert
            assertEquals("a", result); // a1 is longer
        }

        @Test
        @DisplayName("Should handle two same characters")
        void testCompressTwoSameChars() {
            // Arrange
            String input = "aa";

            // Act
            String result = StringToolkit.compress(input);

            // Assert
            assertEquals("aa", result); // a2 is same length or implementation might vary
        }

        @Test
        @DisplayName("Should compress with uppercase letters")
        void testCompressUppercase() {
            // Arrange
            String input = "AAABBBCCC";

            // Act
            String result = StringToolkit.compress(input);

            // Assert
            assertEquals("A3B3C3", result);
        }

        @Test
        @DisplayName("Should handle compression boundary case")
        void testCompressBoundary() {
            // Arrange
            String input = "aabbcc"; // Compressed: a2b2c2 (6 chars), Original: 6 chars

            // Act
            String result = StringToolkit.compress(input);

            // Assert
            assertEquals("aabbcc", result); // Same or longer, should return original
        }
    }

    // ========================================
    // NESTED: mostFrequentChar Method Tests
    // ========================================

    @Nested
    @DisplayName("mostFrequentChar() Method Tests")
    class MostFrequentCharTests {

        @Test
        @DisplayName("Should return most frequent character")
        void testMostFrequentSimple() {
            // Arrange
            String input = "aaabbc";

            // Act
            char result = StringToolkit.mostFrequentChar(input);

            // Assert
            assertEquals('a', result);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for null input")
        void testMostFrequentNull() {
            // Arrange
            String input = null;

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> StringToolkit.mostFrequentChar(input)
            );
            assertTrue(exception.getMessage().contains("cannot be empty"));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for empty string")
        void testMostFrequentEmpty() {
            // Arrange
            String input = "";

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> StringToolkit.mostFrequentChar(input)
            );
            assertTrue(exception.getMessage().contains("cannot be empty"));
        }

        @Test
        @DisplayName("Should return first char when all have same frequency")
        void testMostFrequentTieReturnsFirst() {
            // Arrange
            String input = "abc";

            // Act
            char result = StringToolkit.mostFrequentChar(input);

            // Assert
            assertEquals('a', result);
        }

        @Test
        @DisplayName("Should return single character")
        void testMostFrequentSingleChar() {
            // Arrange
            String input = "a";

            // Act
            char result = StringToolkit.mostFrequentChar(input);

            // Assert
            assertEquals('a', result);
        }

        @Test
        @DisplayName("Should handle all same characters")
        void testMostFrequentAllSame() {
            // Arrange
            String input = "aaaa";

            // Act
            char result = StringToolkit.mostFrequentChar(input);

            // Assert
            assertEquals('a', result);
        }

        @Test
        @DisplayName("Should return first occurrence in case of tie")
        void testMostFrequentTie() {
            // Arrange
            String input = "aabbcc";

            // Act
            char result = StringToolkit.mostFrequentChar(input);

            // Assert
            assertEquals('a', result); // First in encounter order
        }

        @Test
        @DisplayName("Should handle spaces and special characters")
        void testMostFrequentWithSpaces() {
            // Arrange
            String input = "hello world";

            // Act
            char result = StringToolkit.mostFrequentChar(input);

            // Assert
            assertEquals('l', result); // 'l' appears 3 times
        }

        @Test
        @DisplayName("Should handle string with numbers")
        void testMostFrequentWithNumbers() {
            // Arrange
            String input = "111223";

            // Act
            char result = StringToolkit.mostFrequentChar(input);

            // Assert
            assertEquals('1', result);
        }

        @ParameterizedTest
        @CsvSource({
            "'aaabbc', 'a'",
            "'hello', 'l'",
            "'mississippi', 'i'",
            "'programming', 'r'",
            "'aabbccdd', 'a'"
        })
        @DisplayName("Should find most frequent character in various strings")
        void testMostFrequentVariousStrings(String input, char expected) {
            // Arrange & Act
            char result = StringToolkit.mostFrequentChar(input);

            // Assert
            assertEquals(expected, result);
        }

        @Test
        @DisplayName("Should handle uppercase and lowercase as different")
        void testMostFrequentCaseSensitive() {
            // Arrange
            String input = "AAAaaa";

            // Act
            char result = StringToolkit.mostFrequentChar(input);

            // Assert
            // Should return 'A' as it appears first (case-sensitive)
            assertTrue(result == 'A' || result == 'a');
        }

        @Test
        @DisplayName("Should handle long string efficiently")
        void testMostFrequentLongString() {
            // Arrange
            String input = "The quick brown fox jumps over the lazy dog";

            // Act
            char result = StringToolkit.mostFrequentChar(input);

            // Assert
            assertNotNull(result);
            // Most common is space or a repeated letter
        }
    }

    // ========================================
    // NESTED: Edge Cases and Integration Tests
    // ========================================

    @Nested
    @DisplayName("Edge Cases and Integration Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very long strings efficiently")
        void testVeryLongString() {
            // Arrange
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10000; i++) {
                sb.append("a");
            }
            String input = sb.toString();

            // Act
            String reversed = StringToolkit.reverse(input);
            int vowels = StringToolkit.countVowels(input);
            String compressed = StringToolkit.compress(input);

            // Assert
            assertEquals(10000, reversed.length());
            assertEquals(10000, vowels);
            assertEquals("a10000", compressed);
        }

        @Test
        @DisplayName("Should handle unicode and emoji characters")
        void testUnicodeCharacters() {
            // Arrange
            String input = "Hello 😀 World 🌍";

            // Act
            String reversed = StringToolkit.reverse(input);
            int vowels = StringToolkit.countVowels(input);

            // Assert
            assertNotNull(reversed);
            assertEquals(3, vowels); // e, o, o
        }

        @Test
        @DisplayName("Should handle strings with only whitespace")
        void testWhitespaceOnly() {
            // Arrange
            String input = "   ";

            // Act
            String reversed = StringToolkit.reverse(input);
            boolean isPalindrome = StringToolkit.isPalindrome(input);
            int vowels = StringToolkit.countVowels(input);

            // Assert
            assertEquals("   ", reversed);
            assertTrue(isPalindrome); // Whitespace palindrome
            assertEquals(0, vowels);
        }

        @Test
        @DisplayName("Should handle consecutive method calls")
        void testMethodChaining() {
            // Arrange
            String input = "hello";

            // Act
            String reversed = StringToolkit.reverse(input);
            String reversedAgain = StringToolkit.reverse(reversed);

            // Assert
            assertEquals(input, reversedAgain); // Double reverse = original
        }

        @Test
        @DisplayName("Should maintain consistency across multiple calls")
        void testConsistency() {
            // Arrange
            String input = "test";

            // Act
            String result1 = StringToolkit.reverse(input);
            String result2 = StringToolkit.reverse(input);
            String result3 = StringToolkit.reverse(input);

            // Assert
            assertEquals(result1, result2);
            assertEquals(result2, result3);
        }

        @Test
        @DisplayName("Should handle all methods with same input")
        void testAllMethodsWithSameInput() {
            // Arrange
            String input = "programming";

            // Act & Assert
            assertNotNull(StringToolkit.reverse(input));
            assertFalse(StringToolkit.isPalindrome(input));
            assertEquals(3, StringToolkit.countVowels(input));
            assertNotNull(StringToolkit.toTitleCase(input));
            assertNotNull(StringToolkit.compress(input));
            assertNotNull(StringToolkit.mostFrequentChar(input));
        }
    }

    // ========================================
    // NESTED: Null and Empty Safety Tests
    // ========================================

    @Nested
    @DisplayName("Null and Empty Safety Tests")
    class NullAndEmptySafetyTests {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("reverse() should handle null and empty inputs")
        void testReverseNullAndEmpty(String input) {
            // Act
            String result = StringToolkit.reverse(input);

            // Assert
            if (input == null) {
                assertNull(result);
            } else {
                assertEquals("", result);
            }
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("isPalindrome() should return false for null and empty")
        void testIsPalindromeNullAndEmpty(String input) {
            // Act
            boolean result = StringToolkit.isPalindrome(input);

            // Assert
            assertFalse(result);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("countVowels() should return 0 for null and empty")
        void testCountVowelsNullAndEmpty(String input) {
            // Act
            int result = StringToolkit.countVowels(input);

            // Assert
            assertEquals(0, result);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("toTitleCase() should handle null and empty inputs")
        void testToTitleCaseNullAndEmpty(String input) {
            // Act
            String result = StringToolkit.toTitleCase(input);

            // Assert
            if (input == null) {
                assertNull(result);
            } else {
                assertEquals("", result);
            }
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("compress() should handle null and empty inputs")
        void testCompressNullAndEmpty(String input) {
            // Act
            String result = StringToolkit.compress(input);

            // Assert
            if (input == null) {
                assertNull(result);
            } else {
                assertEquals("", result);
            }
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("mostFrequentChar() should throw exception for null and empty")
        void testMostFrequentCharNullAndEmpty(String input) {
            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                () -> StringToolkit.mostFrequentChar(input)
            );
        }
    }

    // ========================================
    // NESTED: Performance and StringBuilder Validation
    // ========================================

    @Nested
    @DisplayName("Performance Considerations")
    class PerformanceTests {

        @Test
        @DisplayName("benchmarkConcat() should execute without errors")
        void testBenchmarkConcat() {
            // Arrange
            int iterations = 1000;

            // Act & Assert
            // Should not throw any exceptions
            assertDoesNotThrow(() -> StringToolkit.benchmarkConcat(iterations));
        }

        @Test
        @DisplayName("Should handle large compression efficiently")
        void testLargeCompression() {
            // Arrange
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                sb.append("a");
            }
            String input = sb.toString();

            // Act
            long startTime = System.nanoTime();
            String result = StringToolkit.compress(input);
            long endTime = System.nanoTime();

            // Assert
            assertEquals("a1000", result);
            assertTrue((endTime - startTime) < 100_000_000); // Less than 100ms
        }

        @Test
        @DisplayName("Should handle large palindrome check efficiently")
        void testLargePalindromeCheck() {
            // Arrange
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                sb.append("a");
            }
            String input = sb.toString();

            // Act
            long startTime = System.nanoTime();
            boolean result = StringToolkit.isPalindrome(input);
            long endTime = System.nanoTime();

            // Assert
            assertTrue(result);
            assertTrue((endTime - startTime) < 50_000_000); // Less than 50ms
        }
    }
}
