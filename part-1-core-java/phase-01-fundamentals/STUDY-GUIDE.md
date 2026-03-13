# Phase 01 — Java Fundamentals: Complete Study Guide

> This guide covers every topic from the Phase 01 curriculum in depth.
> Read it section by section, run the examples, and experiment.

---

## Table of Contents

1. [JDK vs JRE vs JVM Architecture](#1-jdk-vs-jre-vs-jvm-architecture)
2. [Primitive Types, Wrapper Classes, Autoboxing](#2-primitive-types-wrapper-classes-autoboxing)
3. [Operators, Control Flow, Enhanced For-Loop](#3-operators-control-flow-enhanced-for-loop)
4. [Arrays and Multi-Dimensional Arrays](#4-arrays-and-multi-dimensional-arrays)
5. [Methods, Varargs, Method Overloading](#5-methods-varargs-method-overloading)
6. [String Internals](#6-string-internals)
7. [Type Casting and Promotion Rules](#7-type-casting-and-promotion-rules)
8. [var Keyword Basics](#8-var-keyword-basics)

---

## 1. JDK vs JRE vs JVM Architecture

### The Big Picture

When you write Java, your code goes through a unique two-stage process: first compiled to **bytecode**, then interpreted/compiled again to **native machine code** at runtime. This is what makes Java "write once, run anywhere."

```
Your code        Compiler        Bytecode         JVM            Machine code
Main.java  --->  javac  --->  Main.class  --->  JVM  --->  CPU instructions
```

### JVM — Java Virtual Machine

The JVM is an **abstract computing machine** — a specification that describes what a Java runtime must do. It never sees your `.java` files; it only works with `.class` files (bytecode).

**Key components inside the JVM:**

```
┌─────────────────────────────────────────────────────────┐
│                        JVM                              │
│                                                         │
│  ┌─────────────────────────────────────────────┐        │
│  │          Class Loader Subsystem              │        │
│  │  Loading → Linking → Initialization          │        │
│  └─────────────────┬───────────────────────────┘        │
│                    ▼                                    │
│  ┌─────────────────────────────────────────────┐        │
│  │          Runtime Data Areas                  │        │
│  │                                              │        │
│  │  ┌────────┐ ┌───────┐ ┌──────────────────┐  │        │
│  │  │  Heap  │ │ Stack │ │   Method Area    │  │        │
│  │  │(objects│ │(frames│ │(class metadata,  │  │        │
│  │  │  live  │ │ per   │ │ static vars,     │  │        │
│  │  │  here) │ │thread)│ │ constant pool)   │  │        │
│  │  └────────┘ └───────┘ └──────────────────┘  │        │
│  │  ┌──────────────┐  ┌──────────────────────┐  │        │
│  │  │ PC Register  │  │ Native Method Stack  │  │        │
│  │  │(per thread)  │  │ (for JNI calls)      │  │        │
│  │  └──────────────┘  └──────────────────────┘  │        │
│  └─────────────────────────────────────────────┘        │
│                    ▼                                    │
│  ┌─────────────────────────────────────────────┐        │
│  │          Execution Engine                    │        │
│  │  Interpreter  |  JIT Compiler  |  GC         │        │
│  └─────────────────────────────────────────────┘        │
└─────────────────────────────────────────────────────────┘
```

#### Class Loader Subsystem

Three phases happen when a class is first used:

1. **Loading** — finds and reads the `.class` file. Three class loaders form a hierarchy:
   - **Bootstrap ClassLoader** — loads core Java classes (`java.lang.*`, `java.util.*`) from `jrt:/java.base`
   - **Platform (Extension) ClassLoader** — loads platform-specific modules
   - **Application ClassLoader** — loads your application classes from the classpath

2. **Linking** — three steps:
   - *Verify* — checks bytecode is valid and safe
   - *Prepare* — allocates memory for static variables, sets them to default values
   - *Resolve* — replaces symbolic references with direct references

3. **Initialization** — runs static initializers and static blocks, in order of appearance

#### Runtime Data Areas

| Area | Shared? | What it stores |
|------|---------|----------------|
| **Heap** | All threads share | All objects and arrays. This is where GC operates. |
| **Stack** | Per thread | One frame per method call. Each frame holds local variables, operand stack, and a reference to the runtime constant pool. |
| **Method Area** | All threads share | Class metadata, static variables, constant pool, method bytecode. (Called **Metaspace** since Java 8, stored in native memory.) |
| **PC Register** | Per thread | Address of the current bytecode instruction being executed. |
| **Native Method Stack** | Per thread | Used when Java calls native (C/C++) code via JNI. |

#### Execution Engine

- **Interpreter** — reads bytecode instruction by instruction and executes it. Simple but slow for repeated code.
- **JIT (Just-In-Time) Compiler** — identifies "hot" methods (called frequently) and compiles them to native machine code for direct CPU execution. Two tiers:
  - **C1 (Client)** — fast compilation, moderate optimization
  - **C2 (Server)** — slower compilation, aggressive optimization (inlining, escape analysis, loop unrolling)
- **Garbage Collector (GC)** — automatically reclaims memory from objects that are no longer referenced. (Deep-dive in Phase 06.)

### JRE — Java Runtime Environment

```
JRE = JVM + Core Libraries
```

The JRE is what you need to **run** Java applications. It includes:
- The JVM implementation
- Core class libraries (`java.lang`, `java.util`, `java.io`, `java.net`, etc.)
- Supporting files (security configs, timezone data, etc.)

Since Java 11, Oracle no longer ships a standalone JRE — the JDK includes everything.

### JDK — Java Development Kit

```
JDK = JRE + Development Tools
```

The JDK is what you need to **develop** Java applications. It adds:

| Tool | Purpose |
|------|---------|
| `javac` | Compiles `.java` → `.class` |
| `java` | Launches the JVM to run a class or JAR |
| `javadoc` | Generates HTML documentation from doc comments |
| `jdb` | Command-line debugger |
| `jar` | Creates/extracts JAR archives |
| `jlink` | Creates custom JRE images with only the modules you need |
| `jpackage` | Packages into native installers (.msi, .deb, .dmg) |
| `jshell` | Interactive REPL for experimenting with Java snippets |
| `jcmd`, `jstat`, `jmap` | Diagnostic/monitoring tools |

### The Compilation & Execution Pipeline

```java
// File: HelloWorld.java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

**Step-by-step:**

```
1. You write:       HelloWorld.java      (human-readable source code)

2. javac compiles:  HelloWorld.class     (platform-independent bytecode)
   $ javac HelloWorld.java

3. JVM executes:    Native machine code  (platform-specific)
   $ java HelloWorld
   
   Internally:
   a) Class Loader finds and loads HelloWorld.class
   b) Bytecode Verifier checks it's safe
   c) Interpreter starts executing bytecode
   d) If main() is called many times, JIT compiles it to native code
   e) GC cleans up unused objects along the way
```

**Why bytecode matters:** The same `.class` file runs on Windows, Linux, and macOS — as long as there's a JVM for that platform.

### Version Check Gotcha

```bash
$ java --version     # Version of the JVM/runtime
$ javac --version    # Version of the compiler
```

These can differ if you have multiple JDKs installed and your `PATH` is misconfigured. Always verify both match.

### Key JVM Flags to Know

```bash
# Memory
-Xms512m          # Initial heap size (512 MB)
-Xmx2g            # Maximum heap size (2 GB)
-Xss256k          # Thread stack size

# GC diagnostics (useful for Phase 06)
-XX:+PrintGCDetails        # Print GC events
-XX:+UseG1GC               # Use G1 garbage collector
-XX:+UseZGC                # Use ZGC (low-latency)

# Example: Run with 1GB heap using G1
$ java -Xms256m -Xmx1g -XX:+UseG1GC -jar myapp.jar
```

---

## 2. Primitive Types, Wrapper Classes, Autoboxing

### The Eight Primitives

Java has exactly 8 primitive types. They are **not objects** — they live on the stack (when local) and have no methods.

| Type | Size | Range | Default (field) | Example |
|------|------|-------|-----------------|---------|
| `byte` | 8 bits | -128 to 127 | `0` | `byte b = 42;` |
| `short` | 16 bits | -32,768 to 32,767 | `0` | `short s = 1000;` |
| `int` | 32 bits | -2^31 to 2^31 - 1 (~±2.1 billion) | `0` | `int i = 100_000;` |
| `long` | 64 bits | -2^63 to 2^63 - 1 | `0L` | `long l = 9_999_999_999L;` |
| `float` | 32 bits | ±3.4 × 10^38 (6-7 decimal digits) | `0.0f` | `float f = 3.14f;` |
| `double` | 64 bits | ±1.7 × 10^308 (15-16 decimal digits) | `0.0d` | `double d = 3.14159;` |
| `char` | 16 bits | 0 to 65,535 (Unicode) | `'\u0000'` | `char c = 'A';` |
| `boolean` | ~1 bit* | `true` or `false` | `false` | `boolean ok = true;` |

*\* `boolean` size is JVM-dependent; often stored as `int` (32 bits) for alignment.*

**Tip:** Use underscores for readability: `1_000_000` is the same as `1000000`.

### Default Values vs Local Variables

```java
public class Defaults {
    static int fieldInt;       // Defaults to 0
    static boolean fieldBool;  // Defaults to false
    static String fieldStr;    // Defaults to null

    public static void main(String[] args) {
        int localInt;
        // System.out.println(localInt);  // COMPILE ERROR: not initialized
        
        localInt = 5;
        System.out.println(localInt);     // OK: 5
    }
}
```

**Rule:** Instance/static fields get default values. Local variables do NOT — the compiler forces you to initialize them before use.

### Wrapper Classes

Every primitive has a corresponding wrapper class in `java.lang`:

| Primitive | Wrapper | Cache Range |
|-----------|---------|-------------|
| `byte` | `Byte` | -128 to 127 (all) |
| `short` | `Short` | -128 to 127 |
| `int` | `Integer` | -128 to 127 |
| `long` | `Long` | -128 to 127 |
| `float` | `Float` | No cache |
| `double` | `Double` | No cache |
| `char` | `Character` | 0 to 127 |
| `boolean` | `Boolean` | `TRUE` and `FALSE` (both cached) |

**Why wrappers exist:**
- Generics require objects: `List<int>` is illegal, `List<Integer>` works
- Nullable values: primitives can't be `null`, wrappers can
- Utility methods: `Integer.parseInt("42")`, `Integer.MAX_VALUE`

### The Integer Cache — A Classic Gotcha

```java
Integer a = 127;    // Uses Integer.valueOf(127) — returns cached instance
Integer b = 127;    // Same cached instance
System.out.println(a == b);      // true  (same object in memory)

Integer c = 128;    // Uses Integer.valueOf(128) — cache miss, new object
Integer d = 128;    // Another new object
System.out.println(c == d);      // false (different objects!)
System.out.println(c.equals(d)); // true  (same value)
```

**How it works:** `Integer.valueOf(int i)` returns a cached instance for values -128 to 127. Beyond that range, it creates a new object every time.

```java
// Simplified view of Integer.valueOf
public static Integer valueOf(int i) {
    if (i >= -128 && i <= 127) {
        return IntegerCache.cache[i + 128];  // Return cached instance
    }
    return new Integer(i);                    // Create new object
}
```

### Autoboxing and Unboxing

**Autoboxing** = automatic primitive → wrapper conversion.
**Unboxing** = automatic wrapper → primitive conversion.

```java
// Autoboxing: int → Integer
Integer wrapped = 42;           // Compiler generates: Integer.valueOf(42)

// Unboxing: Integer → int
int unwrapped = wrapped;        // Compiler generates: wrapped.intValue()

// In collections
List<Integer> numbers = new ArrayList<>();
numbers.add(10);                // Autoboxing: int 10 → Integer.valueOf(10)
int first = numbers.get(0);    // Unboxing: Integer → int
```

#### Danger: Unboxing null → NullPointerException

```java
Integer maybeNull = null;
int value = maybeNull;          // RUNTIME: NullPointerException!
// Because the compiler generates: maybeNull.intValue() → NPE on null
```

This is one of the most common bugs in real-world Java code. Always null-check before unboxing.

#### Performance Pitfall: Autoboxing in Loops

```java
// BAD: Creates ~1 million Integer objects due to autoboxing
Long sum = 0L;
for (int i = 0; i < 1_000_000; i++) {
    sum += i;  // sum = Long.valueOf(sum.longValue() + i) — box, unbox, rebox
}

// GOOD: Use primitives for computation
long sum = 0L;
for (int i = 0; i < 1_000_000; i++) {
    sum += i;  // Pure primitive arithmetic, no objects created
}
```

### == vs .equals() on Wrappers

```java
// Primitives: == compares values
int x = 5, y = 5;
System.out.println(x == y);              // true

// Wrappers: == compares references, .equals() compares values
Integer a = new Integer(5);               // Deprecated, but illustrative
Integer b = new Integer(5);
System.out.println(a == b);              // false (different objects)
System.out.println(a.equals(b));         // true  (same value)

// valueOf within cache range
Integer c = Integer.valueOf(5);
Integer d = Integer.valueOf(5);
System.out.println(c == d);             // true (same cached object)
```

**Rule of thumb:** Always use `.equals()` to compare wrapper values. Never rely on `==` for wrappers.

---

## 3. Operators, Control Flow, Enhanced For-Loop

### Operator Categories

#### Arithmetic Operators

```java
int a = 10, b = 3;
a + b    // 13   Addition
a - b    // 7    Subtraction
a * b    // 30   Multiplication
a / b    // 3    Integer division (truncates)
a % b    // 1    Modulus (remainder)

// Division with doubles
double x = 10.0 / 3;   // 3.3333333333333335

// Increment/Decrement
int i = 5;
i++;    // Post-increment: use value (5), then increment to 6
++i;    // Pre-increment: increment to 7, then use value (7)

// Real-world gotcha:
int j = 5;
int result = j++ + ++j;   // j++ is 5 (j becomes 6), ++j is 7 (j becomes 7)
                           // result = 5 + 7 = 12
// Avoid this kind of code! It's confusing and error-prone.
```

#### Relational Operators

```java
5 == 5    // true    Equal to
5 != 3    // true    Not equal to
5 > 3     // true    Greater than
5 < 3     // false   Less than
5 >= 5    // true    Greater than or equal
5 <= 3    // false   Less than or equal
```

#### Logical Operators

```java
true && false   // false   Logical AND
true || false   // true    Logical OR
!true           // false   Logical NOT
```

#### Bitwise Operators (Used Less Often, but Important)

```java
int a = 0b1010;  // 10 in binary
int b = 0b1100;  // 12 in binary

a & b    // 0b1000 = 8    AND
a | b    // 0b1110 = 14   OR
a ^ b    // 0b0110 = 6    XOR
~a       // Inverts all bits
a << 1   // 0b10100 = 20  Left shift (multiply by 2)
a >> 1   // 0b0101 = 5    Right shift (divide by 2)
a >>> 1  // Unsigned right shift (fills with 0, not sign bit)

// Real-world use: Checking flags
int READ = 0b001;
int WRITE = 0b010;
int EXECUTE = 0b100;
int permissions = READ | WRITE;           // 0b011 = 3
boolean canRead = (permissions & READ) != 0;  // true
```

### Short-Circuit Evaluation

```java
// && and || are short-circuit: stop evaluating as soon as the result is determined
String name = null;

// SAFE: if name is null, the second condition is never evaluated
if (name != null && name.length() > 5) {
    System.out.println("Long name");
}

// UNSAFE: & always evaluates BOTH sides → NullPointerException
if (name != null & name.length() > 5) {  // NPE!
    System.out.println("Long name");
}

// || short-circuits on first true
boolean result = isValid() || expensiveCheck();
// If isValid() returns true, expensiveCheck() is never called
```

**Rule:** Always use `&&` and `||` for logical conditions. Use `&` and `|` only for bitwise operations.

### Ternary Operator

```java
int age = 20;
String status = (age >= 18) ? "adult" : "minor";
// Equivalent to:
// if (age >= 18) status = "adult"; else status = "minor";

// Can nest, but don't — it's unreadable:
// BAD:
String result = a > b ? "a" : b > c ? "b" : "c";
// GOOD: Use if/else for complex conditions
```

### Switch Statement vs Switch Expression

#### Traditional Switch Statement (all Java versions)

```java
int day = 3;
String dayName;

switch (day) {
    case 1:
        dayName = "Monday";
        break;               // MUST break or it falls through!
    case 2:
        dayName = "Tuesday";
        break;
    case 3:
        dayName = "Wednesday";
        break;
    default:
        dayName = "Unknown";
        break;
}
```

**Fall-through trap:**

```java
switch (day) {
    case 1:
        System.out.println("Monday");
        // No break! Falls through to case 2
    case 2:
        System.out.println("Tuesday");
        break;
}
// If day == 1, prints BOTH "Monday" AND "Tuesday"
```

#### Switch Expression (Java 14+) — The Modern Way

```java
int day = 3;

// Arrow syntax: no fall-through, no break needed
String dayName = switch (day) {
    case 1 -> "Monday";
    case 2 -> "Tuesday";
    case 3 -> "Wednesday";
    case 4 -> "Thursday";
    case 5 -> "Friday";
    case 6, 7 -> "Weekend";     // Multiple values in one case
    default -> "Unknown";
};

// With blocks and yield (for multi-line cases)
String description = switch (day) {
    case 1, 2, 3, 4, 5 -> {
        String name = getDayName(day);
        yield name + " is a weekday";   // 'yield' returns a value from a block
    }
    case 6, 7 -> "Weekend!";
    default -> throw new IllegalArgumentException("Invalid day: " + day);
};
```

**Why switch expressions are better:**
- They are expressions (return a value) — no need for a separate variable
- No fall-through bugs — arrow syntax doesn't fall through
- Exhaustiveness — the compiler ensures all cases are covered (especially with enums/sealed classes)

### Enhanced For-Loop

```java
// Arrays
int[] numbers = {1, 2, 3, 4, 5};
for (int n : numbers) {
    System.out.println(n);  // 1, 2, 3, 4, 5
}

// Collections (anything implementing Iterable)
List<String> names = List.of("Alice", "Bob", "Charlie");
for (String name : names) {
    System.out.println(name);
}

// Limitation: no access to the index
// If you need the index, use a traditional for-loop or IntStream
for (int i = 0; i < names.size(); i++) {
    System.out.println(i + ": " + names.get(i));
}
```

**Limitation:** You cannot modify the collection while iterating with an enhanced for-loop:

```java
List<String> names = new ArrayList<>(List.of("Alice", "Bob"));
for (String name : names) {
    if (name.equals("Bob")) {
        names.remove(name);   // ConcurrentModificationException!
    }
}
// Use Iterator.remove() or removeIf() instead:
names.removeIf(name -> name.equals("Bob"));
```

### Labeled Break and Continue

For nested loops, labels let you break/continue an outer loop:

```java
// Find the first pair that sums to target
int[][] matrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
int target = 10;

outerLoop:
for (int i = 0; i < matrix.length; i++) {
    for (int j = 0; j < matrix[i].length; j++) {
        if (matrix[i][j] == target) {
            System.out.println("Found at [" + i + "][" + j + "]");
            break outerLoop;  // Exits BOTH loops
        }
    }
}

// Without the label, break would only exit the inner loop
```

---

## 4. Arrays and Multi-Dimensional Arrays

### Declaration, Instantiation, Initialization

```java
// Three ways to create an array:

// 1. Declare, then instantiate (elements get default values)
int[] scores;               // Declare
scores = new int[5];        // Instantiate: [0, 0, 0, 0, 0]

// 2. Declare and instantiate in one line
double[] prices = new double[100];

// 3. Declare, instantiate, and initialize with values
String[] colors = {"red", "green", "blue"};
// Equivalent to:
String[] colors2 = new String[]{"red", "green", "blue"};

// Style note: int[] arr is preferred over int arr[]
// The first style makes clear that the TYPE is "int array"
```

### Arrays Are Objects

```java
int[] a = {1, 2, 3};

// Arrays live on the heap
System.out.println(a.getClass().getName());  // [I  (JVM notation for int[])
System.out.println(a instanceof Object);      // true

// .length is a field, not a method (no parentheses)
System.out.println(a.length);                 // 3

// Passed by reference value (the reference is copied)
modifyArray(a);
System.out.println(a[0]);  // Modified!

static void modifyArray(int[] arr) {
    arr[0] = 999;  // Modifies the original array
}
```

### java.util.Arrays Utility Class

```java
import java.util.Arrays;

int[] data = {5, 3, 1, 4, 2};

// Sort
Arrays.sort(data);                 // [1, 2, 3, 4, 5] — modifies in place

// Binary search (array MUST be sorted first)
int index = Arrays.binarySearch(data, 4);   // 3

// Copy
int[] copy = Arrays.copyOf(data, 3);        // [1, 2, 3] — first 3 elements
int[] bigger = Arrays.copyOf(data, 10);     // [1,2,3,4,5,0,0,0,0,0] — padded

// Range copy
int[] range = Arrays.copyOfRange(data, 1, 4); // [2, 3, 4] — index 1 to 3

// Fill
int[] filled = new int[5];
Arrays.fill(filled, 42);            // [42, 42, 42, 42, 42]

// Compare
int[] x = {1, 2, 3};
int[] y = {1, 2, 3};
System.out.println(x == y);                 // false (different objects)
System.out.println(Arrays.equals(x, y));    // true  (same contents)

// toString
System.out.println(data);                   // [I@1a2b3c (useless!)
System.out.println(Arrays.toString(data));  // [1, 2, 3, 4, 5]
```

### Multi-Dimensional Arrays

In Java, a 2D array is an **array of arrays**. Each "row" is an independent array object.

```java
// Regular 2D array (3 rows × 4 columns)
int[][] matrix = new int[3][4];
matrix[0][0] = 1;
matrix[2][3] = 99;

// Initialize with values
int[][] grid = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};

// Iterate
for (int row = 0; row < grid.length; row++) {
    for (int col = 0; col < grid[row].length; col++) {
        System.out.print(grid[row][col] + " ");
    }
    System.out.println();
}

// Or with enhanced for-loop:
for (int[] row : grid) {
    for (int cell : row) {
        System.out.print(cell + " ");
    }
    System.out.println();
}

// Deep toString for printing
System.out.println(Arrays.deepToString(grid));
// [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
```

### Jagged Arrays

Since each "row" is an independent array, they can have different lengths:

```java
int[][] jagged = new int[3][];        // 3 rows, columns TBD
jagged[0] = new int[]{1, 2};          // Row 0: 2 columns
jagged[1] = new int[]{3, 4, 5, 6};   // Row 1: 4 columns
jagged[2] = new int[]{7};             // Row 2: 1 column

// This is fine:
System.out.println(jagged[1][3]);     // 6
// This would throw ArrayIndexOutOfBoundsException:
// System.out.println(jagged[0][3]);  // Row 0 only has 2 elements
```

### Common Pitfalls

```java
// 1. ArrayIndexOutOfBoundsException
int[] arr = new int[5];
arr[5] = 10;  // RUNTIME ERROR: valid indices are 0-4

// 2. .length vs .length() vs .size()
int[] array = {1, 2, 3};
String text = "hello";
List<Integer> list = List.of(1, 2, 3);

array.length;       // Array: field (no parentheses)
text.length();      // String: method (parentheses!)
list.size();        // Collection: method called size()

// 3. Confusing reference equality with content equality
int[] a = {1, 2, 3};
int[] b = {1, 2, 3};
a == b;                    // false (different objects)
Arrays.equals(a, b);      // true  (same contents)
// For 2D arrays:
Arrays.deepEquals(grid1, grid2);
```

---

## 5. Methods, Varargs, Method Overloading

### Method Anatomy

```java
//  access  static?  return   name        parameters
//    ↓       ↓       ↓        ↓             ↓
    public  static  double  calculate(double a, double b, String op) {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            default  -> throw new IllegalArgumentException("Unknown: " + op);
        };
    }
```

**Method signature** = method name + parameter types (in order). Return type is NOT part of the signature.

```java
// These two have the SAME signature → compile error
int calculate(int a, int b) { ... }
double calculate(int a, int b) { ... }   // ERROR: duplicate method
```

### Pass-by-Value — The Most Misunderstood Concept

**Java is ALWAYS pass-by-value.** But for objects, the "value" is the reference (memory address).

```java
// Primitives: the value is copied. Changes inside the method don't affect the caller.
static void tryToChange(int x) {
    x = 999;
}
int num = 5;
tryToChange(num);
System.out.println(num);   // Still 5

// Objects: the REFERENCE is copied. Both caller and method point to the same object.
static void addItem(List<String> list) {
    list.add("new item");    // Modifies the shared object
}
List<String> myList = new ArrayList<>();
addItem(myList);
System.out.println(myList); // [new item] — modified!

// But reassigning the reference inside the method does NOT affect the caller:
static void replaceList(List<String> list) {
    list = new ArrayList<>();  // Only changes the local copy of the reference
    list.add("replaced");
}
List<String> original = new ArrayList<>(List.of("keep me"));
replaceList(original);
System.out.println(original); // [keep me] — NOT replaced!
```

**Mental model:**

```
Caller:     myList ──────────► [ ArrayList object on heap ]
                                       ▲
Method:     list (copy) ───────────────┘

list.add("x") modifies the shared object.
list = new ArrayList<>() makes the LOCAL copy point somewhere else.
```

### Varargs (Variable Arguments)

```java
// Syntax: Type... paramName — must be the LAST parameter
static int sum(int... numbers) {
    int total = 0;
    for (int n : numbers) {  // 'numbers' is just an int[] inside the method
        total += n;
    }
    return total;
}

// Call with any number of arguments:
sum()              // 0
sum(1)             // 1
sum(1, 2, 3)       // 6
sum(1, 2, 3, 4, 5) // 15

// Or pass an array directly:
int[] data = {10, 20, 30};
sum(data);         // 60

// Varargs MUST be last parameter:
void valid(String prefix, int... nums) { }     // OK
// void invalid(int... nums, String suffix) { } // COMPILE ERROR
```

### Method Overloading

Overloading = same method name, different parameter lists.

```java
class Printer {
    void print(String s)          { System.out.println("String: " + s); }
    void print(int i)             { System.out.println("int: " + i); }
    void print(double d)          { System.out.println("double: " + d); }
    void print(String s, int n)   { System.out.println(s + " x " + n); }
}

Printer p = new Printer();
p.print("hello");       // String: hello
p.print(42);            // int: 42
p.print(3.14);          // double: 3.14
p.print("item", 5);     // item x 5
```

### Overloading Resolution Order

When the compiler sees an overloaded call, it tries matches in this order:

```
1. Exact match
2. Widening (byte → short → int → long → float → double)
3. Autoboxing (int → Integer, or Integer → int)
4. Varargs
```

```java
class Demo {
    void foo(int x)       { System.out.println("int"); }
    void foo(long x)      { System.out.println("long"); }
    void foo(Integer x)   { System.out.println("Integer"); }
    void foo(int... x)    { System.out.println("varargs"); }
}

Demo d = new Demo();
d.foo(5);       // "int"      — exact match wins
d.foo(5L);      // "long"     — exact match
d.foo((short)5); // "int"     — widening: short → int

// If we remove foo(int x):
// d.foo(5);    // "long"     — widening beats boxing
// If we also remove foo(long x):
// d.foo(5);    // "Integer"  — boxing beats varargs
// If we also remove foo(Integer x):
// d.foo(5);    // "varargs"  — last resort
```

### Ambiguity Traps

```java
class Trap {
    void foo(int a, long b)  { System.out.println("int, long"); }
    void foo(long a, int b)  { System.out.println("long, int"); }
}

Trap t = new Trap();
// t.foo(1, 1);  // COMPILE ERROR: ambiguous!
// Both require one widening, so neither is "more specific"

// Fix: be explicit
t.foo(1, 1L);    // "int, long"
t.foo(1L, 1);    // "long, int"
```

```java
class NullTrap {
    void greet(String s)  { System.out.println("String"); }
    void greet(Object o)  { System.out.println("Object"); }
}

NullTrap nt = new NullTrap();
nt.greet(null);   // "String" — String is more specific than Object
nt.greet("hi");   // "String"
nt.greet(42);     // "Object" — int autoboxes to Integer, which is-a Object
```

---

## 6. String Internals

This is one of the most important topics for interviews and real-world performance. Understanding Strings deeply will serve you throughout your career.

### String Immutability

`String` objects **cannot be changed** after creation. Every "modification" creates a new String.

```java
String s = "hello";
s.toUpperCase();         // Creates a NEW String "HELLO"
System.out.println(s);   // Still "hello" — s was never changed

s = s.toUpperCase();     // Now s POINTS TO the new String "HELLO"
                         // The old "hello" String object is now eligible for GC
```

**Why is String immutable?**

1. **Thread safety** — Strings can be shared between threads without synchronization.
2. **String pool** — Only works because Strings never change. If "hello" could be mutated, every reference to that pooled String would see the change.
3. **Hash code caching** — `String.hashCode()` is computed once and cached. Safe because the value never changes. This makes Strings efficient as HashMap keys.
4. **Security** — Class names, file paths, network addresses are Strings. Immutability prevents malicious code from changing them after validation.

**How immutability is enforced:**
- The `String` class is `final` (can't subclass it)
- The internal `byte[]` (or `char[]` before Java 9) is `private final`
- No methods modify the internal state — they all return new objects

### The String Pool (String Interning)

The **String Pool** is a special area in the heap where Java caches String literals to save memory.

```java
// String literals are automatically pooled
String a = "hello";    // Creates "hello" in the pool (or reuses existing)
String b = "hello";    // Reuses the same object from the pool
System.out.println(a == b);      // true — same object!

// new String() always creates a NEW object on the heap, bypassing the pool
String c = new String("hello");  // Creates a new object (plus "hello" in pool)
System.out.println(a == c);      // false — different objects!
System.out.println(a.equals(c)); // true  — same content

// .intern() returns the pool version
String d = c.intern();
System.out.println(a == d);      // true — d now references the pooled "hello"
```

**Visual model:**

```
String Pool (special area of heap):
┌─────────────────────┐
│  "hello"  ◄───── a  │
│           ◄───── b  │
│           ◄───── d  │
│  "world"            │
└─────────────────────┘

Regular Heap:
┌─────────────────────┐
│  "hello"  ◄───── c  │  (separate copy)
└─────────────────────┘
```

**How many objects does `new String("hello")` create?**

- If `"hello"` is NOT yet in the pool: **2 objects** — one in the pool (from the literal) and one on the heap (from `new`).
- If `"hello"` is already in the pool: **1 object** — only the heap copy (from `new`).

### String Concatenation and Performance

```java
// Concatenation with + operator
String name = "Alice";
String greeting = "Hello, " + name + "!";  // Creates new String each time

// In a loop — THIS IS THE PERFORMANCE KILLER
String result = "";
for (int i = 0; i < 10000; i++) {
    result += i + ",";
    // Each += creates a new String object:
    // 1. Creates StringBuilder
    // 2. Appends old result
    // 3. Appends i + ","
    // 4. Calls toString() to create new String
    // 5. Assigns new String to result
    // = O(n²) total character copies for n iterations!
}
```

**Why O(n²)?** Each iteration copies all previous characters plus the new content:
- Iteration 1: copy 1 char
- Iteration 2: copy 2 chars
- Iteration 3: copy 3 chars
- ...
- Iteration n: copy n chars
- Total: 1 + 2 + 3 + ... + n = n(n+1)/2 = O(n²)

```java
// CORRECT: Use StringBuilder
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 10000; i++) {
    sb.append(i).append(",");
    // Appends to an internal buffer that grows as needed
    // Each append is O(1) amortized
}
String result = sb.toString();  // One final String creation
// Total: O(n) — dramatically faster
```

**Java 9+ improvement:** The compiler uses `invokedynamic`-based string concatenation (JEP 280), which is smarter about simple concatenation. But for loops, explicit `StringBuilder` is still the right choice.

### StringBuilder vs StringBuffer

Both are mutable sequences of characters with nearly identical APIs.

| Feature | StringBuilder | StringBuffer |
|---------|--------------|--------------|
| Thread-safe | No | Yes (all methods synchronized) |
| Performance | Faster | Slower (synchronization overhead) |
| Introduced | Java 1.5 | Java 1.0 |
| Use when | Single-threaded (99% of cases) | Multi-threaded access to same builder |

```java
// StringBuilder — preferred in almost all cases
StringBuilder sb = new StringBuilder("Hello");
sb.append(" World");         // "Hello World"
sb.insert(5, ",");           // "Hello, World"
sb.delete(5, 6);             // "Hello World"
sb.reverse();                // "dlroW olleH"
sb.replace(0, 5, "Java");   // "Java olleH"

