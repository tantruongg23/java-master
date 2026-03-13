# Phase 01 — Java Fundamentals: Interview Questions & Answers

> 80 questions organized by topic, progressing from basic to advanced.
> The final section contains cross-topic questions that combine multiple concepts.

---

## Table of Contents

1. [JDK / JRE / JVM Architecture](#1-jdk--jre--jvm-architecture) — Q1–Q10
2. [Primitive Types, Wrapper Classes, Autoboxing](#2-primitive-types-wrapper-classes-autoboxing) — Q11–Q22
3. [Operators, Control Flow, Enhanced For-Loop](#3-operators-control-flow-enhanced-for-loop) — Q23–Q32
4. [Arrays and Multi-Dimensional Arrays](#4-arrays-and-multi-dimensional-arrays) — Q33–Q40
5. [Methods, Varargs, Method Overloading](#5-methods-varargs-method-overloading) — Q41–Q50
6. [String Internals](#6-string-internals) — Q51–Q65
7. [Type Casting and Promotion Rules](#7-type-casting-and-promotion-rules) — Q66–Q72
8. [var Keyword Basics](#8-var-keyword-basics) — Q73–Q76
9. [Cross-Topic / Combined Questions (Advanced)](#9-cross-topic--combined-questions) — Q77–Q85

---

## 1. JDK / JRE / JVM Architecture

### Q1. What is the difference between JDK, JRE, and JVM? ⭐ Basic

**Answer:**

| Component | What it is | Contains |
|-----------|-----------|----------|
| **JVM** | An abstract specification + implementation that executes Java bytecode | Class loader, runtime data areas (heap, stack, method area), execution engine (interpreter, JIT, GC) |
| **JRE** | The runtime environment needed to **run** Java programs | JVM + core class libraries (`java.lang`, `java.util`, `java.io`, etc.) |
| **JDK** | The full development kit needed to **develop and run** Java programs | JRE + development tools (`javac`, `javadoc`, `jar`, `jshell`, `jlink`, etc.) |

The relationship is: **JDK ⊃ JRE ⊃ JVM**.

Since Java 11, Oracle no longer ships a standalone JRE — the JDK is the only distribution.

---

### Q2. Describe the Java compilation and execution pipeline. ⭐ Basic

**Answer:**

```
Source (.java)  →  javac (compiler)  →  Bytecode (.class)  →  JVM  →  Native machine code
```

1. **Compile time:** `javac` reads `.java` files, performs syntax/type checking, and produces platform-independent `.class` files containing bytecode.
2. **Load time:** The JVM's class loader finds, loads, and links the `.class` files.
3. **Runtime:** The execution engine interprets bytecode instruction by instruction. When it detects "hot" methods (called frequently), the JIT (Just-In-Time) compiler compiles them to optimized native machine code for direct CPU execution.

This two-stage process is what enables "write once, run anywhere" — the same `.class` file runs on any platform that has a JVM.

---

### Q3. What are the three phases of class loading? ⭐⭐ Intermediate

**Answer:**

1. **Loading** — The class loader locates and reads the `.class` file's byte stream. Three class loaders form a parent-delegation hierarchy:
   - Bootstrap ClassLoader → core Java classes (`java.lang.*`)
   - Platform ClassLoader → platform modules
   - Application ClassLoader → your application's classes from the classpath

2. **Linking** — Three sub-steps:
   - **Verify:** Confirms the bytecode is structurally correct and safe (no stack overflow tricks, valid opcodes, etc.)
   - **Prepare:** Allocates memory for static fields and sets them to default values (0, null, false — NOT their assigned values yet)
   - **Resolve:** Replaces symbolic references (e.g., class names as strings) with direct memory references

3. **Initialization** — Executes static initializer blocks and static field assignments in the order they appear in source code. This happens only once per class, the first time it is actively used.

---

### Q4. What is the difference between the JVM's Heap and Stack? ⭐⭐ Intermediate

**Answer:**

| Feature | Heap | Stack |
|---------|------|-------|
| **Stores** | Objects and arrays | Method frames (local variables, operand stack, frame data) |
| **Shared** | All threads share one heap | Each thread has its own stack |
| **Lifetime** | Objects live until no references exist (then GC reclaims) | Frame is pushed on method entry, popped on method exit |
| **Size** | Controlled by `-Xms` / `-Xmx` | Controlled by `-Xss` (default ~512KB–1MB per thread) |
| **Error** | `OutOfMemoryError: Java heap space` | `StackOverflowError` (usually infinite recursion) |
| **Access speed** | Slower (GC overhead, cache-unfriendly) | Faster (LIFO structure, cache-friendly) |

```java
public void example() {
    int x = 10;                    // x lives on the stack
    String name = new String("A"); // 'name' reference on stack, String object on heap
}
// When example() returns: x is popped from stack, name reference is popped,
// the String object on the heap becomes eligible for GC
```

---

### Q5. What is the JIT compiler and why does Java need it? ⭐⭐ Intermediate

**Answer:**

The JIT (Just-In-Time) compiler converts frequently-executed bytecode into native machine code at runtime. Java needs it because pure interpretation of bytecode is slow — every instruction must be decoded and dispatched each time it executes.

**How it works:**
- The JVM starts by interpreting bytecode (fast startup).
- It profiles execution and identifies "hot spots" — methods called thousands of times or loops with many iterations.
- The JIT compiles these hot methods to optimized native code.
- Subsequent calls execute the compiled native code directly, bypassing the interpreter.

**Two tiers of JIT:**
- **C1 (Client compiler):** Quick compilation with moderate optimizations. Used for code that becomes warm early.
- **C2 (Server compiler):** Slower compilation with aggressive optimizations (inlining, escape analysis, loop unrolling, dead code elimination). Used for truly hot code.

Modern JVMs use **tiered compilation** — start with C1 for fast warmup, promote to C2 when a method gets hot enough.

---

### Q6. What is Metaspace and how does it differ from PermGen? ⭐⭐⭐ Advanced

**Answer:**

**PermGen (Permanent Generation)** was a fixed-size area of the heap (Java 7 and earlier) that stored class metadata, interned strings, and static variables. It had a hard upper limit (`-XX:MaxPermSize`) and could throw `OutOfMemoryError: PermGen space` if too many classes were loaded (common in application servers with hot-deploy).

**Metaspace (Java 8+)** replaced PermGen. Key differences:

| Feature | PermGen | Metaspace |
|---------|---------|-----------|
| Location | Part of the Java heap | Native memory (OS memory) |
| Default size | Fixed (64MB–256MB) | Grows automatically up to available OS memory |
| Max size | `-XX:MaxPermSize` | `-XX:MaxMetaspaceSize` (unlimited by default) |
| GC behavior | Collected as part of full GC | Classes are unloaded when their classloader is GC'd |

Metaspace effectively eliminates the `PermGen space` error. However, in pathological cases (classloader leaks), it can still exhaust native memory. Best practice: set `-XX:MaxMetaspaceSize` in production to get an early, understandable error rather than a native OOM.

---

### Q7. What is the parent-delegation model in class loading? ⭐⭐⭐ Advanced

**Answer:**

When a class loader receives a request to load a class, it **first delegates to its parent** before attempting to load the class itself. The chain is:

```
Application ClassLoader  →  Platform ClassLoader  →  Bootstrap ClassLoader
(your classes)              (platform modules)        (java.lang.*, java.util.*)
```

**Process:**
1. Application ClassLoader receives a request to load `com.example.MyClass`.
2. It delegates to Platform ClassLoader.
3. Platform ClassLoader delegates to Bootstrap ClassLoader.
4. Bootstrap looks in `java.base` etc. — not found, returns.
5. Platform ClassLoader looks in platform modules — not found, returns.
6. Application ClassLoader looks in the classpath — found, loads it.

**Why this matters:**
- **Security:** Prevents user code from replacing core classes. You cannot create a custom `java.lang.String` that gets loaded instead of the real one, because Bootstrap always loads core classes first.
- **Uniqueness:** A class is identified by its fully-qualified name AND its class loader. The same `.class` file loaded by two different class loaders produces two distinct classes that cannot cast to each other.

---

### Q8. What do `-Xms`, `-Xmx`, and `-Xss` flags control? ⭐ Basic

**Answer:**

| Flag | Controls | Example | Note |
|------|----------|---------|------|
| `-Xms` | **Initial** heap size | `-Xms512m` | How much heap memory the JVM allocates at startup |
| `-Xmx` | **Maximum** heap size | `-Xmx4g` | Upper limit the heap can grow to. If exceeded → `OutOfMemoryError` |
| `-Xss` | **Thread stack** size | `-Xss256k` | Size of each thread's stack. Too small → `StackOverflowError` on deep recursion. Too large → fewer concurrent threads possible |

**Production tip:** Set `-Xms` equal to `-Xmx` to avoid heap resizing pauses. For containerized apps (Docker/K8s), always set `-Xmx` explicitly — otherwise the JVM may try to use more memory than the container allows.

---

### Q9. What happens if `java --version` and `javac --version` report different versions? ⭐ Basic

**Answer:**

This means your `PATH` environment variable points to different JDK installations for the runtime (`java`) and the compiler (`javac`). This can cause:
- Classes compiled with a newer `javac` that use features unavailable in the older `java` runtime → `UnsupportedClassVersionError`
- Subtle behavioral differences between versions

**Fix:** Ensure both point to the same JDK. Use `which java` / `which javac` (Linux/Mac) or `where java` / `where javac` (Windows) to check their locations.

---

### Q10. Can a `.class` file compiled with JDK 21 run on JVM 17? ⭐⭐ Intermediate

**Answer:**

**No** (by default). Each `.class` file has a major version number in its header. JDK 21 produces class files with major version 65, while JVM 17 only supports up to major version 61. The JVM will throw `UnsupportedClassVersionError`.

**Workaround:** Compile with `javac --release 17` (or `--target 17 --source 17`), which produces bytecode compatible with Java 17. However, you cannot use Java 18–21 language features in that case.

---

## 2. Primitive Types, Wrapper Classes, Autoboxing

### Q11. How many primitive types does Java have? List them with their sizes. ⭐ Basic

**Answer:**

Java has exactly **8 primitive types**:

| Type | Size | Category |
|------|------|----------|
| `byte` | 8 bits (1 byte) | Integer |
| `short` | 16 bits (2 bytes) | Integer |
| `int` | 32 bits (4 bytes) | Integer |
| `long` | 64 bits (8 bytes) | Integer |
| `float` | 32 bits (4 bytes) | Floating-point |
| `double` | 64 bits (8 bytes) | Floating-point |
| `char` | 16 bits (2 bytes) | Character (unsigned, UTF-16) |
| `boolean` | JVM-dependent (~1 bit logical, often stored as 32 bits) | Boolean |

Primitives are **not objects** — they have no methods, cannot be `null`, and are stored directly on the stack (when local) rather than on the heap.

---

### Q12. What is the difference between default values for fields and local variables? ⭐ Basic

**Answer:**

**Instance and static fields** are initialized to default values by the JVM:
- Numeric types → `0` (or `0.0`)
- `boolean` → `false`
- `char` → `'\u0000'` (null character)
- Object references → `null`

**Local variables** have **no default value**. The compiler forces you to assign a value before reading. This is a compile-time safety check:

```java
public class Example {
    int field;          // OK: defaults to 0

    void method() {
        int local;
        // System.out.println(local);  // COMPILE ERROR: variable might not have been initialized
        local = 5;
        System.out.println(local);     // OK
    }
}
```

**Why the difference?** The compiler can perform definite assignment analysis for local variables (it sees the entire method). For fields, objects may be accessed from multiple methods in any order, so static analysis is impractical — defaults provide a safety net.

---

### Q13. What is the Integer cache? What does this code print? ⭐⭐ Intermediate

```java
Integer a = 127;
Integer b = 127;
Integer c = 128;
Integer d = 128;
System.out.println(a == b);
System.out.println(c == d);
```

**Answer:**

Output:
```
true
false
```

`Integer.valueOf(int)` caches instances for values **-128 to 127**. When you write `Integer a = 127`, the compiler generates `Integer.valueOf(127)`, which returns a cached object. Both `a` and `b` get the same cached object, so `a == b` is `true` (same reference).

For `128`, the value is outside the cache range. `Integer.valueOf(128)` creates a **new** `Integer` object each time. `c` and `d` are different objects, so `c == d` is `false`.

**Lesson:** Never use `==` to compare wrapper objects. Always use `.equals()`.

---

### Q14. What will this code produce? ⭐⭐ Intermediate

```java
Integer x = null;
int y = x;
```

**Answer:**

It throws a **`NullPointerException`** at runtime.

The compiler generates `int y = x.intValue()`. Since `x` is `null`, calling `.intValue()` on it throws NPE. This is one of the most common unboxing bugs in production Java code.

**Prevention strategies:**
- Check for `null` before unboxing: `if (x != null) { int y = x; }`
- Use `Optional<Integer>` instead of nullable `Integer`
- Prefer primitive types whenever the value cannot logically be null

---

### Q15. Why is autoboxing in a loop a performance problem? ⭐⭐ Intermediate

**Answer:**

```java
Long sum = 0L;  // Wrapper type
for (int i = 0; i < 1_000_000; i++) {
    sum += i;
}
```

Each iteration does: `sum = Long.valueOf(sum.longValue() + i)`:
1. **Unbox** `sum` to `long` (call `sum.longValue()`)
2. **Add** `i` (widened to `long`)
3. **Autobox** the result back to `Long` (call `Long.valueOf(...)`)
4. This creates a **new `Long` object** per iteration (values above 127 are not cached)

This generates ~1 million unnecessary `Long` objects, causing GC pressure and ~6x slower execution compared to using `long sum = 0L` (primitive).

**Rule:** Use primitive types for arithmetic and loop counters. Only use wrappers when you need nullability or must satisfy a generic type parameter.

---

### Q16. What is the difference between `new Integer(5)` and `Integer.valueOf(5)`? ⭐⭐ Intermediate

**Answer:**

| | `new Integer(5)` | `Integer.valueOf(5)` |
|-|-------------------|---------------------|
| Creates new object? | **Always** creates a new object on the heap | Returns a **cached** instance for -128 to 127; creates new object outside that range |
| `==` comparison | Two calls always return different objects → `==` is `false` | Within cache range, returns the same object → `==` is `true` |
| Deprecated? | **Yes**, deprecated since Java 9, removed in Java 16 | No, this is the preferred way |

```java
Integer a = new Integer(5);     // Deprecated; always creates new object
Integer b = Integer.valueOf(5); // Preferred; returns cached instance

System.out.println(a == b);     // false (a is a new object, b is cached)
```

Always use `Integer.valueOf()` or simple autoboxing (`Integer x = 5;`), never `new Integer()`.

---

### Q17. Can you use a primitive as a type parameter in generics? Why? ⭐ Basic

**Answer:**

**No.** Java generics work through **type erasure** — at runtime, `List<Integer>` becomes just `List<Object>`. Since primitives don't extend `Object`, they cannot be used as type parameters.

```java
// List<int> list = new ArrayList<>();     // COMPILE ERROR
List<Integer> list = new ArrayList<>();    // Must use wrapper
list.add(42);                              // Autoboxed to Integer.valueOf(42)
```

This is a known limitation of Java generics. Project Valhalla (in development) aims to add value types and primitive generics (`List<int>`) to a future Java version.

---

### Q18. What is the difference between `float` and `double`? When would you use `float`? ⭐ Basic

**Answer:**

| | `float` | `double` |
|-|---------|----------|
| Size | 32 bits | 64 bits |
| Precision | ~6-7 significant decimal digits | ~15-16 significant decimal digits |
| Literal suffix | `3.14f` (required) | `3.14` or `3.14d` |
| Default for decimal literals | No | Yes (`3.14` is `double`) |

**When to use `float`:** Almost never in modern Java. Use it only when:
- Memory is extremely constrained (millions of values, e.g., graphics/game programming)
- You're interfacing with a library that requires `float`

**For money/currency:** Never use `float` or `double`. Use `BigDecimal` to avoid floating-point rounding errors:
```java
System.out.println(0.1 + 0.2);            // 0.30000000000000004
System.out.println(new BigDecimal("0.1")
    .add(new BigDecimal("0.2")));          // 0.3
```

---

### Q19. What does `Character.isDigit()` do differently from checking `c >= '0' && c <= '9'`? ⭐⭐⭐ Advanced

**Answer:**

`c >= '0' && c <= '9'` only checks ASCII digits (0–9).

`Character.isDigit(c)` checks **all Unicode digits**, including:
- Arabic-Indic digits (٠١٢٣٤٥٦٧٨٩)
- Devanagari digits (०१२३४५६७८९)
- Thai digits (๐๑๒๓๔๕๖๗๘๙)
- Many others across dozens of scripts

If you only want ASCII digits (the common case), `c >= '0' && c <= '9'` is correct and slightly faster. If your application handles international input, `Character.isDigit()` is the safer choice.

---

### Q20. Explain `Boolean.valueOf()` behavior. How many `Boolean` objects exist at any time? ⭐⭐ Intermediate

**Answer:**

`Boolean.valueOf(true)` always returns `Boolean.TRUE`, and `Boolean.valueOf(false)` always returns `Boolean.FALSE`. These are two static final fields defined in the `Boolean` class.

There are effectively only **2 `Boolean` instances** in a well-behaved program (ignoring `new Boolean()` which is deprecated). Every autoboxing operation returns one of these two cached singletons:

```java
Boolean a = true;         // Boolean.valueOf(true) → Boolean.TRUE
Boolean b = Boolean.TRUE; // Same object
System.out.println(a == b); // true — same singleton
```

---

### Q21. What is the range of `char`? Can it store negative values? ⭐ Basic

**Answer:**

`char` is a 16-bit **unsigned** integer type. Its range is `0` to `65,535` (`\u0000` to `\uFFFF`). It **cannot** store negative values.

```java
char c = 65;      // 'A'
char max = 65535;  // '\uFFFF'
// char neg = -1;  // COMPILE ERROR: incompatible types (int cannot be narrowed)

int i = 'A';       // 65 — widening char → int
```

---

### Q22. What happens with `Integer.MAX_VALUE + 1`? ⭐⭐ Intermediate

**Answer:**

It **wraps around** to `Integer.MIN_VALUE` due to integer overflow:

```java
System.out.println(Integer.MAX_VALUE);       // 2147483647
System.out.println(Integer.MAX_VALUE + 1);   // -2147483648 (Integer.MIN_VALUE)
```

Java does **not** throw an exception on integer overflow — it silently wraps. This is a common source of bugs.

**To detect overflow:**
```java
// Java 8+: throws ArithmeticException on overflow
Math.addExact(Integer.MAX_VALUE, 1);  // ArithmeticException
Math.multiplyExact(Integer.MAX_VALUE, 2); // ArithmeticException

// Or use long for intermediate results
long safe = (long) Integer.MAX_VALUE + 1; // 2147483648L — no overflow
```

---

## 3. Operators, Control Flow, Enhanced For-Loop

### Q23. What is the difference between `&&` and `&`? ⭐ Basic

**Answer:**

| | `&&` (logical AND) | `&` (bitwise AND / non-short-circuit) |
|-|--------------------|-----------------------------------------|
| Short-circuits | **Yes** — if left side is `false`, right side is NOT evaluated | **No** — always evaluates both sides |
| Primary use | Boolean conditions | Bitwise operations on integers |

```java
String s = null;
if (s != null && s.length() > 0) { }   // Safe: s.length() never called when s is null
if (s != null & s.length() > 0) { }    // NPE! s.length() is always evaluated
```

**Always use `&&` and `||` for boolean logic.** Use `&`, `|`, `^` only for bitwise operations.

---

### Q24. What does this print? ⭐⭐ Intermediate

```java
int x = 5;
System.out.println(x++ + ++x);
```

**Answer:**

Output: **`12`**

Step by step:
1. `x++` (post-increment): uses current value `5` in the expression, then increments `x` to `6`
2. `++x` (pre-increment): increments `x` from `6` to `7`, then uses `7` in the expression
3. `5 + 7 = 12`

**Real-world advice:** Never write code like this. It's confusing, error-prone, and violates the principle of least surprise. Separate increments from expressions.

---

### Q25. Explain the difference between `switch` statement and `switch` expression. ⭐⭐ Intermediate

**Answer:**

**Switch statement** (all Java versions):
- Does NOT return a value
- Uses `case X:` with fall-through by default — requires `break` to prevent it
- `default` is optional

**Switch expression** (Java 14+):
- **Returns a value** — can be assigned to a variable
- Uses `case X ->` (arrow syntax) — no fall-through
- Multi-line blocks use `yield` to return a value
- `default` is **required** unless all possible values are covered (enums, sealed classes)
- The compiler checks **exhaustiveness**

```java
// Statement (old)
String name;
switch (day) {
    case 1: name = "Monday"; break;
    case 2: name = "Tuesday"; break;
    default: name = "Other"; break;
}

// Expression (modern)
String name = switch (day) {
    case 1 -> "Monday";
    case 2 -> "Tuesday";
    default -> "Other";
};
```

---

### Q26. What is a labeled `break`? When would you use it? ⭐⭐ Intermediate

**Answer:**

A labeled `break` exits a specific outer loop, not just the innermost one:

```java
search:
for (int i = 0; i < rows; i++) {
    for (int j = 0; j < cols; j++) {
        if (matrix[i][j] == target) {
            System.out.println("Found at " + i + "," + j);
            break search;  // Exits BOTH loops
        }
    }
}
```

Without the label, `break` only exits the inner loop and the outer loop continues.

**When to use:** When searching in nested structures and you want to stop as soon as a result is found. However, if the logic gets complex, extracting the nested loop into a method with `return` is often cleaner.

---

### Q27. What happens if you remove `break` from a `switch` statement? ⭐ Basic

**Answer:**

**Fall-through** occurs — execution continues into the next `case` regardless of whether it matches:

```java
int x = 1;
switch (x) {
    case 1: System.out.println("One");
    case 2: System.out.println("Two");
    case 3: System.out.println("Three");
}
// Output:
// One
// Two
// Three
```

All three print because execution "falls through" from `case 1` to `case 2` to `case 3`. This is one of the most common bugs in Java. The arrow syntax in switch expressions (`->`) eliminates this problem entirely.

---

### Q28. Can a `switch` expression handle `null`? ⭐⭐⭐ Advanced

**Answer:**

**Before Java 21:** No. Passing `null` to any `switch` throws `NullPointerException` before any case is evaluated.

**Java 21+ (pattern matching for switch):** Yes, `null` can be an explicit case:

```java
String result = switch (input) {
    case null  -> "Input is null";
    case "A"   -> "Got A";
    default    -> "Something else";
};
```

Without the `case null`, `null` input still throws NPE. The `case null` label is opt-in.

---

### Q29. Why can't you modify a collection during an enhanced for-loop? ⭐⭐ Intermediate

**Answer:**

The enhanced for-loop uses an `Iterator` internally. The iterator maintains a `modCount` (modification count) that is checked on each call to `next()`. If the collection's `modCount` changes (due to `add`/`remove`), the iterator detects the mismatch and throws `ConcurrentModificationException`.

```java
// This is what the compiler generates for: for (String s : list) { ... }
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String s = it.next();  // Checks modCount here — throws if collection was modified
}
```

**Safe alternatives:**
```java
list.removeIf(s -> s.equals("remove me"));           // Java 8+
it.remove();                                           // Using iterator directly
new ArrayList<>(list).forEach(s -> list.remove(s));    // Copy first
```

---

### Q30. What is the ternary operator? Can you nest it? ⭐ Basic

**Answer:**

The ternary operator `condition ? valueIfTrue : valueIfFalse` is an expression that returns one of two values:

```java
int age = 20;
String status = (age >= 18) ? "Adult" : "Minor";  // "Adult"
```

**Can you nest it?** Technically yes, but you **should not** — it becomes unreadable:

```java
// BAD: nested ternary
String rating = score > 90 ? "A" : score > 80 ? "B" : score > 70 ? "C" : "F";

// GOOD: use if-else or switch
String rating;
if (score > 90) rating = "A";
else if (score > 80) rating = "B";
else if (score > 70) rating = "C";
else rating = "F";
```

---

### Q31. What data types can a `switch` work with? ⭐⭐ Intermediate

**Answer:**

| Java Version | Supported Types |
|-------------|-----------------|
| All versions | `byte`, `short`, `char`, `int` and their wrappers |
| Java 5+ | `enum` types |
| Java 7+ | `String` |
| Java 21+ | Any type via **pattern matching** (`case Integer i ->`, `case String s ->`, `case null ->`) |

`long`, `float`, `double`, and `boolean` are **not** allowed in traditional switch (before Java 21 pattern matching).

---

### Q32. What is the difference between `break` and `continue`? ⭐ Basic

**Answer:**

| | `break` | `continue` |
|-|---------|-----------|
| Effect | **Exits** the entire loop | **Skips** the rest of the current iteration and moves to the next |

```java
for (int i = 0; i < 10; i++) {
    if (i == 5) break;
    System.out.print(i + " ");
}
// Output: 0 1 2 3 4

for (int i = 0; i < 10; i++) {
    if (i == 5) continue;
    System.out.print(i + " ");
}
// Output: 0 1 2 3 4 6 7 8 9
```

---

## 4. Arrays and Multi-Dimensional Arrays

### Q33. Are arrays objects in Java? ⭐ Basic

**Answer:**

**Yes.** Arrays are objects that live on the heap. They inherit from `Object` and have `Object` methods like `toString()`, `hashCode()`, etc.

```java
int[] arr = new int[5];
System.out.println(arr instanceof Object);     // true
System.out.println(arr.getClass().getName());  // [I  (JVM notation for int[])
System.out.println(arr.getClass().getSuperclass()); // class java.lang.Object
```

Because arrays are objects, they are passed by reference value (the reference is copied, but both caller and callee point to the same array on the heap).

---

### Q34. What is the difference between `.length`, `.length()`, and `.size()`? ⭐ Basic

**Answer:**

| Syntax | Used on | Type |
|--------|---------|------|
| `.length` | Arrays | **Field** (no parentheses) |
| `.length()` | `String` | **Method** |
| `.size()` | `Collection` (`List`, `Set`, `Map`) | **Method** |

```java
int[] arr = {1, 2, 3};
String s = "hello";
List<Integer> list = List.of(1, 2);

arr.length;     // 3 (field)
s.length();     // 5 (method)
list.size();    // 2 (method)
```

This inconsistency is a historical artifact. Arrays were designed before the Collections Framework, and `String` was designed independently.

---

### Q35. What is a jagged array? ⭐⭐ Intermediate

**Answer:**

A jagged array is a multi-dimensional array where each row can have a different number of columns. This works because a 2D array in Java is really an "array of arrays" — each inner array is an independent object.

```java
int[][] jagged = new int[3][];       // 3 rows, columns undefined
jagged[0] = new int[]{1, 2};         // Row 0: 2 elements
jagged[1] = new int[]{3, 4, 5, 6};  // Row 1: 4 elements
jagged[2] = new int[]{7};            // Row 2: 1 element
```

**Real-world use case:** Representing a triangle (like Pascal's triangle), sparse data where rows have varying amounts of data, or adjacency lists in graph algorithms.

---

### Q36. How do you compare two arrays for equality? ⭐ Basic

**Answer:**

**`==`** compares references (are they the same object?). **`Arrays.equals()`** compares contents element by element. For multi-dimensional arrays, use **`Arrays.deepEquals()`**.

```java
int[] a = {1, 2, 3};
int[] b = {1, 2, 3};

a == b;                     // false (different objects)
Arrays.equals(a, b);        // true  (same contents)

int[][] m1 = {{1, 2}, {3, 4}};
int[][] m2 = {{1, 2}, {3, 4}};

Arrays.equals(m1, m2);      // false! Compares inner array REFERENCES
Arrays.deepEquals(m1, m2);  // true  — recursively compares contents
```

---

### Q37. What happens when you print an array with `System.out.println()`? ⭐ Basic

**Answer:**

You get the default `Object.toString()` output — the class name and hash code, which is useless:

```java
int[] arr = {1, 2, 3};
System.out.println(arr);           // [I@1a2b3c4d (garbage)
System.out.println(Arrays.toString(arr));     // [1, 2, 3]
System.out.println(Arrays.deepToString(matrix)); // For 2D+
```

This is because arrays don't override `toString()`.

---

### Q38. Can an array grow after creation? ⭐ Basic

**Answer:**

**No.** Arrays have a **fixed size** that is set at creation time and cannot change.

If you need a growable collection, use `ArrayList`:

```java
int[] fixed = new int[3];  // Forever 3 elements

// To "grow" an array, you must create a new, larger one:
int[] bigger = Arrays.copyOf(fixed, 10);  // New array of size 10, copies old data

// Or just use ArrayList
ArrayList<Integer> list = new ArrayList<>();
list.add(1);  // Grows automatically
list.add(2);
```

---

### Q39. What is `Arrays.copyOf` vs `System.arraycopy`? ⭐⭐ Intermediate

**Answer:**

| | `Arrays.copyOf(src, newLength)` | `System.arraycopy(src, srcPos, dest, destPos, length)` |
|-|-------------------------------|-------------------------------------------------------|
| Creates new array? | **Yes** — returns a new array | **No** — copies into an existing destination array |
| Flexibility | Always copies from index 0 | Can copy from/to any position |
| Performance | Calls `System.arraycopy` internally | Native method, slightly faster for bulk copies |
| Use when | You need a new array (resize, clone) | You have an existing destination array |

```java
// Arrays.copyOf — new array
int[] src = {1, 2, 3, 4, 5};
int[] copy = Arrays.copyOf(src, 3);      // [1, 2, 3]
int[] padded = Arrays.copyOf(src, 8);    // [1, 2, 3, 4, 5, 0, 0, 0]

// System.arraycopy — into existing array
int[] dest = new int[10];
System.arraycopy(src, 1, dest, 3, 3);    // Copy 3 elements: src[1..3] → dest[3..5]
// dest = [0, 0, 0, 2, 3, 4, 0, 0, 0, 0]
```

---

### Q40. Why does `Arrays.sort()` on primitives use Dual-Pivot Quicksort while `Arrays.sort()` on objects uses TimSort? ⭐⭐⭐ Advanced

**Answer:**

| | Primitive arrays | Object arrays |
|-|-----------------|---------------|
| Algorithm | Dual-Pivot Quicksort | TimSort (merge sort variant) |
| Stable? | **No** (equal elements may be reordered) | **Yes** (equal elements preserve original order) |
| Why? | Primitives have no identity — two `5`s are interchangeable, so stability doesn't matter. Quicksort is faster in practice due to better cache locality. | Objects have identity — equal objects (by comparator) should maintain their relative order. TimSort guarantees stability and also performs well on partially-sorted data. |

**Stability matters** when you sort by multiple criteria sequentially:
```java
// Sort employees by department, then by name within department
employees.sort(Comparator.comparing(Employee::getName));
employees.sort(Comparator.comparing(Employee::getDepartment));
// Because TimSort is stable, the name ordering is preserved within each department
```

---

## 5. Methods, Varargs, Method Overloading

### Q41. Is Java pass-by-value or pass-by-reference? ⭐ Basic

**Answer:**

**Java is always pass-by-value.** But what is passed by value depends on the type:

- **Primitives:** The actual value is copied. Changes inside the method do not affect the caller.
- **Objects:** The **reference** (memory address) is copied. Both caller and method reference the same object, so mutations to the object are visible to the caller. However, reassigning the parameter to a new object does NOT affect the caller.

```java
void change(int x, List<String> list) {
    x = 999;               // Only changes the local copy
    list.add("added");      // Mutates the shared object — caller sees it
    list = new ArrayList<>(); // Only changes the local reference — caller NOT affected
}
```

**The confusion:** People see that object mutations are visible and think it's pass-by-reference. But pass-by-reference would mean reassigning the parameter would change the caller's variable — and that does NOT happen in Java.

---

### Q42. Can you have two methods that differ only in return type? ⭐ Basic

**Answer:**

**No.** The method signature consists of the method name and parameter types only. Return type is NOT part of the signature. The compiler would not know which one to call:

```java
int calculate(int a, int b) { return a + b; }
double calculate(int a, int b) { return a + b; }  // COMPILE ERROR: duplicate method
```

---

### Q43. What is the overloading resolution order? ⭐⭐ Intermediate

**Answer:**

When the compiler encounters an overloaded method call, it tries to find the best match in this priority order:

1. **Exact match** — parameter types match perfectly
2. **Widening** — `byte → short → int → long → float → double`, `char → int`
3. **Autoboxing** — `int ↔ Integer`, `double ↔ Double`
4. **Varargs** — `int...` matches any number of `int` arguments

```java
void foo(int x)       { }  // A
void foo(long x)      { }  // B
void foo(Integer x)   { }  // C
void foo(int... x)    { }  // D

foo(5);      // Calls A — exact match
foo(5L);     // Calls B — exact match for long
foo((short)5); // Calls A — widening: short → int

// If A doesn't exist: foo(5) calls B (widening beats boxing)
// If A and B don't exist: foo(5) calls C (boxing beats varargs)
// If only D exists: foo(5) calls D (varargs is last resort)
```

---

### Q44. What is an ambiguous method call? Give an example. ⭐⭐ Intermediate

**Answer:**

An ambiguous call occurs when the compiler cannot determine which overloaded method is "more specific":

```java
void print(int a, long b)  { }
void print(long a, int b)  { }

print(1, 1);  // COMPILE ERROR: ambiguous!
```

Both methods require one widening conversion (`int → long`), so neither is more specific than the other. The compiler refuses to guess.

**Fix:** Provide explicit types: `print(1, 1L)` or `print(1L, 1)`.

---

### Q45. What are the rules for varargs? ⭐ Basic

**Answer:**

1. Syntax: `Type... name` (e.g., `int... numbers`)
2. Must be the **last parameter** in the method signature
3. Only **one** varargs parameter per method
4. Inside the method, it behaves as an **array** (`Type[]`)
5. Caller can pass: zero arguments, multiple arguments, or an actual array

```java
void log(String level, String... messages) { }  // OK: varargs is last

// void bad(String... a, int x) { }  // COMPILE ERROR: varargs not last
// void bad(int... a, int... b) { }  // COMPILE ERROR: only one varargs allowed
```

---

### Q46. What happens if you pass `null` to a varargs method? ⭐⭐⭐ Advanced

**Answer:**

It depends on whether there's overloading:

```java
void process(String... args) {
    System.out.println(args);  // null
}
process(null);  // args is null (NOT an empty array and NOT an array containing null)
                // This is because null matches String[] (the compiled type of varargs)

// Safe: check for null
void safePprocess(String... args) {
    if (args == null) return;
    for (String s : args) { }
}
```

With overloading, it gets tricky:
```java
void foo(String s)      { System.out.println("String"); }
void foo(String... s)   { System.out.println("Varargs"); }

foo(null);  // "String" — non-varargs is more specific
foo();      // "Varargs" — no arguments, only varargs matches
```

---

### Q47. Can you override a method and change the parameter names? ⭐ Basic

**Answer:**

**Yes.** Parameter names are NOT part of the method signature — they are purely for readability and local use. The override is valid as long as the types match:

```java
class Parent {
    void greet(String name) { }
}
class Child extends Parent {
    @Override
    void greet(String person) { }  // OK: same signature (String parameter)
}
```

---

### Q48. What is the difference between method overloading and method overriding? ⭐ Basic

**Answer:**

| | Overloading | Overriding |
|-|------------|-----------|
| Definition | Same name, **different parameter lists** in the **same class** (or class hierarchy) | Same name, **same parameter list** in a **subclass** |
| Resolved at | **Compile time** (static dispatch) | **Runtime** (dynamic dispatch / polymorphism) |
| Return type | Can differ | Must be the same or a covariant (more specific) type |
| Access modifier | Can differ | Cannot be more restrictive |
| `static` methods | Can be overloaded | Cannot be overridden (they can be hidden) |

---

### Q49. Explain the `main` method signature. What happens if you change it? ⭐ Basic

**Answer:**

The JVM looks for exactly: `public static void main(String[] args)`

| Part | Reason |
|------|--------|
| `public` | JVM must access it from outside the class |
| `static` | JVM calls it without creating an instance |
| `void` | No return value expected by the JVM |
| `String[] args` | Command-line arguments. `String... args` also works (varargs is compiled to `String[]`) |

If you change any part, the JVM won't find the entry point and throws `Error: Main method not found`.

Java 21+ also supports unnamed `main` methods: `void main()` (preview feature for simpler programs).

---

### Q50. Can a method call itself? What's the risk? ⭐ Basic

**Answer:**

Yes — this is **recursion**. Each recursive call pushes a new frame onto the thread's stack. The risk is `StackOverflowError` if the recursion doesn't terminate (or is too deep):

```java
int factorial(int n) {
    if (n <= 1) return 1;         // Base case — stops recursion
    return n * factorial(n - 1);  // Recursive case
}

factorial(5);       // OK: 5 * 4 * 3 * 2 * 1 = 120
factorial(100000);  // StackOverflowError — too many frames
```

**Mitigation:** Use iteration instead of deep recursion, increase stack size (`-Xss`), or use tail-call patterns (Java doesn't optimize tail calls, but you can restructure with loops).

---

## 6. String Internals

### Q51. Why is `String` immutable in Java? ⭐ Basic

**Answer:**

Four key reasons:

1. **String pool:** The pool works because strings never change. If "hello" could be mutated, every reference pointing to the pooled "hello" would see the change — chaos.
2. **Thread safety:** Immutable objects are inherently thread-safe — no synchronization needed to share them between threads.
3. **Hashcode caching:** `String.hashCode()` is computed once and cached. This makes `String` an extremely efficient `HashMap` key — the hash never needs recomputation.
4. **Security:** Class names, file paths, database URLs, and network addresses are strings. Immutability prevents malicious code from changing them after security checks.

**How immutability is enforced:**
- `String` class is `final` (cannot be subclassed)
- Internal `byte[] value` is `private` and `final`
- No method modifies the internal state; all return new `String` objects

---

### Q52. How many objects are created by `new String("hello")`? ⭐⭐ Intermediate

**Answer:**

**Up to 2 objects:**

1. The string literal `"hello"` causes the JVM to check the String pool. If `"hello"` is not yet in the pool, a new `String` object is created there. If it already exists, this step creates nothing.
2. `new String(...)` **always** creates a new `String` object on the regular heap, copying the content.

So:
- First time `"hello"` appears in the program: **2 objects** (pool + heap)
- If `"hello"` was already in the pool: **1 object** (heap only)

---

### Q53. What does `String.intern()` do? ⭐⭐ Intermediate

**Answer:**

`intern()` checks if an equal string already exists in the String pool. If yes, it returns the pool reference. If no, it adds the string to the pool and returns it.

```java
String a = new String("hello");  // Heap object
String b = a.intern();           // Returns the pool reference for "hello"
String c = "hello";              // Pool reference

System.out.println(a == b);   // false — a is heap, b is pool
System.out.println(b == c);   // true  — both are the pool reference
```

**When to use:** Rarely. Useful when you have many duplicate strings from external sources (file parsing, network data) and want to save memory. But the pool uses native memory and has its own performance characteristics — profile before using.

---

### Q54. What is the difference between `StringBuilder` and `StringBuffer`? ⭐ Basic

**Answer:**

| | `StringBuilder` | `StringBuffer` |
|-|-----------------|----------------|
| Thread-safe | No | Yes (all methods are `synchronized`) |
| Performance | Faster | Slower (synchronization overhead per method call) |
| Introduced | Java 1.5 | Java 1.0 |
| Use when | Single-threaded context (99% of cases) | Multiple threads append to the same builder |

In practice, **always use `StringBuilder`** unless you're explicitly sharing a mutable string builder across threads — which is an unusual design that should probably be reconsidered.

---

### Q55. Why is string concatenation in a loop O(n²)? ⭐⭐ Intermediate

**Answer:**

```java
String result = "";
for (int i = 0; i < n; i++) {
    result += "x";  // Creates a new String every iteration
}
```

Each `result += "x"` must:
1. Allocate a new `char[]`/`byte[]` of size `result.length() + 1`
2. Copy ALL existing characters from `result` into it
3. Append `"x"`
4. Create a new `String` from the array

Characters copied per iteration: 1, 2, 3, ..., n = n(n+1)/2 = **O(n²)**.

With `StringBuilder`:
```java
StringBuilder sb = new StringBuilder();
for (int i = 0; i < n; i++) {
    sb.append("x");  // Appends to an internal buffer, O(1) amortized
}
String result = sb.toString();  // One copy at the end
// Total: O(n)
```

---

### Q56. What does this print? ⭐⭐ Intermediate

```java
String s1 = "hello";
String s2 = "hel" + "lo";
String s3 = "hel";
String s4 = s3 + "lo";
System.out.println(s1 == s2);
System.out.println(s1 == s4);
```

**Answer:**

```
true
false
```

- `s1 == s2` is `true` because `"hel" + "lo"` is a **compile-time constant** — the compiler evaluates it to `"hello"` and uses the same pool reference.
- `s1 == s4` is `false` because `s3 + "lo"` involves a **variable** (`s3`), so the compiler cannot resolve it at compile time. It generates a `StringBuilder` (or `invokedynamic`) at runtime, producing a new heap object.

**To make s4 use the pool:** Declare s3 as `final`:
```java
final String s3 = "hel";
String s4 = s3 + "lo";        // Now s3 is a compile-time constant
System.out.println(s1 == s4); // true
```

---

### Q57. What are Compact Strings (Java 9+)? ⭐⭐⭐ Advanced

**Answer:**

Before Java 9, `String` internally used `char[]` — every character took 2 bytes (UTF-16), even plain ASCII text.

Since Java 9, `String` uses `byte[]` + a `coder` flag:
- **LATIN1** (coder = 0): 1 byte per character — used when ALL characters fit in ISO-8859-1 (most English text)
- **UTF16** (coder = 1): 2 bytes per character — used when ANY character is outside Latin-1

```java
String ascii = "hello";   // LATIN1: 5 bytes internal storage
String mixed = "café";    // UTF16: 8 bytes (the 'é' forces UTF-16 encoding)
```

This is **transparent** — no API changes, no code changes needed. Most real-world applications are predominantly ASCII, so this reduces `String` memory usage by nearly **50%**.

Controlled by: `-XX:+CompactStrings` (on by default).

---

### Q58. What is the difference between `String.trim()` and `String.strip()`? ⭐⭐ Intermediate

**Answer:**

| | `trim()` (Java 1.0) | `strip()` (Java 11) |
|-|---------------------|---------------------|
| Removes | Characters with code point ≤ `U+0020` (space) | All **Unicode whitespace** as defined by `Character.isWhitespace()` |
| Unicode-aware | No | Yes |

```java
char nbsp = '\u00A0';  // Non-breaking space
String s = nbsp + "hello" + nbsp;

s.trim();    // "\u00A0hello\u00A0" — NBSP not removed (code > 0x20)
s.strip();   // "hello" — NBSP removed (it's Unicode whitespace)
```

Also available: `stripLeading()` and `stripTrailing()` for one-sided trimming.

**Rule:** Use `strip()` in all new code. Use `trim()` only if you need backward compatibility.

---

### Q59. What does `String.substring()` do internally? Has it changed? ⭐⭐⭐ Advanced

**Answer:**

**Before Java 7u6:** `substring()` returned a new `String` that shared the same `char[]` as the original, with different offset and count. This was fast (O(1)) but caused **memory leaks** — a small substring could keep a huge `char[]` alive.

```java
// Pre-Java 7u6: the 5-char substring holds a reference to the entire 1MB char[]
String huge = loadMegabyteString();
String small = huge.substring(0, 5);  // Memory leak!
```

**Java 7u6 and later:** `substring()` copies the relevant portion to a new `byte[]`/`char[]`. This is O(k) where k is the substring length, but eliminates the memory leak. A small substring has no reference to the original.

---

### Q60. What is the output? ⭐⭐ Intermediate

```java
String s = "Hello";
s.concat(" World");
System.out.println(s);
```

**Answer:**

```
Hello
```

`concat()` returns a **new** String — it does not modify `s` (Strings are immutable). The result `"Hello World"` is created but immediately discarded since nothing captures the return value.

**Correct usage:**
```java
s = s.concat(" World");
System.out.println(s);  // "Hello World"
```

---

### Q61. Compare `==` and `.equals()` for Strings with a tricky example. ⭐⭐ Intermediate

**Answer:**

```java
String a = "cat";
String b = "cat";
String c = new String("cat");
String d = c.intern();

System.out.println(a == b);         // true  — both are the same pool object
System.out.println(a == c);         // false — c is a separate heap object
System.out.println(a.equals(c));    // true  — same content
System.out.println(a == d);         // true  — intern() returned the pool reference
System.out.println(c == d);         // false — c is still the heap object, d is pool
```

**Golden rule:** Always use `.equals()` for String comparison. `==` is only reliable for compile-time constants and interned strings.

---

### Q62. How does `String` work as a `HashMap` key? Why is it so efficient? ⭐⭐⭐ Advanced

**Answer:**

`String` is the ideal `HashMap` key because:

1. **Immutable** — the key cannot change after being inserted, so the hash stays valid
2. **Cached hashCode** — `String.hashCode()` is computed once and stored in a private field. Subsequent calls return the cached value instantly (O(1) vs recomputation)
3. **Proper `equals()` and `hashCode()` contract** — `String` correctly implements both, satisfying the `HashMap` requirement that equal objects have equal hash codes

```java
// Inside String class (simplified):
private int hash;  // Default 0

public int hashCode() {
    int h = hash;
    if (h == 0 && !hashIsZero) {
        h = computeHash();
        hash = h;            // Cache it
        hashIsZero = (h == 0);
    }
    return h;
}
```

This is why `String` is the most commonly used `HashMap` key type.

---

### Q63. What does `String.format()` vs `String.formatted()` do? ⭐ Basic

**Answer:**

Both format strings with placeholders, but the syntax differs:

```java
// String.format() — static method (all Java versions)
String s1 = String.format("Hello %s, you are %d years old", "Alice", 30);

// String.formatted() — instance method (Java 15+)
String s2 = "Hello %s, you are %d years old".formatted("Alice", 30);

// Both produce: "Hello Alice, you are 30 years old"
```

Common format specifiers: `%s` (string), `%d` (integer), `%f` (float), `%n` (newline), `%.2f` (2 decimal places), `%10s` (right-aligned, 10 chars wide).

---

### Q64. What is the output? ⭐⭐⭐ Advanced

```java
String s1 = "Java";
String s2 = "Java";
String s3 = new String("Java");
String s4 = new String("Java");

System.out.println(s1.hashCode() == s2.hashCode());
System.out.println(s3.hashCode() == s4.hashCode());
System.out.println(s1.hashCode() == s3.hashCode());
System.out.println(System.identityHashCode(s1) == System.identityHashCode(s2));
System.out.println(System.identityHashCode(s3) == System.identityHashCode(s4));
```

**Answer:**

```
true   — same content → same hashCode
true   — same content → same hashCode
true   — same content → same hashCode (regardless of pool vs heap)
true   — s1 and s2 are the SAME object (pooled) → same identity hash
false  — s3 and s4 are DIFFERENT objects → different identity hash
```

`hashCode()` depends on content. `System.identityHashCode()` depends on object identity (memory address). This demonstrates that same hash code does NOT mean same object.

---

### Q65. What is string deduplication in G1 GC? ⭐⭐⭐ Advanced

**Answer:**

G1 GC (with `-XX:+UseStringDeduplication`, default off) identifies `String` objects on the heap that have identical `byte[]` contents and makes them share the same `byte[]`. The `String` objects remain distinct, but their internal arrays are deduplicated.

```
Before dedup:
  String A → byte[]{72, 101, 108, 108, 111}  ("hello")
  String B → byte[]{72, 101, 108, 108, 111}  ("hello")  ← separate copy

After dedup:
  String A → byte[]{72, 101, 108, 108, 111}  ("hello")
  String B ─────────┘                         ← shares same byte[]
```

This differs from `intern()` — the `String` objects are still separate (different references), only the internal array is shared. It requires no code changes and can significantly reduce memory in applications with many duplicate strings (e.g., large datasets).

---

## 7. Type Casting and Promotion Rules

### Q66. What does `byte + byte` evaluate to? ⭐ Basic

**Answer:**

`int`. In Java, arithmetic on `byte`, `short`, or `char` **always promotes operands to `int`** before the operation:

```java
byte a = 10;
byte b = 20;
// byte c = a + b;    // COMPILE ERROR: int cannot be assigned to byte
int c = a + b;        // OK: 30
byte d = (byte)(a + b);  // OK with explicit cast: 30
```

This is a design decision to prevent silent overflow in small types.

---

### Q67. What is the output? ⭐⭐ Intermediate

```java
System.out.println(7 / 2);
System.out.println(7 / 2.0);
System.out.println(7.0 / 2);
System.out.println((double) 7 / 2);
```

**Answer:**

```
3       — int / int = int (truncated)
3.5     — int / double → double / double = double
3.5     — double / int → double / double = double
3.5     — (double)7 / 2 → double / int → double / double = double
```

The classic integer division gotcha. If both operands are `int`, the result is `int` (truncated, NOT rounded). To get a decimal result, at least one operand must be `float` or `double`.

---

### Q68. What happens when you cast a `double` to an `int`? ⭐ Basic

**Answer:**

The fractional part is **truncated** (chopped off), NOT rounded:

```java
int a = (int) 3.9;    // 3 (not 4)
int b = (int) -3.9;   // -3 (not -4)
int c = (int) 3.1;    // 3

// If you want rounding:
int d = (int) Math.round(3.9);   // 4
int e = (int) Math.round(3.5);   // 4
int f = (int) Math.round(3.4);   // 3
```

---

### Q69. What is the output of this? ⭐⭐ Intermediate

```java
char c = 'A';
int i = c + 1;
char d = (char)(c + 1);
System.out.println(i);
System.out.println(d);
```

**Answer:**

```
66    — char 'A' (65) + int 1 = int 66
B     — char 'A' (65) + 1 = 66, cast back to char = 'B'
```

`char` is numeric in Java — it widens to `int` in arithmetic. You need an explicit cast to store the result back as `char`.

---

### Q70. What is the difference between widening and narrowing for objects? ⭐⭐ Intermediate

**Answer:**

| | Widening (upcasting) | Narrowing (downcasting) |
|-|---------------------|------------------------|
| Direction | Subclass → Superclass | Superclass → Subclass |
| Safety | Always safe | May throw `ClassCastException` |
| Explicit cast needed | No | Yes |

```java
// Upcasting — always safe, implicit
String s = "hello";
Object obj = s;            // String → Object (automatic)

// Downcasting — requires cast, can fail
Object obj2 = "hello";
String s2 = (String) obj2;     // OK: obj2 IS a String at runtime

Object obj3 = Integer.valueOf(5);
// String s3 = (String) obj3;  // ClassCastException: Integer cannot be cast to String

// Safe downcasting
if (obj3 instanceof String str) {  // Java 16+ pattern matching
    System.out.println(str.length());
}
```

---

### Q71. Explain implicit widening in method calls with an example. ⭐⭐ Intermediate

**Answer:**

When you pass an argument to a method, Java applies widening automatically:

```java
void process(double value) {
    System.out.println(value);
}

process(42);       // int 42 → widened to double 42.0
process(42L);      // long 42 → widened to double 42.0
process(3.14f);    // float → widened to double
process(3.14);     // exact match
```

This interacts with overloading resolution — widening is preferred over autoboxing:

```java
void foo(long x) { System.out.println("long"); }
void foo(Integer x) { System.out.println("Integer"); }

foo(5);  // "long" — widening (int→long) beats boxing (int→Integer)
```

---

### Q72. What happens with `float f = 1.0;`? ⭐ Basic

**Answer:**

**Compile error!** `1.0` is a `double` literal. Assigning `double` to `float` is a narrowing conversion that requires an explicit cast.

```java
// float f = 1.0;      // ERROR: incompatible types (double → float)
float f = 1.0f;        // OK: f suffix makes it a float literal
float g = (float) 1.0; // OK: explicit cast
```

---

## 8. var Keyword Basics

### Q73. Is `var` a keyword? ⭐ Basic

**Answer:**

**No, `var` is a reserved type name**, not a keyword. This means you can still use `var` as a variable name (though you shouldn't), but not as a class or interface name:

```java
var var = "hello";          // Legal but terrible — var is the variable name
// class var { }             // COMPILE ERROR: var cannot be a class name

int var = 5;                 // Legal but terrible
System.out.println(var);     // 5
```

This design was chosen for backward compatibility — existing code that used `var` as a variable name still compiles.

---

### Q74. List all the places where `var` CANNOT be used. ⭐⭐ Intermediate

**Answer:**

1. **Class fields** (instance/static variables): `var x = 5;` at class level → ERROR
2. **Method parameters**: `void foo(var x)` → ERROR
3. **Method return types**: `var foo() { }` → ERROR
4. **Without an initializer**: `var x;` → ERROR (nothing to infer from)
5. **With `null`**: `var x = null;` → ERROR (type is ambiguous)
6. **Lambda expressions**: `var fn = (x) -> x * 2;` → ERROR (cannot infer the functional interface type)
7. **Array initializer without `new`**: `var arr = {1, 2, 3};` → ERROR
8. **Catch clause parameter**: `catch (var e)` → ERROR
9. **Multiple variable declarations**: `var x = 1, y = 2;` → ERROR

---

### Q75. What type does `var list = new ArrayList<>();` infer? ⭐⭐ Intermediate

**Answer:**

`ArrayList<Object>` — and this is almost certainly a bug.

The diamond operator `<>` infers the type parameter from the left-hand side. But with `var`, the left-hand side has no explicit type. So the diamond infers `Object`:

```java
var list = new ArrayList<>();           // ArrayList<Object> — probably wrong
list.add("hello");                       // Works — anything is an Object
list.add(42);                            // Also works — not type-safe!

var list2 = new ArrayList<String>();     // ArrayList<String> — correct
// list2.add(42);                        // COMPILE ERROR — type-safe
```

**Rule:** When using `var`, always provide the full generic type on the right-hand side.

---

### Q76. Is `var` dynamic typing? Can the type change after assignment? ⭐ Basic

**Answer:**

**No.** `var` is **static type inference** — the compiler determines the exact type at compile time from the initializer. Once inferred, the type is fixed and cannot change.

```java
var x = "hello";   // Type: String (determined at compile time)
// x = 42;         // COMPILE ERROR: incompatible types (int → String)

var y = 10;        // Type: int
// y = "text";     // COMPILE ERROR
```

This is fundamentally different from dynamically typed languages like JavaScript or Python where a variable can hold any type at any time.

---

## 9. Cross-Topic / Combined Questions

### Q77. What is the output? (Autoboxing + Widening + Overloading) ⭐⭐⭐ Advanced

```java
class Challenge {
    void m(Integer x)  { System.out.println("Integer"); }
    void m(long x)     { System.out.println("long"); }

    public static void main(String[] args) {
        new Challenge().m(5);
    }
}
```

**Answer:**

```
long
```

The argument `5` is an `int`. The compiler tries:
1. Exact match for `int` — no method with `int` parameter
2. Widening (`int → long`) — matches `m(long)` ✓
3. Boxing (`int → Integer`) — matches `m(Integer)`, but this is lower priority

Widening beats boxing, so `m(long)` is called.

**Follow-up:** If we add `void m(int x)`, it would be called (exact match wins over everything).

---

### Q78. What is the output? (String pool + final + Concatenation) ⭐⭐⭐ Advanced

```java
String s1 = "hello";
final String s2 = "hel";
String s3 = "hel";
String s4 = s2 + "lo";
String s5 = s3 + "lo";

System.out.println(s1 == s4);
System.out.println(s1 == s5);
```

**Answer:**

```
true
false
```

- `s2` is `final`, so the compiler treats `s2 + "lo"` as a compile-time constant expression → evaluates to `"hello"` → uses the pool reference → `s1 == s4` is `true`.
- `s3` is NOT `final`, so `s3 + "lo"` cannot be resolved at compile time → creates a new runtime object → `s1 == s5` is `false`.

**Lesson:** `final` on a `String` variable turns concatenation into a compile-time constant.

---

### Q79. What is the output? (Array + Pass-by-Value + Method) ⭐⭐ Intermediate

```java
public static void main(String[] args) {
    int[] arr = {1, 2, 3};
    modify(arr);
    System.out.println(Arrays.toString(arr));
}

static void modify(int[] a) {
    a[0] = 99;
    a = new int[]{7, 8, 9};
    a[1] = 100;
}
```

**Answer:**

```
[99, 2, 3]
```

Step by step:
1. `a[0] = 99` — `a` points to the same array as `arr`. This modifies the shared object. `arr` is now `{99, 2, 3}`.
2. `a = new int[]{7, 8, 9}` — reassigns the local reference `a` to a NEW array. `arr` still points to the original.
3. `a[1] = 100` — modifies the NEW array (now `{7, 100, 9}`), which has no connection to `arr`.

The caller's `arr` only saw the first mutation.

---

### Q80. What is the output? (Type Promotion + Ternary) ⭐⭐⭐ Advanced

```java
Object result = true ? new Integer(1) : new Double(2.0);
System.out.println(result);
System.out.println(result.getClass().getName());
```

**Answer:**

```
1.0
java.lang.Double
```

Surprise! Even though the condition is `true` and selects `new Integer(1)`, the ternary operator must have a single result type. When one branch is `Integer` and the other is `Double`, Java applies **binary numeric promotion**: both are unboxed and widened to `double`. The result `1.0` is then autoboxed back to `Double`.

This is one of the most counterintuitive behaviors in Java.

---

### Q81. Design Question: Why can't you put primitives in a `HashMap`? ⭐⭐ Intermediate

**Answer:**

`HashMap<K, V>` uses generics, which require objects (type erasure replaces generic types with `Object` at runtime). Primitives are not objects and don't extend `Object`, so they can't be type parameters.

When you write:
```java
Map<String, Integer> map = new HashMap<>();
map.put("key", 42);  // 42 is autoboxed to Integer.valueOf(42)
int value = map.get("key");  // Unboxed from Integer to int
```

Every `put` and `get` involves autoboxing/unboxing. For performance-critical code with millions of entries, consider:
- `int[]` arrays with computed indices (if keys are sequential)
- Eclipse Collections `IntObjectHashMap` or similar primitive-specialized collections
- Java might eventually support `HashMap<String, int>` via Project Valhalla

---

### Q82. What is the output? (Varargs + Autoboxing + Overloading) ⭐⭐⭐ Advanced

```java
class Puzzle {
    static void call(int x, int y)        { System.out.println("int, int"); }
    static void call(Integer... x)        { System.out.println("Integer varargs"); }

    public static void main(String[] args) {
        call(1, 2);
    }
}
```

**Answer:**

```
int, int
```

Resolution order:
1. **Exact match** (without varargs): `call(int, int)` ✓
2. Boxing + varargs: `call(Integer...)` — lower priority

If we remove `call(int, int)`:
- `call(1, 2)` would match `call(Integer...)` — boxing the ints, then matching as varargs.

---

### Q83. What is the output? (String + Array + Type casting) ⭐⭐⭐ Advanced

```java
Object[] objects = new String[3];
objects[0] = "hello";
objects[1] = new Object();  // What happens here?
```

**Answer:**

Line 1 compiles fine — `String[]` is a subtype of `Object[]` (Java arrays are **covariant**).

Line 2 compiles fine — the compiler sees `Object[]` and allows any `Object`.

Line 3 throws **`ArrayStoreException`** at runtime! The actual runtime type of the array is `String[]`, which cannot hold a plain `Object`. The JVM checks the runtime type on every array store.

This is a design flaw in Java's type system (arrays shouldn't be covariant). Generics don't have this problem — `List<String>` is NOT a subtype of `List<Object>`.

---

### Q84. Explain the memory layout of this code. (JVM + Stack + Heap + String Pool) ⭐⭐⭐ Advanced

```java
public static void main(String[] args) {
    int x = 10;
    String s = "hello";
    String t = new String("hello");
    int[] arr = {1, 2, 3};
}
```

**Answer:**

```
STACK (main method frame):          HEAP:
┌──────────────────────┐           ┌──────────────────────────┐
│ x = 10               │           │                          │
│ s = ref ──────────────────────── │──► "hello" (String Pool) │
│ t = ref ──────────────────────── │──► new String "hello"    │
│ arr = ref ────────────────────── │──► int[] {1, 2, 3}       │
└──────────────────────┘           └──────────────────────────┘
```

- `x` (primitive `int`): stored directly on the stack, value `10`
- `s` (reference): the reference is on the stack, points to `"hello"` in the String Pool (special area of the heap)
- `t` (reference): the reference is on the stack, points to a SEPARATE `String` object on the regular heap (which contains the same characters as the pooled version)
- `arr` (reference): the reference is on the stack, points to an `int[]` object on the heap containing values `{1, 2, 3}`

When `main()` returns, all four stack entries are popped. The heap objects become eligible for GC (except the pooled string, which lives for the lifetime of the class).

---

### Q85. Architecture Question: You're reviewing code that concatenates strings from 100,000 database rows into a single CSV export. The current code is: ⭐⭐⭐ Advanced

```java
String csv = "";
for (Row row : rows) {
    csv += row.getName() + "," + row.getValue() + "\n";
}
return csv;
```

**What problems do you see and how would you fix them?**

**Answer:**

**Problems:**

1. **O(n²) string concatenation** — Each `+=` creates a new `String`, copying all previous content. With 100,000 rows, this copies billions of characters total.
2. **Memory pressure** — 100,000 intermediate `String` objects are created and immediately discarded, causing heavy GC activity.
3. **Potential `OutOfMemoryError`** — The intermediate strings plus the GC overhead can exhaust heap memory for large datasets.

**Fix:**

```java
StringBuilder sb = new StringBuilder(rows.size() * 50);  // Pre-allocate estimated capacity
for (Row row : rows) {
    sb.append(row.getName()).append(',').append(row.getValue()).append('\n');
}
return sb.toString();
```

**Improvements made:**
1. **O(n) total** — `StringBuilder` appends to an internal buffer without copying old data.
2. **Pre-allocated capacity** — Avoids repeated internal buffer resizing. Estimate 50 chars per row × 100,000 rows.
3. **Single `toString()` call** — Only one final `String` object is created.
4. **Append `char` not `String`** — `','` and `'\n'` as `char` avoids creating single-character `String` objects.

**For very large exports**, consider streaming directly to an `OutputStream`/`Writer` instead of building the entire string in memory.

---

## Difficulty Distribution

| Level | Count | Suitable for |
|-------|-------|-------------|
| ⭐ Basic | ~25 | Fresher / Junior interviews |
| ⭐⭐ Intermediate | ~35 | Mid-level / Senior interviews |
| ⭐⭐⭐ Advanced | ~25 | Senior / Architect interviews |

## Study Strategy

1. **First pass:** Answer all ⭐ Basic questions without looking at the answers.
2. **Second pass:** Tackle ⭐⭐ Intermediate questions. For any you get wrong, re-read the corresponding section in the [Study Guide](STUDY-GUIDE.md).
3. **Third pass:** Work through ⭐⭐⭐ Advanced questions. These are the ones that differentiate senior developers. Practice explaining them out loud.
4. **Final pass:** The cross-topic questions (Q77–Q85) simulate real interview conditions where you need to combine knowledge from multiple areas.
