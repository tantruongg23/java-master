# Interview Questions — Phase 03: Collections & Generics

> **75 questions** organized by topic and difficulty level.
> Each question includes a model answer and, where applicable, code examples.
> Difficulty: **Junior** (0–2 yrs) | **Mid** (2–5 yrs) | **Senior** (5+ yrs)

---

## Section 1: Collection Framework Hierarchy

### Q1. Draw the Java Collections Framework hierarchy. Where does `Map` fit? *(Junior)*

**Model Answer:**

```
Iterable
  └── Collection
        ├── List       (ordered, duplicates allowed)
        │     ├── ArrayList
        │     ├── LinkedList
        │     └── Vector (legacy)
        ├── Set        (no duplicates)
        │     ├── HashSet
        │     ├── LinkedHashSet
        │     └── TreeSet
        └── Queue      (FIFO / priority)
              ├── PriorityQueue
              └── Deque
                    └── ArrayDeque

Map (separate — does NOT extend Collection)
  ├── HashMap
  ├── LinkedHashMap
  ├── TreeMap
  └── WeakHashMap
```

`Map` is **not** part of the `Collection` hierarchy. It represents key-value pairs, not a single group of elements. However, you can get collection views from a Map: `keySet()` returns a `Set`, `values()` returns a `Collection`, and `entrySet()` returns a `Set<Map.Entry>`.

---

### Q2. What is the difference between `Collection` (interface) and `Collections` (class)? *(Junior)*

**Model Answer:**

- `Collection` is the **root interface** of the collections hierarchy (parent of `List`, `Set`, `Queue`).
- `Collections` is a **utility class** (`java.util.Collections`) with static methods like `sort()`, `binarySearch()`, `unmodifiableList()`, `synchronizedList()`, `emptyList()`, etc.

They are completely unrelated despite the similar name.

---

### Q3. Why should you "program to the interface" when declaring collection variables? *(Junior)*

**Model Answer:**

```java
// Good — can swap implementation without changing client code
List<String> names = new ArrayList<>();

// Bad — locks you into ArrayList
ArrayList<String> names = new ArrayList<>();
```

Programming to the interface means the rest of the code depends on `List`, not `ArrayList`. If you later decide to switch to `LinkedList` or `CopyOnWriteArrayList`, only the instantiation line changes. This is the **Dependency Inversion Principle** applied to data structures.

---

## Section 2: List Implementations

### Q4. Explain the internal structure of `ArrayList`. What is its default capacity and growth strategy? *(Junior)*

**Model Answer:**

`ArrayList` is backed by a resizable `Object[]` array.

- **Default initial capacity:** 10 (when first element is added).
- **Growth strategy:** `newCapacity = oldCapacity + (oldCapacity >> 1)`, which is approximately 1.5x the old capacity.
- When the array is full and you `add()`, a new larger array is allocated and elements are copied via `Arrays.copyOf()`.

Performance characteristics:
- `get(i)`: O(1) — direct array index access
- `add(e)` at end: O(1) amortized — occasionally O(n) during resize
- `add(i, e)` at arbitrary index: O(n) — must shift subsequent elements
- `remove(i)`: O(n) — must shift subsequent elements

---

### Q5. When would you use `LinkedList` over `ArrayList`? When would you NOT? *(Mid)*

**Model Answer:**

`LinkedList` is a doubly-linked list where each node holds `item`, `prev`, and `next` references.

**Use `LinkedList` when:**
- You need frequent O(1) insertions/removals at both ends (it implements `Deque`)
- You're using it strictly as a queue or deque

**Do NOT use `LinkedList` when:**
- You need random access — `get(i)` is O(n) because it must traverse from head or tail
- You care about memory overhead — each element requires a `Node` object with two extra pointers
- Cache performance matters — elements are scattered in memory (not contiguous), causing CPU cache misses

**In practice**, `ArrayList` or `ArrayDeque` is preferred in almost all cases. Even for queue operations, `ArrayDeque` outperforms `LinkedList` due to better cache locality.

---

### Q6. Why are `Vector` and `Stack` considered legacy? What should you use instead? *(Junior)*

**Model Answer:**

`Vector` and `Stack` are legacy classes from Java 1.0 that synchronize **every method** — even when thread safety isn't needed. This creates unnecessary overhead.

Modern replacements:
- `Vector` → `ArrayList` (unsynchronized) or `Collections.synchronizedList(new ArrayList<>())` if synchronization is needed, or `CopyOnWriteArrayList` for read-heavy concurrent access.
- `Stack` → `ArrayDeque` used as a stack (`push`/`pop`/`peek`). `ArrayDeque` is faster because it uses a contiguous circular array.

---

### Q7. What happens internally when you call `ArrayList.add(0, element)` on a list with 1 million elements? *(Mid)*

**Model Answer:**

1. The method checks if the internal array needs resizing. If so, a new array is allocated (1.5x size) and all elements are copied.
2. Regardless of resizing, **all 1 million existing elements must be shifted right by one position** via `System.arraycopy()`.
3. The new element is placed at index 0.

This is an **O(n)** operation. For a list with 1 million elements, it copies ~1 million elements. If you frequently insert at the beginning, consider `ArrayDeque.addFirst()` (O(1)) or `LinkedList.addFirst()` (O(1)).

---

## Section 3: Set Implementations

### Q8. How does `HashSet` work internally? *(Junior)*

**Model Answer:**

`HashSet` is backed by a `HashMap<E, Object>`. Every element you add to the `HashSet` becomes a **key** in the internal `HashMap`, and the value is a constant dummy object called `PRESENT` (a static `new Object()`).

```java
// Inside HashSet source code
private static final Object PRESENT = new Object();

public boolean add(E e) {
    return map.put(e, PRESENT) == null;
}
```

This means `HashSet` inherits all of `HashMap`'s characteristics: O(1) amortized `add`/`contains`/`remove`, the same hashing and bucketing logic, and the same need for proper `hashCode()` and `equals()` implementations.

---

### Q9. What is the difference between `HashSet`, `LinkedHashSet`, and `TreeSet`? When would you use each? *(Junior)*

**Model Answer:**

| Feature | `HashSet` | `LinkedHashSet` | `TreeSet` |
|---------|-----------|-----------------|-----------|
| Order | None | Insertion order | Sorted (natural or Comparator) |
| Backing structure | HashMap | HashMap + linked list | TreeMap (Red-Black tree) |
| `add`/`contains`/`remove` | O(1) amortized | O(1) amortized | O(log n) |
| Null elements | One null allowed | One null allowed | No (throws NPE with natural ordering) |
| Memory | Lowest | Slightly more (linked list pointers) | More (tree nodes) |

**When to use:**
- `HashSet` — default choice when you just need uniqueness and don't care about order
- `LinkedHashSet` — need uniqueness AND want to preserve the order elements were added
- `TreeSet` — need uniqueness AND elements must be sorted (e.g., displaying a sorted list of unique categories)

---

### Q10. What is `EnumSet` and why is it special? *(Mid)*

**Model Answer:**