int capacity = sb.capacity();  // Internal buffer size
int length = sb.length();      // Actual character count
sb.setLength(0);               // Clear — fastest way to reset

String result = sb.toString();  // Convert to immutable String
```

```java
// Pre-allocating capacity avoids resizing
// Default capacity is 16. If you know the approximate size:
StringBuilder sb = new StringBuilder(1024);  // Start with 1024 chars
```

### Compact Strings (Java 9+)

Before Java 9, `String` internally used `char[]` (2 bytes per character, always UTF-16).

Since Java 9, String uses `byte[]` + a `coder` flag:
- **LATIN1** (0): 1 byte per character — used when all characters fit in ISO-8859-1 (most ASCII/English text)
- **UTF16** (1): 2 bytes per character — used when any character is outside Latin-1

```java
String ascii = "hello";       // Stored as LATIN1 → 5 bytes (not 10)
String unicode = "héllo";     // The é forces UTF16 → 10 bytes
String emoji = "hello 🌍";   // UTF16 → more bytes
```

This is transparent to developers — no code changes needed. But it means most English-heavy applications use **half the memory** for Strings.

### Key String Methods

```java
String s = "  Hello, World!  ";

// Inspection
s.length()                   // 17
s.charAt(2)                  // 'H' (0-indexed)
s.isEmpty()                  // false
s.isBlank()                  // false (Java 11 — checks whitespace too)
" ".isBlank()                // true

