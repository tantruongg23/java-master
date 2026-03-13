package exercises.json;

import java.util.List;
import java.util.Map;

/**
 * Sealed interface representing a JSON value.
 *
 * <p>Demonstrates:</p>
 * <ul>
 *   <li><b>Sealed interfaces</b> — restricts implementations to the six JSON types</li>
 *   <li><b>Records</b> — each variant is an immutable record</li>
 *   <li><b>Pattern matching for switch</b> — exhaustive dispatch over the sealed hierarchy</li>
 * </ul>
 *
 * <h3>JSON type mapping</h3>
 * <pre>
 *   JSON object  →  JsonObject(Map&lt;String, JsonValue&gt;)
 *   JSON array   →  JsonArray(List&lt;JsonValue&gt;)
 *   JSON string  →  JsonString(String)
 *   JSON number  →  JsonNumber(double)
 *   JSON boolean →  JsonBoolean(boolean)
 *   JSON null    →  JsonNull
 * </pre>
 */
public sealed interface JsonValue
        permits JsonValue.JsonObject,
                JsonValue.JsonArray,
                JsonValue.JsonString,
                JsonValue.JsonNumber,
                JsonValue.JsonBoolean,
                JsonValue.JsonNull {

    /** A JSON object: an ordered map of string keys to JSON values. */
    record JsonObject(Map<String, JsonValue> members) implements JsonValue {
        public JsonObject {
            members = Map.copyOf(members);
        }
    }

    /** A JSON array: an ordered list of JSON values. */
    record JsonArray(List<JsonValue> elements) implements JsonValue {
        public JsonArray {
            elements = List.copyOf(elements);
        }
    }

    /** A JSON string value. */
    record JsonString(String value) implements JsonValue {}

    /** A JSON number value (stored as double for simplicity). */
    record JsonNumber(double value) implements JsonValue {}

    /** A JSON boolean value. */
    record JsonBoolean(boolean value) implements JsonValue {
        public static final JsonBoolean TRUE = new JsonBoolean(true);
        public static final JsonBoolean FALSE = new JsonBoolean(false);
    }

    /** The JSON null literal. Singleton via enum-like pattern. */
    record JsonNull() implements JsonValue {
        public static final JsonNull INSTANCE = new JsonNull();
    }

    // ─── Pretty-printer (TODO) ───────────────────────────────────────

    /**
     * Pretty-print this JSON value with the given indentation level.
     *
     * <p>TODO: implement using pattern matching switch over the sealed hierarchy.</p>
     *
     * <pre>{@code
     * return switch (this) {
     *     case JsonObject(var members) -> { ... }
     *     case JsonArray(var elements) -> { ... }
     *     case JsonString(var value)   -> yield "\"" + escape(value) + "\"";
     *     case JsonNumber(var value)   -> yield formatNumber(value);
     *     case JsonBoolean(var value)  -> yield String.valueOf(value);
     *     case JsonNull n              -> yield "null";
     * };
     * }</pre>
     *
     * @param indent current indentation level (number of spaces)
     * @return formatted JSON string
     */
    default String prettyPrint(int indent) {
        // TODO: implement pretty-printer with pattern matching
        throw new UnsupportedOperationException("TODO: implement prettyPrint");
    }

    /** Convenience overload: pretty-print with no initial indentation. */
    default String prettyPrint() {
        return prettyPrint(0);
    }

    // ─── Parser (TODO) ───────────────────────────────────────────────

    /**
     * Parse a JSON string into a {@link JsonValue} AST.
     *
     * <p>TODO: implement a recursive-descent parser. Suggested approach:</p>
     * <ol>
     *   <li>Create a {@code JsonParser} class with a cursor (index into the input).</li>
     *   <li>Methods: {@code parseValue()}, {@code parseObject()}, {@code parseArray()},
     *       {@code parseString()}, {@code parseNumber()}, {@code parseLiteral()}.</li>
     *   <li>Skip whitespace between tokens.</li>
     *   <li>Use text blocks for test JSON data.</li>
     * </ol>
     *
     * @param json the JSON string to parse
     * @return the parsed AST
     * @throws IllegalArgumentException if the input is not valid JSON
     */
    static JsonValue parse(String json) {
        // TODO: implement JSON parser
        throw new UnsupportedOperationException("TODO: implement parse");
    }

    // ─── Test data (text block) ──────────────────────────────────────

    /** Sample JSON for testing, using a text block. */
    String SAMPLE_JSON = """
            {
              "name": "Alice",
              "age": 30,
              "active": true,
              "address": {
                "street": "123 Main St",
                "city": "Springfield"
              },
              "scores": [95, 87, 92],
              "nickname": null
            }
            """;
}
