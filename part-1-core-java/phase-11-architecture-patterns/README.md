# Phase 11 — Architecture Patterns

**Duration:** ~3–4 weeks (39 hours)

This is the **architect-level capstone** of Part 1: Core Java. You will learn how to structure non-trivial applications so they remain testable, maintainable, and adaptable to change.

---

## Learning Objectives

By the end of this phase you will be able to:

1. Identify the layers in a **layered architecture** and explain when the pattern breaks down.
2. Design a system using **Hexagonal Architecture (Ports & Adapters)** with clear separation between domain logic and infrastructure.
3. Explain the **Dependency Rule** in Clean Architecture and map it to a concrete Java project.
4. Apply **Domain-Driven Design** at both the strategic level (bounded contexts, context mapping) and the tactical level (entities, value objects, aggregates, domain events).
5. Describe **CQRS** and evaluate when separating read and write models is justified.
6. Implement a simple **Event Sourcing** system — storing events, replaying state, and building projections.
7. Understand key **microservices patterns**: sagas, circuit breakers, API gateways, and database-per-service.
8. Design **REST APIs** that follow the Richardson Maturity Model with proper resource naming, versioning, pagination, and error responses.

---

## Topics

### 1. Layered Architecture (3 h)

| Concept | Details |
|---------|---------|
| Layers | Presentation → Business (Service) → Persistence (Repository) → Infrastructure |
| Dependency direction | Each layer depends only on the one below |
| Pros | Simple, well-understood, easy onboarding |
| Cons | Tight coupling to database schema, hard to test business logic in isolation, "smart service, dumb model" anti-pattern |
| When it breaks | Complex domain logic, multiple entry points (REST + CLI + messaging), need for independent deployability |

### 2. Hexagonal Architecture — Ports & Adapters (6 h)

| Concept | Details |
|---------|---------|
| Core domain | Pure business logic, no framework imports |
| Ports | Java interfaces that define how the outside world interacts with the domain |
| Inbound ports | Driven by the outside (e.g. `OrderService` called by a controller) |
| Outbound ports | Driven by the domain toward the outside (e.g. `OrderRepository`, `PaymentGateway`) |
| Adapters | Concrete implementations of ports (e.g. `JpaOrderRepository`, `StripePaymentAdapter`) |
| Dependency inversion | Domain defines interfaces; adapters implement them. Domain has zero dependency on infrastructure. |
| Testing advantage | The entire domain can be tested with in-memory adapters — no database, no HTTP. |

### 3. Clean Architecture (4 h)

| Ring | Content |
|------|---------|
| Entities | Enterprise-wide business rules, domain objects |
| Use Cases | Application-specific business rules (orchestration) |
| Interface Adapters | Controllers, presenters, gateways |
| Frameworks & Drivers | Spring, Hibernate, Jackson — outermost ring |
| **Dependency Rule** | Source code dependencies point **inward** only. Nothing in an inner ring knows about an outer ring. |
| Comparison with hexagonal | Same core idea; Clean Architecture adds explicit use-case layer and prescribes a concentric layout. |

### 4. Domain-Driven Design (10 h)

#### 4.1 Strategic DDD (3 h)

| Concept | Details |
|---------|---------|
| Bounded Context | A linguistic and model boundary. "Customer" means different things in Sales vs Billing. |
| Context Mapping | Relationships between contexts: Shared Kernel, Customer-Supplier, Anti-Corruption Layer, Conformist, Open Host Service, Published Language |
| Ubiquitous Language | Domain experts and developers share the same vocabulary within a context |

#### 4.2 Tactical DDD (3 h)

| Building block | Characteristics |
|----------------|-----------------|
| Entity | Has identity, mutable state, lifecycle |
| Value Object | Defined by attributes, immutable, no identity (use Java `record`) |
| Aggregate | Cluster of entities/value objects with a single root entity |
| Aggregate Root | The only entry point to modify the aggregate; enforces invariants |

#### 4.3 Repositories (1 h)

