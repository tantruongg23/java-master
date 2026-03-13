package exercises;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Exercise 2 — File Transformer
 *
 * <p>Read CSV files and transform them to JSON or XML output.
 * Handle different encodings, use try-with-resources everywhere,
 * and support streaming for large files.</p>
 *
 * <h3>Bonus</h3>
 * Read CSV from ZIP archives.
 */
public class FileTransformer {

    // ──────────────────────────────────────────────────────────────
    // Output format enum
    // ──────────────────────────────────────────────────────────────

    public enum Format {
        CSV, JSON, XML
    }

    // ──────────────────────────────────────────────────────────────
    // Custom Exception Hierarchy
    // ──────────────────────────────────────────────────────────────

    /**
     * Base exception for all transformation errors.
     */
    public static class TransformException extends Exception {
        public TransformException(String message) { super(message); }
        public TransformException(String message, Throwable cause) { super(message, cause); }
    }

    /**
     * Thrown when the input data cannot be parsed.
     */
    public static class ParseException extends TransformException {
        private final int lineNumber;

        public ParseException(String message, int lineNumber) {
            super(message + " (line " + lineNumber + ")");
            this.lineNumber = lineNumber;
        }

        public ParseException(String message, int lineNumber, Throwable cause) {
            super(message + " (line " + lineNumber + ")", cause);
            this.lineNumber = lineNumber;
        }

        public int getLineNumber() { return lineNumber; }
    }

    /**
     * Thrown when writing the output fails.
     */
    public static class WriteException extends TransformException {
        public WriteException(String message, Throwable cause) { super(message, cause); }
    }

    /**
     * Thrown when the requested target format is not supported.
     */
    public static class UnsupportedFormatException extends TransformException {
        private final String format;

        public UnsupportedFormatException(String format) {
            super("Unsupported output format: " + format);
            this.format = format;
        }

        public String getFormat() { return format; }
    }

    // ──────────────────────────────────────────────────────────────
    // Fields
    // ──────────────────────────────────────────────────────────────

    private Charset inputCharset = StandardCharsets.UTF_8;
    private Charset outputCharset = StandardCharsets.UTF_8;

    // ──────────────────────────────────────────────────────────────
    // Configuration
    // ──────────────────────────────────────────────────────────────

    /**
     * Set the character encoding used to read the input file.
     */
    public FileTransformer withInputCharset(Charset charset) {
        this.inputCharset = charset;
        return this;
    }

    /**
     * Set the character encoding used to write the output file.
     */
    public FileTransformer withOutputCharset(Charset charset) {
        this.outputCharset = charset;
        return this;
    }

    // ──────────────────────────────────────────────────────────────
    // Core API
    // ──────────────────────────────────────────────────────────────

    /**
     * Transform a CSV input file into the specified output format.
     *
     * <p>Implementation tips:
     * <ul>
     *   <li>Use try-with-resources for every reader/writer.</li>
     *   <li>Stream rows one at a time — don't load the entire file into memory.</li>
     *   <li>The first CSV row contains headers.</li>
     * </ul>
     *
     * @param input  path to the CSV input file
     * @param output path where the transformed file will be written
     * @param format target output format (JSON or XML)
     * @throws TransformException on parse, write, or unsupported-format errors
     */
    public void transform(Path input, Path output, Format format) throws TransformException {
        // TODO: Validate format — throw UnsupportedFormatException if CSV is requested as output.
        // TODO: Open input with BufferedReader (respect inputCharset).
        // TODO: Read header row to determine column names.
        // TODO: For each subsequent row, parse fields and write to the output format.
        // TODO: Wrap I/O exceptions in ParseException or WriteException as appropriate.
        throw new UnsupportedOperationException("TODO — implement transform");
    }

    /**
     * Transform a CSV file contained inside a ZIP archive.
     *
     * @param zipPath     path to the ZIP archive
     * @param csvFileName name of the CSV entry inside the archive
     * @param output      path where the transformed file will be written
     * @param format      target output format
     * @throws TransformException on errors
     */
    public void transformFromZip(Path zipPath, String csvFileName, Path output, Format format)
            throws TransformException {
        // TODO: Open ZipInputStream with try-with-resources.
        // TODO: Iterate entries until csvFileName is found.
        // TODO: Delegate to a shared parsing/writing method.
        throw new UnsupportedOperationException("TODO — implement transformFromZip");
    }

    // ──────────────────────────────────────────────────────────────
    // Internal helpers
    // ──────────────────────────────────────────────────────────────

    /**
     * Parse a single CSV line into a list of field values.
     * Handles quoted fields and commas inside quotes.
     */
    List<String> parseCsvLine(String line, int lineNumber) throws ParseException {
        // TODO: Implement RFC-4180-compliant CSV field parsing.
        throw new UnsupportedOperationException("TODO — implement parseCsvLine");
    }

    /**
     * Write a single record (map of column → value) as JSON.
     */
    void writeJsonRecord(Writer writer, Map<String, String> record) throws WriteException {
        // TODO: Use Gson to serialize the record.
        throw new UnsupportedOperationException("TODO — implement writeJsonRecord");
    }

    /**
     * Write a single record as an XML element.
     */
    void writeXmlRecord(Writer writer, Map<String, String> record) throws WriteException {
        // TODO: Emit a <record> element with child elements per column.
        throw new UnsupportedOperationException("TODO — implement writeXmlRecord");
    }

    // ──────────────────────────────────────────────────────────────
    // Main (manual testing)
    // ──────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: java exercises.FileTransformer <input.csv> <output> <JSON|XML>");
            System.exit(1);
        }

        FileTransformer transformer = new FileTransformer();
        try {
            transformer.transform(Path.of(args[0]), Path.of(args[1]), Format.valueOf(args[2].toUpperCase()));
            System.out.println("Transformation complete → " + args[1]);
        } catch (TransformException e) {
            System.err.println("Transformation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
