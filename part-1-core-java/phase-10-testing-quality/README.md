# Phase 10 — Testing & Code Quality

**Duration:** ~2 weeks (22 hours)

---

## Learning Objectives

By the end of this phase you will be able to:

1. Write expressive, maintainable unit tests with **JUnit 5** using lifecycle hooks, parameterized tests, nested test classes, and custom extensions.
2. Isolate units under test with **Mockito** — mocking dependencies, verifying interactions, and capturing arguments.
3. Apply **TDD** (Test-Driven Development) to drive design decisions through the Red-Green-Refactor cycle.
4. Measure and interpret **code coverage** with JaCoCo, and understand why high coverage alone does not guarantee quality.
5. Use **mutation testing** (PITest) to evaluate the true strength of a test suite.
6. Configure **static analysis** tools (SonarQube, SpotBugs, Checkstyle) to enforce coding standards automatically.
7. Write integration tests with **WireMock** and understand the role of **Testcontainers**.

---

## Topics

### 1. JUnit 5 (8 h)

| Sub-topic | Hours | Key concepts |
|-----------|-------|--------------|
| Test lifecycle | 1 | `@BeforeAll`, `@BeforeEach`, `@AfterEach`, `@AfterAll`, execution order, `TestInstance.Lifecycle` |
| Assertions | 1 | `assertEquals`, `assertTrue`, `assertThrows`, `assertAll` (grouped assertions), `assertTimeout`, `assertDoesNotThrow` |
| Parameterized tests | 1.5 | `@ParameterizedTest`, `@ValueSource`, `@CsvSource`, `@CsvFileSource`, `@MethodSource`, `@EnumSource`, custom `ArgumentsProvider` |
| Nested tests | 0.5 | `@Nested` inner classes for logical grouping, shared state considerations, display hierarchy |
| Dynamic tests | 1 | `@TestFactory`, `DynamicTest.dynamicTest()`, `DynamicContainer`, stream-based test generation |
| Test ordering & conditional execution | 0.5 | `@TestMethodOrder`, `@Order`, `@EnabledOnOs`, `@EnabledIf`, `@DisabledIfEnvironmentVariable`, `@DisplayName`, `@DisplayNameGeneration` |
| Extensions | 1 | `@ExtendWith`, `BeforeEachCallback`, `AfterEachCallback`, `ParameterResolver`, `TestExecutionCondition`, custom lifecycle extensions |
| Assumptions | 0.5 | `assumeTrue`, `assumeFalse`, `assumingThat`, skipping vs failing, CI-specific assumptions |

### 2. Mockito (6 h)

| Sub-topic | Hours | Key concepts |
|-----------|-------|--------------|
| Mock vs Spy | 1.5 | `@Mock`, `@Spy`, `Mockito.mock()`, `when(...).thenReturn(...)`, `doReturn(...).when(...)`, partial mocking with spies, default return values |
| Verification | 1 | `verify(mock)`, `times(n)`, `never()`, `atLeastOnce()`, `atMost(n)`, `InOrder` verification, `verifyNoMoreInteractions` |
| Argument matchers | 1.5 | `any()`, `anyString()`, `eq()`, `argThat(predicate)`, `ArgumentCaptor.forClass()`, capturing and inspecting arguments |
| Mocking statics & constructors | 1 | `MockedStatic<T>`, `Mockito.mockStatic()`, `MockedConstruction<T>`, `Mockito.mockConstruction()`, try-with-resources pattern |
| BDD style | 1 | `BDDMockito.given(...)`, `willReturn(...)`, `then(mock).should()`, structuring tests as Given / When / Then |

### 3. Testing Strategies (6 h)

| Sub-topic | Hours | Key concepts |
|-----------|-------|--------------|
| TDD workflow | 2 | Red-Green-Refactor, test-first design, triangulation, baby steps, when *not* to TDD |
| Integration testing | 2 | `@SpringBootTest` preview, Testcontainers for Postgres/Redis/Kafka, WireMock for HTTP dependencies, test slices |
| Code coverage | 1 | JaCoCo setup (Maven plugin), line vs branch vs instruction coverage, why 100 % is not enough, coverage as a *trend* metric |
| Mutation testing | 1 | PITest setup, mutant types (conditional boundary, negate conditional, void method call), kill ratio, surviving mutants reveal weak tests |

### 4. Static Analysis (2 h)

| Tool | Focus |
|------|-------|
| **SonarQube** | Quality gates, code smells, security hotspots, technical debt estimation |
| **SpotBugs** | Bytecode analysis, null-pointer patterns, concurrency bugs |
| **Checkstyle** | Code style enforcement, Google/Sun conventions, custom rules |

---

