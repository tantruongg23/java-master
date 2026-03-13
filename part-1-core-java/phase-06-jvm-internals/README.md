# Phase 06 — JVM Internals & Performance

**Duration:** ~2–3 weeks · **Estimated effort:** 22 hours  
**Prerequisites:** Phase 05 (Concurrency & Multithreading)

> Understanding the JVM is what separates a *Java developer* from a
> *Java engineer*. When production is burning at 3 AM, knowing how memory
> allocation, garbage collection, and JIT compilation work is the difference
> between resolving in minutes and flailing for hours.

---

## Learning Objectives

By the end of this phase you will be able to:

1. Describe the JVM class-loading mechanism (bootstrap → extension → application).
2. Draw the JVM memory layout (heap, stack, metaspace, direct memory) and explain each region.
3. Explain the generational hypothesis and how it drives GC design.
4. Choose and tune a GC algorithm (G1, ZGC, Shenandoah) for a given workload.
5. Understand JIT compilation, tiered compilation, and key optimisations (inlining, escape analysis).
6. Use essential JVM flags for heap sizing, GC selection, and diagnostics.
7. Profile applications with JVisualVM, JFR, async-profiler, and JMC.
8. Detect and fix memory leaks using heap dumps and MAT.
9. Write correct JMH benchmarks and interpret results.

---

## Topics

### 1. JVM Architecture & Class Loading *(2 h)*

**Class loaders:**

| Loader | Responsibility |
|---|---|
| Bootstrap | Core JDK classes (`java.lang`, `java.util`) — native code |
| Platform (Extension) | Extension modules (`java.sql`, `java.xml`) |
| Application (System) | Classpath / modulepath classes |

**Linking:**
1. **Verify** — bytecode structural integrity.
2. **Prepare** — allocate memory for static fields, set default values.
3. **Resolve** — symbolic references → direct references (can be lazy).

**Initialization:**
- `<clinit>` method — static initializers and static field assignments.
- Happens at most once; JVM guarantees thread safety.

**Delegation model:** parent-first (child asks parent before loading itself).

### 2. JVM Memory Model *(3 h)*

```
┌───────────────────────────────────────────┐
│                  JVM Process              │
├─────────────────────┬─────────────────────┤
│        Heap         │     Non-Heap        │
│  ┌───────────────┐  │  ┌───────────────┐  │
│  │  Young Gen    │  │  │  Metaspace    │  │
│  │  ┌─────────┐  │  │  │  (class meta) │  │
│  │  │  Eden   │  │  │  ├───────────────┤  │
│  │  ├─────────┤  │  │  │  Code Cache   │  │
│  │  │ S0 | S1 │  │  │  │  (JIT code)   │  │
│  │  └─────────┘  │  │  └───────────────┘  │
│  ├───────────────┤  │                     │
│  │  Old Gen      │  │  ┌───────────────┐  │
│  │  (tenured)    │  │  │ Direct Memory │  │
│  └───────────────┘  │  │  (NIO buffers)│  │
│                     │  └───────────────┘  │
├─────────────────────┴─────────────────────┤
│  Thread Stacks (one per thread)           │
│  ┌─────┐ ┌─────┐ ┌─────┐                 │
│  │Frame│ │Frame│ │Frame│  ...             │
│  └─────┘ └─────┘ └─────┘                 │
└───────────────────────────────────────────┘
```

- **Heap** — shared, GC-managed: objects and arrays.
  - Young Generation: Eden + two Survivor spaces (S0, S1).
  - Old Generation (Tenured): long-lived objects.
- **Stack** — per-thread, frames hold local variables, operand stack, return address.
- **Metaspace** — class metadata; grows natively (replaces PermGen since Java 8).
- **Direct memory** — allocated via `ByteBuffer.allocateDirect()`; outside heap, used by NIO.

### 3. Garbage Collection Fundamentals *(3 h)*

- **Mark-Sweep-Compact:** mark live objects from GC roots → sweep dead → compact survivors.
- **Generational hypothesis:** most objects die young.
  - Minor GC (Young Gen) — fast, frequent.
  - Major / Full GC (Old Gen) — slow, infrequent.
- **GC roots:** stack variables, static fields, JNI references, active threads.
- **Reference types:**

| Type | GC Behaviour | Use Case |
|---|---|---|
| Strong | Never collected while reachable | Default |
| Soft (`SoftReference`) | Collected before OOM | Caches |
| Weak (`WeakReference`) | Collected on next GC | Canonicalization maps |
| Phantom (`PhantomReference`) | Enqueued after finalization | Resource cleanup |

- `ReferenceQueue` — notification when references are cleared.

