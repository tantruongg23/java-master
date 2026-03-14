# Phase 02 — OOP Deep Dive: Complete Study Guide

> This guide covers every topic from the Phase 02 curriculum in depth.
> Read it section by section, run the examples, and experiment.
> OOP is not something you memorize — it's something you internalize through practice.

---

## Table of Contents

1. [Classes, Objects, Constructors, `this` Keyword](#1-classes-objects-constructors-this-keyword)
2. [Encapsulation: Access Modifiers, Getters/Setters, Immutable Objects](#2-encapsulation-access-modifiers-getterssetters-immutable-objects)
3. [Inheritance: extends, super, Constructor Chaining, Method Overriding](#3-inheritance-extends-super-constructor-chaining-method-overriding)
4. [Polymorphism: Compile-Time vs. Runtime](#4-polymorphism-compile-time-vs-runtime)
5. [Abstract Classes vs. Interfaces](#5-abstract-classes-vs-interfaces)
6. [SOLID Principles with Java Examples](#6-solid-principles-with-java-examples)
7. [Composition vs. Inheritance](#7-composition-vs-inheritance)
8. [Records and Sealed Classes](#8-records-and-sealed-classes)
9. [Object Class Methods: equals, hashCode, toString](#9-object-class-methods-equals-hashcode-tostring)
10. [Enums: Advanced Usage](#10-enums-advanced-usage)

---

## 1. Classes, Objects, Constructors, `this` Keyword

### The Big Picture

A class is a **blueprint** — it defines what data an object holds (fields) and what it can do (methods). An object is a **concrete instance** of that blueprint, living on the heap with its own copy of the fields.

```
┌──────────────────────────────────┐
│         Class: BankAccount       │  ← Blueprint (exists once in Method Area)
│──────────────────────────────────│
│  Fields (state):                 │
│    - String owner                │
│    - double balance              │
│──────────────────────────────────│
│  Methods (behavior):             │
│    + deposit(double amount)      │
│    + withdraw(double amount)     │
│    + getBalance(): double        │
│──────────────────────────────────│
│  Constructors (initialization):  │
│    + BankAccount(String, double) │
│    + BankAccount(String)         │
└──────────────────────────────────┘
         │                  │
         ▼                  ▼
   ┌──────────┐      ┌──────────┐
   │ Object 1 │      │ Object 2 │   ← Instances (live on the Heap)
   │ "Alice"  │      │ "Bob"    │
   │ 5000.00  │      │ 1200.50  │
   └──────────┘      └──────────┘
```

### Fields: State of an Object

Fields are variables declared at the class level. They define what data each object carries.

```java
public class BankAccount {
    // Instance fields — each object gets its own copy
    private String owner;
    private double balance;

    // Static field — shared across ALL instances (one copy in Method Area)
    private static int totalAccounts = 0;

    // Constants — static + final, UPPER_SNAKE_CASE by convention
    public static final double MINIMUM_BALANCE = 100.0;
}
```

**Instance vs. static:**

| Aspect | Instance field | Static field |
|--------|---------------|-------------|
| Storage | One copy per object (heap) | One copy per class (Method Area) |
| Access | Through an object reference | Through the class name (preferred) or object |
| Lifetime | Created with the object, GC'd with it | Created when class is loaded, lives until class is unloaded |
| Use case | Data that varies per object | Shared counters, constants, utility data |

```java
BankAccount a1 = new BankAccount("Alice");
BankAccount a2 = new BankAccount("Bob");

// Instance: each object has its own balance
a1.getBalance();  // Alice's balance
a2.getBalance();  // Bob's balance — different from Alice's

// Static: shared across all instances
BankAccount.totalAccounts;   // Preferred: access via class name
a1.totalAccounts;            // Works but misleading — looks instance-specific
```

### Constructors: Initializing Objects

A constructor is a special method that runs when you create an object with `new`. It has no return type (not even `void`) and its name must match the class name exactly.

```java
public class BankAccount {

    private String owner;
    private double balance;

    // Primary constructor
    public BankAccount(String owner, double balance) {
        this.owner = owner;
        this.balance = balance;
    }

    // Overloaded constructor — delegates to the primary one
    public BankAccount(String owner) {
        this(owner, 0.0);  // Constructor delegation with this(...)
    }

    // Default constructor — only exists if you write NO constructors
    // public BankAccount() { }  // Compiler adds this ONLY if no other constructor exists
}
```

**Key rules:**

1. If you write **zero constructors**, the compiler adds a default no-arg constructor.
2. If you write **any constructor**, the compiler does NOT add a default.
3. `this(...)` calls another constructor in the same class. It must be the **first statement**.

### Constructor Overloading and `this(...)` Delegation

Constructor overloading lets you provide multiple ways to create an object. Always delegate to a single "primary" constructor to avoid code duplication.

```java
public class Employee {

    private final String name;
    private final String id;
    private final String department;
    private final double salary;

    // Primary constructor — all fields
    public Employee(String name, String id, String department, double salary) {
        this.name = name;
        this.id = id;
        this.department = department;
        this.salary = salary;
    }

    // Delegate: default department
    public Employee(String name, String id, double salary) {
        this(name, id, "Unassigned", salary);
    }

    // Delegate: default department and salary
    public Employee(String name, String id) {
        this(name, id, "Unassigned", 0.0);
    }
}
```

**Anti-pattern:** Duplicating initialization logic in each constructor. If validation changes, you'd need to update every constructor.

### The `this` Keyword

`this` is a reference to the **current object** — the object on which the method or constructor was invoked.

```java
public class Person {

    private String name;
    private int age;

    public Person(String name, int age) {
        // 1. Disambiguating fields from parameters
        this.name = name;   // this.name = field, name = parameter
        this.age = age;
    }

    // 2. Passing the current instance to another method
    public void register(Registry registry) {
        registry.add(this);  // Pass "myself" to the registry
    }

    // 3. Returning the current instance (fluent/builder pattern)
    public Person withName(String name) {
        this.name = name;
        return this;         // Enables method chaining
    }

    public Person withAge(int age) {
        this.age = age;
        return this;
    }
}

// Fluent usage:
Person p = new Person("Alice", 25)
    .withName("Alice Smith")
    .withAge(26);
```

### Object Lifecycle

Every Java object goes through three stages:

```
1. Allocation     →   new Person("Alice", 25)
   JVM allocates memory on the heap.

2. Initialization →   Constructor runs.
   Fields are set to default values first, then the constructor body executes.

3. GC Eligibility →   No more references point to the object.
   The garbage collector MAY reclaim the memory (timing is not guaranteed).
```

```java
Person p = new Person("Alice", 25);   // (1) Allocated, (2) Initialized
p = null;                              // (3) Original object is GC-eligible
// Or:
p = new Person("Bob", 30);            // (3) The "Alice" object is GC-eligible
```

**There is no destructor in Java.** The `finalize()` method is deprecated (Java 9+) and should never be used. Use `try-with-resources` and `Closeable`/`AutoCloseable` for cleanup.

### Initialization Blocks

Java provides two types of initialization blocks that run before constructors:

```java
public class Demo {

    // Static initialization block — runs ONCE when the class is loaded
    static {
        System.out.println("1. Static block runs first");
    }

    // Instance initialization block — runs for EVERY new object, before the constructor body
    {
        System.out.println("2. Instance block runs second");
    }

    public Demo() {
        System.out.println("3. Constructor runs last");
    }
}

new Demo();
// Output:
// 1. Static block runs first
// 2. Instance block runs second
// 3. Constructor runs last

new Demo();
// Output:
// 2. Instance block runs second   (static block does NOT run again)
// 3. Constructor runs last
```

**Execution order for a single `new` call:**

```
1. Static fields and static blocks (only on first use of the class, top to bottom)
2. Instance fields and instance blocks (top to bottom)
3. Constructor body
```

### Static vs. Instance Members

```java
public class MathHelper {

    // Static method — belongs to the class, not to any object
    public static int add(int a, int b) {
        return a + b;
    }

    // Instance method — requires an object
    public int multiply(int a, int b) {
        return a * b;
    }
}

// Static: call on the class
MathHelper.add(3, 4);           // 7

// Instance: call on an object
MathHelper helper = new MathHelper();
helper.multiply(3, 4);           // 12

// Static methods CANNOT access instance members:
// public static void bad() {
//     System.out.println(this.name);  // COMPILE ERROR: no 'this' in static context
// }
```

**When to use static:**
- Utility methods that don't need object state (`Math.abs()`, `Collections.sort()`)
- Factory methods (`List.of()`, `Integer.valueOf()`)
- Constants (`Math.PI`, `Integer.MAX_VALUE`)
- Counters or caches shared across all instances

---

## 2. Encapsulation: Access Modifiers, Getters/Setters, Immutable Objects

### What Is Encapsulation?

Encapsulation is **hiding the internal state** of an object and exposing a controlled interface. The outside world interacts with an object through its public methods, never touching its fields directly.

**Why it matters:**
- You can change internal representation without breaking callers.
- You can add validation when values are set.
- You can make objects thread-safe.
- You can enforce invariants (rules that must always be true).

### The Four Access Levels

Java has four access modifiers, from most restrictive to most open:

| Modifier | Same Class | Same Package | Subclass (other pkg) | World |
|----------|:---------:|:------------:|:-------------------:|:-----:|
| `private` | ✅ | ❌ | ❌ | ❌ |
| (default / package-private) | ✅ | ✅ | ❌ | ❌ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| `public` | ✅ | ✅ | ✅ | ✅ |

```java
package com.example.bank;

public class BankAccount {
    private double balance;           // Only this class can access
    String accountType;               // Package-private: any class in com.example.bank
    protected String owner;           // This class, package, and subclasses
    public String getId() { ... }     // Everyone
}
```

**Rules of thumb:**
1. Fields: always `private` (or `private final`).
2. Methods: `public` if part of the API, `private` for internal helpers.
3. `protected`: use sparingly — it's the least intuitive level. Typically for methods that subclasses need to override.
4. Package-private (no modifier): good for classes that are internal to a package.

### Getters and Setters

Getters and setters are methods that provide controlled access to private fields.

```java
public class Temperature {

    private double celsius;

    public Temperature(double celsius) {
        setCelsius(celsius);  // Use the setter to get validation
    }

    public double getCelsius() {
        return celsius;
    }

    public void setCelsius(double celsius) {
        if (celsius < -273.15) {
            throw new IllegalArgumentException(
                "Temperature cannot be below absolute zero: " + celsius);
        }
        this.celsius = celsius;
    }

    // Derived getter — no corresponding field
    public double getFahrenheit() {
        return celsius * 9.0 / 5.0 + 32;
    }
}
```

**Don't blindly generate getters and setters for every field.** Only expose what the caller genuinely needs:
- Need to read? Add a getter.
- Need to modify? Add a setter **with validation**.
- Internal implementation detail? Neither getter nor setter.

### Defensive Copying

If a field holds a reference to a **mutable object** (`Date`, `List`, arrays), returning that reference directly breaks encapsulation — the caller can modify your internal state.

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Event {

    private final String name;
    private final Date startDate;       // Date is mutable!
    private final List<String> attendees; // List is mutable!

    public Event(String name, Date startDate, List<String> attendees) {
        this.name = name;
        this.startDate = new Date(startDate.getTime());            // Defensive copy IN
        this.attendees = new ArrayList<>(attendees);                // Defensive copy IN
    }

    public Date getStartDate() {
        return new Date(startDate.getTime());                      // Defensive copy OUT
    }

    public List<String> getAttendees() {
        return Collections.unmodifiableList(attendees);             // Unmodifiable view OUT
    }
}
```

**Without defensive copies:**

```java
Date d = new Date();
Event event = new Event("Party", d, List.of("Alice"));
d.setYear(1900);            // Mutates the Event's internal startDate!
event.getStartDate().setYear(2099);  // Also mutates internal state!
```

**Modern alternative:** Use `java.time` classes (`LocalDate`, `Instant`, `ZonedDateTime`) — they are immutable by design, no defensive copies needed.

### Immutable Objects

An immutable object cannot be modified after creation. This is one of the most powerful patterns in Java.

**Recipe for immutability:**

1. Declare the class `final` (prevent subclasses from adding mutable state).
2. Make all fields `private final`.
3. No setter methods.
4. Defensive copy mutable fields in the constructor and getters.
5. Don't expose references to mutable internal objects.

```java
public final class Money {

    private final String currency;
    private final long amountInCents;

    public Money(String currency, long amountInCents) {
        this.currency = currency;
        this.amountInCents = amountInCents;
    }

    public String getCurrency() {
        return currency;    // String is already immutable — no copy needed
    }

    public long getAmountInCents() {
        return amountInCents;  // Primitives are copied by value — always safe
    }

    // "Modification" returns a new object
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(currency, this.amountInCents + other.amountInCents);
    }
}
```

**Why immutability matters:**

| Benefit | Explanation |
|---------|-------------|
| Thread safety | No synchronization needed — there's nothing to change |
| Cache keys | Safe as `HashMap` keys — `hashCode()` never changes |
| Simpler reasoning | Once created, you know the state forever |
| Defensive copying | No need to copy immutable objects |
| Free sharing | Multiple references can safely point to the same instance |

---

## 3. Inheritance: extends, super, Constructor Chaining, Method Overriding

### Single Inheritance with `extends`

Java supports **single class inheritance** — a class can extend exactly one parent class. Every class implicitly extends `Object` if it doesn't extend anything else.

```java
public class Animal {
    protected String name;

    public Animal(String name) {
        this.name = name;
    }

    public void eat() {
        System.out.println(name + " is eating");
    }

    public String speak() {
        return "...";
    }
}

public class Dog extends Animal {
    private String breed;

    public Dog(String name, String breed) {
        super(name);         // MUST call parent constructor
        this.breed = breed;
    }

    @Override
    public String speak() {
        return "Woof!";
    }

    public void fetch() {
        System.out.println(name + " fetches the ball!");
    }
}
```

**What `Dog` inherits from `Animal`:**
- The `name` field (accessible because it's `protected`)
- The `eat()` method (inherited as-is)
- The `speak()` method (overridden with a new implementation)

**What `Dog` does NOT inherit:**
- Constructors — they are never inherited. `Dog` must define its own.
- `private` members — they exist in the parent object but are invisible to `Dog`.

### The `super` Keyword

`super` refers to the parent class. It has two main uses:

```java
public class Manager extends Employee {

    private List<Employee> reports;

    // 1. Calling the parent constructor — MUST be the first statement
    public Manager(String name, String id, double salary) {
        super(name, id, salary);           // Calls Employee(String, String, double)
        this.reports = new ArrayList<>();
    }

    // 2. Calling a parent method when you've overridden it
    @Override
    public double calculateSalary() {
        double baseSalary = super.calculateSalary();  // Get Employee's calculation
        return baseSalary * 1.15;                      // Add 15% management bonus
    }
}
```

### Constructor Chaining

When you create a subclass object, **constructors run top-down** from `Object` to the most specific class:

```
Object()  →  Animal(String)  →  Dog(String, String)
```

```java
public class Animal {
    protected String name;

    public Animal(String name) {
        super();                     // Implicit call to Object()
        System.out.println("Animal constructor");
        this.name = name;
    }
}

public class Dog extends Animal {
    private String breed;

    public Dog(String name, String breed) {
        super(name);                 // Explicit call to Animal(String)
        System.out.println("Dog constructor");
        this.breed = breed;
    }
}

new Dog("Rex", "Labrador");
// Output:
// Animal constructor
// Dog constructor
```

**Critical rule:** If you don't call `super(...)` explicitly, the compiler inserts `super()` (no-arg). If the parent has no no-arg constructor, you get a **compile error**.

```java
public class Animal {
    public Animal(String name) { ... }  // No no-arg constructor!
}

public class Dog extends Animal {
    public Dog(String name) {
        // Compiler tries to insert super() — COMPILE ERROR!
        // You MUST explicitly call super(name)
    }
}
```

### Method Overriding Rules

| Rule | Details |
|------|---------|
| **Signature** | Must be identical: same name, same parameter types, same order |
| **Return type** | Same type or a **covariant** (more specific) return type |
| **Access** | Cannot be **more restrictive** (can be more permissive) |
| **Exceptions** | Cannot throw **broader** checked exceptions |
| **`@Override`** | Not required but **always use it** — catches typos at compile time |
| **`static`** | Static methods are **hidden**, not overridden |
| **`final`** | `final` methods **cannot** be overridden |
| **`private`** | `private` methods are **not visible** to subclasses — not overriding |

```java
public class Animal {
    protected Animal create() {
        return new Animal("generic");
    }
}

public class Dog extends Animal {
    @Override
    protected Dog create() {        // Covariant return type: Dog is-a Animal ✅
        return new Dog("Rex", "Lab");
    }

    // @Override
    // private Dog create() { }     // COMPILE ERROR: more restrictive access ❌

    // @Override
    // public Dog create() { }      // OK: more permissive access ✅
}
```

### `final` Methods and `final` Classes

```java
// final method — cannot be overridden
public class Payment {
    public final void processPayment() {
        validate();
        execute();
        log();
    }
    // Subclasses can override validate(), execute(), log()
    // but NOT processPayment() — the algorithm is locked
}

// final class — cannot be extended at all
public final class String { ... }         // java.lang.String is final
public final class Integer { ... }        // Wrapper classes are final
// class MyString extends String { }      // COMPILE ERROR
```

### The Fragile Base Class Problem

Deep inheritance hierarchies are dangerous because changing the parent class can break subclasses in unexpected ways.

```java
// Version 1: Parent
public class InstrumentedSet<E> extends HashSet<E> {
    private int addCount = 0;

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);  // BUG: HashSet.addAll() calls add() internally!
    }

    public int getAddCount() { return addCount; }
}

InstrumentedSet<String> s = new InstrumentedSet<>();
s.addAll(List.of("a", "b", "c"));
s.getAddCount();  // Expected 3, actual 6!
// addAll adds 3, then super.addAll() calls add() 3 more times
```

This is why *Effective Java* says: **"Favor composition over inheritance"** (covered in Section 7).

---

## 4. Polymorphism: Compile-Time vs. Runtime

### Compile-Time Polymorphism (Method Overloading)

The compiler decides which method to call based on the **declared types** of the arguments at the call site. This happens at compile time.

```java
public class Calculator {
    int add(int a, int b)         { return a + b; }
    double add(double a, double b) { return a + b; }
    String add(String a, String b) { return a + b; }
}

Calculator calc = new Calculator();
calc.add(1, 2);         // Calls add(int, int) → 3
calc.add(1.5, 2.5);     // Calls add(double, double) → 4.0
calc.add("hi", " there"); // Calls add(String, String) → "hi there"
```

Overloading is resolved at **compile time** based on the **reference type**, not the runtime type:

```java
public class Printer {
    void print(Animal a)  { System.out.println("Animal"); }
    void print(Dog d)     { System.out.println("Dog"); }
}

Animal myDog = new Dog("Rex", "Lab");   // Reference type = Animal
Printer p = new Printer();
p.print(myDog);   // "Animal" — NOT "Dog"!
// The compiler sees the declared type (Animal) and picks print(Animal)
```

### Runtime Polymorphism (Method Overriding)

The JVM decides which overridden method to call based on the **actual type** of the object at runtime. This is called **virtual method dispatch**.

```java
Animal myAnimal = new Dog("Rex", "Lab");
myAnimal.speak();  // "Woof!" — JVM sees the actual object is a Dog
```

```
Compile time:  myAnimal is declared as Animal
               → compiler checks Animal has speak() ✅

Runtime:       myAnimal actually points to a Dog object
               → JVM calls Dog.speak(), not Animal.speak()
```

**This is the heart of OOP.** You write code against abstractions (`Animal`) and the correct behavior runs automatically based on the actual type.

### Upcasting and Downcasting

```java
// UPCASTING: Subtype → Supertype (always safe, implicit)
Dog dog = new Dog("Rex", "Lab");
Animal animal = dog;              // Dog → Animal — always valid
Object obj = dog;                 // Dog → Object — always valid

// After upcasting, you can only call methods declared in the reference type:
animal.eat();       // ✅ defined in Animal
animal.speak();     // ✅ defined in Animal (Dog's override runs)
// animal.fetch();  // ❌ COMPILE ERROR: Animal doesn't have fetch()

// DOWNCASTING: Supertype → Subtype (risky, explicit cast required)
Animal a = new Dog("Rex", "Lab");
Dog d = (Dog) a;      // ✅ Works because a IS actually a Dog
d.fetch();            // ✅ Now we can call Dog-specific methods

Animal a2 = new Animal("Cat");
// Dog d2 = (Dog) a2;  // 💥 ClassCastException at RUNTIME — a2 is not a Dog
```

### Safe Downcasting with `instanceof`

```java
// Traditional instanceof (Java < 16)
if (animal instanceof Dog) {
    Dog d = (Dog) animal;
    d.fetch();
}

// Pattern matching for instanceof (Java 16+) — preferred
if (animal instanceof Dog d) {
    d.fetch();  // 'd' is already cast and scoped
}

// With negation
if (!(animal instanceof Dog d)) {
    System.out.println("Not a dog");
    return;
}
d.fetch();  // 'd' is in scope here because we returned above
```

### Why Polymorphism Matters: Eliminating if/else Chains

**Without polymorphism (fragile, hard to extend):**

```java
// Every new shape requires modifying this method
double calculateArea(Object shape) {
    if (shape instanceof Circle c) {
        return Math.PI * c.radius * c.radius;
    } else if (shape instanceof Rectangle r) {
        return r.width * r.height;
    } else if (shape instanceof Triangle t) {
        return 0.5 * t.base * t.height;
    }
    throw new IllegalArgumentException("Unknown shape");
}
```

**With polymorphism (clean, extensible):**

```java
abstract class Shape {
    abstract double area();
}

class Circle extends Shape {
    double radius;
    @Override double area() { return Math.PI * radius * radius; }
}

class Rectangle extends Shape {
    double width, height;
    @Override double area() { return width * height; }
}

// Adding Triangle requires ZERO changes to existing code
// Just create a new class:
class Triangle extends Shape {
    double base, height;
    @Override double area() { return 0.5 * base * height; }
}

// Usage — works with any Shape, present or future
double totalArea(List<Shape> shapes) {
    return shapes.stream().mapToDouble(Shape::area).sum();
}
```

---

## 5. Abstract Classes vs. Interfaces

### Abstract Classes

An abstract class is a class that **cannot be instantiated** and may contain abstract methods (methods without a body) that subclasses must implement.

```java
public abstract class Vehicle {

    // Can have fields (state)
    protected String make;
    protected int year;

    // Can have constructors (for subclass initialization)
    protected Vehicle(String make, int year) {
        this.make = make;
        this.year = year;
    }

    // Can have concrete methods (shared behavior)
    public String getDescription() {
        return year + " " + make;
    }

    // Abstract methods — subclasses MUST implement
    public abstract double getFuelEfficiency();
    public abstract int getPassengerCapacity();
}

public class Car extends Vehicle {
    private double milesPerGallon;

    public Car(String make, int year, double mpg) {
        super(make, year);
        this.milesPerGallon = mpg;
    }

    @Override
    public double getFuelEfficiency() {
        return milesPerGallon;
    }

    @Override
    public int getPassengerCapacity() {
        return 5;
    }
}

// Vehicle v = new Vehicle("Toyota", 2024);  // COMPILE ERROR: cannot instantiate abstract class
Vehicle v = new Car("Toyota", 2024, 30.5);   // ✅ Polymorphic reference
```

### Interfaces

An interface defines a **contract** — a set of methods that implementing classes must provide. Before Java 8, interfaces could only have abstract methods. Modern interfaces are much more powerful.

```java
// Pure contract
public interface Flyable {
    void fly();           // Implicitly public abstract
    double getAltitude(); // Implicitly public abstract
}

// With default methods (Java 8+) — provide reusable implementations
public interface Loggable {
    default void log(String message) {
        System.out.println("[" + getClass().getSimpleName() + "] " + message);
    }
}

// With static methods (Java 8+) — utility methods on the interface
public interface Validator<T> {
    boolean isValid(T value);

    static <T> Validator<T> not(Validator<T> validator) {
        return value -> !validator.isValid(value);
    }
}

// With private methods (Java 9+) — share code between defaults
public interface Reportable {
    default String generateHtmlReport() {
        return wrapInHtml(getReportData());
    }

    default String generateTextReport() {
        return getReportData();
    }

    String getReportData();

    private String wrapInHtml(String content) {
        return "<html><body>" + content + "</body></html>";
    }
}
```

### A Class Can Implement Multiple Interfaces

```java
public class Eagle extends Bird implements Flyable, Loggable, Serializable {
    @Override
    public void fly() { ... }

    @Override
    public double getAltitude() { ... }

    // Loggable.log() is inherited as a default method — no override needed
    // Serializable has no methods — it's a marker interface
}
```

### Decision Framework: Abstract Class vs. Interface

| Criterion | Abstract Class | Interface |
|-----------|---------------|-----------|
| **Relationship** | "Is-a" (strong) | "Can-do" / capability |
| **State** | Can have instance fields | Cannot (only `static final` constants) |
| **Constructors** | Yes | No |
| **Multiple inheritance** | Only one parent class | Multiple interfaces |
| **Default methods** | Concrete methods (always) | `default` methods (Java 8+) |
| **Access modifiers** | Any modifier on methods | Methods are `public` (Java 9+: `private` helpers) |

**Decision tree:**

```
Does the type represent a capability or behavior?
  → YES: Use an interface (Comparable, Serializable, Iterable)

Do related classes need to share state or constructor logic?
  → YES: Use an abstract class (AbstractList, Number)

When in doubt?
  → Start with an interface. You can always add an abstract class later.
```

### The Diamond Problem

When a class implements two interfaces with the same default method, you get a conflict:

```java
interface A {
    default void greet() { System.out.println("Hello from A"); }
}

interface B {
    default void greet() { System.out.println("Hello from B"); }
}

class C implements A, B {
    // COMPILE ERROR if you don't resolve the conflict!

    @Override
    public void greet() {
        A.super.greet();   // Explicitly choose A's version
        // Or: B.super.greet();
        // Or: provide your own implementation
    }
}
```

This is safe because interfaces have no state — the conflict is purely about behavior, which you resolve by overriding.

### Functional Interfaces

A functional interface has **exactly one abstract method**. It can be used with lambda expressions.

```java
@FunctionalInterface    // Optional but recommended — compiler enforces the constraint
public interface Predicate<T> {
    boolean test(T value);

    // Default and static methods don't count
    default Predicate<T> negate() {
        return value -> !test(value);
    }
}

// Lambda usage
Predicate<String> isLong = s -> s.length() > 10;
isLong.test("short");       // false
isLong.test("a very long string"); // true
```

---

## 6. SOLID Principles with Java Examples

SOLID is a set of five design principles that make software easier to understand, maintain, and extend. These principles are not abstract theories — they are practical guidelines for everyday code.

### S — Single Responsibility Principle (SRP)

**"A class should have one, and only one, reason to change."**

A "reason to change" maps to a stakeholder or business concern. If a class handles multiple concerns, changing one concern risks breaking another.

**Violation:**

```java
// This class has THREE reasons to change:
// 1. Business rules for calculating totals
// 2. Display format changes
// 3. Storage mechanism changes
public class Invoice {
    private List<LineItem> items;

    public double calculateTotal() {
        return items.stream()
            .mapToDouble(item -> item.price() * item.quantity())
            .sum();
    }

    public String generatePdf() {        // Display concern
        // PDF generation logic...
        return "PDF content";
    }

    public void saveToDatabase() {       // Persistence concern
        // JDBC code...
    }
}
```

**Fixed:**

```java
public class InvoiceCalculator {
    public double calculateTotal(List<LineItem> items) {
        return items.stream()
            .mapToDouble(item -> item.price() * item.quantity())
            .sum();
    }
}

public class InvoicePrinter {
    public String generatePdf(Invoice invoice) { ... }
    public String generateHtml(Invoice invoice) { ... }
}

public class InvoiceRepository {
    public void save(Invoice invoice) { ... }
    public Invoice findById(String id) { ... }
}
```

Now changing the PDF format doesn't touch calculation logic. Adding a new persistence strategy doesn't affect printing.

### O — Open/Closed Principle (OCP)

**"Software entities should be open for extension but closed for modification."**

You should be able to add new behavior **without changing existing code**.

**Violation:**

```java
public class DiscountCalculator {
    public double calculate(String customerType, double amount) {
        if ("regular".equals(customerType)) {
            return amount * 0.05;
        } else if ("premium".equals(customerType)) {
            return amount * 0.10;
        } else if ("vip".equals(customerType)) {
            return amount * 0.20;
        }
        // Every new customer type requires modifying this method!
        return 0;
    }
}
```

**Fixed:**

```java
public interface DiscountStrategy {
    double calculate(double amount);
}

public class RegularDiscount implements DiscountStrategy {
    @Override
    public double calculate(double amount) { return amount * 0.05; }
}

public class PremiumDiscount implements DiscountStrategy {
    @Override
    public double calculate(double amount) { return amount * 0.10; }
}

public class VipDiscount implements DiscountStrategy {
    @Override
    public double calculate(double amount) { return amount * 0.20; }
}

// Adding "employee" discount: create a new class, modify NOTHING existing
public class EmployeeDiscount implements DiscountStrategy {
    @Override
    public double calculate(double amount) { return amount * 0.30; }
}

// The calculator is CLOSED for modification
public class DiscountCalculator {
    public double apply(DiscountStrategy strategy, double amount) {
        return strategy.calculate(amount);
    }
}
```

### L — Liskov Substitution Principle (LSP)

**"Subtypes must be substitutable for their base types without breaking correctness."**

If code works with a base type, it should work identically with any subtype. The subtype must honor the base type's **contract** — preconditions, postconditions, and invariants.

**Classic Violation: Square extends Rectangle**

```java
public class Rectangle {
    protected int width;
    protected int height;

    public void setWidth(int width)   { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public int area()                 { return width * height; }
}

public class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width;    // Violates Rectangle's contract!
    }

    @Override
    public void setHeight(int height) {
        this.width = height;    // Violates Rectangle's contract!
        this.height = height;
    }
}

// Code that works with Rectangle breaks with Square:
void resize(Rectangle r) {
    r.setWidth(5);
    r.setHeight(10);
    assert r.area() == 50;  // FAILS for Square! area() returns 100
}
```

**Fix:** Don't model "Square is-a Rectangle" through inheritance. Use separate types or a shared interface:

```java
public interface Shape {
    double area();
}

public record Rectangle(double width, double height) implements Shape {
    @Override
    public double area() { return width * height; }
}

public record Square(double side) implements Shape {
    @Override
    public double area() { return side * side; }
}
```

### I — Interface Segregation Principle (ISP)

**"No client should be forced to depend on methods it doesn't use."**

Fat interfaces force implementors to provide stub implementations for methods they don't need.

**Violation:**

```java
public interface Worker {
    void work();
    void eat();
    void sleep();
}

// A Robot can work but doesn't eat or sleep!
public class Robot implements Worker {
    @Override public void work()  { /* actual work */ }
    @Override public void eat()   { /* does nothing — forced to implement! */ }
    @Override public void sleep() { /* does nothing — forced to implement! */ }
}
```

**Fixed:**

```java
public interface Workable {
    void work();
}

public interface Feedable {
    void eat();
}

public interface Restable {
    void sleep();
}

public class Human implements Workable, Feedable, Restable {
    @Override public void work()  { ... }
    @Override public void eat()   { ... }
    @Override public void sleep() { ... }
}

public class Robot implements Workable {
    @Override public void work()  { ... }
    // No eat() or sleep() — Robot only implements what it needs
}
```

### D — Dependency Inversion Principle (DIP)

**"High-level modules should not depend on low-level modules. Both should depend on abstractions."**

Don't wire a high-level business class directly to a low-level implementation. Depend on an interface instead — this allows you to swap implementations.

**Violation:**

```java
// High-level module directly depends on low-level module
public class OrderService {
    private MySqlDatabase database = new MySqlDatabase();  // Concrete!
    private SmtpEmailSender emailer = new SmtpEmailSender(); // Concrete!

    public void placeOrder(Order order) {
        database.save(order);
        emailer.sendConfirmation(order);
    }
}
// Changing to PostgreSQL or SendGrid requires modifying OrderService
```

**Fixed:**

```java
// Abstractions
public interface OrderRepository {
    void save(Order order);
}

public interface NotificationService {
    void sendConfirmation(Order order);
}

// High-level module depends on abstractions
public class OrderService {
    private final OrderRepository repository;
    private final NotificationService notifier;

    public OrderService(OrderRepository repository, NotificationService notifier) {
        this.repository = repository;
        this.notifier = notifier;
    }

    public void placeOrder(Order order) {
        repository.save(order);
        notifier.sendConfirmation(order);
    }
}

// Low-level modules implement abstractions
public class MySqlOrderRepository implements OrderRepository {
    @Override public void save(Order order) { /* MySQL logic */ }
}

public class EmailNotificationService implements NotificationService {
    @Override public void sendConfirmation(Order order) { /* SMTP logic */ }
}

// Wiring (done at the composition root / DI framework)
OrderService service = new OrderService(
    new MySqlOrderRepository(),
    new EmailNotificationService()
);
```

Now you can swap `MySqlOrderRepository` for `PostgresOrderRepository` without touching `OrderService`. You can also test `OrderService` with mock implementations.

### SOLID Quick Reference

```
S — One class, one responsibility, one reason to change.
O — Extend behavior by adding new code, not changing existing code.
L — Subclasses must work everywhere the parent works.
I — Many focused interfaces beat one fat interface.
D — Depend on abstractions (interfaces), not concrete implementations.
```

---

## 7. Composition vs. Inheritance

### The Problem with Inheritance

Inheritance creates a **tight coupling** between parent and child. The subclass depends on the parent's implementation details, not just its public API.

```
Inheritance = "is-a" relationship
  Car IS-A Vehicle
  Dog IS-A Animal

Composition = "has-a" relationship
  Car HAS-A Engine
  Car HAS-A Transmission
  Department HAS-A List<Employee>
```

### When Inheritance Goes Wrong

```java
// Attempt: Stack "is-a" ArrayList (so we inherit add, get, etc.)
public class Stack<E> extends ArrayList<E> {
    public void push(E item) {
        add(item);
    }

    public E pop() {
        return remove(size() - 1);
    }
}

// Problem: callers can bypass stack discipline
Stack<String> stack = new Stack<>();
stack.push("first");
stack.push("second");
stack.add(0, "sneaky");     // ArrayList method — breaks LIFO contract!
stack.get(1);                // Direct access — stacks shouldn't allow this!
```

### Composition via Delegation

Instead of extending a class, hold an instance of it and delegate only the methods you want to expose.

```java
public class Stack<E> {
    private final List<E> elements = new ArrayList<>();  // HAS-A list

    public void push(E item) {
        elements.add(item);
    }

    public E pop() {
        if (elements.isEmpty()) {
            throw new EmptyStackException();
        }
        return elements.remove(elements.size() - 1);
    }

    public E peek() {
        if (elements.isEmpty()) {
            throw new EmptyStackException();
        }
        return elements.get(elements.size() - 1);
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public int size() {
        return elements.size();
    }

    // No add(), get(), or remove() exposed — stack discipline enforced!
}
```

### The Decorator Pattern: Composition in Action

The Decorator pattern wraps an object to add behavior without modifying it. Java's I/O streams are a textbook example.

```java
// Base interface
public interface Notifier {
    void send(String message);
}

// Concrete implementation
public class EmailNotifier implements Notifier {
    @Override
    public void send(String message) {
        System.out.println("Email: " + message);
    }
}

// Decorator base — uses composition (HAS-A Notifier)
public abstract class NotifierDecorator implements Notifier {
    protected final Notifier wrapped;

    protected NotifierDecorator(Notifier wrapped) {
        this.wrapped = wrapped;
    }
}

// Concrete decorators
public class SlackDecorator extends NotifierDecorator {
    public SlackDecorator(Notifier wrapped) { super(wrapped); }

    @Override
    public void send(String message) {
        wrapped.send(message);                        // Delegate to wrapped
        System.out.println("Slack: " + message);      // Add behavior
    }
}

public class SmsDecorator extends NotifierDecorator {
    public SmsDecorator(Notifier wrapped) { super(wrapped); }

    @Override
    public void send(String message) {
        wrapped.send(message);
        System.out.println("SMS: " + message);
    }
}

// Usage: compose behaviors dynamically
Notifier notifier = new SmsDecorator(
                        new SlackDecorator(
                            new EmailNotifier()));
notifier.send("Server is down!");
// Output:
// Email: Server is down!
// Slack: Server is down!
// SMS: Server is down!
```

### Java Standard Library Example

```java
// java.io uses the Decorator pattern extensively:
InputStream raw = new FileInputStream("data.txt");
InputStream buffered = new BufferedInputStream(raw);       // Adds buffering
InputStream data = new DataInputStream(buffered);           // Adds typed reads

// Each wrapper adds a layer of behavior via composition
```

### When to Use Inheritance vs. Composition

| Use Inheritance When... | Use Composition When... |
|------------------------|------------------------|
| Clear "is-a" relationship | "Has-a" or "uses-a" relationship |
| You control the superclass | You don't control the class you'd extend |
| Subclass is a genuine specialization | You want to reuse behavior from multiple sources |
| You need runtime polymorphism via overriding | You want to change behavior at runtime |
| The hierarchy is shallow (2-3 levels max) | The "is-a" test feels forced or questionable |

**Default position:** Start with composition. Use inheritance only when there's a clear, natural "is-a" relationship.

---

## 8. Records and Sealed Classes

### Records (Java 16+)

A record is a concise way to declare an **immutable data carrier** class. The compiler auto-generates the constructor, getters, `equals()`, `hashCode()`, and `toString()`.

```java
// This one line:
public record Point(int x, int y) {}

// Is equivalent to writing all of this:
public final class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }       // Note: x(), not getX()
    public int y() { return y; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point p)) return false;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point[x=" + x + ", y=" + y + "]";
    }
}
```

**Key restrictions:**
- Records are implicitly `final` — cannot be extended.
- All fields are `private final` — no mutable state.
- Cannot extend a class (but implicitly extend `java.lang.Record`).
- Can implement interfaces.
- Cannot declare additional instance fields (only static fields allowed).

### Compact Constructors

Records support a **compact constructor** for validation — you write the logic without parameter assignments (those happen automatically after the body).

```java
public record Email(String address) {

    // Compact constructor — no parameter list
    public Email {
        if (address == null || !address.contains("@")) {
            throw new IllegalArgumentException("Invalid email: " + address);
        }
        // You can reassign the parameter to normalize:
        address = address.toLowerCase().trim();
        // After this body, the compiler generates: this.address = address;
    }
}

new Email("ALICE@Example.COM");  // Stored as "alice@example.com"
new Email("invalid");            // Throws IllegalArgumentException
```

### Records Can Implement Interfaces

```java
public interface Printable {
    String toPrintableString();
}

public record Invoice(
        String id,
        String customer,
        BigDecimal total
) implements Printable {

    @Override
    public String toPrintableString() {
        return "Invoice #%s for %s: $%s".formatted(id, customer, total);
    }
}
```

### Records Can Have Custom Methods and Static Fields

```java
public record Range(int start, int end) {

    public static final Range EMPTY = new Range(0, 0);

    public Range {
        if (start > end) {
            throw new IllegalArgumentException("start must be <= end");
        }
    }

    public int length() {
        return end - start;
    }

    public boolean contains(int value) {
        return value >= start && value < end;
    }

    public boolean overlaps(Range other) {
        return this.start < other.end && other.start < this.end;
    }
}
```

### Sealed Classes (Java 17+)

A sealed class **restricts which classes can extend it**. This gives you control over your type hierarchy — you know at compile time every possible subtype.

```java
public abstract sealed class Shape
        permits Circle, Rectangle, Triangle {
    public abstract double area();
}

public final class Circle extends Shape {
    private final double radius;

    public Circle(double radius) { this.radius = radius; }

    @Override
    public double area() { return Math.PI * radius * radius; }
}

public final class Rectangle extends Shape {
    private final double width, height;

    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() { return width * height; }
}

public final class Triangle extends Shape {
    private final double base, height;

    public Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }

    @Override
    public double area() { return 0.5 * base * height; }
}

// This would NOT compile — Pentagon is not in the permits list:
// public class Pentagon extends Shape { ... }  // COMPILE ERROR
```

**Subclass modifiers — each permitted subclass must be:**

| Modifier | Meaning |
|----------|---------|
| `final` | No further extension allowed (most common) |
| `sealed` | Can be extended, but only by its own permitted subclasses |
| `non-sealed` | Open for extension by anyone (escape hatch) |

```java
public abstract sealed class Account permits SavingsAccount, CheckingAccount, InvestmentAccount {}

public final class SavingsAccount extends Account { ... }

public sealed class CheckingAccount extends Account permits StudentChecking, BusinessChecking { ... }
public final class StudentChecking extends CheckingAccount { ... }
public final class BusinessChecking extends CheckingAccount { ... }

public non-sealed class InvestmentAccount extends Account { ... }
// Anyone can extend InvestmentAccount — the seal is broken here
```

### Sealed + Records = Algebraic Data Types

Combining sealed classes with records gives Java algebraic data types — a powerful modeling technique from functional programming.

```java
public sealed interface Result<T>
        permits Result.Success, Result.Failure {

    record Success<T>(T value) implements Result<T> {}
    record Failure<T>(String error) implements Result<T> {}
}

// Usage
Result<User> result = findUser("alice");
switch (result) {
    case Result.Success<User> s -> System.out.println("Found: " + s.value());
    case Result.Failure<User> f -> System.out.println("Error: " + f.error());
}
```

### Pattern Matching with Sealed Hierarchies in `switch`

When you switch on a sealed type, the compiler knows all possible subtypes. If you cover all of them, no `default` is needed.

```java
// The compiler ensures exhaustiveness — no default needed
String describe(Shape shape) {
    return switch (shape) {
        case Circle c    -> "Circle with radius " + c.getRadius();
        case Rectangle r -> "Rectangle " + r.getWidth() + "x" + r.getHeight();
        case Triangle t  -> "Triangle with base " + t.getBase();
        // No default — compiler knows these are the only options
    };
}
```

If you add a new permitted subclass to `Shape`, every `switch` that doesn't handle it will fail to compile — the compiler forces you to handle it. This is a huge advantage over traditional `instanceof` chains.

---

## 9. Object Class Methods: equals, hashCode, toString

### Every Class Extends Object

Every Java class implicitly extends `java.lang.Object`, which provides default implementations of several methods. Three of them are commonly overridden:

| Method | Default behavior | Why override? |
|--------|-----------------|---------------|
| `equals(Object)` | `==` (reference identity) | Value-based equality |
| `hashCode()` | Memory address-derived integer | Must match `equals` for hash-based collections |
| `toString()` | `ClassName@hexHashCode` | Human-readable representation for debugging |

### The equals() Contract

The `equals()` method must satisfy these properties:

| Property | Meaning |
|----------|---------|
| **Reflexive** | `x.equals(x)` is always `true` |
| **Symmetric** | If `x.equals(y)`, then `y.equals(x)` |
| **Transitive** | If `x.equals(y)` and `y.equals(z)`, then `x.equals(z)` |
| **Consistent** | Multiple calls return the same result (if objects haven't changed) |
| **Non-null** | `x.equals(null)` is always `false` |

### Implementation Recipe (Effective Java Items 10–11)

```java
public class Student {

    private final String studentId;
    private final String name;
    private final int enrollmentYear;

    public Student(String studentId, String name, int enrollmentYear) {
        this.studentId = Objects.requireNonNull(studentId);
        this.name = Objects.requireNonNull(name);
        this.enrollmentYear = enrollmentYear;
    }

    @Override
    public boolean equals(Object o) {
        // Step 1: Reference check — same object?
        if (this == o) return true;

        // Step 2: Type check — correct type? (also handles null)
        if (!(o instanceof Student other)) return false;

        // Step 3: Compare significant fields
        return enrollmentYear == other.enrollmentYear
            && Objects.equals(studentId, other.studentId)
            && Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, name, enrollmentYear);
    }

    @Override
    public String toString() {
        return "Student{id='%s', name='%s', year=%d}"
            .formatted(studentId, name, enrollmentYear);
    }
}
```

**Step-by-step explanation:**

1. **`this == o`** — If both references point to the same object, they are trivially equal. This is a performance shortcut.

2. **`instanceof` check** — Handles two things at once:
   - If `o` is `null`, `instanceof` returns `false` (no separate null check needed).
   - If `o` is the wrong type, returns `false`.
   - With pattern matching (`Student other`), also casts in one step.

3. **Field comparison** — Compare all fields that define "logical equality":
   - Primitives: use `==`
   - Objects: use `Objects.equals()` (handles nulls safely)
   - Floating-point: use `Double.compare()` or `Float.compare()` (handles `NaN` and `-0.0`)
   - Arrays: use `Arrays.equals()`

### The hashCode() Contract

```
IF   a.equals(b) is true
THEN a.hashCode() == b.hashCode()  MUST be true

IF   a.equals(b) is false
THEN a.hashCode() == b.hashCode()  CAN be true (collision) but SHOULD differ
```

**Breaking this contract breaks HashMap and HashSet:**

```java
// If you override equals() but NOT hashCode():
Set<Student> set = new HashSet<>();
Student s1 = new Student("S001", "Alice", 2024);
Student s2 = new Student("S001", "Alice", 2024);

s1.equals(s2);     // true — we overrode equals()

set.add(s1);
set.contains(s2);  // false! Different hashCode → different bucket → not found
set.size();        // 1, but set.add(s2) would make it 2 — logically wrong
```

**How HashMap uses hashCode and equals:**

```
1. hashCode() → determines which BUCKET the entry goes into
2. equals()   → within that bucket, determines if two keys are the SAME

If hashCode() differs for equal objects → they land in different buckets
→ equals() is never even called → HashMap thinks they're different keys
```

### Implementation Details

```java
// Objects.hash() — convenient, good enough for most cases
@Override
public int hashCode() {
    return Objects.hash(studentId, name, enrollmentYear);
}

// Manual implementation — slightly more control
@Override
public int hashCode() {
    int result = studentId.hashCode();
    result = 31 * result + name.hashCode();
    result = 31 * result + Integer.hashCode(enrollmentYear);
    return result;
}
// Why 31? It's an odd prime. 31 * i can be optimized by the JVM to (i << 5) - i.
```

### toString() — Always Override

The default `toString()` produces something like `Student@4a574795` — useless for debugging.

```java
@Override
public String toString() {
    return "Student{id='%s', name='%s', year=%d}"
        .formatted(studentId, name, enrollmentYear);
}

// Now:
System.out.println(student);
// Student{id='S001', name='Alice', year=2024}

// Instead of:
// Student@4a574795
```

**Guidelines:**
- Include all important fields.
- Use a consistent format across your codebase.
- Don't include sensitive data (passwords, SSNs).
- If the class is part of an API, document the format.

### equals/hashCode: `instanceof` vs. `getClass()`

Two schools of thought:

```java
// Option A: instanceof (Effective Java recommendation)
if (!(o instanceof Student other)) return false;
// Allows subclasses to be equal to parent — supports Liskov
// A GraduateStudent CAN be equal to a Student (if significant fields match)

// Option B: getClass() (strict type equality)
if (o == null || getClass() != o.getClass()) return false;
// Only exact same class can be equal
// A GraduateStudent is NEVER equal to a Student
```

**Rule of thumb:** Use `instanceof` for `final` classes or when subclass equality makes sense. Use `getClass()` when mixed-type equality would be confusing.

---

## 10. Enums: Advanced Usage

### Basic Enums

An enum is a special class that represents a fixed set of constants. Each constant is a `public static final` instance of the enum type.

```java
public enum Direction {
    NORTH, SOUTH, EAST, WEST
}

Direction d = Direction.NORTH;
System.out.println(d);           // "NORTH"
System.out.println(d.name());    // "NORTH" (exact constant name)
System.out.println(d.ordinal()); // 0 (position, 0-indexed)

// Iteration
for (Direction dir : Direction.values()) {
    System.out.println(dir);
}

// Parsing from string
Direction parsed = Direction.valueOf("EAST");  // Direction.EAST
// Direction.valueOf("east");  // IllegalArgumentException — case-sensitive!

// Switch works naturally with enums
String label = switch (d) {
    case NORTH -> "Up";
    case SOUTH -> "Down";
    case EAST  -> "Right";
    case WEST  -> "Left";
};
```

### Enums with Fields and Constructors

Enum constants can carry data. The constructor is always `private` (you can't create new instances at runtime).

```java
public enum Planet {
    MERCURY(3.303e+23, 2.4397e6),
    VENUS  (4.869e+24, 6.0518e6),
    EARTH  (5.976e+24, 6.37814e6),
    MARS   (6.421e+23, 3.3972e6);

    private final double mass;    // in kilograms
    private final double radius;  // in meters

    // Constructor is implicitly private — cannot be called from outside
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }

    // Gravitational constant
    private static final double G = 6.67300E-11;

    public double surfaceGravity() {
        return G * mass / (radius * radius);
    }

    public double surfaceWeight(double otherMass) {
        return otherMass * surfaceGravity();
    }
}

