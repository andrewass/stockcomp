# Spring Integration Testcontainers Reference

## Verification Metadata

- Last verified: 2026-03-28
- Verification method: Context7 against official Spring Boot docs
- Primary Context7 library:
  - `/spring-projects/spring-boot/v4.0.3`

## Source Baseline

- Spring Boot testing docs for full-context integration testing.
- Spring Boot dev-services / Testcontainers integration documentation.

## Extracted Guidance

1. Use `@SpringBootTest` for full application integration scenarios.
2. Use Testcontainers when database/external dependency realism matters.
3. Keep integration coverage focused on cross-layer behavior.
4. Prefer deterministic fixtures and stable container lifecycle handling.

## Links

- https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html
- https://docs.spring.io/spring-boot/reference/features/dev-services.html