`EnumSet` is a specialized `Set` implementation designed exclusively for `enum` types. Internally, it uses a **bit vector** (a single `long` for enums with ≤64 constants, or a `long[]` for larger enums).

```java
enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }

Set<Day> weekend = EnumSet.of(Day.SAT, Day.SUN);
Set<Day> weekdays = EnumSet.complementOf(weekend);
Set<Day> all = EnumSet.allOf(Day.class);
```

Why it's special:
- **Extremely fast** — all operations are bitwise (`add` = set a bit, `contains` = check a bit)
- **Very memory efficient** — a set of up to 64 enum constants uses a single `long` (8 bytes)
- **Iteration order** matches the enum declaration order
- Should always be preferred over `HashSet<MyEnum>` — it's both faster and smaller

---

### Q11. If two objects are `equals()` but have different `hashCode()` values, can they coexist in a `HashSet`? *(Mid)*

**Model Answer:**

**Yes, they can both exist in the `HashSet`** — and that's the bug. Different `hashCode()` values mean they'll likely land in different buckets. The `HashSet` will never even compare them with `equals()` because it looks in the wrong bucket.

This violates the **`hashCode` contract**: if `a.equals(b)` is true, then `a.hashCode()` must equal `b.hashCode()`. The reverse isn't required — equal hash codes don't imply equality (collisions are expected).

Result: you get "duplicate" entries in your set, defeating its entire purpose. This is one of the most common bugs in Java.

---

## Section 4: Map Implementations

### Q12. Explain the internal structure of `HashMap`. Walk through what happens when you call `put(key, value)`. *(Mid)*

**Model Answer:**

`HashMap` uses an **array of buckets** (called `Node<K,V>[] table`). Default initial capacity is 16, load factor is 0.75.

When you call `put(key, value)`:

1. **Compute hash:** `hash = key.hashCode()` is further spread using `hash ^ (hash >>> 16)` to reduce collisions.
2. **Find bucket:** `index = hash & (capacity - 1)` (bitwise AND because capacity is always a power of 2).
3. **Check bucket:**
   - If the bucket is empty → create a new `Node` and place it there.
   - If the bucket has entries → traverse the chain. If a node with the same key (checked via `hashCode` + `equals`) is found, **replace** the value. Otherwise, append a new node.
4. **Treeification:** If a bucket's chain exceeds **8 entries** AND the table capacity is ≥ 64, the linked list is converted to a **Red-Black tree** (O(log n) worst case instead of O(n)).
5. **Resize:** If `size > capacity * loadFactor` (e.g., > 12 when capacity is 16), the table doubles to 32, and all entries are rehashed to new positions.

---

### Q13. Why does `HashMap` require capacity to be a power of 2? *(Senior)*

**Model Answer:**

When capacity is a power of 2, the bucket index can be calculated with `hash & (capacity - 1)` instead of `hash % capacity`. Bitwise AND is significantly faster than modulo division.

Additionally, when resizing (doubling), each entry either stays in its current bucket or moves to `oldIndex + oldCapacity`. This can be determined by checking a single bit of the hash, making the resize operation efficient without full rehashing.

```
capacity = 16  →  capacity - 1 = 15 = 0000 1111
hash & 0000 1111 → extracts the last 4 bits as bucket index

capacity = 32  →  capacity - 1 = 31 = 0001 1111
hash & 0001 1111 → extracts the last 5 bits
```

---

### Q14. What is treeification in `HashMap`? When and why does it happen? *(Mid)*

**Model Answer:**

Treeification is `HashMap`'s defense against pathological hash collisions. When a single bucket's chain length exceeds **8 entries** and the table capacity is at least **64**, the linked list in that bucket is converted to a **balanced Red-Black tree**.

**Why:**
- A linked list chain gives O(n) lookup in the worst case (all keys map to the same bucket).
- A Red-Black tree gives O(log n) lookup even in the worst case.
- This was introduced in Java 8 to protect against hash-flooding denial-of-service attacks.

**When it reverses (untreeify):** If the tree shrinks below **6 entries** (due to removals), it's converted back to a linked list because the tree has higher per-node overhead.

---

### Q15. How would you implement an LRU cache in Java? *(Mid)*

**Model Answer:**

**Option A — Using `LinkedHashMap`** (simplest):

```java
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true); // accessOrder = true
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
```

Setting `accessOrder = true` makes `LinkedHashMap` maintain entries in access order (most recently accessed at the tail). Overriding `removeEldestEntry` triggers automatic eviction when capacity is exceeded.

**Option B — Manual implementation** (HashMap + doubly-linked list):
- `HashMap<K, Node>` for O(1) key lookup
- A doubly-linked list where the head is the least recently used and the tail is the most recently used
- On `get()`: move the accessed node to the tail
- On `put()`: if at capacity, remove the head (LRU entry), then add the new node at the tail

Both achieve O(1) for `get` and `put`.

---

### Q16. What is `WeakHashMap` and when would you use it? *(Senior)*

**Model Answer:**

`WeakHashMap` stores keys as `WeakReference` objects. If a key has no strong references remaining anywhere in the program, the garbage collector can reclaim it — and the corresponding entry is automatically removed from the map.

**Use case:** Caches where you want entries to disappear when the key object is no longer in use elsewhere.

```java
WeakHashMap<Object, String> cache = new WeakHashMap<>();
Object key = new Object();
cache.put(key, "metadata");

// key is still strongly referenced → entry stays
System.out.println(cache.size()); // 1

key = null; // no more strong references
System.gc();
// entry is eligible for removal
System.out.println(cache.size()); // likely 0
```

**Important:** `String` literals should not be used as keys because they live in the string pool and are never garbage collected.

---

### Q17. Compare `HashMap`, `LinkedHashMap`, and `TreeMap`. When would you choose each? *(Junior)*

**Model Answer:**

| Feature | `HashMap` | `LinkedHashMap` | `TreeMap` |
|---------|-----------|-----------------|-----------|
| Order | None | Insertion or access order | Sorted by key |
| `get`/`put` | O(1) amortized | O(1) amortized | O(log n) |
| Null keys | 1 allowed | 1 allowed | Not allowed (natural ordering) |
| Implements | `Map` | `Map` | `NavigableMap`, `SortedMap` |

**Choose:**
- `HashMap` — default choice for key-value lookups. Best performance.
- `LinkedHashMap` — need to iterate in the order entries were inserted (e.g., preserving JSON key order), or build an LRU cache with `accessOrder=true`.
- `TreeMap` — need keys sorted (e.g., range queries with `subMap(from, to)`, `floorKey()`, `ceilingKey()`).

---

### Q18. Can you use a mutable object as a `HashMap` key? What goes wrong? *(Mid)*

**Model Answer:**

Technically yes, but it's dangerous. If you mutate the key after inserting it, its `hashCode()` may change. The entry is now in the **wrong bucket** — `HashMap` can't find it with `get()`, `containsKey()`, or `remove()`.

