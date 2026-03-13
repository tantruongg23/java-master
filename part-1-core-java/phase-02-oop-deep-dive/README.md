# Phase 02 — OOP Deep Dive

> **Duration:** ~2–3 weeks (~29 hours)
> **Pace:** Deliberate and thorough — OOP is the backbone of every Java codebase you'll ever touch.
> **Goal:** Move beyond textbook definitions. Understand *why* the rules exist, apply SOLID in real code, and build systems where adding a new feature means adding a new class — not editing twenty old ones.

---

## Learning Objectives

By the end of this phase you will be able to:

1. Design class hierarchies with proper use of `abstract`, `final`, and access modifiers.
2. Implement robust `equals()`, `hashCode()`, and `toString()` following the contract.
3. Distinguish compile-time polymorphism (overloading) from runtime polymorphism (overriding) and apply both deliberately.
4. Choose between abstract classes and interfaces based on concrete design criteria, not guesswork.
5. Explain and demonstrate all five SOLID principles with Java code examples.
6. Prefer composition over inheritance and implement the delegation pattern.
7. Use Java records for immutable data carriers and sealed classes to restrict type hierarchies.
8. Leverage enums beyond simple constants — with fields, methods, and abstract behavior.

---

## Topics & Estimated Hours

### 1. Classes, Objects, Constructors, `this` Keyword (2 h)

- Class as a blueprint: fields (state), methods (behavior), constructors (initialization).
- Constructor overloading and `this(...)` delegation.
- `this` keyword: disambiguating fields from parameters, returning the current instance (fluent builders).
- Object lifecycle: allocation (`new`), initialization (constructor), garbage collection eligibility.
- Static vs. instance members: when and why.
- Initialization blocks: instance `{ }` and `static { }`.

### 2. Encapsulation: Access Modifiers, Getters/Setters, Immutable Objects (2 h)

- Four access levels: `private`, package-private (default), `protected`, `public`.
- Rule of thumb: fields `private`, expose via getters; setters only when truly needed.
- **Defensive copying:** returning copies of mutable fields (e.g., `Date`, `List`) to preserve encapsulation.
- **Immutable objects:** `final` class, `final` fields, no setters, deep copies of mutable fields in constructor and getters.
- Why immutability matters: thread safety, cache keys, simpler reasoning.

### 3. Inheritance: extends, super, Constructor Chaining, Method Overriding (3 h)

- `extends` — single inheritance in Java (one parent class).
- `super` keyword: calling parent constructors, accessing overridden methods.
- **Constructor chaining:** the implicit `super()` call; what happens when the parent has no no-arg constructor.
- **Method overriding rules:** same signature, covariant return types, access cannot be more restrictive, `@Override` annotation.
- `final` methods (cannot override) and `final` classes (cannot extend).
- The fragile base class problem — why deep hierarchies are dangerous.

### 4. Polymorphism: Compile-Time vs. Runtime (3 h)

- **Compile-time (static) polymorphism:** method overloading — resolved by the compiler based on the declared type of arguments.
- **Runtime (dynamic) polymorphism:** method overriding — resolved by the JVM based on the actual type of the object (virtual method dispatch).
- **Upcasting:** `Animal a = new Dog();` — always safe, implicit.
- **Downcasting:** `Dog d = (Dog) a;` — requires a cast, risky, use `instanceof` first.
- **Pattern matching for instanceof** (Java 16+): `if (a instanceof Dog d) { d.fetch(); }`
- Why polymorphism matters: programming to interfaces, eliminating `if/else` chains.

### 5. Abstract Classes vs. Interfaces (3 h)

- **Abstract class:** can have state (fields), constructors, concrete methods, and abstract methods. A class can extend only one.
- **Interface:** pure contract (pre-Java 8). Since Java 8: `default` methods, `static` methods. Since Java 9: `private` methods.
- Decision framework:
  - Use an interface when defining a capability/contract (`Serializable`, `Comparable`, `Runnable`).
  - Use an abstract class when sharing code/state among closely related classes.
  - When in doubt, start with an interface.
- Multiple interface inheritance: diamond problem is resolved because there's no state conflict.
- Functional interfaces and their role with lambdas (preview for Phase 04).

### 6. SOLID Principles with Java Examples (6 h)

Spend roughly 1+ hour per principle with code exercises.