// Searching
s.indexOf("World")           // 9
s.lastIndexOf('l')           // 12
s.contains("Hello")          // true
s.startsWith("  He")         // true
s.endsWith("!  ")            // true

// Extracting
s.substring(2, 7)            // "Hello"
s.trim()                     // "Hello, World!" (removes <= ' ')
s.strip()                    // "Hello, World!" (Java 11, Unicode-aware)
s.stripLeading()             // "Hello, World!  "
s.stripTrailing()            // "  Hello, World!"

// Transforming (each returns a NEW String)
s.toUpperCase()              // "  HELLO, WORLD!  "
s.toLowerCase()              // "  hello, world!  "
s.replace("World", "Java")  // "  Hello, Java!  "
s.replaceAll("\\s+", " ")   // " Hello, World! " (regex)

// Splitting
"a,b,,d".split(",")          // ["a", "b", "", "d"]
"a,b,,d".split(",", 3)       // ["a", "b", ",d"] (limit)

// Java 11+ additions
"ha".repeat(3)               // "hahaha"
"line1\nline2\nline3".lines() // Stream<String> of 3 lines

// Java 15+ formatted
"Hello %s, you are %d".formatted("Alice", 30)  // "Hello Alice, you are 30"

// Comparison
"abc".equals("abc")          // true
"abc".equalsIgnoreCase("ABC") // true
"abc".compareTo("abd")       // -1 (lexicographic)
```

---

## 7. Type Casting and Promotion Rules

### Widening (Implicit) Conversions

Widening happens automatically when no data can be lost (smaller type → larger type):

```
byte → short → int → long → float → double
                ↑
               char
