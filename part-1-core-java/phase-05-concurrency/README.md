# Phase 05 — Concurrency & Multithreading

**Duration:** ~3–4 weeks · **Estimated effort:** 32 hours  
**Prerequisites:** Phase 04 (Exception Handling, I/O, NIO)

> **This phase is CRITICAL for architect-level mastery.**  
> Concurrency bugs are the hardest to reproduce, the hardest to debug,
> and the most expensive in production. Invest the time here.

---

## Learning Objectives

By the end of this phase you will be able to:

1. Create and manage threads, understand their lifecycle and daemon behaviour.
2. Write correct synchronized code and explain the Java Memory Model's visibility guarantees.
3. Choose among `synchronized`, `ReentrantLock`, `ReadWriteLock`, and `StampedLock`.
4. Design thread-safe classes using immutability, confinement, and safe publication.
5. Configure and tune thread pools using the Executor framework.
6. Compose asynchronous pipelines with `CompletableFuture`.
7. Select the right concurrent collection for a given access pattern.
8. Use atomic variables and understand CAS (Compare-And-Swap) semantics.
9. Diagnose and resolve deadlocks, livelocks, starvation, and race conditions.
10. Leverage the Fork/Join framework for divide-and-conquer parallelism.
11. Adopt Virtual Threads (Java 21) and structured concurrency for modern workloads.

---

## Topics

### 1. Thread Fundamentals *(2 h)*

- Creating threads: `Thread` subclass vs `Runnable` vs `Callable`.
- Thread lifecycle: NEW → RUNNABLE → BLOCKED / WAITING / TIMED_WAITING → TERMINATED.
- `Thread.sleep()`, `join()`, `interrupt()`, `isInterrupted()`.
- Daemon threads — JVM exits when only daemon threads remain.
- `Thread.UncaughtExceptionHandler`.
- Naming threads for debuggability.

### 2. Synchronization *(3 h)*

- **Intrinsic locks** — every object is a monitor.
- `synchronized` blocks vs methods; choosing the right lock granularity.
- **Visibility** — the `volatile` keyword:
  - Guarantees happens-before for reads/writes.
  - Does **not** guarantee atomicity of compound operations.
- Reentrant nature of intrinsic locks.
- `wait()` / `notify()` / `notifyAll()` — classic producer-consumer.
- Why double-checked locking was broken before Java 5 and how `volatile` fixed it.

### 3. java.util.concurrent Locks *(3 h)*

| Lock | Use Case |
|---|---|
| `ReentrantLock` | Advanced locking with `tryLock`, timeouts, fairness, `Condition` |
| `ReadWriteLock` | Many readers, few writers |
| `StampedLock` | Optimistic reads, high-throughput read scenarios |

- `Condition` objects: `await()`, `signal()`, `signalAll()`.
- `tryLock(timeout)` for deadlock avoidance.
- Lock fairness and its performance cost.
- When to use `Lock` over `synchronized`.

### 4. Thread Safety *(2 h)*

- **Immutability** — the simplest form of thread safety.
  - Immutable classes: all fields `final`, no setters, defensive copies.
  - Effectively immutable objects + safe publication.
- **Confinement** — stack confinement, `ThreadLocal` confinement.
- **Safe publication** patterns:
  - Static initializer, `volatile` field, `AtomicReference`, synchronized access.
- The `@ThreadSafe` / `@NotThreadSafe` annotation convention (JSR-305).

### 5. Executor Framework *(4 h)*

- `Executor` → `ExecutorService` → `ScheduledExecutorService` hierarchy.
- `Executors` factory methods and their hidden pitfalls:
  - `newFixedThreadPool` — bounded.
  - `newCachedThreadPool` — unbounded, dangerous for bursty workloads.
  - `newSingleThreadExecutor` — sequential guarantee.
  - `newScheduledThreadPool` — periodic tasks.
  - `newWorkStealingPool` — `ForkJoinPool` under the hood.
- `ThreadPoolExecutor` internals: core size, max size, keep-alive, queue type.
- Pool sizing strategies:
  - CPU-bound: `N_cpu + 1`.
  - I/O-bound: `N_cpu * (1 + W/C)` where W = wait time, C = compute time.
- `Future<V>`, `get()`, `cancel()`, `isDone()`.
- Graceful shutdown: `shutdown()` → `awaitTermination()` → `shutdownNow()`.

### 6. CompletableFuture *(4 h)*