```java
List<String> key = new ArrayList<>(List.of("a", "b"));
Map<List<String>, String> map = new HashMap<>();
map.put(key, "found");

System.out.println(map.get(key)); // "found"

key.add("c"); // mutate the key → hashCode changes

System.out.println(map.get(key)); // null! — entry is lost
System.out.println(map.size());   // 1 — entry still exists, just unreachable
```

**Best practice:** Use immutable objects as map keys (String, Integer, enum, or custom immutable classes).

---

## Section 5: Queue & Deque

### Q19. What is a `PriorityQueue`? Is it FIFO? *(Junior)*

**Model Answer:**

`PriorityQueue` is **not** FIFO. It's a **min-heap** that always dequeues the smallest element first (according to natural ordering or a provided `Comparator`).

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.offer(30);
pq.offer(10);
pq.offer(20);

System.out.println(pq.poll()); // 10 (smallest, not first-in)
System.out.println(pq.poll()); // 20
System.out.println(pq.poll()); // 30
```

Performance:
- `offer()` / `poll()`: O(log n)
- `peek()`: O(1)
- `remove(Object)`: O(n) — must search linearly
- **Not thread-safe.** Use `PriorityBlockingQueue` for concurrent access.

---

### Q20. Why is `ArrayDeque` preferred over `Stack` and `LinkedList`? *(Mid)*

**Model Answer:**

| | `ArrayDeque` | `Stack` | `LinkedList` |
|-|--------------|---------|-------------|
| Backing | Circular array | Resizable array | Doubly-linked nodes |
| Synchronization | None (fast) | Every method synchronized (slow) | None |
| Cache locality | Contiguous memory (fast) | Contiguous memory | Scattered nodes (cache misses) |
| Null elements | Not allowed | Allowed | Allowed |

`ArrayDeque`:
- O(1) amortized for `push`/`pop` (stack) and `offer`/`poll` (queue)
- No per-element object overhead (no `Node` wrappers)
- Better CPU cache performance than `LinkedList`
- No unnecessary synchronization like `Stack`

The Javadoc itself states: *"This class is likely to be faster than Stack when used as a stack, and faster than LinkedList when used as a queue."*

---

### Q21. What is the difference between `offer()`/`poll()`/`peek()` and `add()`/`remove()`/`element()` on a Queue? *(Junior)*

**Model Answer:**

Both sets of methods do the same thing but differ in how they handle failure:

| Operation | Throws Exception | Returns Special Value |
|-----------|-----------------|----------------------|
| Insert | `add(e)` → `IllegalStateException` if full | `offer(e)` → returns `false` if full |
| Remove | `remove()` → `NoSuchElementException` if empty | `poll()` → returns `null` if empty |
| Examine | `element()` → `NoSuchElementException` if empty | `peek()` → returns `null` if empty |

**Best practice:** Use `offer`/`poll`/`peek` when capacity limits or empty queues are expected conditions (not exceptional). Use `add`/`remove`/`element` when failure is a programming error that should throw.

---

## Section 6: Comparable vs. Comparator

### Q22. What is the difference between `Comparable` and `Comparator`? *(Junior)*

**Model Answer:**

| | `Comparable<T>` | `Comparator<T>` |
|-|-----------------|-----------------|
| Package | `java.lang` | `java.util` |
| Method | `compareTo(T other)` | `compare(T a, T b)` |
| Where defined | Inside the class being sorted | External — a separate class or lambda |
| Orderings | One (natural ordering) | Many (any number of custom orderings) |
| Modifies class? | Yes — class must `implements Comparable<T>` | No — the class is untouched |

```java
// Comparable — natural ordering defined inside the class
public class Employee implements Comparable<Employee> {
    public int compareTo(Employee other) {
        return this.name.compareTo(other.name);
    }
}

// Comparator — external, multiple orderings
Comparator<Employee> bySalary = Comparator.comparing(Employee::getSalary);
Comparator<Employee> byNameDesc = Comparator.comparing(Employee::getName).reversed();
```

---

### Q23. How do you build a multi-field sort using `Comparator`? *(Junior)*

**Model Answer:**

```java
Comparator<Employee> comparator = Comparator
    .comparing(Employee::getDepartment)
    .thenComparing(Employee::getSalary, Comparator.reverseOrder())
    .thenComparing(Employee::getName);

employees.sort(comparator);
```

This sorts by department (ascending), then by salary (descending), then by name (ascending) as a tiebreaker.

Additional utilities:
- `Comparator.nullsFirst(comparator)` — null values sort before non-null
- `Comparator.nullsLast(comparator)` — null values sort after non-null
- `Comparator.naturalOrder()` — the natural ordering comparator
- `.reversed()` — reverses any comparator

---

### Q24. What happens if `compareTo()` is inconsistent with `equals()`? *(Senior)*

**Model Answer:**

The `Comparable` contract strongly recommends (but doesn't enforce) that `compareTo()` and `equals()` are consistent — i.e., `a.compareTo(b) == 0` if and only if `a.equals(b)`.

If they're inconsistent:
- `TreeSet` and `TreeMap` use `compareTo()` for equality, so two objects where `compareTo()` returns 0 are treated as duplicates — even if `equals()` returns false.
- `HashSet` and `HashMap` use `equals()` and `hashCode()` — they may allow both objects.

```java
// Inconsistent: compareTo compares by salary, equals compares by id
BigDecimal a = new BigDecimal("1.0");
BigDecimal b = new BigDecimal("1.00");

a.equals(b);      // false (different scale)
a.compareTo(b);   // 0 (same numeric value)

Set<BigDecimal> hashSet = new HashSet<>();
hashSet.add(a); hashSet.add(b);
hashSet.size(); // 2

Set<BigDecimal> treeSet = new TreeSet<>();
treeSet.add(a); treeSet.add(b);
treeSet.size(); // 1 — a and b are "equal" by compareTo
```

---

## Section 7: Iterators & Fail-Fast / Fail-Safe

### Q25. What is the difference between `Iterator` and `ListIterator`? *(Junior)*

**Model Answer:**

| Feature | `Iterator` | `ListIterator` |
|---------|-----------|----------------|
| Direction | Forward only | Bidirectional (forward + backward) |
| Methods | `hasNext()`, `next()`, `remove()` | All of Iterator + `hasPrevious()`, `previous()`, `add()`, `set()`, `nextIndex()`, `previousIndex()` |
| Available on | Any `Collection` | Only `List` implementations |
| Can add elements? | No | Yes, via `add()` |
| Can replace elements? | No | Yes, via `set()` |

`ListIterator` extends `Iterator` and is obtained via `list.listIterator()` or `list.listIterator(index)`.

---

### Q26. What is `ConcurrentModificationException`? How do you avoid it? *(Junior)*

**Model Answer:**

`ConcurrentModificationException` is thrown when you structurally modify a collection (add/remove elements) while iterating over it with a **fail-fast** iterator.

```java
// BUG — throws ConcurrentModificationException
List<String> list = new ArrayList<>(List.of("a", "b", "c"));
for (String s : list) {
    if (s.equals("b")) {
        list.remove(s); // structural modification during iteration
    }
}
```

**How to avoid it:**

```java
// Option 1: Use Iterator.remove()
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (it.next().equals("b")) {
        it.remove(); // safe — uses the iterator's own remove
    }
}

