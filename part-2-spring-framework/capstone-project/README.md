# Capstone Project — Online Learning Platform

> A full-stack microservices platform combining **every concept** from Phases 04–07.

**Duration:** ~4 weeks (milestones below) · **Complexity:** Production-grade

---

## Project Overview

Build a simplified **Udemy-like** online learning platform as a set of
independently deployable microservices. The system covers user management,
course catalog, enrollment with progress tracking, payments, notifications,
and supporting infrastructure (gateway, discovery, configuration, messaging,
observability).

This capstone is designed to prove mastery of:

- RESTful API design with validation, HATEOAS, and OpenAPI
- Security (JWT + OAuth2, role-based access)
- Data access with Spring Data JPA
- Event-driven communication with Kafka
- Resilience patterns (circuit breaker, retry)
- Batch processing for reports
- Containerization and orchestration
- Distributed tracing and metrics

---

## Architecture

```
                        ┌──────────────────────────────────┐
                        │            Client(s)             │
                        │   (Browser / Mobile / Postman)   │
                        └────────────────┬─────────────────┘
                                         │ HTTPS
                                         ▼
                        ┌──────────────────────────────────┐
                        │         API Gateway (8080)       │
                        │      Spring Cloud Gateway        │
                        │  - Route to services via Eureka  │
                        │  - Token relay / auth forwarding │
                        │  - Rate limiting                 │
                        └──────────┬───────────────────────┘
                                   │
            ┌──────────┬───────────┼───────────┬───────────┐
            ▼          ▼           ▼           ▼           ▼
    ┌─────────────┐ ┌──────────┐ ┌──────────┐ ┌─────────┐ ┌──────────────┐
    │ User Service│ │  Course  │ │Enrollment│ │ Payment │ │ Notification │
    │   (8081)    │ │ Service  │ │ Service  │ │ Service │ │   Service    │
    │             │ │  (8082)  │ │  (8083)  │ │ (8084)  │ │   (8085)     │
    │ - Auth      │ │ - CRUD   │ │ - Enroll │ │ - Pay   │ │ - Email      │
    │ - Profiles  │ │ - Search │ │ - Progress│ │ - Refund│ │ - In-app     │
    │ - Roles     │ │ - Categs │ │ - History│ │         │ │ - Templates  │
    └──────┬──────┘ └────┬─────┘ └────┬─────┘ └────┬────┘ └──────┬───────┘
           │             │            │             │             │
           └─────────────┴────────────┴──────┬──────┴─────────────┘
                                             │
                              ┌───────────────┼───────────────┐
                              ▼               ▼               ▼
                     ┌──────────────┐ ┌─────────────┐ ┌─────────────┐
                     │  PostgreSQL  │ │   Apache    │ │   Zipkin    │
                     │  (per-svc)   │ │   Kafka     │ │  + Prometheus│
                     └──────────────┘ └─────────────┘ └─────────────┘

    Infrastructure:
      - Discovery Server (Eureka) — port 8761
      - Config Server — port 8888
```

---

## Services

### 1. User Service (8081)

| Concern | Details |
|---|---|
| Authentication | JWT access + refresh tokens; BCrypt password hashing |
| OAuth2 | Social login (Google, GitHub) with local account linking |
| Profiles | CRUD user profiles (name, bio, avatar URL) |
| Roles | ADMIN, INSTRUCTOR, STUDENT; hierarchical permissions |
| Endpoints | `POST /api/auth/register`, `POST /api/auth/login`, `POST /api/auth/refresh`, `GET /api/users/me`, `PUT /api/users/me` |
| Events published | `UserRegisteredEvent`, `UserUpdatedEvent` |

### 2. Course Service (8082)

| Concern | Details |
|---|---|
| CRUD | Create, read, update, delete courses (INSTRUCTOR/ADMIN only) |
| Categories | Hierarchical categories (parent/child) |
| Search | Full-text search by title/description, filter by category/price/rating |
| Pagination | Offset-based with sort; HATEOAS links |
| Content | Course sections → lessons (title, type, duration, content URL) |
| Endpoints | `POST /api/courses`, `GET /api/courses`, `GET /api/courses/{id}`, `PUT /api/courses/{id}`, `DELETE /api/courses/{id}`, `GET /api/courses/search?q=` |
| Events published | `CoursePublishedEvent`, `CourseUpdatedEvent` |

