# Phase 01 — Spring Core & IoC

**Duration:** ~2 weeks (17 hours of study + exercises)

---

## Learning Objectives

By the end of this phase you will be able to:

- Explain what Inversion of Control (IoC) is and why it leads to loosely coupled, testable code.
- Configure a Spring application context using both annotation-based and Java-based configuration.
- Apply constructor injection, qualifier annotations, and scope management to wire beans correctly.
- Describe the full bean lifecycle from instantiation through destruction.
- Use profiles and externalized properties to build environment-aware applications.
- Implement cross-cutting concerns (logging, timing, security) with Aspect-Oriented Programming.

---

## Topics

### 1. Spring Core Concepts — 4 h

| Sub-topic | Hours | Key ideas |
|-----------|-------|-----------|
| What is IoC and why it matters | 1 | Hollywood Principle ("don't call us, we'll call you"), container-managed objects, loose coupling vs `new` operator |
| `ApplicationContext` vs `BeanFactory` | 0.5 | Eager vs lazy init, event publishing, resource loading, why `ApplicationContext` is the default choice |
| Bean definition: `@Component`, `@Service`, `@Repository`, `@Controller` stereotypes | 1 | Semantic meaning of each stereotype, exception translation in `@Repository`, detection via classpath scanning |
| Component scanning: `@ComponentScan`, base packages, filters | 0.5 | `includeFilters` / `excludeFilters`, regex and annotation type filters, base-package strategies for multi-module projects |
| Configuration: `@Configuration`, `@Bean`, Java-based vs annotation-based | 1 | Full `@Configuration` vs lite mode, inter-bean references, method-level `@Bean` with custom init/destroy |

### 2. Dependency Injection — 4 h

| Sub-topic | Hours | Key ideas |
|-----------|-------|-----------|
| Constructor injection (preferred), setter injection, field injection (`@Autowired`) | 1.5 | Why constructor injection is preferred (immutability, required deps, testability), when setter injection is acceptable |
| `@Qualifier`, `@Primary`, custom qualifiers | 1 | Disambiguating multiple candidates, creating `@MySpecialQualifier` meta-annotations |
| Circular dependencies: how they happen, how to fix | 0.5 | Setter-injection workaround, `@Lazy`, redesigning to break cycles |
| Optional dependencies, lazy injection | 0.5 | `Optional<T>`, `ObjectProvider<T>`, `@Lazy` on injection point |
| Injection of collections (`List<>`, `Map<>`) | 0.5 | Ordered injection with `@Order`, `Map<String, T>` keyed by bean name |

### 3. Bean Lifecycle & Scope — 3 h

| Sub-topic | Hours | Key ideas |
|-----------|-------|-----------|
| Scopes: singleton, prototype, request, session | 1 | Default singleton, prototype gotcha (no destruction callback), web scopes with proxy |
| Lifecycle callbacks: `@PostConstruct`, `@PreDestroy`, `InitializingBean`, `DisposableBean` | 1 | Ordering of callbacks, JSR-250 vs Spring interfaces, `@Bean(initMethod, destroyMethod)` |
| `BeanPostProcessor`, `BeanFactoryPostProcessor` | 1 | When each runs in the lifecycle, `PropertySourcesPlaceholderConfigurer` as real-world BFPP, writing a custom BPP |

### 4. Profiles & Properties — 2 h

| Sub-topic | Hours | Key ideas |
|-----------|-------|-----------|
| `@Profile` for environment-specific beans | 0.5 | Activating profiles: `-Dspring.profiles.active`, programmatic activation, default profile |
| `@Value`, `PropertySource`, externalized configuration | 1 | Placeholder resolution, default values `${key:default}`, loading `.properties` and `.yml` |
| SpEL (Spring Expression Language) basics | 0.5 | `#{…}` syntax, accessing beans, calling methods, conditionals, system properties |

### 5. AOP (Aspect-Oriented Programming) — 4 h

