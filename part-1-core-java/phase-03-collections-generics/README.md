# Phase 03 — Collections & Generics

> **Duration:** ~2–3 weeks (~27 hours)
> **Pace:** Methodical — understand the data structures beneath the APIs. When you know *how* a HashMap works, you stop writing bugs that only appear at scale.
> **Goal:** Master the Collections Framework internals, choose the right data structure by default, and wield generics with confidence (including wildcards and PECS).

---

## Learning Objectives

By the end of this phase you will be able to:

1. Draw the Collections Framework hierarchy from memory (Collection → List/Set/Queue; Map is separate).
2. Explain the internal mechanics of `ArrayList`, `LinkedList`, `HashSet`, `HashMap`, `TreeMap`, and `PriorityQueue`.
3. Choose the optimal collection for a given access pattern (random access, sorted order, uniqueness, FIFO/LIFO, key-value lookup).
4. Implement `Comparable` and build complex `Comparator` chains using `Comparator.comparing()`.
5. Explain iterators, fail-fast vs. fail-safe behavior, and the `ConcurrentModificationException` trap.
6. Write generic classes and methods with bounded type parameters and wildcards.
7. Apply the PECS principle (Producer Extends, Consumer Super) to design flexible APIs.
8. Understand type erasure and its implications (no `new T()`, no `instanceof T`).

---

## Topics & Estimated Hours

### 1. Collection Framework Hierarchy Overview (1 h)

- Root interfaces: `Iterable` → `Collection` → `List`, `Set`, `Queue`.
- `Map` is separate — does NOT extend `Collection`.
- `Deque` extends `Queue`; `SortedSet` extends `Set`; `NavigableMap` extends `SortedMap`.
- Key principle: program to the interface (`List<String> list = new ArrayList<>()`).
- `Collections` (utility class) vs. `Collection` (interface) — don't confuse them.

### 2. List Implementations (3 h)

#### ArrayList
- Backed by a resizable `Object[]` array.
- Default initial capacity: 10. Grow strategy: `newCapacity = oldCapacity + (oldCapacity >> 1)` (≈1.5x).
- O(1) random access (`get(i)`), O(1) amortized `add()` at end, O(n) `add(i)` / `remove(i)` (shift required).
- When to use: default choice for most list operations.

#### LinkedList
- Doubly-linked list of `Node` objects (each holds `item`, `prev`, `next`).
- O(1) `addFirst` / `addLast`, O(n) `get(i)` (must traverse).
- Implements both `List` and `Deque` — can be used as a stack or queue.
- When to use: frequent insertions/removals at both ends; rarely used in practice (cache-unfriendly).

#### Vector & Stack
- Legacy synchronized collections — prefer `ArrayList` + `Collections.synchronizedList()` or `CopyOnWriteArrayList`.

### 3. Set Implementations (3 h)

#### HashSet
- Backed by a `HashMap<E, PRESENT>` where `PRESENT` is a dummy `Object`.
- O(1) `add`, `contains`, `remove` (amortized, assuming good hash distribution).
- No ordering guarantees.

#### LinkedHashSet
- Maintains insertion order via a doubly-linked list threading through the hash table entries.
- Slightly more memory than `HashSet`; same O(1) operations.

#### TreeSet
- Backed by a `TreeMap` (Red-Black tree).
- O(log n) `add`, `contains`, `remove`. Elements are sorted (natural order or `Comparator`).
- Implements `NavigableSet`: `floor`, `ceiling`, `higher`, `lower`, `subSet`, `headSet`, `tailSet`.

#### EnumSet
- Specialized `Set` for `enum` types — backed by a bit vector.
- Extremely fast and memory-efficient. Use `EnumSet.of(...)`, `EnumSet.allOf(...)`.

### 4. Map Implementations (3 h)

