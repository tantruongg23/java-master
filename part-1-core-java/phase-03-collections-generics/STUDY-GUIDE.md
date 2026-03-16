# Study Guide — Phase 03: Collections & Generics

> **Estimated Duration:** ~2–3 weeks (~27 hours)
> **Prerequisites:** Phase 01 (Fundamentals), Phase 02 (OOP Deep Dive)

---

## Why Collections & Generics Matter

### The Real-World Argument

Every non-trivial Java program manipulates groups of objects — user records, order items, sensor readings, cached responses. Without the Collections Framework, you'd be stuck with raw arrays: fixed size, no built-in search, no type safety beyond the element type, and hand-rolled resize logic. Collections give you **battle-tested data structures** that handle resizing, hashing, sorting, and concurrency so you can focus on business logic.

Generics, in turn, let you write **one piece of code** that works safely across many types. Before generics (Java < 5), every collection stored `Object`, and every retrieval required an explicit cast — a source of `ClassCastException` at runtime. Generics move those errors to compile time.

### Why You Can't Skip This Phase

| Scenario | What You Need |
|----------|---------------|
| Look up a user by ID in O(1) | `HashMap<String, User>` |
| Keep a sorted leaderboard | `TreeMap<Integer, Player>` or `TreeSet<Player>` |
| Process tasks in priority order | `PriorityQueue<Task>` |
| Cache the N most recent API responses | `LinkedHashMap` with access-order (LRU cache) |
| Enforce unique email addresses | `HashSet<String>` |
| Build a type-safe data access layer | Generics: `Repository<T extends Identifiable<ID>, ID>` |
| Write a method that accepts any `List` of `Number` subclasses | Wildcards: `List<? extends Number>` |

Interviews, frameworks (Spring, Hibernate), and production code assume fluency with these tools. Collections and Generics aren't "nice to know" — they're table stakes.

### The Deeper Value

- **Performance intuition:** Knowing that `ArrayList.get(i)` is O(1) while `LinkedList.get(i)` is O(n) prevents subtle production slowdowns.
- **Correctness by design:** Proper `hashCode`/`equals` contracts prevent phantom entries in `HashMap`. Understanding fail-fast iterators prevents `ConcurrentModificationException`.
- **API design quality:** PECS (`Producer Extends, Consumer Super`) turns rigid methods into flexible ones used by the entire Java standard library.

---

## Phase Overview — What You'll Learn

```
Phase 03: Collections & Generics
│
├─ 1. Collection Framework Hierarchy (1 h)
│     Iterable → Collection → List, Set, Queue / Map (separate)
│
├─ 2. List Implementations (3 h)
│     ArrayList · LinkedList · Vector/Stack (legacy)
│
├─ 3. Set Implementations (3 h)
│     HashSet · LinkedHashSet · TreeSet · EnumSet
│
├─ 4. Map Implementations (3 h)
│     HashMap · LinkedHashMap · TreeMap · WeakHashMap
│
├─ 5. Queue / Deque (2 h)
│     PriorityQueue · ArrayDeque
│
├─ 6. Comparable vs. Comparator (2 h)
│     Natural ordering · External ordering · Method-reference chains
│
├─ 7. Iterators & Fail-Fast/Fail-Safe (2 h)
│     Iterator · ListIterator · ConcurrentModificationException · modCount
│
├─ 8. Generics (6 h)
│     Type parameters · Bounded types · Wildcards · PECS · Type erasure
│
├─ 9. Collections Utility Class (1 h)
│     Unmodifiable views · Immutable factories (Java 9+) · Sorting/Search
│
└─ 10. ConcurrentHashMap Preview (1 h)
       CAS-based locking · Weakly consistent iterators · Atomic compound ops
```

---

## Study Strategy

### Recommended Order

Follow the topics sequentially — each builds on the previous:

| Week | Focus | Hours | Key Activity |
|------|-------|-------|-------------|
| **Week 1** | Topics 1–5 (Hierarchy, List, Set, Map, Queue) | ~12 h | Understand internal mechanics. Draw diagrams. |
| **Week 2** | Topics 6–8 (Comparable/Comparator, Iterators, Generics) | ~10 h | Write generic classes. Practice PECS on paper. |
| **Week 3** | Topics 9–10 + Exercises | ~5 h | Complete all 4 exercises. Self-assess. |

### How to Study Each Topic

