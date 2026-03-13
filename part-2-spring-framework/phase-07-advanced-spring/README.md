# Phase 07 — Advanced Spring

**Duration:** ~2–3 weeks · **Total estimated hours:** 23h

## Learning Objectives

By the end of this phase you will be able to:

1. Design and run Spring Batch jobs with chunk-oriented processing, skip/retry policies, and partitioned execution.
2. Build reactive applications with Project Reactor (Mono/Flux) and Spring WebFlux.
3. Use R2DBC for non-blocking database access in reactive pipelines.
4. Model complex workflows with Spring State Machine (states, transitions, guards, actions).
5. Structure monolithic applications into well-defined modules with Spring Modulith.
6. Instrument applications with structured logging, Micrometer metrics, and OpenTelemetry tracing.
7. Tune application performance with connection pooling, caching, and asynchronous processing.

---

## Topics

### 1 · Spring Batch — 4 h

| Sub-topic | Key concepts |
|---|---|
| Core model | `Job`, `Step`, `JobInstance`, `JobExecution`, `StepExecution` |
| Chunk processing | `ItemReader` → `ItemProcessor` → `ItemWriter`, commit interval |
| Built-in readers/writers | `FlatFileItemReader`, `JdbcBatchItemWriter`, `JpaPagingItemReader` |
| Skip & retry | `skipLimit()`, `skip(Exception.class)`, `retryLimit()`, `retry()` |
| Job restart | `JobRepository`, restartable jobs, start-from-last-failure |
| Partitioning | `Partitioner`, `TaskExecutorPartitionHandler`, partitioned steps |
| Scheduling | `@Scheduled`, Quartz integration, `JobLauncher` |

### 2 · Reactive Programming — 6 h

| Sub-topic | Key concepts |
|---|---|
| Reactive Streams | `Publisher`, `Subscriber`, `Subscription`, backpressure |
| Project Reactor | `Mono<T>`, `Flux<T>`, operators (map, flatMap, filter, zip, merge) |
| Error handling | `onErrorResume()`, `onErrorReturn()`, `retry()`, `retryWhen()` |
| Spring WebFlux | `@RestController` with reactive returns, `RouterFunction`, `HandlerFunction` |
| Reactive data access | R2DBC, `ReactiveCrudRepository`, `DatabaseClient` |
| Backpressure | `onBackpressureBuffer()`, `onBackpressureDrop()`, `limitRate()` |
| Reactive vs Servlet | When reactive shines (high concurrency, I/O-bound), when servlet is better (CPU-bound, JDBC) |
| Testing | `StepVerifier`, virtual time, `WebTestClient` |

### 3 · Spring State Machine — 2 h

| Sub-topic | Key concepts |
|---|---|
| States & transitions | `StateMachineConfigurer`, `configureStates()`, `configureTransitions()` |
| Events | External triggers, event payloads, `StateMachine.sendEvent()` |
| Guards | `Guard<S, E>`, conditional transitions |
| Actions | `Action<S, E>`, entry/exit/transition actions, side effects |
| Hierarchical states | Sub-states, regions, parallel states |
| Persistence | `StateMachinePersister`, storing machine state in database |

### 4 · Spring Integration — 2 h

| Sub-topic | Key concepts |
|---|---|
| Enterprise Integration Patterns | Message, Channel, Endpoint, Router, Transformer, Filter |
| Message channels | `DirectChannel`, `QueueChannel`, `PublishSubscribeChannel` |
| Endpoints | `@ServiceActivator`, `@Transformer`, `@Router`, `@Filter` |
| Adapters | File, FTP, JMS, AMQP, HTTP inbound/outbound adapters |
| Java DSL | `IntegrationFlow`, fluent builder, lambda handlers |

### 5 · Spring Modulith — 3 h

| Sub-topic | Key concepts |
|---|---|
| Logical modules | Package-based modules, module API vs internals |
| Module interaction | Public API classes, `@ApplicationModuleListener`, events |
| Events | Application events for inter-module communication, event externalization |
| Module testing | `@ApplicationModuleTest`, verifying module boundaries |
| Documentation | Auto-generated module documentation and dependency diagrams |
| Migration path | From monolith to modulith, eventual extraction to microservices |

### 6 · Observability — 3 h

| Sub-topic | Key concepts |
|---|---|
| Structured logging | Logback / Log4j2, JSON layout, MDC (traceId, spanId, userId) |
| Metrics | Micrometer, `@Timed`, `Counter`, `Gauge`, `DistributionSummary` |
| Prometheus | Micrometer Prometheus registry, `/actuator/prometheus` endpoint |
| Tracing | OpenTelemetry, OTLP exporter, trace/span model |
| Grafana | Dashboard creation, metric visualization, alerting rules |
| Health & info | Custom `HealthIndicator`, `/actuator/info`, build info |

### 7 · Performance Tuning — 3 h

