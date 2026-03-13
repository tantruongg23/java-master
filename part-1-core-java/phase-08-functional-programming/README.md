# Phase 08 — Functional Programming

**Duration:** ~2–3 weeks · **Total effort:** ~23 hours

---

## Learning Objectives

By the end of this phase you will be able to:

1. Write concise, expressive code using **lambda expressions** and **method references**.
2. Use the standard **functional interfaces** (`Function`, `Predicate`, `Consumer`, `Supplier`, etc.) fluently.
3. Build complex data-processing pipelines with the **Stream API**.
4. Choose the right **Collector** for aggregation, grouping, and partitioning.
5. Use **Optional** correctly and avoid common anti-patterns.
6. Understand when **parallel streams** help and when they hurt.
7. Compose small functions into larger behaviours using `andThen`, `compose`, and predicate chaining.

---

## Topics

### Lambda Expressions (2 h)

- Syntax forms: `(a, b) -> a + b`, single-expression vs block body.
- Capturing variables from enclosing scope.
- **Effectively final** requirement and why it exists.
- Lambdas vs anonymous inner classes — performance and semantics.

### Functional Interfaces (3 h)

| Interface | Signature | Typical use |
|-----------|-----------|-------------|
| `Function<T,R>` | `R apply(T t)` | Transformations |
| `Predicate<T>` | `boolean test(T t)` | Filtering |
| `Consumer<T>` | `void accept(T t)` | Side effects (logging, sending) |
| `Supplier<T>` | `T get()` | Lazy creation, factories |
| `BiFunction<T,U,R>` | `R apply(T t, U u)` | Two-arg transformations |
| `UnaryOperator<T>` | `T apply(T t)` | Same-type transformation |
| `BinaryOperator<T>` | `T apply(T t1, T t2)` | Same-type reduction |

- `@FunctionalInterface` annotation and compile-time enforcement.
- Writing custom functional interfaces.

### Method References (1 h)

| Kind | Syntax | Example |
|------|--------|---------|
| Static method | `Class::staticMethod` | `Integer::parseInt` |
| Instance method of a particular object | `object::method` | `System.out::println` |
| Instance method of an arbitrary object | `Class::method` | `String::toLowerCase` |
| Constructor | `Class::new` | `ArrayList::new` |

### Stream API — Fundamentals (2 h)

- The pipeline model: **source → intermediate operations → terminal operation**.
- Lazy evaluation — intermediates don't execute until a terminal is invoked.
- Streams are single-use; creating vs reusing.
- Primitive streams: `IntStream`, `LongStream`, `DoubleStream`.

### Intermediate Operations (3 h)

| Operation | Purpose |
|-----------|---------|
| `filter(Predicate)` | Keep elements matching a condition |
| `map(Function)` | Transform each element |
| `flatMap(Function)` | One-to-many transformation, flatten nested structures |
| `peek(Consumer)` | Debugging / side-effect (use sparingly) |
| `distinct()` | Remove duplicates (uses `equals`/`hashCode`) |
| `sorted()` / `sorted(Comparator)` | Order elements |
| `limit(n)` / `skip(n)` | Sub-stream windowing |

### Terminal Operations (3 h)

| Operation | Returns |
|-----------|---------|
| `forEach(Consumer)` | `void` |
| `collect(Collector)` | Collected result |
| `reduce(identity, BinaryOperator)` | Reduced value |
| `count()` | `long` |
| `min(Comparator)` / `max(Comparator)` | `Optional` |
| `anyMatch` / `allMatch` / `noneMatch` | `boolean` |
| `findFirst()` / `findAny()` | `Optional` |
| `toArray()` | `Object[]` or typed array |

### Collectors (3 h)

- `toList()`, `toSet()`, `toUnmodifiableList()`.
- `toMap(keyMapper, valueMapper)` — handling duplicate keys.
- `groupingBy(classifier)` and downstream collectors.
- `partitioningBy(predicate)`.
- `joining(delimiter)`.
- Writing a **custom Collector** (`Collector.of`): supplier, accumulator, combiner, finisher, characteristics.

### Parallel Streams (2 h)

- `parallelStream()` vs `stream().parallel()`.
- Underlying `ForkJoinPool.commonPool()`.
- `Spliterator` — how the source is split for parallelism.
- Performance pitfalls: small data sets, shared mutable state, ordering, I/O-bound operations.
- Benchmarking with JMH before parallelising.

### Optional (2 h)