// Option 2: Use removeIf() (Java 8+)
list.removeIf(s -> s.equals("b"));

// Option 3: Use a fail-safe collection
List<String> list = new CopyOnWriteArrayList<>(List.of("a", "b", "c"));
// iterates over a snapshot — no exception
```

---

### Q27. What is the `modCount` field? How does fail-fast detection work? *(Mid)*

**Model Answer:**

`modCount` is a `protected transient int` field in `AbstractList` (and similar abstract classes). It tracks the number of **structural modifications** (additions, removals, resizes) made to the collection.

When an iterator is created, it saves the current `modCount` as `expectedModCount`. On each call to `next()` or `remove()`, the iterator checks:

```java
if (modCount != expectedModCount)
    throw new ConcurrentModificationException();
```

If the collection was modified by anything other than the iterator's own methods (which update `expectedModCount`), the check fails.

**Important:** This is a best-effort mechanism, not guaranteed in concurrent scenarios. It's designed to catch bugs early, not to provide thread safety.

---

### Q28. What is the difference between fail-fast and fail-safe iterators? Give examples of each. *(Mid)*

**Model Answer:**

| | Fail-Fast | Fail-Safe |
|-|-----------|-----------|
| Behavior | Throws `ConcurrentModificationException` on structural modification | Never throws, works on a snapshot or uses weak consistency |
| Data view | Current data | May show stale data |
| Memory | No extra overhead | May copy the underlying data |
| Examples | `ArrayList`, `HashMap`, `HashSet`, `LinkedList` | `CopyOnWriteArrayList`, `ConcurrentHashMap`, `ConcurrentSkipListSet` |

Fail-safe collections are in `java.util.concurrent`. They trade perfect consistency for thread safety without external synchronization.

---

## Section 8: Generics — Type Parameters & Bounded Types

### Q29. What are generics and why were they introduced? *(Junior)*

**Model Answer:**

Generics (introduced in Java 5) enable **parameterized types** — classes, interfaces, and methods that operate on types specified by the caller.

**Before generics:**
```java
List list = new ArrayList();
list.add("hello");
String s = (String) list.get(0); // manual cast — ClassCastException risk
list.add(42);                     // compiles, fails at runtime
```

**With generics:**
```java
List<String> list = new ArrayList<>();
list.add("hello");
String s = list.get(0);          // no cast
list.add(42);                     // COMPILE ERROR
```

Benefits:
1. **Type safety at compile time** — bugs caught early
2. **No manual casts** — cleaner, less error-prone code
3. **Code reuse** — write one `List<T>` that works for any type

---

### Q30. What is a bounded type parameter? Give an example with multiple bounds. *(Mid)*

**Model Answer:**

A bounded type parameter restricts which types can be used as the type argument.

```java
// Upper bound — T must implement Comparable
public <T extends Comparable<T>> T findMax(List<T> list) {
    T max = list.get(0);
    for (T item : list) {
        if (item.compareTo(max) > 0) max = item;
    }
    return max;
}

// Multiple bounds — T must implement both Serializable AND Comparable
public <T extends Serializable & Comparable<T>> void process(T item) { }
```

Rules:
- Use `extends` for both classes and interfaces (not `implements`)
- At most one class bound, and it must come first: `<T extends MyClass & Interface1 & Interface2>`
- You can have multiple interface bounds

---

### Q31. What is the difference between a generic class and a generic method? *(Junior)*

**Model Answer:**

**Generic class** — the type parameter is declared at the class level and shared across the entire class:
```java
public class Box<T> {
    private T item;
    public void set(T item) { this.item = item; }
    public T get() { return item; }
}

Box<String> box = new Box<>();
```

**Generic method** — the type parameter is declared on the method itself and is independent of the class:
```java
public class Util {
    public static <T> T firstOrNull(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }
}

String s = Util.firstOrNull(List.of("a", "b")); // T inferred as String
```

A non-generic class can have generic methods. A generic class can also have generic methods with different type parameters.

---

## Section 9: Generics — Wildcards & PECS

### Q32. What is the difference between `List<Object>`, `List<?>`, and `List<? extends Object>`? *(Mid)*

**Model Answer:**

| Declaration | Can read as | Can add | Accepts |
|-------------|------------|---------|---------|
| `List<Object>` | `Object` | Any `Object` | Only `List<Object>` |
| `List<?>` | `Object` | Nothing (except `null`) | Any `List<X>` |
| `List<? extends Object>` | `Object` | Nothing (except `null`) | Any `List<X>` |

- `List<Object>` is a concrete parameterized type. A `List<String>` is **not** a `List<Object>` (generics are invariant).
- `List<?>` is an unbounded wildcard — it can reference any `List<X>`, but you can only read as `Object` and cannot add.
- `List<? extends Object>` is functionally identical to `List<?>`.

```java
List<String> strings = List.of("a", "b");

List<Object> objects = strings;           // COMPILE ERROR — invariant
List<?> wildcard = strings;               // OK
List<? extends Object> extended = strings; // OK
```

---

### Q33. Explain the PECS principle with a real code example. *(Mid)*

**Model Answer:**

**PECS = Producer Extends, Consumer Super.**

- If a structure **produces** (you read from it), use `? extends T`
- If a structure **consumes** (you write to it), use `? super T`
- If it does both, use the exact type `T`

```java
// Copy from source (producer) to destination (consumer)
public static <T> void copy(List<? super T> dest, List<? extends T> src) {
    for (T item : src) {   // read from src — src is a producer (extends)
        dest.add(item);     // write to dest — dest is a consumer (super)
    }
}

// Usage:
List<Integer> ints = List.of(1, 2, 3);
List<Number> nums = new ArrayList<>();
copy(nums, ints); // T = Integer; dest accepts ? super Integer (Number works)
```

**Why it matters:** Without PECS, you'd have to use `List<T>` for both parameters, and `copy(List<Number>, List<Integer>)` would **not compile** because `List<Integer>` is not `List<Number>`.

---

### Q34. Why can you read from `List<? extends Number>` but not add to it? *(Mid)*

**Model Answer:**

`List<? extends Number>` means "a list of some unknown type that is `Number` or a subclass." It could be a `List<Integer>`, `List<Double>`, or `List<Number>`.

**Reading is safe:** Whatever the actual type, every element IS-A `Number`, so you can always read as `Number`.

**Writing is unsafe:** The compiler doesn't know the exact type. If the list is actually a `List<Integer>`, adding a `Double` would break type safety. Since the compiler can't tell, it **blocks all additions** (except `null`).

```java
List<? extends Number> list = new ArrayList<Integer>();
Number n = list.get(0);    // OK — every element is at least a Number
list.add(3.14);            // COMPILE ERROR — might be List<Integer>
list.add(42);              // COMPILE ERROR — might be List<Double>
list.add(null);            // OK — null is valid for any reference type
```

---

### Q35. Why can you add to `List<? super Integer>` but reads come back as `Object`? *(Mid)*

**Model Answer:**

`List<? super Integer>` means "a list whose element type is `Integer` or a superclass of `Integer`." It could be `List<Integer>`, `List<Number>`, or `List<Object>`.

**Writing is safe:** An `Integer` can be added because it IS-A `Integer`, IS-A `Number`, and IS-A `Object` — no matter which supertype the list actually holds.

**Reading is limited:** The list could hold `Number`s, `Object`s, or `Integer`s. The only safe common type is `Object`.

```java
List<? super Integer> list = new ArrayList<Number>();
list.add(42);              // OK — Integer fits in any of the possibilities
list.add(new Integer(10)); // OK
Object obj = list.get(0);  // Only safe to read as Object
Integer i = list.get(0);   // COMPILE ERROR
```

---

### Q36. What is wrong with this method signature? How would you fix it using PECS? *(Senior)*

```java
public static <T> void addAll(List<T> dest, List<T> src) {
    for (T item : src) {
        dest.add(item);
    }
}
```

**Model Answer:**

The method is overly restrictive. You can't call `addAll(List<Number>, List<Integer>)` because `List<Integer>` is not `List<Number>`.

**Fixed with PECS:**
```java
public static <T> void addAll(List<? super T> dest, List<? extends T> src) {
    for (T item : src) {
        dest.add(item);
    }
}