1. **Read the README section** for the topic (theory + Big-O).
2. **Write it in code** — open a scratch file, create the collection, add/remove elements, print.
3. **Inspect the source** — `Ctrl+Click` into `ArrayList.java` or `HashMap.java` in your IDE. Read `put()`, `resize()`, `get()`. This cements the theory.
4. **Draw the data structure** on paper (the bucket array for HashMap, the node chain for LinkedList, the tree for TreeMap).
5. **Ask yourself the "when" question:** *When would I pick this over the alternatives?*

### Active Learning Techniques

- **Explain it out loud.** If you can't explain how HashMap resolves collisions in 60 seconds, you don't know it yet.
- **Big-O flashcards.** For each collection, memorize: `add`, `get/contains`, `remove`, `iteration order`.
- **Break it on purpose.** What happens if you forget to override `hashCode()` when using a custom class as a HashMap key? Try it.

---

## Core Concept Deep Dives

### 1. The Collection Hierarchy — Mental Model

```
Iterable<T>
  └── Collection<T>
        ├── List<T>              ← ordered, allows duplicates
        │     ├── ArrayList      (array-backed, fast random access)
        │     ├── LinkedList     (node-backed, fast end insertion)
        │     └── Vector         (legacy, synchronized)
        │
        ├── Set<T>               ← no duplicates
        │     ├── HashSet        (hash table, no order)
        │     ├── LinkedHashSet  (hash table + insertion order)
        │     ├── TreeSet        (Red-Black tree, sorted)
        │     └── EnumSet        (bit vector, enums only)
        │
        └── Queue<T>             ← FIFO or priority
              ├── PriorityQueue  (min-heap)
              └── Deque<T>       ← double-ended
                    └── ArrayDeque (circular array)

Map<K, V>                       ← separate hierarchy, NOT a Collection
  ├── HashMap                   (hash table, no order)
  ├── LinkedHashMap             (hash table + insertion/access order)
  ├── TreeMap                   (Red-Black tree, sorted keys)
  └── WeakHashMap               (weak-reference keys)
```

**Golden rule:** Program to the interface, not the implementation.

```java
// Good
List<String> names = new ArrayList<>();
Map<String, Integer> scores = new HashMap<>();

// Bad
ArrayList<String> names = new ArrayList<>();
```

### 2. Choosing the Right Collection — Decision Matrix

| Need | Best Choice | Why |
|------|-------------|-----|
| Ordered list, fast random access | `ArrayList` | O(1) `get(i)`, cache-friendly |
| Frequent add/remove at both ends | `ArrayDeque` | O(1) amortized, faster than LinkedList |
| Unique elements, no order needed | `HashSet` | O(1) `add`/`contains` |
| Unique elements, sorted | `TreeSet` | O(log n), navigable |
| Unique elements, insertion order | `LinkedHashSet` | O(1) + order |
| Key → Value lookup | `HashMap` | O(1) amortized |
| Key → Value, sorted by key | `TreeMap` | O(log n), range queries |
| Key → Value, insertion order | `LinkedHashMap` | O(1) + order |
| LRU cache | `LinkedHashMap(accessOrder=true)` | Override `removeEldestEntry` |
| Priority processing | `PriorityQueue` | O(log n) offer/poll |
| FIFO queue | `ArrayDeque` | O(1), no null elements |
| LIFO stack | `ArrayDeque` | O(1), replaces legacy `Stack` |
| Thread-safe map | `ConcurrentHashMap` | Lock-free reads, node-level locks |

### 3. HashMap Internals — The Interview Favorite

```
Initial state (capacity=16, loadFactor=0.75):

Bucket Array:
[0] → null
[1] → Entry(key="A", val=1) → Entry(key="Q", val=7) → null   ← collision chain
[2] → null
[3] → Entry(key="B", val=2) → null
...
[15] → null

When size > 16 * 0.75 = 12 → resize to 32, rehash all entries.
When a bucket has > 8 entries AND capacity ≥ 64 → treeify (Red-Black tree).
```

**Critical contract:** If two objects are `equals()`, they **must** have the same `hashCode()`. Violating this means HashMap can never find your key.

### 4. Generics — The Type Safety Foundation

**Without generics (Java < 5):**
```java
List list = new ArrayList();
list.add("hello");
String s = (String) list.get(0);  // manual cast, runtime risk
list.add(42);                      // compiles fine, blows up later
```

**With generics:**
```java
List<String> list = new ArrayList<>();
list.add("hello");
String s = list.get(0);           // no cast needed
list.add(42);                      // COMPILE ERROR — caught immediately
```

### 5. PECS — The Wildcard Rule

