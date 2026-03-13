# Phase 01 — Java Fundamentals (Speed Run)

> **Duration:** ~1 week (~9 hours)
> **Pace:** Fast review — assumes you already know basic programming concepts.
> **Goal:** Lock in Java-specific syntax, memory model quirks, and String internals so you never second-guess them again.

---

## Learning Objectives

By the end of this phase you will be able to:

1. Explain the difference between JDK, JRE, and JVM and describe the compilation/execution pipeline.
2. Choose the right primitive type for a given scenario and explain autoboxing pitfalls.
3. Write clean control-flow logic using enhanced for-loops, switch expressions, and guard clauses.
4. Work confidently with single and multi-dimensional arrays.
5. Design methods with varargs, understand overloading resolution rules, and avoid ambiguous signatures.
6. Explain String immutability, the String pool, `intern()`, and choose between `StringBuilder` and `StringBuffer` with performance reasoning.
7. Apply widening/narrowing conversion rules without surprises.
8. Use `var` (local variable type inference) correctly and know its limitations.

---

## Topics & Estimated Hours

### 1. JDK vs JRE vs JVM Architecture (1 h)

- **JVM (Java Virtual Machine):** Class loader subsystem, runtime data areas (heap, stack, method area, PC register, native method stack), execution engine (interpreter, JIT compiler, GC).
- **JRE (Java Runtime Environment):** JVM + core libraries (`java.lang`, `java.util`, etc.).
- **JDK (Java Development Kit):** JRE + development tools (`javac`, `javadoc`, `jdb`, `jlink`, `jpackage`).
- Compilation pipeline: `.java` → `javac` → `.class` (bytecode) → JVM → native code.
- `java --version` vs `javac --version` — why they can differ.
- Key JVM flags to know: `-Xmx`, `-Xms`, `-XX:+PrintGCDetails`.

### 2. Primitive Types, Wrapper Classes, Autoboxing (1 h)

- Eight primitives: `byte` (8-bit), `short` (16), `int` (32), `long` (64), `float` (32), `double` (64), `char` (16), `boolean`.
- Default values in fields vs. local variables (compiler error if uninitialized locally).
- Wrapper classes: `Integer`, `Long`, `Double`, etc. — cached ranges (`Integer.valueOf` cache: −128 to 127).
- **Autoboxing / unboxing:** implicit conversion, NPE risk when unboxing `null`, performance cost in tight loops.
- `==` vs `.equals()` on wrappers — the classic interview trap.

### 3. Operators, Control Flow, Enhanced For-Loop (1 h)

- Arithmetic, relational, logical, bitwise, ternary operators.
- Short-circuit evaluation: `&&` vs `&`, `||` vs `|`.
- `switch` statement vs `switch` expression (Java 14+): arrow labels, no fall-through, exhaustiveness.
- Enhanced for-loop: `for (Type item : collection)` — works on arrays and `Iterable`.
- Labeled `break` / `continue` for nested loops.

### 4. Arrays and Multi-Dimensional Arrays (1 h)

- Declaration, instantiation, initialization (static initializer `{1, 2, 3}`).
- `Arrays.copyOf`, `Arrays.sort`, `Arrays.binarySearch`, `Arrays.deepToString`.
- Jagged arrays (rows of different lengths).
- Arrays are objects — stored on the heap, passed by reference value.
- Common pitfalls: `ArrayIndexOutOfBoundsException`, confusing `.length` (array) vs `.length()` (String) vs `.size()` (Collection).

### 5. Methods, Varargs, Method Overloading (1 h)

- Method signature = name + parameter types (return type is NOT part of it).
- Pass-by-value semantics (even for references — the reference is copied).
- Varargs: `void log(String... messages)` — syntactic sugar for an array, must be the last parameter.
- Overloading resolution: compiler picks the most specific match; widening beats boxing beats varargs.
- Ambiguity traps: `foo(int, long)` vs `foo(long, int)` called with `foo(1, 1)`.

### 6. String Internals (2 h)

- **Immutability:** `String` objects cannot change after creation — every "modification" creates a new object.
- **String pool (interning):** literal strings live in the pool (part of the heap since Java 7). `new String("hello")` creates two objects (pool + heap). `.intern()` returns the pool reference.
- **StringBuilder vs StringBuffer:** both are mutable; `StringBuffer` is synchronized (thread-safe but slower). Use `StringBuilder` unless sharing across threads.
- **Performance:** `"a" + "b" + "c"` in a loop → O(n²) allocations pre-Java 9. Use `StringBuilder.append()`. Post-Java 9, `invokedynamic`-based concatenation improves this, but explicit `StringBuilder` is still clearer in hot loops.
- **Compact Strings (Java 9+):** internal representation uses `byte[]` + coder (LATIN1 or UTF-16) instead of `char[]`.
- Key methods: `charAt`, `substring`, `indexOf`, `contains`, `matches`, `split`, `strip` (Java 11), `repeat` (Java 11), `formatted` (Java 15).

### 7. Type Casting, Promotion Rules (0.5 h)

- **Widening (implicit):** `byte → short → int → long → float → double`; `char → int`.
- **Narrowing (explicit):** requires a cast, may lose data.
- Mixed expressions: all operands promoted to the widest type. `byte + byte = int` (surprise!).
- Casting objects: upcasting (safe, implicit), downcasting (requires cast, may throw `ClassCastException`).

### 8. `var` Keyword Basics (0.5 h)