### 4. GC Implementations *(2 h)*

| GC | Key Trait | Best For |
|---|---|---|
| **Serial** | Single-threaded, simple | Small heaps, single-core, client apps |
| **Parallel** (Throughput) | Multi-threaded STW | Batch processing, max throughput |
| **G1** | Region-based, mixed GC, pause target | General purpose (default since Java 9) |
| **ZGC** | Colored pointers, sub-ms pauses, concurrent | Large heaps (multi-GB), low-latency |
| **Shenandoah** | Concurrent compaction, Brooks pointers | Low-latency (RedHat JDK) |

- **G1 deep dive:** regions, humongous objects, remembered sets, mixed GC cycle.
- **ZGC deep dive:** load barriers, colored pointers, multi-mapping.
- Choosing a GC: latency vs throughput vs footprint.

### 5. JIT Compilation *(2 h)*

- **Interpretation → C1 (client) → C2 (server)** — tiered compilation.
- **Method inlining** — the most impactful optimization; inline cache, megamorphic calls.
- **Escape analysis** — if an object doesn't escape a method, allocate on stack (scalar replacement).
- **On-Stack Replacement (OSR)** — compile a hot loop while it's running.
- **Deoptimization** — when assumptions break (uncommon traps).
- `-XX:+PrintCompilation`, `-XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining`.

### 6. Essential JVM Flags *(2 h)*

| Flag | Purpose |
|---|---|
| `-Xms` / `-Xmx` | Initial / maximum heap size |
| `-Xss` | Thread stack size |
| `-XX:+UseG1GC` | Select G1 collector |
| `-XX:+UseZGC` | Select ZGC |
| `-XX:MaxGCPauseMillis=200` | G1 pause target |
| `-XX:+PrintGCDetails` | GC logging (pre-Java 9) |
| `-Xlog:gc*` | Unified GC logging (Java 9+) |
| `-XX:MaxMetaspaceSize` | Cap metaspace growth |
| `-XX:+HeapDumpOnOutOfMemoryError` | Auto-dump on OOM |
| `-XX:HeapDumpPath=/path` | Dump location |
| `-XX:+FlightRecorder` | Enable JFR |
| `-XX:NativeMemoryTracking=summary` | Track native memory |

### 7. Profiling Tools *(3 h)*

| Tool | Strengths |
|---|---|
| **JVisualVM** | CPU, memory, threads, heap dump, visual GC plugin |
| **Java Flight Recorder (JFR)** | Low-overhead, always-on, rich events |
| **JDK Mission Control (JMC)** | JFR analysis GUI, flame graphs |
| **async-profiler** | Accurate CPU profiling (no safepoint bias), allocation profiling |

- Starting JFR: `jcmd <pid> JFR.start duration=60s filename=recording.jfr`.
- Key JFR events: `jdk.GarbageCollection`, `jdk.ObjectAllocationSample`, `jdk.ThreadSleep`.
- Flame graph interpretation: wide bars = hot code.

### 8. Memory Leak Detection *(3 h)*

- **Taking a heap dump:**
  - `jcmd <pid> GC.heap_dump /tmp/dump.hprof`
  - `-XX:+HeapDumpOnOutOfMemoryError`
- **Eclipse MAT (Memory Analyzer Tool):**
  - Dominator tree — who retains the most memory.
  - Shortest path to GC root — why is this object alive?
  - Leak suspects report.
- **Common leak patterns:**
  1. Static collections that grow unboundedly.
  2. Listener/callback registration without removal.
  3. Inner class holding implicit reference to outer class.
  4. `ThreadLocal` not cleaned up in thread pools.
  5. Classloader leaks (webapp redeploy in servlet containers).
  6. Unclosed resources (streams, connections).

### 9. Benchmarking with JMH *(2 h)*

- **Why microbenchmarks are hard:** JIT, dead-code elimination, constant folding, loop unrolling.
- **JMH** handles warmup, measurement iterations, fork isolation, blackhole consumption.
- Key annotations: `@Benchmark`, `@Warmup`, `@Measurement`, `@Fork`, `@State`, `@Param`.
- `@BenchmarkMode`: `Throughput`, `AverageTime`, `SampleTime`.
- Running: `mvn clean install && java -jar target/benchmarks.jar`.

---

## References

