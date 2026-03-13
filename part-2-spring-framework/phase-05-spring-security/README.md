# Phase 05 — Spring Security

**Duration:** ~2–3 weeks · **Total estimated hours:** 26h

## Learning Objectives

By the end of this phase you will be able to:

1. Describe the Spring Security filter chain architecture and how authentication/authorization decisions are made.
2. Implement username/password authentication with `UserDetailsService` and `PasswordEncoder`.
3. Build a stateless JWT authentication system with access and refresh tokens.
4. Configure OAuth2 / OpenID Connect login with external identity providers (Google, GitHub).
5. Apply fine-grained authorization at the URL level and method level using SpEL expressions.
6. Properly configure CORS and CSRF protection for SPAs and APIs.
7. Write security-aware integration tests with `@WithMockUser` and `SecurityMockMvcRequestPostProcessors`.

---

## Topics

### 1 · Security Fundamentals — 2 h

| Sub-topic | Key concepts |
|---|---|
| Authentication vs Authorization | Who are you? vs What can you do? |
| Principal | The currently authenticated user; `Authentication` object |
| Granted Authority | `GrantedAuthority`, role vs authority, `ROLE_` prefix convention |
| Security Context | `SecurityContextHolder`, `SecurityContext`, thread-local strategy |
| Password storage | Hashing vs encryption, adaptive hashing (BCrypt, Argon2, SCrypt) |

### 2 · Spring Security Architecture — 4 h

| Sub-topic | Key concepts |
|---|---|
| `SecurityFilterChain` | Replaces deprecated `WebSecurityConfigurerAdapter`, `HttpSecurity` DSL |
| Filter ordering | `DelegatingFilterProxy`, `FilterChainProxy`, default filter list |
| Authentication flow | `AuthenticationManager` → `AuthenticationProvider` → `UserDetailsService` |
| `SecurityContextHolder` | Thread-local, inheritable thread-local, global strategies |
| Multiple filter chains | `@Order`, matcher-based chains for different URL patterns |
| Custom filters | `OncePerRequestFilter`, `addFilterBefore()` / `addFilterAfter()` |

### 3 · Authentication — 5 h

| Sub-topic | Key concepts |
|---|---|
| `UserDetailsService` | `loadUserByUsername()`, `UserDetails`, `User.builder()` |
| `PasswordEncoder` | `BCryptPasswordEncoder`, `DelegatingPasswordEncoder`, encoding on registration |
| In-memory users | `InMemoryUserDetailsManager` — for prototyping only |
| JDBC authentication | `JdbcUserDetailsManager`, default schema, custom queries |
| Custom provider | `AuthenticationProvider`, `DaoAuthenticationProvider` customization |
| Remember-Me | Persistent token approach vs simple hash, `PersistentTokenRepository` |

### 4 · Authorization — 3 h

| Sub-topic | Key concepts |
|---|---|
| URL-based rules | `requestMatchers()`, `permitAll()`, `hasRole()`, `hasAuthority()`, `authenticated()` |
| Method security | `@EnableMethodSecurity`, `@PreAuthorize`, `@PostAuthorize`, `@Secured` |
| SpEL expressions | `#username == authentication.name`, `hasRole('ADMIN')`, custom root object |
| Custom evaluator | `PermissionEvaluator`, `hasPermission()`, domain object security |
| Role hierarchy | `RoleHierarchy` bean, `ADMIN > MANAGER > USER` |

### 5 · JWT Authentication — 4 h

| Sub-topic | Key concepts |
|---|---|
| Token structure | Header (algorithm), Payload (claims: sub, iat, exp, custom), Signature |
| Token generation | HMAC-SHA256 or RSA, setting expiration, adding custom claims |
| Token validation | Signature verification, expiration check, clock skew tolerance |
| Refresh tokens | Longer TTL, stored server-side, one-time use rotation |
| Stateless sessions | `SessionCreationPolicy.STATELESS`, no JSESSIONID |
| Filter integration | Extract token from `Authorization: Bearer ...`, set `SecurityContext` |

### 6 · OAuth2 / OpenID Connect — 4 h

| Sub-topic | Key concepts |
|---|---|
| OAuth2 grant types | Authorization Code (+ PKCE), Client Credentials, implicit (deprecated), device code |
| Spring OAuth2 Client | `spring-boot-starter-oauth2-client`, `ClientRegistration`, `.oauth2Login()` |
| Resource Server | `spring-boot-starter-oauth2-resource-server`, JWT decoder, opaque token introspection |
| Social login | Google, GitHub — registering OAuth apps, scopes, user-info endpoint |
| User mapping | `OAuth2UserService`, mapping OAuth2 attributes to local `User` entity |
| OIDC | ID token, UserInfo endpoint, standard claims (sub, email, name) |

### 7 · CORS & CSRF — 2 h