- Creation: `supplyAsync`, `runAsync`, custom executors.
- Chaining: `thenApply`, `thenAccept`, `thenRun`, `thenCompose` (flatMap).
- Combining: `thenCombine`, `allOf`, `anyOf`.
- Exception handling: `exceptionally`, `handle`, `whenComplete`.
- Timeouts: `orTimeout`, `completeOnTimeout` (Java 9+).
- Async vs non-async variants (`thenApplyAsync` vs `thenApply`).
- Patterns:
  - Fan-out / fan-in.
  - Retry with exponential backoff.
  - Circuit breaker sketch.

### 7. Concurrent Collections *(3 h)*

| Collection | Characteristics |
|---|---|
| `ConcurrentHashMap` | Lock-striping, `computeIfAbsent`, bulk ops, never throws CME |
| `CopyOnWriteArrayList` | Snapshot iterator, ideal for read-heavy / write-rare |
| `CopyOnWriteArraySet` | Set variant of the above |
| `LinkedBlockingQueue` | Unbounded (or bounded) FIFO, producer-consumer |
| `ArrayBlockingQueue` | Fixed-capacity, fair ordering option |
| `PriorityBlockingQueue` | Unbounded, natural ordering |
| `ConcurrentLinkedQueue` | Lock-free, CAS-based |
| `SynchronousQueue` | Zero-capacity hand-off |

- Weakly consistent iterators: what that means in practice.
- `Collections.synchronizedXxx` wrappers vs `java.util.concurrent` — why the latter is almost always better.

### 8. Atomic Variables *(2 h)*

- `AtomicInteger`, `AtomicLong`, `AtomicBoolean`, `AtomicReference`.
- **CAS (Compare-And-Swap)** — the hardware primitive behind atomics.
- `compareAndSet`, `getAndUpdate`, `accumulateAndGet`.
- `LongAdder` / `LongAccumulator` — high-contention counters.
- When to choose atomics vs locks.

### 9. ThreadLocal and InheritableThreadLocal *(1 h)*

- Per-thread storage: request context, transactions, user sessions.
- `ThreadLocal.withInitial()`.
- Memory leak risk in thread pools (always `remove()` after use).
- `InheritableThreadLocal` — propagation to child threads.
- Virtual threads and `ScopedValue` (Java 21 preview) as a safer replacement.

### 10. Common Concurrency Problems *(3 h)*

| Problem | Description | Prevention |
|---|---|---|
| **Deadlock** | Two+ threads each waiting for a lock the other holds | Lock ordering, `tryLock`, timeout |
| **Livelock** | Threads keep changing state in response to each other without progressing | Randomized back-off |
| **Starvation** | A thread never gets CPU time | Fair locks, priority tuning |
| **Race condition** | Outcome depends on thread scheduling | Proper synchronization, atomics |

- Detecting deadlocks: `jstack`, `ThreadMXBean.findDeadlockedThreads()`.
- Thread dumps: how to read them.
- Happens-before relationships (JLS §17.4.5).

### 11. Fork/Join Framework *(2 h)*

- `ForkJoinPool`, `RecursiveTask<V>`, `RecursiveAction`.
- Work-stealing: idle threads steal from busy threads' deques.
- Choosing the threshold for splitting work.
- Relationship to parallel streams (`ForkJoinPool.commonPool()`).
- Pitfalls: blocking in Fork/Join, managed blocker.

### 12. Virtual Threads (Java 21) *(3 h)*

- Platform threads vs virtual threads: OS-level vs JVM-scheduled.
- `Thread.ofVirtual().start(runnable)`, `Executors.newVirtualThreadPerTaskExecutor()`.
- **Structured concurrency** (JEP 462, preview):
  - `StructuredTaskScope`, `ShutdownOnFailure`, `ShutdownOnSuccess`.
- **Scoped values** (JEP 464, preview):
  - `ScopedValue` as a safer `ThreadLocal` replacement.
- When virtual threads shine: I/O-bound, high-concurrency servers.
- When they don't: CPU-bound computation, `synchronized` pinning.

---

## References