| Style | Description |
|-------|-------------|
| Collection-oriented | Mimics an in-memory collection (`add`, `remove`, `findById`) |
| Persistence-oriented | Explicit `save` method; used with JPA-style tracking |

#### 4.4 Domain Events (2 h)

| Concept | Details |
|---------|---------|
| Raising events | Aggregate records events during a business operation |
| Handling events | Handlers react asynchronously or synchronously |
| Eventual consistency | Events propagate state changes across bounded contexts |

#### 4.5 Domain Services vs Application Services (1 h)

| Service type | Responsibility |
|--------------|----------------|
| Domain Service | Business logic that doesn't belong to a single entity (e.g. pricing, transfer) |
| Application Service | Orchestration, transaction management, security — no business rules |

### 5. CQRS — Command Query Responsibility Segregation (3 h)

| Aspect | Details |
|--------|---------|
| Core idea | Separate the write model (commands) from the read model (queries) |
| When to use | Complex queries that don't map to the write model, different scaling needs |
| Complexity trade-offs | Two models to maintain, eventual consistency between them |
| Simple CQRS | Same database, different DTOs for read vs write |
| Full CQRS | Separate data stores, event-driven synchronization |

### 6. Event Sourcing (4 h)

| Concept | Details |
|---------|---------|
| Event store | Append-only log of domain events |
| State rebuild | Replay all events for an aggregate to derive current state |
| Projections | Materialized views built by processing the event stream |
| Snapshots | Periodic state capture to avoid replaying the entire history |
| Pros | Full audit trail, temporal queries, easy debugging |
| Cons | Complexity, eventual consistency, schema evolution challenges |

### 7. Microservices Patterns (6 h)

| Pattern | Hours | Key points |
|---------|-------|------------|
| Saga | 2 | Choreography (events) vs Orchestration (central coordinator); compensating transactions |
| Circuit Breaker | 1 | Closed → Open → Half-Open states; fallback responses; Resilience4j |
| API Gateway | 1 | Single entry point, routing, auth, rate limiting, protocol translation |
| Database per Service | 1 | Autonomy vs joins; eventual consistency; shared database as anti-pattern |
| Event-Driven Architecture | 1 | Event notification, event-carried state transfer, event sourcing in a distributed context |

### 8. API Design (3 h)

| Topic | Details |
|-------|---------|
| Richardson Maturity Model | Level 0 (POX) → Level 1 (Resources) → Level 2 (HTTP Verbs) → Level 3 (HATEOAS) |
| Resource naming | Nouns, plurals, hierarchy (`/orders/{id}/items`) |
| Versioning | URI path (`/v1/`), header, query param — trade-offs |
| Pagination | Offset-based vs cursor-based; `Link` headers |
| Error responses | RFC 7807 Problem Details; consistent structure |

---

## References