// Now this works:
List<Number> numbers = new ArrayList<>();
List<Integer> ints = List.of(1, 2, 3);
addAll(numbers, ints); // T inferred as Integer
```

- `src` is a **producer** (we read from it) → `? extends T`
- `dest` is a **consumer** (we write to it) → `? super T`

---

## Section 10: Generics — Type Erasure

### Q37. What is type erasure? What happens to generics at runtime? *(Mid)*

**Model Answer:**

Type erasure is the process by which the Java compiler removes all generic type information during compilation. At runtime, the JVM sees only raw types.

```java
// Source code
List<String> strings = new ArrayList<>();
List<Integer> ints = new ArrayList<>();

// After erasure (what the JVM sees)
List strings = new ArrayList();
List ints = new ArrayList();
```

At runtime, `strings.getClass() == ints.getClass()` is **true** — both are just `ArrayList`.

The compiler inserts **casts** where needed and generates **bridge methods** for covariant overrides. Generics exist purely for compile-time type checking.

---

### Q38. List four things you cannot do with generics due to type erasure. *(Mid)*

**Model Answer:**

| Cannot Do | Reason |
|-----------|--------|
| `new T()` | Compiler doesn't know T's constructor at runtime |
| `new T[10]` | Arrays are reified (know their element type at runtime); generics are erased |
| `obj instanceof List<String>` | At runtime it's just `List` — the `<String>` part is gone |
| `static T field` | Type parameter is per-instance; static is per-class — T is ambiguous |
| `catch (T e)` | Exception handling is at runtime; T doesn't exist at runtime |
| Overload `void foo(List<String>)` and `void foo(List<Integer>)` | After erasure, both are `void foo(List)` — same signature |

**Workaround for `new T()`:** Pass a `Supplier<T>` or `Class<T>` token:
```java
public <T> T create(Supplier<T> factory) {
    return factory.get();
}
```

---

### Q39. What are bridge methods? Why does the compiler generate them? *(Senior)*

**Model Answer:**

Bridge methods are synthetic methods generated by the compiler to preserve polymorphism after type erasure.

```java
public class StringBox implements Comparable<StringBox> {
    private String value;

    @Override
    public int compareTo(StringBox other) { // takes StringBox
        return this.value.compareTo(other.value);
    }
}
```

After erasure, `Comparable<StringBox>` becomes `Comparable`, whose method is `compareTo(Object)`. But our method takes `StringBox`, not `Object`. The compiler generates a **bridge method**:

```java
// Compiler-generated bridge method
public int compareTo(Object other) {
    return compareTo((StringBox) other); // delegates with cast
}
```

This ensures that when `Comparable.compareTo(Object)` is called polymorphically, it correctly dispatches to our `compareTo(StringBox)`. Bridge methods are marked `synthetic` and `bridge` in bytecode.

---

### Q40. What is `@SafeVarargs` and when should you use it? *(Senior)*

**Model Answer:**

Generic varargs create a safety issue. Varargs internally use arrays, but you can't create a generic array (`new T[]`). Java works around this by creating a raw `Object[]` — which can cause **heap pollution**.

```java
// Warning: Possible heap pollution via varargs parameter
public static <T> List<T> asList(T... elements) {
    return Arrays.asList(elements);
}
```

`@SafeVarargs` suppresses this warning. You should use it **only** when the method does not:
1. Store anything into the varargs array
2. Expose the array to untrusted code

```java
@SafeVarargs
public static <T> List<T> asList(T... elements) {
    return List.of(elements); // safe — doesn't modify the array
}
```

It can only be applied to `static` methods, `final` methods, or constructors (methods that can't be overridden).

---

## Section 11: Collections Utility Class

### Q41. What is the difference between `Collections.unmodifiableList()` and `List.of()`? *(Mid)*

**Model Answer:**

| | `Collections.unmodifiableList(list)` | `List.of(elements)` |
|-|--------------------------------------|---------------------|
| Returns | Unmodifiable **view** of the original list | Truly **immutable** list |
| Mutation via view | Throws `UnsupportedOperationException` | Throws `UnsupportedOperationException` |
| Mutation via original | Changes are reflected through the view! | N/A — there is no original |
| Null elements | Allowed (if original has them) | Not allowed — throws `NullPointerException` |
| Java version | Java 2+ | Java 9+ |

```java
List<String> original = new ArrayList<>(List.of("a", "b"));
List<String> unmodifiable = Collections.unmodifiableList(original);

original.add("c"); // succeeds
System.out.println(unmodifiable); // [a, b, c] — view reflects the change!

List<String> immutable = List.of("a", "b");
// No reference to any mutable source — truly immutable
```

**Best practice:** Prefer `List.of()` / `List.copyOf()` when you want true immutability.

---

### Q42. How does `Collections.synchronizedList()` differ from `CopyOnWriteArrayList`? *(Senior)*

**Model Answer:**

| | `Collections.synchronizedList()` | `CopyOnWriteArrayList` |
|-|---------------------------------|----------------------|
| Locking | Wraps every method in `synchronized(mutex)` | No locks for reads; copies the entire array on every write |
| Read performance | Blocked by concurrent writes (one lock for all ops) | O(1) reads, no contention |
| Write performance | Normal (locked) | O(n) — copies the whole array on add/remove |
| Iteration | Must externally synchronize; fail-fast | Iterates over a snapshot; never throws `ConcurrentModificationException` |
| Best for | Balanced read/write workloads | Read-heavy workloads with rare writes (e.g., listener lists) |

```java
// synchronizedList — must lock during iteration!
List<String> syncList = Collections.synchronizedList(new ArrayList<>());
synchronized (syncList) {
    for (String s : syncList) { /* safe */ }
}