### 3. Enrollment Service (8083)

| Concern | Details |
|---|---|
| Enrollment | Enroll student in course (after payment confirmation) |
| Progress | Track lesson completion per student; compute percentage |
| History | List all enrollments for a user or a course |
| Certificates | Generate completion certificate (placeholder) when 100% |
| Endpoints | `POST /api/enrollments`, `GET /api/enrollments?userId=`, `PUT /api/enrollments/{id}/progress`, `GET /api/enrollments/{id}` |
| Events consumed | `PaymentCompletedEvent` → create enrollment |
| Events published | `EnrollmentCreatedEvent`, `CourseCompletedEvent` |

### 4. Payment Service (8084)

| Concern | Details |
|---|---|
| Checkout | Process payment for a course enrollment |
| Refund | Issue refund within a configurable window (e.g., 30 days) |
| Idempotency | Prevent double charges using idempotency keys |
| Endpoints | `POST /api/payments/checkout`, `POST /api/payments/{id}/refund`, `GET /api/payments/{id}` |
| Events consumed | `EnrollmentRequestedEvent` (optional orchestration variant) |
| Events published | `PaymentCompletedEvent`, `PaymentFailedEvent`, `RefundIssuedEvent` |

### 5. Notification Service (8085)

| Concern | Details |
|---|---|
| Email | Send transactional emails (welcome, enrollment confirmation, completion) |
| In-app | Store in-app notifications; SSE stream to connected clients |
| Templates | Configurable templates per event type |
| Events consumed | `UserRegisteredEvent`, `EnrollmentCreatedEvent`, `CourseCompletedEvent`, `PaymentCompletedEvent`, `RefundIssuedEvent` |
| Endpoints | `GET /api/notifications?userId=`, `GET /api/notifications/stream` (SSE) |

### 6. API Gateway (8080)

- Spring Cloud Gateway
- Routes traffic by path prefix to the correct service via Eureka
- Token relay: forwards `Authorization` header to downstream services
- Rate limiting per client IP / API key
- Global error handling for downstream failures

### 7. Config Server (8888)

- Spring Cloud Config Server
- Git-backed (or local file) configuration per service and profile
- Encryption support for secrets (database passwords, JWT secret)
- Runtime refresh via `/actuator/refresh`

### 8. Discovery Server (8761)

- Spring Cloud Netflix Eureka
- All services register on startup
- Gateway and inter-service calls use Eureka for discovery
- Dashboard at `http://localhost:8761`

---

## Technical Requirements

| Requirement | Technology |
|---|---|
| Runtime | Spring Boot 3.x, Java 21 |
| Security | Spring Security, JWT (jjwt), OAuth2 Client |
| Data | Spring Data JPA, PostgreSQL |
| Cloud | Spring Cloud (Eureka, Config, Gateway) |
| Messaging | Apache Kafka (Spring Kafka) |
| Resilience | Resilience4j (circuit breaker, retry, rate limiter) |
| Batch | Spring Batch (monthly enrollment report) |
| Containers | Docker, docker-compose |
| Documentation | OpenAPI 3.0 per service (springdoc-openapi) |
| Testing | JUnit 5, Mockito, Spring Boot Test, Spring Security Test, Testcontainers |
| Observability | Micrometer metrics, Zipkin tracing, Prometheus endpoint, structured logging |

---

## Event Flow

```
[Student]
   │  POST /api/payments/checkout  { courseId, userId }
   ▼
[Payment Service]
   ├── Validates payment details
   ├── Charges payment (simulated)
   ├── Publishes: PaymentCompletedEvent { paymentId, courseId, userId }
   │                    │
   │                    ▼
   │          [Enrollment Service]
   │              ├── Creates enrollment record
   │              ├── Publishes: EnrollmentCreatedEvent { enrollmentId, courseId, userId }
   │              │                    │
   │              │                    ▼
   │              │          [Notification Service]
   │              │              └── Sends enrollment confirmation email + in-app notification
   │              │
   │              └── (later) Student completes all lessons
   │                    ├── Publishes: CourseCompletedEvent { enrollmentId, courseId, userId }
   │                    │                    │
   │                    │                    ▼
   │                    │          [Notification Service]
   │                    │              └── Sends completion email + certificate notification
   │                    └── Generates completion certificate
   │
   └── If payment fails:
        ├── Publishes: PaymentFailedEvent { courseId, userId, reason }
        └── [Notification Service] sends payment failure notification
```

