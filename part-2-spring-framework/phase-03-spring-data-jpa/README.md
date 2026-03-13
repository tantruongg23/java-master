# Phase 03 — Spring Data & JPA

**Duration:** ~3 weeks (27 hours of study + exercises)

---

## Learning Objectives

By the end of this phase you will be able to:

- Map Java domain objects to relational tables using JPA annotations.
- Model relationships (`@OneToMany`, `@ManyToMany`, `@OneToOne`, `@Embedded`) with correct cascade and fetch strategies.
- Leverage Spring Data repositories for CRUD, pagination, sorting, and derived queries.
- Write JPQL, native SQL, and dynamic Specification-based queries.
- Configure transactions with proper propagation, isolation, and error handling.
- Detect and fix the N+1 problem, apply entity graphs, batch inserts, and second-level caching.
- Manage schema evolution with Flyway or Liquibase.

---

## Topics

### 1. JPA Fundamentals — 6 h

| Sub-topic | Hours | Key ideas |
|-----------|-------|-----------|
| ORM concepts, JPA vs Hibernate | 1 | JPA as specification, Hibernate as implementation, EclipseLink alternative, when ORM is (and isn't) the right choice |
| `@Entity`, `@Table`, `@Id`, `@GeneratedValue` strategies (AUTO, IDENTITY, SEQUENCE, TABLE) | 1 | `SEQUENCE` as the recommended strategy for PostgreSQL, `IDENTITY` for MySQL, table generator for portability |
| Column mapping: `@Column`, `@Enumerated`, `@Temporal`, `@Lob` | 1 | `EnumType.STRING` vs `ORDINAL`, `@Column(nullable, unique, length, columnDefinition)`, large objects |
| Entity lifecycle: transient, managed, detached, removed | 1 | State transitions, `merge()` vs `persist()`, dirty checking, `flush()` vs `commit()` |
| Persistence context, `EntityManager`, first-level cache | 1 | Scope of persistence context (transaction-scoped vs extended), identity guarantee, `clear()` for batch processing |
| JPQL and Criteria API basics | 1 | SELECT, JOIN, WHERE, aggregate functions, named queries, Criteria API type safety |

### 2. Relationships — 6 h

| Sub-topic | Hours | Key ideas |
|-----------|-------|-----------|
| `@OneToMany` / `@ManyToOne`: the most common, `mappedBy`, cascade, `orphanRemoval` | 2 | Owning side (FK holder), `CascadeType.ALL` risks, `orphanRemoval=true` behavior, bidirectional helper methods |
| `@ManyToMany`: join table, avoiding pitfalls | 1 | Extra columns on join table → promote to entity, `Set` vs `List` for equals/hashCode, `@JoinTable` configuration |
| `@OneToOne`: shared primary key vs foreign key | 0.5 | `@MapsId` for shared PK, lazy loading limitations with `@OneToOne` |
| Lazy vs Eager fetching: `LazyInitializationException`, Open-in-View anti-pattern | 1.5 | `FetchType.LAZY` as default, `spring.jpa.open-in-view=false`, DTO projection as the solution |
| `@Embeddable` / `@Embedded` for value objects | 1 | Modeling `Address`, `Money`, `DateRange` as value objects, `@AttributeOverride` |

### 3. Spring Data Repositories — 6 h

| Sub-topic | Hours | Key ideas |
|-----------|-------|-----------|
| `JpaRepository`, `CrudRepository`, `PagingAndSortingRepository` | 1 | Repository hierarchy, `save()` semantics (merge vs persist), `saveAll()` batch behavior |
| Query derivation: `findByNameContaining`, `findByStatusOrderByDate` | 1 | Keyword reference, `And`, `Or`, `Between`, `IsNull`, `In`, return types (`Optional`, `List`, `Stream`) |
| `@Query` with JPQL and native SQL | 1 | Positional vs named parameters, `@Modifying` for updates/deletes, `nativeQuery=true` |
| Specifications for dynamic queries | 1 | `Specification<T>`, combining with `and()`, `or()`, type-safe dynamic filters from request parameters |
| Projections: interface-based, class-based, dynamic | 1 | Open vs closed projections, `@Value` in interface projections, DTO constructor expressions |
| Auditing: `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy` | 1 | `@EnableJpaAuditing`, `AuditorAware<T>` for the current user, `@EntityListeners(AuditingEntityListener.class)` |

### 4. Transactions — 4 h

| Sub-topic | Hours | Key ideas |
|-----------|-------|-----------|
| `@Transactional`: propagation levels (REQUIRED, REQUIRES_NEW, NESTED, etc.) | 1.5 | Default REQUIRED, REQUIRES_NEW for independent transactions (e.g., audit log), SUPPORTS for read operations |
| Isolation levels and their trade-offs | 1 | READ_COMMITTED (default), REPEATABLE_READ, SERIALIZABLE, phantom reads, write skew |
| Read-only transactions, transaction boundaries | 0.5 | `@Transactional(readOnly=true)` optimizations, Hibernate flush mode, transaction at service layer not DAO |
| Programmatic transactions (`TransactionTemplate`) | 0.5 | When annotations aren't enough, `TransactionTemplate.execute()`, `TransactionSynchronization` |
| Common pitfalls: self-invocation, checked exceptions | 0.5 | Proxy-based AOP limitation, `rollbackFor` for checked exceptions, `@Transactional` on interface vs class |

### 5. Performance — 4 h

| Sub-topic | Hours | Key ideas |
|-----------|-------|-----------|
| N+1 problem: detection, `@EntityGraph`, JOIN FETCH | 1.5 | How to detect (SQL logging, Hibernate statistics), `@EntityGraph(attributePaths)`, JOIN FETCH in JPQL, `@BatchSize` |
| Batch inserts: `hibernate.jdbc.batch_size` | 0.5 | `spring.jpa.properties.hibernate.jdbc.batch_size=50`, `@GeneratedValue(strategy=SEQUENCE)` requirement, `clearAutomatically` |
| Second-level cache: Hibernate cache, `@Cacheable` with Spring | 1 | Cache regions, `@Cache(usage=READ_WRITE)`, EHCache/Caffeine integration, cache invalidation strategies |
| Database migrations: Flyway vs Liquibase | 1 | Versioned migrations (V1__init.sql), repeatable migrations, Flyway + Spring Boot auto-config, `flyway.baseline-on-migrate` |

### 6. Pagination & Sorting — 1 h

| Sub-topic | Hours | Key ideas |
|-----------|-------|-----------|
| `Pageable`, `Page<T>`, `Slice<T>`, `Sort` | 1 | `PageRequest.of()`, sort by multiple fields, keyset pagination for large datasets, `@PageableDefault` |

---

## Exercises

### Exercise 1 — Multi-Tenant SaaS Data Layer

**Difficulty:** ★★★ Advanced  
**Estimated time:** 6–8 hours

Design a multi-tenant data layer with automatic tenant filtering.

**Requirements:**

1. Entities: `Tenant` (id, name, plan), `User` (id, email, role, tenant), `Project` (id, name, tenant), `Task` (id, title, status, priority, project).
2. Discriminator-based multi-tenancy: every entity except `Tenant` has a `tenant_id` column.
3. Custom repository base class that automatically filters by the current tenant (use a `TenantContext` thread-local).
4. Spring Data Specifications for dynamic queries: filter tasks by status, priority, date range — all composable.
5. JPA Auditing: `@CreatedDate`, `@LastModifiedDate` on all entities.
6. Pagination and multi-field sorting on Task list endpoints.
7. Database: H2 for dev, Flyway for schema migrations.

**Bonus:** Implement row-level security via a Hibernate `@Filter` / `@FilterDef` that the repository activates automatically.

---

### Exercise 2 — Reporting Module

**Difficulty:** ★★★ Advanced  
**Estimated time:** 5–6 hours

Build a reporting module with complex queries and performance analysis.

**Requirements:**

1. Monthly summary report: total tasks created, completed, avg completion time — grouped by project and month.
2. Use JPQL with aggregate functions (SUM, AVG, COUNT, GROUP BY).
3. Native SQL query for a complex cross-table report (e.g., user productivity ranking).
4. Interface-based projections for read-only report DTOs (no entity overhead).
5. Detect and fix N+1 queries using `@EntityGraph` and JOIN FETCH.
6. Compare approaches: derived query vs `@Query` vs Specifications vs native query — document trade-offs.

**Bonus:** Build a query builder DSL using `Specification<T>` that reads filter criteria from a `Map<String, Object>` (useful for REST query parameters).

---

### Exercise 3 — Inventory System with Optimistic Locking

**Difficulty:** ★★★ Advanced  
**Estimated time:** 5–6 hours

Build an inventory system that handles concurrent stock updates safely.

**Requirements:**

1. `Product` entity with `@Version` for optimistic locking. Fields: id, name, sku, price, stockQuantity, version.
2. Stock operations:
   - `addStock(Long productId, int quantity)` — increases stock
   - `reserveStock(Long productId, int quantity)` — decreases stock (throws if insufficient)
   - `releaseStock(Long productId, int quantity)` — returns reserved stock
3. Handle `OptimisticLockException` with automatic retry (up to 3 attempts).
4. Transaction propagation demo: an outer `@Transactional` method calls an inner `@Transactional(propagation=REQUIRES_NEW)` method that logs the operation — even if the outer transaction rolls back, the log entry persists.
5. Publish a Spring application event (`StockChangedEvent`) on every stock change.

**Bonus:** Implement the same scenario with pessimistic locking (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) and compare behavior under contention.

---

## Self-Assessment Checklist

- [ ] I can map entities with `@Entity`, `@Table`, `@Id`, and `@GeneratedValue`.
- [ ] I understand the JPA entity lifecycle: transient → managed → detached → removed.
- [ ] I can model `@OneToMany` / `@ManyToOne` with the correct owning side.
- [ ] I know why `FetchType.LAZY` is the default and can explain `LazyInitializationException`.
- [ ] I can write derived queries, `@Query` JPQL, and native SQL in Spring Data repositories.
- [ ] I can build dynamic queries using `Specification<T>`.
- [ ] I understand `@Transactional` propagation levels and can explain when to use `REQUIRES_NEW`.
- [ ] I know why `@Transactional` doesn't work on self-invocation and how to fix it.
- [ ] I can detect the N+1 problem and fix it with `@EntityGraph` or JOIN FETCH.
- [ ] I can set up Flyway migrations with Spring Boot.
- [ ] I can implement optimistic locking with `@Version` and handle `OptimisticLockException`.
- [ ] I can configure JPA auditing with `@CreatedDate` and `@LastModifiedDate`.

---

## References

- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/reference/jpa.html)
- [Hibernate ORM Documentation](https://hibernate.org/orm/documentation/)
- *Java Persistence with Hibernate* (Christian Bauer, Gavin King) — the definitive guide
- [Vlad Mihalcea's Blog — High-Performance Java Persistence](https://vladmihalcea.com/)
- [Baeldung — Spring Data JPA Tutorials](https://www.baeldung.com/spring-data-jpa-tutorial)
- [Flyway Documentation](https://documentation.red-gate.com/flyway)
- [Baeldung — JPA Entity Lifecycle](https://www.baeldung.com/jpa-entity-lifecycle-events)
