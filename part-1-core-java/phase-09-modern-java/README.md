# Phase 09 — Modern Java (9–21+)

**Duration:** ~2 weeks · **Total effort:** ~18 hours

---

## Learning Objectives

By the end of this phase you will be able to:

1. Use **Java Platform Module System (JPMS)** to define module boundaries.
2. Apply **local variable type inference** (`var`) appropriately.
3. Write concise code with **switch expressions**, **text blocks**, and **pattern matching**.
4. Model data with **records** and restrict hierarchies with **sealed classes**.
5. Leverage **virtual threads** and **structured concurrency** for scalable I/O.
6. Incrementally adopt modern features in an existing Java 8/11 codebase.

---

## Topics by Java Version

### Java 9 (3 h + 1 h)

#### Module System — JPMS (3 h)

- `module-info.java`: `module`, `requires`, `exports`, `opens`, `provides … with`, `uses`.
- Automatic modules and the unnamed module for gradual migration.
- Services: `ServiceLoader` with `provides` and `uses` directives.
- Strong encapsulation: why internal packages are no longer accessible.
- Practical: modularising a small multi-module project.

#### Private Interface Methods & JShell (1 h)

- Private and private static methods in interfaces — reducing code duplication in default methods.
- JShell for experimentation: evaluating expressions, testing snippets, exploring APIs.

### Java 10–11 (2 h)

| Feature | Notes |
|---------|-------|
| `var` (local variable type inference) | When to use: local variables with obvious types. When *not*: public API signatures, unclear types. |
| `HttpClient` API | Replaces `HttpURLConnection`. Sync and async requests, HTTP/2 support, `BodyHandlers`. |
| `String` methods | `isBlank()`, `strip()` / `stripLeading()` / `stripTrailing()`, `lines()`, `repeat(n)`. |
| `Optional.ifPresentOrElse`, `Optional.stream` | Bridging Optional and Stream worlds. |
| Immutable collection factories | `List.copyOf()`, `Map.copyOf()`, `Set.copyOf()`. |

### Java 12–14 (2 h)