```

```java
byte b = 42;
int i = b;        // Widening: byte → int (automatic)
long l = i;       // Widening: int → long (automatic)
double d = l;     // Widening: long → double (automatic)

char c = 'A';
int code = c;     // Widening: char → int (65)
```

**Subtle precision loss:** `long → float` and `long → double` are considered widening, but they can lose precision:

```java
long bigLong = 123456789012345L;
float f = bigLong;
System.out.println(f);    // 1.23456788E14 — lost precision!
System.out.println((long) f);  // 123456789012345 → may not round-trip exactly
```

### Narrowing (Explicit) Conversions

Narrowing requires an explicit cast and may lose data:

```java
double d = 3.99;
int i = (int) d;          // 3 — truncates, does NOT round!

int big = 300;
byte b = (byte) big;      // 44 — wraps around! (300 % 256 = 44)

long l = 5_000_000_000L;
int j = (int) l;          // Overflow — truncates upper bits

// Safe narrowing check:
if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
    int safe = (int) l;
} else {
    throw new ArithmeticException("Value out of int range: " + l);
}
```

### Arithmetic Promotion Rules

This is where most surprises happen:

```java
// Rule 1: byte, short, char are ALWAYS promoted to int in expressions
byte a = 10;
byte b = 20;
// byte c = a + b;    // COMPILE ERROR! a + b is promoted to int
int c = a + b;        // OK: result is int
byte d = (byte)(a + b); // OK with explicit cast