- *Clean Architecture* — Robert C. Martin. The definitive guide to the dependency rule.
- *Domain-Driven Design: Tackling Complexity in the Heart of Software* — Eric Evans. The original DDD book.
- *Implementing Domain-Driven Design* — Vaughn Vernon. Practical, code-heavy companion to Evans.
- *Building Microservices* — Sam Newman (2nd ed.). Pragmatic patterns for service decomposition.
- *Designing Data-Intensive Applications* — Martin Kleppmann. Essential reading on distributed systems and data architecture.
- [Hexagonal Architecture — Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Microsoft — CQRS Pattern](https://learn.microsoft.com/en-us/azure/architecture/patterns/cqrs)
- [Martin Fowler — Event Sourcing](https://martinfowler.com/eaaDev/EventSourcing.html)

---

## Exercises

### Exercise 1 — Hexagonal Order Management

**Goal:** Build an order management system using hexagonal architecture.

**Domain model:**

- `Order` — aggregate root with status lifecycle.
- `OrderLine` — value object (product id, quantity, unit price).
- `Product`, `Customer` — referenced entities.

**Ports:**

| Port | Direction | Methods |
|------|-----------|---------|
| `OrderService` | Inbound | `createOrder`, `confirmOrder`, `shipOrder`, `cancelOrder` |
| `OrderRepository` | Outbound | `save`, `findById`, `findByCustomer`, `findByStatus` |

**Adapters:**

- `InMemoryOrderRepository` — simple `HashMap`-based implementation.
- `ConsoleOrderController` — reads commands from `System.in`.

**Order lifecycle:**

```
CREATED ──► CONFIRMED ──► SHIPPED ──► DELIVERED
   │                         │
   └──────► CANCELLED ◄─────┘  (only before SHIPPED)
```

**Business rules:**

- Minimum order value: $10.
- Stock must be validated before confirming.
- Cancellation is only allowed before shipping.
- Each state transition is validated (e.g. cannot ship a CANCELLED order).

**Testing:** Test the domain and application layer without any adapter. Use in-memory implementations for outbound ports.

**Bonus:** Define a REST adapter interface (just the Java interface with path annotations — actual Spring implementation will come in Part 2).

---

### Exercise 2 — DDD E-Commerce

**Goal:** Model an e-commerce system using DDD strategic and tactical patterns.

**Bounded contexts:**

| Context | Key aggregates | Notes |
|---------|----------------|-------|
| Catalog | `Product`, `Category` | Read-heavy, rarely changes |
| Sales | `Order`, `Cart`, `Customer` | Write-heavy, complex rules |
| Inventory | `Stock`, `Warehouse` | Event-driven updates |

**Key design decisions:**

- `Customer` in Sales ≠ `Customer` in a hypothetical CRM context. Each context owns its own model.
- Anti-corruption layers translate between contexts.
- Aggregate roots enforce invariants (e.g. an `Order` cannot have zero lines).
- Domain events: `OrderPlaced` → triggers `StockReduced` in Inventory.

**Bonus:** Implement a simple in-memory event bus for cross-context communication.

---

### Exercise 3 — Event-Sourced Bank Ledger

**Goal:** Store state as a sequence of events instead of a mutable row.

**Events:**

| Event | Fields |
|-------|--------|
| `AccountOpened` | accountId, ownerName, initialBalance |
| `MoneyDeposited` | accountId, amount, description |
| `MoneyWithdrawn` | accountId, amount, description |
| `TransferInitiated` | sourceAccountId, targetAccountId, amount |
| `TransferCompleted` | transferId |

**Rebuild state:** Given a list of events for an account, replay them in order to derive the current balance.

**Projections:**

| Projection | Purpose |
|------------|---------|
| Current balance | Sum of deposits minus withdrawals |
| Transaction history | Chronological list of all operations |
| Monthly statement | Grouped by month, opening/closing balance |

**Snapshots:** After every N events, persist a snapshot of the account state. When rebuilding, start from the latest snapshot instead of event zero.

**Bonus:** Implement compensating events for failed transfers (e.g. `TransferFailed` reverses the withdrawal).

---

## Self-Assessment Checklist

- [ ] I can draw the layers of a layered architecture and explain the dependency direction.
- [ ] I can identify when layered architecture becomes a liability.
- [ ] I can design a system with inbound and outbound ports, and swap adapters without touching the domain.
- [ ] I understand the Dependency Rule and can verify it in my code.
- [ ] I can define a bounded context and explain why the same word means different things in different contexts.
- [ ] I can model an aggregate with an aggregate root that protects invariants.
- [ ] I know the difference between an entity, a value object, and a domain service.
- [ ] I can raise and handle domain events within a single process.
- [ ] I can explain CQRS and identify a scenario where it adds value.
- [ ] I can implement a simple event store and rebuild state from events.
- [ ] I can describe the saga pattern and choose between choreography and orchestration.
- [ ] I can explain the circuit breaker pattern and its three states.
- [ ] I can design a REST API at Richardson Maturity Level 2 with proper resource naming.
- [ ] I have implemented at least one of the three exercises end-to-end.

---

*This concludes Part 1: Core Java. Proceed to Part 2 for Spring Boot, databases, and production deployment.*
