---
name: spring-security
description: Design, implement, review, and migrate Spring Security configurations in Spring Boot applications. Use when tasks involve authentication, authorization, SecurityFilterChain or HttpSecurity changes, @EnableMethodSecurity and @PreAuthorize rules, OAuth2/OIDC client or resource server setup, JWT or opaque tokens, CSRF/CORS/session policy decisions, or spring-security-test coverage.
---

# Spring Security

Use this skill to deliver secure, explicit, testable Spring Security configurations without relying on deprecated APIs.

## Workflow

1. Confirm architecture and threat model
- Identify Servlet vs WebFlux, browser vs API clients, stateful session vs stateless token usage, and required identity provider.
- Apply deny-by-default and least-privilege assumptions unless the user explicitly requires broader access.

2. Refresh official docs before coding
- Check the official Spring Security reference for the exact topics needed by the task.
- Focus on configuration DSL, method security, resource server setup, and test support.
- Use [references/spring-security-reference.md](references/spring-security-reference.md) as the baseline checklist and source map.

3. Implement baseline configuration
- Use explicit `SecurityFilterChain` beans.
- Split chains by `securityMatcher` and order them with `@Order` when endpoints have different policies.
- Prefer `requestMatchers` with lambda DSL; do not introduce deprecated matcher APIs.
- Keep CSRF enabled for browser/session flows; disable only for stateless non-browser APIs after documenting the tradeoff.
- For token APIs, set `SessionCreationPolicy.STATELESS`.
- Use a strong `PasswordEncoder` (`DelegatingPasswordEncoder`/`BCryptPasswordEncoder`).

4. Enforce authorization at both layers
- URL layer: map endpoint groups to clear authorities/roles/scopes.
- Method layer: use `@EnableMethodSecurity` and `@PreAuthorize`/`@PostAuthorize` for business rules close to service methods.
- Prefer custom authorization beans over brittle expression-handler subclassing when custom logic is needed.

5. Add focused security tests
- Use `spring-security-test` and prove allowed + denied behavior.
- For Servlet tests, use `@WithMockUser`, `jwt()`, `oauth2Login()`, or request post-processors based on auth mechanism.
- Cover both filter-chain behavior (HTTP) and method-security behavior (service layer).

6. Deliver migration-safe output
- Flag and replace deprecated/legacy patterns:
  - `WebSecurityConfigurerAdapter` style configs
  - `@EnableGlobalMethodSecurity` in new code
  - weak or no-op password encoders
- Include a short migration note when replacing legacy code to preserve behavior intentionally.

## Implementation Patterns

### Multi-chain Servlet configuration (Java)

```java
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    SecurityFilterChain appChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .formLogin(Customizer.withDefaults());
        return http.build();
    }
}
```

### Method security baseline

```java
@Configuration
@EnableMethodSecurity
class MethodSecurityConfig {}

@Service
class OrderService {
    @PreAuthorize("hasAuthority('orders:read')")
    public Order readOrder(String id) { ... }
}
```

## Quality Gate

Before finishing, verify:
- Every exposed route has an explicit authorization rule.
- Session policy matches the authentication model.
- CSRF and CORS decisions are deliberate and documented.
- Tests prove both allow and deny paths.
- No deprecated Spring Security APIs are newly introduced.