---

## Milestones

### Milestone 1 — Foundation (Week 1)

- [ ] Set up multi-module Maven project structure
- [ ] Create Discovery Server (Eureka) and Config Server
- [ ] Create API Gateway with basic routes
- [ ] Create User Service: registration, login (JWT), profile CRUD
- [ ] Docker-compose for infrastructure (PostgreSQL, Kafka, Zipkin)

### Milestone 2 — Core Domain (Week 2)

- [ ] Create Course Service: full CRUD, search, categories
- [ ] Create Payment Service: checkout, refund
- [ ] Create Enrollment Service: enroll, progress tracking
- [ ] Kafka event publishing and consumption for the enrollment flow
- [ ] Resilience4j circuit breaker on inter-service calls

### Milestone 3 — Cross-Cutting Concerns (Week 3)

- [ ] Create Notification Service: email + in-app + SSE
- [ ] Spring Batch job: monthly enrollment report (CSV export)
- [ ] Distributed tracing with Zipkin
- [ ] OpenAPI documentation per service
- [ ] OAuth2 social login (Google) in User Service
- [ ] Comprehensive testing (unit + integration + security)

### Milestone 4 — Production Readiness (Week 4)

- [ ] Dockerize all services (multi-stage builds)
- [ ] Complete docker-compose (all services + infrastructure)
- [ ] Health probes (liveness + readiness)
- [ ] Prometheus metrics endpoint per service
- [ ] Structured logging with trace/span correlation
- [ ] Load testing with a tool of choice (k6, JMeter, or Gatling)
- [ ] Final documentation and README per service

---

## Evaluation Criteria

| Category | Weight | What to look for |
|---|---|---|
| **Architecture** | 20% | Clean service boundaries, proper event-driven design, no shared databases |
| **API Design** | 15% | RESTful conventions, validation, error handling, HATEOAS, OpenAPI docs |
| **Security** | 15% | JWT + OAuth2, role-based access, CORS/CSRF, no hardcoded secrets |
| **Resilience** | 10% | Circuit breakers, retries, graceful degradation, saga compensations |
| **Testing** | 15% | Unit tests (services, controllers), integration tests, security tests, >70% coverage |
| **Observability** | 10% | Structured logs, distributed traces, Prometheus metrics |
| **Containerization** | 10% | Dockerfiles, docker-compose, environment configuration, health probes |
| **Code Quality** | 5% | Clean code, consistent style, meaningful names, no dead code |

---

## Getting Started

```bash
# 1. Start infrastructure
docker-compose up -d postgres kafka zookeeper zipkin

# 2. Start discovery server
cd discovery-server && mvn spring-boot:run

# 3. Start config server
cd config-server && mvn spring-boot:run

# 4. Start services (each in a separate terminal)
cd user-service && mvn spring-boot:run
cd course-service && mvn spring-boot:run
cd enrollment-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run

# 5. Start API gateway
cd api-gateway && mvn spring-boot:run

# 6. Access
#    - Eureka dashboard: http://localhost:8761
#    - API Gateway:      http://localhost:8080
#    - Zipkin:           http://localhost:9411
#    - Swagger UI:       http://localhost:808x/swagger-ui.html (per service)
```

---

## References

| Resource | Link |
|---|---|
| Spring Boot Reference | https://docs.spring.io/spring-boot/reference/ |
| Spring Cloud Reference | https://docs.spring.io/spring-cloud/reference/ |
| Spring Security Reference | https://docs.spring.io/spring-security/reference/ |
| Building Microservices (Newman) | O'Reilly |
| Microservices Patterns (Richardson) | Manning |
| Clean Architecture (Martin) | Pearson |