## References

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/) — official, comprehensive.
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html) — API reference with examples.
- *Pragmatic Unit Testing in Java with JUnit* — Jeff Langr. Practical, pattern-oriented.
- *Growing Object-Oriented Software, Guided by Tests* — Steve Freeman & Nat Pryce. The gold standard on TDD and mock-based design.
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- [PITest Documentation](https://pitest.org/)
- [WireMock User Guide](https://wiremock.org/docs/)

---

## Exercises

### Exercise 1 — TDD Banking System

**Goal:** Build a `BankAccount` class entirely through TDD.

**Requirements:**

- `deposit(BigDecimal amount)` — adds funds. Rejects negative or zero amounts.
- `withdraw(BigDecimal amount)` — removes funds. Rejects if insufficient balance.
- `transfer(BankAccount target, BigDecimal amount)` — atomic move between accounts.
- `getBalance()` — returns current balance.
- `getTransactionHistory()` — returns an unmodifiable list of `Transaction` records.

**Business rules:**

- No negative balance allowed.
- Savings accounts enforce a minimum balance.
- Each operation records a `Transaction` with timestamp, type, and description.
- Interest can be calculated on the current balance.

**Test approach:**

1. Write the test **first** for each feature, watch it fail (Red).
2. Implement the minimum code to pass (Green).
3. Refactor without changing behavior.
4. Use `@ParameterizedTest` with `@CsvSource` for edge cases (negative amounts, zero, `BigDecimal` extremes).
5. Use `@Nested` classes to group tests per feature (Deposit, Withdraw, Transfer, History).

**Bonus:** Implement overdraft protection that notifies a mock `NotificationService` when balance drops below a threshold.

---

### Exercise 2 — Service Layer Testing

**Goal:** Write comprehensive mock-based tests for `UserService`.

**Setup:**

`UserService` depends on:
- `UserRepository` — data access
- `EmailService` — sends emails
- `AuditLogger` — records actions

**Scenarios to test:**

| Flow | Happy path | Error paths |
|------|-----------|-------------|
| Registration | User saved, welcome email sent, audit logged | Duplicate email → exception, invalid data → validation error |
| Password reset | Reset token generated, email sent | Unknown email → specific exception |
| Account deactivation | User marked inactive, farewell email sent, audit logged | Non-existent user → exception |

**Requirements:**

- Use `@Mock` and `@InjectMocks`.
- Verify interaction order where it matters (`InOrder`).
- Capture email arguments with `ArgumentCaptor` and assert content.
- Use BDDMockito style (`given` / `willReturn` / `then`).

**Bonus:** Write a custom JUnit 5 extension that logs the execution time of each test method.

---

### Exercise 3 — Integration Test Suite

**Goal:** Test `ExternalApiClient` against a simulated HTTP API.

**Setup:**

- Use **WireMock** to stub REST endpoints.
- `ExternalApiClient` calls `GET /api/users/{id}`, `POST /api/users`, etc.

**Scenarios:**

| Scenario | Expected behavior |
|----------|-------------------|
| 200 OK with JSON body | Parse and return domain object |
| 404 Not Found | Throw `ResourceNotFoundException` |
| 500 Internal Server Error | Throw `ServerException`, trigger retry |
| Request timeout (simulated delay) | Throw `TimeoutException` after configured threshold |
| Rate limiting (429) | Back off and retry, or throw after max retries |
| Malformed JSON | Throw `ParseException` |

**Bonus:** Use **Testcontainers** to spin up a PostgreSQL container and run repository integration tests against it.

---

## Self-Assessment Checklist

- [ ] I can write a JUnit 5 test class with lifecycle hooks and run it from Maven.
- [ ] I know the difference between `@BeforeAll` and `@BeforeEach` and when to use each.
- [ ] I can write parameterized tests with at least three different sources.
- [ ] I can group related tests using `@Nested` and provide readable `@DisplayName` annotations.
- [ ] I can create dynamic tests with `@TestFactory`.
- [ ] I understand the difference between `@Mock` and `@Spy` and can choose correctly.
- [ ] I can verify method calls, capture arguments, and assert on captured values.
- [ ] I can mock static methods and constructors using `MockedStatic` and `MockedConstruction`.
- [ ] I can write tests in BDD style using `BDDMockito`.
- [ ] I have practiced the Red-Green-Refactor TDD cycle on a non-trivial feature.
- [ ] I can configure JaCoCo in Maven and interpret the coverage report.
- [ ] I understand why 100 % line coverage does not mean bug-free code.
- [ ] I can run PITest and interpret surviving mutants.
- [ ] I can use WireMock to simulate HTTP responses in tests.
- [ ] I can identify at least three rules from SonarQube/SpotBugs/Checkstyle that I find useful.

---

*Next → [Phase 11 — Architecture Patterns](../phase-11-architecture-patterns/README.md)*
