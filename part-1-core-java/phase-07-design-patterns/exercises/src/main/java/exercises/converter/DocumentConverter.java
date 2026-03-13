package exercises.converter;

/**
 * Abstract base class implementing the <b>Template Method</b> pattern for
 * document conversion.
 *
 * <p>The {@link #convert(String)} method defines the invariant conversion
 * algorithm. Subclasses provide concrete implementations for each step.</p>
 *
 * <h3>Template Method structure</h3>
 * <pre>
 *   convert(input)          ← final (template method)
 *     ├─ parseDocument()    ← abstract
 *     ├─ transformContent() ← abstract
 *     └─ writeOutput()      ← abstract
 * </pre>
 *
 * <h3>Concrete converters to implement</h3>
 * <ul>
 *   <li>{@code MarkdownToHtmlConverter}</li>
 *   <li>{@code CsvToJsonConverter}</li>
 *   <li>{@code XmlToJsonConverter}</li>
 * </ul>
 *
 * <p>Also create a {@code ConverterFactory} that returns the correct subclass
 * based on a format string (Factory Method pattern).</p>
 */
public abstract class DocumentConverter {

    /**
     * Template method — defines the fixed conversion algorithm.
     * Subclasses must not override this method.
     *
     * @param input raw source content
     * @return converted output
     */
    public final String convert(String input) {
        Object parsed = parseDocument(input);
        Object transformed = transformContent(parsed);
        return writeOutput(transformed);
    }

    /**
     * Parse the raw input into an intermediate representation.
     *
     * @param raw the source content
     * @return a parsed structure (type depends on concrete converter)
     */
    protected abstract Object parseDocument(String raw);

    /**
     * Transform the parsed structure (e.g. enrich, normalise, restructure).
     *
     * @param parsed output of {@link #parseDocument(String)}
     * @return the transformed structure
     */
    protected abstract Object transformContent(Object parsed);

    /**
     * Serialise the transformed structure into the target format string.
     *
     * @param transformed output of {@link #transformContent(Object)}
     * @return the final converted string
     */
    protected abstract String writeOutput(Object transformed);

    // TODO: Create concrete subclasses:
    //
    //   public class MarkdownToHtmlConverter extends DocumentConverter { ... }
    //   public class CsvToJsonConverter      extends DocumentConverter { ... }
    //   public class XmlToJsonConverter       extends DocumentConverter { ... }
    //
    // TODO: Create ConverterFactory:
    //
    //   public class ConverterFactory {
    //       public static DocumentConverter create(String format) {
    //           return switch (format) {
    //               case "md-to-html"  -> new MarkdownToHtmlConverter();
    //               case "csv-to-json" -> new CsvToJsonConverter();
    //               case "xml-to-json" -> new XmlToJsonConverter();
    //               default -> throw new IllegalArgumentException("Unknown format: " + format);
    //           };
    //       }
    //   }
}
