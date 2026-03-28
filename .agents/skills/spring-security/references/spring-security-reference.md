# Spring Security Reference Notes

This file captures the official-source baseline used when authoring this skill. Refresh against official Spring documentation before major changes.

## Verification Metadata

- Last verified: 2026-03-28
- Verification method: Context7 against official Spring Security documentation
- Primary Context7 library:
  - `/websites/spring_io_spring-security_reference_7_0`

## Source Baseline

- Preferred docs: official Spring Security 7.0 reference pages.
- Reason: best coverage for current configuration and migration patterns.

## Core Patterns to Reuse

1. `SecurityFilterChain` + lambda DSL
- Define one or more `SecurityFilterChain` beans.
- Use `securityMatcher` and `@Order` when different URL groups need different auth models.

2. Stateless APIs
- For token-based APIs, use `SessionCreationPolicy.STATELESS`.

3. Password storage
- Prefer `DelegatingPasswordEncoder` or BCrypt-backed encoding.
- Avoid weak digest or no-op encoders in real systems.

4. Method security
- Use `@EnableMethodSecurity`.
- Apply `@PreAuthorize` / `@PostAuthorize` near service logic.
- For migration, replace `@EnableGlobalMethodSecurity` patterns in new code.

5. OAuth2 Resource Server
- Configure `oauth2ResourceServer` with either JWT or opaque token support.
- For scope checks, use scope/authority-based authorization policies.

6. Testing
- Use `spring-security-test` helpers in MockMvc/Web tests.
- Common helpers include user mocks and OAuth2/JWT post-processors.

## Refresh Prompts

Use these prompt patterns to refresh implementation details from official docs:

1. Configuration DSL and deprecations
- `Summarize modern Servlet configuration patterns for SecurityFilterChain, requestMatchers, CSRF/CORS, session policy, and deprecations.`

2. Method security and migration
- `Provide official patterns for @EnableMethodSecurity, @PreAuthorize/@PostAuthorize, and migration from @EnableGlobalMethodSecurity.`

3. OAuth2 resource server and tests
- `Provide official OAuth2 resource server patterns (JWT and opaque tokens) and spring-security-test examples for MockMvc.`

## Official Documentation URLs

- https://docs.spring.io/spring-security/reference/7.0/servlet/configuration/java.html
- https://docs.spring.io/spring-security/reference/7.0/servlet/authorization/method-security.html
- https://docs.spring.io/spring-security/reference/7.0/servlet/oauth2/resource-server/index.html
- https://docs.spring.io/spring-security/reference/7.0/servlet/test/mockmvc/index.html
