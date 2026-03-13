package exercises;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Exercise 2 — GC Tuning Exercise
 *
 * <p>Creates different object allocation patterns and measures GC behaviour.
 * Run this class with different GC algorithms and flags to compare results.</p>
 *
 * <h3>Suggested JVM flag combinations</h3>
 * <pre>
 * # Serial GC
 * java -Xms256m -Xmx256m -XX:+UseSerialGC -Xlog:gc*:file=gc-serial.log exercises.GCTuningExercise
 *
 * # G1 GC (default)
 * java -Xms256m -Xmx256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Xlog:gc*:file=gc-g1.log exercises.GCTuningExercise
 *
 * # G1 GC (tuned)
 * java -Xms512m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=4m
 *   -XX:InitiatingHeapOccupancyPercent=35 -Xlog:gc*:file=gc-g1-tuned.log exercises.GCTuningExercise
 *
 * # ZGC
 * java -Xms256m -Xmx256m -XX:+UseZGC -Xlog:gc*:file=gc-zgc.log exercises.GCTuningExercise
 * </pre>
 *
 * <h3>Analysis</h3>
 * After running, compare the GC log files for:
 * <ul>
 *   <li>Number of GC pauses</li>
 *   <li>Max and average pause duration</li>
 *   <li>Total throughput (app time vs GC time)</li>
 *   <li>Heap occupancy over time</li>
 * </ul>
 */
public class GCTuningExercise {

    // ──────────────────────────────────────────────────────────────
    // Configuration
    // ──────────────────────────────────────────────────────────────

    private static final int SHORT_LIVED_ITERATIONS = 500_000;
    private static final int LONG_LIVED_CACHE_SIZE = 50_000;
    private static final int LARGE_ARRAY_COUNT = 100;
    private static final int LARGE_ARRAY_SIZE = 1024 * 512; // 512 KB each

    // ──────────────────────────────────────────────────────────────
    // Pattern 1 — Short-lived burst
    // ──────────────────────────────────────────────────────────────

    /**
     * Allocate millions of small, short-lived objects (simulates request processing).
     * These should all be collected in Young Gen minor GCs.
     */
    public void shortLivedBurst() {
        // TODO: In a loop (SHORT_LIVED_ITERATIONS):
        //   - Create small objects (e.g., new byte[64], small strings, small maps).
        //   - Do a trivial computation so JIT doesn't eliminate the allocation.
        //   - Let objects go out of scope immediately.
        // TODO: Print elapsed time.
        throw new UnsupportedOperationException("TODO — implement shortLivedBurst");
    }

    // ──────────────────────────────────────────────────────────────
    // Pattern 2 — Long-lived cache
    // ──────────────────────────────────────────────────────────────

    private final Map<String, byte[]> cache = new HashMap<>();

    /**
     * Build a growing cache of retained objects (simulates an in-memory cache).
     * These will be promoted to Old Gen and stay.
     */
    public void longLivedCache() {
        // TODO: Add LONG_LIVED_CACHE_SIZE entries to the cache.
        //   Key: "entry-" + i
        //   Value: new byte[256]
        // TODO: Periodically log progress.
        // TODO: After filling, do some reads to simulate usage.
        throw new UnsupportedOperationException("TODO — implement longLivedCache");
    }

    // ──────────────────────────────────────────────────────────────
    // Pattern 3 — Large array allocations
    // ──────────────────────────────────────────────────────────────

    /**
     * Allocate large byte arrays that may go directly to Old Gen
     * or be treated as humongous objects in G1.
     */
    public void largeArrayAllocations() {
        // TODO: Allocate LARGE_ARRAY_COUNT arrays of LARGE_ARRAY_SIZE bytes.
        // TODO: Keep some alive, let others be collected.
        // TODO: In G1, arrays > 50% of a region become humongous.
        throw new UnsupportedOperationException("TODO — implement largeArrayAllocations");
    }

    // ──────────────────────────────────────────────────────────────
    // Combined workload
    // ──────────────────────────────────────────────────────────────

    /**
     * Run all three patterns sequentially to create a mixed GC workload.
     */
    public void runAllPatterns() {
        System.out.println("=== Pattern 1: Short-lived burst ===");
        shortLivedBurst();

        System.out.println("\n=== Pattern 2: Long-lived cache ===");
        longLivedCache();

        System.out.println("\n=== Pattern 3: Large array allocations ===");
        largeArrayAllocations();

        System.out.println("\n=== All patterns complete ===");
        System.out.println("Cache size: " + cache.size() + " entries");
        System.out.printf("Heap: used=%,d bytes, max=%,d bytes%n",
                Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(),
                Runtime.getRuntime().maxMemory());
    }

    // ──────────────────────────────────────────────────────────────
    // Main
    // ──────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("GC Tuning Exercise");
        System.out.println("JVM: " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version"));
        System.out.printf("Max heap: %,d MB%n", Runtime.getRuntime().maxMemory() / (1024 * 1024));
        System.out.println();

        new GCTuningExercise().runAllPatterns();
    }
}
