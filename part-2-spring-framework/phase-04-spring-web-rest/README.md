# Phase 04 — Spring Web & REST

**Duration:** ~2–3 weeks · **Total estimated hours:** 24h

## Learning Objectives

By the end of this phase you will be able to:

1. Explain how Spring MVC processes an HTTP request from the DispatcherServlet to the rendered response.
2. Design and implement fully RESTful APIs that follow HTTP semantics (methods, status codes, content negotiation).
3. Validate incoming data with Bean Validation and return machine-readable error responses using RFC 7807 ProblemDetail.
4. Implement HATEOAS-driven REST resources with discoverable link relations.
5. Use WebClient for non-blocking HTTP communication between services.
6. Build real-time features with WebSocket (STOMP) and Server-Sent Events.
7. Apply API design best practices: versioning, pagination, filtering, and OpenAPI documentation.

---

## Topics

### 1 · Spring MVC Fundamentals — 3 h

| Sub-topic | Key concepts |
|---|---|
| DispatcherServlet | Front-controller pattern, servlet registration, application context hierarchy |
| Handler Mapping | `RequestMappingHandlerMapping`, ant-style paths, path matching strategy |
| Handler Adapters | `RequestMappingHandlerAdapter`, argument resolvers, return-value handlers |
| View Resolution | `ViewResolver`, content negotiation view resolver, forward vs redirect |
| Request Lifecycle | Filter → DispatcherServlet → Interceptor → Handler → View → Response |

### 2 · REST Controllers — 4 h

| Sub-topic | Key concepts |
|---|---|
| `@RestController` | Difference from `@Controller` + `@ResponseBody`, stereotype detection |
| HTTP method mappings | `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping` |
| Path variables | `@PathVariable`, optional path vars, regex in paths |
| Query parameters | `@RequestParam`, required vs optional, default values, multi-value |
| Request body | `@RequestBody`, `HttpMessageConverter`, JSON ↔ Java (Jackson) |
| Response body | `@ResponseBody`, `ResponseEntity<T>`, `@ResponseStatus`, custom headers |

### 3 · Request / Response Handling — 3 h

| Sub-topic | Key concepts |
|---|---|
| Content negotiation | `Accept` header, `produces` / `consumes`, JSON & XML (`jackson-dataformat-xml`) |
| Headers & cookies | `@RequestHeader`, `@CookieValue`, `HttpHeaders` builder |
| File upload | `MultipartFile`, `@RequestPart`, file-size limits, storage strategies |
| File download | `Resource`, `InputStreamResource`, `Content-Disposition` header |
| Streaming responses | `StreamingResponseBody`, `ResponseBodyEmitter`, chunked transfer encoding |

### 4 · Validation — 3 h

| Sub-topic | Key concepts |
|---|---|
| Bean Validation | `@NotNull`, `@NotBlank`, `@Size`, `@Email`, `@Pattern`, `@Min`, `@Max` |
| Triggering validation | `@Valid` on `@RequestBody`, `@Validated` on class level |
| Custom validators | `ConstraintValidator<A, T>`, annotation + implementation |
| Validation groups | Interface markers, `@Validated(OnCreate.class)`, group sequences |
| Error formatting | `MethodArgumentNotValidException`, `BindingResult`, structured error DTOs |

### 5 · Exception Handling — 3 h

| Sub-topic | Key concepts |
|---|---|
| `@ExceptionHandler` | Method-level, controller-specific handlers |
| `@ControllerAdvice` | Global exception handling, `@RestControllerAdvice` shortcut |
| ProblemDetail (RFC 7807) | `type`, `title`, `status`, `detail`, `instance`, extension fields |
| Custom exceptions | Domain-specific exceptions, exception hierarchy |
| Global vs local | Precedence rules, combining controller-level with global advice |

### 6 · HATEOAS — 2 h

| Sub-topic | Key concepts |
|---|---|
| Concept | Hypermedia as the engine of application state, link-driven navigation |
| Spring HATEOAS | `RepresentationModel`, `EntityModel`, `CollectionModel`, `PagedModel` |
| Links | `Link`, `WebMvcLinkBuilder.linkTo()`, self/rel links |
| Link relations | IANA link relations, custom rels, affordances |

### 7 · WebClient — 2 h

| Sub-topic | Key concepts |
|---|---|
| Creating a client | `WebClient.builder()`, base URL, default headers, codecs |
| Requests | `get()`, `post()`, `retrieve()`, `exchangeToMono()` |
| Error handling | `onStatus()`, `WebClientResponseException`, custom error decoders |
| Resilience | Retry (`retryWhen`), timeout (`timeout()`), connection pooling |

### 8 · WebSocket & SSE — 2 h

| Sub-topic | Key concepts |
|---|---|
| WebSocket basics | Full-duplex, handshake, frames, `WebSocketHandler` |
| STOMP over WebSocket | `@MessageMapping`, `SimpMessagingTemplate`, topic subscriptions |
| Server-Sent Events | `SseEmitter`, `text/event-stream`, one-way server → client |
| Use-case selection | WebSocket for bi-directional, SSE for server push, polling as fallback |

