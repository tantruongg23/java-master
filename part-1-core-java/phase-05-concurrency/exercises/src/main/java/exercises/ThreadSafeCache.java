package exercises;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Exercise 3 — Thread-Safe Cache
 *
 * <p>A generic cache with TTL-based eviction, {@link ReadWriteLock} for concurrent
 * access, and a background cleanup thread.</p>
 *
 * <h3>Bonus</h3>
 * Cache statistics: hit rate, miss rate, eviction count.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class ThreadSafeCache<K, V> {

    // ──────────────────────────────────────────────────────────────
    // Internal entry wrapper
    // ──────────────────────────────────────────────────────────────

    private record CacheEntry<V>(V value, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }

    // ──────────────────────────────────────────────────────────────
    // Fields
    // ──────────────────────────────────────────────────────────────

    private final Map<K, CacheEntry<V>> store = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final LongAdder hits = new LongAdder();
    private final LongAdder misses = new LongAdder();
    private final LongAdder evictions = new LongAdder();

    private volatile boolean cleanupRunning = false;
    private ScheduledExecutorService cleanupExecutor;

    // ──────────────────────────────────────────────────────────────
    // Core API
    // ──────────────────────────────────────────────────────────────

    /**
     * Retrieve a value from the cache.
     *
     * <p>Implementation tips:
     * <ul>
     *   <li>Acquire the read lock.</li>
     *   <li>Check if the entry exists and is not expired.</li>
     *   <li>Track hits and misses.</li>
     * </ul>
     *
     * @param key the key to look up
     * @return the value wrapped in an Optional, or empty if absent/expired
     */
    public Optional<V> get(K key) {
        // TODO: Acquire read lock.
        // TODO: Look up the key; check expiration.
        // TODO: Increment hits or misses accordingly.
        // TODO: Return Optional.of(value) or Optional.empty().
        throw new UnsupportedOperationException("TODO — implement get");
    }

    /**
     * Store a value in the cache with a time-to-live.
     *
     * @param key   the key
     * @param value the value
     * @param ttl   how long before the entry expires
     */
    public void put(K key, V value, Duration ttl) {
        // TODO: Acquire write lock.
        // TODO: Create a CacheEntry with Instant.now().plus(ttl).
        // TODO: Store in the map.
        throw new UnsupportedOperationException("TODO — implement put");
    }

    /**
     * Remove a specific key from the cache.
     *
     * @return the removed value, or empty if not found
     */
    public Optional<V> remove(K key) {
        // TODO: Acquire write lock.
        // TODO: Remove and return the value.
        throw new UnsupportedOperationException("TODO — implement remove");
    }

    /**
     * Return the current number of entries (including potentially expired ones).
     */
    public int size() {
        // TODO: Acquire read lock and return store.size().
        throw new UnsupportedOperationException("TODO — implement size");
    }

    // ──────────────────────────────────────────────────────────────
    // Cleanup
    // ──────────────────────────────────────────────────────────────

    /**
     * Remove all expired entries. Called periodically by the background thread.
     */
    void cleanup() {
        // TODO: Acquire write lock.
        // TODO: Iterate entries, remove expired ones.
        // TODO: Increment evictions counter for each removal.
        throw new UnsupportedOperationException("TODO — implement cleanup");
    }

    /**
     * Start a background daemon thread that runs {@link #cleanup()} at a fixed interval.
     *
     * @param interval how often to run the cleanup
     */
    public void startCleanup(Duration interval) {
        // TODO: Create a ScheduledExecutorService with a daemon thread factory.
        // TODO: Schedule cleanup at the given interval.
        // TODO: Set cleanupRunning = true.
        throw new UnsupportedOperationException("TODO — implement startCleanup");
    }

    /**
     * Stop the background cleanup thread.
     */
    public void stopCleanup() {
        cleanupRunning = false;
        if (cleanupExecutor != null) {
            cleanupExecutor.shutdownNow();
        }
    }

    // ──────────────────────────────────────────────────────────────
    // Statistics (Bonus)
    // ──────────────────────────────────────────────────────────────

    public long getHits() { return hits.sum(); }
    public long getMisses() { return misses.sum(); }
    public long getEvictions() { return evictions.sum(); }

    /**
     * Hit rate as a percentage (0.0 – 1.0).
     */
    public double getHitRate() {
        long total = hits.sum() + misses.sum();
        return total == 0 ? 0.0 : (double) hits.sum() / total;
    }

    // ──────────────────────────────────────────────────────────────
    // Main (manual testing)
    // ──────────────────────────────────────────────────────────────

    public static void main(String[] args) throws InterruptedException {
        ThreadSafeCache<String, String> cache = new ThreadSafeCache<>();
        cache.startCleanup(Duration.ofSeconds(1));

        cache.put("key1", "value1", Duration.ofSeconds(3));
        cache.put("key2", "value2", Duration.ofSeconds(1));

        System.out.println("key1 = " + cache.get("key1"));
        System.out.println("key2 = " + cache.get("key2"));

        Thread.sleep(2000);

        System.out.println("After 2s:");
        System.out.println("key1 = " + cache.get("key1"));
        System.out.println("key2 = " + cache.get("key2")); // should be empty

        System.out.printf("Hit rate: %.2f%n", cache.getHitRate());
        cache.stopCleanup();
    }
}