// Rule 2: If any operand is double, the whole expression is double
int x = 5;
double y = 2.0;
double result = x / y;   // 2.5 — x promoted to double

// Rule 3: If any operand is float, the whole expression is float
int p = 5;
float q = 2.0f;
float r = p / q;          // 2.5f

// Rule 4: If any operand is long, the whole expression is long
int m = 5;
long n = 2L;
long o = m + n;           // 7L

// THE CLASSIC GOTCHA: Integer division
int a2 = 7;
int b2 = 2;
double result2 = a2 / b2;     // 3.0 NOT 3.5!
// a2 / b2 is evaluated as int / int = int (3), THEN widened to double (3.0)

// Fix: cast one operand first
double correct = (double) a2 / b2;   // 3.5
double alsoOk = a2 / (double) b2;    // 3.5
```

### Object Casting (Preview for Phase 02)

```java
// Upcasting — always safe, implicit
Object obj = "hello";     // String → Object (every class extends Object)

// Downcasting — requires explicit cast, can fail at runtime
String s = (String) obj;   // OK if obj actually IS a String
// Integer i = (Integer) obj;  // ClassCastException at runtime!

// Safe downcasting with instanceof
if (obj instanceof String str) {  // Pattern matching (Java 16+)
    System.out.println(str.length());
}
```

---

## 8. var Keyword Basics

### Local Variable Type Inference (Java 10+)

`var` is not a keyword (it's a reserved type name) — it tells the compiler to infer the type from the right-hand side.

```java
// Without var
ArrayList<HashMap<String, List<Integer>>> data = new ArrayList<HashMap<String, List<Integer>>>();