| Sub-topic | Key concepts |
|---|---|
| CORS | `CorsConfigurationSource`, allowed origins/methods/headers, `@CrossOrigin` |
| CSRF | `CsrfTokenRepository`, `CookieCsrfTokenRepository`, when to disable (stateless API) |
| SameSite cookies | `Strict`, `Lax`, `None`; interaction with CSRF and cross-site requests |
| Security headers | `Content-Security-Policy`, `X-Frame-Options`, `X-Content-Type-Options` |

### 8 · Security Testing — 2 h

| Sub-topic | Key concepts |
|---|---|
| `@WithMockUser` | Simulating authenticated users in tests, custom roles/authorities |
| `@WithUserDetails` | Loading real `UserDetails` from a bean |
| `SecurityMockMvcRequestPostProcessors` | `csrf()`, `user()`, `httpBasic()`, `jwt()` |
| Integration patterns | Testing protected endpoints, verifying 401/403 responses |

---

## Exercises

### Exercise 1 — JWT Authentication System

**Goal:** Build a complete stateless JWT authentication flow.

**Requirements:**

1. `POST /api/auth/register` — accepts `username`, `email`, `password`; returns created user. Password stored as BCrypt hash.
2. `POST /api/auth/login` — validates credentials, returns `{ accessToken, refreshToken, expiresIn }`.
3. `POST /api/auth/refresh` — accepts `refreshToken`, returns new token pair. Old refresh token is invalidated (one-time use).
4. `POST /api/auth/logout` — blacklists the current access token (store in an in-memory set with TTL).
5. `SecurityFilterChain`: stateless session, `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter`.
6. Protected endpoints: `GET /api/users/me` (any authenticated user), `GET /api/admin/users` (ADMIN only).
7. **Bonus:** Implement token rotation — each refresh issues a new refresh token family; reuse detection revokes the entire family.

---

### Exercise 2 — OAuth2 Social Login

**Goal:** Allow users to log in via Google or GitHub alongside local credentials.

**Requirements:**

1. Configure Spring Security OAuth2 client with Google and GitHub client IDs/secrets (in `application.yml`).
2. On first OAuth2 login, create a local `User` entity from OAuth2 attributes (name, email, avatar URL).
3. If a local user with the same email already exists, link the OAuth2 identity to the existing account.
4. For browser-based access, use session-based authentication after OAuth2 redirect.
5. For API access, issue a JWT after OAuth2 authentication via a success handler.
6. `GET /api/users/me` returns the same response structure regardless of login method.
7. **Bonus:** Add a custom OAuth2 provider (e.g., a company's internal IdP) by implementing a `ClientRegistration`.

---

### Exercise 3 — Role-Based Admin Panel API

**Goal:** Implement hierarchical, resource-level authorization.

**Requirements:**

1. Three roles with hierarchy: `ADMIN > MANAGER > USER`.
2. URL-based rules: `/api/admin/**` requires ADMIN, `/api/management/**` requires MANAGER, others require authentication.
3. Method-level security with `@PreAuthorize`:
   - Users can update only their own profile.
   - Managers can view/edit users within their team.
   - Admins can do everything.
4. Custom `PermissionEvaluator`: `hasPermission(authentication, targetId, 'User', 'edit')` checks ownership or team membership.
5. API key authentication for service-to-service calls: custom `AuthenticationProvider` that validates an `X-API-Key` header.
6. Rate limiting per API key (simple in-memory counter per minute).
7. **Bonus:** Store roles and permissions in the database. Implement `PUT /api/admin/roles/{id}/permissions` to add/remove permissions. Changes take effect without restarting the application (cache eviction).

---

## Self-Assessment Checklist

- [ ] I can draw the Spring Security filter chain and explain the role of each default filter.
- [ ] I can implement `UserDetailsService` backed by a database.
- [ ] I can configure `BCryptPasswordEncoder` and explain why plain hashing is insecure.
- [ ] I can build a `SecurityFilterChain` with stateless JWT authentication.
- [ ] I can generate, validate, and refresh JWT tokens using a library like jjwt.
- [ ] I can configure OAuth2 login with Google or GitHub.
- [ ] I can map OAuth2 user attributes to a local user entity.
- [ ] I can use `@PreAuthorize` with SpEL to enforce method-level authorization.
- [ ] I can implement a custom `PermissionEvaluator` for resource-level access control.
- [ ] I can configure CORS for a specific set of allowed origins and methods.
- [ ] I can explain when to enable/disable CSRF protection.
- [ ] I can write tests with `@WithMockUser` and verify 401/403 responses.

---

## References

| Resource | Link |
|---|---|
| Spring Security — Reference | https://docs.spring.io/spring-security/reference/ |
| Spring Security in Action | Manning — Laurentiu Spilca |
| OAuth 2.0 — RFC 6749 | https://www.rfc-editor.org/rfc/rfc6749 |
| JWT — RFC 7519 | https://www.rfc-editor.org/rfc/rfc7519 |
| Baeldung — Spring Security | https://www.baeldung.com/security-spring |
| Spring OAuth2 Login Guide | https://docs.spring.io/spring-security/reference/servlet/oauth2/login.html |
| OWASP Authentication Cheatsheet | https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html |