double earthWeight = 75.0;
double mass = earthWeight / Planet.EARTH.surfaceGravity();
for (Planet p : Planet.values()) {
    System.out.printf("Your weight on %s is %6.2f%n", p, p.surfaceWeight(mass));
}
```

### Enums with Methods

```java
public enum HttpStatus {
    OK(200, "OK"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized");

    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode()      { return code; }
    public String getMessage() { return message; }

    public boolean isSuccess() { return code >= 200 && code < 300; }
    public boolean isError()   { return code >= 400; }

    // Static lookup method
    public static HttpStatus fromCode(int code) {
        for (HttpStatus status : values()) {
            if (status.code == code) return status;
        }
        throw new IllegalArgumentException("Unknown HTTP status code: " + code);
    }
}

HttpStatus status = HttpStatus.fromCode(404);
System.out.println(status);              // NOT_FOUND
System.out.println(status.getMessage()); // "Not Found"
System.out.println(status.isError());    // true
```

### Abstract Methods in Enums — Strategy per Constant

Each enum constant can provide its own implementation of an abstract method. This is the **strategy pattern baked into the language**.

```java
public enum Operation {
    ADD("+") {
        @Override
        public double apply(double a, double b) { return a + b; }
    },
    SUBTRACT("-") {
        @Override
        public double apply(double a, double b) { return a - b; }
    },
    MULTIPLY("*") {
        @Override
        public double apply(double a, double b) { return a * b; }
    },
    DIVIDE("/") {
        @Override
        public double apply(double a, double b) {
            if (b == 0) throw new ArithmeticException("Division by zero");
            return a / b;
        }
    };

