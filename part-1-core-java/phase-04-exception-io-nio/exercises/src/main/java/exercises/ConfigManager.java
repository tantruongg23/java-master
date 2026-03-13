package exercises;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Exercise 3 — Config Manager
 *
 * <p>Properties-based configuration with type-safe access, environment variable
 * overrides, default values, and live reload via {@link WatchService}.</p>
 *
 * <h3>Precedence (highest → lowest)</h3>
 * <ol>
 *   <li>Environment variables (key uppercased, dots replaced with underscores)</li>
 *   <li>Values in the loaded properties file</li>
 *   <li>Default values supplied at access time</li>
 * </ol>
 *
 * <h3>Bonus</h3>
 * Support YAML-like nested configs with dot-separated keys.
 */
public class ConfigManager {

    // ──────────────────────────────────────────────────────────────
    // Custom Exceptions
    // ──────────────────────────────────────────────────────────────

    /**
     * Thrown when a required configuration key is missing and no default is provided.
     */
    public static class MissingRequiredKeyException extends RuntimeException {
        private final String key;

        public MissingRequiredKeyException(String key) {
            super("Required configuration key not found: " + key);
            this.key = key;
        }

        public String getKey() { return key; }
    }

    /**
     * Thrown when a configuration value cannot be converted to the requested type.
     */
    public static class TypeMismatchException extends RuntimeException {
        private final String key;
        private final String value;
        private final String targetType;

        public TypeMismatchException(String key, String value, String targetType, Throwable cause) {
            super("Cannot convert key '" + key + "' value '" + value + "' to " + targetType, cause);
            this.key = key;
            this.value = value;
            this.targetType = targetType;
        }

        public String getKey() { return key; }
        public String getValue() { return value; }
        public String getTargetType() { return targetType; }
    }

    // ──────────────────────────────────────────────────────────────
    // Fields
    // ──────────────────────────────────────────────────────────────

    private final Map<String, String> properties = new ConcurrentHashMap<>();
    private Path configFilePath;
    private final AtomicBoolean watching = new AtomicBoolean(false);

    // ──────────────────────────────────────────────────────────────
    // Loading
    // ──────────────────────────────────────────────────────────────

    /**
     * Load configuration from a {@code .properties} file.
     *
     * <p>Implementation tips:
     * <ul>
     *   <li>Use {@link java.util.Properties#load(java.io.Reader)} with try-with-resources.</li>
     *   <li>Store all key-value pairs in {@link #properties}.</li>
     *   <li>Remember the file path for later reload / watching.</li>
     * </ul>
     *
     * @param path path to the properties file
     * @throws IOException if the file cannot be read
     */
    public void load(Path path) throws IOException {
        // TODO: Open file with try-with-resources.
        // TODO: Load into java.util.Properties, then copy into this.properties map.
        // TODO: Store configFilePath for watch/reload.
        throw new UnsupportedOperationException("TODO — implement load");
    }

    /**
     * Reload the configuration from the previously loaded file path.
     */
    public void reload() throws IOException {
        // TODO: Clear current properties and re-load from configFilePath.
        throw new UnsupportedOperationException("TODO — implement reload");
    }

    // ──────────────────────────────────────────────────────────────
    // Type-safe getters
    // ──────────────────────────────────────────────────────────────

    /**
     * Get a string value. Checks environment variables first, then properties file.
     *
     * @param key the configuration key
     * @return the value
     * @throws MissingRequiredKeyException if the key is not found anywhere
     */
    public String get(String key) {
        // TODO: Check env variable (key uppercased, dots → underscores).
        // TODO: Fall back to this.properties.
        // TODO: Throw MissingRequiredKeyException if missing.
        throw new UnsupportedOperationException("TODO — implement get(key)");
    }

    /**
     * Get a string value with a default fallback.
     */
    public String get(String key, String defaultValue) {
        // TODO: Same as get(key) but return defaultValue instead of throwing.
        throw new UnsupportedOperationException("TODO — implement get(key, default)");
    }

    /**
     * Get an integer value.
     *
     * @throws TypeMismatchException if the value is not a valid integer
     */
    public int getInt(String key) {
        // TODO: Get the string value, parse to int.
        // TODO: Wrap NumberFormatException in TypeMismatchException.
        throw new UnsupportedOperationException("TODO — implement getInt");
    }

    /**
     * Get an integer value with a default fallback.
     */
    public int getInt(String key, int defaultValue) {
        // TODO: Return defaultValue if key is missing.
        throw new UnsupportedOperationException("TODO — implement getInt(key, default)");
    }

    /**
     * Get a boolean value.
     * Accepts: "true", "false", "yes", "no", "1", "0" (case-insensitive).
     */
    public boolean getBoolean(String key) {
        // TODO: Parse the string to boolean.
        // TODO: Throw TypeMismatchException for unrecognized values.
        throw new UnsupportedOperationException("TODO — implement getBoolean");
    }

    /**
     * Get a boolean value with a default fallback.
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        // TODO: Return defaultValue if key is missing.
        throw new UnsupportedOperationException("TODO — implement getBoolean(key, default)");
    }

    /**
     * Get a list of strings (comma-separated in the properties file).
     *
     * @param key the configuration key
     * @return list of trimmed string values
     */
    public List<String> getList(String key) {
        // TODO: Split by comma, trim each element.
        throw new UnsupportedOperationException("TODO — implement getList");
    }

    // ──────────────────────────────────────────────────────────────
    // File watching
    // ──────────────────────────────────────────────────────────────

    /**
     * Start watching the config file's directory for changes. When the config file
     * is modified, automatically reload it.
     *
     * <p>Runs in a daemon thread so it won't prevent JVM shutdown.</p>
     */
    public void watchForChanges() {
        // TODO: Validate that configFilePath has been set.
        // TODO: Register a WatchService on the parent directory.
        // TODO: On ENTRY_MODIFY for the config file, call reload().
        // TODO: Run the loop in a daemon thread.
        // TODO: Use this.watching flag to allow stopping.
        throw new UnsupportedOperationException("TODO — implement watchForChanges");
    }

    /**
     * Stop watching for file changes.
     */
    public void stopWatching() {
        watching.set(false);
    }

    // ──────────────────────────────────────────────────────────────
    // Utility
    // ──────────────────────────────────────────────────────────────

    /**
     * Convert a config key to the corresponding environment variable name.
     * Example: "server.port" → "SERVER_PORT"
     */
    String toEnvVarName(String key) {
        // TODO: Uppercase, replace dots with underscores.
        throw new UnsupportedOperationException("TODO — implement toEnvVarName");
    }

    /**
     * Return a snapshot of all currently loaded configuration entries.
     */
    public Map<String, String> getAll() {
        return Collections.unmodifiableMap(new HashMap<>(properties));
    }

    // ──────────────────────────────────────────────────────────────
    // Main (manual testing)
    // ──────────────────────────────────────────────────────────────

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: java exercises.ConfigManager <config.properties>");
            System.exit(1);
        }

        ConfigManager config = new ConfigManager();
        config.load(Path.of(args[0]));

        System.out.println("All properties: " + config.getAll());
        config.watchForChanges();
        System.out.println("Watching for changes. Press Ctrl+C to exit.");
    }
}
