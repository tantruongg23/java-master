package exercises.profiler;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * AOP aspect that profiles methods annotated with {@link Profiled}.
 *
 * <p><b>Exercise 3 — Method Performance Profiler</b></p>
 *
 * <p>Implementation steps:
 * <ol>
 *   <li>Intercept {@code @Profiled} methods with {@code @Around} advice.</li>
 *   <li>Measure elapsed time using {@code System.nanoTime()}.</li>
 *   <li>If elapsed time &gt; threshold, log a warning.</li>
 *   <li>Record the measurement in a per-method statistics collector.</li>
 *   <li>Expose statistics via getter methods.</li>
 * </ol>
 *
 * <p><b>Bonus:</b> Use a {@code BeanPostProcessor} to discover all
 * {@code @Profiled} methods at startup and pre-register them so
 * {@link #getStatistics()} returns entries even for uncalled methods.
 *
 * <p><b>Bonus:</b> Add a daemon thread or {@code @Scheduled} task
 * that periodically prints a formatted "dashboard" of stats to stdout.
 */
@Aspect
@Component
public class ProfilerAspect {

    private final Map<String, MethodStats> statsMap = new ConcurrentHashMap<>();

    /**
     * Intercepts any method annotated with {@link Profiled}.
     */
    @Around("@annotation(profiled)")
    public Object profile(ProceedingJoinPoint joinPoint, Profiled profiled) throws Throwable {
        long thresholdMs = profiled.threshold();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodKey = signature.getDeclaringTypeName() + "#" + signature.getName();

        // TODO: Implement the profiling logic
        //
        // 1. Record start time:
        //    long start = System.nanoTime();
        //
        // 2. Proceed with method execution:
        //    Object result = joinPoint.proceed();
        //
        // 3. Calculate elapsed ms:
        //    long elapsedMs = (System.nanoTime() - start) / 1_000_000;
        //
        // 4. If elapsedMs > thresholdMs, log a warning:
        //    "SLOW METHOD: {} took {} ms (threshold: {} ms)"
        //
        // 5. Record the measurement:
        //    statsMap.computeIfAbsent(methodKey, k -> new MethodStats(k))
        //            .record(elapsedMs);
        //
        // 6. Return result

        throw new UnsupportedOperationException("Implement profile advice — see TODO above");
    }

    /** Returns statistics for all profiled methods. */
    public Map<String, MethodStats> getStatistics() {
        return Collections.unmodifiableMap(statsMap);
    }

    /** Returns statistics for a specific method, or {@code null} if not tracked. */
    public MethodStats getStatistics(String methodKey) {
        return statsMap.get(methodKey);
    }

    /**
     * Collects per-method performance statistics.
     *
     * <p>TODO: Implement thread-safe statistic collection.
     * Consider using {@code LongAdder} for counters and a sorted
     * list or reservoir sampling for p95 calculation.
     */
    public static class MethodStats {

        private final String methodKey;
        private final List<Long> measurements = new CopyOnWriteArrayList<>();

        public MethodStats(String methodKey) {
            this.methodKey = methodKey;
        }

        /** Records a single execution time measurement. */
        public void record(long elapsedMs) {
            // TODO: Add the measurement and update running statistics
            measurements.add(elapsedMs);
        }

        public String getMethodKey() {
            return methodKey;
        }

        public long getCallCount() {
            return measurements.size();
        }

        /** TODO: Calculate and return average execution time. */
        public double getAverageMs() {
            throw new UnsupportedOperationException("Implement getAverageMs");
        }

        /** TODO: Return the maximum execution time recorded. */
        public long getMaxMs() {
            throw new UnsupportedOperationException("Implement getMaxMs");
        }

        /**
         * TODO: Calculate the 95th percentile.
         * Sort measurements, pick the value at index (int)(count * 0.95).
         */
        public long getP95Ms() {
            throw new UnsupportedOperationException("Implement getP95Ms");
        }

        @Override
        public String toString() {
            return "MethodStats{method='%s', calls=%d}".formatted(methodKey, getCallCount());
        }
    }
}