| Feature | Notes |
|---------|-------|
| **Switch expressions** | Arrow syntax `case X ->`, `yield` for block returns, exhaustiveness checking. |
| **Text blocks** | Triple-quote `"""` strings, automatic indentation stripping, `\` line continuation. |
| **Helpful NullPointerExceptions** | `-XX:+ShowCodeDetailsInExceptionMessages` — pinpoints which reference was null. |

### Java 15–16 (3 h)

| Feature | Notes |
|---------|-------|
| **Sealed classes** | `sealed`, `permits`, `non-sealed`. Restricting who can extend/implement. Exhaustive switch over sealed hierarchy. |
| **Records** | Compact constructors, custom accessor methods, implementing interfaces. Records vs Lombok `@Value`. |
| **Pattern matching for `instanceof`** | `if (obj instanceof String s)` — eliminates explicit cast. Flow-scoping rules. |

### Java 17 — LTS (2 h)

- Consolidation of all features since Java 11 in one production-ready release.
- Migration considerations: removed modules (Java EE, CORBA), `--illegal-access=deny` by default.
- Recommended upgrade path from Java 8 → 11 → 17.
- Toolchain updates: Maven, Gradle, IDE compatibility.

### Java 18–20 (2 h)

| Feature | Notes |
|---------|-------|
| **Pattern matching for `switch`** (preview → final stages) | Switch over types, guarded patterns (`case String s when s.length() > 5`). |
| **Record patterns** | Destructuring records in `instanceof` and `switch`: `case Point(int x, int y)`. |
| Simple web server (`jwebserver`) | Quick static file serving for development. |
| UTF-8 by default | `Charset.defaultCharset()` is now UTF-8 on all platforms. |

### Java 21 — LTS (4 h)

| Feature | Notes |
|---------|-------|
| **Virtual threads** (Project Loom) | `Thread.ofVirtual().start(...)`, `Executors.newVirtualThreadPerTaskExecutor()`. Scalable I/O without reactive frameworks. |
| **Structured concurrency** (preview) | `StructuredTaskScope.ShutdownOnFailure`, `ShutdownOnSuccess`. Fan-out/fan-in with automatic cancellation. |
| **Scoped values** (preview) | Replacement for `ThreadLocal` in virtual-thread world. |
| **Sequenced collections** | `SequencedCollection`, `SequencedSet`, `SequencedMap` — `getFirst()`, `getLast()`, `reversed()`. |
| **Pattern matching finalised** | Switch patterns, record patterns, and guarded patterns are stable. |
| **String templates** (preview) | `STR."Hello \{name}"` — type-safe string interpolation. |

### Migration Strategy (1 h)

- **Incremental adoption:** don't rewrite everything — start with new code.
- Priority order: `var` → records → text blocks → switch expressions → pattern matching → sealed classes → virtual threads.
- Dealing with library compatibility: `--add-opens`, `--add-modules`.
- CI matrix: compile on 21, test on 17 + 21.
- Feature flags and preview features: `--enable-preview`.

---

## References

| Resource | Notes |
|----------|-------|
| [OpenJDK JEP Index](https://openjdk.org/jeps/0) | Every JDK Enhancement Proposal by number |
| *Core Java*, Vol. 1 & 2, 12th Ed. (Horstmann) | Comprehensive, updated for Java 17+ |
| [JEP 395 — Records](https://openjdk.org/jeps/395) | Definitive specification |
| [JEP 409 — Sealed Classes](https://openjdk.org/jeps/409) | Definitive specification |
| [JEP 444 — Virtual Threads](https://openjdk.org/jeps/444) | Definitive specification |
| [JEP 453 — Structured Concurrency](https://openjdk.org/jeps/453) | Preview in 21 |
| [JEP 441 — Pattern Matching for switch](https://openjdk.org/jeps/441) | Finalised in 21 |
| [JEP 440 — Record Patterns](https://openjdk.org/jeps/440) | Finalised in 21 |
| [JEP 431 — Sequenced Collections](https://openjdk.org/jeps/431) | Finalised in 21 |

---

## Exercises

### Exercise 1 — Legacy Code Moderniser

A `LegacyCode.java` file is provided with Java 8-style code. Refactor it step-by-step:

1. Replace verbose local types with `var` where it improves readability.
2. Convert `instanceof` chains to **pattern matching**.
3. Convert data-holder classes to **records**.
4. Replace string concatenation with **text blocks**.
5. Replace old-style `switch` with **switch expressions**.
6. Track before/after: line count, cyclomatic complexity, readability.

**Bonus:** add a `module-info.java` to modularise the exercise project.

### Exercise 2 — Modern Java Showcase: Task Manager CLI

Build a command-line task manager demonstrating every major modern feature:

- **Records** for `Task`, `TaskId`, `Priority`.
- **Sealed interface** for task state: `Open`, `InProgress`, `Done`, `Cancelled`.
- **Pattern matching switch** for dispatching CLI commands (`add`, `start`, `complete`, `list`, `help`).
- **Text blocks** for multi-line help output.
- **Virtual threads** for concurrent file I/O (save/load tasks).
- Switch expressions for priority parsing.

**Bonus:** use **structured concurrency** (`StructuredTaskScope`) for parallel task operations (e.g. searching across multiple task files).

### Exercise 3 — JSON Parser with Pattern Matching

Hand-write a simple JSON parser that produces a typed AST:

- **Sealed interface** `JsonValue` with `permits`: `JsonObject`, `JsonArray`, `JsonString`, `JsonNumber`, `JsonBoolean`, `JsonNull`.
- Each variant is a **record**.
- **Pattern matching switch** for type dispatch in pretty-printing and querying.
- **Text blocks** for embedding test JSON.

**Bonus:** implement a pretty-printer with configurable indentation, using pattern matching to handle each node type.

---

## Self-Assessment Checklist

- [ ] I can create a `module-info.java` with `exports` and `requires`.
- [ ] I know when `var` helps readability and when it hurts.
- [ ] I can write switch expressions with `->` and `yield`.
- [ ] I can define a sealed hierarchy and switch exhaustively over it.
- [ ] I can create records with compact constructors and custom methods.
- [ ] I can use pattern matching with `instanceof` and `switch`.
- [ ] I can start virtual threads and explain how they differ from platform threads.
- [ ] I understand the basic structured concurrency API (`StructuredTaskScope`).
- [ ] I can describe a migration path from Java 8 → 17 → 21.
- [ ] I can use text blocks and know how indentation stripping works.