| Sub-topic | Hours | Key ideas |
|-----------|-------|-----------|
| Core concepts: aspect, advice, pointcut, join point, weaving | 1 | Proxy-based AOP in Spring, JDK dynamic proxy vs CGLIB, limitations (final classes, self-invocation) |
| Advice types: `@Before`, `@After`, `@AfterReturning`, `@AfterThrowing`, `@Around` | 1.5 | `ProceedingJoinPoint`, modifying return values, swallowing exceptions (don't!) |
| Pointcut expressions: `execution`, `within`, `@annotation` | 1 | Wildcards `*` and `..`, combining pointcuts with `&&`, `||`, `!`, reusable `@Pointcut` methods |
| Real-world uses: logging, timing, security, caching | 0.5 | Spring's own use of AOP (`@Transactional`, `@Cacheable`, `@Async`) |

---

## Exercises

### Exercise 1 — Multi-Environment Config System

**Difficulty:** ★★☆ Intermediate  
**Estimated time:** 3–4 hours

Build a **pure Spring** application (no Spring Boot) with three profiles: `dev`, `staging`, `prod`.

**Requirements:**

1. Each profile provides a different simulated `DataSource` configuration (URL, username, pool size).
2. Each profile defines a different logging level (`DEBUG`, `INFO`, `WARN`).
3. Introduce at least two feature flags (e.g., `cacheEnabled`, `emailNotifications`) whose values change per profile.
4. Use `@Profile`, `@Value`, `@PropertySource` to load profile-specific `.properties` files.
5. Demonstrate **bean override** per profile (e.g., a `CacheManager` stub in `dev`, real impl in `prod`).
6. Test by switching the active profile via system property or environment variable and observing different outputs.

**Bonus:** Implement a custom `BeanPostProcessor` that logs every bean being created (bean name, class, scope).

**Hints:**
- Create separate property files: `application-dev.properties`, `application-staging.properties`, `application-prod.properties`.
- Bootstrap via `AnnotationConfigApplicationContext`.

---

### Exercise 2 — Annotation-Driven Audit Logger

**Difficulty:** ★★★ Advanced  
**Estimated time:** 3–4 hours

Create a custom `@Auditable` annotation and an AOP aspect that intercepts annotated methods.

**Requirements:**

1. `@Auditable` accepts an `AuditLevel` enum parameter: `BASIC`, `DETAILED`, `FULL`.
2. The `AuditAspect` uses `@Around` advice.
3. Captured data per level:
   - `BASIC` — method name, execution time
   - `DETAILED` — method name, arguments (stringified), execution time, return value
   - `FULL` — all of `DETAILED` + caller class/method (via stack trace), timestamp, thread name
4. Store audit entries in an in-memory `List<AuditEntry>` accessible via an `AuditService`.
5. Support both successful executions and failures (log the exception in `FULL` mode).

**Bonus:** Make the audit storage pluggable — define an `AuditStore` interface with `InMemoryAuditStore` and `FileAuditStore` implementations, wired via DI.

---

### Exercise 3 — Method Performance Profiler

**Difficulty:** ★★★ Advanced  
**Estimated time:** 4–5 hours

Build a profiling framework using custom annotations, AOP, and bean lifecycle hooks.

**Requirements:**

1. `@Profiled` annotation with a `threshold` attribute (default 100 ms).
2. AOP `@Around` advice measures execution time of `@Profiled` methods.
3. If execution time exceeds `threshold`, log a warning with method details and elapsed time.
4. Collect statistics per method: call count, total time, average time, max time, p95 (95th percentile).
5. Expose statistics via a `ProfilerService` with methods like `getStatistics()`, `getStatistics(String methodName)`.
6. Use a `BeanPostProcessor` to discover all `@Profiled` methods at startup and register them in the profiler.

**Bonus:** Create a simple console "dashboard" — a scheduled task (or daemon thread) that prints formatted statistics every N seconds.

---

## Self-Assessment Checklist

- [ ] I can bootstrap a Spring `ApplicationContext` without Spring Boot.
- [ ] I can explain why constructor injection is preferred over field injection.
- [ ] I know the difference between `@Component`, `@Service`, `@Repository`, and `@Controller`.
- [ ] I can resolve ambiguous beans using `@Qualifier` and `@Primary`.
- [ ] I understand singleton vs prototype scope and the prototype-in-singleton problem.
- [ ] I can describe the bean lifecycle: instantiation → populate properties → `BeanPostProcessor.postProcessBeforeInitialization` → init → `postProcessAfterInitialization` → ready → destroy.
- [ ] I can activate profiles and load environment-specific properties.
- [ ] I can write a pointcut expression to match specific methods.
- [ ] I can implement an `@Around` advice that measures execution time.
- [ ] I know that Spring AOP is proxy-based and cannot intercept self-invocation.

---

## References

- [Spring Framework Reference — Core Container](https://docs.spring.io/spring-framework/reference/core.html)
- *Spring in Action* (Craig Walls) — Chapters 1–3
- [Baeldung — Spring Core Tutorials](https://www.baeldung.com/spring-tutorial)
- [Baeldung — Introduction to Spring AOP](https://www.baeldung.com/spring-aop)
- [Spring Framework API Javadoc](https://docs.spring.io/spring-framework/docs/current/javadoc-api/)