// With var — the compiler infers the full type
var data = new ArrayList<HashMap<String, List<Integer>>>();
// The type of data is still ArrayList<HashMap<String, List<Integer>>>
// var is NOT dynamic typing — it's compile-time inference
```

### Where You CAN Use var

```java
// Local variables with initializers
var name = "Alice";              // Inferred as String
var count = 42;                  // Inferred as int
var prices = new double[10];     // Inferred as double[]
var list = List.of(1, 2, 3);    // Inferred as List<Integer>
var map = new HashMap<String, Integer>();  // Inferred as HashMap<String, Integer>

// In for loops
for (var item : list) {          // item is Integer
    System.out.println(item);
}
for (var i = 0; i < 10; i++) {   // i is int
    System.out.println(i);
}

// In try-with-resources
try (var reader = new BufferedReader(new FileReader("file.txt"))) {
    var line = reader.readLine();
}
```

### Where You CANNOT Use var

```java
// Fields (instance/static variables)
// var name = "Alice";           // COMPILE ERROR at class level

// Method parameters
// void greet(var name) { }     // COMPILE ERROR

// Method return types
// var getName() { return "Alice"; }  // COMPILE ERROR

// Without an initializer
// var x;                        // COMPILE ERROR: cannot infer type

// With null (type is ambiguous)
// var nothing = null;           // COMPILE ERROR