- Local Variable Type Inference (Java 10+): `var list = new ArrayList<String>();`
- Only for local variables with initializers — not fields, method params, return types, or `null`.
- Readability: use when the type is obvious from the right-hand side.
- Cannot use with diamond operator when the type is ambiguous: `var list = new ArrayList<>();` → `ArrayList<Object>`.

---

## References

| Resource | Scope |
|----------|-------|
| [Oracle Java Tutorials — Learning the Java Language](https://docs.oracle.com/javase/tutorial/java/index.html) | Official tutorial covering all fundamentals |
| *Head First Java*, 3rd ed. — Chapters 1–5 | Visual, engaging review of primitives, classes, arrays |
| *Effective Java*, 3rd ed. — Item 63: "Beware the performance of string concatenation" | Deep dive into `StringBuilder` vs `+` |
| [Baeldung — Java String Pool](https://www.baeldung.com/java-string-pool) | String pool mechanics with examples |
| [JEP 286 — Local-Variable Type Inference](https://openjdk.org/jeps/286) | The `var` specification |

---

## Exercises

### Exercise 1 — CLI Calculator

**Business Context:** Your team needs a quick command-line tool for developers to evaluate arithmetic expressions without leaving the terminal.

**Requirements:**

1. Read expressions from `System.in` in a REPL loop. Type `exit` to quit.
2. Support operators: `+`, `-`, `*`, `/`, `%`, `^` (power).
3. Parse the expression, extract two operands and one operator (e.g., `12.5 + 3`).
4. Handle errors gracefully:
   - Division by zero → print a clear error, do not crash.
   - Invalid input → print "Invalid expression" and prompt again.
5. Maintain an in-memory **history** of the last 10 calculations (expression + result). Type `history` to print them.

**Bonus:**
- Support parenthesized sub-expressions (e.g., `(2 + 3) * 4`).
- Support chained expressions (e.g., `2 + 3 * 4` with proper operator precedence).

**Starter file:** `exercises/src/main/java/exercises/CliCalculator.java`

---

### Exercise 2 — StringToolkit

**Business Context:** You're building a shared utility library that every microservice in the company will depend on. String manipulation methods must be bulletproof — null-safe, performant, and well-documented.

**Requirements:**

Implement the following static methods:

| Method | Signature | Description |
|--------|-----------|-------------|
| `reverse` | `String reverse(String input)` | Reverse the string. Return `null` if input is `null`, `""` if empty. |
| `isPalindrome` | `boolean isPalindrome(String input)` | Case-insensitive, ignore non-alphanumeric chars. `null` → `false`. |
| `countVowels` | `int countVowels(String input)` | Count a/e/i/o/u (case-insensitive). `null` → `0`. |
| `toTitleCase` | `String toTitleCase(String input)` | Capitalize first letter of each word. `"hello world"` → `"Hello World"`. |
| `compress` | `String compress(String input)` | Run-length encoding: `"aaabbc"` → `"a3b2c1"`. Return original if compressed is longer. |
| `mostFrequentChar` | `char mostFrequentChar(String input)` | Return the most frequent character. Ties: return the first one encountered. Throw `IllegalArgumentException` on null/empty. |

**Performance:** All implementations must use `StringBuilder` — no `String` concatenation in loops.

**Bonus:**
- Write a `benchmarkConcat(int iterations)` method that compares `String +=` vs `StringBuilder.append()` for `iterations` concatenations and prints elapsed time for each.

**Starter file:** `exercises/src/main/java/exercises/StringToolkit.java`

---

### Exercise 3 — CSV Parser

**Business Context:** The data team sends you CSV exports that need to be loaded into memory for processing. No external libraries allowed — just core Java.

**Requirements:**

1. Method signature: `String[][] parse(String csv)`
2. Split by newlines to get rows, then by commas to get fields.
3. Handle **quoted fields**: `"hello, world"` is a single field containing `hello, world`.
4. Handle edge cases:
   - Empty fields: `a,,c` → `["a", "", "c"]`
   - Trailing commas: `a,b,` → `["a", "b", ""]`
   - Escaped quotes inside quoted fields: `"He said ""hi"""` → `He said "hi"`
5. Return a 2D `String[][]` where `result[row][col]` gives the field value.

**Bonus:**
- Accept a custom delimiter: `String[][] parse(String csv, char delimiter)`
- Handle newlines inside quoted fields.

**Starter file:** `exercises/src/main/java/exercises/CsvParser.java`

---

## Self-Assessment Checklist

Before moving to Phase 02, make sure you can confidently answer "yes" to each:

- [ ] I can draw the JDK / JRE / JVM relationship from memory and explain the bytecode execution pipeline.
- [ ] I know the size of every primitive type and can explain the `Integer` cache range.
- [ ] I understand why `new Integer(5) == new Integer(5)` is `false` but `Integer.valueOf(5) == Integer.valueOf(5)` is `true`.
- [ ] I can explain why `String` is immutable and what the String pool is.
- [ ] I know when to use `StringBuilder` vs `StringBuffer` and can justify it with a performance argument.
- [ ] I can predict the result of mixed-type arithmetic expressions (e.g., `byte + byte` evaluates to `int`).
- [ ] I understand pass-by-value semantics for both primitives and object references.
- [ ] I can explain varargs, overloading resolution order, and identify ambiguous method calls.
- [ ] I can use `var` correctly and list its restrictions.
- [ ] I have completed all three exercises and tested edge cases.