- Creation: `Optional.of`, `Optional.ofNullable`, `Optional.empty`.
- Transformation: `map`, `flatMap`, `filter`.
- Retrieval: `orElse` vs `orElseGet` vs `orElseThrow` — performance and semantics.
- Anti-patterns: `Optional` as method parameter, `Optional` fields, `isPresent()`+`get()` pair.

### Immutability Patterns (1 h)

- `Collections.unmodifiableList/Map/Set`.
- `List.of`, `Map.of`, `Set.of` (Java 9+).
- Defensive copies in constructors and getters.
- `record` types as naturally immutable value objects.

### Composing Functions (1 h)

- `Function.andThen(after)` and `Function.compose(before)`.
- `Predicate.and()`, `Predicate.or()`, `Predicate.negate()`.
- Building reusable pipelines by composing small functions.

---

## References

| Resource | Notes |
|----------|-------|
| *Modern Java in Action* (Urma, Fusco, Mycroft) | The definitive guide to lambdas, streams, and functional Java |
| [Oracle Stream API Tutorial](https://docs.oracle.com/javase/tutorial/collections/streams/) | Official tutorial with examples |
| *Effective Java*, 3rd Ed. (Bloch), Items 42–48 | Lambda and stream best practices |
| [java.util.stream Javadoc](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/stream/package-summary.html) | API reference with detailed operation contracts |

---

## Exercises

### Exercise 1 — Sales Analytics Pipeline

Given `Transaction(id, date, amount, category, customer, region)`, build stream pipelines for:

1. **Top 5 customers** by total spend.
2. **Revenue by category per month** — nested grouping.
3. **Running average** of transaction amounts.
4. **Moving-window analysis** — average amount over the last N transactions per region.

Use `groupingBy`, `reducing`, and write at least one **custom Collector**.

**Bonus:** implement a parallel-stream version and compare performance on a large generated dataset.

### Exercise 2 — Stream-based ETL Processor

Build an Extract-Transform-Load pipeline entirely with streams:

```
source (file lines) → parse → normalise → enrich → filter invalid → load (sink)
```

- Each stage is a `Function` or `Predicate` that can be **composed**.
- Fluent API: `pipeline.extract(source).transform(normalize).transform(enrich).filter(valid).load(sink)`.
- Lazily process the file — don't load all lines into memory.

**Bonus:** add monitoring (count processed / filtered / errors) without breaking the functional pipeline (hint: `peek` or a custom collector).

### Exercise 3 — Functional Validation Framework

Build `Validator<T>` backed by `Predicate<T>` with descriptive error messages:

```java
Validator<String> username = Validator.of(notNull(), "must not be null")
    .and(lengthBetween(3, 50), "must be 3–50 chars")
    .and(matchesPattern("[a-zA-Z0-9_]+"), "only alphanumeric and underscore");

ValidationResult result = username.validate("ab");
// → invalid: ["must be 3–50 chars"]
```

- `ValidationResult` is either *valid* or contains a list of errors.
- Support **nested validation** for complex objects.

**Bonus:** implement async validation (`CompletableFuture<ValidationResult>`).

### Exercise 4 — Event Processing System

Build a functional event bus:

```java
bus.on("order.created", event -> processOrder(event));
bus.on("order.created", event -> sendConfirmation(event));
bus.emit("order.created", orderEvent);
```

- Handlers are `Consumer<Event>` composed with `andThen`.
- Support **filtering**, **mapping** (transform event before handler), and **debounce** (window-based deduplication).
- Event aggregation: collect N events before emitting a summary.

**Bonus:** implement back-pressure with bounded queues (`BlockingQueue`).

---

## Self-Assessment Checklist

- [ ] I can rewrite an anonymous inner class as a lambda and explain the differences.
- [ ] I know all four types of method references and when each applies.
- [ ] I can build a multi-stage stream pipeline with `filter`, `map`, `flatMap`, `collect`.
- [ ] I understand lazy evaluation and can explain why intermediate operations don't execute alone.
- [ ] I can write a custom `Collector` with `Collector.of`.
- [ ] I can use `groupingBy` with downstream collectors for nested aggregations.
- [ ] I know the difference between `orElse` and `orElseGet` and when it matters.
- [ ] I can explain three scenarios where parallel streams *hurt* performance.
- [ ] I can compose predicates and functions to build reusable pipelines.
- [ ] I avoid `Optional` anti-patterns (as field, as parameter, `isPresent`+`get`).
