# Phase 06 — Spring Cloud & Microservices

**Duration:** ~3–4 weeks · **Total estimated hours:** 30h

## Learning Objectives

By the end of this phase you will be able to:

1. Decompose a monolithic domain into bounded-context-aligned microservices with clear data ownership.
2. Set up service discovery with Eureka and route traffic through Spring Cloud Gateway.
3. Externalize configuration with Spring Cloud Config and refresh it at runtime.
4. Apply resilience patterns (circuit breaker, retry, rate limiter, bulkhead) using Resilience4j.
5. Implement distributed tracing with Micrometer Tracing and Zipkin to follow requests across services.
6. Build event-driven architectures with Kafka and RabbitMQ using Spring Cloud Stream.
7. Implement the Saga pattern for distributed transactions (choreography and orchestration).
8. Containerize services with Docker and orchestrate them locally with docker-compose.

---

## Topics

### 1 · Microservices Architecture — 3 h

| Sub-topic | Key concepts |
|---|---|
| Monolith vs Microservices | Trade-offs, when to split, migration strategies |
| Bounded contexts | DDD alignment, service boundaries, ubiquitous language |
| Data ownership | Database-per-service, no shared databases, eventual consistency |
| Communication | Synchronous (REST, gRPC) vs asynchronous (messaging, events) |
| API contracts | Consumer-driven contracts, schema evolution, backward compatibility |

### 2 · Service Discovery — 3 h

| Sub-topic | Key concepts |
|---|---|
| Eureka Server | `@EnableEurekaServer`, standalone and peer-aware setup |
| Eureka Client | `@EnableDiscoveryClient`, `eureka.client.*` config, heartbeat/lease |
| Client-side LB | Spring Cloud LoadBalancer, `@LoadBalanced RestTemplate/WebClient` |
| Health checks | Eureka health indicator, custom health checks, eviction |
| Alternatives | Consul, Kubernetes service discovery (when to skip Eureka) |

### 3 · API Gateway — 3 h

| Sub-topic | Key concepts |
|---|---|
| Spring Cloud Gateway | Reactive gateway, route definitions (Java DSL and YAML) |
| Predicates | Path, Host, Method, Header, Query, Weight |
| Filters | `AddRequestHeader`, `RewritePath`, `CircuitBreaker`, `RequestRateLimiter` |
| Rate limiting | Redis-based `RequestRateLimiter`, custom key resolver |
| Security at gateway | Token relay, forwarding `Authorization` header to downstream |

### 4 · Config Server — 2 h

| Sub-topic | Key concepts |
|---|---|
| Spring Cloud Config Server | `@EnableConfigServer`, git backend, file-system backend |
| Client bootstrap | `spring.config.import`, profile-specific config |
| Encryption | `{cipher}` property values, symmetric/asymmetric keys |
| Refresh | `@RefreshScope`, `/actuator/refresh`, Spring Cloud Bus for broadcast |

### 5 · Resilience — 4 h

| Sub-topic | Key concepts |
|---|---|
| Circuit Breaker | States (CLOSED → OPEN → HALF_OPEN), failure rate threshold, wait duration |
| Retry | Max attempts, wait interval, exponential backoff, retry on specific exceptions |
| Rate Limiter | Permits per period, timeout duration, fair vs greedy |
| Bulkhead | Thread-pool vs semaphore isolation, max concurrent calls |
| Time Limiter | Timeout duration, cancel running future |
| Fallback | `@CircuitBreaker(fallbackMethod=...)`, graceful degradation |
| Resilience4j config | YAML-based, per-instance, health indicators, metrics |

### 6 · Distributed Tracing — 3 h

| Sub-topic | Key concepts |
|---|---|
| Concepts | Trace, Span, Span ID, Trace ID, parent-child relationships |
| Micrometer Tracing | Auto-instrumentation, `ObservationRegistry`, brave/otel bridge |
| Zipkin | Zipkin server, trace visualization, dependency graph |
| Log correlation | MDC `traceId` / `spanId` in log patterns, structured logging |
| Propagation | B3 (single/multi), W3C Trace Context, header propagation |

### 7 · Messaging — 5 h

| Sub-topic | Key concepts |
|---|---|
| Apache Kafka | Topics, partitions, consumer groups, offsets, compaction |
| Spring Kafka | `KafkaTemplate`, `@KafkaListener`, `ConsumerFactory`, `ProducerFactory` |
| RabbitMQ | Exchanges (direct, topic, fanout), queues, bindings, acknowledgments |
| Spring AMQP | `RabbitTemplate`, `@RabbitListener`, message converters |
| Spring Cloud Stream | Binder abstraction, `Supplier`/`Function`/`Consumer` bindings |
| Dead Letter Queues | DLQ configuration, error channel, retry + DLQ strategy |
| Exactly-once | Idempotent consumer, transactional outbox pattern |

### 8 · Saga Pattern — 3 h

| Sub-topic | Key concepts |
|---|---|
| Choreography | Event-driven, each service publishes and listens, decentralized |
| Orchestration | Central coordinator, step-by-step, easier to reason about |
| Compensating transactions | Undo operations, semantic rollback, idempotency |
| Failure handling | Timeout detection, dead letter processing, manual intervention |

### 9 · Docker & Kubernetes — 4 h

