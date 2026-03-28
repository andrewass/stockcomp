---
name: spring-integration-testcontainers
description: Build reliable full-context Spring integration tests with real infrastructure (Testcontainers) to validate cross-layer HTTP, persistence, transactions, and migrations. Use when slice tests are insufficient.
---

# Spring Integration Testcontainers

Use this skill for cross-layer tests where slice tests are insufficient.

## Scope and Precedence

- This skill is authoritative for full-context integration test patterns and container realism.
- It does not replace slice-test guidance; use specialized slice skills for controller/repository/security detail assertions.

## Workflow

1. Use full context intentionally
- Use `@SpringBootTest` for end-to-end application wiring.
- Keep the integration suite focused on flows that cross boundaries.

2. Use real infrastructure
- Use Testcontainers for database and critical dependencies.
- Ensure schema migrations run in tests when migrations are part of runtime behavior.

3. Validate complete behavior
- Assert HTTP contract, persistence side effects, and rollback/transaction semantics.
- Verify error paths with realistic data and constraint failures.

4. Keep tests deterministic
- Isolate test data setup and teardown.
- Avoid shared mutable global state across tests.

## Quality Gate

- Integration tests cover only scenarios that need full wiring.
- Containerized tests are stable, repeatable, and not flaky under parallel execution.

## Source Baseline

See [references/integration-testcontainers-reference.md](references/integration-testcontainers-reference.md).