// Lambda expressions
// var fn = (x) -> x * 2;       // COMPILE ERROR: cannot infer functional interface

// Array initializer without new
// var arr = {1, 2, 3};          // COMPILE ERROR
var arr = new int[]{1, 2, 3};   // OK
```

### var with the Diamond Operator — Watch Out

```java
// Specific type on the right → var works perfectly
var list1 = new ArrayList<String>();    // ArrayList<String> — correct

// Diamond operator → no type info to infer!
var list2 = new ArrayList<>();         // ArrayList<Object> — probably not what you want!

// Rule: if you use var, provide the full type on the right side
var map = new HashMap<String, Integer>();  // Good
// var map = new HashMap<>();              // Bad: HashMap<Object, Object>
```

### Style Guidelines: When to Use var

**Good uses — type is obvious from context:**

```java
var reader = new BufferedReader(new FileReader(path));  // Obviously BufferedReader
var count = items.size();                               // Obviously int
var entry = map.entrySet().iterator().next();           // Complex type, obvious from context
var response = httpClient.send(request, ofString());    // Long generic type
```

**Bad uses — type is not obvious:**

```java
var result = computeResult();   // What type is this? Must read the method.
var data = fetchData();         // What comes back? List? Map? DTO?
var x = process(input);        // Completely opaque
```

**The principle:** Use `var` when it removes visual noise without removing understanding. If the reader has to look up the right-hand side to understand the type, spell it out.

### var Is Not Dynamic Typing

```java
var s = "hello";    // s is String at compile time
// s = 42;          // COMPILE ERROR: incompatible types (int → String)

