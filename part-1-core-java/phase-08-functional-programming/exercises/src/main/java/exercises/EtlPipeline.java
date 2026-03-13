package exercises;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * A functional Extract-Transform-Load pipeline built on the Stream API.
 *
 * <p>The pipeline is constructed via a fluent builder and executed lazily.
 * Each stage is a composable {@link Function} or {@link Predicate}.</p>
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * EtlPipeline.<RawLine, CleanRecord>builder()
 *     .extract(source)
 *     .transform(normalize)
 *     .transform(enrich)
 *     .filter(valid)
 *     .load(sink);
 * }</pre>
 *
 * @param <I> the intermediate domain type produced by parsing
 * @param <O> the final output type written to the sink
 */
public class EtlPipeline<I, O> {

    /**
     * Create a new pipeline builder.
     *
     * @param <I> intermediate type
     * @param <O> output type
     * @return a fresh builder
     */
    public static <I, O> Builder<I, O> builder() {
        return new Builder<>();
    }

    /**
     * Fluent builder for {@link EtlPipeline}.
     *
     * <p>TODO: implement each stage.</p>
     *
     * @param <I> intermediate type
     * @param <O> output type
     */
    public static class Builder<I, O> {

        private Supplier<Stream<String>> source;
        private Function<String, I> parser;
        private Function<I, I> transformChain = Function.identity();
        private Predicate<I> filterChain = t -> true;
        private Function<I, O> finalMapper;

        /**
         * Set the extraction source — produces a lazy stream of raw lines.
         *
         * <p>TODO: accept a {@link Path} and return {@code Files.lines(path)}
         * (wrapped in a supplier for lazy evaluation).</p>
         *
         * @param path path to the source file
         * @return this builder
         */
        public Builder<I, O> extract(Path path) {
            // TODO: set this.source to () -> Files.lines(path)
            //       Handle IOException appropriately.
            throw new UnsupportedOperationException("TODO: implement extract");
        }

        /**
         * Set the parser that converts a raw line into the intermediate type.
         *
         * @param parser line → domain object
         * @return this builder
         */
        public Builder<I, O> parse(Function<String, I> parser) {
            this.parser = parser;
            return this;
        }

        /**
         * Append a transformation stage. Multiple calls compose left-to-right.
         *
         * @param transform intermediate → intermediate
         * @return this builder
         */
        public Builder<I, O> transform(Function<I, I> transform) {
            // TODO: compose with existing transformChain using andThen
            throw new UnsupportedOperationException("TODO: implement transform");
        }

        /**
         * Append a filter stage. Multiple calls are AND-ed together.
         *
         * @param predicate keep elements that match
         * @return this builder
         */
        public Builder<I, O> filter(Predicate<I> predicate) {
            // TODO: compose with existing filterChain using and()
            throw new UnsupportedOperationException("TODO: implement filter");
        }

        /**
         * Set the final mapper from intermediate to output type.
         *
         * @param mapper intermediate → output
         * @return this builder
         */
        public Builder<I, O> mapToOutput(Function<I, O> mapper) {
            this.finalMapper = mapper;
            return this;
        }

        /**
         * Terminal operation: execute the pipeline and collect output into a list.
         *
         * <p>TODO:</p>
         * <ol>
         *   <li>Open the source stream.</li>
         *   <li>Parse each line with {@code parser}.</li>
         *   <li>Apply the composed transforms.</li>
         *   <li>Apply the composed filters.</li>
         *   <li>Map to the output type.</li>
         *   <li>Collect into a list.</li>
         * </ol>
         *
         * @return list of output records
         */
        public List<O> load() {
            // TODO: build and execute the full stream pipeline
            throw new UnsupportedOperationException("TODO: implement load");
        }

        /**
         * Terminal operation: execute the pipeline and pass each output to a consumer.
         *
         * @param sink consumer that receives each output element
         */
        public void load(Consumer<O> sink) {
            // TODO: same as load() but use forEach(sink) instead of collect
            throw new UnsupportedOperationException("TODO: implement load(sink)");
        }
    }

    // Bonus: add monitoring — count processed, filtered, errors —
    //        without breaking the functional pipeline.
    //        Hint: use AtomicLong counters and peek(), or a custom Collector.
}
