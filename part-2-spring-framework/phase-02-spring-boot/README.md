# Phase 02 — Spring Boot

**Duration:** ~2 weeks (17 hours of study + exercises)

---

## Learning Objectives

By the end of this phase you will be able to:

- Explain what Spring Boot adds on top of the Spring Framework and how auto-configuration works.
- Configure applications using `application.yml`, `@ConfigurationProperties`, and profiles.
- Expose and secure Spring Boot Actuator endpoints for production monitoring.
- Package applications as fat JARs and Docker images.
- Write effective tests using `@SpringBootTest`, test slices, and `MockMvc`.
- Create a custom Spring Boot starter with conditional auto-configuration.

---

## Topics

### 1. Spring Boot Fundamentals — 4 h

| Sub-topic | Hours | Key ideas |
|-----------|-------|-----------|
| What Boot adds on top of Spring | 0.5 | Opinionated defaults, embedded server, production readiness, starter ecosystem |
| `@SpringBootApplication`: what it combines | 0.5 | `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`, why the main class location matters |
| Auto-configuration: how it works, `@Conditional` annotations, `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` | 1.5 | `spring.factories` (legacy) vs new imports file, `@ConditionalOnClass`, `@ConditionalOnProperty`, debug mode (`--debug`) to see auto-config report |
| Starters: what they are, how to find them | 0.5 | Naming convention (`spring-boot-starter-*`), transitive dependencies, community starters |
| Spring Initializr and project structure | 1 | [start.spring.io](https://start.spring.io), recommended package layout, why the main class should sit at the root package |

### 2. Configuration — 4 h

| Sub-topic | Hours | Key ideas |
|-----------|-------|-----------|
| `application.properties` vs `application.yml` | 0.5 | YAML hierarchy, multi-document files (`---`), when to prefer which |
| `@ConfigurationProperties`: type-safe config binding, nested properties, validation | 1.5 | `@EnableConfigurationProperties`, prefix binding, `@Validated` + JSR-303, immutable `@ConstructorBinding` |
| Profile-specific configuration files | 0.5 | `application-{profile}.yml`, profile groups, `spring.profiles.include` |
| Externalized config precedence order | 0.5 | 17-level precedence: defaults → YAML → env vars → CLI args → config server |
| Environment variables, command-line args, config server preview | 1 | Relaxed binding (`MY_APP_NAME` → `my.app.name`), `@Value` vs `@ConfigurationProperties`, Spring Cloud Config intro |

### 3. Spring Boot Actuator — 3 h

| Sub-topic | Hours | Key ideas |
|-----------|-------|-----------|
| Built-in endpoints: `/health`, `/info`, `/metrics`, `/env`, `/beans`, `/mappings` | 1 | Enabling/disabling endpoints, exposure over HTTP vs JMX, `management.endpoints.web.exposure.include` |
| Custom health indicators | 0.5 | `AbstractHealthIndicator`, `Health.up()` / `Health.down()`, composite health |
| Custom endpoints | 0.5 | `@Endpoint`, `@ReadOperation`, `@WriteOperation`, `@DeleteOperation` |
| Securing actuator endpoints | 0.5 | Separate management port, Spring Security integration, role-based access |
| Metrics with Micrometer: counters, gauges, timers | 0.5 | `MeterRegistry`, tags/dimensions, Prometheus/Grafana export preview |

### 4. Embedded Server & Deployment — 2 h

| Sub-topic | Hours | Key ideas |
|-----------|-------|-----------|
| Tomcat, Jetty, Undertow | 0.5 | Swapping servers via exclusion + dependency, `server.port`, `server.ssl.*` |
| Fat JAR packaging: how it works | 0.5 | Spring Boot Maven/Gradle plugin, nested JAR structure, `BOOT-INF/`, executable JAR |
| Docker containerization | 0.5 | `Dockerfile` best practices, multi-stage builds, layered JARs (`spring-boot:build-image`) |
| GraalVM native image basics | 0.5 | AOT compilation, reflection config, build-time vs runtime trade-offs, `spring-boot:build-image` with Paketo |

### 5. DevTools & Testing — 2 h

| Sub-topic | Hours | Key ideas |
|-----------|-------|-----------|
| `spring-boot-devtools`: auto-restart, live reload | 0.5 | Class change detection, excluded paths, `spring.devtools.restart.enabled` |
| `@SpringBootTest`, test slices (`@WebMvcTest`, `@DataJpaTest`) | 1 | Full context vs slice, `@MockBean`, `@TestConfiguration`, `webEnvironment` options |
| `TestRestTemplate`, `MockMvc` intro | 0.5 | Integration testing with random port, `MockMvc` for controller unit tests |

### 6. Custom Auto-Configuration — 2 h

| Sub-topic | Hours | Key ideas |
|-----------|-------|-----------|
| Creating your own starter | 1 | Module structure (`*-autoconfigure` + `*-starter`), registering in imports file, naming conventions |
| `@Conditional` annotations: `@ConditionalOnClass`, `@ConditionalOnProperty`, `@ConditionalOnMissingBean` | 1 | Ordering with `@AutoConfigureBefore` / `@AutoConfigureAfter`, testing auto-configs with `ApplicationContextRunner` |

---

## Exercises

### Exercise 1 — Production-Ready REST Service

**Difficulty:** ★★☆ Intermediate  
**Estimated time:** 4–5 hours

Build a task management REST API with Spring Boot.

**Requirements:**

1. CRUD endpoints for `Task` (id, title, description, priority, status, createdAt).
2. Configure the application via `@ConfigurationProperties` with prefix `app`:
   - `app.name` — display name
   - `app.tasks.max-count` — maximum number of tasks allowed
   - `app.tasks.default-priority` — default priority for new tasks
3. Add Spring Boot Actuator:
   - Custom health indicator that reports DOWN when task count ≥ `max-count`.
   - Custom `/info` contributor showing app version (from `pom.xml`) and JVM uptime.
4. Profile-specific config:
   - `dev` — H2 in-memory database, all actuator endpoints exposed
   - `prod` — simulated PostgreSQL config, only `/health` and `/info` exposed
5. Write integration tests with `@SpringBootTest` and `MockMvc`.

**Bonus:** Create a `Dockerfile` with multi-stage build (Maven build → slim JRE runtime).

---

### Exercise 2 — Custom Auto-Configuration Starter

**Difficulty:** ★★★ Advanced  
**Estimated time:** 4–5 hours

Create a reusable Spring Boot starter in a separate Maven module.

**Requirements:**

1. Module: `greeting-spring-boot-starter`
   - Contains `GreetingService` interface + default implementation.
   - Auto-configuration class with `@ConditionalOnProperty(prefix="greeting", name="enabled", havingValue="true", matchIfMissing=true)`.
   - `@ConditionalOnMissingBean` so users can provide their own `GreetingService`.
2. `@ConfigurationProperties(prefix="greeting")`:
   - `greeting.template` — message template with `{name}` placeholder
   - `greeting.locale` — locale for formatting
3. Write a test application that depends on the starter.
4. Verify: when user defines their own `GreetingService` bean, the auto-configured one backs off.

**Bonus:** Add Actuator integration — a custom Micrometer counter that tracks greeting invocations.

---

### Exercise 3 — Feature Flag System

**Difficulty:** ★★★ Advanced  
**Estimated time:** 4–5 hours

Build a feature flag management system for Spring Boot applications.

**Requirements:**

1. `@ConfigurationProperties(prefix="features")` binding a `Map<String, Boolean>`.
2. `FeatureFlagService`:
   - `isEnabled(String flag)` — returns `true` if the flag is enabled.
   - `getAllFlags()` — returns all flag names and their states.
   - Flags can be set via `application.yml` and overridden via environment variables.
3. AOP: `@FeatureGated("flag-name")` annotation.
   - Aspect checks the flag before method execution.
   - Throws `FeatureDisabledException` if the flag is disabled.
4. Actuator: custom endpoint at `/actuator/features`.
   - `@ReadOperation` — list all flags and their states.

**Bonus:** `@WriteOperation` on the endpoint to toggle flags at runtime (POST).

---

## Self-Assessment Checklist

- [ ] I can explain what `@SpringBootApplication` combines and why.
- [ ] I can describe how auto-configuration uses `@Conditional` annotations to decide what to configure.
- [ ] I can create type-safe configuration using `@ConfigurationProperties` with validation.
- [ ] I can list the externalized configuration precedence order.
- [ ] I know how to add and secure Actuator endpoints.
- [ ] I can create a custom health indicator.
- [ ] I understand fat JAR packaging and can containerize an app with Docker.
- [ ] I can write `@SpringBootTest` integration tests and use test slices.
- [ ] I can create a custom starter with `@ConditionalOnMissingBean` back-off.
- [ ] I understand the difference between `@Value` and `@ConfigurationProperties`.

---

## References

- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- *Spring Boot in Action* (Craig Walls)
- [Baeldung — Spring Boot Tutorials](https://www.baeldung.com/spring-boot)
- [Spring Boot Actuator Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Baeldung — Creating a Custom Starter](https://www.baeldung.com/spring-boot-custom-starter)