#### HashMap
- Array of buckets (initially 16, load factor 0.75).
- **Hashing:** `key.hashCode()` → spread/mix → bucket index.
- **Collision handling:** linked list in each bucket; when a bucket exceeds **8 entries** AND total capacity ≥ 64, the list is converted to a balanced tree (treeification) → O(log n) worst case instead of O(n).
- **Resize:** when `size > capacity * loadFactor`, capacity doubles, all entries are rehashed.
- `null` key allowed (stored in bucket 0).

#### LinkedHashMap
- Extends `HashMap` with a doubly-linked list maintaining insertion order.
- Constructor parameter `accessOrder = true` → maintains access order (most recently accessed at the tail).
- Override `removeEldestEntry()` to build an **LRU cache**.

#### TreeMap
- Red-Black tree. O(log n) for `get`, `put`, `remove`.
- Keys are sorted (natural order or custom `Comparator`).
- Implements `NavigableMap`: `floorEntry`, `ceilingEntry`, `subMap`, etc.

#### WeakHashMap
- Keys are `WeakReference`s — entries are automatically removed when the key is garbage collected.
- Use case: caches where you don't want to prevent GC of key objects.

### 5. Queue / Deque (2 h)

#### PriorityQueue
- Min-heap by default (smallest element at the head).
- O(log n) `offer` / `poll`, O(1) `peek`, O(n) `remove(Object)`.
- Provide a `Comparator` for custom ordering.

#### ArrayDeque
- Resizable circular array. O(1) amortized for `addFirst`, `addLast`, `removeFirst`, `removeLast`.
- Faster than `LinkedList` for both stack (LIFO) and queue (FIFO) usage.
- **Preferred** over `Stack` (legacy) and `LinkedList` (slower for deque ops).

### 6. Comparable vs. Comparator (2 h)

- **`Comparable<T>`:** defines *natural ordering*. Implement `compareTo(T)` in the class itself. Single ordering.
- **`Comparator<T>`:** defines *external ordering*. Multiple orderings possible.
- `Comparator.comparing(Employee::getSalary)` — method reference based.
- Chaining: `.thenComparing(Employee::getName)`.
- Reverse: `.reversed()`.
- Null-safe: `Comparator.nullsFirst(...)`, `Comparator.nullsLast(...)`.
- `Collections.sort(list)` uses natural ordering; `list.sort(comparator)` uses the provided comparator.

### 7. Iterator, Iterable, ListIterator, Fail-Fast vs. Fail-Safe (2 h)

