# Phase 02 — OOP Deep Dive: Interview Questions & Answers

> 100 questions organized by topic, progressing from basic to advanced.
> The final section contains cross-topic questions that combine multiple concepts.

---

## Table of Contents

1. [Classes, Objects, Constructors, `this` Keyword](#1-classes-objects-constructors-this-keyword) — Q1–Q10
2. [Encapsulation: Access Modifiers, Getters/Setters, Immutable Objects](#2-encapsulation-access-modifiers-getterssetters-immutable-objects) — Q11–Q20
3. [Inheritance: extends, super, Constructor Chaining, Method Overriding](#3-inheritance-extends-super-constructor-chaining-method-overriding) — Q21–Q32
4. [Polymorphism: Compile-Time vs. Runtime](#4-polymorphism-compile-time-vs-runtime) — Q33–Q42
5. [Abstract Classes vs. Interfaces](#5-abstract-classes-vs-interfaces) — Q43–Q52
6. [SOLID Principles](#6-solid-principles) — Q53–Q62
7. [Composition vs. Inheritance](#7-composition-vs-inheritance) — Q63–Q70
8. [Records and Sealed Classes](#8-records-and-sealed-classes) — Q71–Q78
9. [Object Class Methods: equals, hashCode, toString](#9-object-class-methods-equals-hashcode-tostring) — Q79–Q90
10. [Enums: Advanced Usage](#10-enums-advanced-usage) — Q91–Q97
11. [Cross-Topic / Combined Questions (Advanced)](#11-cross-topic--combined-questions) — Q98–Q105

---

## 1. Classes, Objects, Constructors, `this` Keyword

### Q1. What is the difference between a class and an object? ⭐ Basic

**Answer:**

| | Class | Object |
|-|-------|--------|
| **What** | A blueprint/template that defines fields and methods | A concrete instance created from that blueprint |
| **Where** | Class metadata lives in the Method Area | Objects live on the Heap |
| **How many** | Loaded once per classloader | Multiple instances can be created |
| **Created by** | Writing `.java` code and compiling | Using the `new` keyword at runtime |

```java
// Car is the class (blueprint)
public class Car {
    String color;
    int speed;
    void accelerate() { speed += 10; }
}

// myCar and yourCar are objects (instances)
Car myCar = new Car();      // Object 1 on the heap
Car yourCar = new Car();    // Object 2 on the heap — separate state
myCar.color = "red";
yourCar.color = "blue";     // Each object has its own copy of 'color'
```

---

### Q2. What happens if you define no constructor in a class? ⭐ Basic

**Answer:**

The compiler automatically generates a **default no-argument constructor** that does nothing except call `super()` (the parent's no-arg constructor):

```java
// What you write:
public class Dog { }

// What the compiler generates:
public class Dog {
    public Dog() {
        super();  // Calls Object()
    }
}
```

**Critical point:** The moment you write **any** constructor, the compiler no longer generates the default. This commonly breaks subclasses:

```java
public class Animal {
    public Animal(String name) { }  // Custom constructor — no default generated
}

public class Dog extends Animal {
    // COMPILE ERROR: There is no default constructor in Animal
    // The implicit super() call fails because Animal has no no-arg constructor
}
```

---

### Q3. What is constructor chaining? How do `this()` and `super()` work in constructors? ⭐⭐ Intermediate

**Answer:**

Constructor chaining is when one constructor calls another to reuse initialization logic.

- **`this(...)`** calls another constructor **in the same class**.
- **`super(...)`** calls a constructor **in the parent class**.

Both must be the **first statement** in the constructor body, so you cannot use both in the same constructor.

```java
public class Employee {
    private String name;
    private String dept;
    private double salary;

    // Primary constructor
    public Employee(String name, String dept, double salary) {
        this.name = name;
        this.dept = dept;
        this.salary = salary;
    }

    // Chains to the primary constructor via this(...)
    public Employee(String name) {
        this(name, "Unassigned", 0.0);  // Must be first statement
    }
}

public class Manager extends Employee {
    private int teamSize;

    public Manager(String name, double salary, int teamSize) {
        super(name, "Management", salary);  // Chains to Employee constructor
        this.teamSize = teamSize;
    }
}
```

---

### Q4. What is the execution order when you create a subclass object? ⭐⭐ Intermediate

**Answer:**

When you call `new Manager(...)`, the execution order is:

```
1. Static initializers of Object          (only on first use)
2. Static initializers of Employee        (only on first use)
3. Static initializers of Manager         (only on first use)
4. Instance initializers of Object   →  Object constructor body
5. Instance initializers of Employee →  Employee constructor body
6. Instance initializers of Manager  →  Manager constructor body
```

Constructors always run **top-down** (from `Object` to the most specific class). The parent is always fully initialized before the child constructor body runs.

```java
class Parent {
    { System.out.println("1. Parent instance block"); }
    Parent() { System.out.println("2. Parent constructor"); }
}

class Child extends Parent {
    { System.out.println("3. Child instance block"); }
    Child() { System.out.println("4. Child constructor"); }
}

new Child();
// Output:
// 1. Parent instance block
// 2. Parent constructor
// 3. Child instance block
// 4. Child constructor
```

---

### Q5. What does the `this` keyword refer to? List its uses. ⭐ Basic

**Answer:**

`this` is a reference to the **current object** — the object on which the method or constructor was called. It has four uses:

1. **Disambiguate fields from parameters:**
   ```java
   public Person(String name) {
       this.name = name;  // this.name = field, name = parameter
   }
   ```

2. **Call another constructor (constructor delegation):**
   ```java
   public Person() {
       this("Unknown", 0);  // Calls Person(String, int)
   }
   ```

3. **Pass the current object to another method:**
   ```java
   public void register() {
       registry.add(this);
   }
   ```

4. **Return the current object (fluent API):**
   ```java
   public Builder setName(String name) {
       this.name = name;
       return this;  // Enables chaining: builder.setName("A").setAge(25)
   }
   ```

`this` is NOT available in `static` methods (there is no current object in a static context).

---

### Q6. What is the difference between static and instance members? ⭐ Basic

**Answer:**

| | Instance member | Static member |
|-|----------------|---------------|
| **Belongs to** | Each object | The class itself |
| **Memory** | One copy per object (heap) | One copy per class (Method Area) |
| **Access** | Requires an object reference | Access via class name (preferred) |
| **Can access** | Both instance and static members | Only static members (no `this`) |

```java
public class Counter {
    private int instanceCount = 0;      // Each object has its own
    private static int globalCount = 0;  // Shared across all instances

    public void increment() {
        instanceCount++;  // This object's counter
        globalCount++;    // The class-wide counter
    }

    public static int getGlobalCount() {
        // return instanceCount;  // COMPILE ERROR: no instance context
        return globalCount;       // OK
    }
}
```

---

### Q7. When should you use a static method? ⭐⭐ Intermediate

**Answer:**

Use a static method when the method **does not depend on any instance state** — it operates purely on its parameters:

1. **Utility/helper methods:** `Math.abs(-5)`, `Collections.sort(list)`, `Integer.parseInt("42")`
2. **Factory methods:** `List.of(1, 2, 3)`, `LocalDate.now()`, `Optional.empty()`
3. **Static accessors for class-level data:** `Thread.currentThread()`, `Runtime.getRuntime()`

**Do NOT use static for:**
- Methods that read or modify instance fields
- Methods that should be overridden by subclasses (static methods are hidden, not overridden)
- Methods where different instances need different behavior (polymorphism requires instance methods)

---

### Q8. What are static and instance initialization blocks? When does each run? ⭐⭐ Intermediate

**Answer:**

**Static initialization block (`static { }`):**
- Runs **once** when the class is first loaded by the classloader
- Used to initialize static fields with complex logic
- Multiple static blocks run in order of appearance

**Instance initialization block (`{ }`):**
- Runs **every time** a new object is created, before the constructor body
- Used to share initialization logic across multiple constructors

```java
public class Config {
    private static final Map<String, String> defaults;
    private final String id;

    static {
        defaults = new HashMap<>();
        defaults.put("timeout", "30");
        defaults.put("retries", "3");
        System.out.println("Static block: class loaded");
    }

    {
        id = UUID.randomUUID().toString();
        System.out.println("Instance block: new object, id=" + id);
    }

    public Config() {
        System.out.println("Constructor: after instance block");
    }
}
```

---

### Q9. Can a constructor be `private`? Why would you do this? ⭐⭐ Intermediate

**Answer:**

**Yes.** A `private` constructor prevents external instantiation. Common uses:

1. **Singleton pattern:** Only one instance is ever created.
   ```java
   public class Singleton {
       private static final Singleton INSTANCE = new Singleton();
       private Singleton() { }
       public static Singleton getInstance() { return INSTANCE; }
   }
   ```

2. **Utility classes:** Classes with only static methods (should never be instantiated).
   ```java
   public final class MathUtils {
       private MathUtils() { }  // Prevent instantiation
       public static int add(int a, int b) { return a + b; }
   }
   ```

3. **Factory methods:** Force callers to use a static factory instead of `new`.
   ```java
   public class Connection {
       private Connection(String url) { ... }
       public static Connection create(String url) {
           // Validation, caching, pooling logic here
           return new Connection(url);
       }
   }
   ```

4. **Builder pattern:** The outer class has a private constructor; the builder calls it.

---

### Q10. What happens if you call an overridable method from a constructor? ⭐⭐⭐ Advanced

**Answer:**

This is a dangerous anti-pattern. The overriding method in the subclass runs **before** the subclass constructor has completed, so the subclass fields are still at their default values (null, 0, false).

```java
class Parent {
    Parent() {
        init();  // Calls overridden version in Child!
    }
    void init() {
        System.out.println("Parent.init()");
    }
}

class Child extends Parent {
    private String name;

    Child(String name) {
        super();          // Parent constructor calls init()
        this.name = name; // This runs AFTER init()!
    }

    @Override
    void init() {
        System.out.println("Child.init(): name = " + name);
        // name is still null here!
    }
}

new Child("Alice");
// Output:
// Child.init(): name = null    ← Bug! Expected "Alice"
```

**Rule:** Never call overridable (non-final, non-private) methods from a constructor. If the method is called from a constructor, make it `private` or `final`.

---

## 2. Encapsulation: Access Modifiers, Getters/Setters, Immutable Objects

### Q11. What are the four access modifiers in Java? List them from most restrictive to least. ⭐ Basic

**Answer:**

| Modifier | Same Class | Same Package | Subclass (other pkg) | World |
|----------|:---------:|:------------:|:-------------------:|:-----:|
| `private` | ✅ | ❌ | ❌ | ❌ |
| (default / package-private) | ✅ | ✅ | ❌ | ❌ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| `public` | ✅ | ✅ | ✅ | ✅ |

Note: "default" (no modifier) is often called **package-private**. It is NOT a keyword — you simply omit the modifier.

---

### Q12. Why should fields be `private`? ⭐ Basic

**Answer:**

Making fields `private` enforces **encapsulation** — hiding internal state. The benefits are:

1. **Validation:** Setters can reject invalid values.
   ```java
   public void setAge(int age) {
       if (age < 0 || age > 150) throw new IllegalArgumentException("Invalid age");
       this.age = age;
   }
   ```

2. **Internal change freedom:** You can change the field's type or representation without breaking callers.
   ```java
   // Before: stored as Fahrenheit
   private double tempF;
   public double getCelsius() { return (tempF - 32) * 5.0 / 9.0; }

   // After: changed to Celsius internally — getter API unchanged
   private double tempC;
   public double getCelsius() { return tempC; }
   ```

3. **Read-only fields:** Provide a getter without a setter.

4. **Debugging:** You can add logging or breakpoints in getters/setters.

---

### Q13. What is the `protected` access modifier? Why is it sometimes considered a design smell? ⭐⭐ Intermediate

**Answer:**

`protected` makes a member accessible to:
- The same class
- All classes in the same package
- Subclasses in **any** package

**Why it's controversial:**
- It breaks encapsulation across package boundaries — any subclass anywhere can access the member.
- It creates coupling: if you change a `protected` field in the parent, subclasses in unknown packages may break.
- Often, `protected` is used lazily instead of providing a proper API via methods.

**Appropriate uses:**
- Hook methods that subclasses are expected to override: `protected void doExecute()`
- Template method pattern: the parent defines the algorithm skeleton, subclasses fill in `protected` abstract steps
- Framework extension points (e.g., Spring's `AbstractController`)

**Default position:** Prefer `private` fields + `public`/package-private methods. Use `protected` only when subclasses genuinely need access.

---

### Q14. What is defensive copying? When do you need it? ⭐⭐ Intermediate

**Answer:**

Defensive copying means creating a copy of a mutable object when it enters or leaves your class, to prevent external code from modifying your internal state.

**When you need it:** Any time a field holds a reference to a **mutable object** (`Date`, `List`, arrays, any mutable class).

```java
public class Event {
    private final Date startDate;
    private final List<String> tags;

    public Event(Date startDate, List<String> tags) {
        this.startDate = new Date(startDate.getTime());   // Copy IN
        this.tags = new ArrayList<>(tags);                  // Copy IN
    }

    public Date getStartDate() {
        return new Date(startDate.getTime());              // Copy OUT
    }

    public List<String> getTags() {
        return Collections.unmodifiableList(tags);          // Unmodifiable view OUT
    }
}
```

**Without defensive copies:**
```java
Date d = new Date();
Event event = new Event(d, List.of("java"));
d.setYear(1900);                      // Mutates event's internal date!
event.getStartDate().setYear(2099);   // Also mutates internal state!
```

**When you DON'T need it:** For immutable types (`String`, `Integer`, `LocalDate`, `BigDecimal`).

---

### Q15. What makes a class immutable? List the requirements. ⭐⭐ Intermediate

**Answer:**

Five requirements for an immutable class:

1. **Declare the class `final`** — prevents subclasses from adding mutable state.
2. **Make all fields `private final`** — they cannot be reassigned after construction.
3. **No setter methods** — no way to modify state after creation.
4. **Defensive copy mutable fields** in the constructor (incoming) and in getters (outgoing).
5. **Don't leak mutable internal references** — never return a reference to a mutable field directly.

```java
public final class ImmutablePerson {
    private final String name;       // String is already immutable
    private final List<String> hobbies;

    public ImmutablePerson(String name, List<String> hobbies) {
        this.name = name;
        this.hobbies = List.copyOf(hobbies);  // Immutable copy
    }

    public String getName() { return name; }
    public List<String> getHobbies() { return hobbies; }  // Already immutable
}
```

---

### Q16. Why is immutability important in multithreaded programs? ⭐⭐ Intermediate

**Answer:**

Immutable objects are **inherently thread-safe** — no synchronization is needed to share them between threads. Since no thread can modify the state, there are no race conditions, no visibility problems, and no need for `synchronized`, `volatile`, or locks.

```java
// MUTABLE — not thread-safe
class MutablePoint {
    int x, y;
    void moveTo(int x, int y) {
        this.x = x;  // Thread A reads x between these two writes
        this.y = y;   // Thread A sees inconsistent state!
    }
}

// IMMUTABLE — always thread-safe
record ImmutablePoint(int x, int y) { }
// No method can change x or y after creation → no race condition possible
```

Additional benefits:
- Safe as `HashMap` keys (hash code never changes)
- Can be freely cached and shared
- Simpler to reason about — state never changes after the constructor

---

### Q17. Can a `final` reference prevent the object it points to from changing? ⭐⭐ Intermediate

**Answer:**

**No.** `final` prevents **reassignment of the reference** — you cannot point it to a different object. But the object itself can still be mutated (if it's mutable):

```java
final List<String> list = new ArrayList<>();
list.add("hello");        // ✅ Mutating the list — allowed
list.add("world");        // ✅ Still mutating — allowed
// list = new ArrayList<>();  // ❌ COMPILE ERROR: cannot reassign final reference

final int[] arr = {1, 2, 3};
arr[0] = 99;              // ✅ Mutating the array — allowed
// arr = new int[5];       // ❌ COMPILE ERROR: cannot reassign
```

**To get true immutability,** the object itself must be immutable (`List.of()`, `Collections.unmodifiableList()`, records, etc.).

---

### Q18. What is the difference between `Collections.unmodifiableList()` and `List.of()`? ⭐⭐⭐ Advanced

**Answer:**

| | `Collections.unmodifiableList(original)` | `List.of(elements...)` |
|-|------------------------------------------|------------------------|
| Creates a copy? | **No** — wraps the original list as a read-only **view** | **Yes** — creates a new independent immutable list |
| Mutations through original? | **Yes** — changes to `original` are visible through the view | **N/A** — there is no backing list |
| Null elements? | Allowed (if original contains them) | **Not allowed** — throws `NullPointerException` |

```java
List<String> original = new ArrayList<>(List.of("a", "b"));
List<String> view = Collections.unmodifiableList(original);
List<String> copy = List.copyOf(original);

original.add("c");  // Mutates the original

view.size();  // 3 — view reflects the change!
copy.size();  // 2 — copy is independent
```

**Best practice:** Use `List.copyOf()` or `List.of()` for truly immutable lists. Use `Collections.unmodifiableList()` only when you need a read-only view of a list that may legitimately change.

---

### Q19. Should you always generate getters and setters for every field? ⭐ Basic

**Answer:**

**No.** Blindly generating getters and setters for every field is "encapsulation theater" — it adds boilerplate without benefit. You've effectively made the field public, just with extra steps.

**Guidelines:**
- **Getter only:** When the field should be readable but not writable externally.
- **Setter with validation:** When external code needs to modify the field, but within constraints.
- **Neither:** When the field is an internal implementation detail.
- **Consider behavior methods instead:** Instead of `setStatus(Status s)`, consider `approve()`, `reject()`, `cancel()` — methods that express intent.

```java
// BAD: Anemic domain model with blind getters/setters
order.setStatus(Status.SHIPPED);
order.setShippedDate(LocalDate.now());

// GOOD: Behavior-rich domain model
order.ship();  // Internally sets status, date, and validates the transition
```

---

### Q20. How does the `protected` modifier interact with packages and inheritance? ⭐⭐⭐ Advanced

**Answer:**

`protected` grants access in two independent ways:

1. **Same package:** Any class in the same package can access the member, regardless of inheritance.
2. **Subclass in another package:** A subclass can access the `protected` member **only through its own type** (not through a reference to the parent type).

```java
// package com.base
public class Parent {
    protected void helper() { }
}

// package com.other
public class Child extends Parent {
    public void test() {
        helper();                  // ✅ Inherited — called on 'this'
        this.helper();             // ✅ Same as above

        Parent p = new Parent();
        // p.helper();             // ❌ COMPILE ERROR! Not through a Parent reference
                                   // from outside the package

        Child c = new Child();
        c.helper();                // ✅ Accessed through own type
    }
}
```

This restriction prevents a subclass from accessing `protected` members of arbitrary instances of the parent — it can only access them on `this` or its own subtype.

---

## 3. Inheritance: extends, super, Constructor Chaining, Method Overriding

### Q21. Does Java support multiple inheritance? ⭐ Basic

**Answer:**

**For classes: No.** A class can extend only one parent class (`extends` one).

**For interfaces: Yes.** A class can implement multiple interfaces, and an interface can extend multiple interfaces.

```java
public class Dog extends Animal implements Trainable, Serializable { }
// One parent class, multiple interfaces

public interface Readable extends Closeable, Iterable<String> { }
// An interface extending multiple interfaces
```

This design avoids the **diamond problem** for state (classes can have fields; interfaces cannot) while allowing multiple contracts (interfaces define behavior without state conflicts).

---

### Q22. What is the difference between `super` and `this`? ⭐ Basic

**Answer:**

| | `this` | `super` |
|-|--------|---------|
| Refers to | The current object | The parent class portion of the current object |
| Constructor call | `this(...)` — calls another constructor in the same class | `super(...)` — calls a parent class constructor |
| Method call | `this.method()` — calls a method on the current object | `super.method()` — calls the parent's version of an overridden method |
| Field access | `this.field` — accesses a field in the current class | `super.field` — accesses a parent class field (if shadowed) |
| In static context | Not available | Not available |

Both `this(...)` and `super(...)` must be the **first statement** in a constructor, so you can never use both in the same constructor.

---

### Q23. What are the rules for method overriding? ⭐⭐ Intermediate

**Answer:**

For a method in a subclass to override a parent method:

| Rule | Details |
|------|---------|
| **Method name** | Must be identical |
| **Parameter list** | Must be identical (types and order) |
| **Return type** | Same type or a **covariant** (more specific) return type |
| **Access modifier** | Cannot be **more restrictive** (can be equal or more permissive) |
| **Checked exceptions** | Cannot declare **broader** checked exceptions than the parent |
| **`static`** | Static methods are **hidden**, not overridden |
| **`final`** | `final` methods **cannot** be overridden |
| **`private`** | `private` methods are invisible to subclasses — no overriding |
| **`@Override`** | Not required but **always use it** to catch mistakes at compile time |

```java
class Animal {
    protected Animal create() throws IOException { return new Animal(); }
}

class Dog extends Animal {
    @Override
    public Dog create() throws FileNotFoundException {
        // ✅ Covariant return (Dog is-a Animal)
        // ✅ Access broadened (protected → public)
        // ✅ Exception narrowed (IOException → FileNotFoundException)
        return new Dog();
    }
}
```

---

### Q24. What is the difference between method hiding and method overriding? ⭐⭐ Intermediate

**Answer:**

**Overriding** applies to **instance methods**: the JVM uses the actual runtime type to decide which method to call (dynamic dispatch).

**Hiding** applies to **static methods**: the compiler uses the declared reference type (static dispatch).

```java
class Parent {
    static void staticMethod()    { System.out.println("Parent static"); }
    void instanceMethod()         { System.out.println("Parent instance"); }
}

class Child extends Parent {
    static void staticMethod()    { System.out.println("Child static"); }  // HIDING
    @Override
    void instanceMethod()         { System.out.println("Child instance"); } // OVERRIDING
}

Parent ref = new Child();
ref.staticMethod();    // "Parent static"   — hiding: uses reference type (Parent)
ref.instanceMethod();  // "Child instance"  — overriding: uses actual type (Child)
```

---

### Q25. What is the `final` keyword used for? (class, method, variable) ⭐ Basic

**Answer:**

| Applied to | Effect |
|-----------|--------|
| **`final` class** | Cannot be extended (no subclasses). Example: `String`, `Integer` |
| **`final` method** | Cannot be overridden by subclasses |
| **`final` variable** | Cannot be reassigned after initialization (single assignment) |
| **`final` parameter** | Cannot be reassigned inside the method body |

```java
public final class Utility { }           // Cannot be subclassed
// class MyUtility extends Utility { }   // COMPILE ERROR

public class Parent {
    public final void locked() { }       // Cannot be overridden
}

final int x = 10;
// x = 20;                               // COMPILE ERROR: cannot reassign
```

---

### Q26. What is a covariant return type? ⭐⭐ Intermediate

**Answer:**

A covariant return type allows an overriding method to return a **more specific** (subclass) type than the parent method's return type.

```java
class AnimalFactory {
    Animal create() { return new Animal(); }
}

class DogFactory extends AnimalFactory {
    @Override
    Dog create() { return new Dog(); }  // Dog is-a Animal → covariant return ✅
}
```

This works because any code expecting an `Animal` will also accept a `Dog` (Liskov Substitution). It was introduced in Java 5 — before that, the return type had to be identical.

**Note:** Primitive return types are NOT covariant — `int` cannot be covariant with `long`.

---

### Q27. What is the output? ⭐⭐ Intermediate

```java
class A {
    int x = 10;
    int getX() { return x; }
}

class B extends A {
    int x = 20;
    @Override
    int getX() { return x; }
}

A obj = new B();
System.out.println(obj.x);
System.out.println(obj.getX());
```

**Answer:**

```
10
20
```

**Fields are NOT polymorphic** — field access is resolved at compile time based on the **reference type** (`A`), so `obj.x` returns `A`'s `x` (10).

**Methods ARE polymorphic** — method calls are resolved at runtime based on the **actual type** (`B`), so `obj.getX()` calls `B`'s `getX()` which returns `B`'s `x` (20).

This is called **field hiding** and is another reason to always use `private` fields with getters.

---

### Q28. What is the fragile base class problem? ⭐⭐⭐ Advanced

**Answer:**

The fragile base class problem occurs when changes to a parent class **break subclass behavior** in unexpected ways, because the subclass depends on the parent's internal implementation details.

Classic example (*Effective Java* Item 18):

```java
class InstrumentedSet<E> extends HashSet<E> {
    private int addCount = 0;

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);  // HashSet.addAll() calls add() internally!
    }
}

var set = new InstrumentedSet<String>();
set.addAll(List.of("a", "b", "c"));
set.getAddCount();  // Expected 3, got 6!
```

The subclass assumed `addAll()` doesn't call `add()` — a detail of the parent's implementation that could change between JDK versions.

**Solution:** Favor composition over inheritance (wrap `HashSet` instead of extending it).

---

### Q29. Can you extend a `final` class? Can you override a `final` method? ⭐ Basic

**Answer:**

**No** to both.

```java
public final class String { ... }
// class MyString extends String { }     // COMPILE ERROR

public class Parent {
    public final void process() { ... }
}
class Child extends Parent {
    // @Override
    // public void process() { ... }     // COMPILE ERROR
}
```

**When to use `final`:**
- Classes that should not be subclassed for security/correctness: `String`, `Integer`, `LocalDate`
- Methods that define a fixed algorithm (Template Method pattern — the skeleton is `final`, the hooks are `abstract` or `protected`)
- Records are implicitly `final`

---

### Q30. What happens if a parent class constructor throws an exception? ⭐⭐⭐ Advanced

**Answer:**

If the parent constructor throws an exception, the child object is **never fully constructed**. Since `super(...)` must be the first statement, there's no way to catch it within the child constructor:

```java
class Parent {
    Parent() throws IOException {
        throw new IOException("Failed!");
    }
}

class Child extends Parent {
    Child() throws IOException {
        super();  // If this throws, the Child constructor body never runs
        // This line never executes
    }
}
```

The child constructor must declare (or be a subtype of) the exceptions thrown by the parent constructor. There is no way to suppress them.

---

### Q31. What is `super()` vs `super.method()`? ⭐ Basic

**Answer:**

| | `super()` | `super.method()` |
|-|-----------|------------------|
| **What** | Calls the parent class constructor | Calls the parent's version of a method |
| **Where** | Only in a constructor | In any instance method |
| **Restriction** | Must be the first statement | No position restriction |
| **Default behavior** | Implicit `super()` is inserted if you don't write it | No implicit call — you must explicitly write it |

```java
class Parent {
    Parent(String name) { }
    void greet() { System.out.println("Hello from Parent"); }
}

class Child extends Parent {
    Child() {
        super("default");          // Calls Parent(String)
    }

    @Override
    void greet() {
        super.greet();             // Calls Parent.greet()
        System.out.println("Hello from Child");
    }
}
```

---

### Q32. Can a constructor call both `this()` and `super()`? ⭐ Basic

**Answer:**

**No.** Both must be the first statement, so only one can appear. The compiler enforces this:

```java
class Child extends Parent {
    Child() {
        // super();    // Must be first
        // this(42);   // Also must be first — COMPILE ERROR: can't have both
    }

    Child(int x) {
        super();  // OK
    }
}
```

If you call `this(...)`, the chained constructor will eventually call `super(...)` (directly or indirectly). The chain must always end at a `super(...)` call — you cannot have an infinite `this()` loop (the compiler detects this).

---

## 4. Polymorphism: Compile-Time vs. Runtime

### Q33. What is the difference between compile-time and runtime polymorphism? ⭐ Basic

**Answer:**

| | Compile-time (static) | Runtime (dynamic) |
|-|----------------------|-------------------|
| **Mechanism** | Method **overloading** | Method **overriding** |
| **Resolved by** | Compiler, based on **declared types** | JVM, based on **actual object type** |
| **Binding** | Early binding | Late binding (virtual method dispatch) |
| **Performance** | Slightly faster (no lookup) | Negligible overhead (vtable lookup) |

```java
// Compile-time: compiler picks the method based on parameter types
void process(String s) { }
void process(int i)    { }
process("hi");  // Compiler picks process(String) at compile time

// Runtime: JVM picks the method based on actual object type
Animal a = new Dog();
a.speak();  // JVM calls Dog.speak() at runtime, not Animal.speak()
```

---

### Q34. What is upcasting? Is it safe? ⭐ Basic

**Answer:**

Upcasting is converting a subclass reference to a superclass reference. It is **always safe** and **implicit** (no cast needed).

```java
Dog dog = new Dog("Rex");
Animal animal = dog;      // Upcasting: Dog → Animal (implicit, always safe)
Object obj = dog;         // Upcasting: Dog → Object (implicit)
```

After upcasting, you can only call methods **declared in the reference type**:

```java
animal.eat();       // ✅ defined in Animal
animal.speak();     // ✅ defined in Animal (Dog's override runs at runtime)
// animal.fetch();  // ❌ COMPILE ERROR: Animal doesn't have fetch()
```

The actual object is still a `Dog` — only the compiler's "view" is restricted.

---

### Q35. What is downcasting? What can go wrong? ⭐⭐ Intermediate

**Answer:**

Downcasting is converting a superclass reference to a subclass reference. It requires an **explicit cast** and can fail at runtime with `ClassCastException`.

```java
Animal animal = new Dog("Rex");
Dog dog = (Dog) animal;         // ✅ Works — the actual object IS a Dog

Animal animal2 = new Cat("Whiskers");
Dog dog2 = (Dog) animal2;      // 💥 ClassCastException at runtime!
```

**Always use `instanceof` before downcasting:**

```java
// Traditional (pre-Java 16)
if (animal instanceof Dog) {
    Dog d = (Dog) animal;
    d.fetch();
}

// Pattern matching (Java 16+) — preferred
if (animal instanceof Dog d) {
    d.fetch();  // d is already cast and scoped
}
```

---

### Q36. What is pattern matching for `instanceof`? ⭐⭐ Intermediate

**Answer:**

Introduced in Java 16, pattern matching combines the `instanceof` check and the cast into a single expression:

```java
// Old way: check + cast (two steps)
if (obj instanceof String) {
    String s = (String) obj;
    System.out.println(s.length());
}

// New way: pattern variable (one step)
if (obj instanceof String s) {
    System.out.println(s.length());
}

// Works with negation and flow scoping
if (!(obj instanceof String s)) {
    return;  // Exit early
}
s.length();  // s is in scope here because we returned above
```

The pattern variable (`s`) is only in scope where the compiler can prove the type check succeeded.

---

### Q37. Overloading is resolved based on which type — declared or actual? ⭐⭐ Intermediate

**Answer:**

**Declared (reference) type.** The compiler picks the overloaded method at compile time based on the compile-time type of each argument:

```java
class Formatter {
    String format(Object obj)  { return "Object: " + obj; }
    String format(String str)  { return "String: " + str; }
    String format(Integer num) { return "Integer: " + num; }
}

Object x = "hello";       // Declared type: Object, actual type: String
Formatter f = new Formatter();
f.format(x);              // "Object: hello" — compiler sees Object, picks format(Object)
f.format("hello");         // "String: hello" — compiler sees String, picks format(String)
f.format((String) x);     // "String: hello" — explicit cast changes declared type
```

This is one of the most important distinctions in Java OOP.

---

### Q38. What is the output? ⭐⭐⭐ Advanced

```java
class Parent {
    String identify() { return "Parent"; }
}

class Child extends Parent {
    @Override
    String identify() { return "Child"; }
}

class GrandChild extends Child {
    @Override
    String identify() { return "GrandChild"; }
}

Parent obj = new GrandChild();
System.out.println(obj.identify());
```

**Answer:**

```
GrandChild
```

Runtime polymorphism always calls the **most specific override** based on the actual object type. The JVM walks the type hierarchy from the actual type upward until it finds an implementation. `GrandChild` has its own `identify()`, so that's what runs — regardless of the reference type being `Parent`.

---

### Q39. Can you achieve polymorphism with `static` methods? ⭐⭐ Intermediate

**Answer:**

**No.** Static methods use **static dispatch** — the method is selected at compile time based on the reference type, not the actual object type. Static methods are **hidden**, not overridden:

```java
class A {
    static String who() { return "A"; }
}
class B extends A {
    static String who() { return "B"; }  // Hiding, NOT overriding
}

A ref = new B();
ref.who();   // "A" — resolved by reference type at compile time
B.who();     // "B" — called on the class directly
```

The `@Override` annotation on a static method would cause a compile error, confirming that overriding doesn't apply.

---

### Q40. What is the output? ⭐⭐ Intermediate

```java
class Animal {
    void speak() { System.out.println("..."); }
}

class Dog extends Animal {
    @Override
    void speak() { System.out.println("Woof"); }

    void fetch() { System.out.println("Fetching!"); }
}

Animal a = new Dog();
a.speak();
// a.fetch();   // Uncomment — what happens?
```

**Answer:**

`a.speak()` prints `"Woof"` — runtime polymorphism calls `Dog.speak()`.

`a.fetch()` would **not compile**. The reference type is `Animal`, and the compiler only checks if the method exists in `Animal`. Since `fetch()` is not declared in `Animal`, the call is rejected at compile time — even though the actual object is a `Dog` and has `fetch()`.

**To call `fetch()`, you must downcast:**
```java
if (a instanceof Dog d) {
    d.fetch();  // ✅ Now the compiler knows it's a Dog
}
```

---

### Q41. Why does polymorphism eliminate `if/else` chains? ⭐⭐ Intermediate

**Answer:**

Without polymorphism, you check the type manually and branch:

```java
double area(Object shape) {
    if (shape instanceof Circle c) return Math.PI * c.r * c.r;
    if (shape instanceof Rectangle r) return r.w * r.h;
    // Every new shape type requires adding another if-branch HERE
    throw new IllegalArgumentException("Unknown");
}
```

With polymorphism, each type carries its own behavior:

```java
abstract class Shape { abstract double area(); }
class Circle extends Shape { @Override double area() { return Math.PI * r * r; } }
class Rectangle extends Shape { @Override double area() { return w * h; } }
// New shape? Just add a new class — area() caller code doesn't change
```

This is the **Open/Closed Principle** in action: the system is open for extension (new shapes) but closed for modification (existing code).

---

### Q42. Can you override a method and make it `static`? Or vice versa? ⭐⭐ Intermediate

**Answer:**

**No** to both. You cannot override an instance method with a static method, or vice versa:

```java
class Parent {
    void instanceMethod() { }
    static void staticMethod() { }
}

class Child extends Parent {
    // static void instanceMethod() { }  // COMPILE ERROR: cannot make static
    // void staticMethod() { }           // COMPILE ERROR: cannot make non-static
}
```

An instance method and a static method live in fundamentally different dispatch mechanisms. The compiler enforces that overriding preserves the static/instance nature.

---

## 5. Abstract Classes vs. Interfaces

### Q43. What is the difference between an abstract class and an interface? ⭐ Basic

**Answer:**

| Feature | Abstract Class | Interface |
|---------|---------------|-----------|
| **State** | Can have instance fields | Only `static final` constants |
| **Constructors** | Yes | No |
| **Method implementations** | Concrete + abstract | Abstract + `default` + `static` + `private` |
| **Inheritance** | A class can extend **one** abstract class | A class can implement **many** interfaces |
| **Access modifiers** | Any (`private`, `protected`, etc.) | Methods are implicitly `public` (except `private` since Java 9) |
| **Represents** | "Is-a" relationship | "Can-do" capability / contract |

---

### Q44. When should you use an abstract class vs. an interface? ⭐⭐ Intermediate

**Answer:**

**Use an interface when:**
- You're defining a **capability** or **contract**: `Comparable`, `Serializable`, `Iterable`
- You need **multiple inheritance** of type: a class needs to be both `Drawable` and `Clickable`
- You want to define a functional interface for lambda use

**Use an abstract class when:**
- Related classes share **common state** (fields) or **constructor logic**
- You need `protected` methods for internal subclass communication
- You want to provide a partially-implemented base that subclasses complete (Template Method pattern)

**When in doubt:** Start with an interface. You can always introduce an abstract class later as a convenience base implementation (e.g., `List` interface + `AbstractList` abstract class).

---

### Q45. Can an abstract class have a constructor? Why, if you can't instantiate it? ⭐⭐ Intermediate

**Answer:**

**Yes.** You cannot call `new AbstractClass()` directly, but the constructor is called by **subclass constructors** via `super(...)`:

```java
abstract class Shape {
    private final String color;

    protected Shape(String color) {
        this.color = color;  // Shared initialization logic
    }

    public String getColor() { return color; }
    public abstract double area();
}

class Circle extends Shape {
    private double radius;

    public Circle(String color, double radius) {
        super(color);          // Calls Shape(String)
        this.radius = radius;
    }

    @Override
    public double area() { return Math.PI * radius * radius; }
}
```

The abstract class constructor ensures every subclass is properly initialized with shared state.

---

### Q46. What are `default` methods in interfaces? Why were they added? ⭐⭐ Intermediate

**Answer:**

`default` methods (Java 8) allow interfaces to provide **method implementations** that implementing classes inherit automatically.

**Why they were added:** To enable **interface evolution** without breaking existing implementations. When `Iterable` wanted to add `forEach()` in Java 8, adding an abstract method would break every existing class that implemented `Iterable`. Making it `default` provided a sensible implementation that existing classes got for free.

```java
public interface Iterable<T> {
    Iterator<T> iterator();  // Abstract — all implementations must provide

    default void forEach(Consumer<? super T> action) {  // Default — free for everyone
        for (T t : this) {
            action.accept(t);
        }
    }
}
```

Implementing classes can override the `default` method if they need custom behavior.

---

### Q47. What happens when a class inherits the same `default` method from two interfaces? ⭐⭐ Intermediate

**Answer:**

The class gets a **compile error** and must resolve the conflict by explicitly overriding the method:

```java
interface Flyable {
    default String describe() { return "I can fly"; }
}

interface Swimmable {
    default String describe() { return "I can swim"; }
}

class Duck implements Flyable, Swimmable {
    // COMPILE ERROR without this override!
    @Override
    public String describe() {
        return Flyable.super.describe() + " and " + Swimmable.super.describe();
    }
}
```

The syntax `InterfaceName.super.method()` allows you to call a specific interface's default method.

---

### Q48. Can an interface have `private` methods? ⭐⭐ Intermediate

**Answer:**

**Yes, since Java 9.** `private` methods in interfaces allow you to share code between `default` methods without exposing it:

```java
public interface Reportable {
    default String htmlReport() {
        return wrap("<html>", "</html>", getContent());
    }

    default String xmlReport() {
        return wrap("<report>", "</report>", getContent());
    }

    String getContent();

    // Private helper — shared by both default methods, hidden from implementors
    private String wrap(String open, String close, String content) {
        return open + content + close;
    }
}
```

---

### Q49. What is a functional interface? ⭐ Basic

**Answer:**

A functional interface has **exactly one abstract method** (SAM — Single Abstract Method). It can have any number of `default`, `static`, and `private` methods. Functional interfaces can be implemented using **lambda expressions**.

```java
@FunctionalInterface  // Optional annotation — compiler enforces the SAM constraint
public interface Converter<F, T> {
    T convert(F from);              // The single abstract method

    default Converter<F, T> andLog() {  // Default methods don't count
        return from -> {
            T result = convert(from);
            System.out.println(from + " → " + result);
            return result;
        };
    }
}

// Lambda implementation
Converter<String, Integer> toInt = Integer::parseInt;
toInt.convert("42");  // 42
```

Common functional interfaces from `java.util.function`: `Predicate<T>`, `Function<T,R>`, `Consumer<T>`, `Supplier<T>`, `Comparator<T>`.

---

### Q50. Can an abstract class implement an interface without implementing its methods? ⭐⭐ Intermediate

**Answer:**

**Yes.** An abstract class can declare that it implements an interface but leave some (or all) methods unimplemented. The responsibility passes to the first **concrete** subclass:

```java
interface Printable {
    void print();
    void preview();
}

abstract class Document implements Printable {
    @Override
    public void print() {
        System.out.println("Printing...");
    }
    // preview() is NOT implemented — OK because Document is abstract
}

class Invoice extends Document {
    @Override
    public void preview() {
        System.out.println("Invoice preview");
    }
    // print() is inherited from Document — no need to implement again
}
```

---

### Q51. What is a marker interface? Give examples. ⭐⭐ Intermediate

**Answer:**

A marker interface has **no methods** — it's an empty interface used to "tag" a class with metadata that the runtime or framework checks with `instanceof`.

**Standard library examples:**
- `Serializable` — marks a class as serializable by Java's serialization mechanism
- `Cloneable` — marks that `Object.clone()` should not throw `CloneNotSupportedException`
- `RandomAccess` — marks that a `List` supports fast random access (e.g., `ArrayList`)

```java
public class User implements Serializable {
    // No methods to implement — Serializable is a marker
}

if (obj instanceof Serializable) {
    // Runtime can safely serialize this object
}
```

**Modern alternative:** Annotations (`@Entity`, `@Deprecated`) often replace marker interfaces because they can carry additional metadata.

---

### Q52. Why does `Comparable` go on the class but `Comparator` is separate? ⭐⭐⭐ Advanced

**Answer:**

| | `Comparable<T>` | `Comparator<T>` |
|-|----------------|-----------------|
| **Where** | Implemented by the class itself | A separate, external class or lambda |
| **Method** | `int compareTo(T other)` | `int compare(T a, T b)` |
| **Name** | "Natural ordering" | "Custom ordering" |
| **Count** | One per class | Unlimited — different strategies |

```java
// Comparable: the class defines its own natural ordering
class Employee implements Comparable<Employee> {
    @Override
    public int compareTo(Employee other) {
        return this.name.compareTo(other.name);  // Natural order: by name
    }
}
Collections.sort(employees);  // Uses natural order

// Comparator: external, multiple strategies
Comparator<Employee> bySalary = Comparator.comparing(Employee::getSalary);
Comparator<Employee> byHireDate = Comparator.comparing(Employee::getHireDate);
employees.sort(bySalary);      // Sort by salary
employees.sort(byHireDate);    // Sort by hire date
```

A class should implement `Comparable` for its most obvious ordering (alphabetical for `String`, numeric for `Integer`). Use `Comparator` for any alternative orderings.

---

## 6. SOLID Principles

### Q53. What does SRP (Single Responsibility Principle) mean? How do you identify violations? ⭐ Basic

**Answer:**

**"A class should have one, and only one, reason to change."**

A "reason to change" is tied to a **stakeholder** or **business concern**. If a class serves multiple stakeholders, a change requested by one can break functionality for another.

**Identifying violations:**
- The class has methods that serve unrelated purposes (calculation + formatting + persistence)
- The class name includes "And" or "Manager" or "Handler" that covers multiple concerns
- You can extract a coherent subset of methods into a separate class with a clear name

```java
// VIOLATION: Three reasons to change
class UserService {
    void createUser(User u) { ... }        // Business logic
    String formatUserReport(User u) { ... } // Presentation
    void sendWelcomeEmail(User u) { ... }   // Notification
}

// FIXED: One responsibility each
class UserService { void createUser(User u) { ... } }
class UserReportFormatter { String format(User u) { ... } }
class UserNotifier { void sendWelcomeEmail(User u) { ... } }
```

---

### Q54. Explain the Open/Closed Principle with a code example. ⭐⭐ Intermediate

**Answer:**

**"Open for extension, closed for modification."** You should be able to add new behavior without changing existing code.

```java
// VIOLATION: Adding a new discount type requires modifying this method
double getDiscount(String type, double price) {
    return switch (type) {
        case "regular"  -> price * 0.05;
        case "premium"  -> price * 0.10;
        case "vip"      -> price * 0.20;
        // Adding "employee" means modifying this code
        default -> 0;
    };
}

// FIXED: Strategy pattern — extend by adding new classes
interface DiscountPolicy {
    double apply(double price);
}

class RegularDiscount implements DiscountPolicy {
    public double apply(double price) { return price * 0.05; }
}

class VipDiscount implements DiscountPolicy {
    public double apply(double price) { return price * 0.20; }
}

// Adding "employee" discount = new class, zero modifications to existing code
class EmployeeDiscount implements DiscountPolicy {
    public double apply(double price) { return price * 0.30; }
}
```

---

### Q55. Explain Liskov Substitution with the Rectangle/Square problem. ⭐⭐ Intermediate

**Answer:**

**"Subtypes must be substitutable for their base types without breaking correctness."**

The classic violation: making `Square` extend `Rectangle`.

```java
class Rectangle {
    protected int width, height;
    void setWidth(int w)  { width = w; }
    void setHeight(int h) { height = h; }
    int area()            { return width * height; }
}

class Square extends Rectangle {
    @Override void setWidth(int w)  { width = w; height = w; }  // Breaks Rectangle contract
    @Override void setHeight(int h) { width = h; height = h; }  // Breaks Rectangle contract
}
```

Code that works with `Rectangle` breaks with `Square`:

```java
void test(Rectangle r) {
    r.setWidth(5);
    r.setHeight(10);
    assert r.area() == 50;  // ✅ with Rectangle, ❌ with Square (100!)
}
```

**The fix:** Don't model "Square is-a Rectangle" with inheritance. Use separate types sharing a `Shape` interface, or use immutable value types where the problem doesn't arise.

---

### Q56. What is the Interface Segregation Principle? Why do fat interfaces cause problems? ⭐⭐ Intermediate

**Answer:**

**"No client should be forced to depend on methods it doesn't use."**

Fat interfaces force implementors to provide stubs or throw `UnsupportedOperationException` for irrelevant methods:

```java
// FAT interface — forces unnecessary implementations
interface MultiFunctionDevice {
    void print(Document d);
    void scan(Document d);
    void fax(Document d);
    void staple(Document d);
}

// A simple printer doesn't fax or staple!
class SimplePrinter implements MultiFunctionDevice {
    public void print(Document d) { /* actual logic */ }
    public void scan(Document d) { throw new UnsupportedOperationException(); }
    public void fax(Document d) { throw new UnsupportedOperationException(); }
    public void staple(Document d) { throw new UnsupportedOperationException(); }
}
```

**Fixed:**

```java
interface Printer  { void print(Document d); }
interface Scanner  { void scan(Document d); }
interface Fax      { void fax(Document d); }

class SimplePrinter implements Printer {
    public void print(Document d) { /* actual logic */ }
}

class AllInOne implements Printer, Scanner, Fax {
    public void print(Document d) { ... }
    public void scan(Document d) { ... }
    public void fax(Document d) { ... }
}
```

---

### Q57. Explain Dependency Inversion with constructor injection. ⭐⭐ Intermediate

**Answer:**

**"High-level modules should depend on abstractions, not concrete implementations."**

Constructor injection is the most common way to implement DIP — dependencies are passed in via the constructor:

```java
// Abstraction
interface MessageSender {
    void send(String to, String body);
}

// Low-level implementations
class EmailSender implements MessageSender {
    public void send(String to, String body) { /* SMTP logic */ }
}

class SmsSender implements MessageSender {
    public void send(String to, String body) { /* SMS API logic */ }
}

// High-level module depends on abstraction
class OrderService {
    private final MessageSender sender;  // Depends on interface, not class

    public OrderService(MessageSender sender) {  // Injected via constructor
        this.sender = sender;
    }

    public void placeOrder(Order order) {
        // ... business logic ...
        sender.send(order.getEmail(), "Order confirmed");
    }
}

// Composition root — wire dependencies
OrderService emailOrders = new OrderService(new EmailSender());
OrderService smsOrders = new OrderService(new SmsSender());

// For testing — inject a mock
OrderService testOrders = new OrderService((to, body) -> log.info("Mock: " + body));
```

---

### Q58. Can you give a real-world example where SOLID was violated and caused pain? ⭐⭐⭐ Advanced

**Answer:**

**Scenario:** A `ReportService` class that generates reports in PDF, Excel, and CSV formats:

```java
class ReportService {
    void generateReport(String format, Data data) {
        if ("pdf".equals(format)) {
            // 200 lines of PDF generation
        } else if ("excel".equals(format)) {
            // 150 lines of Excel generation
        } else if ("csv".equals(format)) {
            // 50 lines of CSV generation
        }
    }
}
```

**Violations:**
- **SRP:** One class handles three unrelated format concerns.
- **OCP:** Adding HTML format requires modifying this class.
- **DIP:** Caller is coupled to a concrete class, not an abstraction.

**Pain caused:**
- Adding JSON format required modifying a 400-line method — risk of breaking PDF/Excel.
- A bug fix in CSV formatting accidentally broke Excel output (shared variables).
- Impossible to unit test PDF generation without also loading Excel libraries.

**Fix:**

```java
interface ReportGenerator {
    String getFormat();
    byte[] generate(Data data);
}

class PdfReportGenerator implements ReportGenerator { ... }
class ExcelReportGenerator implements ReportGenerator { ... }
class CsvReportGenerator implements ReportGenerator { ... }

class ReportService {
    private final Map<String, ReportGenerator> generators;

    ReportService(List<ReportGenerator> generators) {
        this.generators = generators.stream()
            .collect(Collectors.toMap(ReportGenerator::getFormat, g -> g));
    }

    byte[] generate(String format, Data data) {
        ReportGenerator gen = generators.get(format);
        if (gen == null) throw new IllegalArgumentException("Unknown: " + format);
        return gen.generate(data);
    }
}
```

---

### Q59. Is SOLID always applicable? When might you intentionally violate it? ⭐⭐⭐ Advanced

**Answer:**

SOLID principles are guidelines, not laws. Over-applying them can lead to **over-engineering** — too many tiny classes with excessive indirection.

**Intentional violations:**

1. **SRP:** A small CRUD controller that handles validation, persistence, and response formatting in one place. Splitting into 3 classes for a 30-line controller adds complexity without benefit.

2. **OCP:** If a `switch` has 3 cases and will never grow, creating an interface + 3 implementation classes + a factory is overkill.

3. **DIP:** A utility class that directly uses `System.currentTimeMillis()` doesn't need a `TimeProvider` interface unless you need to test with fake clocks.

**Rule of thumb:** Apply SOLID when the **cost of violation exceeds the cost of abstraction**. For stable, small, rarely-changing code, simplicity wins.

---

### Q60. What is the difference between DIP and Dependency Injection? ⭐⭐ Intermediate

**Answer:**

| | Dependency Inversion Principle (DIP) | Dependency Injection (DI) |
|-|--------------------------------------|---------------------------|
| **What** | A design **principle** (from SOLID) | A **technique** / **pattern** |
| **Says** | "Depend on abstractions, not concretions" | "Pass dependencies in from outside instead of creating them inside" |
| **How** | Define interfaces for dependencies | Constructor injection, setter injection, or DI frameworks (Spring, Guice) |

DIP tells you **what** to do (depend on abstractions). DI tells you **how** to do it (inject the concrete implementations from outside).

```java
// DIP: OrderService depends on the PaymentGateway INTERFACE
// DI: The concrete StripeGateway is INJECTED via the constructor
class OrderService {
    private final PaymentGateway gateway;  // DIP: abstraction
    OrderService(PaymentGateway gateway) { this.gateway = gateway; }  // DI: injection
}
```

You can follow DIP without a DI framework (manual wiring). And you can use a DI framework without following DIP (injecting concrete classes).

---

### Q61. How does the Strategy pattern relate to OCP? ⭐⭐ Intermediate

**Answer:**

The Strategy pattern is the **primary implementation technique** for OCP. It replaces conditionals with polymorphic behavior:

```
OCP says:   "Open for extension, closed for modification"
Strategy:   Define a family of algorithms, encapsulate each one,
            and make them interchangeable.
```

Each strategy is a new class. Adding a new strategy extends the system without modifying the code that uses strategies:

```java
interface SortStrategy {
    <T extends Comparable<T>> void sort(List<T> items);
}

class QuickSort implements SortStrategy { ... }
class MergeSort implements SortStrategy { ... }
class TimSort implements SortStrategy { ... }
// New strategy = new class, zero changes to existing code

class DataProcessor {
    private final SortStrategy strategy;  // Closed for modification
    DataProcessor(SortStrategy s) { this.strategy = s; }

    void process(List<String> data) {
        strategy.sort(data);  // Open for extension
    }
}
```

---

### Q62. How do you test code that follows DIP? ⭐⭐ Intermediate

**Answer:**

When code depends on interfaces (DIP), you can inject **mock or stub implementations** for testing:

```java
// Production code
class OrderService {
    private final PaymentGateway gateway;
    private final InventoryService inventory;

    OrderService(PaymentGateway gateway, InventoryService inventory) {
        this.gateway = gateway;
        this.inventory = inventory;
    }

    boolean placeOrder(Order order) {
        if (!inventory.isAvailable(order.getItem())) return false;
        return gateway.charge(order.getTotal());
    }
}

// Test — inject fake implementations
@Test
void testPlaceOrder_success() {
    PaymentGateway fakeGateway = amount -> true;         // Always succeeds
    InventoryService fakeInventory = item -> true;       // Always available

    OrderService service = new OrderService(fakeGateway, fakeInventory);
    assertTrue(service.placeOrder(new Order("Widget", 9.99)));
}

@Test
void testPlaceOrder_outOfStock() {
    PaymentGateway fakeGateway = amount -> true;
    InventoryService fakeInventory = item -> false;      // Out of stock

    OrderService service = new OrderService(fakeGateway, fakeInventory);
    assertFalse(service.placeOrder(new Order("Widget", 9.99)));
}
```

Without DIP (if `OrderService` directly created `new StripeGateway()`), you'd need to set up a real Stripe connection in tests — slow, brittle, and expensive.

---

## 7. Composition vs. Inheritance

### Q63. What does "Favor composition over inheritance" mean? ⭐ Basic

**Answer:**

It means: when you need to reuse behavior from another class, prefer **holding a reference** to it (composition / "has-a") rather than **extending** it (inheritance / "is-a").

**Inheritance:** `Dog extends Animal` — Dog gets everything Animal has, tightly coupled.
**Composition:** `Car has-a Engine` — Car delegates to Engine, loosely coupled.

Benefits of composition:
- You can change the delegate at runtime (swap implementations)
- You expose only the methods you choose (no unwanted inherited methods)
- You're not coupled to the parent's implementation details
- You can compose from multiple sources (Java has single inheritance)

---

### Q64. Show a refactoring from inheritance to composition. ⭐⭐ Intermediate

**Answer:**

```java
// BEFORE: Inheritance — Stack inherits all ArrayList methods
class Stack<E> extends ArrayList<E> {
    void push(E item) { add(item); }
    E pop() { return remove(size() - 1); }
}

// Problem: callers can call add(index, element), get(index), set(), etc.
// These break stack discipline (LIFO)

// AFTER: Composition — Stack hides ArrayList behind a clean API
class Stack<E> {
    private final List<E> items = new ArrayList<>();  // Delegation target

    void push(E item) { items.add(item); }

    E pop() {
        if (items.isEmpty()) throw new EmptyStackException();
        return items.remove(items.size() - 1);
    }

    E peek() {
        if (items.isEmpty()) throw new EmptyStackException();
        return items.get(items.size() - 1);
    }

    boolean isEmpty() { return items.isEmpty(); }
    int size() { return items.size(); }

    // No add(), get(), set(), remove() exposed — stack discipline enforced
}
```

---

### Q65. What is the Decorator pattern and how does it use composition? ⭐⭐ Intermediate

**Answer:**

The Decorator wraps an object to **add behavior** without modifying it. It uses composition (has-a wrapped object) and implements the same interface.

```java
interface DataSource {
    String read();
    void write(String data);
}

class FileDataSource implements DataSource {
    public String read() { return readFromFile(); }
    public void write(String data) { writeToFile(data); }
}

class EncryptionDecorator implements DataSource {
    private final DataSource wrapped;  // Composition

    EncryptionDecorator(DataSource wrapped) { this.wrapped = wrapped; }

    public String read() { return decrypt(wrapped.read()); }
    public void write(String data) { wrapped.write(encrypt(data)); }
}

class CompressionDecorator implements DataSource {
    private final DataSource wrapped;

    CompressionDecorator(DataSource wrapped) { this.wrapped = wrapped; }

    public String read() { return decompress(wrapped.read()); }
    public void write(String data) { wrapped.write(compress(data)); }
}

// Stack decorators dynamically:
DataSource source = new CompressionDecorator(
                        new EncryptionDecorator(
                            new FileDataSource()));
source.write("secret data");  // Compressed, then encrypted, then written to file
```

Java's `BufferedInputStream(new FileInputStream(...))` uses this exact pattern.

---

### Q66. When IS inheritance the right choice? ⭐⭐ Intermediate

**Answer:**

Inheritance is appropriate when **all** of these conditions are met:

1. **Clear "is-a" relationship:** A `Dog` genuinely IS an `Animal`, not just "uses animal behavior."
2. **You control the superclass:** You can ensure changes to the parent don't break subclasses.
3. **The subclass is a genuine specialization:** It overrides behavior or adds semantics, not just reuses methods.
4. **The hierarchy is shallow:** 2–3 levels max. Deep hierarchies are fragile.
5. **Liskov Substitution holds:** Anywhere the parent is used, the subclass works correctly.

**Good inheritance examples in Java:**
- `ArrayList extends AbstractList` (is-a List, AbstractList provides shared logic)
- `IOException extends Exception` (is-an Exception, natural hierarchy)
- `HttpServletRequest extends ServletRequest` (HTTP specializes the servlet contract)

---

### Q67. What is the delegation pattern? ⭐⭐ Intermediate

**Answer:**

Delegation is when an object handles a request by forwarding it to a helper object (the delegate) rather than implementing it itself:

```java
class Printer {
    void print(String content) {
        System.out.println(content);
    }
}

class Report {
    private final Printer printer;  // Delegate

    Report(Printer printer) {
        this.printer = printer;
    }

    void publish() {
        String content = generateContent();
        printer.print(content);  // Delegate the printing
    }
}
```

Delegation is the mechanism behind composition. It gives you the same code reuse as inheritance but without the tight coupling.

---

### Q68. Can composition achieve polymorphism like inheritance does? ⭐⭐⭐ Advanced

**Answer:**

**Yes**, through interfaces. Composition + interfaces provides the same polymorphic behavior as inheritance:

```java
// With inheritance:
abstract class Notifier {
    abstract void send(String msg);
}
class EmailNotifier extends Notifier {
    void send(String msg) { /* email */ }
}
class SmsNotifier extends Notifier {
    void send(String msg) { /* sms */ }
}

// With composition + interface:
interface Notifier {
    void send(String msg);
}
class EmailNotifier implements Notifier {
    void send(String msg) { /* email */ }
}
class SmsNotifier implements Notifier {
    void send(String msg) { /* sms */ }
}
```

In both cases, client code works with `Notifier` and runtime polymorphism dispatches to the correct implementation. The interface-based approach is more flexible because classes can implement multiple interfaces.

---

### Q69. How does Java's I/O library demonstrate both inheritance and composition? ⭐⭐⭐ Advanced

**Answer:**

Java I/O uses **inheritance** for the type hierarchy and **composition (Decorator)** for adding behavior:

```
Inheritance hierarchy:
    InputStream (abstract)
      ├── FileInputStream       (reads from file)
      ├── ByteArrayInputStream  (reads from byte array)
      └── FilterInputStream     (abstract decorator base)
            ├── BufferedInputStream    (adds buffering)
            ├── DataInputStream        (adds typed reads)
            └── GZIPInputStream        (adds decompression)
```

Decorators compose at runtime:

```java
InputStream stream = new GZIPInputStream(       // Decompresses
                         new BufferedInputStream(    // Buffers
                             new FileInputStream("data.gz"))); // Reads file
```

Each decorator **wraps** (has-a) another `InputStream` and adds a layer of behavior. You can combine them in any order. This is far more flexible than creating `BufferedGZIPFileInputStream` through inheritance.

---

### Q70. What is the "diamond problem" and how does Java handle it? ⭐⭐ Intermediate

**Answer:**

The diamond problem occurs when a class inherits from two sources that share a common ancestor, creating ambiguity about which version of a method or field to use:

```
      A
     / \
    B   C
     \ /
      D
```

**For classes:** Java avoids the problem entirely by **prohibiting multiple class inheritance** (`extends` only one class).

**For interfaces:** Java allows it but resolves conflicts explicitly:
- If two interfaces provide the same `default` method, the implementing class **must override** it.
- If one is a class and the other is an interface, the **class wins** (class methods take priority over interface defaults).

```java
interface A { default void greet() { System.out.println("A"); } }
interface B extends A { default void greet() { System.out.println("B"); } }

class C implements A, B {
    // No conflict — B is more specific than A
    // B.greet() is inherited automatically
}
```

---

## 8. Records and Sealed Classes

### Q71. What is a Java record? What does it generate automatically? ⭐ Basic

**Answer:**

A record is a concise way to declare an **immutable data class**. The compiler generates:

1. A `private final` field for each component
2. A canonical constructor
3. A public accessor method for each component (named the same as the component, e.g., `name()`, not `getName()`)
4. `equals()` — compares all components
5. `hashCode()` — based on all components
6. `toString()` — includes class name and all component values

```java
// This single line:
public record Point(int x, int y) {}

// Is equivalent to ~40 lines of a traditional class
```

**Restrictions:**
- Implicitly `final` — cannot be extended
- Cannot declare additional instance fields (only `static` fields)
- Cannot extend a class (implicitly extends `java.lang.Record`)
- Can implement interfaces

---

### Q72. What is a compact constructor? How does it differ from a canonical constructor? ⭐⭐ Intermediate

**Answer:**

A **canonical constructor** has explicit parameters matching the record components. A **compact constructor** omits the parameter list and the field assignments (the compiler adds them after the body):

```java
// Canonical constructor (explicit)
public record Email(String address) {
    public Email(String address) {
        if (address == null || !address.contains("@")) {
            throw new IllegalArgumentException("Invalid: " + address);
        }
        this.address = address.toLowerCase();  // Must assign explicitly
    }
}

// Compact constructor (concise)
public record Email(String address) {
    public Email {  // No parameter list!
        if (address == null || !address.contains("@")) {
            throw new IllegalArgumentException("Invalid: " + address);
        }
        address = address.toLowerCase();  // Reassigns the PARAMETER, not the field
        // The compiler adds: this.address = address; AFTER this body
    }
}
```

In a compact constructor, you modify the **parameter** (which shadows the field). After the body, the compiler generates `this.field = parameter` for each component.

---

### Q73. Can a record implement an interface? Can it extend a class? ⭐ Basic

**Answer:**

- **Implement interfaces:** ✅ Yes
- **Extend a class:** ❌ No (records implicitly extend `java.lang.Record`)

```java
interface Printable {
    String toPrintableString();
}

record Invoice(String id, double total) implements Printable {
    @Override
    public String toPrintableString() {
        return "Invoice #" + id + ": $" + total;
    }
}

// record SpecialInvoice extends Invoice { }  // COMPILE ERROR: records can't extend
// class Child extends Invoice { }            // COMPILE ERROR: records are final
```

---

### Q74. What is a sealed class? Why was it introduced? ⭐⭐ Intermediate

**Answer:**

A sealed class **restricts which classes can extend it** using the `permits` clause. Only the listed classes are allowed as direct subclasses.

```java
public abstract sealed class Shape permits Circle, Rectangle, Triangle { }
```

**Why introduced (Java 17):**
1. **Exhaustive pattern matching:** The compiler knows all subtypes, so `switch` can verify all cases are covered without a `default`.
2. **Domain modeling:** You can model a closed set of alternatives (like algebraic data types in functional languages).
3. **Security/correctness:** Prevents unauthorized subclasses from being injected into your type hierarchy.

Each permitted subclass must be:
- `final` — no further extension
- `sealed` — further restricted extension chain
- `non-sealed` — opens the seal (escape hatch)

---

### Q75. What is the output? ⭐⭐ Intermediate

```java
record Point(int x, int y) {
    Point {
        if (x < 0 || y < 0) throw new IllegalArgumentException("Negative!");
    }
}

Point p1 = new Point(3, 4);
System.out.println(p1);
System.out.println(p1.x());
System.out.println(p1.equals(new Point(3, 4)));
System.out.println(p1.equals(new Point(4, 3)));
```

**Answer:**

```
Point[x=3, y=4]    — auto-generated toString()
3                   — accessor x() returns the x component
true                — auto-generated equals() compares all components (3==3, 4==4)
false               — (3!=4 or 4!=3) → not equal
```

---

### Q76. How do sealed classes enable exhaustive `switch`? ⭐⭐⭐ Advanced

**Answer:**

When you `switch` on a sealed type, the compiler knows every possible subtype at compile time. If your `switch` covers all permitted subclasses, no `default` is needed:

```java
sealed interface Result permits Success, Failure {}
record Success(String data) implements Result {}
record Failure(String error) implements Result {}

String describe(Result r) {
    return switch (r) {
        case Success s -> "OK: " + s.data();
        case Failure f -> "Error: " + f.error();
        // No default needed — compiler knows these are the only options
    };
}
```

**Key benefit:** If you later add a new permitted subtype (e.g., `Pending`), the compiler will flag **every** `switch` that doesn't handle it. This turns a runtime bug into a compile-time error.

---

### Q77. What are algebraic data types and how does Java model them with sealed + records? ⭐⭐⭐ Advanced

**Answer:**

Algebraic data types (ADTs) are a way to model data that can be **one of several shapes**, each with different fields. They come from functional programming (Haskell, Scala, Rust).

Java models them with **sealed interfaces/classes** (the "one of" part) + **records** (the data per shape):

```java
// An expression can be a Number, Add, or Multiply — nothing else
sealed interface Expr permits Num, Add, Mul {}
record Num(double value) implements Expr {}
record Add(Expr left, Expr right) implements Expr {}
record Mul(Expr left, Expr right) implements Expr {}

// Exhaustive evaluation
double eval(Expr expr) {
    return switch (expr) {
        case Num n   -> n.value();
        case Add a   -> eval(a.left()) + eval(a.right());
        case Mul m   -> eval(m.left()) * eval(m.right());
    };
}

// Build: (2 + 3) * 4
Expr expr = new Mul(new Add(new Num(2), new Num(3)), new Num(4));
eval(expr);  // 20.0
```

This gives Java the power of pattern matching on data shapes — previously only available in ML-family languages.

---

### Q78. Can a record be a permitted subclass of a sealed class? ⭐⭐ Intermediate

**Answer:**

**Yes.** Records are implicitly `final`, which is one of the three valid modifiers for sealed subclasses (`final`, `sealed`, or `non-sealed`):

```java
sealed interface Shape permits Circle, Rectangle {}

record Circle(double radius) implements Shape {}
record Rectangle(double width, double height) implements Shape {}

// Records are ideal for sealed hierarchies because they're:
// 1. Implicitly final ✅ (required by sealed)
// 2. Immutable ✅ (safe data carriers)
// 3. Auto-generate equals/hashCode/toString ✅ (less boilerplate)
```

---

## 9. Object Class Methods: equals, hashCode, toString

### Q79. What is the default behavior of `equals()` in `Object`? ⭐ Basic

**Answer:**

The default `equals()` in `Object` is **reference equality** — it returns `true` only if both references point to the exact same object in memory (identical to `==`):

```java
// Object.equals() source code:
public boolean equals(Object obj) {
    return (this == obj);
}
```

```java
class Product { String name; }
Product a = new Product(); a.name = "Widget";
Product b = new Product(); b.name = "Widget";

a.equals(b);  // false — different objects in memory (default Object.equals)
a == b;        // false — same result as default equals
a.equals(a);   // true  — same object
```

You override `equals()` when you need **logical equality** (same meaningful content) instead of identity equality.

---

### Q80. What are the five rules of the `equals()` contract? ⭐⭐ Intermediate

**Answer:**

| Rule | Meaning | Example |
|------|---------|---------|
| **Reflexive** | `x.equals(x)` must return `true` | An object always equals itself |
| **Symmetric** | If `x.equals(y)`, then `y.equals(x)` | A equals B ⟺ B equals A |
| **Transitive** | If `x.equals(y)` and `y.equals(z)`, then `x.equals(z)` | A=B, B=C → A=C |
| **Consistent** | Multiple calls return the same result (if objects don't change) | No randomness |
| **Non-null** | `x.equals(null)` must return `false` | Nothing equals null |

**Symmetry violation example:**

```java
class CaseInsensitiveString {
    String value;
    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseInsensitiveString cis)
            return value.equalsIgnoreCase(cis.value);
        if (o instanceof String s)
            return value.equalsIgnoreCase(s);  // CIS.equals(String) → true
        return false;
    }
}

CaseInsensitiveString cis = new CaseInsensitiveString("Hello");
String s = "hello";
cis.equals(s);  // true  ← CIS knows about String
s.equals(cis);  // false ← String doesn't know about CIS → VIOLATION
```

---

### Q81. Why must `hashCode()` be overridden when `equals()` is overridden? ⭐⭐ Intermediate

**Answer:**

The contract states: **if `a.equals(b)` is `true`, then `a.hashCode() == b.hashCode()` must be `true`.**

Hash-based collections (`HashMap`, `HashSet`, `LinkedHashSet`) use a two-step lookup:

```
Step 1: hashCode() → which bucket?
Step 2: equals()   → which entry in that bucket?
```

If you override `equals()` without `hashCode()`:

```java
class Product {
    String sku;
    @Override public boolean equals(Object o) {
        return o instanceof Product p && sku.equals(p.sku);
    }
    // hashCode() NOT overridden — uses Object's default (memory address)
}

Product a = new Product("SKU-001");
Product b = new Product("SKU-001");

a.equals(b);  // true — same SKU

Set<Product> set = new HashSet<>();
set.add(a);
set.contains(b);  // false! Different hashCode → different bucket → never checks equals
```

---

### Q82. What is a good recipe for implementing `equals()` and `hashCode()`? ⭐⭐ Intermediate

**Answer:**

```java
public class Book {
    private final String isbn;
    private final String title;
    private final int edition;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                      // 1. Same reference?
        if (!(o instanceof Book other)) return false;    // 2. Correct type? (handles null)
        return edition == other.edition                   // 3. Compare fields
            && Objects.equals(isbn, other.isbn)
            && Objects.equals(title, other.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn, title, edition);       // Must use same fields as equals
    }
}
```

**Rules:**
- **Same fields** in both `equals()` and `hashCode()`.
- Primitives: compare with `==` in equals, use `Type.hashCode(value)` in hashCode.
- Objects: use `Objects.equals()` (null-safe) in equals, include in `Objects.hash()`.
- Floats/doubles: use `Float.compare()` / `Double.compare()` to handle `NaN` and `-0.0`.
- Arrays: use `Arrays.equals()` in equals, `Arrays.hashCode()` in hashCode.

---

### Q83. What happens if two unequal objects have the same `hashCode()`? ⭐⭐ Intermediate

**Answer:**

This is called a **hash collision**. It's perfectly legal and expected. The `hashCode()` contract only says:
- Equal objects **must** have equal hash codes ✅
- Unequal objects **should** (but don't have to) have different hash codes

When a collision occurs in a `HashMap`:
1. Both keys land in the **same bucket**.
2. The bucket stores them in a linked list (or a balanced tree if the list gets long — Java 8+, threshold of 8 entries).
3. When searching, `hashCode()` finds the bucket, then `equals()` is called on each entry in that bucket to find the exact match.

**Performance impact:** If ALL objects have the same hash code (worst case), `HashMap` degrades to O(n) lookup instead of O(1). With treeification (Java 8+), worst case is O(log n).

---

### Q84. Should `equals()` use `instanceof` or `getClass()`? ⭐⭐⭐ Advanced

**Answer:**

Two valid approaches:

**`instanceof` (Effective Java recommendation):**
```java
if (!(o instanceof Point other)) return false;
```
- Allows a subclass instance to be equal to a parent instance
- Required for Liskov Substitution (a `ColoredPoint` can equal a `Point` if the `Point` fields match)
- Works well with `final` classes (no subclasses exist, so no concern)

**`getClass()` (strict type matching):**
```java
if (o == null || getClass() != o.getClass()) return false;
```
- Only the exact same class can be equal — no cross-type equality
- Safer when subclasses add fields that affect equality
- Violates Liskov (a `ColoredPoint` can never equal a `Point`)

**Recommendation:**
- Use `instanceof` for `final` classes (most common) — there are no subclasses to worry about.
- Use `getClass()` when you have a non-final class hierarchy where subclasses introduce new fields into equality.
- Records use `instanceof` by default.

---

### Q85. Why should you always override `toString()`? ⭐ Basic

**Answer:**

The default `Object.toString()` returns `ClassName@hexHashCode` (e.g., `Employee@4a574795`), which is useless for debugging:

```java
Employee e = new Employee("Alice", "E001");
System.out.println(e);  // Employee@4a574795  — What is this?
```

Overriding `toString()` provides a human-readable representation:

```java
@Override
public String toString() {
    return "Employee{name='%s', id='%s'}".formatted(name, id);
}

System.out.println(e);  // Employee{name='Alice', id='E001'}  — Useful!
```

**Benefits:**
- Logging: `log.info("Processing {}", employee)` becomes meaningful
- Debugging: IDE debug views, assertion messages, stack traces
- Collections: `System.out.println(list)` calls `toString()` on each element

**Guidelines:**
- Include all significant fields
- Exclude sensitive data (passwords, SSNs)
- Keep it concise — it's for debugging, not serialization

---

### Q86. What does `Objects.equals()` do differently from `==`? ⭐ Basic

**Answer:**

`Objects.equals(a, b)` is a **null-safe** value comparison:

```java
// Without Objects.equals() — must handle null manually
if (a == null ? b == null : a.equals(b)) { ... }

// With Objects.equals() — handles null automatically
if (Objects.equals(a, b)) { ... }
```

Internally:

```java
public static boolean equals(Object a, Object b) {
    return (a == b) || (a != null && a.equals(b));
}
```

| Expression | `a == null, b == null` | `a == null, b != null` | `a != null, b == null` | `a != null, b != null` |
|-----------|----------------------|----------------------|----------------------|----------------------|
| `a.equals(b)` | NPE! | NPE! | `false` | Depends on `.equals()` |
| `Objects.equals(a, b)` | `true` | `false` | `false` | Depends on `.equals()` |

---

### Q87. What does `Objects.hash()` do? Why not write `hashCode()` manually? ⭐⭐ Intermediate

**Answer:**

`Objects.hash(field1, field2, ...)` creates a hash code from multiple values using a standard algorithm. It's a convenience over manual computation:

```java
// Manual (slightly more performant, more error-prone)
@Override
public int hashCode() {
    int result = 17;
    result = 31 * result + name.hashCode();
    result = 31 * result + Integer.hashCode(age);
    result = 31 * result + (email != null ? email.hashCode() : 0);
    return result;
}

// Objects.hash() (simpler, good enough for 99% of cases)
@Override
public int hashCode() {
    return Objects.hash(name, age, email);
}
```

**Trade-off:** `Objects.hash()` creates a temporary `Object[]` for the varargs, adding a tiny GC cost. For performance-critical code (millions of hash lookups per second), the manual version avoids this. For normal code, use `Objects.hash()` — clarity wins.

---

### Q88. Can two objects with different `hashCode()` values be `.equals()`? ⭐⭐ Intermediate

**Answer:**

**No!** The contract explicitly states:

> If `a.equals(b)` returns `true`, then `a.hashCode()` must equal `b.hashCode()`.

If two objects are equal but have different hash codes, hash-based collections will malfunction:

```java
// BROKEN: equals says true, hashCode says different
class Broken {
    int id;
    @Override public boolean equals(Object o) {
        return o instanceof Broken b && id == b.id;
    }
    // hashCode NOT overridden — uses Object default (memory address)
}

Broken a = new Broken(); a.id = 1;
Broken b = new Broken(); b.id = 1;
a.equals(b);  // true

Set<Broken> set = new HashSet<>();
set.add(a);
set.contains(b);  // false! → broken behavior
```

**However:** Two objects with the **same** hash code do NOT have to be equal — that's just a collision, which is legal and handled by the collection.

---

### Q89. How do records implement `equals()`, `hashCode()`, and `toString()`? ⭐⭐ Intermediate

**Answer:**

Records auto-generate these methods based on **all components**:

```java
record Point(int x, int y) {}

// equals: compares ALL components
new Point(1, 2).equals(new Point(1, 2));  // true
new Point(1, 2).equals(new Point(2, 1));  // false

// hashCode: based on ALL components
new Point(1, 2).hashCode() == new Point(1, 2).hashCode();  // true

// toString: includes class name and all component values
new Point(1, 2).toString();  // "Point[x=1, y=2]"
```

You **can** override any of these methods in a record if you need custom behavior (e.g., excluding a field from equality, custom toString format), but you rarely need to.

---

### Q90. What is `System.identityHashCode()` and how does it differ from `hashCode()`? ⭐⭐⭐ Advanced

**Answer:**

`System.identityHashCode(obj)` returns the **default hash code** as if `hashCode()` was never overridden — based on the object's identity (memory address).

```java
String a = new String("hello");
String b = new String("hello");

a.hashCode();                         // 99162322 — based on content
b.hashCode();                         // 99162322 — same content, same hash
System.identityHashCode(a);           // e.g., 312714112 — based on identity
System.identityHashCode(b);           // e.g., 692404036 — different object

a.hashCode() == b.hashCode();         // true  — same value
System.identityHashCode(a) == System.identityHashCode(b);  // false — different objects
```

**Use case:** `IdentityHashMap` uses `System.identityHashCode()` and `==` instead of `hashCode()` and `equals()`. Useful when you need to track specific object instances (e.g., serialization graphs, reference counting).

---

## 10. Enums: Advanced Usage

### Q91. What is an enum in Java? Is it a class? ⭐ Basic

**Answer:**

An enum is a **special class** that represents a fixed set of constants. Each constant is a `public static final` instance of the enum type, created once at class loading time.

```java
public enum Season { SPRING, SUMMER, FALL, WINTER }
```

**Yes, enums are classes.** They implicitly extend `java.lang.Enum<E>` and can have:
- Fields and constructors (constructor is always `private`)
- Instance and static methods
- Implement interfaces
- Override methods

```java
Season.SPRING instanceof Enum;   // true
Season.SPRING instanceof Object; // true
Season.class.getSuperclass();    // class java.lang.Enum
```

---

### Q92. Can you create enum instances with `new`? Why not? ⭐ Basic

**Answer:**

**No.** Enum constructors are **always `private`** (implicitly, even if you don't write the modifier). You cannot call `new Season()` from outside the enum declaration.

```java
public enum Color {
    RED, GREEN, BLUE;

    // Color() { }            // Implicitly private — cannot be called externally
    // Color c = new Color(); // COMPILE ERROR
}
```

This restriction ensures that the enum constants declared inside the enum body are the **only** instances that ever exist. It's what makes enums safe for:
- `==` comparison (you can safely use `==` instead of `.equals()`)
- Singleton pattern
- Switch exhaustiveness checking

---

### Q93. How do you add fields, constructors, and methods to an enum? ⭐⭐ Intermediate

**Answer:**

```java
public enum Planet {
    MERCURY(3.303e+23, 2.4397e6),
    VENUS  (4.869e+24, 6.0518e6),
    EARTH  (5.976e+24, 6.37814e6);

    private final double mass;    // Field
    private final double radius;  // Field

    Planet(double mass, double radius) {  // Constructor (implicitly private)
        this.mass = mass;
        this.radius = radius;
    }

    public double surfaceGravity() {  // Method
        return 6.67300E-11 * mass / (radius * radius);
    }
}

Planet.EARTH.surfaceGravity();  // ~9.8
```

Constants are listed first (with constructor arguments), then fields, constructors, and methods.

---

### Q94. What are abstract methods in enums? Why are they useful? ⭐⭐⭐ Advanced

**Answer:**

Enums can declare abstract methods, forcing **each constant** to provide its own implementation. This is the **strategy pattern per constant**:

```java
public enum Operation {
    ADD {
        @Override public double apply(double a, double b) { return a + b; }
    },
    SUBTRACT {
        @Override public double apply(double a, double b) { return a - b; }
    },
    MULTIPLY {
        @Override public double apply(double a, double b) { return a * b; }
    };

    public abstract double apply(double a, double b);
}

Operation.ADD.apply(10, 3);       // 13.0
Operation.MULTIPLY.apply(10, 3);  // 30.0

// Polymorphic dispatch — no switch or if/else needed
for (Operation op : Operation.values()) {
    System.out.printf("10 %s 3 = %.0f%n", op, op.apply(10, 3));
}
```

Each constant is internally an anonymous subclass of the enum, providing its own implementation of the abstract method.

---

### Q95. What is `EnumSet` and why is it better than `HashSet<MyEnum>`? ⭐⭐ Intermediate

**Answer:**

`EnumSet` is a specialized `Set` implementation for enum types, backed by a **bit vector** (a single `long` for enums with ≤64 constants). It's dramatically faster and more memory-efficient than `HashSet<MyEnum>`.

| | `EnumSet` | `HashSet<MyEnum>` |
|-|-----------|-------------------|
| Internal storage | Bit vector (one `long`) | Hash table (buckets, linked lists) |
| Memory | ~8 bytes | Hundreds of bytes |
| `contains()` | Bit check — O(1), extremely fast | Hash + equals — O(1) but much slower |
| Iteration | Sequential bit scan | Hash table traversal (sparse, cache-unfriendly) |

```java
EnumSet<Day> weekdays = EnumSet.range(Day.MONDAY, Day.FRIDAY);
EnumSet<Day> weekend  = EnumSet.of(Day.SATURDAY, Day.SUNDAY);
EnumSet<Day> allDays  = EnumSet.allOf(Day.class);

weekdays.contains(Day.MONDAY);   // Single bitmask operation
```

**Rule:** Always use `EnumSet` instead of `HashSet` for enum values, and `EnumMap` instead of `HashMap` for enum keys.

---

### Q96. Why is enum the best way to implement a singleton in Java? ⭐⭐⭐ Advanced

**Answer:**

*Effective Java* Item 3: "A single-element enum is the best way to implement a singleton."

```java
public enum Database {
    INSTANCE;

    private final Connection connection;

    Database() {
        connection = createConnection();
    }

    public Connection getConnection() { return connection; }
}

// Usage
Database.INSTANCE.getConnection();
```

**Why it's superior to traditional singletons:**

| Threat | Traditional singleton | Enum singleton |
|--------|----------------------|----------------|
| **Thread safety** | Requires `synchronized` or double-checked locking | JVM guarantees single initialization |
| **Reflection attack** | Can create extra instances via `Constructor.newInstance()` | JVM prohibits enum instantiation via reflection |
| **Serialization** | Must implement `readResolve()` to prevent duplicate instances | Serialization/deserialization always returns the same instance |
| **Simplicity** | 15+ lines with `private` constructor, `volatile`, `getInstance()` | 3 lines |

---

### Q97. Can an enum implement an interface? ⭐ Basic

**Answer:**

**Yes.** Enums can implement any number of interfaces:

```java
interface Printable {
    String label();
}

enum Priority implements Printable {
    LOW("Low Priority"),
    MEDIUM("Medium Priority"),
    HIGH("High Priority");

    private final String label;

    Priority(String label) { this.label = label; }

    @Override
    public String label() { return label; }
}

// Polymorphic usage
Printable p = Priority.HIGH;
p.label();  // "High Priority"
```

Enums **cannot** extend a class (they implicitly extend `java.lang.Enum`), but implementing multiple interfaces is fully supported.

---

## 11. Cross-Topic / Combined Questions

### Q98. What is the output? (Inheritance + Polymorphism + Object methods) ⭐⭐⭐ Advanced

```java
class Animal {
    String name;
    Animal(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Animal a && name.equals(a.name);
    }
}

class Dog extends Animal {
    String breed;
    Dog(String name, String breed) {
        super(name);
        this.breed = breed;
    }
}

Animal a = new Animal("Rex");
Dog d = new Dog("Rex", "Labrador");

System.out.println(a.equals(d));  // ?
System.out.println(d.equals(a));  // ?
System.out.println(d instanceof Animal);  // ?
```

**Answer:**

```
true    — d instanceof Animal is true, names match
true    — Dog inherits Animal.equals(), a instanceof Animal is true, names match
true    — Dog is-a Animal
```

Both comparisons are `true` because `Dog` inherits `Animal`'s `equals()`, which uses `instanceof Animal`. A `Dog` is an `Animal`, so it passes the type check.

**Subtle issue:** This `equals()` ignores the `breed` field in `Dog`. Two dogs with the same name but different breeds are considered equal. If `Dog` needs breed-aware equality, it should override `equals()` — but then you must be careful about symmetry.

---

### Q99. What is the output? (Constructor chaining + Initialization order) ⭐⭐⭐ Advanced

```java
class Base {
    int x = 10;
    { System.out.println("Base init block: x=" + x); }

    Base() {
        System.out.println("Base constructor: x=" + x);
        x = 20;
    }
}

class Derived extends Base {
    int y = x + 5;
    { System.out.println("Derived init block: y=" + y); }

    Derived() {
        System.out.println("Derived constructor: x=" + x + ", y=" + y);
    }
}

new Derived();
```

**Answer:**

```
Base init block: x=10
Base constructor: x=10
Derived init block: y=25
Derived constructor: x=20, y=25
```

Execution order:
1. `Base` field `x = 10` is set
2. `Base` instance block runs → prints `x=10`
3. `Base` constructor runs → prints `x=10`, then sets `x=20`
4. `Derived` field `y = x + 5` → `x` is now 20, so `y = 25`
5. `Derived` instance block runs → prints `y=25`
6. `Derived` constructor runs → prints `x=20, y=25`

---

### Q100. Design Question: You need a payment system supporting Credit Card, PayPal, and Bank Transfer, with easy extensibility for future methods. How do you design it? ⭐⭐ Intermediate

**Answer:**

Apply OCP (Strategy pattern) + DIP (depend on abstractions) + SRP (separate concerns):

```java
// Contract (interface — DIP)
interface PaymentMethod {
    boolean validate();
    PaymentResult process(BigDecimal amount);
    String getType();
}

// Implementations (OCP — add new ones without modifying existing code)
class CreditCardPayment implements PaymentMethod { ... }
class PayPalPayment implements PaymentMethod { ... }
class BankTransferPayment implements PaymentMethod { ... }

// Processor depends on abstraction (DIP + SRP — orchestration only)
class PaymentProcessor {
    PaymentResult process(PaymentMethod method, BigDecimal amount) {
        if (!method.validate()) return PaymentResult.failure("Invalid");
        return method.process(amount);
    }
}

// Result as immutable record
record PaymentResult(boolean success, String transactionId, String message) {
    static PaymentResult failure(String msg) { return new PaymentResult(false, null, msg); }
}
```

**Adding CryptoPayment:** Create a new class `CryptoPayment implements PaymentMethod`. Zero changes to `PaymentProcessor` or existing payment classes. OCP is proven.

---

### Q101. What is wrong with this code? (equals/hashCode + Collections) ⭐⭐⭐ Advanced

```java
class Employee {
    String name;
    int id;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Employee e)) return false;
        return id == e.id && Objects.equals(name, e.name);
    }

    // hashCode() NOT overridden
}

Set<Employee> team = new HashSet<>();
Employee alice = new Employee();
alice.name = "Alice"; alice.id = 1;
team.add(alice);

Employee sameAlice = new Employee();
sameAlice.name = "Alice"; sameAlice.id = 1;
System.out.println(team.contains(sameAlice));
```

**Answer:**

Prints `false` — even though `alice.equals(sameAlice)` returns `true`.

**The bug:** `hashCode()` is not overridden. `HashSet` first computes `sameAlice.hashCode()` (which uses the default `Object` implementation — memory address), gets a **different bucket** from `alice.hashCode()`, and never even calls `equals()`.

**Fix:**
```java
@Override
public int hashCode() {
    return Objects.hash(name, id);
}
```

This is the most common `equals()`/`hashCode()` bug in production Java code.

---

### Q102. What is the output? (Sealed classes + Pattern matching + Records) ⭐⭐⭐ Advanced

```java
sealed interface Shape permits Circle, Rect {}
record Circle(double r) implements Shape {}
record Rect(double w, double h) implements Shape {}

String describe(Shape s) {
    return switch (s) {
        case Circle c when c.r() > 10 -> "Big circle (r=" + c.r() + ")";
        case Circle c                  -> "Small circle (r=" + c.r() + ")";
        case Rect r when r.w() == r.h() -> "Square (" + r.w() + ")";
        case Rect r                     -> "Rectangle (" + r.w() + "x" + r.h() + ")";
    };
}

System.out.println(describe(new Circle(5)));
System.out.println(describe(new Circle(15)));
System.out.println(describe(new Rect(4, 4)));
System.out.println(describe(new Rect(3, 7)));
```

**Answer:**

```
Small circle (r=5.0)
Big circle (r=15.0)
Square (4.0)
Rectangle (3.0x7.0)
```

This demonstrates:
- **Sealed types** enabling exhaustive switch (no `default` needed)
- **Record deconstruction** (accessing components via `c.r()`, `r.w()`, etc.)
- **Guarded patterns** (`when` clause for conditional matching)
- Pattern order matters: more specific guarded cases must come before general ones

---

### Q103. Refactor this code to fix the SOLID violations. ⭐⭐⭐ Advanced

```java
class NotificationService {
    void notify(String type, String recipient, String message) {
        if ("email".equals(type)) {
            // 30 lines of SMTP code
        } else if ("sms".equals(type)) {
            // 20 lines of SMS API code
        } else if ("push".equals(type)) {
            // 25 lines of push notification code
        }
        // Log the notification
        System.out.println("Sent " + type + " to " + recipient);
    }
}
```

**Answer:**

**Violations:** SRP (one class does three different protocols + logging), OCP (new channel requires modifying this class), DIP (hardcoded implementation details).

**Refactored:**

```java
// Abstraction (DIP + OCP)
interface NotificationChannel {
    void send(String recipient, String message);
    String getType();
}

// Concrete implementations (SRP — each handles one protocol)
class EmailChannel implements NotificationChannel {
    @Override public void send(String recipient, String message) { /* SMTP */ }
    @Override public String getType() { return "email"; }
}

class SmsChannel implements NotificationChannel {
    @Override public void send(String recipient, String message) { /* SMS API */ }
    @Override public String getType() { return "sms"; }
}

// Orchestrator (SRP — routing only, DIP — depends on abstraction)
class NotificationService {
    private final Map<String, NotificationChannel> channels;

    NotificationService(List<NotificationChannel> channelList) {
        channels = channelList.stream()
            .collect(Collectors.toMap(NotificationChannel::getType, c -> c));
    }

    void notify(String type, String recipient, String message) {
        NotificationChannel channel = channels.get(type);
        if (channel == null) throw new IllegalArgumentException("Unknown: " + type);
        channel.send(recipient, message);
    }
}
```

Adding Slack notifications: create `SlackChannel implements NotificationChannel`, register it — zero modifications to existing code.

---

### Q104. What is the output? (Enum + Polymorphism + Override) ⭐⭐⭐ Advanced

```java
enum Level {
    LOW {
        @Override public String format(String msg) { return "[LOW] " + msg; }
    },
    HIGH {
        @Override public String format(String msg) { return "⚠ [HIGH] " + msg.toUpperCase(); }
    };

    public abstract String format(String msg);

    @Override
    public String toString() { return name().toLowerCase(); }
}

System.out.println(Level.LOW);
System.out.println(Level.HIGH.format("disk full"));
System.out.println(Level.LOW instanceof Enum);
System.out.println(Level.values().length);
```

**Answer:**

```
low
⚠ [HIGH] DISK FULL
true
2
```

- `Level.LOW` → calls overridden `toString()` → `"low"`
- `Level.HIGH.format(...)` → calls HIGH's implementation → uppercase + prefix
- All enum constants are instances of `Enum`
- `values()` returns all constants — 2 in this case

---

### Q105. Architecture Question: You're designing a library catalog system. Items can be Books, DVDs, or Magazines. Books and DVDs are borrowable; Magazines are reference-only. How do you model this? ⭐⭐⭐ Advanced

**Answer:**

Use sealed classes for the closed hierarchy, interfaces for capabilities, and records for data:

```java
// Closed type hierarchy (sealed)
abstract sealed class LibraryItem permits Book, DVD, Magazine {
    private final String title;
    private final String id;
    protected LibraryItem(String title, String id) {
        this.title = title;
        this.id = id;
    }
    abstract String getCategory();
    // equals/hashCode by id, toString included
}

// Capability interfaces (ISP — not all items are borrowable)
interface Borrowable {
    void borrow(String patronId);
    void returnItem();
    boolean isAvailable();
}

interface Searchable {
    default boolean matchesKeyword(String keyword) {
        return getTitle().toLowerCase().contains(keyword.toLowerCase());
    }
    String getTitle();
}

// Book and DVD implement both; Magazine implements only Searchable
final class Book extends LibraryItem implements Borrowable, Searchable { ... }
final class DVD extends LibraryItem implements Borrowable, Searchable { ... }
final class Magazine extends LibraryItem implements Searchable { ... }

// Patron uses composition (has-a list of borrowed items)
class Patron {
    private final List<Borrowable> borrowed = new ArrayList<>();
    void borrowItem(Borrowable item) { item.borrow(id); borrowed.add(item); }
}

// Immutable data for tracking (record)
record BorrowingRecord(String patronId, LocalDate borrowDate, LocalDate dueDate) {}
```

**Design decisions justified:**
- **Sealed class:** Prevents unauthorized item types (LibraryItem is a closed set)
- **ISP:** `Borrowable` is separate from `Searchable` — Magazines shouldn't be forced to implement borrow logic
- **Composition:** `Patron` has-a list of borrowed items, not extends anything
- **Default method:** `Searchable.matchesKeyword()` provides reusable search logic
- **Record:** `BorrowingRecord` is an immutable data carrier — ideal for records

---

## Difficulty Distribution

| Level | Count | Suitable for |
|-------|-------|-------------|
| ⭐ Basic | ~30 | Fresher / Junior interviews |
| ⭐⭐ Intermediate | ~45 | Mid-level / Senior interviews |
| ⭐⭐⭐ Advanced | ~30 | Senior / Architect interviews |

## Study Strategy

1. **First pass:** Answer all ⭐ Basic questions without looking at the answers.
2. **Second pass:** Tackle ⭐⭐ Intermediate questions. For any you get wrong, re-read the corresponding section in the [Study Guide](STUDY-GUIDE.md).
3. **Third pass:** Work through ⭐⭐⭐ Advanced questions. These are the ones that differentiate senior developers. Practice explaining them out loud.
4. **Final pass:** The cross-topic questions (Q98–Q105) simulate real interview conditions where you need to combine knowledge from multiple areas.
