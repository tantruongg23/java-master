package exercises.converter;

/**
 * Base class for the <b>Chain of Responsibility</b> pattern in the
 * document conversion pipeline.
 *
 * <p>Each handler in the chain performs one stage of conversion
 * (validation, parsing, transformation, output) and then passes
 * the result to the next handler — or short-circuits on error.</p>
 *
 * <h3>Chain structure</h3>
 * <pre>
 *   ValidationHandler → ParsingHandler → TransformHandler → OutputHandler
 * </pre>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * ConversionHandler chain = new ValidationHandler(
 *     new ParsingHandler(
 *         new TransformHandler(
 *             new OutputHandler(null))));
 *
 * ConversionContext result = chain.handle(context);
 * }</pre>
 */
public abstract class ConversionHandler {

    private final ConversionHandler next;

    /**
     * @param next the next handler in the chain, or {@code null} if this is the last
     */
    protected ConversionHandler(ConversionHandler next) {
        this.next = next;
    }

    /**
     * Process the context and optionally delegate to the next handler.
     *
     * @param context mutable conversion context carrying data between stages
     * @return the context after processing
     */
    public ConversionContext handle(ConversionContext context) {
        ConversionContext processed = doHandle(context);
        if (processed.hasError()) {
            return processed; // short-circuit
        }
        if (next != null) {
            return next.handle(processed);
        }
        return processed;
    }

    /**
     * Perform this handler's specific processing.
     *
     * @param context the current conversion context
     * @return the updated context (may set an error to short-circuit)
     */
    protected abstract ConversionContext doHandle(ConversionContext context);

    // ─── Inner context class ──────────────────────────────────────────

    /**
     * Mutable context object passed through the handler chain.
     *
     * TODO: Add fields as needed:
     *   - String rawInput
     *   - Object parsedData
     *   - Object transformedData
     *   - String output
     *   - String errorMessage
     */
    public static class ConversionContext {

        private String rawInput;
        private Object parsedData;
        private Object transformedData;
        private String output;
        private String errorMessage;

        public ConversionContext(String rawInput) {
            this.rawInput = rawInput;
        }

        public boolean hasError() {
            return errorMessage != null;
        }

        public String getRawInput()         { return rawInput; }
        public Object getParsedData()       { return parsedData; }
        public Object getTransformedData()  { return transformedData; }
        public String getOutput()           { return output; }
        public String getErrorMessage()     { return errorMessage; }

        public void setParsedData(Object parsedData)           { this.parsedData = parsedData; }
        public void setTransformedData(Object transformedData) { this.transformedData = transformedData; }
        public void setOutput(String output)                   { this.output = output; }
        public void setError(String errorMessage)              { this.errorMessage = errorMessage; }
    }

    // TODO: Implement concrete handlers:
    //
    //   public class ValidationHandler extends ConversionHandler {
    //       // Validate that input is non-null, non-empty, correct format
    //   }
    //
    //   public class ParsingHandler extends ConversionHandler {
    //       // Parse raw input into an intermediate structure
    //   }
    //
    //   public class TransformHandler extends ConversionHandler {
    //       // Transform parsed data into target structure
    //   }
    //
    //   public class OutputHandler extends ConversionHandler {
    //       // Serialise transformed data to output string
    //   }
}
