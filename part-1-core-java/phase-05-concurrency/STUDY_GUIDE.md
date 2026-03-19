# Study Guide - Phase 05: Concurrency & Multithreading

> Estimated Duration: ~3-4 weeks (~32 hours)
> Prerequisites: Phase 04 (Exception Handling, I/O, NIO)
> Philosophy: Concurrency bugs are hardest to reproduce and hardest to debug. Master the mental models first, then build with the right Java concurrency primitives.

---

## Table of Contents
1. [Why Concurrency and Parallel Matter](#why-concurrency-and-parallel-matter)
2. [How to Study This Phase](#how-to-study-this-phase-efficient-and-powerful)
3. [Core Mental Models (Deep Dive)](#core-mental-models-deep-dive)
4. [Part I - Threads and the Java Memory Model](#part-i---threads-and-the-java-memory-model)
5. [Part II - Locking, Atomicity, and Thread-Safe Design](#part-ii---locking-atomicity-and-thread-safe-design)
6. [Part III - Parallel Execution Frameworks](#part-iii---parallel-execution-frameworks)
7. [Part IV - Async Pipelines with CompletableFuture](#part-iv---async-pipelines-with-completablefuture)
8. [Part V - Concurrent Collections](#part-v---concurrent-collections-choose-by-access-pattern)
9. [Part VI - Common Concurrency Problems](#part-vi---common-concurrency-problems-and-how-to-fix-them)
10. [Part VII - Virtual Threads and Structured Concurrency](#part-vii---virtual-threads-and-structured-concurrency-java-21)
11. [Appendix - Deep Code Examples](#appendix---deep-code-examples-by-learning-objective)
12. [Exercises Roadmap](#exercises-roadmap-aligned-to-your-repo)
13. [Common Mistakes to Avoid](#common-mistakes-to-avoid)
14. [Key Terms Glossary](#key-terms-glossary)
15. [Progress Tracker](#progress-tracker)
16. [What's Next](#whats-next)

---

## Why Concurrency and Parallel Matter

Modern Java systems rarely do one thing at a time. Understanding concurrency is not optional — it is a **core competency** for any backend or systems developer.

### Real-World Scenarios

| Scenario | Why Concurrency is Needed |
|---|---|
| Web server handling 1000 requests/sec | Each request = a task; you cannot process them one-by-one sequentially |
| Database query + HTTP call + cache lookup | I/O operations block; while waiting for DB, CPU could be doing useful work |
| Data pipeline processing 10GB CSV | Split work across cores to finish in minutes instead of hours |
| Real-time chat application | Incoming/outgoing messages must be handled simultaneously for many users |

### Concurrency vs Parallelism — The Precise Distinction

**Concurrency** = multiple tasks make progress by **interleaving** execution over time. They may or may not run at the same physical instant. Think of a single chef cooking 3 dishes — switching between them, but only one hand active at a time.

```
Time →
Thread-1: [Task A]          [Task A continued]
Thread-2:         [Task B]                    [Task B continued]
             ↑ interleaved on 1 core (concurrent but NOT parallel)
```

**Parallelism** = multiple tasks run at the **same physical instant** on multiple CPU cores. Think of 3 chefs each cooking a different dish simultaneously.

```
Time →
Core-1: [Task A] [Task A] [Task A]
Core-2: [Task B] [Task B] [Task B]
Core-3: [Task C] [Task C] [Task C]
             ↑ truly simultaneous (parallel)
```

**Key insight**: Parallelism is a subset of concurrency. All parallel execution is concurrent, but not all concurrent execution is parallel. On a single-core machine, you can have concurrency (via time-slicing) but never true parallelism.

### Why It Is Essential — With Concrete Impact

| Concern | Without Concurrency | With Concurrency |
|---|---|---|
| **Performance** | HTTP request waits 200ms for DB, then 150ms for external API = 350ms total | DB + API run in parallel = ~200ms total (max of the two) |
| **Scalability** | Server handles 1 request at a time = max 3 req/sec | Thread pool handles 200 concurrent requests = thousands req/sec |
| **Reliability** | One slow operation blocks everything | Tasks isolated; one failure doesn't stall others |
| **Cost** | Single-core utilization on 16-core machine = paying for 15 idle cores | Proper parallelism uses 80-100% of available cores |

**The most expensive production failures are concurrency bugs** — they are intermittent, hard to reproduce, and often only manifest under load.

---

## How to Study This Phase (Efficient and Powerful)

### The 5-Step Mastery Loop
For every subtopic, follow this loop:

1. **Learn the rule** (1-2 pages of explanation — this guide provides that).
2. **Write the smallest correct program** that demonstrates it.
3. **Break it intentionally** (remove a happens-before edge, remove a lock, swap lock order).
4. **Observe the failure** (wrong output, hangs, exceptions, thread dump clues).
5. **Fix it and explain why** the fix re-established correctness.

### Practical Studying Rules

- **Draw a happens-before story before writing code.** If you cannot state "Thread A's write to field X is visible to Thread B because of [this happens-before edge]," you are not ready to code.
- **Prefer Java's built-in primitives** (`Executor`, `ConcurrentHashMap`, `CompletableFuture`, `BlockingQueue`) over manual thread management. These encode correctness rules.
- **Optimize only after correctness.** Contention and blocking are the real performance killers — but incorrect concurrent code is infinitely worse than slow code.

---

## Core Mental Models (Deep Dive)

These four mental models are the **foundation of everything** in this phase. Every concept, every API, every exercise connects back to one or more of them.

### Mental Model 1: Data Race = Correctness Bug

**Definition**: A data race occurs when:
1. Two or more threads access the **same mutable data**,
2. At least one of those accesses is a **write**, and
3. There is **no happens-before relationship** ordering those accesses.

**Why it is a correctness bug, not just a performance issue**:
A data race means the program's behavior is **undefined** — the JVM specification makes no guarantees about what values you will see. You might get:
- The old value (stale read)
- The new value (lucky timing)
- A **torn read** — for `long` and `double` (64-bit), you might see *half* of the old value and *half* of the new value because JVM is allowed to split 64-bit writes into two 32-bit operations on some platforms

**Concrete Example — A Data Race in Action**:
```java
class UnsafeCounter {
    private int count = 0; // shared mutable state, NO synchronization

    void increment() {
        count++; // READ count → ADD 1 → WRITE count (3 steps, not atomic!)
    }

    int getCount() {
        return count; // may see stale value
    }
}

// Two threads each call increment() 1,000,000 times
// Expected: count = 2,000,000
// Actual: some number < 2,000,000 (e.g., 1,543,217)
// WHY: Thread A reads count=5, Thread B reads count=5,
//      Thread A writes 6, Thread B writes 6. One increment is LOST.
```

**The "lost update" problem step-by-step**:
```
Thread A                    Thread B
─────────                   ─────────
read count → 5
                            read count → 5
compute 5+1 = 6
                            compute 5+1 = 6
write count ← 6
                            write count ← 6   ← LOST UPDATE! Should be 7
```

**How to fix**: Establish a happens-before relationship (see next mental model).

---

### Mental Model 2: Happens-Before Edges Create Visibility

**Definition**: The **happens-before** relationship is the Java Memory Model's formal guarantee that one action's effects are **visible** to another action. If action A *happens-before* action B, then:
- Everything A wrote to memory is **guaranteed visible** to B.
- A's effects are **ordered** before B's effects.

**The Java Memory Model (JMM) — Why It Exists**:
Modern CPUs and compilers re-order instructions for performance. The JVM is allowed to:
- Cache variable values in CPU registers (so different threads see different values)
- Re-order instructions that appear independent
- Buffer writes so they don't immediately reach main memory

The JMM defines **when** these optimizations are NOT allowed — specifically, across happens-before edges.

**Complete List of Happens-Before Rules** (JLS §17.4.5):

| Rule | What Happens-Before What | Practical Meaning |
|---|---|---|
| **Program order** | Each action in a thread HB the next action in that thread | Within a single thread, everything is ordered as written |
| **Monitor lock** | An `unlock` on monitor M HB the next `lock` on M | Exiting `synchronized` flushes writes; entering `synchronized` reads fresh values |
| **Volatile variable** | A write to volatile field HB the next read of that field | `volatile` guarantees visibility between writer and reader threads |
| **Thread start** | `thread.start()` HB any action in the started thread | The new thread sees everything the parent thread wrote before `start()` |
| **Thread join** | All actions in thread T HB `T.join()` returns | After `join()`, you see everything thread T wrote |
| **Transitivity** | If A HB B and B HB C, then A HB C | Chains of happens-before compose |
| **Interruption** | A call to `interrupt()` HB detection of the interrupt | The interrupted thread sees writes made before the `interrupt()` call |
| **Finalizer** | End of constructor HB start of `finalize()` | Finalizer sees constructed object |
| **Default value** | Writing the default value (0, null, false) HB the first action in any thread | Every thread sees default-initialized fields |

**Visual Example — volatile establishing happens-before**:
```java
class VisibilityDemo {
    private int data = 0;           // non-volatile
    private volatile boolean ready = false; // volatile = happens-before edge

    // Thread A (Writer)
    void writer() {
        data = 42;        // Step 1: write to non-volatile
        ready = true;     // Step 2: write to volatile (FLUSH everything before this)
    }

    // Thread B (Reader)
    void reader() {
        if (ready) {      // Step 3: read volatile (REFRESH everything)
            // Step 4: read data — GUARANTEED to see 42
            // Because: Step 2 happens-before Step 3 (volatile rule)
            //          Step 1 happens-before Step 2 (program order)
            //          Therefore: Step 1 happens-before Step 3 (transitivity)
            System.out.println(data); // Always prints 42 when ready is true
        }
    }
}
```

**Without `volatile` on `ready`**: Thread B might see `ready = true` but `data = 0` (!!) because the JVM is allowed to reorder the writes in Thread A or cache stale `data` in Thread B's CPU register.

---

### Mental Model 3: Liveness Matters

**Definition**: A concurrent program has a **liveness** property if every thread that is supposed to make progress **eventually** does so.

Liveness failures come in three forms:

#### 3a. Deadlock — Permanent Waiting

**What**: Two or more threads are **permanently blocked**, each waiting for a resource the other holds.

**The Four Necessary Conditions** (Coffman conditions — ALL four must be present):
1. **Mutual exclusion** — at least one resource is held in a non-sharable mode
2. **Hold and wait** — a thread holds one resource while waiting for another
3. **No preemption** — resources cannot be forcibly taken away
4. **Circular wait** — Thread A waits for Thread B, which waits for Thread A

**Example**:
```java
// Thread 1                    // Thread 2
synchronized(lockA) {          synchronized(lockB) {
    // holds lockA               // holds lockB
    synchronized(lockB) {        synchronized(lockA) {
        // DEADLOCK!               // DEADLOCK!
    }                            }
}                              }
```

**Fix strategies** (each breaks one Coffman condition):
| Strategy | Breaks Which Condition | How |
|---|---|---|
| Lock ordering | Circular wait | Always acquire locks in the same global order (lockA before lockB) |
| `tryLock` with timeout | Hold and wait | If you can't get the second lock, release the first and retry |
| Arbitrator/semaphore | Hold and wait | Ask a central coordinator for all needed resources at once |

#### 3b. Livelock — Active but No Progress

**What**: Threads are **not blocked** — they are actively running — but they keep **undoing each other's work** or **retrying** in a way that prevents any forward progress.

**Real-world analogy**: Two people meet in a narrow hallway. Both step left to let the other pass. Then both step right. Then both step left again. They're moving but never passing.

**Code example**:
```java
// Thread A: if resource is locked by B, release my resource, wait, retry
// Thread B: if resource is locked by A, release my resource, wait, retry
// Both keep releasing and retrying in lockstep → no progress
```

**Fix**: Add **randomized backoff** — each thread waits a random amount of time before retrying, breaking the lockstep pattern.

#### 3c. Starvation — Unfairly Denied Resources

**What**: A thread is **never** (or rarely) given the opportunity to run because other threads keep acquiring the resources it needs.

**Example**: Non-fair `ReentrantLock` — if many threads compete, a particular thread might never win the lock. With fair locks, longest-waiting thread gets priority, but fairness has a performance cost (~2x slower due to thread parking/unparking overhead).

**Fix**: Use fair locks when starvation is a concern, or design systems where no single resource is a bottleneck for all threads.

---

### Mental Model 4: Threads Are Not Your Abstraction

**The principle**: You should think in terms of **tasks** + **execution strategies**, not raw threads.

**Why raw threads are problematic**:
| Problem | Description |
|---|---|
| Resource cost | Each platform thread costs ~1MB of stack memory + OS scheduling overhead |
| Lifecycle management | You must handle thread creation, naming, daemon status, exception handling, shutdown |
| Scalability limits | Creating 10,000 threads = 10GB of stack memory + OS thread limit |
| Error propagation | No built-in way to propagate exceptions from worker threads to the caller |

**The modern approach**: Submit **tasks** (Runnable/Callable) to an **Executor** and let the framework manage threads.

```java
// ❌ BAD: managing threads directly
Thread t = new Thread(() -> processOrder(order));
t.start();
// How do you get the result? How do you handle errors? How do you limit concurrency?

// ✅ GOOD: submit tasks to an executor
ExecutorService executor = Executors.newFixedThreadPool(16);
Future<Result> future = executor.submit(() -> processOrder(order));
Result result = future.get(); // blocks until done, propagates exceptions
```

---

## Part I - Threads and the Java Memory Model

### Thread Fundamentals

#### Thread Lifecycle — Every State Explained

A Java thread exists in exactly one of these states at any time (defined in `java.lang.Thread.State`):

```
                    ┌─────────────────────────────────────────┐
                    │                                         │
    ┌────────┐  start()  ┌──────────┐                  ┌─────────────┐
    │  NEW   │──────────→│ RUNNABLE │─────────────────→│ TERMINATED  │
    └────────┘           └──────────┘                  └─────────────┘
                              │  ↑                          ↑
                              │  │                          │
                  ┌───────────┘  └──────────┐               │
                  ↓                         │               │
           ┌──────────┐  ┌──────────────────────┐          │
           │ BLOCKED  │  │ WAITING/TIMED_WAITING │──────────┘
           └──────────┘  └──────────────────────┘
```

| State | What It Means | How You Get Here | How You Leave | Example |
|---|---|---|---|---|
| **NEW** | Thread object created but `start()` not yet called. The thread does **not** exist at the OS level yet. | `new Thread(runnable)` | Call `thread.start()` | `Thread t = new Thread(() -> work()); // t is NEW` |
| **RUNNABLE** | Thread is **eligible to run**. It may be actually executing on a CPU core, or it may be waiting for the OS scheduler to give it a CPU time slice. Java does not distinguish between "running" and "ready to run." | `start()` called, or returned from BLOCKED/WAITING | OS scheduler gives it CPU time; or transition to BLOCKED/WAITING/TERMINATED | Thread is executing your `run()` method |
| **BLOCKED** | Thread is waiting to **acquire an intrinsic monitor lock** (`synchronized`). It cannot proceed until another thread releases that lock. | Trying to enter a `synchronized` block whose monitor is held by another thread | The other thread exits the `synchronized` block → this thread acquires the lock → RUNNABLE | Thread tries to enter `synchronized(sharedLock)` but another thread holds `sharedLock` |
| **WAITING** | Thread is waiting **indefinitely** for another thread to perform a specific action. It will NOT resume on its own — something external must wake it. | `Object.wait()` (no timeout), `Thread.join()` (no timeout), `LockSupport.park()` | `notify()`/`notifyAll()` called, joined thread finishes, `unpark()` called | `lock.wait()` — waits until someone calls `lock.notify()` |
| **TIMED_WAITING** | Like WAITING, but with a **time limit**. Thread will auto-resume after the timeout. | `Thread.sleep(ms)`, `Object.wait(ms)`, `Thread.join(ms)`, `LockSupport.parkNanos()` | Timeout expires, or `notify()`/`interrupt()` called | `Thread.sleep(1000)` — pauses for 1 second then back to RUNNABLE |
| **TERMINATED** | Thread has finished execution. Either `run()` completed normally, or an uncaught exception killed it. Thread object still exists in memory but cannot be restarted. | `run()` returns normally, or uncaught exception | Cannot leave — terminal state | Thread finished processing all items |

**Critical distinctions**:
- **BLOCKED vs WAITING**: BLOCKED = waiting for a **monitor lock**. WAITING = waiting for a **signal/notification**. Different mechanisms, different thread dump appearances.
- **You cannot call `start()` twice**: Once TERMINATED, calling `start()` throws `IllegalThreadStateException`.

**Inspecting thread state in code**:
```java
Thread t = new Thread(() -> {
    try { Thread.sleep(5000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
});
System.out.println(t.getState()); // NEW
t.start();
System.out.println(t.getState()); // RUNNABLE (or TIMED_WAITING if sleep started)
Thread.sleep(100); // give thread time to enter sleep
System.out.println(t.getState()); // TIMED_WAITING
t.join();
System.out.println(t.getState()); // TERMINATED
```

---

#### How `join()` Works — In Detail

`join()` makes the **calling thread** wait until the **target thread** finishes (reaches TERMINATED state).

```java
Thread worker = new Thread(() -> {
    // long computation
    computeResult();
});
worker.start();

// Main thread blocks here until 'worker' finishes
worker.join(); // main thread goes to WAITING state
// After this line, everything 'worker' wrote is VISIBLE to main thread (happens-before!)
```

**Overloaded forms**:
- `join()` — wait forever (until target thread terminates)
- `join(long millis)` — wait up to `millis` milliseconds
- `join(long millis, int nanos)` — wait with nanosecond precision

**Happens-before guarantee**: All writes in the joined thread are visible to the caller after `join()` returns. This is a formal JMM happens-before edge.

---

#### How `interrupt()` Works — The Cooperative Mechanism

`interrupt()` does **NOT** forcibly stop a thread. It sets an **interrupt flag** and may cause blocking methods to throw `InterruptedException`.

**The interrupt mechanism step-by-step**:
1. Thread A calls `threadB.interrupt()` — this sets Thread B's interrupt flag to `true`.
2. If Thread B is currently in a blocking call (`sleep`, `wait`, `join`, `BlockingQueue.take()`), it throws `InterruptedException` and **clears** the interrupt flag.
3. If Thread B is running normally, nothing happens immediately — Thread B must **check** the flag with `Thread.currentThread().isInterrupted()`.

```java
class InterruptDemo implements Runnable {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // This is a cancellation point — if interrupted, throws exception
                Thread.sleep(100);
                doWork();
            } catch (InterruptedException e) {
                // sleep() threw because we were interrupted
                // The interrupt flag is now CLEARED — we must re-set it or exit
                Thread.currentThread().interrupt(); // re-set the flag (best practice)
                break; // or: return; — stop the loop
            }
        }
        System.out.println("Thread cleanly stopped");
    }
}
```

**Critical rule — never swallow InterruptedException**:
```java
// ❌ BAD — swallows the interrupt, caller never knows thread was interrupted
catch (InterruptedException e) {
    // do nothing
}

// ✅ GOOD — either re-throw or re-set the interrupt flag
catch (InterruptedException e) {
    Thread.currentThread().interrupt(); // preserve the interrupt signal
}
```

---

#### Why Daemon Threads Are Dangerous

**Daemon thread** = a background thread that does NOT prevent JVM shutdown. When all non-daemon threads finish, the JVM exits, killing all daemon threads **without running finally blocks**.

```java
Thread daemon = new Thread(() -> {
    try {
        while (true) {
            writeToFile(data); // might be interrupted mid-write!
        }
    } finally {
        closeFile(); // MAY NEVER EXECUTE — JVM kills daemon threads abruptly
    }
});
daemon.setDaemon(true); // must set BEFORE start()
daemon.start();
```

**When to use daemon threads**: Background tasks like monitoring, cleanup, or heartbeats that are truly non-essential.
**When NOT to use daemon threads**: Anything involving data integrity (file writes, DB transactions, message acknowledgments).

---

#### Why Thread Naming Matters

When your application has 200 threads and you get a thread dump (because something is stuck), unnamed threads appear as `Thread-0`, `Thread-1`, etc. This is **useless** for debugging.

```java
// ❌ BAD — unnamed threads in thread dumps
new Thread(task).start();

// ✅ GOOD — descriptive names show in thread dumps, logs, and monitoring tools
Thread t = new Thread(task, "order-processor-1");
// or with executors:
ThreadFactory factory = r -> {
    Thread t = new Thread(r);
    t.setName("worker-pool-" + t.getId());
    return t;
};
```

**Thread dump example with named threads**:
```
"order-processor-1" #14 prio=5 os_prio=0 tid=0x00007f BLOCKED
  - waiting to lock <0x000000076b> (a java.lang.Object)
  - which is held by "payment-handler-3" #18
```

Now you can immediately see: the order processor is blocked waiting for a lock held by the payment handler. This is debugging gold.

---

#### Micro-Pattern: Cooperative Cancellation

```java
class Worker implements Runnable {
    private final AtomicBoolean stop = new AtomicBoolean(false);

    @Override
    public void run() {
        while (!stop.get() && !Thread.currentThread().isInterrupted()) {
            try {
                // do small units of work
                processNextItem();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // preserve interrupt
                break;
            }
        }
        cleanup(); // release resources before exiting
    }

    void requestStop() {
        stop.set(true);
        // For immediate responsiveness when thread is blocking:
        // threadRef.interrupt();
    }
}
```

**Why both `stop` flag AND interrupt check?**
- `stop` flag: allows graceful shutdown from your own code
- `interrupt()`: allows the executor/framework to signal cancellation (e.g., during `shutdownNow()`)

---

### Synchronization and Happens-Before

#### `synchronized` (Intrinsic Monitor Lock) — Deep Dive

Every Java object has an **intrinsic lock** (also called a **monitor**). When you use `synchronized`, you acquire this lock.

**What exactly happens**:
1. Thread A enters `synchronized(obj)` → JVM atomically acquires `obj`'s monitor lock
2. If another thread B tries to enter `synchronized(obj)`, it transitions to **BLOCKED** state
3. Thread A exits the `synchronized` block → JVM releases the monitor
4. Thread B acquires the monitor → transitions from BLOCKED to RUNNABLE
5. **All writes** Thread A made inside the block are **guaranteed visible** to Thread B (happens-before edge)

**Two forms of synchronized**:
```java
// Form 1: synchronized method — locks on 'this' (or Class object for static methods)
public synchronized void increment() {
    count++;
}
// Equivalent to: synchronized(this) { count++; }

// Form 2: synchronized block — locks on any object
public void increment() {
    synchronized(lockObject) {
        count++;
    }
}
```

**When to prefer synchronized block over synchronized method**:
- You want to lock on a **specific object** (not `this`)
- You want to minimize the **critical section** (lock only the code that needs it)
- You need to use **different locks** for different state (reduces contention)

```java
class BankAccount {
    private final Object balanceLock = new Object();
    private final Object historyLock = new Object();
    private double balance;
    private final List<String> history = new ArrayList<>();

    void deposit(double amount) {
        synchronized (balanceLock) {
            balance += amount; // only locks balance operations
        }
        synchronized (historyLock) {
            history.add("Deposited " + amount); // separate lock for history
        }
    }
}
```

---

#### `volatile` — Deep Dive

`volatile` provides two guarantees:
1. **Visibility**: Every read of a volatile variable sees the **most recent write** by any thread.
2. **Ordering**: The volatile write happens-before the volatile read (and drags along all preceding writes via transitivity).

**What `volatile` does NOT provide**:
- **Atomicity of compound operations** — `volatile int x; x++;` is NOT atomic

**When to use volatile**:
| Use Case | Why Volatile Works |
|---|---|
| Boolean shutdown flag | Single write, single read — no compound operation |
| Publishing an immutable object reference | Reader sees either old or new fully-constructed object |
| One-way status flags | `volatile State state = RUNNING;` → one thread writes, many read |

**When NOT to use volatile**:
| Use Case | Why Volatile Fails |
|---|---|
| Counter (`x++`) | Read-modify-write — not atomic. Use `AtomicInteger` |
| Multiple related fields | No way to atomically update both `x` and `y`. Use a lock |
| Check-then-act | `if (x == 0) x = 1;` — race between check and act. Use CAS |

---

### wait() / notify() — Producer-Consumer Deep Dive

#### Why wait/notify Exist

`wait()` and `notify()` allow threads to **communicate about conditions**. Instead of busy-waiting (spinning in a loop checking a condition), a thread can **sleep** until another thread signals that the condition may have changed.

**The three rules for correctness**:

1. **Must hold the monitor**: `wait()` and `notify()` must be called inside a `synchronized` block on the same object.
2. **Always wait in a loop**: After `wait()` returns, re-check the condition because of **spurious wakeups** (the JVM may wake you up for no reason) and because another thread may have consumed the resource between `notify()` and your re-acquisition of the lock.
3. **Prefer `notifyAll()` over `notify()`**: `notify()` wakes only one thread — if the wrong thread is woken (one that can't proceed), the system can deadlock. `notifyAll()` wakes all waiters; the correct one will proceed, others will re-enter `wait()`.

```java
class BoundedBuffer<T> {
    private final Queue<T> queue = new ArrayDeque<>();
    private final int capacity;

    BoundedBuffer(int capacity) { this.capacity = capacity; }

    // Producer calls this
    public synchronized void put(T item) throws InterruptedException {
        while (queue.size() == capacity) {  // LOOP, not if!
            wait(); // releases the monitor, thread goes to WAITING state
            // when notified: re-acquires monitor, re-checks condition
        }
        queue.add(item);
        notifyAll(); // wake up consumers that might be waiting on "not empty"
    }

    // Consumer calls this
    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) {  // LOOP, not if!
            wait(); // releases the monitor, thread goes to WAITING state
        }
        T item = queue.remove();
        notifyAll(); // wake up producers that might be waiting on "not full"
        return item;
    }
}
```

**What happens internally during `wait()`**:
1. Thread **releases** the monitor lock (this is what makes wait/notify useful — other threads can acquire the lock)
2. Thread transitions to **WAITING** state
3. When `notifyAll()` is called by another thread, this thread moves to **BLOCKED** (competing to re-acquire the monitor)
4. Once it re-acquires the monitor, `wait()` returns → thread re-checks the condition in the `while` loop

**Why your exercises use `BlockingQueue` instead**: `BlockingQueue` encodes all these rules (wait-in-loop, notify-after-change, bounded capacity) in a production-tested API. Use `wait/notify` only when you need custom condition logic that `BlockingQueue` cannot express.

---

## Part II - Locking, Atomicity, and Thread-Safe Design

### Locks: ReentrantLock, ReadWriteLock, StampedLock

#### ReentrantLock — Deep Dive

`ReentrantLock` provides the same mutual exclusion as `synchronized`, but with additional capabilities:

| Feature | `synchronized` | `ReentrantLock` |
|---|---|---|
| Syntax | Language keyword, auto-release | Explicit `lock()`/`unlock()` — MUST use try/finally |
| Timed lock attempt | Not possible | `tryLock(timeout, unit)` — avoids deadlocks |
| Non-blocking lock attempt | Not possible | `tryLock()` — returns false if lock is held |
| Fairness | Not configurable | `new ReentrantLock(true)` — FIFO ordering |
| Multiple conditions | One wait-set per monitor | `lock.newCondition()` — separate wait-sets |
| Interruptible locking | Not possible | `lockInterruptibly()` — can be interrupted while waiting |

**What "reentrant" means**: The same thread can acquire the lock multiple times. An internal counter tracks the hold count. The lock is released only when the counter reaches zero.

```java
ReentrantLock lock = new ReentrantLock();

void outer() {
    lock.lock(); // hold count = 1
    try {
        inner(); // calls lock.lock() again — hold count = 2
    } finally {
        lock.unlock(); // hold count = 1
    }
}

void inner() {
    lock.lock(); // hold count = 2 (same thread, no blocking)
    try {
        // do work
    } finally {
        lock.unlock(); // hold count = 1
    }
}
```

**Critical pattern — always use try/finally**:
```java
// ❌ DANGEROUS — if doWork() throws, the lock is NEVER released → deadlock
lock.lock();
doWork();
lock.unlock();

// ✅ CORRECT — lock is ALWAYS released regardless of exceptions
lock.lock();
try {
    doWork();
} finally {
    lock.unlock();
}
```

**Condition variables** — separate wait-sets for separate conditions:
```java
class BoundedBuffer<T> {
    private final Queue<T> queue = new ArrayDeque<>();
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition(); // consumer waits here
    private final Condition notFull = lock.newCondition();  // producer waits here

    BoundedBuffer(int capacity) { this.capacity = capacity; }

    void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) {
                notFull.await(); // ONLY producers wait on this condition
            }
            queue.add(item);
            notEmpty.signal(); // wake ONLY consumers (more efficient than notifyAll)
        } finally {
            lock.unlock();
        }
    }

    T take() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                notEmpty.await(); // ONLY consumers wait on this condition
            }
            T item = queue.remove();
            notFull.signal(); // wake ONLY producers
            return item;
        } finally {
            lock.unlock();
        }
    }
}
```

**Why separate `Condition` objects are better than `notifyAll()`**:
With `wait/notifyAll`, ALL waiting threads wake up, check their condition, and most go back to sleep. With separate `Condition` objects, you wake ONLY the threads that might be able to proceed — much more efficient.

---

#### ReadWriteLock — Deep Dive

`ReadWriteLock` maintains **two locks**: a read lock and a write lock.

**Rules**:
- Multiple threads can hold the **read lock simultaneously** (shared access)
- Only ONE thread can hold the **write lock** (exclusive access)
- A thread cannot acquire the write lock while ANY thread holds the read lock
- A thread cannot acquire the read lock while another thread holds the write lock

```
Thread A: [readLock]  [readLock]  [readLock]
Thread B: [readLock]  [readLock]  [readLock]    ← concurrent reads OK
Thread C:                         [writeLock]   ← must wait for all reads to finish
Thread D:                                       [readLock] ← must wait for write to finish
```

**When ReadWriteLock pays off**: Only when reads **significantly** outnumber writes. If writes are frequent, threads spend too much time waiting for exclusive access, and the overhead of managing two locks exceeds the benefit. Rule of thumb: **reads should be >10x writes**.

```java
class ThreadSafeCache<K, V> {
    private final Map<K, V> cache = new HashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    V get(K key) {
        rwLock.readLock().lock();     // multiple threads can read simultaneously
        try {
            return cache.get(key);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    void put(K key, V value) {
        rwLock.writeLock().lock();    // exclusive access — blocks all reads and writes
        try {
            cache.put(key, value);
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}
```

---

#### StampedLock — Deep Dive

`StampedLock` (Java 8+) adds an **optimistic read** mode that doesn't acquire a lock at all — it just checks if a write has happened since you started reading.

**Three modes**:
1. **Write lock** — exclusive, like a write lock
2. **Read lock** — shared, like a read lock (pessimistic)
3. **Optimistic read** — no lock acquired; you validate afterward whether data was modified

```java
class Point {
    private double x, y;
    private final StampedLock lock = new StampedLock();

    void move(double deltaX, double deltaY) {
        long stamp = lock.writeLock();           // exclusive lock
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    double distanceFromOrigin() {
        long stamp = lock.tryOptimisticRead();   // NO lock acquired!
        double currentX = x, currentY = y;       // read values
        if (!lock.validate(stamp)) {             // was there a write since our read?
            // Optimistic read failed — fall back to pessimistic read lock
            stamp = lock.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
}
```

**When to use StampedLock**: Read-heavy scenarios where write contention is **very rare** (e.g., configuration lookups, metadata reads).

**Warning**: `StampedLock` is NOT reentrant — calling `writeLock()` while already holding a write stamp will deadlock.

---

#### Lock Selection Decision Tree

```
Do you need mutual exclusion?
├─ YES → Is it simple synchronization?
│        ├─ YES → Use synchronized (simplest, fewer bugs)
│        └─ NO  → Do you need tryLock, timeouts, or Conditions?
│                  ├─ YES → Use ReentrantLock
│                  └─ NO  → Do reads vastly outnumber writes (>10:1)?
│                           ├─ YES → Are writes extremely rare?
│                           │        ├─ YES → Use StampedLock (optimistic)
│                           │        └─ NO  → Use ReadWriteLock
│                           └─ NO  → Use synchronized or ReentrantLock
└─ NO → Do you need atomic single-value updates?
         ├─ YES → Use AtomicInteger/AtomicLong/AtomicReference
         └─ NO  → Rethink your design (immutability? confinement?)
```

---

### Thread-Safe Classes: Immutability, Confinement, Safe Publication

#### The Three Pillars of Thread Safety

##### Pillar 1: Immutability — The Safest Strategy

**An immutable object is inherently thread-safe** because no thread can modify it. No locks needed, no synchronization bugs possible.

**Recipe for an immutable class**:
1. Declare the class `final` (prevents subclasses from adding mutable state)
2. Make all fields `private final`
3. Don't provide setter methods
4. If fields reference mutable objects, make **defensive copies** in constructor and getters
5. Don't allow `this` to escape during construction

```java
// ✅ IMMUTABLE — inherently thread-safe, zero synchronization needed
public final class Money {
    private final String currency;
    private final long amountInCents;

    public Money(String currency, long amountInCents) {
        this.currency = Objects.requireNonNull(currency);
        this.amountInCents = amountInCents;
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency))
            throw new IllegalArgumentException("Currency mismatch");
        return new Money(currency, this.amountInCents + other.amountInCents);
        // Returns NEW object — never modifies 'this'
    }

    // Getters only — no setters
    public String currency() { return currency; }
    public long amountInCents() { return amountInCents; }
}
```

**Java Records** are great for immutability:
```java
// Record: final class, final fields, no setters, equals/hashCode/toString auto-generated
public record Order(long id, String product, Money price, Instant createdAt) {}
```

##### Pillar 2: Confinement — Keep Data Owned by One Thread

If only one thread can access the data, there's no race condition — no synchronization needed.

**Stack confinement**: Local variables are inherently confined to the executing thread's stack.
```java
void processOrders(List<Order> orders) {
    // 'results' lives on this thread's stack — no other thread can see it
    List<Result> results = new ArrayList<>();
    for (Order o : orders) {
        results.add(process(o)); // safe — only this thread accesses 'results'
    }
    return results;
}
```

**Thread confinement**: Mutable objects are only accessed from a single designated thread (e.g., Swing's EDT, single-threaded event loops).

##### Pillar 3: Safe Publication — Making Objects Visible Correctly

When you create an object in one thread and share it with another, you must ensure the receiving thread sees a **fully constructed** object.

**Unsafe publication** — the receiving thread may see a **partially constructed** object:
```java
// ❌ UNSAFE — Thread B might see holder with a non-null reference
// but the object's fields might not yet be initialized!
class Holder {
    private int value;
    Holder(int value) { this.value = value; }
}
static Holder holder; // not volatile, not synchronized

// Thread A:
holder = new Holder(42);
// Thread B:
if (holder != null) {
    // holder.value might be 0 (!!!) — partially constructed object
}
```

**Safe publication patterns**:
| Pattern | How It Works |
|---|---|
| `volatile` reference | `private static volatile Holder holder;` — volatile write happens-before volatile read |
| `synchronized` block | Store the reference inside a synchronized block |
| `final` fields | If all fields are `final` and no `this` escapes constructor, the object is safely published by construction (JMM guarantee) |
| `AtomicReference` | `AtomicReference<Holder> ref = new AtomicReference<>();` |
| Static initializer | `static Holder holder = new Holder(42);` — class loading is thread-safe |

---

### Atomics and CAS — Deep Dive

#### What CAS (Compare-And-Swap) Actually Does

CAS is a **hardware-level atomic instruction** (on x86: `CMPXCHG`). It does the following atomically:

```
CAS(memory_location, expected_value, new_value):
    if memory_location == expected_value:
        memory_location = new_value
        return true  (SUCCESS)
    else:
        return false (FAILURE — someone else changed it)
```

**The retry pattern** (used by `AtomicInteger.incrementAndGet()`):
```
do {
    current = read value from memory
    next = current + 1
} while (!CAS(memory, current, next))     // retry if someone changed it
```

This is called **lock-free** programming — no locks are held, but correctness is maintained through atomic hardware operations.

#### Atomic Classes — When and How

```java
// AtomicInteger — thread-safe counter
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();                  // atomically: read + add 1 + write
counter.getAndUpdate(curr -> curr * 2);     // atomically apply function
counter.compareAndSet(5, 10);               // if value is 5, set to 10

// AtomicReference — thread-safe reference swap
AtomicReference<Config> configRef = new AtomicReference<>(defaultConfig);
configRef.compareAndSet(oldConfig, newConfig); // atomic reference swap

// AtomicBoolean — thread-safe flag
AtomicBoolean initialized = new AtomicBoolean(false);
if (initialized.compareAndSet(false, true)) {
    // only ONE thread enters here — the one that won the CAS race
    doExpensiveInit();
}
```

#### LongAdder vs AtomicLong — When High Contention Destroys Performance

Under high contention (many threads incrementing simultaneously), `AtomicLong` creates a **hot spot** — every thread CAS-loops on the same memory location, causing cache line bouncing between CPU cores.

`LongAdder` solves this by spreading updates across **multiple cells**:

```
AtomicLong:      All threads → [single counter] ← contention!

LongAdder:       Thread 1 → [cell 0]
                 Thread 2 → [cell 1]     sum() = cell0 + cell1 + cell2 + ...
                 Thread 3 → [cell 2]
```

```java
// Use LongAdder for high-contention counters (metrics, statistics)
LongAdder requestCount = new LongAdder();
requestCount.increment();    // spreads across cells, very fast
long total = requestCount.sum(); // aggregates all cells (slightly slower, but reads are rare)

// Use AtomicLong when you need the exact current value frequently
AtomicLong sequence = new AtomicLong(0);
long next = sequence.incrementAndGet(); // need the exact return value
```

**Decision**: If you only need `increment()` and occasionally `sum()`, use `LongAdder`. If you need `get()` and `compareAndSet()`, use `AtomicLong`.

---

### Thread-Local State and Cleanup

#### What ThreadLocal Does

`ThreadLocal<T>` gives each thread its own **independent copy** of a variable. Threads cannot see or modify each other's copies.

```java
// Each thread gets its own SimpleDateFormat instance (SimpleDateFormat is NOT thread-safe)
private static final ThreadLocal<SimpleDateFormat> dateFormat =
    ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

String format(Date date) {
    return dateFormat.get().format(date); // each thread uses its own formatter
}
```

#### The Thread Pool Memory Leak Problem

In a thread pool, threads are **reused** for multiple tasks. If you set a `ThreadLocal` value in one task, it persists when the thread picks up the next task — potentially leaking data across requests.

```java
// ❌ DANGEROUS with thread pools
ThreadLocal<UserContext> userCtx = new ThreadLocal<>();

void handleRequest(Request req) {
    userCtx.set(new UserContext(req.getUserId())); // set for this request
    processRequest();
    // OOPS: forgot to remove — next request on this thread sees the WRONG user!
}

// ✅ SAFE — always clean up in finally block
void handleRequest(Request req) {
    userCtx.set(new UserContext(req.getUserId()));
    try {
        processRequest();
    } finally {
        userCtx.remove(); // CRITICAL: clean up after every task
    }
}
```

#### ScopedValue (Java 21+ Preview) — The Modern Replacement

`ScopedValue` is designed to replace `ThreadLocal` for passing context through call chains. It is:
- Automatically scoped (value exists only during the scope — no cleanup needed)
- Immutable within a scope (can't be modified, only re-bound in a child scope)
- Compatible with virtual threads (no memory leak concern)

```java
private static final ScopedValue<UserContext> USER = ScopedValue.newInstance();

void handleRequest(Request req) {
    ScopedValue.runWhere(USER, new UserContext(req.getUserId()), () -> {
        processRequest(); // USER.get() returns the bound UserContext
    });
    // After runWhere exits, the value is automatically unbound — no cleanup needed
}
```


---

## Part III - Parallel Execution Frameworks

### Executor Framework and Tuning — Deep Dive

#### The Abstraction Hierarchy

```
┌──────────────────────────────┐
│   ScheduledExecutorService   │  periodic/delayed tasks
├──────────────────────────────┤
│      ExecutorService         │  life-cycle control (shutdown, submit, futures)
├──────────────────────────────┤
│         Executor             │  bare minimum: void execute(Runnable)
└──────────────────────────────┘
```

| Interface | Key Methods | When to Use |
|---|---|---|
| `Executor` | `execute(Runnable)` | Fire-and-forget task submission |
| `ExecutorService` | `submit()`, `invokeAll()`, `shutdown()` | Most common — need Future results and lifecycle |
| `ScheduledExecutorService` | `schedule()`, `scheduleAtFixedRate()` | Periodic tasks, delayed execution |

#### ThreadPoolExecutor — The Real Engine

All `Executors.newXxx()` factory methods create a `ThreadPoolExecutor` (or `ScheduledThreadPoolExecutor`) internally. Understanding its 7 parameters is essential:

```java
ThreadPoolExecutor(
    int corePoolSize,      // threads always kept alive (even if idle)
    int maximumPoolSize,   // max threads when queue is full
    long keepAliveTime,    // how long excess threads (above core) stay alive
    TimeUnit unit,
    BlockingQueue<Runnable> workQueue,  // queues tasks when all core threads are busy
    ThreadFactory threadFactory,        // customize thread names, daemon status
    RejectedExecutionHandler handler   // what happens when queue AND max threads are full
)
```

**Task submission flow**:
```
New task arrives
    │
    ├─ Active threads < corePoolSize?
    │   └─ YES → Create new thread, execute task immediately
    │
    ├─ Work queue has space?
    │   └─ YES → Add task to queue (existing threads will pick it up)
    │
    ├─ Active threads < maximumPoolSize?
    │   └─ YES → Create new thread (temporary, beyond core)
    │
    └─ All full → RejectedExecutionHandler kicks in
```

**Rejection policies**:
| Policy | Behavior | When to Use |
|---|---|---|
| `AbortPolicy` (default) | Throws `RejectedExecutionException` | When you must know about overload |
| `CallerRunsPolicy` | Caller thread executes the task | Back-pressure: slows down producer naturally |
| `DiscardPolicy` | Silently drops the task | Non-critical work (metrics, telemetry) |
| `DiscardOldestPolicy` | Drops oldest queued task, retries new | Latest data is more important than old |

**Production-ready configuration**:
```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    8,                       // core threads (always alive)
    32,                      // max threads (temporary surge capacity)
    60, TimeUnit.SECONDS,    // excess threads die after 60s idle
    new ArrayBlockingQueue<>(10_000),  // BOUNDED queue → prevents OOM
    r -> {
        Thread t = new Thread(r);
        t.setName("worker-" + t.getId());
        t.setDaemon(false);
        t.setUncaughtExceptionHandler((thread, ex) ->
            log.error("Uncaught in " + thread.getName(), ex));
        return t;
    },
    new ThreadPoolExecutor.CallerRunsPolicy()  // back-pressure
);
```

#### Common Pitfalls of `Executors.newXxx()`

| Factory Method | Hidden Danger |
|---|---|
| `newCachedThreadPool()` | **Unbounded thread creation** — under burst load, can create thousands of threads → OOM |
| `newFixedThreadPool(n)` | **Unbounded queue** (`LinkedBlockingQueue`) — tasks pile up forever → OOM |
| `newSingleThreadExecutor()` | **Unbounded queue** — same issue as fixed pool |
| `newScheduledThreadPool(n)` | **Unbounded `DelayedWorkQueue`** — scheduled tasks accumulate → OOM |

**Rule**: In production, always use `ThreadPoolExecutor` with explicit bounds for both threads AND queue.

#### Pool Sizing — The Science

| Workload Type | Optimal Pool Size | Reasoning |
|---|---|---|
| **CPU-bound** | `N_cpu` or `N_cpu + 1` | More threads = more context switching overhead, no benefit. +1 handles page faults |
| **I/O-bound** | `N_cpu × (1 + W/C)` where W=wait time, C=compute time | Threads spend time waiting; more threads keep CPUs busy while others block |
| **Mixed** | Separate pools for CPU and I/O work | Prevents I/O-bound tasks from starving CPU-bound tasks |

**Example**: If tasks spend 80% of time waiting for I/O and 20% computing, on an 8-core machine:
`8 × (1 + 0.8/0.2) = 8 × 5 = 40 threads`

#### Graceful Shutdown Pattern
```java
executor.shutdown();                                    // stop accepting new tasks
try {
    if (!executor.awaitTermination(30, TimeUnit.SECONDS)) { // wait for running tasks
        List<Runnable> dropped = executor.shutdownNow();  // interrupt running tasks
        log.warn("Dropped {} tasks", dropped.size());
        executor.awaitTermination(10, TimeUnit.SECONDS);  // brief wait for interrupts
    }
} catch (InterruptedException e) {
    executor.shutdownNow();
    Thread.currentThread().interrupt();
}
```

---

### Fork/Join Work-Stealing — Deep Dive

#### What Fork/Join Does

Fork/Join is designed for **divide-and-conquer** parallelism:
1. Split a large task into smaller subtasks (`fork`)
2. Compute subtasks in parallel
3. Combine results (`join`)

#### How Work-Stealing Works

Each worker thread has its own **double-ended queue (deque)**. When a thread runs out of work, it **steals** from the tail of another thread's deque.

```
Thread 0's deque:  [Task A1] [Task A2] [Task A3]  ← thread 0 takes from HEAD
                                                    ↑
Thread 1's deque:  [empty]                          │ Thread 1 STEALS from TAIL
                                              steal A3
```

This minimizes contention because:
- The owning thread works from the **head** (LIFO — depth-first, good cache locality)
- Stealing threads take from the **tail** (FIFO — breadth-first, large chunks of work)

#### RecursiveTask Example (with detailed reasoning)

```java
class ParallelSum extends RecursiveTask<Long> {
    private final int[] array;
    private final int start, end;
    private static final int THRESHOLD = 10_000; // tune this!

    ParallelSum(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int size = end - start;
        if (size <= THRESHOLD) {
            // BASE CASE: small enough → compute directly (sequential)
            long sum = 0;
            for (int i = start; i < end; i++) sum += array[i];
            return sum;
        }
        // RECURSIVE CASE: split in half
        int mid = start + size / 2;
        ParallelSum left = new ParallelSum(array, start, mid);
        ParallelSum right = new ParallelSum(array, mid, end);

        left.fork();              // submit left to the deque (another thread may steal it)
        long rightResult = right.compute(); // compute right in THIS thread
        long leftResult = left.join();       // wait for left to complete
        return leftResult + rightResult;
    }
}

// Usage
ForkJoinPool pool = ForkJoinPool.commonPool(); // or new ForkJoinPool(parallelism)
long total = pool.invoke(new ParallelSum(array, 0, array.length));
```

**Threshold tuning**:
- **Too small** (e.g., 10) → task creation overhead dominates
- **Too large** (e.g., 1M) → not enough tasks to distribute across cores
- **Good starting point**: array size ÷ (4 × number of cores)

**Critical rule**: **Never block** inside Fork/Join tasks. I/O, `Thread.sleep`, or lock waiting destroys the work-stealing model because carrier threads are pinned.

---

## Part IV - Async Pipelines with CompletableFuture

### Chaining, Composing, Combining — Complete API Reference

#### Stage Transformation Methods

| Method | Input | Output | Analogy |
|---|---|---|---|
| `thenApply(fn)` | `T → U` | `CompletableFuture<U>` | `map` — transform the value |
| `thenAccept(consumer)` | `T → void` | `CompletableFuture<Void>` | Side effect (logging, sending) |
| `thenRun(runnable)` | `void → void` | `CompletableFuture<Void>` | Execute after completion |
| `thenCompose(fn)` | `T → CF<U>` | `CompletableFuture<U>` | `flatMap` — chain another async operation |
| `thenCombine(other, fn)` | `(T, U) → V` | `CompletableFuture<V>` | Combine two independent results |

#### `thenApply` vs `thenCompose` — The Critical Distinction

```java
// thenApply: SYNCHRONOUS transformation of the result
// fetchUser returns CompletableFuture<User>
CompletableFuture<String> userName =
    fetchUser(userId)
    .thenApply(user -> user.getName()); // User → String (synchronous)

// thenCompose: ASYNCHRONOUS chaining (like flatMap)
// fetchUser returns CF<User>, fetchOrders returns CF<List<Order>>
CompletableFuture<List<Order>> orders =
    fetchUser(userId)
    .thenCompose(user -> fetchOrders(user.getId())); // User → CF<List<Order>>

// ❌ WRONG: using thenApply for async chaining gives CF<CF<List<Order>>>
CompletableFuture<CompletableFuture<List<Order>>> wrong =
    fetchUser(userId)
    .thenApply(user -> fetchOrders(user.getId())); // NESTED futures!
```

#### Fan-Out / Fan-In Pattern

```java
ExecutorService executor = Executors.newFixedThreadPool(8);

// Fan-out: start N independent tasks in parallel
List<CompletableFuture<ServiceResult>> futures = services.stream()
    .map(svc -> CompletableFuture.supplyAsync(
        () -> svc.call(), executor)
        .orTimeout(2, TimeUnit.SECONDS)
        .exceptionally(ex -> new ServiceResult(svc.name(), null, ex)))
    .toList();

// Fan-in: wait for all and collect results
CompletableFuture<Void> allDone = CompletableFuture.allOf(
    futures.toArray(CompletableFuture[]::new));

CompletableFuture<List<ServiceResult>> combined = allDone.thenApply(
    v -> futures.stream()
        .map(CompletableFuture::join)  // safe — all are already complete
        .toList());
```

### Error Handling and Timeouts — Complete Guide

| Method | Signature | When to Use |
|---|---|---|
| `exceptionally(fn)` | `Throwable → T` | Recover from failure with a fallback value |
| `handle(bifn)` | `(T, Throwable) → U` | Always runs; handles both success and failure |
| `whenComplete(biconsumer)` | `(T, Throwable) → void` | Side effects (logging); doesn't change the result |
| `orTimeout(duration)` | — | Fails with `TimeoutException` if not done in time |
| `completeOnTimeout(value, duration)` | — | Returns default value if not done in time |

**Error handling example**:
```java
CompletableFuture<String> result = fetchData()
    .orTimeout(3, TimeUnit.SECONDS)               // fail after 3s
    .handle((data, ex) -> {                        // handle both cases
        if (ex != null) {
            if (ex.getCause() instanceof TimeoutException) {
                return "TIMEOUT_DEFAULT";
            }
            log.error("Fetch failed", ex);
            return "ERROR_DEFAULT";
        }
        return data;
    });
```

**Retry with exponential backoff**:
```java
static <T> CompletableFuture<T> withRetry(
        Supplier<CompletableFuture<T>> task,
        int maxRetries, long initialBackoffMs, Executor executor) {
    return task.get().handle((result, ex) -> {
        if (ex == null) return CompletableFuture.completedFuture(result);
        if (maxRetries <= 0) return CompletableFuture.<T>failedFuture(ex);
        // Wait before retry
        try { Thread.sleep(initialBackoffMs); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.<T>failedFuture(e);
        }
        return withRetry(task, maxRetries - 1, initialBackoffMs * 2, executor);
    }).thenCompose(Function.identity());
}
```

---

## Part V - Concurrent Collections (Choose by Access Pattern)

### Decision Matrix

| Need | Collection | Thread-Safety Mechanism | Performance Profile |
|---|---|---|---|
| Concurrent key-value with many writers | `ConcurrentHashMap` | Fine-grained lock striping (segments) | O(1) get/put; scales linearly with cores |
| Read-heavy list, rare writes | `CopyOnWriteArrayList` | Copy entire array on every write | Reads: O(1), no locking. Writes: O(n), expensive |
| Blocking producer-consumer | `ArrayBlockingQueue` | Single lock + two conditions | Bounded, blocks when full/empty |
| Blocking producer-consumer (linked) | `LinkedBlockingQueue` | Separate put/take locks | Higher throughput than ArrayBQ, optionally bounded |
| High-contention counter | `LongAdder` | Cell striping | Best for increment-only, sum() is eventual |
| Lock-free FIFO queue | `ConcurrentLinkedQueue` | CAS-based linked list | Non-blocking, unbounded |
| Sorted concurrent map | `ConcurrentSkipListMap` | Lock-free skip list | O(log n) operations, sorted key order |
| Unbounded transfer queue | `LinkedTransferQueue` | CAS-based | Direct handoff between producer and consumer |

### ConcurrentHashMap — Deep Dive

**Key operations** you must know:

```java
ConcurrentHashMap<String, LongAdder> wordCounts = new ConcurrentHashMap<>();

// computeIfAbsent: atomically check + create if missing
wordCounts.computeIfAbsent("hello", k -> new LongAdder()).increment();

// merge: atomically combine with existing value
wordCounts.merge("hello", new LongAdder(), (existing, fresh) -> {
    existing.increment();
    return existing;
});

// compute: atomically transform the value
wordCounts.compute("hello", (key, adder) -> {
    if (adder == null) adder = new LongAdder();
    adder.increment();
    return adder;
});
```

**Warning — compound operations are NOT atomic across calls**:
```java
// ❌ NOT ATOMIC — another thread can modify between these two calls
if (!map.containsKey(key)) {
    map.put(key, value);            // CHECK-THEN-ACT race!
}

// ✅ ATOMIC — single call
map.putIfAbsent(key, value);       // atomic check-and-put
```

### Iterator Behavior — Weakly Consistent

Concurrent collections use **weakly consistent** iterators:
- They **never** throw `ConcurrentModificationException`
- They may reflect some, none, or all modifications made after the iterator was created
- They are guaranteed to traverse elements as they existed at some point

```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.put("a", 1);

// Iterator sees a snapshot — concurrent modifications may or may not be visible
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    map.put("b", 2); // may or may not appear in this iteration
}
```

---

## Part VI - Common Concurrency Problems and How to Fix Them

### Problem Classification Quick Reference

| Problem | Symptom | Root Cause | Fix Strategy |
|---|---|---|---|
| **Race condition** | Wrong/inconsistent results | Missing happens-before | Add synchronization (lock, volatile, atomic) |
| **Deadlock** | Program hangs forever | Circular lock dependency | Lock ordering, tryLock, arbitrator |
| **Livelock** | CPU busy but no progress | Threads keep retrying in lockstep | Randomized backoff |
| **Starvation** | One thread never runs | Unfair scheduling/locking | Fair locks, redesign contention |
| **Memory leak** | OOM over time | ThreadLocal not cleaned, unbounded queues | `remove()`, bounded collections |

### Deadlock — The Four Coffman Conditions

For a deadlock to occur, **all four** conditions must hold simultaneously:

1. **Mutual exclusion** — resource is held exclusively (only one thread can use it)
2. **Hold and wait** — thread holds one resource while waiting for another
3. **No preemption** — resources cannot be forcibly taken from holding threads
4. **Circular wait** — A waits for B, B waits for A (or longer cycle)

**Breaking ANY one condition prevents deadlock**:

| Strategy | Condition Broken | Implementation |
|---|---|---|
| Lock ordering | Circular wait | Always acquire locks in consistent global order (e.g., by hash code or ID) |
| `tryLock` with timeout | Hold and wait | If can't get second lock, release first and retry |
| Single lock granularity | Hold and wait | Use one lock for all related resources (sacrifice concurrency for safety) |
| Arbitrator pattern | Hold and wait | Request all needed resources from a central coordinator atomically |

### Deadlock Detection and Diagnosis

#### Reading Thread Dumps

A thread dump shows every thread's state and what locks it holds/waits for. To get one:
- `jstack <pid>` (command line)
- `Ctrl+Break` (Windows) or `Ctrl+\` (Unix) in the terminal running the JVM
- `ThreadMXBean.findDeadlockedThreads()` (programmatic)

**Example thread dump showing deadlock**:
```
"thread-1" #12 BLOCKED
  - waiting to lock <0x000000076b123456> (a java.lang.Object)
  - locked <0x000000076b654321> (a java.lang.Object)

"thread-2" #13 BLOCKED
  - waiting to lock <0x000000076b654321> (a java.lang.Object)
  - locked <0x000000076b123456> (a java.lang.Object)
```

**Reading this**: Thread-1 holds `...321` and waits for `...456`. Thread-2 holds `...456` and waits for `...321`. **Circular dependency → deadlock**.

#### Programmatic Detection

```java
ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
long[] deadlockedIds = mxBean.findDeadlockedThreads();
if (deadlockedIds != null) {
    ThreadInfo[] infos = mxBean.getThreadInfo(deadlockedIds, true, true);
    for (ThreadInfo info : infos) {
        System.err.println("Deadlocked thread: " + info.getThreadName());
        System.err.println("  Lock: " + info.getLockName());
        System.err.println("  Lock owner: " + info.getLockOwnerName());
    }
}
```

### Race Condition Example — Check-Then-Act

```java
// ❌ RACE: another thread can put a value between containsKey and put
if (!map.containsKey(key)) {
    map.put(key, computeExpensiveValue(key));
}

// ✅ ATOMIC: ConcurrentHashMap guarantees atomicity
map.computeIfAbsent(key, k -> computeExpensiveValue(k));
```

---

## Part VII - Virtual Threads and Structured Concurrency (Java 21+)

### Platform Threads vs Virtual Threads — Architecture

| Aspect | Platform Thread | Virtual Thread |
|---|---|---|
| **Backed by** | OS thread (1:1 mapping) | JVM-managed continuation on carrier thread (M:N mapping) |
| **Stack memory** | ~1MB per thread (fixed) | Starts at ~1KB, grows as needed (stored on heap) |
| **Creation cost** | Expensive (OS syscall) | Cheap (plain Java object) |
| **Max concurrent** | ~thousands (OS/memory limits) | ~millions (limited by heap, not OS) |
| **Scheduling** | OS scheduler (preemptive) | JVM scheduler (cooperative, on `ForkJoinPool`) |
| **Blocking behavior** | Blocks the OS thread (wastes resources) | **Unmounts** from carrier, carrier runs other virtual threads |

**How virtual thread blocking works internally**:
```
Virtual Thread A calls Thread.sleep(1000)
    │
    ├─ JVM detects blocking operation
    ├─ Saves VT-A's continuation (stack snapshot) to heap
    ├─ UNMOUNTS VT-A from carrier thread
    ├─ Carrier thread picks up VT-B (another virtual thread) → runs it
    │
    ... 1000ms later ...
    │
    ├─ JVM schedules VT-A to be REMOUNTED on a carrier
    └─ VT-A resumes exactly where it left off
```

### When Virtual Threads Shine

| Workload | Virtual Threads Benefit |
|---|---|
| HTTP server (1000s of concurrent requests, each doing DB + API calls) | ✅ Massive — each request gets its own virtual thread, blocking is free |
| JDBC database queries | ✅ Great — blocking I/O is efficiently multiplexed |
| File I/O (NIO) | ✅ Good — virtual threads work naturally with blocking file APIs |
| CPU-bound computation (math, compression) | ❌ No benefit — still need same number of CPU cores |
| `synchronized` blocks with long hold times | ⚠️ Caution — carrier thread is **pinned** (cannot unmount) |

### Pinning — The Virtual Thread Gotcha

A virtual thread becomes **pinned** to its carrier thread when:
1. It is inside a `synchronized` block or method
2. It calls a `native` method that holds a monitor

**When pinned**: The carrier thread is blocked, reducing the pool's effective parallelism — exactly like a platform thread.

**Fix**: Replace `synchronized` with `ReentrantLock` (virtual threads unmount cleanly while waiting for a `ReentrantLock`).

```java
// ❌ CAUSES PINNING — virtual thread pins its carrier
synchronized (sharedResource) {
    blockingDatabaseCall(); // carrier thread is stuck!
}

// ✅ NO PINNING — virtual thread unmounts while waiting for lock
private final ReentrantLock lock = new ReentrantLock();
lock.lock();
try {
    blockingDatabaseCall(); // carrier thread is freed during I/O wait
} finally {
    lock.unlock();
}
```

### Using Virtual Threads

```java
// Create a single virtual thread
Thread.startVirtualThread(() -> handleRequest(request));

// Virtual-thread-per-task executor (most common pattern)
try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (Request req : requests) {
        executor.submit(() -> handleRequest(req)); // each task gets its own virtual thread
    }
} // auto-shutdown — waits for all tasks to complete

// Thread.Builder API
Thread vt = Thread.ofVirtual()
    .name("handler-", 0)   // "handler-0", "handler-1", ...
    .start(() -> work());
```

### Structured Concurrency (Preview)

Structured concurrency ensures that tasks spawned within a scope are **bounded by that scope** — they all complete (or are cancelled) before the scope exits. No orphaned threads.

```java
// PSEUDO-CODE — API details vary by JDK preview version
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Subtask<User> userTask = scope.fork(() -> fetchUser(userId));
    Subtask<List<Order>> ordersTask = scope.fork(() -> fetchOrders(userId));

    scope.join();           // wait for both tasks
    scope.throwIfFailed();  // propagate first failure

    // Both tasks completed — safe to access results
    User user = userTask.get();
    List<Order> orders = ordersTask.get();
    return new UserProfile(user, orders);
}
// If fetchUser fails:
//   1. scope cancels fetchOrders automatically
//   2. throwIfFailed() re-throws the exception
//   3. No orphaned threads — scope guarantees cleanup
```

**Shutdown policies**:
- `ShutdownOnFailure` — cancel remaining tasks on first failure
- `ShutdownOnSuccess` — cancel remaining tasks on first success (useful for "first response wins")

---

## Exercises Roadmap (Aligned to Your Repo)

You have 5 exercises. Each is a targeted mastery check.

### Exercise 1 — Producer-Consumer Order System
**File**: `exercises/src/main/java/exercises/OrderQueue.java`
**Concepts tested**: `BlockingQueue`, `ExecutorService`, poison pill shutdown, `LongAdder` metrics

### Exercise 2 — Parallel File Processor
**File**: `exercises/src/main/java/exercises/ParallelFileProcessor.java`
**Concepts tested**: 4 parallelism strategies comparison, `ConcurrentHashMap<String, LongAdder>`, `computeIfAbsent`

### Exercise 3 — Thread-Safe Cache with TTL
**File**: `exercises/src/main/java/exercises/ThreadSafeCache.java`
**Concepts tested**: `ReadWriteLock`, TTL eviction, background cleanup scheduling, hit/miss counters

### Exercise 4 — Async Data Aggregator
**File**: `exercises/src/main/java/exercises/AsyncDataAggregator.java`
**Concepts tested**: `CompletableFuture` fan-out/fan-in, `orTimeout`, retry with backoff, circuit breaker

### Exercise 5 — Dining Philosophers
**File**: `exercises/src/main/java/exercises/DiningPhilosophers.java`
**Concepts tested**: Deadlock demonstration, lock ordering fix, `tryLock` fix, arbitrator fix, virtual threads

---

## Common Mistakes to Avoid

| Mistake | Why It's Wrong | Fix |
|---|---|---|
| Assuming `volatile` is enough for `x++` | `volatile` gives visibility, NOT atomicity for compound ops | Use `AtomicInteger` or `synchronized` |
| Updating multiple related fields without a lock | Readers may see inconsistent state (field A updated, field B not yet) | Use one lock for all related fields, or redesign as immutable object |
| Forgetting `ThreadLocal.remove()` in thread pools | Values leak across tasks — security risk + memory leak | Always `remove()` in `finally` block |
| Using unbounded executors/queues | Tasks/threads pile up under load → OOM | Use `ThreadPoolExecutor` with bounded queue |
| Blocking inside Fork/Join tasks | Carrier threads are pinned → parallelism collapses | Use `ManagedBlocker` or move blocking to a separate pool |
| Not shutting down executors | JVM never exits (non-daemon threads keep running) | Always `shutdown()` + `awaitTermination()` |
| Swallowing exceptions in async pipelines | Failures become invisible — silent data corruption | Always add `exceptionally`/`handle` to log and recover |
| Using `synchronized` with virtual threads | Pins the virtual thread to carrier → defeats the purpose | Replace with `ReentrantLock` |
| "Fixing" deadlocks by adding `sleep()` | Just changes the timing — deadlock will still occur under load | Fix root cause: lock ordering, tryLock, or redesign |
| `notify()` instead of `notifyAll()` | Wrong thread may wake up → can cause deadlock | Use `notifyAll()` or separate `Condition` objects |

---

## Key Terms Glossary

| Term | Definition |
|---|---|
| **Concurrency** | Multiple tasks making progress by interleaving execution (may or may not run simultaneously) |
| **Parallelism** | Multiple tasks running at the same physical instant on different CPU cores |
| **Happens-before** | JMM guarantee that one action's effects are visible and ordered before another action |
| **Visibility** | The property that a write by one thread is observable by another thread |
| **Atomicity** | An operation completes as a single indivisible step — no intermediate state is visible |
| **Intrinsic lock (monitor)** | The lock associated with every Java object, used by `synchronized` |
| **Volatile** | Field modifier providing visibility + ordering guarantees, but NOT compound atomicity |
| **CAS (Compare-And-Swap)** | Hardware-level atomic instruction: update a value only if it currently equals the expected value |
| **LongAdder** | High-contention counter using cell striping — faster than `AtomicLong` for increment-only workloads |
| **Thread-safe class** | A class that correctly handles concurrent access without requiring external synchronization |
| **Immutability** | Object state cannot change after construction → inherently thread-safe |
| **Confinement** | Restricting data access to a single thread — eliminates need for synchronization |
| **Safe publication** | Ensuring an object is fully constructed before making it visible to other threads |
| **Work-stealing** | Fork/Join scheduling where idle threads steal tasks from busy threads' deques |
| **Virtual thread** | JVM-managed lightweight thread (~1KB stack); unmounts from carrier on blocking I/O |
| **Carrier thread** | Platform thread that executes virtual threads (part of the ForkJoinPool) |
| **Pinning** | Virtual thread cannot unmount from carrier (due to `synchronized` or native code) |
| **Structured concurrency** | Task scoping pattern where all child tasks complete before the parent scope exits |
| **Weakly consistent iterator** | Iterator that may show partial/stale state without throwing `ConcurrentModificationException` |
| **Deadlock** | Permanent blocking due to circular lock dependencies |
| **Livelock** | Threads actively running but making no progress (keep undoing each other's work) |
| **Starvation** | Thread never gets CPU time or lock access because other threads monopolize resources |
| **Data race** | Unsynchronized concurrent access to shared mutable state (at least one write) |
| **Spurious wakeup** | JVM may wake a thread from `wait()` without `notify()` — always re-check condition in loop |
| **Back-pressure** | Mechanism to slow producers when consumers can't keep up (bounded queues, CallerRunsPolicy) |
| **Reentrant lock** | Lock that can be re-acquired by the same thread without deadlock (tracks hold count) |

---

## Progress Tracker

Use this as your "am I ready?" checklist:

### Threads and Synchronization
- [ ] Explain all 6 thread lifecycle states with examples of how to enter/exit each state
- [ ] Explain how `interrupt()` works and why you must never swallow `InterruptedException`
- [ ] Explain happens-before rules (at least 5 of them) and demonstrate with volatile vs lock
- [ ] Write correct guarded condition loops with `wait/notify` — explain why loops, not `if`
- [ ] Explain what a thread dump shows and how to diagnose deadlock from one

### Locks and Thread-Safe Design
- [ ] Choose between `synchronized`, `ReentrantLock`, `ReadWriteLock`, `StampedLock` — explain tradeoffs
- [ ] Explain what "reentrant" means and why try/finally is mandatory with explicit locks
- [ ] Design a thread-safe class using all three pillars (immutability, confinement, safe publication)
- [ ] Explain CAS at the hardware level and when to use AtomicInteger vs LongAdder
- [ ] Explain the ThreadLocal memory leak with thread pools and how ScopedValue fixes it

### Execution Frameworks
- [ ] Configure a `ThreadPoolExecutor` with all 7 parameters — explain task submission flow
- [ ] Calculate thread pool size for CPU-bound vs I/O-bound workloads
- [ ] Explain Fork/Join work-stealing with the deque diagram
- [ ] Implement a `RecursiveTask` and tune the threshold

### Async Pipelines and Collections
- [ ] Explain the difference between `thenApply` (map) and `thenCompose` (flatMap)
- [ ] Build a fan-out/fan-in pipeline with timeouts and retry
- [ ] Pick the correct concurrent collection from the decision matrix — justify the choice
- [ ] Use `ConcurrentHashMap.computeIfAbsent` and explain why check-then-act is a race

### Liveness and Diagnosis
- [ ] List the 4 Coffman conditions and explain which one each fix strategy breaks
- [ ] Identify deadlock in a thread dump (find the lock cycle)
- [ ] Explain livelock vs deadlock — give an example of each

### Virtual Threads
- [ ] Explain platform vs virtual thread architecture (M:N mapping, continuation, unmounting)
- [ ] Explain pinning and why `ReentrantLock` is preferred over `synchronized` for virtual threads
- [ ] Rewrite a thread-pool-based design to use virtual-thread-per-task executor

### Exercises
- [ ] Complete all 5 exercises successfully

---

## What's Next

After this phase, you will be ready for:
- **Phase 06 (JVM Internals & Performance)**: Learn why concurrency decisions interact with CPU caches, GC pauses, and JIT compiler optimizations.
- **Advanced production patterns**: Concurrent caching with TTL, background job design, safe shutdown strategies, and reactive programming.