// This is NOT like JavaScript or Python where variables can change type.
// var is purely syntactic sugar — the compiler infers a fixed type.
```

---

## Quick Reference Table

| Topic | Key Takeaway |
|-------|-------------|
| JVM | Executes bytecode; has class loader, heap/stack, JIT compiler, GC |
| JRE | JVM + standard libraries (what you need to run Java) |
| JDK | JRE + dev tools (what you need to develop Java) |
| Primitives | 8 types, not objects, live on stack, no null |
| Wrappers | Object versions of primitives, needed for generics, cached -128 to 127 |
| Autoboxing | Convenient but hides NPE risk and performance cost |
| `==` vs `.equals()` | `==` compares references for objects; always use `.equals()` for value comparison |
| Short-circuit | `&&` and `\|\|` stop early; `&` and `\|` always evaluate both sides |
| Switch expression | Java 14+, no fall-through, returns a value, enforces exhaustiveness |
| Arrays | Objects on heap, fixed size, `length` field (no parens) |
| Pass-by-value | Always. For objects, the reference is copied (not the object). |
| Varargs | `Type...` — must be last param, treated as array inside method |
| Overload resolution | Exact match → widening → boxing → varargs |
| String immutability | Every "change" creates a new object; originals stay in pool |
| String pool | Literals are interned automatically; `new String()` bypasses pool |
| StringBuilder | Mutable, fast (not thread-safe). Use for loops/concatenation. |
| StringBuffer | Mutable, slow (thread-safe). Rarely needed. |
| Widening | Small type → big type, automatic: `byte → short → int → long → float → double` |
| Narrowing | Big → small, explicit cast required, may lose data |
| Promotion | `byte + byte = int`; mixed types → widest type wins |
| `var` | Local-only, needs initializer, compile-time inference, not dynamic typing |

---

## What's Next?

Once you're comfortable with everything above and have completed the three exercises (`CliCalculator`, `StringToolkit`, `CsvParser`), run through the **Self-Assessment Checklist** in the [Phase 01 README](README.md). If you can answer every item confidently, move on to [Phase 02 — OOP Deep Dive](../phase-02-oop-deep-dive/).
