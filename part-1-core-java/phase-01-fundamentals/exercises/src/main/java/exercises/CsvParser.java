package exercises;

import java.util.ArrayList;
import java.util.List;

/**
 * CsvParser — parses a CSV-formatted string into a 2D String array.
 *
 * <p>Handles quoted fields, escaped quotes, empty fields, and trailing commas.
 *
 * <p>Usage:
 * <pre>
 *   String csv = "name,age,city\nAlice,30,\"New York\"\nBob,25,London";
 *   String[][] data = new CsvParser().parse(csv);
 *   // data[0] = ["name", "age", "city"]
 *   // data[1] = ["Alice", "30", "New York"]
 *   // data[2] = ["Bob", "25", "London"]
 * </pre>
 */
public class CsvParser {

    private static final char DEFAULT_DELIMITER = ',';

    /**
     * Parse a CSV string using the default comma delimiter.
     *
     * @param csv the raw CSV content (rows separated by {@code \n})
     * @return a 2D array where {@code result[row][col]} is the field value
     * @throws IllegalArgumentException if csv is {@code null}
     */
    public String[][] parse(String csv) {
        return parse(csv, DEFAULT_DELIMITER);
    }

    /**
     * Parse a CSV string using a custom delimiter.
     *
     * <p>Rules:
     * <ul>
     *   <li>Fields may be enclosed in double quotes.</li>
     *   <li>A double-quote inside a quoted field is escaped by doubling it:
     *       {@code "He said ""hi"""} → {@code He said "hi"}</li>
     *   <li>Empty fields are preserved: {@code a,,c} → {@code ["a", "", "c"]}</li>
     *   <li>A trailing delimiter produces a trailing empty field.</li>
     * </ul>
     *
     * @param csv       the raw CSV content
     * @param delimiter the field delimiter character
     * @return a 2D array of parsed fields
     * @throws IllegalArgumentException if csv is {@code null}
     */
    public String[][] parse(String csv, char delimiter) {
        // TODO: Implement the CSV parsing logic.
        //
        //   High-level approach:
        //   1. Validate input (throw on null).
        //   2. Split into lines — but be careful: newlines inside quoted fields
        //      should NOT split the row (bonus).
        //   3. For each line, walk character by character:
        //      a. If the char is a double-quote, enter "quoted mode".
        //         In quoted mode, the delimiter and newline are literal text.
        //         A pair of double-quotes ("") is an escaped quote.
        //      b. If the char is the delimiter (and not in quoted mode), finalize
        //         the current field and start a new one.
        //      c. Otherwise, append the char to the current field buffer.
        //   4. Collect fields into a List<String> per row, rows into a
        //      List<List<String>>, then convert to String[][].
        //
        //   Edge cases to test:
        //   - Empty string → empty array or single empty row
        //   - Only delimiters: ",," → ["", "", ""]
        //   - Quoted field with commas: "\"a,b\",c" → ["a,b", "c"]
        //   - Escaped quotes: "\"He said \"\"hi\"\"\"" → [He said "hi"]

        throw new UnsupportedOperationException("TODO: implement parse()");
    }
}