| Sub-topic | Key concepts |
|---|---|
| Connection pooling | HikariCP configuration (pool size, idle timeout, max lifetime) |
| Caching | `@Cacheable`, `@CacheEvict`, `@CachePut`, Spring Cache abstraction |
| Cache providers | Caffeine (in-process), Redis (distributed), EhCache |
| Async processing | `@Async`, `@EnableAsync`, custom `TaskExecutor`, `CompletableFuture` |
| Database optimization | N+1 query detection, batch fetching, read replicas, query hints |
| JVM tuning | Heap sizing, GC selection (G1, ZGC), JFR (Java Flight Recorder) |

---

## Exercises

### Exercise 1 — Data Migration Batch Job

**Goal:** Process a large CSV file into a database with production-grade resilience.

**Requirements:**

1. **Job definition:** single-step chunk-oriented job reading from a 1-million-row CSV.
2. **Reader:** `FlatFileItemReader<RawRecord>` with column mapping and line tokenizer.
3. **Processor:** `ItemProcessor<RawRecord, CleanRecord>` — validates required fields, normalizes formats (dates, phone numbers), enriches with computed fields.
4. **Writer:** `JdbcBatchItemWriter<CleanRecord>` — batch INSERT with configurable chunk size (default 1000).
5. **Skip policy:** skip up to 500 `FlatFileParseException` or `ValidationException`; write skipped records to a separate error file.
6. **Retry policy:** retry up to 3 times on `DataAccessException` (transient DB errors).
7. **Restart:** if the job fails at row 500,000, restarting picks up from the last committed chunk.
8. **Partitioning:** `TaskExecutorPartitionHandler` splits the CSV into N ranges for parallel processing.
9. **Scheduling:** run nightly at 02:00 via `@Scheduled` or Quartz.
10. **Monitoring:** expose job metrics via Actuator (`spring.batch.*` metrics).
11. **Bonus:** Implement a custom `ItemReader` that reads paginated records from an external REST API.

---

### Exercise 2 — Reactive Stock Ticker

**Goal:** Stream real-time stock prices using WebFlux and R2DBC.

**Requirements:**

1. **Price generator:** a `Flux<StockPrice>` that emits random prices for 10 stocks every 500ms.
2. **SSE endpoint:** `GET /api/stocks/stream` returns `text/event-stream` with `Flux<ServerSentEvent<StockPrice>>`.
3. **Filtered stream:** `GET /api/stocks/stream?symbols=AAPL,GOOG` streams only selected symbols.
4. **Reactive repository:** `ReactiveCrudRepository<StockPrice, Long>` backed by R2DBC (H2 for dev).
5. **Persistence:** each price event is saved to the database reactively.
6. **History endpoint:** `GET /api/stocks/{symbol}/history?minutes=30` returns recent prices.
7. **Backpressure:** if a slow client cannot keep up, drop oldest undelivered prices.
8. **Combine streams:** `GET /api/stocks/portfolio?symbols=AAPL,GOOG` merges multiple symbol streams and emits aggregated portfolio value.
9. **Bonus:** Implement a simple moving average (SMA) as a reactive pipeline — emit SMA alongside raw price.

---

## Capstone Preview

After completing Phases 04–07, you are ready for the **Capstone Project** (see `capstone-project/README.md`). The capstone combines every concept from the entire roadmap into a full microservices platform.

---

## Self-Assessment Checklist

- [ ] I can configure a Spring Batch job with reader, processor, writer, and chunk size.
- [ ] I can implement skip and retry policies for resilient batch processing.
- [ ] I can restart a failed batch job from the last committed chunk.
- [ ] I can partition a batch step for parallel execution.
- [ ] I can create Mono/Flux pipelines with map, flatMap, and error handling operators.
- [ ] I can build a WebFlux controller that streams SSE to clients.
- [ ] I can use R2DBC with a reactive repository for non-blocking database access.
- [ ] I can explain when reactive is beneficial vs when servlet-stack is better.
- [ ] I can define states, transitions, guards, and actions in Spring State Machine.
- [ ] I can structure an application into logical modules with Spring Modulith.
- [ ] I can set up structured logging with MDC and export metrics to Prometheus.
- [ ] I can configure HikariCP, Spring Cache, and @Async for performance tuning.

---

## References

| Resource | Link |
|---|---|
| Spring Batch — Reference | https://docs.spring.io/spring-batch/reference/ |
| Project Reactor — Reference | https://projectreactor.io/docs/core/release/reference/ |
| Spring WebFlux — Reference | https://docs.spring.io/spring-framework/reference/web/webflux.html |
| R2DBC — Specification | https://r2dbc.io/ |
| Spring Modulith — Reference | https://docs.spring.io/spring-modulith/reference/ |
| Spring State Machine — Reference | https://docs.spring.io/spring-statemachine/docs/current/reference/ |
| Micrometer — Reference | https://micrometer.io/docs |
| Baeldung — Spring Batch | https://www.baeldung.com/introduction-to-spring-batch |
| Baeldung — Spring WebFlux | https://www.baeldung.com/spring-webflux |