| Resource | Description |
|---|---|
| *Java Concurrency in Practice* (Goetz et al.) | **THE** book on Java concurrency — read cover to cover |
| [Oracle Concurrency Tutorial](https://docs.oracle.com/javase/tutorial/essential/concurrency/) | Official guide |
| [JEP 444 — Virtual Threads](https://openjdk.org/jeps/444) | Virtual Threads specification |
| [JEP 462 — Structured Concurrency](https://openjdk.org/jeps/462) | Structured concurrency preview |
| [JEP 464 — Scoped Values](https://openjdk.org/jeps/464) | Scoped values preview |
| *Effective Java*, Items 78–84 | Concurrency best practices |

---

## Exercises

### Exercise 1 — Producer-Consumer Order System

**Goal:** Build a multi-threaded order processing pipeline.

**Requirements:**

1. `OrderQueue` backed by a `BlockingQueue<Order>`.
2. N producer threads generate random orders (id, item, quantity, price).
3. M consumer threads process orders: validate → charge → fulfill.
4. Graceful shutdown using the **poison pill** pattern.
5. Metrics: throughput (orders/sec), average processing time.
6. **Bonus:** Implement back-pressure — producers block when the queue exceeds a threshold.

**Starter:** `exercises/src/main/java/exercises/OrderQueue.java`

---

### Exercise 2 — Parallel File Processor

**Goal:** Process thousands of text files in parallel and count word frequencies.

**Requirements:**

1. Read all `.txt` files from a directory.
2. Count word frequencies per file, then merge into a global map.
3. Use `ConcurrentHashMap` with `merge()` for thread-safe aggregation.
4. Benchmark four strategies:
   - Single-threaded.
   - `FixedThreadPool` (N = number of cores).
   - `newWorkStealingPool`.
   - Virtual threads (`newVirtualThreadPerTaskExecutor`).
5. Report: total words, unique words, top-20 most frequent, execution time per strategy.
6. **Bonus:** Progress reporting with `AtomicLong`.

**Starter:** `exercises/src/main/java/exercises/ParallelFileProcessor.java`

---

### Exercise 3 — Thread-Safe Cache

**Goal:** Implement a generic `Cache<K, V>` with TTL-based eviction.

**Requirements:**

1. `get(K key)` — returns `Optional<V>`.
2. `put(K key, V value, Duration ttl)` — stores with expiration.
3. Use `ReadWriteLock`: multiple concurrent readers, exclusive writer.
4. Background cleanup thread removes expired entries periodically.
5. Compare performance with a `ConcurrentHashMap`-based implementation.
6. **Bonus:** Cache statistics — hit rate, miss rate, eviction count.

**Starter:** `exercises/src/main/java/exercises/ThreadSafeCache.java`

---

### Exercise 4 — Async Data Aggregator

**Goal:** Fetch data from multiple simulated services and combine results asynchronously.

**Requirements:**

1. Simulate 5 services with varying response times (100 ms – 3 s).
2. Use `CompletableFuture` to call all services in parallel.
3. Combine results into a single aggregated response.
4. Handle per-service timeouts (2 s) and failures.
5. Implement retry logic (up to 3 attempts) with exponential backoff.
6. **Bonus:** Implement a simple circuit breaker — after 3 consecutive failures, fast-fail for 10 s.

**Starter:** `exercises/src/main/java/exercises/AsyncDataAggregator.java`

---

### Exercise 5 — Dining Philosophers

**Goal:** Understand and solve the classic deadlock problem.

**Requirements:**

1. **Naive implementation:** 5 philosophers, 5 forks — demonstrate deadlock.
2. **Fix 1 — Resource ordering:** Always pick up the lower-numbered fork first.
3. **Fix 2 — tryLock with timeout:** Use `ReentrantLock.tryLock(Duration)`.
4. **Fix 3 — Conductor/Arbitrator:** A central coordinator limits concurrent eaters.
5. Visualize state changes (console output: THINKING / HUNGRY / EATING).
6. **Bonus:** Re-implement with virtual threads.

**Starter:** `exercises/src/main/java/exercises/DiningPhilosophers.java`

---

## Self-Assessment Checklist

Before moving to Phase 06, confirm you can:

- [ ] Explain the difference between `Runnable` and `Callable`.
- [ ] Describe the full thread lifecycle with all six states.
- [ ] Write a producer-consumer using `BlockingQueue` without `wait/notify`.
- [ ] Explain what `volatile` guarantees and what it does **not**.
- [ ] Choose between `synchronized`, `ReentrantLock`, and `StampedLock` for a given scenario.
- [ ] Configure a `ThreadPoolExecutor` with appropriate core/max/queue settings.
- [ ] Compose a multi-step async pipeline with `CompletableFuture` including error handling.
- [ ] Use `ConcurrentHashMap.merge()` for safe concurrent aggregation.
- [ ] Detect a deadlock in a thread dump.
- [ ] Explain CAS and when `AtomicInteger` suffices vs when you need a `Lock`.
- [ ] Describe the Fork/Join work-stealing algorithm.
- [ ] Create virtual threads and explain when they outperform platform threads.
- [ ] Complete all five exercises with passing tests and no data races.

---

*Next → [Phase 06 — JVM Internals & Performance](../phase-06-jvm-internals/README.md)*
