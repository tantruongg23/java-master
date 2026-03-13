package exercises;

import java.util.*;
import java.util.concurrent.*;

/**
 * Exercise 1 — Memory Leak Lab
 *
 * <p>Intentionally create four classic memory leak patterns, then detect and fix them.
 * Run with {@code -Xmx128m} to make leaks visible faster.</p>
 *
 * <h3>How to observe</h3>
 * <ol>
 *   <li>Start with JVisualVM attached or JFR recording.</li>
 *   <li>Watch heap usage climb without plateauing.</li>
 *   <li>Take a heap dump: {@code jcmd <pid> GC.heap_dump /tmp/dump.hprof}</li>
 *   <li>Analyze with Eclipse MAT → Leak Suspects.</li>
 * </ol>
 */
public class MemoryLeakLab {

    // ──────────────────────────────────────────────────────────────
    // Leak 1 — Static collection that grows unboundedly
    // ──────────────────────────────────────────────────────────────

    private static final List<byte[]> staticCache = new ArrayList<>();

    /**
     * Simulates a cache that never evicts. Each call adds 1 KB to a static list.
     *
     * <p><b>Leak cause:</b> Static collections are GC roots; objects are never eligible for collection.</p>
     * <p><b>Fix:</b> Use bounded cache (LRU via {@code LinkedHashMap}), weak references, or TTL eviction.</p>
     */
    public void leakStaticCollection() {
        // TODO: In a loop, add new byte[1024] to staticCache.
        // TODO: Observe heap growth.
        throw new UnsupportedOperationException("TODO — implement leakStaticCollection");
    }

    /**
     * Fixed version — bounded cache.
     */
    public void fixedStaticCollection() {
        // TODO: Use a bounded structure (e.g., LinkedHashMap with removeEldestEntry).
        throw new UnsupportedOperationException("TODO — implement fixedStaticCollection");
    }

    // ──────────────────────────────────────────────────────────────
    // Leak 2 — Listener registration without removal
    // ──────────────────────────────────────────────────────────────

    public interface EventListener {
        void onEvent(String event);
    }

    private final List<EventListener> listeners = new ArrayList<>();

    public void addListener(EventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(EventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Registers listeners in a loop but never unregisters them.
     *
     * <p><b>Leak cause:</b> Each listener holds a reference to its enclosing object,
     * preventing GC of both the listener and the enclosed data.</p>
     * <p><b>Fix:</b> Always unregister listeners; use weak listener patterns.</p>
     */
    public void leakListeners() {
        // TODO: In a loop, create objects that register an anonymous EventListener.
        // TODO: The listeners hold references to large data.
        // TODO: Never call removeListener.
        throw new UnsupportedOperationException("TODO — implement leakListeners");
    }

    /**
     * Fixed version — unregister listeners properly.
     */
    public void fixedListeners() {
        // TODO: Store a reference to each listener and call removeListener when done.
        throw new UnsupportedOperationException("TODO — implement fixedListeners");
    }

    // ──────────────────────────────────────────────────────────────
    // Leak 3 — Inner class holding outer class reference
    // ──────────────────────────────────────────────────────────────

    /**
     * An outer class with a large payload.
     */
    static class HeavyOuter {
        private final byte[] payload = new byte[1024 * 1024]; // 1 MB

        /**
         * Non-static inner class — implicitly holds a reference to HeavyOuter.
         */
        class InnerTask implements Runnable {
            @Override
            public void run() {
                // Does some work but doesn't need the outer's payload.
            }
        }

        /**
         * Fixed: static inner class — no implicit outer reference.
         */
        static class StaticInnerTask implements Runnable {
            @Override
            public void run() {
                // Does the same work without retaining HeavyOuter.
            }
        }
    }

    /**
     * Creates HeavyOuter instances and stores only the inner tasks.
     * The non-static inner class keeps HeavyOuter alive.
     *
     * <p><b>Leak cause:</b> Non-static inner class retains outer instance.</p>
     * <p><b>Fix:</b> Use static inner class (or top-level class).</p>
     */
    public void leakInnerClass() {
        // TODO: Create HeavyOuter instances, extract InnerTask, store tasks in a list.
        // TODO: The HeavyOuter (1 MB each) cannot be GC'd because InnerTask references it.
        throw new UnsupportedOperationException("TODO — implement leakInnerClass");
    }

    /**
     * Fixed version — uses static inner class.
     */
    public void fixedInnerClass() {
        // TODO: Use StaticInnerTask instead.
        throw new UnsupportedOperationException("TODO — implement fixedInnerClass");
    }

    // ──────────────────────────────────────────────────────────────
    // Leak 4 — ThreadLocal not cleaned up in a thread pool
    // ──────────────────────────────────────────────────────────────

    private static final ThreadLocal<byte[]> threadLocalData = new ThreadLocal<>();

    /**
     * Sets ThreadLocal data in thread-pool threads without removing it.
     *
     * <p><b>Leak cause:</b> Thread-pool threads are long-lived; ThreadLocal entries
     * accumulate because threads are reused, not destroyed.</p>
     * <p><b>Fix:</b> Always call {@code ThreadLocal.remove()} in a finally block.</p>
     */
    public void leakThreadLocal() {
        // TODO: Submit tasks to a fixed thread pool.
        // TODO: Each task sets threadLocalData to new byte[1024 * 100].
        // TODO: Never call threadLocalData.remove().
        throw new UnsupportedOperationException("TODO — implement leakThreadLocal");
    }

    /**
     * Fixed version — remove ThreadLocal after use.
     */
    public void fixedThreadLocal() {
        // TODO: Same as above but wrap task body in try-finally with threadLocalData.remove().
        throw new UnsupportedOperationException("TODO — implement fixedThreadLocal");
    }

    // ──────────────────────────────────────────────────────────────
    // Main — Run each leak scenario
    // ──────────────────────────────────────────────────────────────

    /**
     * Run with: {@code java -Xmx128m exercises.MemoryLeakLab <scenario>}
     *
     * <p>Scenarios: static, listener, inner, threadlocal</p>
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java -Xmx128m exercises.MemoryLeakLab <static|listener|inner|threadlocal>");
            System.exit(1);
        }

        MemoryLeakLab lab = new MemoryLeakLab();

        switch (args[0].toLowerCase()) {
            case "static" -> lab.leakStaticCollection();
            case "listener" -> lab.leakListeners();
            case "inner" -> lab.leakInnerClass();
            case "threadlocal" -> lab.leakThreadLocal();
            default -> System.err.println("Unknown scenario: " + args[0]);
        }
    }
}