| Principle | Summary | Java Example |
|-----------|---------|--------------|
| **S** — Single Responsibility | A class has one reason to change. | Separate `InvoiceCalculator`, `InvoicePrinter`, `InvoicePersistence` instead of a god-class `Invoice`. |
| **O** — Open/Closed | Open for extension, closed for modification. | New payment types added by implementing `PaymentMethod` — no `if/else` chain in `PaymentProcessor`. |
| **L** — Liskov Substitution | Subtypes must be substitutable for their base types without breaking correctness. | `Square extends Rectangle` violates LSP if `setWidth` changes height. Fix: use separate types or an interface. |
| **I** — Interface Segregation | No client should be forced to depend on methods it doesn't use. | Split `Worker` interface into `Workable` and `Feedable` — robots don't eat. |
| **D** — Dependency Inversion | High-level modules depend on abstractions, not concrete implementations. | `OrderService` depends on `PaymentGateway` (interface), not `StripeGateway` (class). |

### 7. Composition vs. Inheritance (3 h)

- **"Favor composition over inheritance"** — *Effective Java* Item 18.
- Inheritance breaks encapsulation: subclass is coupled to parent's implementation.
- Composition via delegation: `Car` **has-a** `Engine` instead of `Car` **is-a** `Vehicle` (when the relationship is questionable).
- Decorator pattern as a composition example: `BufferedInputStream` wraps `FileInputStream`.
- When inheritance *is* appropriate: clear "is-a" relationship AND you control the superclass.

### 8. Records and Sealed Classes (2 h)

- **Records** (Java 16+): `record Point(int x, int y) {}` — auto-generates constructor, getters, `equals`, `hashCode`, `toString`. Restrictions: implicitly `final`, no mutable state, cannot extend a class (but can implement interfaces).
- **Compact constructors:** validation logic without reassigning fields.
- **Sealed classes** (Java 17+): `sealed class Shape permits Circle, Rectangle, Triangle {}` — restricts which classes can extend. Subclasses must be `final`, `sealed`, or `non-sealed`.
- Sealed + records = algebraic data types in Java.
- Pattern matching with sealed hierarchies in `switch` (preview features).

### 9. Object Class Methods: equals, hashCode, toString (3 h)

- **`equals()` contract:** reflexive, symmetric, transitive, consistent, `null` returns `false`.
- **`hashCode()` contract:** equal objects must have equal hash codes; unequal objects *should* have different hash codes.
- Breaking the contract → broken `HashMap`, `HashSet` behavior.
- **Implementation recipe** (Effective Java Item 10–11):
  1. Check `== this`, then `instanceof`, then compare significant fields.
  2. Use `Objects.hash(field1, field2, ...)` for `hashCode()`.
  3. Use `Objects.equals()` for nullable field comparisons.
- **`toString()`:** always override — invaluable for debugging. Include all important fields.
- IDE generation vs. manual: know what the generated code does.

### 10. Enums: Advanced Usage (2 h)

- Basic enums: `enum Color { RED, GREEN, BLUE }`
- Enums with fields and constructors: `enum Planet { EARTH(5.97e24, 6.37e6); ... }`
- Enums with methods: `getLabel()`, `fromCode(String)`.
- Abstract methods in enums — each constant provides its own implementation (strategy per constant).
- `EnumSet` and `EnumMap` — specialized, high-performance collections.
- Enums as singletons (Effective Java Item 3).
- Enum-based state machines.

---

## References

