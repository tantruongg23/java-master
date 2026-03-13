package exercises;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * SimpleHashMap — a from-scratch hash map implementation.
 *
 * <p>Uses an array of buckets with separate chaining (linked list per bucket).
 * Resizes when the load factor exceeds 0.75.
 *
 * <p>Implements {@link Iterable} so entries can be traversed with a for-each loop.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class SimpleHashMap<K, V> implements Iterable<SimpleHashMap.Entry<K, V>> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    /**
     * A key-value pair stored in the map. Forms a singly-linked list within a bucket.
     */
    public static class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    @SuppressWarnings("unchecked")
    private Entry<K, V>[] buckets = new Entry[DEFAULT_CAPACITY];
    private int size = 0;

    /**
     * Insert or update a key-value pair.
     *
     * <p>If the key already exists, update the value and return the old value.
     * If the key is new, insert it and return {@code null}.
     * Supports {@code null} keys (stored in bucket 0).
     *
     * @param key   the key
     * @param value the value
     * @return the previous value, or {@code null} if the key was new
     */
    public V put(K key, V value) {
        // TODO: 1. Compute the bucket index using hash(key) % buckets.length.
        //       2. Walk the chain in that bucket looking for an existing entry with the same key.
        //       3. If found, update the value and return the old value.
        //       4. If not found, prepend a new Entry to the chain.
        //       5. Increment size. If size > capacity * LOAD_FACTOR, call resize().

        throw new UnsupportedOperationException("TODO: implement put()");
    }

    /**
     * Retrieve the value associated with the given key.
     *
     * @param key the key to look up
     * @return the value, or {@code null} if the key is not present
     */
    public V get(K key) {
        // TODO: 1. Compute the bucket index.
        //       2. Walk the chain looking for a matching key.
        //       3. Return the value if found, null otherwise.

        throw new UnsupportedOperationException("TODO: implement get()");
    }

    /**
     * Remove the entry with the given key.
     *
     * @param key the key to remove
     * @return the removed value, or {@code null} if the key was not present
     */
    public V remove(K key) {
        // TODO: 1. Compute the bucket index.
        //       2. Walk the chain, keeping track of the previous node.
        //       3. If found, unlink the node and decrement size.
        //       4. Return the removed value.

        throw new UnsupportedOperationException("TODO: implement remove()");
    }

    /**
     * @param key the key to check
     * @return {@code true} if the map contains the key
     */
    public boolean containsKey(K key) {
        // TODO: Delegate to get(key) != null — but be careful with null values!
        //       A more correct approach walks the chain and checks for key equality.

        throw new UnsupportedOperationException("TODO: implement containsKey()");
    }

    /**
     * @return the number of key-value pairs in the map
     */
    public int size() {
        return size;
    }

    /**
     * Double the capacity and rehash all entries.
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        // TODO: 1. Create a new array with double the capacity.
        //       2. Iterate over all entries in the old array.
        //       3. For each entry, recompute the bucket index in the new array and insert.
        //       4. Replace the old array with the new one.

        throw new UnsupportedOperationException("TODO: implement resize()");
    }

    /**
     * Compute a non-negative bucket index for the given key.
     */
    private int bucketIndex(K key) {
        if (key == null) return 0;
        return Math.abs(key.hashCode() % buckets.length);
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        // TODO: Return an iterator that walks through all buckets and all entries
        //       within each bucket, yielding one Entry<K,V> at a time.

        // Stub iterator for compilation:
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                throw new UnsupportedOperationException("TODO: implement hasNext()");
            }

            @Override
            public Entry<K, V> next() {
                throw new UnsupportedOperationException("TODO: implement next()");
            }
        };
    }
}
