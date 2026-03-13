# Phase 07 — Design Patterns

**Duration:** ~3–4 weeks · **Total effort:** ~26 hours

---

## Learning Objectives

By the end of this phase you will be able to:

1. Identify common design problems and select the appropriate GoF pattern to solve them.
2. Implement all major **Creational**, **Structural**, and **Behavioral** patterns in idiomatic Java.
3. Combine multiple patterns to build flexible, maintainable architectures.
4. Recognise **anti-patterns** and know when *not* to apply a pattern.
5. Refactor existing code toward well-known patterns without over-engineering.

---

## Topics

### Creational Patterns (6 h)

| Topic | Hours | Key concepts |
|-------|:-----:|--------------|
| **Singleton** | 1.5 | Lazy initialisation, eager initialisation, enum singleton, double-checked locking, why enum is the preferred approach in modern Java, thread-safety guarantees |
| **Factory Method & Abstract Factory** | 1.5 | Factory vs constructor, when to use each, parameterised factories, open/closed principle, `java.util.Calendar.getInstance()` as real-world example |
| **Builder** | 1.5 | Telescoping constructor problem, step-by-step construction, fluent API, immutable objects via Builder, Lombok `@Builder`, records + builder hybrid |
| **Prototype** | 1.5 | `Cloneable` interface, deep vs shallow copy pitfalls, copy constructors as safer alternative, serialization-based cloning |

### Structural Patterns (8 h)

| Topic | Hours | Key concepts |
|-------|:-----:|--------------|
| **Adapter** | 1.5 | Class adapter (inheritance) vs object adapter (composition), legacy system integration, `InputStreamReader` as adapter |
| **Decorator** | 2 | `java.io` stream hierarchy as canonical example, adding behaviour dynamically, decorator vs inheritance, stacking decorators |
| **Proxy** | 2 | Protection proxy, virtual (lazy-loading) proxy, remote proxy, JDK dynamic proxies (`java.lang.reflect.Proxy`, `InvocationHandler`), CGLIB proxies |
| **Facade** | 1 | Simplifying complex subsystems, providing a unified interface, `javax.faces.context.FacesContext` |
| **Composite** | 1.5 | Tree structures, file-system example, uniform treatment of leaf and composite nodes, recursive traversal |

### Behavioral Patterns (10 h)

| Topic | Hours | Key concepts |
|-------|:-----:|--------------|
| **Observer** | 2 | Event systems, `PropertyChangeListener`, custom implementation with generics, push vs pull models, memory leaks with listeners |
| **Strategy** | 1.5 | Encapsulating algorithm families, replacing long conditional chains, `Comparator` as Strategy, lambdas as lightweight strategies |
| **Command** | 1.5 | Undo/redo support, macro commands, task queues, decoupling invoker from receiver |
| **Template Method** | 1.5 | Algorithm skeleton in base class, abstract "hook" methods, `AbstractList` as example, Hollywood Principle |
| **Chain of Responsibility** | 1.5 | Filter chains, validation pipelines, servlet filters, logging handlers, short-circuit vs full-chain processing |
| **State** | 1 | Finite state machines, eliminating complex switch/if-else, state transitions, `java.util.Iterator` states |
| **Mediator** | 1 | Reducing direct coupling between components, chat-room analogy, event-bus as mediator variant |

### Anti-patterns & When NOT to Use Patterns (2 h)

- **Singleton abuse** — global mutable state, hidden dependencies, testing difficulties.
- **Pattern mania** — applying patterns where simple code suffices; YAGNI.
- **Speculative generality** — building abstract factory hierarchies for one implementation.
- **Golden hammer** — forcing every problem into Observer/Strategy.
- **God object** hiding behind a Facade.
- Guidelines: favour composition over inheritance, keep pattern count proportional to actual complexity.

---

## References

