# Study Guide — Phase 03: Collections & Generics

> **Estimated Duration:** ~~2–3 weeks (~~27 hours)
> **Prerequisites:** Phase 01 (Fundamentals), Phase 02 (OOP Deep Dive)
> **Philosophy:** *If you can explain it clearly to someone else, you truly understand it. This guide goes deep so that you can.*

---

## Table of Contents

- [Why Collections & Generics Matter](#why-collections--generics-matter)
- [Phase Overview](#phase-overview--what-youll-learn)
- [Study Strategy](#study-strategy)
- **Part I — Collections Framework**
  - [The Hierarchy — Big Picture](#part-i--the-java-collections-framework)
  - [The `List` Interface](#the-list-interface)
  - [The `Set` Interface](#the-set-interface)
  - [The `Map` Interface](#the-map-interface)
  - [The `Queue` & `Deque` Interfaces](#the-queue--deque-interfaces)
  - [Comparable vs. Comparator](#comparable-vs-comparator)
  - [Iterators & Fail-Fast / Fail-Safe](#iterators--fail-fast--fail-safe)
  - [Collections Utility Class](#collections-utility-class)
  - [ConcurrentHashMap Preview](#concurrenthashmap-preview)
- **Part II — Generics**
  - [The World Before Generics — Pain Points](#the-world-before-generics--pain-points)
  - [How Generics Solve These Problems](#how-generics-solve-these-problems)
  - [Generic Classes](#generic-classes)
  - [Generic Methods](#generic-methods)
  - [Bounded Type Parameters](#bounded-type-parameters)
  - [Wildcards](#wildcards--the-unknown-type)
  - [The PECS Principle](#the-pecs-principle--producer-extends-consumer-super)
  - [Type Erasure](#type-erasure--the-hidden-cost)
  - [Caveats & Gotchas](#caveats--gotchas-with-generics)
- [Big-O Cheat Sheet](#big-o-cheat-sheet)
- [Exercises Roadmap](#exercises-roadmap)
- [Common Mistakes to Avoid](#common-mistakes-to-avoid)
- [Key Terms Glossary](#key-terms-glossary)
- [Progress Tracker](#progress-tracker)
- [What's Next](#whats-next)

---

## Why Collections & Generics Matter

### The Real-World Argument

Every non-trivial Java program manipulates groups of objects — user records, order items, sensor readings, cached responses. Without the Collections Framework, you'd be stuck with raw arrays: fixed size, no built-in search, no type safety beyond the element type, and hand-rolled resize logic. Collections give you **battle-tested data structures** that handle resizing, hashing, sorting, and concurrency so you can focus on business logic.

Generics, in turn, let you write **one piece of code** that works safely across many types. Before generics (Java < 5), every collection stored `Object`, and every retrieval required an explicit cast — a source of `ClassCastException` at runtime. Generics move those errors to compile time.

### Why You Can't Skip This Phase


| Scenario                                                      | What You Need                                          |
| ------------------------------------------------------------- | ------------------------------------------------------ |
| Look up a user by ID in O(1)                                  | `HashMap<String, User>`                                |
| Keep a sorted leaderboard                                     | `TreeMap<Integer, Player>` or `TreeSet<Player>`        |
| Process tasks in priority order                               | `PriorityQueue<Task>`                                  |
| Cache the N most recent API responses                         | `LinkedHashMap` with access-order (LRU cache)          |
| Enforce unique email addresses                                | `HashSet<String>`                                      |
| Build a type-safe data access layer                           | Generics: `Repository<T extends Identifiable<ID>, ID>` |
| Write a method that accepts any `List` of `Number` subclasses | Wildcards: `List<? extends Number>`                    |


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
├─ Part I: Collections Framework
│  ├─ 1. Collection Framework Hierarchy (1 h)
│  │     Iterable → Collection → List, Set, Queue / Map (separate)
│  ├─ 2. List Implementations (3 h)
│  │     ArrayList · LinkedList · Vector/Stack (legacy)
│  ├─ 3. Set Implementations (3 h)
│  │     HashSet · LinkedHashSet · TreeSet · EnumSet
│  ├─ 4. Map Implementations (3 h)
│  │     HashMap · LinkedHashMap · TreeMap · WeakHashMap
│  ├─ 5. Queue / Deque (2 h)
│  │     PriorityQueue · ArrayDeque
│  ├─ 6. Comparable vs. Comparator (2 h)
│  ├─ 7. Iterators & Fail-Fast/Fail-Safe (2 h)
│  ├─ 8. Collections Utility Class (1 h)
│  └─ 9. ConcurrentHashMap Preview (1 h)
│
└─ Part II: Generics
   ├─ 10. Pain Points Before Generics (1 h)
   ├─ 11. Generic Classes & Methods (2 h)
   ├─ 12. Bounded Types & Wildcards (2 h)
   ├─ 13. PECS Principle (1 h)
   └─ 14. Type Erasure & Caveats (1 h)
```

---

## Study Strategy

### Recommended Order


| Week       | Focus                                         | Hours | Key Activity                                                                      |
| ---------- | --------------------------------------------- | ----- | --------------------------------------------------------------------------------- |
| **Week 1** | Topics 1–5 (Hierarchy, List, Set, Map, Queue) | ~12 h | Understand internal mechanics. Draw diagrams. Write code for every example below. |
| **Week 2** | Topics 6–9 + Generics Part II                 | ~10 h | Write generic classes. Practice PECS on paper. Build Comparator chains.           |
| **Week 3** | Exercises + Self-Assessment                   | ~5 h  | Complete all 4 exercises. Answer interview questions out loud.                    |


### How to Study Each Topic

1. **Read the section below** — understand the theory, internals, and Big-O.
2. **Type every code example** — don't copy-paste. Typing builds muscle memory.
3. **Inspect the JDK source** — `Ctrl+Click` into `ArrayList.java` or `HashMap.java` in your IDE. Read `put()`, `resize()`, `get()`.
4. **Draw the data structure** on paper (bucket array for HashMap, node chain for LinkedList, tree for TreeMap).
5. **Ask yourself the "when" question:** *When would I pick this over the alternatives? When would I NOT?*
6. **Explain it out loud** — if you can't explain how HashMap resolves collisions in 60 seconds, you don't know it yet.

---

# Part I — The Java Collections Framework

## The Hierarchy — Big Picture

```
Iterable<T>
  └── Collection<T>
        ├── List<T>              ← ordered, allows duplicates, index-based access
        │     ├── ArrayList      (array-backed, fast random access)
        │     ├── LinkedList     (doubly-linked nodes, fast end operations)
        │     └── Vector         (legacy, synchronized — avoid)
        │           └── Stack    (legacy — use ArrayDeque instead)
        │
        ├── Set<T>               ← no duplicates
        │     ├── HashSet        (hash table, no order)
        │     ├── LinkedHashSet  (hash table + insertion order)
        │     ├── TreeSet        (Red-Black tree, sorted)
        │     └── EnumSet        (bit vector, enum types only)
        │
        └── Queue<T>             ← elements waiting to be processed
              ├── PriorityQueue  (min-heap, priority ordering)
              └── Deque<T>       ← double-ended queue
                    ├── ArrayDeque   (circular array — preferred)
                    └── LinkedList   (also implements Deque)

Map<K, V>                       ← SEPARATE hierarchy — NOT a Collection
  ├── HashMap                   (hash table, no order, allows null key)
  ├── LinkedHashMap             (hash table + insertion/access order)
  ├── TreeMap                   (Red-Black tree, sorted by key)
  ├── WeakHashMap               (weak-reference keys, GC-friendly)
  ├── IdentityHashMap           (uses == instead of equals)
  └── EnumMap                   (enum keys, array-backed)
```

**The golden rule:** Program to the interface, not the implementation.

```java
List<String> names = new ArrayList<>();      // Good — depends on List
Map<String, Integer> scores = new HashMap<>(); // Good — depends on Map

ArrayList<String> names = new ArrayList<>();  // Bad — locked to ArrayList
```

Why? If you later switch from `ArrayList` to `CopyOnWriteArrayList`, only the instantiation line changes — every method that takes `List<String>` still works.

---

## The `List` Interface

> **Contract:** An ordered collection (sequence). Elements can be accessed by their integer index. Duplicates are allowed.

**Key methods from `List<E>`:**

```java
void    add(int index, E element)    // insert at position
E       get(int index)               // retrieve by position
E       set(int index, E element)    // replace at position
E       remove(int index)            // remove by position
int     indexOf(Object o)            // first occurrence
int     lastIndexOf(Object o)        // last occurrence
List<E> subList(int from, int to)    // view (not a copy)
```

---

### ArrayList — The Default Workhorse

**What it is:** A resizable array. Internally it's an `Object[]` that grows when full.

**How it works internally:**

```
Before add (capacity=4, size=3):
┌───┬───┬───┬───┐
│ A │ B │ C │   │   ← Object[] elementData
└───┴───┴───┴───┘

After add("D") (capacity=4, size=4):
┌───┬───┬───┬───┐
│ A │ B │ C │ D │   ← full now
└───┴───┴───┴───┘

After add("E") — resize triggered (new capacity = 4 + 4>>1 = 6):
┌───┬───┬───┬───┬───┬───┐
│ A │ B │ C │ D │ E │   │   ← new, larger array
└───┴───┴───┴───┴───┴───┘
```

- **Default initial capacity:** 10 (allocated when first element is added)
- **Growth formula:** `newCapacity = oldCapacity + (oldCapacity >> 1)` — approximately 1.5x

**Performance:**


| Operation             | Time           | Why                                                       |
| --------------------- | -------------- | --------------------------------------------------------- |
| `get(index)`          | O(1)           | Direct array index: `elementData[index]`                  |
| `add(element)` at end | O(1) amortized | Append to end; occasionally O(n) for array copy on resize |
| `add(index, element)` | O(n)           | Must shift all elements after `index` right by one        |
| `remove(index)`       | O(n)           | Must shift all elements after `index` left by one         |
| `contains(element)`   | O(n)           | Linear scan with `equals()`                               |
| `size()`              | O(1)           | Stored as a field                                         |


**Real-World Example — Managing an Order's Line Items:**

```java
public class Order {
    private final String orderId;
    private final List<LineItem> items = new ArrayList<>();

    public void addItem(LineItem item) {
        items.add(item); // O(1) — items added at the end
    }

    public LineItem getItem(int index) {
        return items.get(index); // O(1) — fast random access by position
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (LineItem item : items) { // iterates in insertion order
            total = total.add(item.getPrice().multiply(
                BigDecimal.valueOf(item.getQuantity())
            ));
        }
        return total;
    }

    public void removeItem(String sku) {
        items.removeIf(item -> item.getSku().equals(sku)); // O(n)
    }
}
```

**When to use:** Your **default choice** for any list. Random access, iteration, adding at the end — all fast.

**When NOT to use:** Frequent insertions/removals in the middle of a very large list (each one shifts elements). Consider a `LinkedList` only if profiling proves it's faster for your specific workload.

**Practical tip — preallocate capacity when you know the size:**

```java
// Bad — will resize multiple times as data grows
List<String> names = new ArrayList<>();

// Good — avoids resizing for up to 10,000 elements
List<String> names = new ArrayList<>(10_000);
```

---

### LinkedList — The Doubly-Linked Node Chain

**What it is:** A sequence of `Node` objects, each pointing to its predecessor and successor.

**How it works internally:**

```
head                                            tail
 ↓                                               ↓
┌──────────┐    ┌──────────┐    ┌──────────┐
│ prev:null│◄───│ prev     │◄───│ prev     │
│ item: A  │    │ item: B  │    │ item: C  │
│ next     │───►│ next     │───►│ next:null│
└──────────┘    └──────────┘    └──────────┘
```

Each `Node<E>` holds: `E item`, `Node<E> prev`, `Node<E> next`.

**Performance:**


| Operation                        | Time | Why                                                              |
| -------------------------------- | ---- | ---------------------------------------------------------------- |
| `addFirst(e)` / `addLast(e)`     | O(1) | Just rewire head/tail pointers                                   |
| `removeFirst()` / `removeLast()` | O(1) | Just rewire head/tail pointers                                   |
| `get(index)`                     | O(n) | Must traverse from head or tail (whichever is closer)            |
| `add(index, element)`            | O(n) | Must traverse to find the position; the actual insertion is O(1) |
| `contains(element)`              | O(n) | Linear scan                                                      |


**Real-World Example — Browser History (Back/Forward):**

```java
public class BrowserHistory {
    private final LinkedList<String> history = new LinkedList<>();
    private int currentIndex = -1;

    public void visit(String url) {
        // Remove all "forward" pages after current position
        while (history.size() > currentIndex + 1) {
            history.removeLast(); // O(1)
        }
        history.addLast(url); // O(1)
        currentIndex++;
    }

    public String back() {
        if (currentIndex > 0) {
            currentIndex--;
            return history.get(currentIndex); // O(n) — but history is usually small
        }
        return history.getFirst();
    }

    public String forward() {
        if (currentIndex < history.size() - 1) {
            currentIndex++;
            return history.get(currentIndex);
        }
        return history.getLast();
    }
}
```

**When to use:** When you truly need O(1) insertions/removals at both ends AND you never need random access by index. In practice, `ArrayDeque` is almost always better.

**When NOT to use:** Almost everywhere. `ArrayList` beats `LinkedList` in most benchmarks because modern CPUs are optimized for sequential memory access (cache locality). Each `Node` in `LinkedList` is a separate object scattered in memory — causing cache misses.

**The honest truth:** In 15+ years of Java, most developers use `LinkedList` less than a handful of times. `ArrayList` and `ArrayDeque` cover 99% of use cases.

---

### Vector & Stack — Legacy (Don't Use)

**Vector** is identical to `ArrayList` but synchronizes every method. Even if no thread contention exists, you pay the synchronization cost. **Prefer:** `ArrayList` or `Collections.synchronizedList(new ArrayList<>())` or `CopyOnWriteArrayList`.

**Stack** extends `Vector` and models a LIFO stack. Because it extends `Vector`, it inherits all `List` methods — meaning you can `add(index, element)` in the middle of a "stack," which violates the stack concept. **Prefer:** `ArrayDeque` used as a stack.

```java
// Legacy — avoid
Stack<String> stack = new Stack<>();
stack.push("A");
stack.push("B");
stack.pop(); // "B"

// Modern — preferred
Deque<String> stack = new ArrayDeque<>();
stack.push("A");
stack.push("B");
stack.pop(); // "B"
```

---

## The `Set` Interface

> **Contract:** A collection that contains **no duplicate elements**. At most one `null` element (in `HashSet`/`LinkedHashSet`; not in `TreeSet`). Models the mathematical set abstraction.

**Key methods from `Set<E>`:**

```java
boolean add(E e)          // returns false if element already present
boolean contains(Object o)
boolean remove(Object o)
int     size()
boolean isEmpty()
```

The real question with sets is always: *How does it determine uniqueness?* The answer depends on the implementation.

---

### HashSet — Fast Uniqueness, No Order

**What it is:** A set backed by a `HashMap`. Every element is stored as a **key** in the map, with a dummy constant as the value.

**How it works internally:**

```java
// Inside OpenJDK's HashSet.java (simplified)
private transient HashMap<E, Object> map;
private static final Object PRESENT = new Object();

public boolean add(E e) {
    return map.put(e, PRESENT) == null;
}

public boolean contains(Object o) {
    return map.containsKey(o);
}
```

So `HashSet` is literally a `HashMap` where we only care about the keys. This means `hashCode()` and `equals()` must be correctly implemented on the elements.

**Performance:** Same as `HashMap` keys — O(1) amortized for `add`, `contains`, `remove`.

**Real-World Example — Deduplicating User Emails:**

```java
public class RegistrationService {
    private final Set<String> registeredEmails = new HashSet<>();

    public boolean registerUser(String email, String name) {
        String normalized = email.toLowerCase().trim();

        if (!registeredEmails.add(normalized)) {
            // add() returns false if already present
            throw new IllegalArgumentException("Email already registered: " + email);
        }

        // proceed with registration...
        return true;
    }

    public boolean isEmailTaken(String email) {
        return registeredEmails.contains(email.toLowerCase().trim()); // O(1)
    }
}
```

**Real-World Example — Finding Common Tags Between Two Articles:**

```java
Set<String> articleATags = new HashSet<>(List.of("java", "spring", "backend", "api"));
Set<String> articleBTags = new HashSet<>(List.of("java", "testing", "junit", "backend"));

// Intersection — tags in both articles
Set<String> common = new HashSet<>(articleATags);
common.retainAll(articleBTags);
System.out.println(common); // [java, backend]

// Union — all unique tags
Set<String> allTags = new HashSet<>(articleATags);
allTags.addAll(articleBTags);
System.out.println(allTags); // [java, spring, backend, api, testing, junit]

// Difference — tags only in A
Set<String> onlyA = new HashSet<>(articleATags);
onlyA.removeAll(articleBTags);
System.out.println(onlyA); // [spring, api]
```

**When to use:** Default choice when you need uniqueness and don't care about element order.

---

### LinkedHashSet — Uniqueness + Insertion Order

**What it is:** Extends `HashSet` with a doubly-linked list that threads through all entries, preserving the order elements were inserted.

**How it works internally:**

```
Hash table (same as HashSet):
[0] → null
[1] → "banana"
[2] → "apple"
[3] → null
[4] → "cherry"

Linked list overlay (insertion order):
"apple" ←→ "banana" ←→ "cherry"
  (1st)       (2nd)       (3rd)
```

**Performance:** Same O(1) as `HashSet`, slightly more memory for the linked list pointers.

**Real-World Example — Preserving Category Display Order:**

```java
public class MenuBuilder {
    // categories should appear in the order they were first added, with no duplicates
    private final Set<String> categories = new LinkedHashSet<>();

    public void addCategory(String category) {
        categories.add(category); // duplicates silently ignored, order preserved
    }

    public List<String> getMenuCategories() {
        return new ArrayList<>(categories); // predictable order
    }
}

MenuBuilder menu = new MenuBuilder();
menu.addCategory("Appetizers");
menu.addCategory("Main Course");
menu.addCategory("Desserts");
menu.addCategory("Main Course"); // ignored — already present

System.out.println(menu.getMenuCategories());
// [Appetizers, Main Course, Desserts] — insertion order preserved
```

**When to use:** Need uniqueness AND want to preserve the order elements were first added. Common in UI rendering where display order matters.

---

### TreeSet — Sorted Uniqueness

**What it is:** A set backed by a `TreeMap` (Red-Black balanced binary search tree). Elements are stored in **sorted order** — either natural ordering (`Comparable`) or a custom `Comparator`.

**How it works internally:**

```
TreeSet containing: [20, 10, 30, 5, 15]

Red-Black Tree (balanced BST):
           20 (black)
          /          \
      10 (red)     30 (black)
      /     \
  5 (black) 15 (black)

In-order traversal gives: [5, 10, 15, 20, 30] — always sorted
```

**Performance:**


| Operation                 | Time     |
| ------------------------- | -------- |
| `add(e)`                  | O(log n) |
| `contains(e)`             | O(log n) |
| `remove(e)`               | O(log n) |
| `first()` / `last()`      | O(log n) |
| `floor(e)` / `ceiling(e)` | O(log n) |


**The `NavigableSet` methods (what makes TreeSet special):**

```java
TreeSet<Integer> scores = new TreeSet<>(List.of(55, 70, 85, 90, 95, 100));

scores.first();          // 55 — smallest
scores.last();           // 100 — largest
scores.floor(87);        // 85 — greatest element ≤ 87
scores.ceiling(87);      // 90 — smallest element ≥ 87
scores.lower(85);        // 70 — greatest element strictly < 85
scores.higher(85);       // 90 — smallest element strictly > 85
scores.headSet(85);      // [55, 70] — elements < 85
scores.tailSet(85);      // [85, 90, 95, 100] — elements ≥ 85
scores.subSet(70, 95);   // [70, 85, 90] — elements in [70, 95)
```

**Real-World Example — Leaderboard with Rank Queries:**

```java
public class Leaderboard {
    // Sorted by score descending, then by name for tiebreaking
    private final TreeSet<PlayerScore> board = new TreeSet<>(
        Comparator.comparingInt(PlayerScore::score).reversed()
                  .thenComparing(PlayerScore::name)
    );

    record PlayerScore(String name, int score) {}

    public void submit(String name, int score) {
        board.add(new PlayerScore(name, score));
    }

    public List<PlayerScore> topN(int n) {
        return board.stream().limit(n).toList();
    }

    public PlayerScore playerJustAbove(int score) {
        // Who is the closest player with a higher score?
        PlayerScore dummy = new PlayerScore("", score);
        return board.lower(dummy); // lower in the reversed set = higher score
    }
}
```

**When to use:** You need a unique collection that is  always sorted, or you need range queries (`subSet`, `headSet`, `tailSet`), or nearest-neighbor lookups (`floor`, `ceiling`).

**When NOT to use:** If you just need uniqueness without sorting — use `HashSet` (O(1) vs O(log n)).

---

### EnumSet — The Hidden Performance Gem

**What it is:** A specialized `Set` for `enum` types, backed by a **bit vector**. If your enum has ≤64 constants, the entire set is a single `long` (8 bytes).

```java
enum Permission { READ, WRITE, EXECUTE, DELETE, ADMIN }

// Create sets
Set<Permission> readOnly  = EnumSet.of(Permission.READ);
Set<Permission> readWrite = EnumSet.of(Permission.READ, Permission.WRITE);
Set<Permission> all       = EnumSet.allOf(Permission.class);
Set<Permission> none      = EnumSet.noneOf(Permission.class);
Set<Permission> notAdmin  = EnumSet.complementOf(EnumSet.of(Permission.ADMIN));

// Operations — all are bitwise, incredibly fast
readWrite.contains(Permission.READ); // true  — checks a single bit
readWrite.add(Permission.EXECUTE);   // set a bit
readWrite.remove(Permission.WRITE);  // clear a bit
```

**Real-World Example — Feature Flags:**

```java
public class FeatureFlags {
    enum Feature { DARK_MODE, BETA_SEARCH, NEW_CHECKOUT, AI_SUGGEST, EXPORT_CSV }

    private final Map<String, Set<Feature>> userFeatures = new HashMap<>();

    public void enableFeature(String userId, Feature feature) {
        userFeatures.computeIfAbsent(userId, k -> EnumSet.noneOf(Feature.class))
                    .add(feature);
    }

    public boolean hasFeature(String userId, Feature feature) {
        return userFeatures.getOrDefault(userId, EnumSet.noneOf(Feature.class))
                           .contains(feature); // O(1) — single bit check
    }

    public void enableAllBetaFeatures(String userId) {
        userFeatures.put(userId, EnumSet.allOf(Feature.class));
    }
}
```

**When to use:** Always prefer `EnumSet` over `HashSet<MyEnum>`. It's faster, uses less memory, and iterates in enum declaration order.

---

## The `Map` Interface

> **Contract:** An object that maps **keys to values**. No duplicate keys; each key maps to at most one value. `Map` does NOT extend `Collection`.

**Key methods from `Map<K, V>`:**

```java
V       put(K key, V value)         // insert or update, returns old value
V       get(Object key)             // retrieve, null if absent
V       getOrDefault(Object key, V defaultValue)
V       remove(Object key)
boolean containsKey(Object key)
boolean containsValue(Object value) // O(n) — scans all values
int     size()

// Java 8+ — powerful compound operations
V       putIfAbsent(K key, V value)
V       computeIfAbsent(K key, Function<K, V> mapper)
V       compute(K key, BiFunction<K, V, V> remapper)
V       merge(K key, V value, BiFunction<V, V, V> remapper)
void    forEach(BiConsumer<K, V> action)

// Views
Set<K>          keySet()
Collection<V>   values()
Set<Entry<K,V>> entrySet()
```

---

### HashMap — The Backbone of Java

**What it is:** A hash table storing key-value pairs in an array of buckets.

**How it works — step by step when you call `put(key, value)`:**

```
Step 1: Hash the key
   hash = key.hashCode()                    // e.g., "alice".hashCode() = 92903640
   hash = hash ^ (hash >>> 16)              // spread high bits into low bits (reduces collisions)

Step 2: Find the bucket
   index = hash & (capacity - 1)            // e.g., 92903640 & 15 = 8  (when capacity=16)

Step 3: Place in bucket
   If bucket[8] is empty:
       bucket[8] = new Node(hash, key, value, null)
   If bucket[8] has entries:
       Walk the chain. If an existing key equals our key → replace value.
       Otherwise → append new Node at end of chain.

Step 4: Check load
   If (++size > capacity * loadFactor):
       resize() — double the array, rehash all entries.

Step 5: Check chain length
   If chain length > 8 AND capacity ≥ 64:
       Convert linked list to Red-Black tree (treeification).
```

**Visual representation:**

```
Bucket Array (capacity=16, loadFactor=0.75, resize at 12 entries):
┌────┐
│  0 │ → null
│  1 │ → [hash=1, "key1"=val1] → [hash=17, "key2"=val2] → null  ← collision chain
│  2 │ → null
│  3 │ → [hash=3, "key3"=val3] → null
│  4 │ → null
│  5 │ → [hash=5, "key4"=val4] → null
│ .. │
│ 15 │ → null
└────┘

After treeification (chain > 8 entries in one bucket):
│  1 │ →    [key_D]           ← Red-Black tree (O(log n) lookup)
           /       \
       [key_A]   [key_F]
       /    \       \
   [key_B] [key_C] [key_G]
```

**Performance:**


| Operation          | Average | Worst (before treeification) | Worst (after treeification) |
| ------------------ | ------- | ---------------------------- | --------------------------- |
| `put(k, v)`        | O(1)    | O(n)                         | O(log n)                    |
| `get(k)`           | O(1)    | O(n)                         | O(log n)                    |
| `remove(k)`        | O(1)    | O(n)                         | O(log n)                    |
| `containsKey(k)`   | O(1)    | O(n)                         | O(log n)                    |
| `containsValue(v)` | O(n)    | O(n)                         | O(n)                        |


**The hashCode/equals Contract — Critical:**

```java
// RULE: If a.equals(b) → a.hashCode() MUST == b.hashCode()
// The reverse is NOT required (hash collisions are fine)

public class Employee {
    private final String id;
    private final String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee e)) return false;
        return Objects.equals(id, e.id); // equality based on id
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // MUST use same fields as equals
    }
}
```

**What goes wrong if you forget `hashCode()`:**

```java
Map<Employee, String> map = new HashMap<>();
Employee e1 = new Employee("E001", "Alice");
map.put(e1, "Engineering");

Employee e2 = new Employee("E001", "Alice"); // same id, different object
System.out.println(e1.equals(e2));   // true — correctly overridden
System.out.println(map.get(e2));     // null!! — hashCode not overridden
// e2 uses Object.hashCode() → different hash → wrong bucket → never found
```

**Real-World Example — Word Frequency Counter:**

```java
public static Map<String, Integer> countWords(String text) {
    Map<String, Integer> frequency = new HashMap<>();
    for (String word : text.toLowerCase().split("\\W+")) {
        frequency.merge(word, 1, Integer::sum);
        // merge: if key exists, apply function (oldVal + 1); else put(word, 1)
    }
    return frequency;
}

Map<String, Integer> counts = countWords("to be or not to be");
// {to=2, be=2, or=1, not=1}
```

**Real-World Example — Grouping Employees by Department:**

```java
Map<String, List<Employee>> byDept = new HashMap<>();
for (Employee emp : employees) {
    byDept.computeIfAbsent(emp.getDepartment(), k -> new ArrayList<>())
          .add(emp);
}
// {"Engineering": [Alice, Bob], "Marketing": [Charlie], ...}
```

---

### LinkedHashMap — HashMap + Insertion/Access Order

**What it is:** Extends `HashMap` by threading a doubly-linked list through all entries, so iteration follows a predictable order.

**Two modes:**

1. **Insertion order** (default) — entries iterate in the order they were first put
2. **Access order** (`accessOrder=true`) — entries move to the tail on every `get()` or `put()`

**Real-World Example — LRU Cache (the most common use):**

```java
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    public LRUCache(int maxSize) {
        super(maxSize, 0.75f, true); // accessOrder = true
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize; // evict LRU when over capacity
    }
}

// Usage — API response cache
LRUCache<String, String> cache = new LRUCache<>(3);
cache.put("/users/1", "{name: Alice}");
cache.put("/users/2", "{name: Bob}");
cache.put("/users/3", "{name: Charlie}");

cache.get("/users/1"); // access moves /users/1 to most-recently-used

cache.put("/users/4", "{name: Dave}"); // capacity exceeded!
// /users/2 evicted (least recently used)

System.out.println(cache.keySet()); // [/users/3, /users/1, /users/4]
```

**Real-World Example — Preserving JSON Key Order:**

```java
// Standard HashMap — key order is unpredictable
Map<String, Object> json = new HashMap<>();
json.put("name", "Alice");
json.put("age", 30);
json.put("email", "alice@example.com");
// Might print: {age=30, email=alice@example.com, name=Alice}

// LinkedHashMap — preserves insertion order
Map<String, Object> json = new LinkedHashMap<>();
json.put("name", "Alice");
json.put("age", 30);
json.put("email", "alice@example.com");
// Always prints: {name=Alice, age=30, email=alice@example.com}
```

---

### TreeMap — Sorted Keys with Range Queries

**What it is:** A Red-Black tree where keys are always sorted. Implements `NavigableMap`.

**Real-World Example — Time-Based Event Log with Range Queries:**

```java
public class EventLog {
    private final TreeMap<Instant, List<String>> events = new TreeMap<>();

    public void log(Instant time, String event) {
        events.computeIfAbsent(time, k -> new ArrayList<>()).add(event);
    }

    public Map<Instant, List<String>> getEventsBetween(Instant from, Instant to) {
        return events.subMap(from, true, to, true); // O(log n) to find range
    }

    public Map.Entry<Instant, List<String>> getLatestBefore(Instant time) {
        return events.floorEntry(time); // closest entry ≤ time
    }

    public Map.Entry<Instant, List<String>> getEarliestAfter(Instant time) {
        return events.ceilingEntry(time); // closest entry ≥ time
    }

    public Instant getOldestEvent() {
        return events.firstKey();
    }

    public Instant getNewestEvent() {
        return events.lastKey();
    }
}
```

**Real-World Example — Price Tier Lookup:**

```java
// Shipping costs by weight tier
TreeMap<Double, BigDecimal> shippingRates = new TreeMap<>();
shippingRates.put(0.0, new BigDecimal("5.99"));    // 0–1 kg
shippingRates.put(1.0, new BigDecimal("9.99"));    // 1–5 kg
shippingRates.put(5.0, new BigDecimal("14.99"));   // 5–10 kg
shippingRates.put(10.0, new BigDecimal("24.99"));  // 10+ kg

double packageWeight = 3.7;
BigDecimal rate = shippingRates.floorEntry(packageWeight).getValue();
// floorEntry(3.7) → entry for key 1.0 → $9.99
```

---

### WeakHashMap — GC-Friendly Cache

**What it is:** Keys are wrapped in `WeakReference`. When no strong reference to a key exists, the GC can collect it and the entry is automatically removed.

**Real-World Example — Metadata Cache That Doesn't Prevent GC:**

```java
// Cache computed metadata for objects — entries auto-expire when objects are GC'd
WeakHashMap<Image, ImageMetadata> metadataCache = new WeakHashMap<>();

public ImageMetadata getMetadata(Image img) {
    ImageMetadata meta = metadataCache.get(img);
    if (meta == null) {
        meta = computeExpensiveMetadata(img);
        metadataCache.put(img, meta);
    }
    return meta;
}
// When the Image object is no longer referenced anywhere else,
// its entry is automatically removed from the cache → no memory leak.
```

---

## The `Queue` & `Deque` Interfaces

> **Queue Contract:** Elements are processed in a specific order — typically FIFO (first-in, first-out) or by priority.
> **Deque Contract:** Double-ended queue — elements can be added/removed from both ends.

**Queue's two method families:**


| Operation | Throws Exception              | Returns Special Value        |
| --------- | ----------------------------- | ---------------------------- |
| Insert    | `add(e)` → throws if full     | `offer(e)` → returns `false` |
| Remove    | `remove()` → throws if empty  | `poll()` → returns `null`    |
| Examine   | `element()` → throws if empty | `peek()` → returns `null`    |


---

### PriorityQueue — Not FIFO, It's Priority

**What it is:** A min-heap. The element with the highest priority (smallest value by default) is always at the head.

**How it works internally:**

```
After offer(30), offer(10), offer(20), offer(5):

Heap array: [5, 10, 20, 30]

Tree view (min-heap):
       5
      / \
    10   20
    /
  30

poll() → returns 5, then re-heapifies:
       10
      / \
    30   20
```

**Real-World Example — Task Scheduler by Priority:**

```java
record Task(String name, int priority) implements Comparable<Task> {
    @Override
    public int compareTo(Task other) {
        return Integer.compare(this.priority, other.priority); // lower = higher priority
    }
}

PriorityQueue<Task> taskQueue = new PriorityQueue<>();
taskQueue.offer(new Task("Send report", 3));
taskQueue.offer(new Task("Fix critical bug", 1));
taskQueue.offer(new Task("Update docs", 5));
taskQueue.offer(new Task("Deploy hotfix", 1));

while (!taskQueue.isEmpty()) {
    Task next = taskQueue.poll(); // always gets the highest priority (lowest number)
    System.out.println(next.name() + " (priority: " + next.priority() + ")");
}
// Output:
// Fix critical bug (priority: 1)
// Deploy hotfix (priority: 1)
// Send report (priority: 3)
// Update docs (priority: 5)
```

**Gotcha:** Iterating or printing a `PriorityQueue` does NOT show elements in sorted order. Only `poll()` guarantees the min element. The internal array is a heap, not a sorted array.

---

### ArrayDeque — The Swiss Army Knife

**What it is:** A resizable circular array that implements `Deque`. Fastest choice for both stack (LIFO) and queue (FIFO) operations.

**How it works internally:**

```
Circular array (capacity=8):
         head              tail
          ↓                 ↓
┌────┬────┬────┬────┬────┬────┬────┬────┐
│    │    │ A  │ B  │ C  │ D  │    │    │
└────┴────┴────┴────┴────┴────┴────┴────┘
  0    1    2    3    4    5    6    7

addFirst("Z"):
    head
     ↓
┌────┬────┬────┬────┬────┬────┬────┬────┐
│    │ Z  │ A  │ B  │ C  │ D  │    │    │
└────┴────┴────┴────┴────┴────┴────┴────┘

addLast("E"):
                                tail
                                 ↓
┌────┬────┬────┬────┬────┬────┬────┬────┐
│    │ Z  │ A  │ B  │ C  │ D  │ E  │    │
└────┴────┴────┴────┴────┴────┴────┴────┘
```

**Real-World Example — Using as a Stack (Undo History):**

```java
Deque<String> undoStack = new ArrayDeque<>();
undoStack.push("Type 'Hello'");
undoStack.push("Bold text");
undoStack.push("Change font");

System.out.println(undoStack.peek()); // "Change font" — most recent action
undoStack.pop();                       // undo "Change font"
System.out.println(undoStack.peek()); // "Bold text"
```

**Real-World Example — Using as a Queue (Request Processing):**

```java
Deque<HttpRequest> requestQueue = new ArrayDeque<>();
requestQueue.offer(new HttpRequest("/api/users"));    // enqueue
requestQueue.offer(new HttpRequest("/api/orders"));

HttpRequest next = requestQueue.poll(); // dequeue — FIFO: /api/users first
```

---

## Comparable vs. Comparator

### Comparable — "I know how to sort myself"

```java
public class Employee implements Comparable<Employee> {
    private String name;
    private BigDecimal salary;

    @Override
    public int compareTo(Employee other) {
        return this.name.compareTo(other.name); // natural ordering: alphabetical by name
    }
}

List<Employee> team = new ArrayList<>(/* ... */);
Collections.sort(team);     // uses compareTo — sorts by name
```

### Comparator — "Let me sort you in different ways"

```java
// Sort by salary descending, then by name as tiebreaker
Comparator<Employee> bySalaryDesc = Comparator
    .comparing(Employee::getSalary)
    .reversed()
    .thenComparing(Employee::getName);

team.sort(bySalaryDesc);

// Null-safe comparator
Comparator<Employee> byManager = Comparator.comparing(
    Employee::getManagerName,
    Comparator.nullsLast(Comparator.naturalOrder())
);
```

**Real-World Example — Flexible Product Sorting in an E-Commerce API:**

```java
public class ProductSortFactory {
    public static Comparator<Product> fromQueryParam(String sortBy) {
        return switch (sortBy) {
            case "price_asc"  -> Comparator.comparing(Product::getPrice);
            case "price_desc" -> Comparator.comparing(Product::getPrice).reversed();
            case "name"       -> Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
            case "rating"     -> Comparator.comparing(Product::getRating).reversed()
                                           .thenComparing(Product::getReviewCount).reversed();
            case "newest"     -> Comparator.comparing(Product::getCreatedAt).reversed();
            default           -> Comparator.comparing(Product::getName);
        };
    }
}

// In the controller
List<Product> products = repository.findByCategory(category);
products.sort(ProductSortFactory.fromQueryParam(request.getParam("sort")));
```

---

## Iterators & Fail-Fast / Fail-Safe

### The Problem — Modifying During Iteration

```java
// BUG — throws ConcurrentModificationException
List<String> names = new ArrayList<>(List.of("Alice", "Bob", "Charlie"));
for (String name : names) {
    if (name.startsWith("B")) {
        names.remove(name); // structural modification during iteration!
    }
}
```

### Safe Alternatives

```java
// Solution 1: Iterator.remove()
Iterator<String> it = names.iterator();
while (it.hasNext()) {
    if (it.next().startsWith("B")) {
        it.remove(); // safe — iterator manages the modification
    }
}

// Solution 2: removeIf (Java 8+) — cleanest
names.removeIf(name -> name.startsWith("B"));

// Solution 3: Collect indices or items to remove, then remove after iteration
List<String> toRemove = new ArrayList<>();
for (String name : names) {
    if (name.startsWith("B")) toRemove.add(name);
}
names.removeAll(toRemove);
```

### How Fail-Fast Works Internally

Every modifiable collection has an internal `modCount` counter. Structural modifications (add, remove, clear) increment it. When an iterator is created, it snapshots `modCount` as `expectedModCount`. On every `next()` call, it checks:

```java
if (modCount != expectedModCount)
    throw new ConcurrentModificationException();
```

### Fail-Safe Collections (java.util.concurrent)

Collections like `CopyOnWriteArrayList` and `ConcurrentHashMap` use different strategies:

- `**CopyOnWriteArrayList**` — every write creates a new copy of the internal array. Iterators work on the snapshot. Safe but expensive for writes.
- `**ConcurrentHashMap**` — weakly consistent iterators. Never throw CME, may or may not reflect concurrent modifications.

---

## Collections Utility Class

```java
// Immutable collections (Java 9+) — truly immutable, no null elements
List<String> list = List.of("a", "b", "c");
Set<String> set = Set.of("a", "b", "c");
Map<String, Integer> map = Map.of("a", 1, "b", 2);

// Unmodifiable views — wraps a mutable source (changes to source ARE visible)
List<String> mutable = new ArrayList<>(List.of("a", "b"));
List<String> view = Collections.unmodifiableList(mutable);
mutable.add("c");
System.out.println(view); // [a, b, c] — view reflects the change!

// Copy-based immutability (Java 10+)
List<String> immutableCopy = List.copyOf(mutable); // snapshot, no further changes

// Synchronized wrappers
List<String> syncList = Collections.synchronizedList(new ArrayList<>());

// Sorting and searching
Collections.sort(list);
int index = Collections.binarySearch(sortedList, "target"); // list must be sorted!

// Convenience factories
List<String> empty = Collections.emptyList();
List<String> single = Collections.singletonList("only");
List<String> repeated = Collections.nCopies(5, "default");
```

---

## ConcurrentHashMap Preview

**Key differences from HashMap:**

- No null keys or null values allowed (avoids ambiguity in concurrent operations)
- Weakly consistent iterators — never throw `ConcurrentModificationException`
- Node-level CAS + synchronized (Java 8+) instead of locking the whole map
- Atomic compound operations: `computeIfAbsent`, `merge`, `putIfAbsent`

```java
ConcurrentHashMap<String, AtomicInteger> hitCounter = new ConcurrentHashMap<>();

// Atomic — safe for concurrent access
hitCounter.computeIfAbsent("/api/users", k -> new AtomicInteger(0)).incrementAndGet();
hitCounter.computeIfAbsent("/api/orders", k -> new AtomicInteger(0)).incrementAndGet();
```

Full deep dive comes in the Concurrency phase.

---

# Part II — Generics

## The World Before Generics — Pain Points

Before Java 5, collections stored `Object`. Every interaction with a collection had the same problems:

### Pain Point 1: No Type Safety — Bugs Hide Until Runtime

```java
// Pre-generics Java (before Java 5)
List employeeNames = new ArrayList();
employeeNames.add("Alice");
employeeNames.add("Bob");
employeeNames.add(42); // OOPS — an Integer slipped in. No compiler error.

// Much later in the code...
for (int i = 0; i < employeeNames.size(); i++) {
    String name = (String) employeeNames.get(i); // ClassCastException on 42!
}
```

The bug was introduced on line 4 but only detected on line 8 — possibly in production, possibly weeks later. The compiler couldn't help because `List` accepted any `Object`.

### Pain Point 2: Casts Everywhere — Verbose and Error-Prone

```java
Map config = new HashMap();
config.put("timeout", 30);
config.put("retries", 3);

// Every. Single. Retrieval. Needs. A. Cast.
int timeout = (Integer) config.get("timeout");
int retries = (Integer) config.get("retries");

// Typo goes unnoticed:
int maxConn = (Integer) config.get("maxConnections"); // NullPointerException — key doesn't exist
// Or worse:
String maxConn = (String) config.get("timeout"); // ClassCastException — it's an Integer
```

### Pain Point 3: No Way to Write Reusable, Type-Safe Code

```java
// Want a Box that holds anything but gives it back with the right type?
// Pre-generics: impossible without casts
class Box {
    private Object item;
    public void set(Object item) { this.item = item; }
    public Object get() { return item; }
}

Box box = new Box();
box.set("Hello");
String s = (String) box.get(); // works, but you always have to cast

box.set(42);
String s2 = (String) box.get(); // ClassCastException! No compile-time protection.
```

### Pain Point 4: API Documentation Was the Only "Type Safety"

Without generics, you had to read Javadoc or comments to know what a collection was supposed to hold:

```java
/** @return List of String — employee names. DO NOT put non-Strings in this list! */
public List getEmployeeNames() { ... }

// Nothing stopped someone from doing:
List names = getEmployeeNames();
names.add(new Date()); // compiles fine, violates the invisible contract
```

---

## How Generics Solve These Problems

### Solution 1: Compile-Time Type Checking

```java
List<String> employeeNames = new ArrayList<>();
employeeNames.add("Alice");
employeeNames.add("Bob");
employeeNames.add(42); // COMPILE ERROR: "required: String, found: int"
// Bug caught IMMEDIATELY — before you even run the program
```

### Solution 2: No More Casts

```java
Map<String, Integer> config = new HashMap<>();
config.put("timeout", 30);
config.put("retries", 3);

int timeout = config.get("timeout"); // auto-unboxed, no cast needed
// The compiler knows the value is Integer
```

### Solution 3: Reusable, Type-Safe Components

```java
class Box<T> {
    private T item;
    public void set(T item) { this.item = item; }
    public T get() { return item; }
}

Box<String> stringBox = new Box<>();
stringBox.set("Hello");
String s = stringBox.get(); // no cast — compiler knows it's String

stringBox.set(42); // COMPILE ERROR — can't put Integer in Box<String>

Box<Integer> intBox = new Box<>(); // same class, different type — code reuse!
intBox.set(42);
int n = intBox.get(); // auto-unboxed
```

### Solution 4: Self-Documenting APIs

```java
public List<String> getEmployeeNames() { ... }
// The return type IS the documentation — it's a List of String. Period.

List<String> names = getEmployeeNames();
names.add(new Date()); // COMPILE ERROR — the contract is enforced by the compiler
```

---

## Generic Classes

A generic class declares one or more **type parameters** that act as placeholders for real types:

```java
public class Pair<A, B> {
    private final A first;
    private final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public A getFirst() { return first; }
    public B getSecond() { return second; }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}

// Usage — the caller specifies the types
Pair<String, Integer> nameAge = new Pair<>("Alice", 30);
String name = nameAge.getFirst();   // String — no cast
int age = nameAge.getSecond();      // Integer (auto-unboxed) — no cast

Pair<Double, Double> coordinates = new Pair<>(40.7128, -74.0060);
```

**Real-World Example — API Response Wrapper:**

```java
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String errorMessage;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }

    private ApiResponse(boolean success, T data, String errorMessage) {
        this.success = success;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public T getData() { return data; }
    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
}

// Usage — different endpoints return different data types, all wrapped consistently
ApiResponse<User> userResp = ApiResponse.ok(new User("Alice"));
ApiResponse<List<Order>> ordersResp = ApiResponse.ok(List.of(order1, order2));
ApiResponse<Void> deleteResp = ApiResponse.error("Not authorized");

User user = userResp.getData(); // type-safe — no cast needed
```

---

## Generic Methods

A generic method declares its own type parameter(s) independently of the class. The type is inferred from the arguments:

```java
public class CollectionUtils {

    // <T> declares the type parameter; T is then used in the return type and parameter
    public static <T> T firstOrNull(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }

    public static <T> List<T> filter(List<T> list, Predicate<T> condition) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (condition.test(item)) {
                result.add(item);
            }
        }
        return result;
    }
}

// Type inference — compiler figures out T from the argument
String first = CollectionUtils.firstOrNull(List.of("a", "b", "c")); // T = String
Integer firstNum = CollectionUtils.firstOrNull(List.of(1, 2, 3));   // T = Integer

List<String> longNames = CollectionUtils.filter(
    List.of("Al", "Alexander", "Bo", "Benjamin"),
    name -> name.length() > 3
); // ["Alexander", "Benjamin"]
```

---

## Bounded Type Parameters

Sometimes "any type" is too broad. You need to constrain what T can be:

### Upper Bound — `T extends SomeType`

```java
// T must be Comparable — otherwise, how do we compare elements?
public static <T extends Comparable<T>> T findMax(List<T> list) {
    if (list.isEmpty()) throw new IllegalArgumentException("Empty list");
    T max = list.get(0);
    for (T item : list) {
        if (item.compareTo(max) > 0) {
            max = item;
        }
    }
    return max;
}

String longest = findMax(List.of("apple", "banana", "cherry")); // "cherry"
Integer biggest = findMax(List.of(3, 1, 4, 1, 5));              // 5

// This would NOT compile:
// findMax(List.of(new Object(), new Object())); // Object doesn't implement Comparable
```

### Multiple Bounds

```java
// T must implement BOTH Serializable AND Comparable
public static <T extends Serializable & Comparable<T>> void process(T item) {
    // can call Serializable methods and Comparable methods on item
}
```

**Rules:**

- Use `extends` for both class bounds and interface bounds (never `implements` in generics)
- At most one class bound, and it must come first: `<T extends MyClass & Interface1 & Interface2>`

**Real-World Example — Repository With Type-Safe ID Constraint:**

```java
public interface Identifiable<ID extends Serializable> {
    ID getId();
}

public class InMemoryRepository<T extends Identifiable<ID>, ID extends Serializable> {
    private final Map<ID, T> store = new HashMap<>();

    public T save(T entity) {
        store.put(entity.getId(), entity);
        return entity;
    }

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<T> findBy(Predicate<T> filter) {
        return store.values().stream().filter(filter).toList();
    }

    public boolean delete(ID id) {
        return store.remove(id) != null;
    }
}

// Usage
public class User implements Identifiable<String> {
    private final String id;
    private final String name;
    // ...
    @Override public String getId() { return id; }
}

InMemoryRepository<User, String> userRepo = new InMemoryRepository<>();
userRepo.save(new User("u-001", "Alice"));
Optional<User> found = userRepo.findById("u-001");
List<User> admins = userRepo.findBy(user -> user.isAdmin());
```

---

## Wildcards — The Unknown Type

Wildcards (`?`) solve a specific problem: **generics are invariant**. `List<Integer>` is NOT a subtype of `List<Number>`, even though `Integer` extends `Number`.

### Why Invariance Exists

```java
// If this were allowed...
List<Integer> ints = new ArrayList<>(List.of(1, 2, 3));
List<Number> nums = ints; // hypothetical — this does NOT compile

// ...then this would corrupt the list:
nums.add(3.14); // adding a Double to what's actually a List<Integer>!
Integer n = ints.get(2); // ClassCastException — 3.14 is not an Integer

// Java prevents this entirely by making generics invariant.
```

### Unbounded Wildcard — `List<?>`

"A list of some unknown type." You can read as `Object` but can't add anything (except `null`).

```java
public static void printAll(List<?> list) {
    for (Object item : list) {
        System.out.println(item); // reads are safe — everything is at least Object
    }
    // list.add("hello"); // COMPILE ERROR — we don't know what type the list holds
}

printAll(List.of("a", "b"));     // works with List<String>
printAll(List.of(1, 2, 3));      // works with List<Integer>
printAll(List.of(true, false));   // works with List<Boolean>
```

### Upper Bounded Wildcard — `List<? extends Number>` (Producer)

"A list of Number or any subtype." You can **read** as `Number`, but you **cannot add** (because the actual type is unknown).

```java
public static double sum(List<? extends Number> numbers) {
    double total = 0;
    for (Number n : numbers) {  // safe — every element IS-A Number
        total += n.doubleValue();
    }
    // numbers.add(42); // COMPILE ERROR — might be List<Double>, can't add Integer
    return total;
}

sum(List.of(1, 2, 3));         // List<Integer> — works!
sum(List.of(1.5, 2.5, 3.5));   // List<Double> — works!
sum(List.of(1L, 2L, 3L));      // List<Long> — works!
```

### Lower Bounded Wildcard — `List<? super Integer>` (Consumer)

"A list that can hold Integer or any supertype." You can **add** `Integer`, but **reads** come back as `Object`.

```java
public static void addNumbers(List<? super Integer> list) {
    list.add(1);    // safe — Integer is compatible with Integer, Number, or Object
    list.add(2);
    list.add(3);
    // Integer n = list.get(0); // COMPILE ERROR — might be List<Object>
    Object o = list.get(0); // only safe read type is Object
}

List<Integer> ints = new ArrayList<>();
List<Number> nums = new ArrayList<>();
List<Object> objs = new ArrayList<>();

addNumbers(ints); // works — List<Integer> accepts ? super Integer
addNumbers(nums); // works — List<Number> accepts ? super Integer
addNumbers(objs); // works — List<Object> accepts ? super Integer
```

---

## The PECS Principle — Producer Extends, Consumer Super

**The single most important rule for wildcards:**


| If a structure...                      | Use            | Mnemonic                 |
| -------------------------------------- | -------------- | ------------------------ |
| **Produces** values (you READ from it) | `? extends T`  | **P**roducer **E**xtends |
| **Consumes** values (you WRITE to it)  | `? super T`    | **C**onsumer **S**uper   |
| Both reads and writes                  | Exact type `T` | No wildcard              |


**Real-World Example — Flexible Data Transfer:**

```java
// Without PECS — overly restrictive:
public static <T> void transfer(List<T> source, List<T> destination) {
    for (T item : source) {
        destination.add(item);
    }
}
// transfer(List<Integer>, List<Number>) → COMPILE ERROR!
// because List<Integer> is not List<Number>

// With PECS — flexible:
public static <T> void transfer(List<? extends T> source, List<? super T> destination) {
    for (T item : source) { // source produces T → ? extends T
        destination.add(item); // destination consumes T → ? super T
    }
}
// Now this works:
List<Integer> ints = List.of(1, 2, 3);
List<Number> nums = new ArrayList<>();
transfer(ints, nums); // T inferred as Integer
// ints produces Integers (extends), nums consumes Integers (super)
```

**JDK Examples That Use PECS:**

```java
// Collections.copy — source produces, dest consumes
public static <T> void copy(List<? super T> dest, List<? extends T> src)

// Collections.addAll — collection consumes
public static <T> boolean addAll(Collection<? super T> c, T... elements)

// Comparable — typically: <T extends Comparable<? super T>>
// This means T can be compared to itself OR any supertype
public static <T extends Comparable<? super T>> void sort(List<T> list)
```

---

## Type Erasure — The Hidden Cost

**What happens:** The Java compiler removes all generic type information during compilation. At runtime, the JVM sees only raw types.

```java
// What you write:
List<String> strings = new ArrayList<>();
List<Integer> ints = new ArrayList<>();

// What the JVM sees after erasure:
List strings = new ArrayList();
List ints = new ArrayList();

// At runtime:
strings.getClass() == ints.getClass() // TRUE — both are just ArrayList
```

**Where the compiler helps:** It inserts casts at call sites and generates bridge methods for polymorphism:

```java
// What you write:
String s = strings.get(0);

// What the compiler generates (bytecode):
String s = (String) strings.get(0); // cast is invisible but present
```

### What You CANNOT Do Because of Type Erasure


| Forbidden                                             | Why                                                 | Workaround                                                           |
| ----------------------------------------------------- | --------------------------------------------------- | -------------------------------------------------------------------- |
| `new T()`                                             | T is unknown at runtime                             | Pass `Supplier<T>`: `Supplier<T> factory = MyClass::new;`            |
| `new T[10]`                                           | Arrays need to know their element type at runtime   | Use `(T[]) new Object[10]` with `@SuppressWarnings` or use `List<T>` |
| `obj instanceof List<String>`                         | Erased to `List` — can only check `instanceof List` | Check `instanceof List<?>` then verify contents                      |
| `static T field`                                      | T is per-instance; static is per-class              | Use `static Object` or redesign                                      |
| `catch (T e)`                                         | Exception handling is at runtime; T doesn't exist   | Catch specific exceptions                                            |
| Overload `foo(List<String>)` and `foo(List<Integer>)` | Both erase to `foo(List)` — same signature          | Rename methods or use different parameter types                      |


---

## Caveats & Gotchas with Generics

### Caveat 1: Generics and Arrays Don't Mix Well

Arrays are **covariant** and **reified** (they know their element type at runtime). Generics are **invariant** and **erased**. This mismatch causes problems:

```java
// Arrays are covariant — this compiles but is unsafe:
Object[] array = new String[3];
array[0] = 42; // ArrayStoreException at RUNTIME

// Generics are invariant — this doesn't compile (safe):
// List<Object> list = new ArrayList<String>(); // COMPILE ERROR

// You cannot create a generic array:
// List<String>[] array = new List<String>[10]; // COMPILE ERROR
// Because at runtime it's just List[], and you could put List<Integer> in it.
```

### Caveat 2: Primitive Types Not Allowed

```java
// List<int> list = new ArrayList<>(); // COMPILE ERROR
List<Integer> list = new ArrayList<>(); // must use wrapper type
list.add(42);     // autoboxing: int → Integer
int n = list.get(0); // auto-unboxing: Integer → int
```

Autoboxing has a cost — creating `Integer` objects. For performance-critical code with millions of primitives, consider specialized libraries like Eclipse Collections (`IntList`) or arrays.

### Caveat 3: Type Inference Can Be Surprising

```java
// Diamond operator — Java infers the type from the left side
List<String> list = new ArrayList<>(); // <String> inferred

// Sometimes inference fails:
// Collections.emptyList() returns List<Object> without context
// Fix with explicit type witness:
List<String> empty = Collections.<String>emptyList();

// Or use the modern factory:
List<String> empty = List.of(); // type inferred from assignment
```

### Caveat 4: Heap Pollution via Raw Types

```java
List<String> strings = new ArrayList<>();
List rawList = strings; // raw type — compiler warns but allows
rawList.add(42);        // no error at compile time (raw type bypasses generics)

String s = strings.get(0); // ClassCastException at runtime!
// The list now contains an Integer but is typed as List<String>
```

**Rule:** Never use raw types. If you need a collection of unknown type, use `List<?>`.

### Caveat 5: Cannot Use instanceof with Parameterized Types

```java
// obj instanceof List<String> // COMPILE ERROR — type erased

// What you CAN do:
if (obj instanceof List<?> list) {
    // we know it's a List, but not what type of elements
    if (!list.isEmpty() && list.get(0) instanceof String) {
        // now we have reasonable confidence it's a List<String>
    }
}
```

### Caveat 6: Recursive Bounds Look Confusing but Are Useful

```java
// "T must be comparable to itself" — the standard pattern for Comparable
public static <T extends Comparable<T>> T max(T a, T b) {
    return a.compareTo(b) >= 0 ? a : b;
}

// Even more flexible — T comparable to itself or a supertype:
public static <T extends Comparable<? super T>> T max(T a, T b) {
    return a.compareTo(b) >= 0 ? a : b;
}
// This allows: max(apple, banana) where Apple extends Fruit implements Comparable<Fruit>
```

### Caveat 7: Varargs + Generics = Heap Pollution Warning

```java
// This generates an "unchecked" warning:
public static <T> List<T> toList(T... items) {
    return Arrays.asList(items);
}
// Varargs creates an Object[] at runtime. If T is a generic type,
// the array type doesn't match, risking heap pollution.

// Use @SafeVarargs if the method doesn't store into the array or expose it:
@SafeVarargs
public static <T> List<T> toList(T... items) {
    return List.of(items);
}
```

---

## Big-O Cheat Sheet


| Collection      | `add`        | `get` / `contains`            | `remove`                       | Order               |
| --------------- | ------------ | ----------------------------- | ------------------------------ | ------------------- |
| `ArrayList`     | O(1)*        | O(1) by index / O(n) contains | O(n)                           | Insertion           |
| `LinkedList`    | O(1) at ends | O(n)                          | O(1) at ends / O(n) by index   | Insertion           |
| `ArrayDeque`    | O(1)*        | O(n) contains                 | O(1) at ends                   | Insertion           |
| `HashSet`       | O(1)*        | O(1)*                         | O(1)*                          | None                |
| `LinkedHashSet` | O(1)*        | O(1)*                         | O(1)*                          | Insertion           |
| `TreeSet`       | O(log n)     | O(log n)                      | O(log n)                       | Sorted              |
| `HashMap`       | O(1)*        | O(1)*                         | O(1)*                          | None                |
| `LinkedHashMap` | O(1)*        | O(1)*                         | O(1)*                          | Insertion/Access    |
| `TreeMap`       | O(log n)     | O(log n)                      | O(log n)                       | Sorted (keys)       |
| `PriorityQueue` | O(log n)     | O(n)                          | O(log n) poll / O(n) arbitrary | Priority (min-heap) |


 *= amortized, assuming good hash distribution*

---

## Exercises Roadmap


| #   | Exercise               | Concepts Practiced                                                | Difficulty |
| --- | ---------------------- | ----------------------------------------------------------------- | ---------- |
| 1   | **Custom HashMap**     | Hashing, buckets, linked lists, resizing, `Iterable`              | ★★★☆☆      |
| 2   | **LRU Cache**          | `LinkedHashMap`, doubly-linked list, O(1) design                  | ★★★☆☆      |
| 3   | **Inventory System**   | `HashMap`, `TreeMap`, `PriorityQueue`, `Comparable`, `Comparator` | ★★★★☆      |
| 4   | **Generic Repository** | Bounded generics, wildcards, PECS, `Predicate`                    | ★★★★☆      |


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


| Mistake                                                | Consequence                                                           | Fix                                           |
| ------------------------------------------------------ | --------------------------------------------------------------------- | --------------------------------------------- |
| Not overriding `hashCode()` when overriding `equals()` | HashMap/HashSet silently fails to find entries                        | Always override both together                 |
| Using `==` instead of `.equals()` for keys             | Works for String literals (interning) but fails for `new String(...)` | Always use `.equals()`                        |
| Modifying a collection during for-each loop            | `ConcurrentModificationException`                                     | Use `Iterator.remove()` or `removeIf()`       |
| Using raw types (`List` instead of `List<String>`)     | No type safety, compiler warnings                                     | Always parameterize                           |
| Using `double` for money in Product.price              | Rounding errors (0.1 + 0.2 ≠ 0.3)                                     | Use `BigDecimal`                              |
| Choosing `LinkedList` as default List                  | Cache-unfriendly, slower than ArrayList for most workloads            | Default to `ArrayList`                        |
| Ignoring PECS when designing generic APIs              | Overly restrictive method signatures                                  | Apply extends/super correctly                 |
| Using mutable objects as HashMap keys                  | Entries become unreachable after key mutation                         | Use immutable keys (String, Integer, records) |
| Printing a `PriorityQueue` expecting sorted output     | Internal heap array is NOT sorted                                     | Only `poll()` guarantees order                |
| Mixing arrays and generics                             | Compile errors or heap pollution                                      | Prefer `List<T>` over `T[]`                   |


---

## Key Terms Glossary


| Term                 | Definition                                                                                                     |
| -------------------- | -------------------------------------------------------------------------------------------------------------- |
| **Amortized O(1)**   | Usually O(1), but occasionally O(n) for resizing; averaged over all operations it's O(1)                       |
| **Autoboxing**       | Automatic conversion between primitives and wrappers (int ↔ Integer)                                           |
| **Bucket**           | A slot in a hash table's internal array that holds entries mapping to the same index                           |
| **Capacity**         | The number of buckets in a HashMap (default: 16, always a power of 2)                                          |
| **Comparable**       | Interface for natural ordering — the class defines its own sort order                                          |
| **Comparator**       | External ordering strategy — sort objects without modifying their class                                        |
| **Covariant**        | Arrays are covariant: `String[]` IS-A `Object[]`. Generics are invariant: `List<String>` is NOT `List<Object>` |
| **Diamond operator** | The `<>` in `new ArrayList<>()` — lets the compiler infer the type                                             |
| **Fail-fast**        | Iterator throws `ConcurrentModificationException` if the collection is modified during iteration               |
| **Fail-safe**        | Iterator works on a snapshot; never throws, but may show stale data                                            |
| **Heap pollution**   | Occurs when a variable of a parameterized type refers to an object of a different type                         |
| **Invariant**        | Generic types are invariant: `List<Dog>` is NOT a subtype of `List<Animal>`                                    |
| **Load factor**      | Threshold ratio (`size / capacity`) that triggers resize (default: 0.75)                                       |
| **PECS**             | Producer Extends, Consumer Super — rule for choosing wildcard bounds                                           |
| **Raw type**         | A generic type used without type parameters: `List` instead of `List<String>`. Bypasses type safety.           |
| **Reifiable type**   | A type whose information is fully available at runtime (e.g., `List`, `String`, but NOT `List<String>`)        |
| **Treeification**    | HashMap converts a long collision chain (>8 entries) from linked list to Red-Black tree                        |
| **Type erasure**     | Compiler removes generic type info at compile time; the JVM sees raw types only                                |
| **Type witness**     | Explicit type argument on a method call: `Collections.<String>emptyList()`                                     |
| **Wildcard**         | `?` in generics — represents an unknown type; can be bounded with `extends` or `super`                         |


---

## Progress Tracker

Use this to track your study progress:

**Part I — Collections**

- **Hierarchy:** Drew the full hierarchy diagram from memory
- **ArrayList:** Explained internal array, growth formula, and when to preallocate
- **LinkedList:** Understood node structure, why it's rarely the best choice
- **HashSet:** Explained it's backed by HashMap, practiced set operations (union, intersection, difference)
- **LinkedHashSet:** Used for deduplication with order preservation
- **TreeSet:** Used NavigableSet methods (floor, ceiling, subSet)
- **EnumSet:** Created bit-vector-backed enum sets
- **HashMap:** Walked through put() step-by-step, explained treeification and resize
- **LinkedHashMap:** Built LRU cache with accessOrder=true
- **TreeMap:** Used range queries (subMap, floorEntry, ceilingEntry)
- **PriorityQueue:** Built a task scheduler, understood heap vs sorted order
- **ArrayDeque:** Used as both stack and queue
- **Comparable/Comparator:** Built multi-field Comparator chains with method references
- **Iterators:** Reproduced and fixed ConcurrentModificationException three ways
- **Collections utility:** Compared List.of() vs unmodifiableList vs copyOf

**Part II — Generics**

- **Pain Points:** Wrote pre-generics code and experienced the ClassCastException firsthand
- **Generic Class:** Built a Pair<A,B> and ApiResponse
- **Generic Method:** Wrote a filter method with type inference
- **Bounded Types:** Used `<T extends Comparable<T>>` for a findMax method
- **Wildcards:** Practiced `? extends` (producer) and `? super` (consumer)
- **PECS:** Wrote a transfer method using both extends and super
- **Type Erasure:** Listed 6 things you can't do and explained why
- **Caveats:** Encountered and resolved heap pollution, raw type warning, and array-generic mismatch

**Exercises**

- **Exercise 1:** Custom HashMap — implemented with resize and iteration
- **Exercise 2:** LRU Cache — both LinkedHashMap and manual approaches
- **Exercise 3:** Inventory System — HashMap + TreeMap + PriorityQueue integration
- **Exercise 4:** Generic Repository — bounded generics, wildcards, PECS demo
- **Self-Assessment:** Completed all checklist items in README.md

---

## What's Next

After completing this phase, you'll have the data structure and type system fluency needed for:

- **Phase 04 (Functional Programming & Streams):** Streams operate *on* collections. You'll chain `filter`, `map`, `reduce` on the collections you've mastered here.
- **Phase 05+ (Concurrency):** Thread-safe collections (`ConcurrentHashMap`, `CopyOnWriteArrayList`) build on the non-concurrent ones.
- **Real-world projects:** Spring Data repositories, Hibernate entity collections, and cache implementations all assume you can choose and use the right collection.

**The bottom line:** Collections are how Java programs organize data. Generics are how they do it safely. The details matter — knowing *why* `HashMap` uses a power-of-2 capacity, or *why* `List<Integer>` isn't `List<Number>`, is what separates a developer who writes correct code from one who debugs mysterious production failures.