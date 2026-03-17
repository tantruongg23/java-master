# Study Guide — Phase 04: Exception Handling, I/O & NIO

> **Estimated Duration:** ~1–2 weeks (~14 hours)
> **Prerequisites:** Phase 03 (Collections & Generics)
> **Philosophy:** *A crashing application is the worst thing you can ship. This guide teaches you to build software that fails gracefully, communicates clearly, and handles the messy real world of files, networks, and broken data without losing its composure.*

---

## Table of Contents

- [Why This Phase Will Change How You Write Code](#why-this-phase-will-change-how-you-write-code)
- [Phase Overview](#phase-overview--what-youll-learn)
- [Study Strategy](#study-strategy)
- **Part I — Exception Handling**
  - [Why Crashing Is the Worst Thing in Production](#why-crashing-is-the-worst-thing-in-production)
  - [The Exception Hierarchy](#the-exception-hierarchy)
  - [Checked vs Unchecked Exceptions](#checked-vs-unchecked-exceptions)
  - [Custom Exception Classes](#custom-exception-classes)
  - [try-with-resources & AutoCloseable](#try-with-resources--autocloseable)
  - [Exception Handling Best Practices](#exception-handling-best-practices)
- **Part II — Classic I/O**
  - [Why I/O Matters](#why-io-matters)
  - [The Decorator Pattern in Java I/O](#the-decorator-pattern-in-java-io)
  - [Byte Streams](#byte-streams)
  - [Character Streams](#character-streams)
  - [Buffering — The Single Biggest Performance Win](#buffering--the-single-biggest-performance-win)
  - [Character Encodings](#character-encodings)
- **Part III — NIO.2 (Modern File I/O)**
  - [The Path API](#the-path-api)
  - [The Files Utility Class](#the-files-utility-class)
  - [Channels & Buffers](#channels--buffers)
  - [Memory-Mapped Files](#memory-mapped-files)
  - [WatchService — Reacting to File System Changes](#watchservice--reacting-to-file-system-changes)
  - [NIO vs Classic I/O — When to Use Which](#nio-vs-classic-io--when-to-use-which)
- **Part IV — Serialization**
  - [Java Serialization — Why It's Dangerous](#java-serialization--why-its-dangerous)
  - [Safer Alternatives](#safer-alternatives)
- [Exercises Roadmap](#exercises-roadmap)
- [Common Mistakes to Avoid](#common-mistakes-to-avoid)
- [Key Terms Glossary](#key-terms-glossary)
- [Progress Tracker](#progress-tracker)
- [What's Next](#whats-next)

---

## Why This Phase Will Change How You Write Code

### The Uncomfortable Truth About Software

Every program you've written so far probably assumed the happy path. The file exists. The network responds. The user input is valid. The disk has space. Memory is plentiful.

**In production, none of these assumptions hold.**

Files get deleted mid-read. Networks drop connections after the third retry. Users paste emoji into numeric fields. Disks fill up at 3 AM on a Saturday. Memory runs out because a single query returned 10 million rows instead of 10.

This phase teaches you the two skills that separate production-ready code from tutorial code:

1. **Exception handling** — designing systems that fail gracefully, preserve data integrity, and give operators enough information to diagnose problems.
2. **I/O mastery** — reading and writing data efficiently, whether it's a 50 KB config file or a 50 GB log archive.

### Why You Can't Skip This Phase

| Scenario | What You Need |
|---|---|
| User submits a payment but the database times out | Exception handling that rolls back the transaction, retries intelligently, and doesn't charge the user twice |
| Parse a 2 GB CSV export from a client | NIO + BufferedReader that processes line-by-line without loading the entire file into memory |
| Config file has a typo in a required field | Custom exceptions with clear messages: `MissingRequiredKeyException: "database.url" not found in app.properties` |
| Deploy to production and a critical file is missing | `try-with-resources` that closes connections cleanly even when errors cascade |
| Monitor a directory for new data files to process | `WatchService` that reacts to file creation events in real time |
| Serialize user session data for a distributed cache | Understanding why `Serializable` is a security risk and preferring JSON |

---

## Phase Overview — What You'll Learn

```
Phase 04: Exception Handling, I/O & NIO
│
├─ Part I: Exception Handling
│  ├─ 1. Exception Hierarchy (1 h)
│  │     Throwable → Error / Exception → RuntimeException
│  ├─ 2. Checked vs Unchecked Exceptions (1 h)
│  │     Trade-offs, when to use each, Effective Java guidance
│  ├─ 3. Custom Exception Classes (2 h)
│  │     Business exceptions, chaining, layered hierarchies
│  └─ 4. try-with-resources (1 h)
│        AutoCloseable, suppressed exceptions, resource management
│
├─ Part II: Classic I/O
│  └─ 5. Java I/O Streams (3 h)
│        Decorator pattern, byte/char streams, buffering, encodings
│
├─ Part III: NIO.2
│  ├─ 6. Path & Files API (3 h)
│  │     Path operations, Files utility, Channels & Buffers
│  └─ 7. WatchService (1 h)
│        File system event monitoring
│
└─ Part IV: Serialization
   └─ 8. Serialization & Alternatives (2 h)
         Serializable pitfalls, JSON/XML alternatives
```

---

## Study Strategy

### Recommended Order

| Week | Focus | Hours | Key Activity |
|---|---|---|---|
| **Week 1** | Topics 1–5 (Exceptions + Classic I/O) | ~8 h | Build a mental model of the exception hierarchy. Write code that deliberately fails and observe behavior. Read files in every wrong way first, then the right way. |
| **Week 2** | Topics 6–8 (NIO + Serialization) + Exercises | ~6 h | Refactor your Week 1 code to use NIO.2. Build the exercises. Measure performance differences between buffered/unbuffered, classic/NIO. |

### How to Study Each Topic

1. **Write failing code first** — before reading about exception handling, write code that throws exceptions. Observe what happens when you don't handle them. This builds intuition faster than reading rules.
2. **Measure I/O performance** — time your file operations with `System.nanoTime()`. The difference between unbuffered and buffered reads on a 100 MB file is visceral.
3. **Read JDK source** — `Ctrl+Click` into `BufferedReader.java`, `Files.java`, and `Path.java`. These are well-written reference implementations.
4. **Break things intentionally** — delete a file mid-read, fill up a temp directory, corrupt a serialized object. See how your code responds.
5. **Explain the "why"** — anyone can memorize `try-catch`. Can you explain *why* `finally` runs even during a `return`? Can you explain *why* `BufferedReader` is 100x faster than `FileReader` alone?

---

# Part I — Exception Handling

## Why Crashing Is the Worst Thing in Production

### The Real Cost of a Crash

When your application crashes in production, the damage goes far beyond "the server is down." Here's what actually happens:

**1. Data Loss and Corruption**

A crash interrupts operations mid-execution. A user was saving a document — half-written. A transaction was processing a payment — money debited but not credited. A batch job was importing 500,000 records — 247,312 imported, 252,688 lost, and you don't know which ones.

```java
// This code is a time bomb in production
public void transferMoney(Account from, Account to, BigDecimal amount) {
    from.debit(amount);
    // <<< If the app crashes HERE, money vanishes
    to.credit(amount);
}
```

Without proper exception handling and transaction management, every line of code between two related operations is a potential corruption point.

**2. Cascading Failures**

In microservices architectures, one crashed service brings down its dependents. Service A calls Service B which calls Service C. Service C crashes. Service B's requests to C start timing out. Service B's thread pool fills with waiting threads. Service B stops responding. Service A's requests to B start timing out. Now three services are down because one didn't handle an `OutOfMemoryError` gracefully.

```
User Request
  → Service A (API Gateway)
    → Service B (Order Service)
      → Service C (Inventory Service) ← CRASH
      ← timeout... timeout... timeout...
    ← thread pool exhausted
  ← 503 Service Unavailable
```

**3. Lost Revenue**

Amazon calculated that every 100ms of latency costs them 1% in sales. A crash isn't 100ms — it's minutes or hours of total unavailability. For an e-commerce platform doing $100,000/hour, a 30-minute outage is $50,000 in lost revenue. For a financial trading platform, a 10-second outage during market hours can mean millions.

**4. Broken Trust**

Users don't see stack traces. They see "Something went wrong." They see a blank screen. They see their shopping cart emptied. They see their uploaded file vanished. Every crash erodes trust, and trust is the hardest thing to rebuild. One crash during a user's first experience with your product, and they may never return.

**5. Debugging Nightmares**

The cruelest aspect of crashes: they're hardest to debug when they matter most. Production environments have different data, different load, different timing than your development machine. A crash that happens under heavy load at 2 AM with a specific combination of user inputs may be impossible to reproduce locally.

### What Robust Error Handling Gives You

Instead of crashing, a well-designed system:

| Without Error Handling | With Robust Error Handling |
|---|---|
| `NullPointerException` — stack trace dumped, user sees 500 | Validates input early: "Order ID is required" — clear message, HTTP 400 |
| Database timeout → thread dies → thread pool exhausted | Catches timeout, retries with backoff, falls back to cached data, alerts ops team |
| Malformed CSV line → entire import aborts | Logs the bad line with line number, skips it, continues processing, reports summary: "49,999 of 50,000 imported" |
| File not found → raw exception in API response | Returns structured error: `{"error": "CONFIG_NOT_FOUND", "detail": "app.properties missing from /etc/myapp/"}` |
| Out of disk space → data corrupted | Checks available space before write, fails fast with actionable error, existing data preserved |

**The principle:** Your application should never surprise a user, an operator, or a downstream system with an uncontrolled failure. Every failure mode should be anticipated, handled, and communicated.

---

## The Exception Hierarchy

Understanding Java's exception hierarchy isn't about memorizing a tree diagram — it's about understanding a design philosophy: **different kinds of problems deserve different responses.**

### The Full Picture

```
Throwable                         ← Root of everything that can be thrown
├── Error                         ← JVM-level catastrophes — DO NOT CATCH
│   ├── OutOfMemoryError          ← Heap exhausted
│   ├── StackOverflowError        ← Infinite recursion
│   ├── VirtualMachineError       ← JVM itself is broken
│   ├── InternalError             ← Internal JVM failure
│   ├── AssertionError            ← Failed assertion
│   └── ...
│
└── Exception                     ← Application-level problems — SHOULD handle
    ├── IOException               ← (checked) File/network failures
    │   ├── FileNotFoundException
    │   ├── EOFException
    │   └── SocketException
    ├── SQLException              ← (checked) Database failures
    ├── ParseException            ← (checked) Malformed data
    ├── InterruptedException      ← (checked) Thread interruption
    │
    └── RuntimeException          ← (unchecked) Programming errors
        ├── NullPointerException        ← Dereferencing null
        ├── IllegalArgumentException    ← Bad method argument
        │   └── NumberFormatException   ← String → number failed
        ├── IllegalStateException       ← Object in wrong state
        ├── IndexOutOfBoundsException   ← Array/list index wrong
        │   ├── ArrayIndexOutOfBoundsException
        │   └── StringIndexOutOfBoundsException
        ├── ClassCastException          ← Invalid type cast
        ├── UnsupportedOperationException ← Operation not implemented
        ├── ArithmeticException         ← Math error (divide by zero)
        ├── ConcurrentModificationException ← Modified during iteration
        └── ...
```

### Why This Design Exists

**`Error` — "The house is on fire. Get out."**

`Error` represents conditions that a reasonable application should not try to recover from. If the JVM runs out of memory, allocating objects for error recovery... requires memory. If the stack overflows, calling a recovery method... pushes another frame onto the stack. These are Catch-22 situations.

```java
// Don't do this — there's no meaningful recovery
try {
    processLargeDataSet();
} catch (OutOfMemoryError e) {
    // WRONG — this catch block itself may fail with OOM
    // because logging, string concatenation, etc. all allocate memory
    logger.error("Out of memory!", e);
}
```

The correct response to `Error` is to let the JVM die, rely on external monitoring (process supervisors, Kubernetes liveness probes) to restart it, and fix the root cause (increase heap, fix the memory leak, limit the query).

**`Exception` (checked) — "Something went wrong, but you can handle it."**

Checked exceptions represent conditions that a well-written application should anticipate and recover from. The file might not exist. The network might be down. The input might be malformed. The compiler forces you to deal with these because ignoring them leads to data loss.

```java
// The compiler forces you to handle this — and rightly so
public String readConfig(Path path) throws IOException {
    return Files.readString(path);
    // IOException is checked — caller MUST handle it or propagate it
    // This is a feature: you CAN'T accidentally ignore a missing config file
}
```

**`RuntimeException` (unchecked) — "You have a bug. Fix the code."**

Runtime exceptions represent programming errors — conditions that shouldn't happen if the code is correct. A `NullPointerException` means you forgot a null check. An `IllegalArgumentException` means you passed bad data. These don't require `throws` declarations because they should be prevented by writing correct code, not caught.

```java
public void setAge(int age) {
    if (age < 0 || age > 150) {
        throw new IllegalArgumentException("Age must be 0–150, got: " + age);
    }
    this.age = age;
}
```

### Reading Stack Traces Like a Pro

A stack trace is a snapshot of the call stack at the moment the exception was thrown. Reading it correctly is a core debugging skill.

```
Exception in thread "main" java.lang.NullPointerException: 
    Cannot invoke "String.length()" because "name" is null
    at com.myapp.service.UserService.validateName(UserService.java:47)    ← WHERE it was thrown
    at com.myapp.service.UserService.createUser(UserService.java:23)       ← WHO called that
    at com.myapp.api.UserController.handleCreate(UserController.java:58)   ← WHO called THAT
    at com.myapp.api.Router.dispatch(Router.java:112)                      ← ...and so on
Caused by: java.io.IOException: Connection refused
    at java.base/sun.nio.ch.Net.connect0(Native Method)
    at java.base/sun.nio.ch.Net.connect(Net.java:579)
    ... 8 more
```

**How to read it:**

1. **Start at the top** — the exception type and message tell you *what* happened
2. **First `at` line** — this is *where* the exception was thrown (the originating frame)
3. **Read downward** — this is the call chain that led there
4. **`Caused by`** — this is the original root cause (always check this first in chained exceptions)
5. **`... N more`** — the remaining frames are identical to the parent's stack trace (Java deduplicates them)

### Multi-Catch Blocks

Java 7 introduced multi-catch to reduce duplicated handler code:

```java
// Before Java 7 — duplicated code
try {
    String content = Files.readString(path);
    int value = Integer.parseInt(content.trim());
} catch (IOException e) {
    logger.error("File read failed: {}", e.getMessage());
    throw new ConfigException("Cannot read config", e);
} catch (NumberFormatException e) {
    logger.error("File read failed: {}", e.getMessage());  // same code!
    throw new ConfigException("Cannot read config", e);
}

// After Java 7 — multi-catch
try {
    String content = Files.readString(path);
    int value = Integer.parseInt(content.trim());
} catch (IOException | NumberFormatException e) {
    // 'e' is effectively final — you can't reassign it
    logger.error("Config load failed: {}", e.getMessage());
    throw new ConfigException("Cannot read config", e);
}
```

**Rules:**
- The caught exceptions must not be in a parent-child relationship (can't catch both `IOException` and `FileNotFoundException` in the same multi-catch — the child is already covered by the parent)
- The variable `e` is implicitly `final` — you cannot reassign it inside the block

### The `finally` Block — Guarantees and Gotchas

`finally` always executes, whether or not an exception was thrown, and even if a `return` statement is encountered in the `try` or `catch` block.

```java
// finally always runs — even with return
public int riskyMethod() {
    try {
        return 1;
    } finally {
        System.out.println("This ALWAYS prints");
        // If you put 'return 2' here, it OVERRIDES the try's return!
        // This is a well-known gotcha. NEVER return from finally.
    }
}
// prints "This ALWAYS prints", returns 1
```

**Gotcha — return in finally overrides everything:**

```java
public int brokenMethod() {
    try {
        throw new RuntimeException("Error!");
    } catch (RuntimeException e) {
        return -1;  // this return is prepared...
    } finally {
        return 0;   // ...but THIS return overrides it (and swallows the exception!)
    }
}
// returns 0 — the exception is silently swallowed. NEVER DO THIS.
```

**Gotcha — exception in finally replaces the original:**

```java
public void dangerousCleanup() throws IOException {
    FileInputStream fis = null;
    try {
        fis = new FileInputStream("data.txt");
        // ... process file, throws ProcessingException
    } finally {
        fis.close(); // If this throws IOException, the ProcessingException is LOST
    }
}
// This is exactly why try-with-resources was invented (covered later)
```

---

## Checked vs Unchecked Exceptions

This is one of the most debated topics in Java. Understanding the trade-offs lets you make informed decisions rather than dogmatic ones.

### The Core Distinction

| Aspect | Checked Exceptions | Unchecked Exceptions |
|---|---|---|
| **Extends** | `Exception` (but not `RuntimeException`) | `RuntimeException` |
| **Compiler enforcement** | YES — must catch or declare `throws` | NO — can be thrown anywhere |
| **Typical cause** | External conditions beyond your control | Programming errors in your code |
| **Examples** | `IOException`, `SQLException`, `ParseException` | `NullPointerException`, `IllegalArgumentException` |
| **Caller's responsibility** | Handle it or propagate it | Fix the bug that causes it |
| **Effect on API** | Forces `throws` clause in method signature | Clean method signatures |

### When to Use Checked Exceptions

Use checked exceptions when **all three** conditions are true:

1. **The failure is caused by something external** — a file, a network, a database, user input
2. **The caller can reasonably recover** — retry, use a default, prompt the user, fall back to a cache
3. **Ignoring the failure would cause data loss or corruption**

```java
// Good use of checked exception — caller MUST deal with this
public UserProfile loadProfile(Path configFile) throws IOException {
    String json = Files.readString(configFile);
    return objectMapper.readValue(json, UserProfile.class);
}

// Caller can recover
try {
    profile = loadProfile(path);
} catch (IOException e) {
    logger.warn("Profile not found, using defaults: {}", e.getMessage());
    profile = UserProfile.defaults();
}
```

### When to Use Unchecked Exceptions

Use unchecked exceptions when the failure indicates a **programming error** — something the developer should fix, not the caller should handle:

```java
// Good use of unchecked exception — this is a bug in the caller's code
public void setDiscount(double percentage) {
    if (percentage < 0 || percentage > 100) {
        throw new IllegalArgumentException(
            "Discount must be 0–100, got: " + percentage
        );
    }
    this.discount = percentage;
}

// The fix isn't to catch this — it's to not pass invalid values:
// product.setDiscount(-5);  ← fix THIS, don't catch the exception
```

### The Real-World Debate

Many modern frameworks (Spring, Hibernate) wrap checked exceptions in unchecked ones. Here's why:

**Problem with checked exceptions at scale:**

```java
// A 6-layer call chain where everyone must declare "throws"
public User getUser(String id) throws SQLException, IOException, ParseException {
    return repository.findById(id);
}

public Order processOrder(OrderRequest req) 
    throws SQLException, IOException, ParseException, MessagingException {
    User user = getUser(req.getUserId());
    // ...
}

public Response handleRequest(Request req) 
    throws SQLException, IOException, ParseException, MessagingException, ValidationException {
    Order order = processOrder(req.getBody());
    // ...
}
// Every layer accumulates more throws clauses. This is "exception declaration escalation."
```

**The pragmatic solution — wrap at layer boundaries:**

```java
// Repository layer — catches low-level, throws domain-specific
public User findById(String id) {
    try {
        return jdbcTemplate.queryForObject(sql, mapper, id);
    } catch (DataAccessException e) {
        throw new UserNotFoundException(id, e);  // unchecked, wraps the cause
    }
}

// Service layer — clean signature
public Order processOrder(OrderRequest req) {
    User user = userRepo.findById(req.getUserId()); // no throws clause needed
    // ...
}
```

### The Decision Framework

```
Is the failure caused by a programming error (bad argument, null, wrong state)?
  → YES → Use unchecked (IllegalArgumentException, IllegalStateException, NullPointerException)

Is the failure caused by an external condition (file, network, database, user input)?
  → Can the IMMEDIATE caller recover meaningfully?
    → YES → Use checked (IOException, custom checked exception)
    → NO  → Wrap in unchecked at the boundary (throw new ServiceException(cause))
```

---

## Custom Exception Classes

### When to Create Custom Exceptions

Create a custom exception when:
- Standard exceptions don't carry enough context (which order failed? which file?)
- You need to distinguish between different failure modes in the same catch block
- Your domain has failure conditions that deserve their own type (payment declined, inventory insufficient)
- You're building a library and want a clean exception API for consumers

Do **NOT** create a custom exception when:
- A standard exception already describes the problem (`IllegalArgumentException`, `IllegalStateException`)
- You'd be creating `MyNullPointerException` — that's just noise
- The exception adds no new information beyond what the message string provides

### The Business Exception Pattern

A well-designed custom exception carries **context** — not just a message, but structured data that helps diagnose the problem:

```java
public class OrderProcessingException extends RuntimeException {
    private final String orderId;
    private final String customerId;
    private final OrderStatus statusAtFailure;

    public OrderProcessingException(String orderId, String customerId,
                                     OrderStatus status, String message, Throwable cause) {
        super(message, cause);
        this.orderId = orderId;
        this.customerId = customerId;
        this.statusAtFailure = status;
    }

    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public OrderStatus getStatusAtFailure() { return statusAtFailure; }

    @Override
    public String getMessage() {
        return String.format("[Order: %s, Customer: %s, Status: %s] %s",
            orderId, customerId, statusAtFailure, super.getMessage());
    }
}
```

**Usage:**

```java
public Order processOrder(String orderId) {
    Order order = orderRepo.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(orderId));

    try {
        paymentService.charge(order.getCustomerId(), order.getTotal());
    } catch (PaymentDeclinedException e) {
        throw new OrderProcessingException(
            orderId, order.getCustomerId(), order.getStatus(),
            "Payment declined: " + e.getDeclineReason(), e
        );
    }
    // ...
}
```

### Exception Chaining — Always Preserve the Cause

**The golden rule:** When you catch an exception and throw a new one, **always pass the original as the cause.** Losing the original exception is the #1 cause of undebuggable production issues.

```java
// WRONG — original cause is lost forever
try {
    connection.execute(query);
} catch (SQLException e) {
    throw new DataAccessException("Query failed"); // WHERE did it fail? WHAT was the SQL error? Gone.
}

// RIGHT — chain preserves the full story
try {
    connection.execute(query);
} catch (SQLException e) {
    throw new DataAccessException("Query failed: " + query, e); // e is preserved as cause
}
```

When someone reads the production logs, chained exceptions give the full picture:

```
com.myapp.DataAccessException: Query failed: SELECT * FROM users WHERE id = ?
    at com.myapp.UserRepository.findById(UserRepository.java:42)
    ...
Caused by: java.sql.SQLException: Connection refused: connect
    at com.mysql.cj.jdbc.ConnectionImpl.createNewIO(ConnectionImpl.java:836)
    ...
Caused by: java.net.ConnectException: Connection refused
    at java.base/sun.nio.ch.Net.connect0(Native Method)
    ...
```

Three levels of context: "query failed" → "connection refused (SQL)" → "connection refused (TCP)". Without chaining, you'd only see the first line.

### Exception Hierarchies for Layered Architectures

In a well-structured application, each layer has its own exception family:

```java
// === Base exception for the entire application ===
public abstract class AppException extends RuntimeException {
    private final String errorCode;

    protected AppException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() { return errorCode; }
}

// === Service layer exceptions ===
public class ServiceException extends AppException {
    public ServiceException(String errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}

public class OrderNotFoundException extends ServiceException {
    public OrderNotFoundException(String orderId) {
        super("ORDER_NOT_FOUND", "Order not found: " + orderId, null);
    }
}

public class InsufficientInventoryException extends ServiceException {
    private final String productId;
    private final int requested;
    private final int available;

    public InsufficientInventoryException(String productId, int requested, int available) {
        super("INSUFFICIENT_INVENTORY",
            String.format("Product %s: requested %d, available %d", productId, requested, available),
            null);
        this.productId = productId;
        this.requested = requested;
        this.available = available;
    }

    public String getProductId() { return productId; }
    public int getRequested() { return requested; }
    public int getAvailable() { return available; }
}

// === Repository layer exceptions ===
public class DataAccessException extends AppException {
    public DataAccessException(String message, Throwable cause) {
        super("DATA_ACCESS_ERROR", message, cause);
    }
}
```

**The benefit:** Your API layer can catch `ServiceException` to return 4xx errors and `DataAccessException` to return 5xx errors, without knowing the specifics of every possible failure:

```java
@PostMapping("/orders")
public ResponseEntity<?> createOrder(@RequestBody OrderRequest req) {
    try {
        Order order = orderService.create(req);
        return ResponseEntity.ok(order);
    } catch (OrderNotFoundException | InsufficientInventoryException e) {
        return ResponseEntity.badRequest().body(Map.of(
            "error", e.getErrorCode(),
            "message", e.getMessage()
        ));
    } catch (DataAccessException e) {
        logger.error("Database error processing order", e);
        return ResponseEntity.status(500).body(Map.of(
            "error", "INTERNAL_ERROR",
            "message", "Please try again later"
        ));
    }
}
```

---

## try-with-resources & AutoCloseable

### The Problem try-with-resources Solves

Before Java 7, resource cleanup was verbose, error-prone, and frequently buggy:

```java
// Pre-Java 7 — manual resource management (don't do this anymore)
BufferedReader reader = null;
try {
    reader = new BufferedReader(new FileReader("data.txt"));
    String line = reader.readLine();
    // process...
} catch (IOException e) {
    logger.error("Read failed", e);
} finally {
    if (reader != null) {
        try {
            reader.close();  // close() itself can throw!
        } catch (IOException e) {
            logger.error("Close failed", e);  // original exception may be lost
        }
    }
}
```

Problems with this pattern:
- **9 lines just for cleanup** — the ceremony overwhelms the logic
- **close() can throw**, and if it does, it replaces the original exception
- **Easy to forget** — especially with multiple resources
- **Null-check required** — if the constructor throws, the variable is null

### The try-with-resources Solution

```java
// Java 7+ — clean, correct, and exception-safe
try (BufferedReader reader = new BufferedReader(new FileReader("data.txt"))) {
    String line = reader.readLine();
    // process...
} catch (IOException e) {
    logger.error("Read failed", e);
}
// reader is automatically closed, even if an exception occurs
```

**Rules:**
1. The resource must implement `AutoCloseable` (or its subinterface `Closeable`)
2. Resources are closed in **reverse declaration order**
3. If both the `try` block and `close()` throw, the `close()` exception becomes a **suppressed exception** (not lost!)

### Multiple Resources

```java
try (
    FileInputStream fis = new FileInputStream("input.bin");
    BufferedInputStream bis = new BufferedInputStream(fis);
    FileOutputStream fos = new FileOutputStream("output.bin");
    BufferedOutputStream bos = new BufferedOutputStream(fos)
) {
    byte[] buffer = new byte[8192];
    int bytesRead;
    while ((bytesRead = bis.read(buffer)) != -1) {
        bos.write(buffer, 0, bytesRead);
    }
}
// Close order: bos → fos → bis → fis (reverse of declaration)
```

### Suppressed Exceptions

When the `try` block throws exception A, and then `close()` throws exception B, Java doesn't lose either one:

```java
public class FlakyResource implements AutoCloseable {
    public void doWork() {
        throw new RuntimeException("Work failed");        // Exception A
    }

    @Override
    public void close() {
        throw new RuntimeException("Close also failed");  // Exception B
    }
}

try (FlakyResource resource = new FlakyResource()) {
    resource.doWork();
} catch (RuntimeException e) {
    System.out.println(e.getMessage());                         // "Work failed" — primary
    System.out.println(e.getSuppressed()[0].getMessage());      // "Close also failed" — suppressed
}
```

This is a massive improvement over the old `finally` pattern where exception B would have completely replaced exception A.

### Writing Your Own AutoCloseable

Any class that holds an external resource (connection, file handle, lock, temp file) should implement `AutoCloseable`:

```java
public class DatabaseConnection implements AutoCloseable {
    private final Connection conn;
    private boolean closed = false;

    public DatabaseConnection(String url) throws SQLException {
        this.conn = DriverManager.getConnection(url);
    }

    public ResultSet query(String sql) throws SQLException {
        if (closed) throw new IllegalStateException("Connection already closed");
        return conn.createStatement().executeQuery(sql);
    }

    @Override
    public void close() throws SQLException {
        if (!closed) {
            closed = true;
            conn.close();
        }
    }
}

// Usage — automatic cleanup guaranteed
try (DatabaseConnection db = new DatabaseConnection(url)) {
    ResultSet rs = db.query("SELECT * FROM users");
    // process results...
}  // db.close() called automatically
```

---

## Exception Handling Best Practices

These principles come from years of production experience and Effective Java (Items 69–77):

### 1. Never Swallow Exceptions Silently

```java
// THE WORST THING YOU CAN DO — silent swallow
try {
    processPayment(order);
} catch (Exception e) {
    // empty catch block — the payment might have failed,
    // but we'll never know. Data corruption guaranteed.
}

// If you truly can't handle it, at minimum LOG it
try {
    processPayment(order);
} catch (PaymentException e) {
    logger.error("Payment failed for order {}: {}", order.getId(), e.getMessage(), e);
    throw e;  // re-throw after logging
}
```

### 2. Catch Specific Exceptions, Not Broad Ones

```java
// BAD — catches everything including NullPointerException, which is a bug
try {
    int value = Integer.parseInt(input);
    processValue(value);
} catch (Exception e) {
    return defaultValue;  // hides bugs in processValue()
}

// GOOD — catches only what you expect
try {
    int value = Integer.parseInt(input);
    processValue(value);
} catch (NumberFormatException e) {
    logger.warn("Invalid number input: '{}'", input);
    return defaultValue;
}
```

### 3. Fail Fast — Validate Early

```java
// BAD — fails deep in the stack with a confusing NullPointerException
public Report generate(ReportConfig config) {
    // ... 200 lines later ...
    String title = config.getTitle().toUpperCase();  // NPE if config is null
}

// GOOD — fails immediately with a clear message
public Report generate(ReportConfig config) {
    Objects.requireNonNull(config, "ReportConfig must not be null");
    if (config.getTitle() == null || config.getTitle().isBlank()) {
        throw new IllegalArgumentException("Report title is required");
    }
    // now proceed with confidence
}
```

### 4. Use Exceptions for Exceptional Conditions Only

```java
// BAD — using exceptions for control flow
public boolean isValidEmail(String email) {
    try {
        new InternetAddress(email).validate();
        return true;
    } catch (AddressException e) {
        return false;  // exceptions are expensive — this is a normal condition
    }
}

// GOOD — use normal control flow for expected conditions
public Optional<User> findUser(String id) {
    User user = cache.get(id);
    if (user != null) return Optional.of(user);
    return repository.findById(id);  // returns Optional.empty() if not found
}
```

### 5. Include Context in Exception Messages

```java
// BAD — useless message
throw new IllegalStateException("Invalid state");

// GOOD — includes what, where, and why
throw new IllegalStateException(String.format(
    "Cannot ship order %s: status is %s (expected PAID), customer: %s",
    order.getId(), order.getStatus(), order.getCustomerId()
));
```

### 6. Document Exceptions in Your API

```java
/**
 * Transfers funds between two accounts.
 *
 * @throws InsufficientFundsException if the source account balance is less than the amount
 * @throws AccountNotFoundException if either account does not exist
 * @throws AccountFrozenException if either account is frozen due to compliance holds
 * @throws IllegalArgumentException if amount is negative or zero
 */
public TransferReceipt transfer(String fromId, String toId, BigDecimal amount) { ... }
```

---

# Part II — Classic I/O (Deep Dive)

Classic I/O is still everywhere: CLI tools, ETL scripts, parsing exports, reading configs, and building simple file utilities. NIO.2 is usually better for file-system operations, but **the foundation is the same**: streams, buffering, encoding, and resource safety.

## Why I/O Matters (Production Reality)

I/O is where your program hits the physical world: disk, network, stdin/out. Because it’s slow, it amplifies mistakes:
- a tiny inefficiency becomes huge at 100 GB
- a missing `close()` becomes a file-descriptor leak under load
- a wrong encoding becomes silent data corruption

### Performance intuition (why buffers exist)

| Operation | Approximate Latency |
|---|---|
| L1 cache reference | 1 ns |
| RAM access | 100 ns |
| SSD random read | 16,000 ns (16 µs) |
| HDD random read | 2,000,000 ns (2 ms) |
| Network RTT (cross-continent) | 150,000,000 ns (150 ms) |

**Translation:** you win by (1) reducing the number of I/O operations, (2) reading/writing sequentially, (3) batching.

---

## The Decorator Pattern in Java I/O (The Real Model)

Java I/O is a pipeline. Each wrapper adds a capability.

### Common pipeline shapes

```java
// Read text file line-by-line (explicit charset)
try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
    for (String line; (line = reader.readLine()) != null; ) {
        // parse line
    }
}

// Read binary structured data
try (DataInputStream in = new DataInputStream(
        new BufferedInputStream(new FileInputStream("data.bin")))) {
    int version = in.readInt();
    long ts = in.readLong();
}

// Write text with good performance
try (BufferedWriter out = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
    out.write("header");
    out.newLine();
}
```

### Best practice (composition order)
- **Source first** (`FileInputStream`, socket stream)
- **Buffer next** (`BufferedInputStream` / `BufferedReader`)
- **Interpretation last** (`DataInputStream`, `InputStreamReader`, etc.)

---

## Byte Streams (InputStream / OutputStream)

Use byte streams for **binary**: images, compressed files, encrypted blobs, checksums.

### Two correct ways to read bytes

**A) Chunked reads with your own buffer (great for copy/transform):**

```java
public static void copy(InputStream in, OutputStream out) throws IOException {
    byte[] buf = new byte[64 * 1024]; // 64KB common sweet spot
    for (int n; (n = in.read(buf)) != -1; ) {
        out.write(buf, 0, n);
    }
}
```

**B) Buffered stream + byte-at-a-time convenience (fine for simple parsing):**

```java
try (InputStream in = new BufferedInputStream(new FileInputStream("data.bin"))) {
    int b;
    while ((b = in.read()) != -1) {
        // simple state machine
    }
}
```

### Common issues & best practices
- **Never assume `read(buf)` fills the buffer**: always use the returned `n`.
- **Never write `out.write(buf)` unless buf is full**: use `out.write(buf, 0, n)`.
- **Close always**: try-with-resources is non-negotiable for files/sockets.

---

## Character Streams (Reader / Writer)

Use character streams for text. Text = bytes + encoding + line rules.

### Always control the charset

```java
// BAD: default charset depends on OS and locale
try (BufferedReader r = new BufferedReader(new FileReader("data.txt"))) { }

// GOOD: explicit UTF-8
try (BufferedReader r = Files.newBufferedReader(Path.of("data.txt"), StandardCharsets.UTF_8)) { }
```

### Common text-reading patterns

**1) Line-by-line (logs, CSV, NDJSON):**

```java
try (BufferedReader r = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
    for (String line; (line = r.readLine()) != null; ) {
        // parse line
    }
}
```

**2) Fixed-size char buffer (large text transforms):**

```java
try (Reader r = Files.newBufferedReader(inPath, StandardCharsets.UTF_8);
     Writer w = Files.newBufferedWriter(outPath, StandardCharsets.UTF_8)) {
    char[] buf = new char[32 * 1024];
    for (int n; (n = r.read(buf)) != -1; ) {
        w.write(buf, 0, n);
    }
}
```

### PrintWriter trap (silent failures)

`PrintWriter` does **not** throw `IOException`. It can silently fail (e.g., disk full).

```java
try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8))) {
    pw.println("hello");
    if (pw.checkError()) {
        throw new IOException("Write failed (PrintWriter swallowed an error)");
    }
}
```

**Best practice:** prefer `BufferedWriter` unless you really need PrintWriter formatting.

---

## Buffering (How to Choose Buffer Sizes)

- Default buffers are often 8KB.
- For large sequential reads/writes: **32KB–256KB** is often better.
- Measure if performance matters; don’t guess.

---

## Safe Writing in Production (Atomicity & Durability)

Real systems must handle partial writes, crashes mid-write, and concurrent readers.

### Pattern: write to temp file → atomic move

```java
public static void atomicWrite(Path target, byte[] data) throws IOException {
    Path dir = target.getParent();
    if (dir == null) throw new IllegalArgumentException("Target must have a parent directory");
    Files.createDirectories(dir);

    Path tmp = Files.createTempFile(dir, target.getFileName().toString(), ".tmp");
    try {
        Files.write(tmp, data, StandardOpenOption.TRUNCATE_EXISTING);
        Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    } catch (AtomicMoveNotSupportedException e) {
        // Fallback when ATOMIC_MOVE isn't available
        Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
    } finally {
        Files.deleteIfExists(tmp);
    }
}
```

**Why it matters:** readers never see a half-written file.

---

# Part III — NIO.2 (Modern File I/O) — Deep Dive

NIO.2 gives you a modern file-system API (`Path`, `Files`) and high-performance primitives (`FileChannel`, `ByteBuffer`). In large-data systems, NIO is about **streaming**, **controlling memory**, and **safe file lifecycle**.

## The Path API (Practical Usage + Pitfalls)

`Path` is more than a string. It’s an object model for file system paths, with semantics for resolve/normalize/real paths.

### Creating paths

```java
// The standard way (Java 11+)
Path path = Path.of("data", "users", "alice.json");        // data/users/alice.json
Path abs = Path.of("C:", "Projects", "myapp", "config.yml"); // C:\Projects\myapp\config.yml

// From URI
Path fromUri = Path.of(URI.create("file:///home/user/data.txt"));

// Pre-Java 11
Path old = Paths.get("data", "users", "alice.json");
```

### Path operations (and gotchas)

```java
Path path = Path.of("/home/user/projects/myapp/src/Main.java");

// Decomposing
path.getFileName();    // Main.java
path.getParent();      // /home/user/projects/myapp/src
path.getRoot();        // /
path.getNameCount();   // 6 (home, user, projects, myapp, src, Main.java)
path.getName(2);       // projects (0-indexed)

// Resolving (combining paths)
Path base = Path.of("/home/user");
Path resolved = base.resolve("documents/report.pdf");
// → /home/user/documents/report.pdf
System.out.println(base.resolve("/tmp/x")); // /tmp/x (absolute replaces base!)

Path partial = Path.of("src/main");
Path full = partial.resolve("java/App.java");
// → src/main/java/App.java

// Relativizing (finding the relative path between two paths)
Path from = Path.of("/home/user/projects");
Path to = Path.of("/home/user/documents/report.pdf");
Path relative = from.relativize(to);
// → ../documents/report.pdf

// Normalizing (removing . and ..)
Path messy = Path.of("/home/user/./projects/../documents/./report.pdf");
Path clean = messy.normalize();
// → /home/user/documents/report.pdf

// Converting to absolute
Path rel = Path.of("config.yml");
Path absolute = rel.toAbsolutePath();
// → /current/working/directory/config.yml

// Converting to real path (resolves symlinks, must exist)
Path real = path.toRealPath();
```

---

## The Files Utility Class (Correct Patterns)

`java.nio.file.Files` is a treasure trove of static methods that make file operations one-liners:

### Reading files (choose the correct API)

```java
// Read entire file as a String (small files only!)
String content = Files.readString(Path.of("config.json"));

// Read all lines into a List
List<String> lines = Files.readAllLines(Path.of("data.csv"));

// Read all bytes
byte[] bytes = Files.readAllBytes(Path.of("image.png"));

// Stream lines lazily — ideal for large files (MUST be closed)
try (Stream<String> lines = Files.lines(Path.of("huge-log.txt"), StandardCharsets.UTF_8)) {
    long errorCount = lines.filter(line -> line.contains("ERROR")).count();
}
```

### Writing files (safe defaults)

```java
// Write a string to a file (creates or overwrites)
Files.writeString(Path.of("output.txt"), "Hello, World!\n");

// Write lines
Files.write(Path.of("names.txt"), List.of("Alice", "Bob", "Charlie"));

// Append to an existing file
Files.writeString(Path.of("log.txt"), "New entry\n",
    StandardOpenOption.APPEND, StandardOpenOption.CREATE);

// Write bytes
Files.write(Path.of("data.bin"), byteArray);
```

### File Operations

```java
// Copy
Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

// Move (atomic on most file systems)
Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);

// Delete
Files.delete(path);                   // throws if not exists
Files.deleteIfExists(path);           // returns false if not exists

// Create directories
Files.createDirectory(Path.of("output"));        // parent must exist
Files.createDirectories(Path.of("a/b/c/d"));     // creates all missing parents

// Create temp file/directory
Path tempFile = Files.createTempFile("prefix-", ".tmp");
Path tempDir = Files.createTempDirectory("myapp-");

// Check file properties
Files.exists(path);
Files.isRegularFile(path);
Files.isDirectory(path);
Files.isReadable(path);
Files.isWritable(path);
Files.size(path);                     // file size in bytes
Files.getLastModifiedTime(path);
```

### Walking directory trees (don’t leak streams)

```java
// List immediate children of a directory
try (Stream<Path> entries = Files.list(Path.of("src"))) {
    entries.forEach(System.out::println);
}

// Walk entire tree recursively (MUST be closed)
try (Stream<Path> tree = Files.walk(Path.of("project"))) {
    List<Path> javaFiles = tree
        .filter(p -> p.toString().endsWith(".java"))
        .toList();
}

// Walk with depth limit
try (Stream<Path> tree = Files.walk(Path.of("project"), 2)) {
    tree.forEach(System.out::println);
}

// Find files matching a condition
try (Stream<Path> found = Files.find(Path.of("project"), Integer.MAX_VALUE,
        (path, attrs) -> attrs.isRegularFile() && path.toString().endsWith(".java"))) {
    found.forEach(System.out::println);
}
```

### Real-world example — processing all CSVs in a directory (streaming)

```java
public Map<String, Long> countRecordsInAllCsvs(Path directory) throws IOException {
    Map<String, Long> counts = new LinkedHashMap<>();

    try (Stream<Path> csvFiles = Files.list(directory)
            .filter(p -> p.toString().endsWith(".csv"))) {

        for (Path csv : csvFiles.toList()) { // snapshot list; ok for small directory sizes
            try (Stream<String> lines = Files.lines(csv, StandardCharsets.UTF_8)) {
                long recordCount = lines.count() - 1; // subtract header
                counts.put(csv.getFileName().toString(), recordCount);
            }
        }
    }
    return counts;
}
```

---

## Channels & Buffers (Production-Level Understanding)

Channels and Buffers are the low-level NIO primitives for high-performance I/O. Think of a Channel as a connection to a data source and a Buffer as a fixed-size container you fill and drain.

### ByteBuffer — The Core Buffer

```java
// Creating buffers
ByteBuffer heapBuffer = ByteBuffer.allocate(1024);         // JVM heap memory
ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024);  // OS-native memory (faster for I/O)
ByteBuffer wrapped = ByteBuffer.wrap(existingByteArray);    // wraps existing array

// Buffer has four key properties:
// capacity: total size (fixed at creation)
// limit:    highest index that can be read/written
// position: current read/write index
// mark:     a saved position (optional)
//
// Invariant: 0 ≤ mark ≤ position ≤ limit ≤ capacity
```

### The flip/clear/compact Cycle

This is the single most confusing aspect of NIO. Understanding it is essential:

```
WRITE MODE (after allocate or clear):
┌───┬───┬───┬───┬───┬───┬───┬───┐
│   │   │   │   │   │   │   │   │  capacity = 8
└───┴───┴───┴───┴───┴───┴───┴───┘
 ↑                               ↑
 position=0                      limit=capacity=8
 "I'll write starting here"      "I can write up to here"

After writing 5 bytes: buffer.put(data)
┌───┬───┬───┬───┬───┬───┬───┬───┐
│ A │ B │ C │ D │ E │   │   │   │
└───┴───┴───┴───┴───┴───┴───┴───┘
                     ↑           ↑
                     position=5  limit=8

After flip() — switch to READ MODE:
┌───┬───┬───┬───┬───┬───┬───┬───┐
│ A │ B │ C │ D │ E │   │   │   │
└───┴───┴───┴───┴───┴───┴───┴───┘
 ↑                   ↑
 position=0          limit=5
 "Read from here"    "Read up to here"

After reading 3 bytes: buffer.get() × 3
┌───┬───┬───┬───┬───┬───┬───┬───┐
│ A │ B │ C │ D │ E │   │   │   │
└───┴───┴───┴───┴───┴───┴───┴───┘
             ↑       ↑
             pos=3   limit=5

After clear() — reset to WRITE MODE (data not erased, just ignored):
┌───┬───┬───┬───┬───┬───┬───┬───┐
│ A │ B │ C │ D │ E │   │   │   │
└───┴───┴───┴───┴───┴───┴───┴───┘
 ↑                               ↑
 position=0                      limit=capacity=8

After compact() — preserves unread data, shifts to start:
┌───┬───┬───┬───┬───┬───┬───┬───┐
│ D │ E │   │   │   │   │   │   │
└───┴───┴───┴───┴───┴───┴───┴───┘
         ↑                       ↑
         position=2              limit=capacity=8
```

**Summary:**
- **`flip()`** — switch from write mode to read mode. Sets `limit = position`, `position = 0`.
- **`clear()`** — reset to write mode. Sets `position = 0`, `limit = capacity`. Unread data is lost.
- **`compact()`** — preserve unread data, shift to start, switch to write mode.

### FileChannel — High-Performance File I/O

```java
// Reading with FileChannel
try (FileChannel channel = FileChannel.open(Path.of("data.bin"), StandardOpenOption.READ)) {
    ByteBuffer buffer = ByteBuffer.allocate(8192);

    while (channel.read(buffer) != -1) {
        buffer.flip();           // switch to read mode
        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            // process byte
        }
        buffer.clear();          // switch back to write mode
    }
}

// Writing with FileChannel
try (FileChannel channel = FileChannel.open(Path.of("output.bin"),
        StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
    ByteBuffer buffer = ByteBuffer.allocate(8192);

    for (byte[] data : dataChunks) {
        buffer.clear();
        buffer.put(data);
        buffer.flip();           // switch to read mode for the channel to read from
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }
}

// Transferring between channels (often OS-level zero-copy; loop handles partial transfers)
try (FileChannel source = FileChannel.open(Path.of("input.bin"), StandardOpenOption.READ);
     FileChannel dest = FileChannel.open(Path.of("output.bin"),
         StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

    long size = source.size();
    long pos = 0;
    while (pos < size) {
        pos += source.transferTo(pos, size - pos, dest);
    }
}
```

### Heap vs Direct Buffers

| Feature | Heap Buffer (`allocate`) | Direct Buffer (`allocateDirect`) |
|---|---|---|
| Memory location | JVM heap (garbage-collected) | OS native memory |
| Allocation speed | Fast | Slow (system call) |
| I/O performance | Slightly slower (JVM copies to native buffer first) | Faster (no intermediate copy) |
| GC impact | Normal GC pressure | No GC pressure on heap, but native memory can leak |
| Use case | Short-lived, small buffers | Long-lived, large I/O operations |

**Rule of thumb:** Use heap buffers by default. Use direct buffers for long-lived buffers that are used repeatedly for I/O (like a server's read buffer).

---

## Memory-Mapped Files

Memory-mapped files allow you to treat a file as if it were an array in memory. The OS handles paging data in and out — you just read/write bytes at offsets.

```java
// Map entire file into memory
try (FileChannel channel = FileChannel.open(Path.of("huge-data.bin"), StandardOpenOption.READ)) {
    MappedByteBuffer mapped = channel.map(
        FileChannel.MapMode.READ_ONLY,
        0,                    // offset
        channel.size()        // length
    );

    // Now you can access any byte instantly — the OS pages it in on demand
    byte firstByte = mapped.get(0);
    byte lastByte = mapped.get((int) channel.size() - 1);

    // No explicit read calls — the OS handles it via virtual memory
    // This is the fastest way to read large files that you access randomly
}
```

**When to use memory-mapped files:**
- Random access to very large files (databases, indexes)
- Files that multiple processes need to share
- Read-only access to files larger than available RAM (the OS pages on demand)

**When NOT to use:**
- Sequential reads of files — `BufferedInputStream` or `Files.lines()` are simpler and just as fast
- Small files — the overhead of mapping isn't worth it
- Files you need to close reliably — `MappedByteBuffer` doesn't have a `close()` method; the mapping persists until GC

---

## WatchService — Reacting to File System Changes

`WatchService` lets your application react to file system events: files created, modified, or deleted in a directory.

### Basic Usage

```java
public class DirectoryWatcher {

    public void watch(Path directory) throws IOException, InterruptedException {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            directory.register(watcher,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
            );

            System.out.println("Watching: " + directory);

            while (true) {
                WatchKey key = watcher.take();  // blocks until an event occurs

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        System.out.println("Event overflow — some events may have been lost");
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                    Path fileName = pathEvent.context();
                    Path fullPath = directory.resolve(fileName);

                    System.out.printf("[%s] %s%n", kind.name(), fullPath);

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE
                            && fileName.toString().endsWith(".csv")) {
                        System.out.println("  → New CSV detected! Processing...");
                        processCsvFile(fullPath);
                    }
                }

                boolean valid = key.reset();  // MUST reset — otherwise no more events
                if (!valid) {
                    System.out.println("Watch key no longer valid — directory may have been deleted");
                    break;
                }
            }
        }
    }

    private void processCsvFile(Path file) {
        // ... handle the new file ...
    }
}
```

### Practical Concerns

1. **Event coalescing:** If a file is modified rapidly (e.g., a text editor saving multiple times), you may receive only one `ENTRY_MODIFY` event. Don't assume one event per write.

2. **OVERFLOW:** If events arrive faster than you process them, some are dropped and you receive an `OVERFLOW` event. Handle this by re-scanning the directory.

3. **Not recursive:** `WatchService` only watches the registered directory, not its subdirectories. To watch a tree, register each subdirectory individually and register new subdirectories as they're created.

4. **OS-level differences:** On Linux, `WatchService` uses `inotify` (efficient, event-driven). On macOS, it falls back to polling (slower, higher CPU). On Windows, it uses `ReadDirectoryChangesW` (efficient).

---

## Large Data Systems: How NIO Is Used in the Real World

Large-file / large-data processing is not “read file, parse, done”. It’s a workflow:
- avoid partial reads of in-progress files
- prevent double-processing
- keep memory constant
- checkpoint so you can resume after a crash
- write outputs atomically

### Standard ingestion lifecycle (inbox → processing → archive/error)

```
Producer writes file.tmp → rename to file.csv (signals complete)
  ↓
Inbox directory (WatchService or scheduled scan)
  ↓
Atomic move to processing/ (claims file so only one worker processes it)
  ↓
Stream parse + validate + batch outputs
  ↓
On success: move to archive/YYYY/MM/DD/
On failure: move to error/ and emit a .error report
```

### Claiming files (idempotency boundary)

```java
public static Path claimFile(Path inboxFile, Path processingDir) throws IOException {
    Files.createDirectories(processingDir);
    Path claimed = processingDir.resolve(inboxFile.getFileName().toString());
    try {
        return Files.move(inboxFile, claimed, StandardCopyOption.ATOMIC_MOVE);
    } catch (AtomicMoveNotSupportedException e) {
        return Files.move(inboxFile, claimed);
    }
}
```

### File created ≠ file ready (WatchService pitfall)

Best practice is a “finalization protocol”:
- write to `*.tmp`, then rename to final name
- or write data file, then write a `.done` marker file

If you can’t control producers, you can wait for stable size:

```java
public static void waitUntilStable(Path file, Duration stableFor, Duration timeout)
        throws IOException, InterruptedException {
    long deadline = System.nanoTime() + timeout.toNanos();
    long stableNanos = stableFor.toNanos();

    long lastSize = -1;
    long lastChange = System.nanoTime();

    while (System.nanoTime() < deadline) {
        long size = Files.size(file);
        if (size != lastSize) {
            lastSize = size;
            lastChange = System.nanoTime();
        } else if (System.nanoTime() - lastChange >= stableNanos) {
            return;
        }
        Thread.sleep(200);
    }
    throw new IOException("File never became stable in time: " + file);
}
```

### Streaming parse + batching (constant memory)

```java
public static void processLargeTextFile(Path file) throws IOException {
    try (BufferedReader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
        List<String> batch = new ArrayList<>(10_000);
        for (String line; (line = r.readLine()) != null; ) {
            if (line.isBlank()) continue;
            batch.add(line);
            if (batch.size() == 10_000) {
                writeBatch(batch); // DB batch insert, Kafka batch publish, etc.
                batch.clear();
            }
        }
        if (!batch.isEmpty()) writeBatch(batch);
    }
}
```

### Checkpointing (resume after crash)

Simple approach:
- persist last processed byte offset (or line number) in a small checkpoint file
- resume with `FileChannel.position(offset)`

```java
try (FileChannel ch = FileChannel.open(file, StandardOpenOption.READ)) {
    long offset = loadCheckpoint(file);
    ch.position(offset);
    ByteBuffer buf = ByteBuffer.allocateDirect(128 * 1024);
    while (ch.read(buf) != -1) {
        buf.flip();
        // parse bytes; after a successful batch, saveCheckpoint(file, ch.position())
        buf.clear();
    }
}
```

### Backpressure (don’t OOM if downstream is slower)

Parsing a file can be faster than writing to a DB. Use bounded queues and block when full:
- 1 reader thread → N worker threads
- `BlockingQueue` with fixed capacity

---

## NIO vs Classic I/O — When to Use Which

| Task | Recommended API | Why |
|---|---|---|
| Read a small text file (< 1 MB) | `Files.readString(path)` | One line, returns a String |
| Read a large text file line by line | `Files.lines(path)` or `Files.newBufferedReader(path)` | Lazy stream, constant memory |
| Read all lines into a List | `Files.readAllLines(path)` | Simple, but loads everything into memory |
| Write a string to a file | `Files.writeString(path, content)` | One line |
| Copy a file | `Files.copy(source, target)` | OS-optimized, handles large files |
| Walk a directory tree | `Files.walk(path)` | Lazy stream, doesn't load the entire tree |
| Watch for file changes | `WatchService` | Event-driven, efficient |
| High-performance binary I/O | `FileChannel` + `ByteBuffer` | Direct OS I/O, zero-copy transfers |
| Random access to huge files | `MappedByteBuffer` | Virtual memory mapping |
| Network I/O | NIO `SocketChannel` | Non-blocking, selector-based |
| Simple sequential file read/write | `BufferedReader` / `BufferedWriter` | Simple, well-understood, fast enough |

**The general rule:** Use `Files` utility methods for simple operations. Use `FileChannel` + `ByteBuffer` when you need maximum performance or random access. Use classic `BufferedReader`/`BufferedWriter` when you're processing text line by line and don't need NIO features.

---

## Appendix — Original I/O & NIO Content (Kept for Quick Review)

This appendix keeps the earlier “quick reference” version of the I/O + NIO content. Use it as a fast recap after you complete the deep dive.

### Classic I/O — Quick Reference

#### Why I/O Matters

Almost every meaningful program reads from or writes to something — files, sockets, databases, the console. I/O is where your program meets the physical world, and the physical world is slow:

| Operation | Approximate Latency |
|---|---|
| L1 cache reference | 1 ns |
| L2 cache reference | 4 ns |
| RAM access | 100 ns |
| SSD random read | 16,000 ns (16 µs) |
| HDD random read | 2,000,000 ns (2 ms) |
| Network round-trip (same datacenter) | 500,000 ns (0.5 ms) |
| Network round-trip (cross-continent) | 150,000,000 ns (150 ms) |

Reading from disk is **160,000x slower** than reading from RAM. Reading from across the internet is **1.5 billion times slower** than an L1 cache hit. This is why buffering, efficient I/O patterns, and understanding the stream abstractions matter so much.

---

#### The Decorator Pattern in Java I/O

Java's I/O library is one of the most famous examples of the **Decorator pattern** in practice. Instead of having `BufferedFileInputStreamWithEncoding`, you compose capabilities by wrapping streams:

```java
// Each layer adds a capability
InputStream raw  = new FileInputStream("data.bin");           // reads bytes from a file
InputStream buf  = new BufferedInputStream(raw);              // adds buffering (8 KB default)
DataInputStream data = new DataInputStream(buf);              // adds ability to read int, double, etc.

// You build the pipeline: File → Buffer → Data interpretation
int value = data.readInt();     // reads 4 bytes, buffered, from a file
String text = data.readUTF();   // reads a UTF-8 string, buffered, from a file
```

Mix and match as needed:

```text
Reading text from a file:
  FileInputStream → InputStreamReader(UTF-8) → BufferedReader

Reading binary data from a network socket:
  socket.getInputStream() → BufferedInputStream → DataInputStream

Reading objects from a file:
  FileInputStream → BufferedInputStream → ObjectInputStream
```

---

#### Byte Streams — Base Classes (and what each one is for)

```text
InputStream (abstract)                    OutputStream (abstract)
├── FileInputStream                       ├── FileOutputStream
├── ByteArrayInputStream                  ├── ByteArrayOutputStream
├── BufferedInputStream                   ├── BufferedOutputStream
├── DataInputStream                       ├── DataOutputStream
├── ObjectInputStream                     ├── ObjectOutputStream
└── FilterInputStream                     └── FilterOutputStream
```

Below is what **each API** does, typical use cases, and a small example.

##### `InputStream` (abstract)

- **What it is**: the base type for *reading bytes*.
- **Key methods**: `int read()`, `int read(byte[])`, `int read(byte[], int, int)`, `long skip(long)`, `int available()`, `void close()`.
- **Typical use**: treat any binary source uniformly (file, network, memory).

```java
public static long countBytes(InputStream in) throws IOException {
    byte[] buf = new byte[8192];
    long total = 0;
    for (int n; (n = in.read(buf)) != -1; ) total += n;
    return total;
}
```

##### `OutputStream` (abstract)

- **What it is**: the base type for *writing bytes*.
- **Key methods**: `void write(int)`, `void write(byte[])`, `void write(byte[], int, int)`, `void flush()`, `void close()`.
- **Typical use**: treat any binary destination uniformly (file, network, memory).

```java
public static void writeHeader(OutputStream out) throws IOException {
    out.write(new byte[] { 'M', 'A', 'G', 'I', 'C' });
    out.flush();
}
```

##### `FileInputStream`

- **What it is**: reads raw bytes from a file on disk.
- **Common use**: images, zip files, binary protocols, checksums.
- **Common issue**: unbuffered reads can be very slow; wrap in `BufferedInputStream` for performance.

```java
try (InputStream in = new BufferedInputStream(new FileInputStream("photo.jpg"))) {
    byte[] buf = new byte[64 * 1024];
    while (in.read(buf) != -1) { /* process */ }
}
```

##### `FileOutputStream`

- **What it is**: writes raw bytes to a file on disk.
- **Common use**: generate binary outputs, copy files, write exports.
- **Common issue**: forgetting to close/flush means data may not be fully written.

```java
try (OutputStream out = new BufferedOutputStream(new FileOutputStream("out.bin"))) {
    out.write("hello".getBytes(StandardCharsets.UTF_8));
} // close() flushes buffered data
```

##### `ByteArrayInputStream`

- **What it is**: an `InputStream` that reads from a `byte[]` in memory.
- **Common use**: test code, parsing a byte array using stream-based APIs, adapting in-memory data to code that expects an InputStream.

```java
byte[] data = {0, 1, 2, 3, 4};
try (InputStream in = new ByteArrayInputStream(data)) {
    System.out.println(in.read()); // 0
}
```

##### `ByteArrayOutputStream`

- **What it is**: an `OutputStream` that writes into a growable in-memory byte buffer.
- **Common use**: build a `byte[]` result before writing/sending it; compress/encrypt into memory.
- **Best practice**: call `toByteArray()` once at the end; avoid converting repeatedly in loops.

```java
ByteArrayOutputStream baos = new ByteArrayOutputStream();
try (OutputStream out = new BufferedOutputStream(baos)) {
    out.write("A".getBytes(StandardCharsets.UTF_8));
    out.write("B".getBytes(StandardCharsets.UTF_8));
}
byte[] result = baos.toByteArray(); // [65, 66]
```

##### `BufferedInputStream`

- **What it is**: wraps an `InputStream` and buffers reads to reduce system calls.
- **Common use**: almost always wrap `FileInputStream` or socket streams.
- **Best practice**: consider larger buffers (e.g., 64KB) for huge sequential reads.

```java
try (InputStream in = new BufferedInputStream(new FileInputStream("big.bin"), 64 * 1024)) {
    while (in.read() != -1) { /* byte-at-a-time, still buffered */ }
}
```

##### `BufferedOutputStream`

- **What it is**: wraps an `OutputStream` and buffers writes.
- **Common use**: almost always wrap `FileOutputStream` for performance.
- **Common issue**: forgetting to close/flush means buffered data may not reach disk.

```java
try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("out.bin"))) {
    out.write(new byte[1024 * 1024]); // 1MB
    out.flush(); // optional here; close() will flush
}
```

##### `DataInputStream`

- **What it is**: reads Java primitive types (`int`, `long`, `double`, etc.) from an underlying `InputStream`.
- **Important**: uses **big-endian** byte order and a specific encoding for `readUTF()` (modified UTF-8).
- **Use case**: reading a binary file/protocol you control (versioned formats).
- **Common pitfall**: the writer must use matching `DataOutputStream` methods in the same order.

```java
// Writer side
try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("data.bin")))) {
    out.writeInt(1);          // version
    out.writeLong(123456789); // id
}

// Reader side
try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream("data.bin")))) {
    int version = in.readInt();
    long id = in.readLong();
}
```

##### `DataOutputStream`

- **What it is**: writes Java primitive types to an underlying `OutputStream`.
- **Use case**: create simple binary formats; interop between Java services.
- **Best practice**: include a **magic header + version** so you can evolve the format safely.

```java
try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("v1.bin")))) {
    out.writeInt(0xCAFEBABE); // magic
    out.writeInt(1);          // version
    out.writeDouble(3.14);
}
```

##### `ObjectInputStream`

- **What it is**: reads Java objects serialized with `ObjectOutputStream`.
- **Use case**: legacy systems, trusted internal caches, controlled environments.
- **Major warning**: deserializing untrusted bytes is a security risk (RCE). Prefer JSON.

```java
// Writing
try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("obj.bin"))) {
    out.writeObject(List.of("a", "b"));
}

// Reading (trusted only)
try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("obj.bin"))) {
    Object obj = in.readObject();
}
```

##### `ObjectOutputStream`

- **What it is**: writes Java objects to a stream in the Java serialization format.
- **Common issue**: class changes can break old data without careful `serialVersionUID` and versioning.

```java
try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("obj.bin")))) {
    out.writeObject(new HashMap<>(Map.of("k", "v")));
}
```

##### `FilterInputStream`

- **What it is**: a base class for “filtering” wrappers around an InputStream (forwards calls to an inner stream).
- **Use case**: build decorators that transform/observe bytes (counting, hashing, decrypting).

```java
class CountingInputStream extends FilterInputStream {
    private long count = 0;
    protected CountingInputStream(InputStream in) { super(in); }
    @Override public int read() throws IOException {
        int b = super.read();
        if (b != -1) count++;
        return b;
    }
    public long getCount() { return count; }
}
```

##### `FilterOutputStream`

- **What it is**: OutputStream equivalent of FilterInputStream; base class for wrappers that transform or observe writes.
- **Use case**: checksumming, encryption, throttling, byte counting.

```java
class CountingOutputStream extends FilterOutputStream {
    private long count = 0;
    protected CountingOutputStream(OutputStream out) { super(out); }
    @Override public void write(int b) throws IOException {
        super.write(b);
        count++;
    }
    public long getCount() { return count; }
}
```

---

### NIO.2 — Quick Reference

NIO.2 (New I/O 2), introduced in Java 7, provides a modern, powerful API for file system operations. It replaces most uses of `java.io.File` with `Path` and `Files`.

Key ideas:
- `Path` for paths
- `Files` for operations (read/write/copy/move/walk)
- `FileChannel` + `ByteBuffer` for high-performance binary I/O
- `WatchService` for directory watching


# Part IV — Serialization

## Java Serialization — Why It's Dangerous

Java serialization (`Serializable`) converts an object to a byte stream and back. It sounds convenient, but it's one of the most dangerous features in the Java platform.

### The Security Problem

Deserialization of untrusted data can execute arbitrary code. When Java deserializes an object, it calls the constructor of the serialized class and populates its fields. If the class has a `readObject()` method, that runs during deserialization. An attacker can craft a malicious byte stream that chains together existing library classes (a "gadget chain") to execute commands.

```java
// This innocent-looking code is a remote code execution vulnerability
// if 'untrustedData' comes from the network
ObjectInputStream ois = new ObjectInputStream(untrustedData);
Object obj = ois.readObject();  // DANGER — arbitrary code may execute here
```

Real-world attacks using Java serialization have compromised Apache Commons Collections, Spring, Jenkins, WebLogic, and many other systems.

### The Versioning Problem

```java
public class User implements Serializable {
    private String name;
    private String email;
    // Serialized to disk. Later you add a field:
    private int age;  // now deserialization of old data fails with InvalidClassException
}
```

If you don't explicitly declare `serialVersionUID`, Java generates one from the class structure. Adding a field, renaming a method — any change invalidates all previously serialized data.

```java
// Always declare this if you use Serializable
private static final long serialVersionUID = 1L;
// But this only avoids the automatic invalidation — you still have to handle
// the fact that old data doesn't have the new field
```

### The `transient` Keyword

Fields marked `transient` are excluded from serialization:

```java
public class UserSession implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private Instant loginTime;

    private transient String authToken;     // sensitive — don't persist
    private transient Connection dbConn;    // not serializable — would throw
}
```

---

## Safer Alternatives

Effective Java, Item 85: *"Prefer alternatives to Java serialization."*

### JSON (Jackson / Gson)

```java
// Jackson — the industry standard
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new JavaTimeModule());

// Serialize to JSON
String json = mapper.writeValueAsString(user);
// {"name":"Alice","email":"alice@example.com","age":30}

// Deserialize from JSON
User user = mapper.readValue(json, User.class);

// To/from file
mapper.writeValue(new File("user.json"), user);
User fromFile = mapper.readValue(new File("user.json"), User.class);
```

**Advantages over Java serialization:**
- Human-readable
- Language-independent (any language can read JSON)
- No arbitrary code execution on deserialization
- Easy to version (add fields with defaults, ignore unknown fields)
- Industry standard for APIs

### Records + JSON — The Modern Approach

```java
public record UserDto(String name, String email, int age) {}

// Jackson handles records natively
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(new UserDto("Alice", "alice@ex.com", 30));
UserDto user = mapper.readValue(json, UserDto.class);
```

### When Java Serialization Is Acceptable

- **Inter-JVM communication in a controlled environment** (same codebase, same versions, no untrusted data)
- **Caching libraries** (Hazelcast, Ehcache) that use serialization internally — but configure serialization filters
- **Legacy systems** — use deserialization filters (Java 9+) to whitelist allowed classes

```java
// Java 9+ deserialization filter
ObjectInputFilter filter = ObjectInputFilter.Config.createFilter(
    "com.myapp.**;!*"  // allow only my classes, reject everything else
);
ois.setObjectInputFilter(filter);
```

---

## Exercises Roadmap

| # | Exercise | Concepts Practiced | Difficulty |
|---|---|---|---|
| 1 | **Log Analyzer** | BufferedReader, NIO Files, custom exceptions, WatchService | ★★★☆☆ |
| 2 | **File Transformer** | CSV parsing, JSON output, try-with-resources, exception hierarchy, streaming | ★★★★☆ |
| 3 | **Config Manager** | Properties, type-safe getters, WatchService, custom exceptions, environment overrides | ★★★★☆ |

### Exercise Tips

**Exercise 1 (Log Analyzer):**
- Use `Files.lines()` for lazy reading — don't load 100 MB into memory
- Parse each line with a regex or `String.split()`. Invalid lines should throw `InvalidLogFormatException` with the line number and content
- Use a `Map<String, Long>` to count by level. Use `Map.merge(level, 1L, Long::sum)`
- For the WatchService bonus: register the directory, filter for `.log` files, and call your analyzer on each new file

**Exercise 2 (File Transformer):**
- Handle quoted CSV fields properly: `"Smith, Jr.",John,30` has 3 fields, not 4
- Build your exception hierarchy first: `TransformException` (abstract) → `ParseException`, `WriteException`, `UnsupportedFormatException`
- For streaming: read one line, transform it, write the output, then read the next. Don't collect all lines first
- For ZIP bonus: use `ZipInputStream` wrapped in a `BufferedInputStream`

**Exercise 3 (Config Manager):**
- `Properties.load()` for the base config. Then override with `System.getenv()` for each key
- For type-safe getters: `getInt("port")` should catch `NumberFormatException` and throw `TypeMismatchException` with the key, the raw value, and the target type
- The WatchService reload: register the config file's parent directory, filter for the config filename, and re-call `load()` when it changes

---

## Common Mistakes to Avoid

| Mistake | Consequence | Fix |
|---|---|---|
| Empty catch block `catch (Exception e) { }` | Bugs silently swallowed, data corruption | At minimum log the exception. Prefer re-throwing. |
| Catching `Exception` or `Throwable` | Hides programming errors (NPE, ClassCast) | Catch the specific exception type you expect |
| Not closing resources (no try-with-resources) | File handle leaks, connection pool exhaustion | Always use try-with-resources for `AutoCloseable` |
| Losing exception cause (no chaining) | Root cause lost, undebuggable production issues | Always pass original exception: `new MyException(msg, cause)` |
| Not specifying charset in I/O | Garbled text on different OS or locale | Always pass `StandardCharsets.UTF_8` explicitly |
| Reading entire large file into memory | `OutOfMemoryError` | Use `Files.lines()` or `BufferedReader` for streaming |
| Unbuffered byte-at-a-time reading | 100x slower than buffered | Always wrap in `BufferedInputStream` / `BufferedReader` |
| Return in `finally` block | Silently swallows exceptions | Never return from finally |
| Using `java.io.File` in new code | Missing features, worse API | Use `java.nio.file.Path` and `Files` |
| Java serialization for untrusted data | Remote code execution vulnerability | Use JSON (Jackson/Gson) instead |
| Ignoring `OVERFLOW` in WatchService | Missing file events silently | Handle OVERFLOW by re-scanning the directory |
| Forgetting `key.reset()` in WatchService | No more events after first batch | Always call `reset()` after processing events |
| Not calling `buffer.flip()` before reading | Reading garbage data from buffer | Remember: write → flip → read → clear/compact |

---

## Key Terms Glossary

| Term | Definition |
|---|---|
| **AutoCloseable** | Interface with `close()` method. Enables try-with-resources. All I/O resources implement this. |
| **Buffering** | Accumulating data in memory before performing I/O. Reduces system calls dramatically. |
| **ByteBuffer** | A fixed-size container for bytes with position/limit/capacity tracking. Core NIO primitive. |
| **Channel** | A connection to a data source (file, socket) that reads/writes `ByteBuffer`. NIO replacement for streams. |
| **Checked exception** | Exception that the compiler forces you to handle or declare. Subclass of `Exception` but not `RuntimeException`. |
| **Closeable** | Subinterface of `AutoCloseable` from `java.io`. Its `close()` throws `IOException` specifically. |
| **Compact** | Buffer operation that shifts unread data to position 0 and prepares for more writing. |
| **Decorator pattern** | Wrapping an object to add behavior. Java I/O uses this: `BufferedInputStream(FileInputStream)`. |
| **Direct buffer** | ByteBuffer backed by OS-native memory, bypassing the JVM heap for faster I/O. |
| **Exception chaining** | Wrapping a caught exception as the `cause` of a new exception to preserve the full failure story. |
| **Fail fast** | Validating inputs and preconditions at the beginning of a method, before doing any work. |
| **FileChannel** | A channel for reading/writing files. Supports memory mapping and channel-to-channel transfers. |
| **Flip** | Buffer operation that switches from write mode to read mode: sets limit=position, position=0. |
| **Heap buffer** | ByteBuffer backed by a JVM `byte[]` array on the heap. Subject to GC. |
| **MappedByteBuffer** | A ByteBuffer that maps a file region into memory. The OS handles paging. |
| **Memory-mapped file** | A file accessed through virtual memory mapping rather than read/write system calls. |
| **Mojibake** | Garbled text caused by encoding/decoding mismatch (e.g., UTF-8 data read as ISO-8859-1). |
| **Multi-catch** | Catching multiple exception types in one catch block: `catch (A \| B e)`. |
| **NIO.2** | The `java.nio.file` package introduced in Java 7. Modern replacement for `java.io.File`. |
| **Path** | Interface representing a file system path. Immutable and comparable. |
| **RuntimeException** | Unchecked exception. The compiler doesn't force handling. Typically indicates programming errors. |
| **serialVersionUID** | A version number for serialized classes. Prevents `InvalidClassException` when classes change. |
| **StandardCharsets** | Class with constants for common charsets: `UTF_8`, `ISO_8859_1`, `US_ASCII`. |
| **Suppressed exception** | An exception from `close()` that's attached to the primary exception via `addSuppressed()`. |
| **System call** | A request from user space to the OS kernel (e.g., to read bytes from disk). Expensive. |
| **Transient** | Keyword marking fields to be excluded from Java serialization. |
| **try-with-resources** | Java 7 syntax for automatic resource cleanup. Calls `close()` in reverse declaration order. |
| **Unchecked exception** | Exception not checked at compile time. Subclass of `RuntimeException`. |
| **WatchService** | NIO.2 API for monitoring directories for file system events (create, modify, delete). |
| **Zero-copy** | OS-level optimization where data transfers between channels without copying through user space. |

---

## Progress Tracker

Use this to track your study progress:

**Part I — Exception Handling**

- **Exception Hierarchy:** Drew the full hierarchy from memory; explained Error vs Exception vs RuntimeException
- **Stack Traces:** Read a production stack trace; identified the originating frame and root cause
- **Multi-catch:** Used `catch (A | B e)` syntax; understood when it applies
- **Checked vs Unchecked:** Applied the decision framework to 5 different scenarios
- **Custom Exceptions:** Built a 3-level exception hierarchy with chaining and context fields
- **try-with-resources:** Used with multiple resources; demonstrated suppressed exceptions
- **AutoCloseable:** Implemented a custom AutoCloseable class
- **Best Practices:** Reviewed code for all 6 anti-patterns (swallowing, broad catch, late validation, etc.)

**Part II — Classic I/O**

- **Decorator Pattern:** Explained how wrapping streams adds capabilities
- **Byte Streams:** Copied a file using byte-level I/O with buffering
- **Character Streams:** Read a text file line by line with explicit encoding
- **Buffering:** Ran the benchmark; measured the 100x+ difference between buffered and unbuffered
- **Encodings:** Wrote UTF-8, read with ISO-8859-1, observed the mojibake; fixed it

**Part III — NIO.2**

- **Path API:** Used resolve, relativize, normalize, toAbsolutePath
- **Files utility:** Used readString, readAllLines, lines, write, copy, walk, find, createTempFile
- **FileChannel:** Read and wrote a file using ByteBuffer with flip/clear cycle
- **ByteBuffer:** Explained position, limit, capacity; demonstrated flip, clear, compact
- **Direct vs Heap:** Explained when to use each type of buffer
- **Memory-Mapped Files:** Mapped a file and read random offsets without seek
- **WatchService:** Set up a watcher that detected file creation and processed new files
- **NIO vs Classic:** Chose the right API for 5 different scenarios

**Part IV — Serialization**

- **Serializable Dangers:** Explained why deserialization of untrusted data is a security vulnerability
- **serialVersionUID:** Explained what happens without it and why explicit is better
- **Transient Fields:** Used transient to exclude sensitive/non-serializable fields
- **JSON Alternative:** Serialized/deserialized an object with Jackson; compared to Serializable

**Exercises**

- **Exercise 1:** Log Analyzer — streams large files, handles bad lines, reports summary
- **Exercise 2:** File Transformer — CSV → JSON/XML with proper exception hierarchy
- **Exercise 3:** Config Manager — type-safe getters, env overrides, live reload
- **Self-Assessment:** Completed all checklist items in README.md

---

## What's Next

After completing this phase, you'll have the error handling and I/O fluency needed for:

- **Phase 05 (Concurrency & Multithreading):** Thread safety issues in I/O, `InterruptedException` handling, concurrent file access, `AsynchronousFileChannel`
- **Framework development:** Spring's `@ExceptionHandler`, exception translation in repositories, `@ControllerAdvice` — all build on the patterns you've learned here
- **Production systems:** Log processing, ETL pipelines, config management, file watching — these are bread-and-butter production tasks
- **Distributed systems:** Exception handling across service boundaries, serialization for message queues, file transfer protocols

**The bottom line:** Exception handling is how your program communicates failure. I/O is how your program communicates with the world. Master both, and you build software that survives the chaos of production environments — software that bends under pressure but doesn't break.