| Sub-topic | Key concepts |
|---|---|
| Dockerizing Spring Boot | Dockerfile, multi-stage build, Spring Boot layered JARs, Jib |
| docker-compose | Service definitions, networking, volumes, environment variables |
| Kubernetes basics | Pods, Deployments, Services (ClusterIP, NodePort), ConfigMap, Secret |
| Health probes | Liveness (`/actuator/health/liveness`), readiness (`/actuator/health/readiness`) |
| Kubernetes for Spring | Spring Cloud Kubernetes, config from ConfigMap, service discovery |

---

## Exercise — Microservices E-Commerce System

**Goal:** Build a complete event-driven microservices system.

### Services

| Service | Responsibility | Key endpoints |
|---|---|---|
| **Order Service** | Create orders, query status, order history | `POST /api/orders`, `GET /api/orders/{id}`, `GET /api/orders?userId=` |
| **Inventory Service** | Manage stock levels, reserve stock | `GET /api/inventory/{sku}`, `PUT /api/inventory/{sku}/reserve` |
| **Payment Service** | Process payments, refunds | `POST /api/payments`, `POST /api/payments/{id}/refund` |
| **Notification Service** | Send notifications on domain events | Consumes events, no REST API |

### Infrastructure Services

| Component | Technology |
|---|---|
| Discovery Server | Spring Cloud Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Config Server | Spring Cloud Config (git/file) |
| Message Broker | Apache Kafka |
| Tracing | Zipkin |

### Event Flow (Order Saga — Choreography)

```
[Client] → POST /api/orders
    │
    ▼
[Order Service]
    ├── Creates Order (status: PENDING)
    └── Publishes: OrderPlacedEvent { orderId, items[], userId }
                         │
                         ▼
              [Inventory Service]
                  ├── Reserves stock for each item
                  ├── If OK → publishes: StockReservedEvent { orderId }
                  └── If insufficient → publishes: StockInsufficientEvent { orderId, item }
                                                           │
               ┌───────────────────────────────────────────┘
               ▼                                           ▼
    [Order Service]                             [Inventory Service]
    Updates to CANCELLED                        (no stock change needed)
               │
               ▼
    [Notification Service]
    Sends "order cancelled" notification
                                     ▼
                          [Payment Service]
                              ├── Charges payment
                              ├── If OK → publishes: PaymentCompletedEvent { orderId }
                              └── If FAIL → publishes: PaymentFailedEvent { orderId }
                                                              │
               ┌──────────────────────────────────────────────┘
               ▼                                              ▼
    [Inventory Service]                            [Order Service]
    Releases reserved stock                        Updates to CONFIRMED
               │                                              │
               ▼                                              ▼
    [Order Service]                              [Notification Service]
    Updates to CANCELLED                         Sends "order confirmed"
               │
               ▼
    [Notification Service]
    Sends "payment failed"
```

### Requirements

1. Each service is a separate Spring Boot application with its own database (H2 for dev, PostgreSQL for docker).
2. Eureka for service discovery — all services register.
3. API Gateway routes: `/api/orders/**` → Order Service, `/api/inventory/**` → Inventory Service, etc.
4. Config Server provides `application.yml` per service from a git repo (or local file).
5. Kafka topics: `order-events`, `inventory-events`, `payment-events`.
6. Resilience4j circuit breaker on synchronous inter-service calls (e.g., Order → Inventory stock check).
7. Distributed tracing with Zipkin — each service propagates trace/span IDs.
8. `docker-compose.yml` starts all infrastructure (Kafka, Zookeeper, Zipkin, PostgreSQL) and all services.
9. Each service exposes `/actuator/health` for container health checks.
10. **Bonus:** Create Kubernetes deployment manifests (Deployment + Service + ConfigMap per service).

---

## Self-Assessment Checklist

- [ ] I can explain when microservices are appropriate vs a modular monolith.
- [ ] I can set up a Eureka server and register client services.
- [ ] I can configure Spring Cloud Gateway routes with predicates and filters.
- [ ] I can externalize configuration with Spring Cloud Config and refresh at runtime.
- [ ] I can configure Resilience4j circuit breaker, retry, and rate limiter.
- [ ] I can implement distributed tracing with Micrometer Tracing and view traces in Zipkin.
- [ ] I can produce and consume Kafka messages with Spring Kafka.
- [ ] I can implement the Saga pattern (choreography) for distributed transactions.
- [ ] I can write compensating transactions to handle saga failures.
- [ ] I can Dockerize a Spring Boot application with a multi-stage Dockerfile.
- [ ] I can write a `docker-compose.yml` that starts all services and infrastructure.
- [ ] I can configure Kubernetes Deployments, Services, and ConfigMaps for Spring Boot.

---

## References

| Resource | Link |
|---|---|
| Spring Cloud — Reference | https://docs.spring.io/spring-cloud/reference/ |
| Spring Cloud Gateway | https://docs.spring.io/spring-cloud-gateway/reference/ |
| Resilience4j | https://resilience4j.readme.io/docs |
| Spring Kafka — Reference | https://docs.spring.io/spring-kafka/reference/ |
| Micrometer Tracing | https://micrometer.io/docs/tracing |
| Building Microservices | O'Reilly — Sam Newman |
| Microservices Patterns | Manning — Chris Richardson |
| Release It! | Pragmatic Bookshelf — Michael Nygard |
| Baeldung — Spring Cloud | https://www.baeldung.com/spring-cloud-series |