### 9 · API Best Practices — 2 h

| Sub-topic | Key concepts |
|---|---|
| Versioning | URI (`/api/v1/`), custom header (`X-API-Version`), content-type (`application/vnd.app.v1+json`) |
| Pagination | Offset-based (`page`, `size`), cursor-based, `Link` headers, `PagedModel` |
| Filtering & sorting | Query parameter conventions, `Specification<T>`, `Sort` |
| Documentation | OpenAPI 3.0, `springdoc-openapi`, `@Operation`, `@Schema`, Swagger UI |

---

## Exercises

### Exercise 1 — Full CRUD REST API (Task Management)

**Goal:** Build a production-quality REST API for task management.

**Domain model:**

```
Task(id, title, description, status, priority, dueDate, assignee)
  - status:   PENDING | IN_PROGRESS | COMPLETED | CANCELLED
  - priority: LOW | MEDIUM | HIGH | CRITICAL
```

**Requirements:**

1. Full CRUD with correct HTTP methods and status codes (201 on create, 204 on delete, etc.).
2. Pagination: `GET /api/tasks?page=0&size=20&sort=dueDate,asc`.
3. Filtering: `?status=PENDING&priority=HIGH&assignee=john`.
4. Search: `?title=deploy` (case-insensitive contains).
5. Bean Validation on `TaskRequest` DTO — `@NotBlank title`, `@Size`, `@NotNull status`, `@Future dueDate`.
6. Global exception handler returning `ProblemDetail` (RFC 7807).
7. HATEOAS: each task includes `self`, `update`, `delete` links; collection includes pagination links.
8. OpenAPI documentation via `springdoc-openapi` — accessible at `/swagger-ui.html`.
9. **Bonus:** Implement cursor-based pagination alongside offset pagination.

---

### Exercise 2 — Real-Time Notification System

**Goal:** Add real-time push capabilities to an application.

**Requirements:**

1. WebSocket endpoint (`/ws/notifications`) using STOMP for live notifications.
2. Users subscribe to personal channel (`/user/{userId}/notifications`) and topic channels (`/topic/tasks`).
3. Server pushes structured events: `TASK_CREATED`, `TASK_COMPLETED`, `USER_MENTIONED`.
4. Each event has `type`, `payload`, `timestamp`, `metadata`.
5. Fallback SSE endpoint (`GET /api/notifications/stream`) for clients that do not support WebSocket.
6. In-memory store of recent notifications per user.
7. **Bonus:** Implement message acknowledgment — clients confirm receipt; unacknowledged messages are redelivered.

---

### Exercise 3 — File Storage Service

**Goal:** Build a robust file upload/download microservice.

**Requirements:**

1. `POST /api/files` — multipart upload; validate max size (10 MB) and allowed types (jpg, png, pdf, docx).
2. Store file metadata (original name, content type, size, upload date, storage path) in a database.
3. Store files on disk at a configurable path (`app.storage.location`).
4. `GET /api/files/{id}` — download with correct `Content-Type` and `Content-Disposition`.
5. `GET /api/files/{id}/metadata` — return metadata JSON.
6. Support HTTP `Range` header for resumable downloads.
7. Generate a placeholder thumbnail URL for image files.
8. **Bonus:** Implement chunked upload — client sends file in parts, server assembles.

---

## Self-Assessment Checklist

- [ ] I can explain the DispatcherServlet request lifecycle from filter to response.
- [ ] I can build a `@RestController` with all HTTP method mappings and proper status codes.
- [ ] I can configure content negotiation for JSON and XML.
- [ ] I can validate request bodies with Bean Validation and return structured errors.
- [ ] I can implement global exception handling with `@RestControllerAdvice` and `ProblemDetail`.
- [ ] I can add HATEOAS links to REST resources using `EntityModel` and `WebMvcLinkBuilder`.
- [ ] I can use `WebClient` to call external APIs with error handling and retries.
- [ ] I can set up WebSocket communication with STOMP and provide an SSE fallback.
- [ ] I can implement offset and cursor-based pagination.
- [ ] I can document a REST API with springdoc-openapi and Swagger UI.
- [ ] I can handle multipart file uploads with size/type validation.
- [ ] I can serve file downloads with resume support (Range headers).

---

## References

| Resource | Link |
|---|---|
| Spring Web MVC — Reference | https://docs.spring.io/spring-framework/reference/web/webmvc.html |
| Spring HATEOAS — Reference | https://docs.spring.io/spring-hateoas/docs/current/reference/html/ |
| Spring WebClient — Reference | https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html |
| springdoc-openapi | https://springdoc.org/ |
| RFC 7807 — Problem Details | https://www.rfc-editor.org/rfc/rfc7807 |
| Baeldung — REST with Spring | https://www.baeldung.com/rest-with-spring-series |
| RESTful Web Services Cookbook | O'Reilly — Subbu Allamaraju |
| Baeldung — WebSocket with Spring | https://www.baeldung.com/spring-websockets-send-message |