| Resource | Description |
|---|---|
| *Java Performance* (Scott Oaks, 2nd ed.) | Comprehensive JVM performance guide |
| [JVM Specification](https://docs.oracle.com/javase/specs/jvms/se21/html/) | Authoritative JVM spec |
| [JEP 376 — ZGC: Concurrent Thread-Stack Processing](https://openjdk.org/jeps/376) | ZGC details |
| [JMH Documentation](https://github.com/openjdk/jmh) | Benchmark harness |
| [Eclipse MAT](https://eclipse.dev/mat/) | Heap analysis tool |
| [async-profiler](https://github.com/async-profiler/async-profiler) | Low-overhead profiler |

---

## Exercises

### Exercise 1 — Memory Leak Lab

**Goal:** Intentionally create memory leaks, detect them with profiling tools, then fix them.

**Requirements:**

1. Create four leak scenarios:
   - **Static collection leak** — objects added to a static `List` but never removed.
   - **Listener leak** — event listeners registered but never unregistered.
   - **Inner class leak** — anonymous inner class holding outer class reference.
   - **ThreadLocal leak** — value set in a thread-pool thread but never removed.
2. Run the application and observe heap growth with JVisualVM or JFR.
3. Take a heap dump, analyze with MAT, identify the retaining path.
4. Fix each leak and verify heap stabilizes.
5. Document each pattern: cause, detection method, fix, prevention.

**Starter:** `exercises/src/main/java/exercises/MemoryLeakLab.java`

---

### Exercise 2 — GC Tuning Exercise

**Goal:** Observe how different GC algorithms behave under various allocation patterns.

**Requirements:**

1. Create allocation patterns:
   - **Short-lived burst** — millions of small objects created and discarded.
   - **Long-lived cache** — growing collection of retained objects.
   - **Large arrays** — allocations that go directly to Old Gen / humongous regions.
2. Run with: Serial (`-XX:+UseSerialGC`), G1 (`-XX:+UseG1GC`), ZGC (`-XX:+UseZGC`).
3. Enable GC logging (`-Xlog:gc*`).
4. Tune G1: `-XX:MaxGCPauseMillis`, `-XX:G1HeapRegionSize`, `-XX:InitiatingHeapOccupancyPercent`.
5. Compare: pause times, throughput, heap usage.
6. Write up findings in a results document.

**Starter:** `exercises/src/main/java/exercises/GCTuningExercise.java`

---

### Exercise 3 — JMH Benchmarking Suite

**Goal:** Benchmark common Java operations and learn to interpret JMH results.

**Requirements:**

1. Benchmark the following comparisons:
   - `ArrayList` vs `LinkedList` iteration.
   - `HashMap` vs `TreeMap` random lookup.
   - `String` concatenation (`+`) vs `StringBuilder`.
   - `Stream` pipeline vs traditional `for` loop.
   - `synchronized` block vs `ReentrantLock` vs `AtomicInteger` increment.
2. Proper JMH setup: warmup (5 iterations), measurement (10 iterations), 3 forks.
3. Use `@State(Scope.Benchmark)` for shared state.
4. Use `Blackhole.consume()` to prevent dead-code elimination.
5. **Bonus:** Benchmark virtual threads vs platform threads for I/O-bound work.

**Starter:** `exercises/src/main/java/exercises/BenchmarkSuite.java`

---

### Exercise 4 — Custom ClassLoader (Plugin System)

**Goal:** Implement a classloader that loads classes from a custom directory, simulating a plugin system.

**Requirements:**

1. Extend `ClassLoader` and override `findClass(String name)`.
2. Read `.class` files from a configurable directory.
3. Demonstrate classloader isolation: two plugins with the same class name but different implementations.
4. Show how classloader leaks happen (strong reference to loaded classes prevents GC of the classloader).
5. **Bonus:** Hot-reload simulation — unload plugin, reload updated version.

**Starter:** `exercises/src/main/java/exercises/PluginClassLoader.java`

---

## Self-Assessment Checklist

Before moving to Phase 07, confirm you can:

- [ ] Draw the JVM memory layout from memory and label every region.
- [ ] Explain bootstrap → platform → application classloader delegation.
- [ ] Describe the generational hypothesis in one sentence.
- [ ] List four GC roots.
- [ ] Explain the difference between `SoftReference` and `WeakReference`.
- [ ] Choose between G1 and ZGC for a given latency/throughput requirement.
- [ ] Read a GC log and identify full GC events, pause times, and promotion failures.
- [ ] Explain what escape analysis enables and give an example.
- [ ] Take a heap dump, open it in MAT, and find the biggest retainer.
- [ ] Identify at least three common memory leak patterns.
- [ ] Write a correct JMH benchmark with proper warmup and blackhole usage.
- [ ] Implement a custom classloader and explain parent delegation.
- [ ] Complete all four exercises.

---

*Next → [Phase 07 — Design Patterns](../phase-07-design-patterns/README.md)*