- **`Iterable<T>`:** has `iterator()` method. Anything implementing it works with the enhanced for-loop.
- **`Iterator<T>`:** `hasNext()`, `next()`, `remove()`. One-directional.
- **`ListIterator<T>`:** extends `Iterator` with `hasPrevious()`, `previous()`, `add()`, `set()`. Bidirectional.
- **Fail-fast iterators** (ArrayList, HashMap): throw `ConcurrentModificationException` if the collection is structurally modified during iteration (except through the iterator's own `remove()`).
- **Fail-safe iterators** (CopyOnWriteArrayList, ConcurrentHashMap): iterate over a snapshot; no exception, but may not reflect concurrent modifications.
- The `modCount` field in `AbstractList` — how fail-fast detection works internally.

### 8. Generics (6 h)

#### Type Parameters and Bounded Types
- Generic class: `class Box<T> { T item; }`.
- Generic method: `<T> T firstOrNull(List<T> list)`.
- Bounded type: `<T extends Comparable<T>>` — T must implement Comparable.
- Multiple bounds: `<T extends Serializable & Comparable<T>>`.

#### Wildcards
- Unbounded: `List<?>` — read-only (can only read as `Object`).
- Upper bounded: `List<? extends Number>` — **producer** (can read as `Number`, cannot add).
- Lower bounded: `List<? super Integer>` — **consumer** (can add `Integer`, reads as `Object`).

#### PECS Principle
- **P**roducer **E**xtends, **C**onsumer **S**uper.
- If you read from a structure, use `? extends T`.
- If you write to a structure, use `? super T`.
- If you read AND write, use an exact type `T`.
- Example: `Collections.copy(List<? super T> dest, List<? extends T> src)`.

#### Type Erasure
- Generics are a compile-time feature. At runtime, `List<String>` and `List<Integer>` are both `List`.
- Implications: no `new T()`, no `new T[]`, no `instanceof List<String>`.
- Bridge methods generated by the compiler for covariant overrides.
- Reifiable types vs. non-reifiable types; `@SafeVarargs` for generic varargs.

### 9. Collections Utility Class (1 h)

- `Collections.unmodifiableList/Set/Map(...)` — read-only views (not truly immutable; wrap a mutable source).
- `List.of(...)`, `Set.of(...)`, `Map.of(...)` (Java 9+) — truly immutable collections.
- `Collections.synchronizedList/Set/Map(...)` — thread-safe wrappers.
- `Collections.sort(list)`, `Collections.binarySearch(list, key)` — sorting and searching.
- `Collections.singletonList(item)`, `Collections.emptyList()` — convenience factories.

### 10. ConcurrentHashMap Preview (1 h)

- Segment-based locking (pre-Java 8) → node-level CAS + synchronized (Java 8+).
- No `ConcurrentModificationException`; weakly consistent iterators.
- `computeIfAbsent`, `merge`, `forEach` — atomic compound operations.
- `null` keys and values are NOT allowed (unlike `HashMap`).
- Full deep dive in the Concurrency phase — this is just an appetizer.

---

## References

| Resource | Scope |
|----------|-------|
| *Effective Java*, 3rd ed. — Items 26–33 (Generics), 42–48 (Lambdas & Streams preview) | Generics best practices, PECS, raw types |
| [Java Collections Framework — Oracle Docs](https://docs.oracle.com/javase/tutorial/collections/index.html) | Official tutorial |
| [Baeldung — Guide to Java Collections](https://www.baeldung.com/java-collections) | Comprehensive practical guide |
| [Baeldung — Java HashMap Internals](https://www.baeldung.com/java-hashmap) | How HashMap really works |
| [Baeldung — Java Generics](https://www.baeldung.com/java-generics) | Generics with examples |
| *Head First Java*, 3rd ed. — Chapters 11–12 | Collections and generics intro |
| [Angelika Langer — Java Generics FAQ](http://www.angelikalanger.com/GenericsFAQ/JavaGenericsFAQ.html) | The definitive generics reference |

---

## Exercises

### Exercise 1 — Custom HashMap

**Business Context:** Understanding how `HashMap` works internally is essential for writing correct and performant Java code. Build one from scratch.

**Requirements:**

1. Implement `SimpleHashMap<K, V>` with:
   - `void put(K key, V value)` — insert or update. Handle `null` key.
   - `V get(K key)` — return the value or `null` if not found.
   - `V remove(K key)` — remove and return the value, or `null`.
   - `boolean containsKey(K key)`
   - `int size()`
2. Use an array of buckets. Each bucket is a linked list of `Entry<K, V>` nodes.
3. Resize when `size > capacity * 0.75`. New capacity = old capacity * 2. Rehash all entries.
4. Implement `Iterable<SimpleHashMap.Entry<K, V>>` so you can use it in a for-each loop.

**Bonus:**
- Implement a `Set<K> keySet()` view backed by the map.
- Convert long chains (> 8 entries) to a balanced tree node (like real `HashMap`).

**Starter file:** `exercises/src/main/java/exercises/SimpleHashMap.java`

---

### Exercise 2 — LRU Cache

**Business Context:** An API gateway needs to cache the most recently accessed responses. The cache has a fixed capacity and must evict the least recently used entry when full.

**Requirements:**

1. Implement `LRUCache<K, V>` with:
   - `LRUCache(int capacity)` — fixed maximum size.
   - `V get(K key)` — return the value and mark it as recently used. Return `null` if absent.
   - `void put(K key, V value)` — insert or update. If at capacity, evict the least recently used entry first.
2. Both `get` and `put` must be **O(1)**.
3. Approach options:
   - **Option A:** Use `LinkedHashMap` with `accessOrder = true` and override `removeEldestEntry`.
   - **Option B:** Build from scratch with a `HashMap<K, Node>` + a custom doubly-linked list.

**Bonus:**
- Make it thread-safe using `ReentrantReadWriteLock` or `synchronized`.
- Add an `evictionCount()` method that tracks how many entries have been evicted.

**Starter file:** `exercises/src/main/java/exercises/LRUCache.java`

---

### Exercise 3 — Inventory Management System

**Business Context:** A retail company needs an inventory system that supports fast lookup by SKU, sorted browsing by price, and automatic low-stock alerts.

**Requirements:**

1. **`Product`** class: `sku` (String), `name` (String), `category` (String), `price` (BigDecimal), `stockQuantity` (int). Implement `Comparable<Product>` (sort by price ascending, then by name).

2. **`InventoryManager`** class:
   - `HashMap<String, Product>` for O(1) lookup by SKU.
   - `TreeMap<BigDecimal, List<Product>>` for browsing products sorted by price.
   - `PriorityQueue<Product>` for low-stock alerts (min-heap by `stockQuantity`).
   - Methods: `addProduct(Product)`, `findBySku(String)`, `getProductsInPriceRange(BigDecimal min, BigDecimal max)`, `getLowStockAlerts(int threshold)`.

3. Use `Comparator.comparing()` chains for sorting products by multiple criteria.

**Bonus:**
- Implement a generic `Repository<T extends Identifiable>` with CRUD operations.
- Build a generic filtering/sorting pipeline: `repository.findAll().filter(predicate).sortedBy(comparator)`.

**Starter file:** `exercises/src/main/java/exercises/inventory/Product.java`

---

### Exercise 4 — Generic Repository Pattern

**Business Context:** Every microservice needs a data access layer. Instead of duplicating code, create a generic, type-safe repository.

**Requirements:**

1. **`Repository<T, ID>`** interface:
   - `Optional<T> findById(ID id)`
   - `List<T> findAll()`
   - `T save(T entity)`
   - `boolean delete(ID id)`
   - `List<T> findBy(Predicate<T> filter)`

2. **`Identifiable<ID>`** interface:
   - `ID getId()`

3. **`InMemoryRepository<T extends Identifiable<ID>, ID>`**:
   - Backed by a `HashMap<ID, T>`.
   - Implements all `Repository` methods.

4. Demonstrate:
   - Bounded type parameters: `T extends Identifiable<ID>`.
   - Wildcards: a method that accepts `Repository<? extends Identifiable<?>, ?>`.
   - PECS: a method that copies entities from a producer to a consumer.

**Bonus:**
- Add pagination: `Page<T> findAll(int pageNumber, int pageSize)`.
- Add a `Specification<T>` pattern for composable queries: `spec1.and(spec2).or(spec3)`.

**Starter files:**
- `exercises/src/main/java/exercises/repository/Repository.java`
- `exercises/src/main/java/exercises/repository/InMemoryRepository.java`

---

## Self-Assessment Checklist

Before moving to Phase 04, confirm:

- [ ] I can draw the Collection hierarchy (List, Set, Queue, Map) and name at least two implementations for each.
- [ ] I can explain how `HashMap` handles collisions, when it resizes, and what treeification is.
- [ ] I know when to use `ArrayList` vs. `LinkedList` vs. `ArrayDeque` and can justify it with Big-O.
- [ ] I can explain the difference between `HashSet`, `LinkedHashSet`, and `TreeSet`.
- [ ] I can implement `Comparable`, create `Comparator` chains, and sort collections both ways.
- [ ] I understand fail-fast vs. fail-safe iterators and know how to safely remove elements during iteration.
- [ ] I can write generic classes with bounded type parameters and explain the PECS principle with an example.
- [ ] I understand type erasure and can list three things you cannot do with generics at runtime.
- [ ] I can explain `List.of()` vs. `Collections.unmodifiableList()` and when each is appropriate.
- [ ] I have completed all four exercises, including the custom `HashMap` and `LRUCache`.
