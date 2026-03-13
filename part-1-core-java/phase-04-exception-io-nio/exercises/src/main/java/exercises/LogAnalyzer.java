package exercises;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Exercise 1 — Log Analyzer
 *
 * <p>Read large log files (100 MB+) efficiently using NIO / BufferedReader.
 * Parse log entries, filter by level and date range, and generate a summary report.</p>
 *
 * <h3>Log format</h3>
 * <pre>[2024-03-15T10:30:45] ERROR — NullPointerException in OrderService.process</pre>
 *
 * <h3>Bonus</h3>
 * Watch a directory for new {@code .log} files and analyze them automatically.
 */
public class LogAnalyzer {

    // ──────────────────────────────────────────────────────────────
    // Custom Exceptions
    // ──────────────────────────────────────────────────────────────

    /**
     * Thrown when a log line does not match the expected format.
     */
    public static class InvalidLogFormatException extends RuntimeException {
        private final String rawLine;
        private final int lineNumber;

        public InvalidLogFormatException(String rawLine, int lineNumber, String message) {
            super(message);
            this.rawLine = rawLine;
            this.lineNumber = lineNumber;
        }

        public String getRawLine() { return rawLine; }
        public int getLineNumber() { return lineNumber; }
    }

    /**
     * Thrown when a log file cannot be found or read.
     */
    public static class LogFileNotFoundException extends RuntimeException {
        private final Path filePath;

        public LogFileNotFoundException(Path filePath, Throwable cause) {
            super("Log file not found or unreadable: " + filePath, cause);
            this.filePath = filePath;
        }

        public Path getFilePath() { return filePath; }
    }

    // ──────────────────────────────────────────────────────────────
    // Data model
    // ──────────────────────────────────────────────────────────────

    /**
     * Represents a single parsed log entry.
     */
    public record LogEntry(LocalDateTime timestamp, String level, String message) {}

    /**
     * Summary report produced after analysis.
     */
    public record Report(
            Map<String, Long> countByLevel,
            List<Map.Entry<String, Long>> topErrors,
            long totalEntries,
            long malformedEntries
    ) {}

    // ──────────────────────────────────────────────────────────────
    // Fields
    // ──────────────────────────────────────────────────────────────

    private static final DateTimeFormatter LOG_TS_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final List<LogEntry> entries = new ArrayList<>();
    private long malformedCount = 0;

    // ──────────────────────────────────────────────────────────────
    // Core API
    // ──────────────────────────────────────────────────────────────

    /**
     * Analyze a log file, parsing every line into {@link LogEntry} objects.
     *
     * <p>Implementation tips:
     * <ul>
     *   <li>Use {@link java.nio.file.Files#newBufferedReader(Path)} for efficient reading.</li>
     *   <li>Track malformed lines — do not abort on a single bad line.</li>
     *   <li>Throw {@link LogFileNotFoundException} if the path does not exist.</li>
     * </ul>
     *
     * @param logFile path to the log file
     * @throws LogFileNotFoundException if the file does not exist
     */
    public void analyzeFile(Path logFile) {
        // TODO: Validate that logFile exists; throw LogFileNotFoundException otherwise.
        // TODO: Open with try-with-resources using Files.newBufferedReader.
        // TODO: Parse each line into a LogEntry. Track malformed lines.
        // TODO: Store results in this.entries.
        throw new UnsupportedOperationException("TODO — implement analyzeFile");
    }

    /**
     * Return entries filtered by the given log level.
     *
     * @param level one of ERROR, WARN, INFO, DEBUG
     * @return filtered list (never null)
     */
    public List<LogEntry> filterByLevel(String level) {
        // TODO: Filter this.entries by level (case-insensitive).
        throw new UnsupportedOperationException("TODO — implement filterByLevel");
    }

    /**
     * Return entries within the specified date range (inclusive).
     *
     * @param from start timestamp (inclusive)
     * @param to   end timestamp (inclusive)
     * @return filtered list
     */
    public List<LogEntry> filterByDateRange(LocalDateTime from, LocalDateTime to) {
        // TODO: Filter this.entries by timestamp range.
        throw new UnsupportedOperationException("TODO — implement filterByDateRange");
    }

    /**
     * Generate a summary report of all analyzed entries.
     *
     * @return report with counts by level and top error messages
     */
    public Report generateReport() {
        // TODO: Count entries by level.
        // TODO: Find top-10 most frequent ERROR messages.
        // TODO: Return a Report record.
        throw new UnsupportedOperationException("TODO — implement generateReport");
    }

    // ──────────────────────────────────────────────────────────────
    // Bonus — Directory Watcher
    // ──────────────────────────────────────────────────────────────

    /**
     * Watch a directory for new {@code .log} files and analyze each one.
     *
     * <p>Uses {@link WatchService} to react to {@code ENTRY_CREATE} events.</p>
     *
     * @param directory the directory to watch
     */
    public void watchDirectory(Path directory) {
        // TODO: Register a WatchService on the directory.
        // TODO: Poll for ENTRY_CREATE events; when a .log file appears, call analyzeFile.
        // TODO: Handle InterruptedException and IOException properly.
        throw new UnsupportedOperationException("TODO — implement watchDirectory");
    }

    // ──────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────

    /**
     * Parse a single log line into a {@link LogEntry}.
     *
     * @param line       the raw log line
     * @param lineNumber line number in the file (for error reporting)
     * @return parsed entry
     * @throws InvalidLogFormatException if the line cannot be parsed
     */
    LogEntry parseLine(String line, int lineNumber) {
        // TODO: Extract timestamp, level, message from the line.
        // TODO: Throw InvalidLogFormatException on malformed input.
        throw new UnsupportedOperationException("TODO — implement parseLine");
    }

    // ──────────────────────────────────────────────────────────────
    // Main (manual testing)
    // ──────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java exercises.LogAnalyzer <log-file-path>");
            System.exit(1);
        }

        LogAnalyzer analyzer = new LogAnalyzer();
        analyzer.analyzeFile(Path.of(args[0]));

        Report report = analyzer.generateReport();
        System.out.println("Total entries : " + report.totalEntries());
        System.out.println("Malformed     : " + report.malformedEntries());
        System.out.println("By level      : " + report.countByLevel());
        System.out.println("Top errors    : " + report.topErrors());
    }
}
