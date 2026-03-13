# Phase 04 — Exception Handling, I/O & NIO

**Duration:** ~1–2 weeks · **Estimated effort:** 14 hours  
**Prerequisites:** Phase 03 (Collections & Generics)

---

## Learning Objectives

By the end of this phase you will be able to:

1. Design robust error-handling strategies using Java's exception hierarchy.
2. Decide when to use checked vs unchecked exceptions and articulate the trade-offs.
3. Create custom exception hierarchies for business domains.
4. Manage resources safely with try-with-resources and `AutoCloseable`.
5. Read and write data efficiently using classic I/O streams and the decorator pattern.
6. Leverage the NIO.2 API (`Path`, `Files`, `Channels`, `Buffers`) for modern file operations.
7. React to file-system changes in real time with `WatchService`.
8. Understand serialization pitfalls and evaluate alternatives (JSON, XML).

---

## Topics

### 1. Exception Hierarchy *(1 h)*

| Concept | Key Points |
|---|---|
| `Throwable` | Root of every throwable; carries message, cause, and stack trace. |
| `Error` | Irrecoverable JVM problems (`OutOfMemoryError`, `StackOverflowError`) — **do not catch**. |
| `Exception` | Recoverable conditions the application is expected to handle. |
| `RuntimeException` | Subclass of `Exception`; programming errors (NPE, IOOBE, IAE). |

Hierarchy:

```
Throwable
├── Error
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   └── ...
└── Exception
    ├── IOException          (checked)
    ├── SQLException         (checked)
    └── RuntimeException     (unchecked)
        ├── NullPointerException
        ├── IllegalArgumentException
        └── ...
```

- Stack-trace reading: how to identify the originating frame.
- Multi-catch blocks (`catch (IOException | ParseException e)`).
- `finally` guarantees and pitfalls (return in finally).

### 2. Checked vs Unchecked Exceptions *(1 h)*

| Aspect | Checked | Unchecked |
|---|---|---|
| Compile-time enforcement | Yes | No |
| Typical use | Recoverable I/O, network, parsing errors | Programming bugs, violated preconditions |
| Effect on API signature | Forces `throws` clause | Clean signatures |
| Effective Java guidance | Items 70–72 | Items 70–72 |

**Guidelines:**
- Use checked exceptions for conditions the caller can reasonably recover from.
- Use unchecked exceptions for programming errors (precondition violations).
- Never swallow exceptions silently; at minimum, log them.
- Prefer standard exceptions (`IllegalArgumentException`, `IllegalStateException`, `UnsupportedOperationException`).

### 3. Custom Exception Classes *(2 h)*

- When to create a custom exception vs reusing standard ones.
- Business exception pattern:

```java
public class OrderProcessingException extends RuntimeException {
    private final String orderId;
    public OrderProcessingException(String orderId, String message, Throwable cause) {
        super(message, cause);
        this.orderId = orderId;
    }
    public String getOrderId() { return orderId; }
}
```

- **Exception chaining** — always pass the original cause.
- `getCause()` and `initCause()`.
- Exception hierarchies for layered architectures:  
  `ServiceException` → `RepositoryException` → `DataAccessException`.
- Adding context to exceptions (error codes, affected entity IDs).

### 4. try-with-resources *(1 h)*

- The `AutoCloseable` / `Closeable` contract.
- Declaring multiple resources; close order (reverse).
- Suppressed exceptions (`getSuppressed()`).
- Resource management best practices:
  - Always prefer try-with-resources over manual `finally` blocks.
  - Custom `AutoCloseable` implementations (DB connections, locks, temp files).

### 5. Java I/O Streams *(3 h)*

| Layer | Byte-oriented | Character-oriented |
|---|---|---|
| Base | `InputStream` / `OutputStream` | `Reader` / `Writer` |
| File | `FileInputStream` | `FileReader` |
| Buffered | `BufferedInputStream` | `BufferedReader` |
| Data | `DataInputStream` | — |
| Object | `ObjectInputStream` | — |

- **Decorator pattern in I/O** — wrapping streams to add capabilities.
- Buffering: why `BufferedReader`/`BufferedWriter` matter for performance.
- Character encodings: `InputStreamReader(is, StandardCharsets.UTF_8)`.
- Reading strategies: line-by-line, fixed buffer, full slurp.
- `System.in` / `System.out` / `System.err`.
- `PrintWriter` vs `BufferedWriter`.

### 6. Java NIO.2 *(3 h)*

- **Path API** — `Path.of()`, resolve, relativize, normalize.
- **Files utility class** — `readAllLines`, `readString`, `write`, `copy`, `move`, `walk`, `find`, `list`.
- **Channels & Buffers** — `FileChannel`, `ByteBuffer`, direct vs heap buffers.
- **Memory-mapped files** — `FileChannel.map()` for ultra-fast large-file access.
- NIO vs classic I/O: when to use which.
- `StandardOpenOption` flags.