```
PECS = Producer Extends, Consumer Super

┌─────────────────────────────────────────────────┐
│  List<? extends Number>  ← PRODUCER (read-only) │
│  You can READ Number out of it                   │
│  You CANNOT add to it (compiler doesn't know     │
│  the exact type — Integer? Double? Float?)       │
├─────────────────────────────────────────────────┤
│  List<? super Integer>   ← CONSUMER (write-only) │
│  You can ADD Integer to it                       │
│  Reads come back as Object                       │
└─────────────────────────────────────────────────┘
```

**Real-world example from the JDK:**
```java
// src is a PRODUCER (we read from it) → ? extends T
// dest is a CONSUMER (we write to it) → ? super T
public static <T> void copy(List<? super T> dest, List<? extends T> src)
```

### 6. Type Erasure — What You Can't Do

At runtime, generics disappear. The JVM sees `List`, not `List<String>`.

| Forbidden | Why |
|-----------|-----|
| `new T()` | Compiler doesn't know T's constructor |
| `new T[10]` | Arrays are reified; generics are erased |
| `obj instanceof List<String>` | Can only check `instanceof List` |
| `static T field` | T is per-instance, not per-class |

---

## Exercises Roadmap

| # | Exercise | Concepts Practiced | Difficulty |
|---|----------|--------------------|------------|
| 1 | **Custom HashMap** | Hashing, buckets, linked lists, resizing, `Iterable` | ★★★☆☆ |
| 2 | **LRU Cache** | `LinkedHashMap`, doubly-linked list, O(1) design | ★★★☆☆ |
| 3 | **Inventory System** | `HashMap`, `TreeMap`, `PriorityQueue`, `Comparable`, `Comparator` | ★★★★☆ |
| 4 | **Generic Repository** | Bounded generics, wildcards, PECS, `Predicate` | ★★★★☆ |

### Exercise Tips

**Exercise 1 (Custom HashMap):**
- Start with a fixed-size bucket array and `put`/`get`. Add resizing last.
- The `hashCode() & (capacity - 1)` trick only works when capacity is a power of 2.
- Test with `null` keys — they go in bucket 0.

**Exercise 2 (LRU Cache):**
- Try Option A first (LinkedHashMap) — it's 15 lines of code. Then try Option B (manual doubly-linked list + HashMap) for deeper understanding.
- The trick: every `get()` must move the accessed node to the tail of the list.

**Exercise 3 (Inventory System):**
- Use `BigDecimal` for prices (never `double` for money).
- `TreeMap.subMap(min, true, max, true)` gives you range queries for free.
- The `PriorityQueue` for low-stock alerts: iterate and collect items below threshold.

**Exercise 4 (Generic Repository):**
- The signature `<T extends Identifiable<ID>, ID>` means T must expose an `getId()` method returning type ID.
- For the PECS demo: write a `transferAll` method that reads from `Repository<? extends T, ID>` and writes to `Repository<? super T, ID>`.

---

## Common Mistakes to Avoid

| Mistake | Consequence | Fix |
|---------|-------------|-----|
| Not overriding `hashCode()` when overriding `equals()` | HashMap/HashSet silently fails to find entries | Always override both together |
| Using `==` instead of `.equals()` for keys | Works for String literals (interning) but fails for `new String(...)` | Always use `.equals()` |
| Modifying a collection during for-each loop | `ConcurrentModificationException` | Use `Iterator.remove()` or `removeIf()` |
| Using raw types (`List` instead of `List<String>`) | No type safety, compiler warnings | Always parameterize |
| Using `double` for money in Product.price | Rounding errors (0.1 + 0.2 ≠ 0.3) | Use `BigDecimal` |
| Choosing `LinkedList` as default List | Cache-unfriendly, slower than ArrayList for most workloads | Default to `ArrayList` |
| Ignoring PECS when designing generic APIs | Overly restrictive method signatures | Apply extends/super correctly |

---

## Big-O Cheat Sheet

| Collection | `add` | `get` / `contains` | `remove` | Order |
|------------|-------|---------------------|----------|-------|
| `ArrayList` | O(1)* | O(1) by index / O(n) contains | O(n) | Insertion |
| `LinkedList` | O(1) at ends | O(n) | O(1) at ends / O(n) by index | Insertion |
| `ArrayDeque` | O(1)* | O(n) contains | O(1) at ends | Insertion |
| `HashSet` | O(1)* | O(1)* | O(1)* | None |
| `LinkedHashSet` | O(1)* | O(1)* | O(1)* | Insertion |
| `TreeSet` | O(log n) | O(log n) | O(log n) | Sorted |
| `HashMap` | O(1)* | O(1)* | O(1)* | None |
| `LinkedHashMap` | O(1)* | O(1)* | O(1)* | Insertion/Access |
| `TreeMap` | O(log n) | O(log n) | O(log n) | Sorted (keys) |
| `PriorityQueue` | O(log n) | O(n) | O(log n) poll / O(n) arbitrary | Priority (min-heap) |

