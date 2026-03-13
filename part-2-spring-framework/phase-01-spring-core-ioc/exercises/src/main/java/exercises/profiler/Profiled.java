package exercises.profiler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method for automatic performance profiling via AOP.
 *
 * <p>The {@link ProfilerAspect} wraps calls to annotated methods,
 * measures their execution time, and collects statistics. If the
 * execution time exceeds the configured {@link #threshold()}, a
 * warning is logged.
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * @Profiled(threshold = 200)
 * public List<Product> searchProducts(String query) { ... }
 * }</pre>
 *
 * @see ProfilerAspect
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Profiled {

    /**
     * Maximum acceptable execution time in milliseconds.
     * If a method call exceeds this value, a warning is logged.
     *
     * @return threshold in ms (default 100)
     */
    long threshold() default 100;
}
