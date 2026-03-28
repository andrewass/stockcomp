---
name: spring-modulith-testing
description: Test modular monolith boundaries and inter-module behavior with Spring Modulith (ApplicationModules.verify, @ApplicationModuleTest modes, Scenario DSL). Use for module-level verification; combine with layer-specific testing skills for controller/repository/security detail tests.
---

# Spring Modulith Testing

Use this skill when a Spring Boot application uses modular boundaries that need explicit verification.

## Scope and Precedence

- This skill is authoritative for module-boundary and inter-module scenario testing.
- Use it alongside other testing skills for layer-specific behavior, without duplicating those layer assertions here.

## Workflow

1. Verify module structure
- Add/maintain a modularity verification test using `ApplicationModules.verify()`.

2. Test modules with controlled bootstrap scope
- Use `@ApplicationModuleTest` and choose mode intentionally:
- `STANDALONE` for strict module isolation.
- `DIRECT_DEPENDENCIES` when direct upstream collaborators are needed.
- `ALL_DEPENDENCIES` for wider integration in modular context.

3. Test event-driven behavior
- Use Modulith testing DSL (`Scenario`) to stimulate behavior and assert published events or state changes.

4. Keep module tests focused
- Validate module contracts and boundaries, not full-system behavior already covered elsewhere.

## Quality Gate

- Module boundaries are verified in CI.
- Critical inter-module event flows have explicit scenario tests.

## Source Baseline

See [references/modulith-testing-reference.md](references/modulith-testing-reference.md).