| Resource | Notes |
|----------|-------|
| *Head First Design Patterns*, 2nd Ed. (Freeman & Robson) | Accessible, visual explanations with Java examples |
| *Design Patterns: Elements of Reusable Object-Oriented Software* (Gamma, Helm, Johnson, Vlissides — "GoF") | The original catalogue; dense but essential |
| [Refactoring.Guru — Design Patterns](https://refactoring.guru/design-patterns) | Interactive diagrams, code in multiple languages |
| *Effective Java*, 3rd Ed. (Bloch) | Items 1 (static factories), 2 (Builder), 3 (Singleton via enum), 17 (immutability), 18 (composition over inheritance) |

---

## Exercises

### Exercise 1 — Notification System (Observer + Strategy + Builder)

**Patterns:** Observer, Strategy, Builder, Decorator

Build a `NotificationService` that sends alerts via multiple channels.

- **Strategy** — `DeliveryChannel` implementations: `EmailChannel`, `SmsChannel`, `PushChannel`, `SlackChannel`.
- **Observer** — Users subscribe to event types (e.g. `"order.shipped"`, `"payment.failed"`). When an event fires, all subscribers are notified through their preferred channel.
- **Builder** — `Notification.builder().recipient("alice").message("Your order shipped").type("order.shipped").urgency(HIGH).channel(EMAIL).build()`.
- **Decorator** — `NotificationDecorator` subclasses add urgency prefix, HTML formatting, open-tracking pixel.
- **Bonus:** deliver notifications asynchronously using `CompletableFuture`.

### Exercise 2 — Document Converter Pipeline (Chain of Responsibility + Template Method + Factory)

**Patterns:** Chain of Responsibility, Template Method, Factory Method

Build a conversion pipeline: `Document → validate → parse → transform → output`.

- **Chain of Responsibility** — each stage (`ValidationHandler`, `ParsingHandler`, `TransformHandler`, `OutputHandler`) is a link in the chain. A handler may short-circuit on error.
- **Template Method** — `DocumentConverter` defines `final convert()` calling abstract steps: `parseDocument()`, `transformContent()`, `writeOutput()`.
- **Factory** — `ConverterFactory.create(format)` returns the right converter: `MarkdownToHtmlConverter`, `CsvToJsonConverter`, `XmlToJsonConverter`.
- **Bonus:** add a `CachingDecorator` that caches output for repeated identical inputs.

### Exercise 3 — Plugin Architecture (Factory + Proxy + Command)

**Patterns:** Factory, Proxy, Command

Design a plugin system with lifecycle management.

- **Factory** — `PluginManager` loads and creates plugins by name from a registry.
- **Command** — each plugin operation is a `PluginCommand` with `execute()` and `undo()`.
- **Proxy** — `PluginProxy` wraps every plugin to add logging, permission checks, and execution timing.
- **Plugin interface** — lifecycle: `init()`, `execute(String command)`, `destroy()`.
- **Bonus:** implement a dependency graph so plugins can declare dependencies and load in topological order.

### Exercise 4 — E-commerce Discount Engine (Strategy + Decorator + Composite)

**Patterns:** Strategy, Decorator, Composite, (Bonus) Chain of Responsibility

Build a flexible pricing/discount engine.

- **Strategy** — `DiscountStrategy` with implementations: `PercentageDiscount`, `FixedDiscount`, `BuyNGetMFree`.
- **Decorator** — `SeasonalBonus`, `LoyaltyMultiplier`, `MinPurchaseGuard` wrap a base strategy to add/modify behaviour.
- **Composite** — `CombinedDiscount` stacks multiple strategies into one, applying them sequentially.
- **PricingEngine** — evaluates all applicable discounts for a cart and returns the final price.
- **Bonus:** add `DiscountRuleChain` (Chain of Responsibility) that selects which discounts apply based on cart contents, customer tier, and date.

---

## Self-Assessment Checklist

- [ ] I can draw a UML class diagram for each pattern from memory.
- [ ] I can explain the **intent**, **applicability**, and **consequences** of every pattern covered.
- [ ] I know the difference between class-based and object-based adapter.
- [ ] I can implement a JDK dynamic proxy with `InvocationHandler`.
- [ ] I understand why enum is the preferred singleton implementation.
- [ ] I can recognise at least three anti-patterns related to design pattern misuse.
- [ ] I have combined three or more patterns in a single exercise.
- [ ] I can refactor a block of conditional logic into the Strategy or State pattern.
- [ ] I can explain when a pattern adds unnecessary complexity and should be avoided.
- [ ] I can identify which patterns the Java standard library uses (e.g. `java.io` decorators, `Collections.unmodifiable*` proxy).