// CopyOnWriteArrayList — iteration is always safe
List<String> cowList = new CopyOnWriteArrayList<>();
for (String s : cowList) { /* always safe, reads snapshot */ }
```

---

## Section 12: ConcurrentHashMap

### Q43. How does `ConcurrentHashMap` differ from `HashMap`? *(Mid)*

**Model Answer:**

| | `HashMap` | `ConcurrentHashMap` |
|-|-----------|---------------------|
| Thread safety | Not thread-safe | Thread-safe (lock-free reads, node-level locks for writes) |
| Null keys/values | One null key, any null values | **Neither** null keys nor null values allowed |
| Iterator | Fail-fast | Weakly consistent (never throws CME) |
| Compound ops | Not atomic (check-then-act race) | Atomic: `computeIfAbsent`, `merge`, `putIfAbsent` |
| Performance | Fastest (single-threaded) | Excellent concurrent read throughput |

---

### Q44. Why does `ConcurrentHashMap` not allow null keys or values? *(Senior)*

**Model Answer:**

In a concurrent map, `get(key)` returning `null` is ambiguous — does it mean:
1. The key maps to `null` (value is null), or
2. The key doesn't exist?

In `HashMap`, you can disambiguate with `containsKey()`. But in a concurrent context, between calling `containsKey()` and `get()`, another thread could modify the map — making the check-then-act pattern unreliable.

By banning null keys and null values, `ConcurrentHashMap` ensures that `get(key)` returning `null` always means "key not present," enabling safe concurrent code:

```java
// Safe pattern with ConcurrentHashMap
V value = map.get(key);
if (value != null) {
    // guaranteed the key was present
}
```

---

### Q45. What is `computeIfAbsent` and why is it important? *(Mid)*

**Model Answer:**

`computeIfAbsent(key, mappingFunction)` atomically checks if a key is present and, if absent, computes and inserts a value. This eliminates the check-then-act race condition.

```java
// BAD — race condition in concurrent code
if (!map.containsKey(key)) {        // another thread could insert between
    map.put(key, expensiveCompute()); // these two lines
}

// GOOD — atomic operation
map.computeIfAbsent(key, k -> expensiveCompute());
```

Common use case — building a multimap:
```java
Map<String, List<String>> multimap = new ConcurrentHashMap<>();
multimap.computeIfAbsent("fruits", k -> new ArrayList<>()).add("apple");
multimap.computeIfAbsent("fruits", k -> new ArrayList<>()).add("banana");
```

---

## Section 13: Scenario & Design Questions

### Q46. Design a system that deduplicates incoming events while maintaining the order they were first seen. Which collection(s) would you use? *(Mid)*

**Model Answer:**

Use a `LinkedHashSet<Event>`. It:
- Rejects duplicates (set semantics, requires proper `equals`/`hashCode`)
- Maintains insertion order (linked list threading)
- Provides O(1) `add` and `contains`

```java
Set<Event> seen = new LinkedHashSet<>();
for (Event event : incomingStream) {
    seen.add(event); // duplicates silently ignored, order preserved
}
```

If you need the dedup check but want a `List` output, use a `LinkedHashSet` for tracking and convert at the end: `new ArrayList<>(seen)`.

For a concurrent environment, use a `ConcurrentHashMap.newKeySet()` (but this loses insertion order) or a `CopyOnWriteArraySet` for small sets.

---

### Q47. You need to find the top K most frequent words in a document. Which collections would you use? *(Mid)*

**Model Answer:**

1. **`HashMap<String, Integer>`** — count word frequencies. O(n) to process all words.
2. **`PriorityQueue<Map.Entry<String, Integer>>`** with a min-heap of size K — keeps only the top K entries. O(n log K) total.

```java
Map<String, Integer> freq = new HashMap<>();
for (String word : words) {
    freq.merge(word, 1, Integer::sum);
}

PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(
    Comparator.comparingInt(Map.Entry::getValue)
);

for (Map.Entry<String, Integer> entry : freq.entrySet()) {
    pq.offer(entry);
    if (pq.size() > k) pq.poll(); // remove smallest — keeps top K
}
```

Alternatively, for smaller datasets, sort the entries and take the last K: `freq.entrySet().stream().sorted(Map.Entry.comparingByValue()).toList()`.

---

### Q48. A service receives timestamped log entries from multiple sources. Entries may arrive out of order. How would you maintain a sorted view while allowing efficient insertion? *(Mid)*

**Model Answer:**

Use a `TreeMap<Instant, List<LogEntry>>` keyed by timestamp.

- **Insertion:** O(log n) — TreeMap maintains sorted order automatically.
- **Range queries:** `subMap(from, to)` returns all entries in a time window in O(log n + k) where k is the result size.
- **Multiple entries per timestamp:** Use a `List<LogEntry>` as the value.

```java
TreeMap<Instant, List<LogEntry>> logs = new TreeMap<>();

void addEntry(LogEntry entry) {
    logs.computeIfAbsent(entry.getTimestamp(), k -> new ArrayList<>()).add(entry);
}

Collection<List<LogEntry>> getEntriesBetween(Instant from, Instant to) {
    return logs.subMap(from, true, to, true).values();
}
```

If concurrency is needed, wrap with `Collections.synchronizedSortedMap()` or use `ConcurrentSkipListMap` (concurrent sorted map).

---

### Q49. How would you implement a generic event bus where listeners can subscribe to specific event types? *(Senior)*

**Model Answer:**

```java
public class EventBus {
    private final Map<Class<?>, List<Consumer<?>>> listeners = new HashMap<>();