| Resource | Scope |
|----------|-------|
| *Effective Java*, 3rd ed. — Items 10–25 | `equals`, `hashCode`, inheritance, interfaces, composition |
| [Oracle OOP Trail](https://docs.oracle.com/javase/tutorial/java/concepts/index.html) | Official OOP tutorial |
| [SOLID Principles by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2020/10/18/Solid-Relevance.html) | Uncle Bob's own explanation |
| *Head First Design Patterns*, 2nd ed. — Chapters 1–3 | Strategy, Observer, Decorator patterns |
| [Baeldung — Java Records](https://www.baeldung.com/java-record-keyword) | Records deep dive |
| [Baeldung — Sealed Classes](https://www.baeldung.com/java-sealed-classes-interfaces) | Sealed classes tutorial |

---

## Exercises

### Exercise 1 — Employee Management System

**Business Context:** An HR department needs a payroll system that calculates compensation for different employment types and organizes employees into departments.

**Requirements:**

1. **`Employee`** (abstract class):
   - Fields: `name` (String), `id` (String), `hireDate` (LocalDate).
   - Abstract method: `double calculateSalary()`.
   - Override `equals()` (by `id`), `hashCode()`, and `toString()`.

2. **Concrete employee types:**
   - `FullTimeEmployee`: has `annualSalary`. `calculateSalary()` returns `annualSalary / 12`.
   - `PartTimeEmployee`: has `hourlyRate` and `hoursWorked`. `calculateSalary()` returns `hourlyRate * hoursWorked`.
   - `Contractor`: has `dailyRate` and `daysWorked`. `calculateSalary()` returns `dailyRate * daysWorked`.

3. **`Manager`** extends `FullTimeEmployee`:
   - Has a `List<Employee>` of direct reports.
   - `calculateSalary()` adds a 15% management bonus.

4. **`Department`** (composition, not inheritance):
   - Contains a `List<Employee>`.
   - Methods: `addEmployee`, `removeEmployee`, `getTotalPayroll()`, `getEmployeesByType(Class<?>)`.

5. **Demonstrate polymorphism:** Create a `List<Employee>` with mixed types, iterate, and call `calculateSalary()` on each.

**Bonus:**
- Implement `Comparable<Employee>` to sort by salary descending.
- Add a `PayrollReport` class that generates a formatted report.

**Starter files:**
- `exercises/src/main/java/exercises/employee/Employee.java`
- `exercises/src/main/java/exercises/employee/Department.java`

---

### Exercise 2 — Payment Processing System (SOLID Focus)

**Business Context:** An e-commerce platform needs a payment system that supports multiple payment methods and must be easy to extend as new providers are added.

**Requirements:**

1. **`PaymentMethod`** interface:
   - `boolean validate()` — check if the payment details are valid.
   - `PaymentResult process(BigDecimal amount)` — execute the payment.
   - `String getPaymentType()` — return a human-readable type name.

2. **Implementations:** `CreditCardPayment`, `DebitCardPayment`, `PayPalPayment`, `BankTransferPayment`. Each has its own validation rules and processing logic.

3. **`PaymentProcessor`** class:
   - `PaymentResult processPayment(PaymentMethod method, BigDecimal amount)`:
     1. Validate the payment method.
     2. Process the payment.
     3. Generate a receipt.
   - Should depend on the `PaymentMethod` interface (DIP).
   - Adding a new payment type should require zero changes to `PaymentProcessor` (OCP).

4. **Apply all five SOLID principles.** Comment in the code where each principle is demonstrated.

**Bonus:**
- Add `CryptoPayment` without modifying any existing class — prove OCP works.
- Add a `PaymentValidator` interface to separate validation concerns (SRP + ISP).

**Starter files:**
- `exercises/src/main/java/exercises/payment/PaymentMethod.java`
- `exercises/src/main/java/exercises/payment/PaymentProcessor.java`

---

### Exercise 3 — Library Catalog System

**Business Context:** A public library needs a catalog system that handles different media types, tracks borrowing, and notifies patrons about overdue items.

**Requirements:**

1. **`LibraryItem`** (abstract sealed class):
   - Fields: `title` (String), `year` (int), `id` (String).
   - Abstract method: `String getCategory()`.
   - Permitted subclasses: `Book`, `DVD`, `Magazine`.

2. **`Borrowable`** interface:
   - `void borrowItem(String patronId)` — marks the item as borrowed.
   - `void returnItem()` — marks the item as returned.
   - `boolean isAvailable()`.
   - Not all items are borrowable (e.g., reference-only magazines).

3. **`Searchable`** interface with a `default` method:
   - `boolean matchesKeyword(String keyword)` — default implementation checks if `title` contains keyword (case-insensitive).

4. **`Book`**, **`DVD`** implement both `Borrowable` and `Searchable`. **`Magazine`** implements only `Searchable`.

5. **`Patron`** class: has a name, id, and a list of currently borrowed items. Method `borrowItem(Borrowable item)`.

6. **Demonstrate sealed classes:** Try extending `LibraryItem` with an unauthorized class — show the compile error.

**Bonus:**
- Implement a simple Observer pattern: `OverdueNotifier` that notifies patrons when an item is overdue.
- Use `record` for `BorrowingRecord(String patronId, LocalDate borrowDate, LocalDate dueDate)`.

**Starter file:**
- `exercises/src/main/java/exercises/library/LibraryItem.java`

---

## Self-Assessment Checklist

Before moving to Phase 03, confirm:

- [ ] I can explain the difference between an abstract class and an interface and give a concrete example of when to use each.
- [ ] I can implement `equals()`, `hashCode()`, and `toString()` correctly without IDE assistance and explain the contract.
- [ ] I can describe all five SOLID principles, give a Java example for each, and identify violations in code.
- [ ] I understand why "favor composition over inheritance" is important and can refactor an inheritance hierarchy to use composition.
- [ ] I can explain upcasting, downcasting, and `instanceof` pattern matching.
- [ ] I know the rules for method overriding: signature, access, exceptions, `@Override`.
- [ ] I can create a Java `record` and explain what it generates automatically.
- [ ] I can define a sealed class hierarchy and explain the `permits` clause.
- [ ] I can create an enum with fields, constructors, methods, and per-constant abstract method implementations.
- [ ] I have completed all three exercises, applying SOLID principles in at least one of them.