    private final String symbol;

    Operation(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() { return symbol; }

    // Each constant MUST implement this
    public abstract double apply(double a, double b);

    @Override
    public String toString() { return symbol; }
}

// Usage — no if/else or switch needed
double result = Operation.ADD.apply(10, 5);  // 15.0

// Dynamic dispatch
for (Operation op : Operation.values()) {
    System.out.printf("10 %s 5 = %.1f%n", op, op.apply(10, 5));
}
// 10 + 5 = 15.0
// 10 - 5 = 5.0
// 10 * 5 = 50.0
// 10 / 5 = 2.0
```

### EnumSet and EnumMap

Java provides specialized, high-performance collections for enums that use bit manipulation internally.

```java
import java.util.EnumMap;
import java.util.EnumSet;

public enum Day {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
}

// EnumSet — backed by a single long bitmask (blazing fast, tiny memory)
EnumSet<Day> weekdays = EnumSet.range(Day.MONDAY, Day.FRIDAY);
EnumSet<Day> weekend  = EnumSet.of(Day.SATURDAY, Day.SUNDAY);
EnumSet<Day> allDays  = EnumSet.allOf(Day.class);
EnumSet<Day> noDays   = EnumSet.noneOf(Day.class);

weekdays.contains(Day.MONDAY);   // true — O(1), just a bitmask check
weekend.contains(Day.MONDAY);    // false

// Set operations
EnumSet<Day> complement = EnumSet.complementOf(weekdays);  // {SATURDAY, SUNDAY}

// EnumMap — backed by a plain array indexed by ordinal (fast, compact)
EnumMap<Day, String> schedule = new EnumMap<>(Day.class);
schedule.put(Day.MONDAY, "Team standup");
schedule.put(Day.FRIDAY, "Demo day");

schedule.get(Day.MONDAY);  // "Team standup"
schedule.get(Day.TUESDAY); // null
```

**When to use:**
- `EnumSet` instead of `int` bitfields (flags): type-safe, iterable, readable.
- `EnumMap` instead of `HashMap<MyEnum, V>`: faster, less memory.

### Enums as Singletons (Effective Java Item 3)

Josh Bloch calls a single-element enum "the best way to implement a singleton":

```java
public enum DatabaseConnection {
    INSTANCE;