    public <T> void subscribe(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T> void publish(T event) {
        List<Consumer<?>> handlers = listeners.getOrDefault(event.getClass(), List.of());
        for (Consumer<?> handler : handlers) {
            ((Consumer<T>) handler).accept(event);
        }
    }
}
```

Collections used:
- `HashMap<Class<?>, List<Consumer<?>>>` — O(1) lookup of listeners by event type
- `List<Consumer<?>>` — ordered list of handlers, preserving subscription order

Generics concepts: type tokens (`Class<T>`), wildcard containers, unchecked cast (unavoidable due to type erasure).

---

### Q50. You have a method that should accept a list of any `Number` subtype and return the sum. Write the signature and explain your wildcard choice. *(Mid)*

**Model Answer:**

```java
public static double sum(List<? extends Number> numbers) {
    double total = 0;
    for (Number n : numbers) {
        total += n.doubleValue();
    }
    return total;
}

// Works with all of these:
sum(List.of(1, 2, 3));           // List<Integer>
sum(List.of(1.5, 2.5));         // List<Double>
sum(List.of(1L, 2L, 3L));      // List<Long>
```

Why `? extends Number`:
- We only **read** from the list (producer) → PECS says use `extends`
- Without the wildcard, `sum(List<Number>)` would reject `List<Integer>` because generics are invariant

---

## Section 14: Code Output & Tricky Questions

### Q51. What does this code print? *(Mid)*

```java
Map<String, Integer> map = new HashMap<>();
map.put("a", 1);
map.put("b", 2);
map.put("a", 3);
System.out.println(map.size());
System.out.println(map.get("a"));
```

**Answer:** `2` and `3`. `put("a", 3)` **replaces** the existing value for key `"a"`. HashMap does not allow duplicate keys.

---

### Q52. What does this code print? *(Mid)*

```java
Set<String> set = new TreeSet<>();
set.add("banana");
set.add("apple");
set.add("cherry");
set.add("apple");
System.out.println(set);
```

**Answer:** `[apple, banana, cherry]`. TreeSet sorts elements in natural order and rejects duplicates.

---

### Q53. What does this code print? *(Mid)*

```java
List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
list.remove(2);
System.out.println(list);
```

**Answer:** `[1, 2, 4, 5]`. `remove(2)` calls `remove(int index)`, which removes the element **at index 2** (the value `3`), not the element with value `2`. To remove by value, use `list.remove(Integer.valueOf(2))`.

---

### Q54. Will this code compile? If yes, what happens at runtime? *(Senior)*

```java
List<String> strings = new ArrayList<>();
List rawList = strings;
rawList.add(42);
String s = strings.get(0);
```

**Answer:** It compiles with an unchecked warning (raw type assignment). At runtime, `rawList.add(42)` succeeds because generics are erased — the list is just a `List` of `Object`. But `strings.get(0)` causes a `ClassCastException` because the compiler inserted a cast to `String`, and `42` (an `Integer`) can't be cast to `String`. This is called **heap pollution**.

---

### Q55. What does this print? *(Mid)*

```java
PriorityQueue<Integer> pq = new PriorityQueue<>();
pq.offer(30);
pq.offer(10);
pq.offer(20);
System.out.println(pq); // NOT poll
```

**Answer:** Something like `[10, 30, 20]` (exact order depends on heap structure). **Printing a PriorityQueue does NOT show elements in sorted order.** Only `poll()` guarantees you get the minimum. The internal array is a heap, not a sorted array. Iteration order is undefined.

---

### Q56. What is wrong with this code? *(Mid)*

```java
Map<List<String>, String> cache = new HashMap<>();
List<String> key = new ArrayList<>(List.of("a", "b"));
cache.put(key, "result");

key.add("c");
System.out.println(cache.get(key));
```

**Answer:** Prints `null`. The `List` used as a key is mutable. After `key.add("c")`, its `hashCode()` changes, so `HashMap` looks in a different bucket. The entry still exists in the map (at the old bucket position) but is now unreachable. Never use mutable objects as map keys.

---

### Q57. Does this compile? Why or why not? *(Senior)*

```java
List<Integer> ints = new ArrayList<>();
List<Number> nums = ints;
```

**Answer:** Does NOT compile. Generics are **invariant**: `List<Integer>` is not a subtype of `List<Number>`, even though `Integer` extends `Number`. If it were allowed, you could do `nums.add(3.14)` and corrupt the `List<Integer>`. Use `List<? extends Number>` to establish a producer relationship.

---

### Q58. What does this print? *(Senior)*

```java
System.out.println(new ArrayList<String>().getClass() == new ArrayList<Integer>().getClass());
```

**Answer:** `true`. Due to type erasure, both are `java.util.ArrayList` at runtime. `<String>` and `<Integer>` are erased.

---

## Section 15: Best Practices & Design

### Q59. What are the rules for overriding `hashCode()` when overriding `equals()`? *(Mid)*

**Model Answer:**

The **contract:**
1. If `a.equals(b)` is true, then `a.hashCode() == b.hashCode()` must be true.
2. If `a.equals(b)` is false, hash codes are **not** required to differ (but differing hashes improve performance).
3. `hashCode()` must return the same value for the same object across calls within a single JVM execution.

**Good implementation pattern (using `Objects.hash`):**
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Employee e)) return false;
    return Objects.equals(id, e.id) && Objects.equals(name, e.name);
}

@Override
public int hashCode() {
    return Objects.hash(id, name); // uses same fields as equals
}
```

**Golden rule:** Use the **same fields** in both `equals()` and `hashCode()`.

---

### Q60. When would you use `Map.of()` vs `Map.ofEntries()` vs `new HashMap<>()`? *(Mid)*

**Model Answer:**

```java
// Map.of() — up to 10 key-value pairs, immutable
Map<String, Integer> small = Map.of("a", 1, "b", 2, "c", 3);

// Map.ofEntries() — any number of entries, immutable
Map<String, Integer> larger = Map.ofEntries(
    Map.entry("a", 1),
    Map.entry("b", 2),
    // ... any number
    Map.entry("z", 26)
);

// new HashMap<>() — mutable, null keys/values allowed
Map<String, Integer> mutable = new HashMap<>();
mutable.put("a", 1);
mutable.put(null, 0); // allowed
```

| | `Map.of()` | `Map.ofEntries()` | `new HashMap<>()` |
|-|-----------|-------------------|-------------------|
| Mutable | No | No | Yes |
| Null keys/values | No | No | Yes |
| Max entries | 10 | Unlimited | Unlimited |
| Use case | Small constant maps | Larger constant maps | Mutable state |

---

### Q61. How would you create a thread-safe `Set`? List three approaches. *(Senior)*

**Model Answer:**

1. **`Collections.synchronizedSet(new HashSet<>())`** — wraps every method in `synchronized`. Must externally synchronize during iteration.

2. **`ConcurrentHashMap.newKeySet()`** — returns a `Set` backed by a `ConcurrentHashMap`. Lock-free reads, node-level write locks. Best general-purpose concurrent set.

3. **`CopyOnWriteArraySet`** — backed by `CopyOnWriteArrayList`. Every write copies the entire array. Best for very small sets with rare writes and frequent reads (e.g., listener registries).

4. **`ConcurrentSkipListSet`** — concurrent, sorted set. O(log n) operations. Use when you need both thread safety and sorted order.

---

## Section 16: Advanced & Senior-Level Questions

### Q62. Explain how `HashMap` resizing works in Java 8+. What optimization does it use? *(Senior)*

**Model Answer:**

When `size > capacity * loadFactor`, the table doubles in size. In Java 8+, the rehashing is optimized:

Since capacity is always a power of 2, doubling means the new capacity adds one more bit to the index mask. For each entry, the new bucket is either:
- **Same index** (if the new high bit of the hash is 0)
- **Old index + old capacity** (if the new high bit is 1)

This can be determined by checking `(hash & oldCapacity) == 0`. Entries are split into two chains (lo and hi) and placed into the correct buckets without recomputing the full index. This is faster than the pre-Java 8 approach of rehashing every entry from scratch.

---

### Q63. What is the `IdentityHashMap`? When would you use it? *(Senior)*

**Model Answer:**

`IdentityHashMap` uses reference equality (`==`) instead of `equals()` for comparing keys. Two keys are considered equal only if they are the **exact same object** in memory.

```java
IdentityHashMap<String, Integer> map = new IdentityHashMap<>();
String a = new String("hello");
String b = new String("hello");

map.put(a, 1);
map.put(b, 2);
map.size(); // 2 — a and b are different objects

HashMap<String, Integer> normal = new HashMap<>();
normal.put(a, 1);
normal.put(b, 2);
normal.size(); // 1 — a.equals(b) is true
```

**Use cases:**
- Serialization frameworks (tracking which objects have already been serialized)
- Object graph traversal (detecting cycles)
- Any scenario where you need to distinguish between different instances of logically equal objects

---

### Q64. What is a `NavigableMap` and what methods does it provide that `Map` doesn't? *(Mid)*

**Model Answer:**

`NavigableMap` extends `SortedMap` and provides methods for closest-match lookups:

```java
TreeMap<Integer, String> map = new TreeMap<>();
map.put(10, "ten"); map.put(20, "twenty"); map.put(30, "thirty");

map.floorKey(25);    // 20 — greatest key ≤ 25
map.ceilingKey(25);  // 30 — smallest key ≥ 25
map.lowerKey(20);    // 10 — greatest key strictly < 20
map.higherKey(20);   // 30 — smallest key strictly > 20

map.firstKey();      // 10
map.lastKey();       // 30

map.subMap(10, true, 30, false); // {10=ten, 20=twenty} — range view
map.headMap(20, true);           // {10=ten, 20=twenty} — keys ≤ 20
map.tailMap(20, false);          // {30=thirty} — keys > 20

map.descendingMap(); // {30=thirty, 20=twenty, 10=ten}
```

`TreeMap` is the standard `NavigableMap` implementation.

---

### Q65. How would you implement a type-safe heterogeneous container? *(Senior)*

**Model Answer:**

A type-safe heterogeneous container stores values of different types but preserves type safety through `Class<T>` tokens:

```java
public class TypeSafeMap {
    private final Map<Class<?>, Object> map = new HashMap<>();

    public <T> void put(Class<T> type, T value) {
        map.put(type, type.cast(value));
    }

    public <T> T get(Class<T> type) {
        return type.cast(map.get(type));
    }
}

TypeSafeMap prefs = new TypeSafeMap();
prefs.put(String.class, "dark-mode");
prefs.put(Integer.class, 42);

String theme = prefs.get(String.class); // "dark-mode" — no cast needed
Integer size = prefs.get(Integer.class); // 42 — type safe
```

This is the **Typesafe Heterogeneous Container** pattern from *Effective Java* (Item 33). The key insight: parameterize the key (`Class<T>`), not the container.

---

### Q66. Explain the difference between `Iterable` and `Iterator`. Can a class implement both? *(Mid)*

**Model Answer:**

- **`Iterable<T>`** — has one method: `iterator()` that returns an `Iterator<T>`. Any class implementing `Iterable` can be used in the enhanced for-loop.
- **`Iterator<T>`** — has `hasNext()`, `next()`, and `remove()`. It's a stateful cursor that tracks position during traversal.

A class CAN implement both, but it's usually a bad idea:

```java
// Technically possible but problematic
public class Numbers implements Iterable<Integer>, Iterator<Integer> {
    public Iterator<Integer> iterator() { return this; }
    // ...
}
```

The problem: if `iterator()` returns `this`, you can only iterate once. The standard pattern is for `Iterable` to create a **new** `Iterator` each time `iterator()` is called, so multiple independent traversals are possible.

---

### Q67. What is the diamond problem with generics? Explain with `Comparable`. *(Senior)*

**Model Answer:**

When a class hierarchy involves multiple `Comparable` implementations, ambiguity arises:

```java
class Animal implements Comparable<Animal> {
    public int compareTo(Animal other) { return 0; }
}

// Problem: Dog wants to be comparable to Dog, not Animal
class Dog extends Animal implements Comparable<Dog> { // COMPILE ERROR
    // Can't implement Comparable with two different type arguments
}
```

Java doesn't allow a class to implement the same generic interface with two different type parameters (they erase to the same raw type).

**Solution:** Use `Comparator` for Dog-specific ordering instead:

```java
class Dog extends Animal {
    // Inherits Comparable<Animal> from Animal
    // Use Comparator<Dog> for Dog-specific sorting
    public static final Comparator<Dog> BY_BREED = Comparator.comparing(Dog::getBreed);
}
```

Or design the hierarchy with a self-referential bound: `class Animal<T extends Animal<T>> implements Comparable<T>`.

---

## Section 17: Rapid-Fire Questions (Short Answers)

### Q68. Can you add `null` to a `TreeSet`? *(Junior)*
**Answer:** No (with natural ordering). `TreeSet` calls `compareTo()` on the element, which throws `NullPointerException` for `null`. With a custom null-safe `Comparator`, it's technically possible but not recommended.

### Q69. What is the time complexity of `Collections.sort()`? *(Junior)*
**Answer:** O(n log n). It uses **TimSort** (a hybrid merge sort + insertion sort), which is stable and adaptive.

### Q70. Can a `HashMap` have duplicate values? *(Junior)*
**Answer:** Yes. Keys must be unique, but multiple keys can map to the same value.

### Q71. What is the difference between `poll()` and `remove()` on an empty queue? *(Junior)*
**Answer:** `poll()` returns `null`. `remove()` throws `NoSuchElementException`.

### Q72. Is `ArrayList` thread-safe? *(Junior)*
**Answer:** No. Use `Collections.synchronizedList()`, `CopyOnWriteArrayList`, or external synchronization for concurrent access.

### Q73. What is the initial capacity of a `HashSet`? *(Mid)*
**Answer:** 16 (because `HashSet` is backed by a `HashMap`, which has default capacity 16).

### Q74. Can you use a primitive type as a generic parameter? *(Junior)*
**Answer:** No. Generics only work with reference types. Use wrapper classes: `List<Integer>` instead of `List<int>`. Autoboxing handles the conversion.

### Q75. What is the difference between `List.of("a", "b")` and `Arrays.asList("a", "b")`? *(Mid)*

**Answer:**

| | `List.of()` | `Arrays.asList()` |
|-|------------|-------------------|
| Mutable | No (throws on add/remove/set) | Fixed size (can `set`, but not `add`/`remove`) |
| Null elements | Not allowed | Allowed |
| Backed by array | No | Yes (changes to the original array reflect in the list) |
| Java version | 9+ | 1.2+ |

---

## How to Use This Document

### For Interview Preparation
1. Start with all **Junior** questions — these are fundamentals you must know cold.
2. Move to **Mid** questions — expect these in 2–5 year experience interviews.
3. Study **Senior** questions for principal/staff-level interviews or deep-dive rounds.

### For Self-Assessment
- Answer each question out loud or in writing before reading the model answer.
- If you can't answer a Junior question confidently, revisit that topic in the README.
- Track your weak areas and revisit them before the interview.

### Question Distribution

| Difficulty | Count | Topics |
|-----------|-------|--------|
| Junior | 22 | Framework hierarchy, basic collections, generics basics, simple comparisons |
| Mid | 35 | HashMap internals, iterators, wildcards, PECS, type erasure, code output, design scenarios |
| Senior | 18 | Treeification, bridge methods, heap pollution, concurrent collections, advanced generics, architecture |