*\* = amortized, assuming good hash distribution*

---

## Interview Questions to Prepare

### Collections

1. How does `HashMap` handle collisions? What is treeification and when does it kick in?
2. What happens if you don't override `hashCode()` but override `equals()` in a class used as a HashMap key?
3. Explain the difference between `fail-fast` and `fail-safe` iterators. Give an example of each.
4. When would you use `TreeMap` over `HashMap`?
5. How would you implement an LRU cache in Java?
6. What is the difference between `Collections.unmodifiableList()` and `List.of()`?
7. Why is `ArrayDeque` preferred over `Stack` and `LinkedList` for stack/queue operations?

### Generics

8. What is type erasure? What are its practical implications?
9. Explain the PECS principle with a concrete code example.
10. What is the difference between `List<?>`, `List<Object>`, and `List<? extends Object>`?
11. Can you create an array of generic type (e.g., `new T[10]`)? Why or why not?
12. What are bridge methods and why does the compiler generate them?

---

## Key Terms Glossary

| Term | Definition |
|------|-----------|
| **Amortized O(1)** | Usually O(1), but occasionally O(n) for resizing; averaged over all operations it's O(1) |
| **Bucket** | A slot in a hash table's internal array that holds entries mapping to the same index |
| **Capacity** | The number of buckets in a HashMap (default: 16, always a power of 2) |
| **Comparable** | Interface for natural ordering — the class defines its own sort order |
| **Comparator** | External ordering strategy — sort objects without modifying their class |
| **Fail-fast** | Iterator throws `ConcurrentModificationException` if the collection is modified during iteration |
| **Fail-safe** | Iterator works on a snapshot; never throws, but may show stale data |
| **Load factor** | Threshold ratio (`size / capacity`) that triggers resize (default: 0.75) |
| **PECS** | Producer Extends, Consumer Super — rule for choosing wildcard bounds |
| **Reifiable type** | A type whose information is fully available at runtime (e.g., `List`, `String`, but NOT `List<String>`) |
| **Treeification** | HashMap converts a long collision chain (>8 entries) from linked list to Red-Black tree |
| **Type erasure** | Compiler removes generic type info at compile time; the JVM sees raw types only |
| **Wildcard** | `?` in generics — represents an unknown type; can be bounded with `extends` or `super` |

---

## Progress Tracker

Use this to track your study progress:

- [ ] **Topic 1:** Collection Framework Hierarchy — drew the hierarchy diagram
- [ ] **Topic 2:** List Implementations — compared ArrayList vs LinkedList with code
- [ ] **Topic 3:** Set Implementations — used HashSet, LinkedHashSet, TreeSet, EnumSet
- [ ] **Topic 4:** Map Implementations — explored HashMap internals, built LRU with LinkedHashMap
- [ ] **Topic 5:** Queue/Deque — used PriorityQueue and ArrayDeque
- [ ] **Topic 6:** Comparable vs Comparator — built multi-field Comparator chains
- [ ] **Topic 7:** Iterators — reproduced and fixed ConcurrentModificationException
- [ ] **Topic 8:** Generics — wrote generic class with bounded types, applied PECS
- [ ] **Topic 9:** Collections utility class — used `List.of()`, `unmodifiableList`, `sort`
- [ ] **Topic 10:** ConcurrentHashMap preview — explored atomic compound operations
- [ ] **Exercise 1:** Custom HashMap — implemented with resize and iteration
- [ ] **Exercise 2:** LRU Cache — both LinkedHashMap and manual approaches
- [ ] **Exercise 3:** Inventory System — HashMap + TreeMap + PriorityQueue integration
- [ ] **Exercise 4:** Generic Repository — bounded generics, wildcards, PECS demo
- [ ] **Self-Assessment:** Completed all checklist items in README.md

---

## What's Next

After completing this phase, you'll have the data structure and type system fluency needed for:

- **Phase 04 (Functional Programming & Streams):** Streams operate *on* collections. You'll chain `filter`, `map`, `reduce` on the collections you've mastered here.
- **Phase 05+ (Concurrency):** Thread-safe collections (`ConcurrentHashMap`, `CopyOnWriteArrayList`) build on the non-concurrent ones.
- **Real-world projects:** Spring Data repositories, Hibernate entity collections, and cache implementations all assume you can choose and use the right collection.

**The bottom line:** Collections are how Java programs organize data. Generics are how they do it safely. Master both, and you're ready for the rest of the journey.
