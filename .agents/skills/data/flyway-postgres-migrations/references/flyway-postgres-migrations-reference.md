# Flyway PostgreSQL Migrations Reference

## Verification Metadata

- Last verified: 2026-03-28
- Verification method: Context7 + official Flyway docs
- Primary Context7 libraries:
  - `/flyway/flyway`
  - `/spring-projects/spring-boot/v4.0.3`

## Source Baseline

- Flyway migration naming conventions for versioned (`V...__...`) and repeatable (`R__...`) scripts.
- Flyway validation and status workflows (`info`, `validate`, `migrate`).
- Spring Boot Flyway integration and execution behavior.

## Extracted Guidance

1. Keep versioned migrations immutable after apply.
2. Use repeatable migrations only for checksum-driven objects (for example views/functions).
3. Prefer additive, staged schema evolution for safer rollouts.
4. Validate migration history before runtime migration in production paths.

## Links

- https://github.com/flyway/flyway/tree/main/documentation/Reference
- https://github.com/flyway/flyway/blob/main/documentation/Reference/Tutorials/Tutorial%20-%20Repeatable%20Migrations.md
- https://docs.spring.io/spring-boot/reference/data/sql.html#data.sql.flyway
