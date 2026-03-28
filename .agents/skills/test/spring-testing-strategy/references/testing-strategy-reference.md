# Spring Testing Strategy Reference

This reference captures the baseline used to define testing scope decisions.

## Verification Metadata

- Last verified: 2026-03-28
- Verification method: Context7 against official Spring docs
- Primary Context7 libraries:
  - `/spring-projects/spring-boot/v4.0.3`
  - `/websites/spring_io_spring-security_reference_7_0`
  - `/spring-projects/spring-modulith`

## Source Baseline

- Spring Boot 4.0.3 reference testing chapter:
  - `@WebMvcTest` and MockMvc guidance
  - `@DataJpaTest` guidance
  - `@SpringBootTest` usage patterns
- Spring Security testing reference:
  - MockMvc and method security testing patterns
- Spring Modulith testing docs:
  - module boundary verification and module integration tests

## Core Guidance

1. Prefer narrow test slices first.
2. Use full application context only for cross-layer integration risks.
3. Verify both happy path and failure path.
4. For secured behavior, always test both allowed and denied access.

## Links

- https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html
- https://docs.spring.io/spring-security/reference/7.0/servlet/test/mockmvc/index.html
- https://docs.spring.io/spring-security/reference/7.0/servlet/authorization/method-security.html
- https://docs.spring.io/spring-modulith/reference/testing.html