    private final Connection connection;

    DatabaseConnection() {
        this.connection = createConnection();
    }

    public Connection getConnection() {
        return connection;
    }

    private Connection createConnection() {
        // ...actual connection setup...
        return null;
    }
}

// Usage
Connection conn = DatabaseConnection.INSTANCE.getConnection();
```

**Why enum singletons are superior:**
- Thread-safe by default (JVM guarantees single initialization).
- Serialization-safe (no duplicate instances after deserialization).
- Reflection-safe (the JVM prevents creating enum instances via reflection).
- Concise — no `private` constructors, `getInstance()` methods, or `volatile` fields.

### Enum-Based State Machines

Enums naturally model state machines where transitions are controlled:

```java
public enum OrderStatus {
    PENDING {
        @Override
        public OrderStatus next() { return CONFIRMED; }
    },
    CONFIRMED {
        @Override
        public OrderStatus next() { return SHIPPED; }
    },
    SHIPPED {
        @Override
        public OrderStatus next() { return DELIVERED; }
    },
    DELIVERED {
        @Override
        public OrderStatus next() {
            throw new IllegalStateException("Order already delivered");
        }
    },
    CANCELLED {
        @Override
        public OrderStatus next() {
            throw new IllegalStateException("Cannot advance a cancelled order");
        }
    };

