package exercises;

/**
 * LRUCache — a fixed-capacity cache that evicts the least recently used entry.
 *
 * <p>Both {@link #get(Object)} and {@link #put(Object, Object)} must run in O(1) time.
 *
 * <p>Implementation options:
 * <ul>
 *   <li><b>Option A (simpler):</b> Extend or wrap {@link java.util.LinkedHashMap} with
 *       {@code accessOrder = true} and override {@code removeEldestEntry()}.</li>
 *   <li><b>Option B (from scratch):</b> Use a {@link java.util.HashMap} for O(1) key lookup
 *       combined with a custom doubly-linked list for O(1) order maintenance.</li>
 * </ul>
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class LRUCache<K, V> {

    private final int capacity;

    // === Option B data structures (uncomment if building from scratch) ===
    // private final Map<K, Node<K, V>> map;
    // private Node<K, V> head; // most recently used (sentinel)
    // private Node<K, V> tail; // least recently used (sentinel)

    // private static class Node<K, V> {
    //     K key;
    //     V value;
    //     Node<K, V> prev;
    //     Node<K, V> next;
    //     Node(K key, V value) { this.key = key; this.value = value; }
    // }

    /**
     * Create an LRU cache with the given maximum capacity.
     *
     * @param capacity the maximum number of entries before eviction occurs
     * @throws IllegalArgumentException if capacity is less than 1
     */
    public LRUCache(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least 1, got: " + capacity);
        }
        this.capacity = capacity;

        // TODO: Initialize the backing data structure.
        //
        // Option A: LinkedHashMap
        //   this.map = new LinkedHashMap<>(capacity, 0.75f, true) {
        //       @Override
        //       protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        //           return size() > capacity;
        //       }
        //   };
        //
        // Option B: HashMap + doubly-linked list
        //   this.map = new HashMap<>(capacity);
        //   this.head = new Node<>(null, null); // sentinel
        //   this.tail = new Node<>(null, null); // sentinel
        //   head.next = tail;
        //   tail.prev = head;
    }

    /**
     * Retrieve the value for the given key and mark it as recently used.
     *
     * @param key the key to look up
     * @return the value, or {@code null} if not present
     */
    public V get(K key) {
        // TODO: 1. Look up the key in the map.
        //       2. If found, move the entry to the "most recently used" position.
        //       3. Return the value (or null if absent).

        throw new UnsupportedOperationException("TODO: implement get()");
    }

    /**
     * Insert or update a key-value pair. If the cache is at capacity,
     * evict the least recently used entry first.
     *
     * @param key   the key
     * @param value the value
     */
    public void put(K key, V value) {
        // TODO: 1. If the key already exists, update the value and move to MRU position.
        //       2. If the key is new:
        //          a. If at capacity, evict the LRU entry (tail of the list / eldest in LinkedHashMap).
        //          b. Insert the new entry at the MRU position.

        throw new UnsupportedOperationException("TODO: implement put()");
    }

    /**
     * @return the number of entries currently in the cache
     */
    public int size() {
        // TODO: Return the number of entries in the backing map.

        throw new UnsupportedOperationException("TODO: implement size()");
    }

    /**
     * @return the maximum capacity of this cache
     */
    public int getCapacity() {
        return capacity;
    }

    // BONUS TODO: Add evictionCount() method.
    // BONUS TODO: Add thread-safety using ReentrantReadWriteLock.
}
