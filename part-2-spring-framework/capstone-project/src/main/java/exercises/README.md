# Capstone Project — Source Root

> This directory is the **capstone project root**. The actual implementation
> should use a **multi-module Maven project** with one module per service.

## Suggested Module Structure

Create each module as a sibling directory under `capstone-project/`, each
with its own `pom.xml` inheriting from a shared parent POM.

```
capstone-project/
├── pom.xml                        ← Parent POM (packaging=pom, modules list)
├── README.md                      ← Project overview and architecture
├── docker-compose.yml             ← All infrastructure + services
│
├── discovery-server/              ← Eureka Server
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/platform/discovery/
│       └── DiscoveryServerApplication.java
│
├── config-server/                 ← Spring Cloud Config
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/platform/config/
│       └── ConfigServerApplication.java
│
├── api-gateway/                   ← Spring Cloud Gateway
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/platform/gateway/
│       └── GatewayApplication.java
│
├── user-service/                  ← Authentication, profiles, roles
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/platform/user/
│       ├── UserServiceApplication.java
│       ├── config/
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── event/
│       ├── repository/
│       ├── security/
│       └── service/
│
├── course-service/                ← Course CRUD, search, categories
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/platform/course/
│       ├── CourseServiceApplication.java
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── event/
│       ├── repository/
│       └── service/
│
├── enrollment-service/            ← Enrollment, progress tracking
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/platform/enrollment/
│       ├── EnrollmentServiceApplication.java
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── event/
│       ├── listener/
│       ├── repository/
│       └── service/
│
├── payment-service/               ← Checkout, refund
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/platform/payment/
│       ├── PaymentServiceApplication.java
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── event/
│       ├── listener/
│       ├── repository/
│       └── service/
│
├── notification-service/          ← Email, in-app, SSE
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/platform/notification/
│       ├── NotificationServiceApplication.java
│       ├── controller/
│       ├── dto/
│       ├── entity/
│       ├── listener/
│       ├── repository/
│       ├── service/
│       └── template/
│
└── config-repo/                   ← Git-backed config files
    ├── application.yml            ← Shared defaults
    ├── user-service.yml
    ├── course-service.yml
    ├── enrollment-service.yml
    ├── payment-service.yml
    └── notification-service.yml
```

## Quick Start

1. Copy this structure and create a parent `pom.xml` with `<packaging>pom</packaging>`.
2. List all modules in the parent POM's `<modules>` block.
3. Use the Spring Cloud BOM in `<dependencyManagement>`.
4. Start infrastructure with `docker-compose up -d`.
5. Build all modules: `mvn clean package -DskipTests` from the root.
6. Run each service individually or via docker-compose.

## Notes

- Each service should have its **own database schema** (database-per-service pattern).
- Use **Kafka topics** for inter-service communication (`user-events`, `course-events`, `enrollment-events`, `payment-events`).
- Ensure every service registers with **Eureka** and pulls config from the **Config Server**.
- Add **OpenAPI documentation** to each REST service (`springdoc-openapi`).
- Write tests at every level: unit → integration → contract.