    public abstract OrderStatus next();

    public boolean canCancel() {
        return this == PENDING || this == CONFIRMED;
    }
}

OrderStatus status = OrderStatus.PENDING;
status = status.next();   // CONFIRMED
status = status.next();   // SHIPPED
// status.canCancel();     // false — too late
```

---

## Quick Reference Table

| Topic | Key Takeaway |
|-------|-------------|
| Class vs. Object | Class = blueprint (Method Area); Object = instance (Heap) |
| Constructor | No return type, same name as class; `this(...)` delegates, `super(...)` chains |
| `this` keyword | Reference to the current object; disambiguates fields, enables fluent APIs |
| Static | Belongs to the class, not instances; no `this` access |
| Init blocks | Static: once on class load; Instance: every `new`, before constructor |
| Access modifiers | `private` → package-private → `protected` → `public` |
| Encapsulation | Fields `private`, expose via getters; defensive copy mutable fields |
| Immutable objects | `final` class + `final` fields + no setters + defensive copies |
| Inheritance | `extends` one class; constructors run top-down; tight coupling risk |
| `super` | Call parent constructor or access overridden parent method |
| Overriding rules | Same signature, covariant return, ≥ access, ≤ exceptions, `@Override` |
| `final` | `final` method: can't override; `final` class: can't extend |
| Compile-time poly | Overloading — resolved by compiler based on declared types |
| Runtime poly | Overriding — resolved by JVM based on actual object type |
| Upcasting | Subtype → Supertype (always safe, implicit) |
| Downcasting | Supertype → Subtype (requires cast, use `instanceof` first) |
| Abstract class | Can have state + constructors + abstract methods; single inheritance |
| Interface | Contract (pure abstraction); `default`, `static`, `private` methods; multiple inheritance |
| Functional interface | One abstract method; usable with lambdas |
| SRP | One class, one reason to change |
| OCP | Extend by adding new classes, not modifying existing ones |
| LSP | Subtypes must be substitutable without breaking correctness |
| ISP | Prefer many focused interfaces over one fat interface |
| DIP | Depend on abstractions (interfaces), not concrete implementations |
| Composition | "Has-a" via delegation; preferred over inheritance by default |
| Decorator | Wraps an object to add behavior; classic composition pattern |
| Records | Immutable data carriers; auto-generates constructor, accessors, equals, hashCode, toString |
| Compact constructor | Validation in records without repeating field assignments |
| Sealed classes | `permits` restricts which classes can extend; enables exhaustive `switch` |
| Sealed + Records | Algebraic data types in Java |
| `equals()` contract | Reflexive, symmetric, transitive, consistent, non-null |
| `hashCode()` contract | Equal objects → equal hash codes; must be overridden if `equals()` is |
| `toString()` | Always override; include all important fields for debugging |
| Enum with fields | Constants carry data; constructor is `private` |
| Enum abstract method | Strategy per constant — each provides its own implementation |
| `EnumSet` / `EnumMap` | Specialized collections for enums; backed by bit manipulation / arrays |
| Enum singleton | Thread-safe, serialization-safe, reflection-safe singleton pattern |

---

## What's Next?

Once you're comfortable with everything above and have completed the three exercises (`Employee Management`, `Payment Processing`, `Library Catalog`), run through the **Self-Assessment Checklist** in the [Phase 02 README](README.md). If you can answer every item confidently, move on to [Phase 03 — Exception Handling & Defensive Programming](../phase-03-exceptions-defensive/).