### 7. File Watching with WatchService *(1 h)*

- `WatchService` registration on a `Path`.
- `WatchEvent.Kind`: `ENTRY_CREATE`, `ENTRY_MODIFY`, `ENTRY_DELETE`, `OVERFLOW`.
- Polling loop pattern.
- Practical concerns: event coalescing, OS-level limitations.

### 8. Serialization *(2 h)*

| Mechanism | Pros | Cons |
|---|---|---|
| `Serializable` | Zero-config for simple cases | Fragile, security risks, tight coupling |
| `Externalizable` | Full control over format | Verbose, manual maintenance |
| JSON (Gson/Jackson) | Readable, interoperable | Slightly slower, no type info |
| XML (JAXB) | Schema validation, mature tooling | Verbose, heavy |

- `serialVersionUID` — why explicit is better.
- Deserialization attacks — untrusted data can execute arbitrary code.
- Effective Java Item 85: *"Prefer alternatives to Java serialization."*
- Transient fields, custom `readObject`/`writeObject`.

---

## References

| Resource | Description |
|---|---|
| [Oracle I/O Tutorial](https://docs.oracle.com/javase/tutorial/essential/io/) | Official guide to I/O and NIO |
| Effective Java, Items 69–77 | Exception best practices |
| [NIO.2 API (java.nio.file)](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/nio/file/package-summary.html) | Path, Files, WatchService |
| Effective Java, Item 85 | Prefer alternatives to Java serialization |

---

## Exercises

### Exercise 1 — Log Analyzer

**Goal:** Read large log files (100 MB+) efficiently and produce a summary report.

**Requirements:**

1. Parse log entries with format: `[TIMESTAMP] LEVEL — message`.
2. Filter by level (`ERROR`, `WARN`, `INFO`, `DEBUG`) and date range.
3. Generate a summary: count by level, top-10 error messages.
4. Use NIO / `BufferedReader` for efficient large-file reading.
5. Custom exceptions:
   - `InvalidLogFormatException` — malformed log line.
   - `LogFileNotFoundException` — file does not exist or is unreadable.
6. **Bonus:** Watch a directory for new `.log` files and analyze them automatically.

**Starter:** `exercises/src/main/java/exercises/LogAnalyzer.java`

---

### Exercise 2 — File Transformer

**Goal:** Read CSV files and transform them to JSON and XML.

**Requirements:**

1. Parse CSV (handle quoted fields, commas inside quotes).
2. Output JSON (using Gson) and XML.
3. Handle encodings: UTF-8, ISO-8859-1.
4. Use try-with-resources everywhere.
5. Custom exception hierarchy:
   - `TransformException` (base)
     - `ParseException` — bad input data.
     - `WriteException` — output failure.
     - `UnsupportedFormatException` — unknown target format.
6. Streaming for large files — do not load everything into memory.
7. **Bonus:** Support reading CSV from ZIP archives (`java.util.zip`).

**Starter:** `exercises/src/main/java/exercises/FileTransformer.java`

---

### Exercise 3 — Config Manager

**Goal:** Properties-based configuration with type-safe access and live reload.

**Requirements:**

1. Load from a `.properties` file.
2. Environment variable override (env takes precedence).
3. Default values for every key.
4. Type-safe getters: `getInt`, `getBoolean`, `getString`, `getList`.
5. File watching: auto-reload when config changes on disk.
6. Custom exceptions:
   - `MissingRequiredKeyException` — key not found and no default.
   - `TypeMismatchException` — value cannot be converted.
7. **Bonus:** Support YAML-like nested configurations (dot-separated keys → tree).

**Starter:** `exercises/src/main/java/exercises/ConfigManager.java`

---

## Self-Assessment Checklist

Before moving to Phase 05, confirm you can:

- [ ] Explain the full `Throwable` hierarchy and when each branch applies.
- [ ] Justify choosing checked vs unchecked for a given scenario.
- [ ] Write a custom exception hierarchy with chaining and context.
- [ ] Use try-with-resources with multiple resources; explain close order and suppressed exceptions.
- [ ] Explain the decorator pattern as it appears in Java I/O.
- [ ] Read a 500 MB file without running out of memory.
- [ ] Use `Path`, `Files.walk`, `Files.readString`, and `Files.copy`.
- [ ] Create a `ByteBuffer`, read/write via `FileChannel`, and explain flip/clear/compact.
- [ ] Set up a `WatchService` that reacts to file creation/modification.
- [ ] Explain why Java serialization is dangerous and name two safer alternatives.
- [ ] Complete all three exercises with passing tests.

---

*Next → [Phase 05 — Concurrency & Multithreading](../phase-05-concurrency/README.md)*
