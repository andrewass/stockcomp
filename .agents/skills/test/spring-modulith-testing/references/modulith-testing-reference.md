# Spring Modulith Testing Reference

## Source Baseline

- Spring Modulith testing docs and examples:
  - `ApplicationModules.verify()` for structural verification
  - `@ApplicationModuleTest` bootstrap modes
  - `Scenario` DSL for event-driven module tests

## Extracted Guidance

1. Keep one explicit architecture verification test for module boundaries.
2. Prefer the narrowest `@ApplicationModuleTest` mode that proves behavior.
3. Use scenario-based tests for module events and asynchronous flows.

## Links

- https://docs.spring.io/spring-modulith/reference/testing.html
- https://github.com/spring-projects/spring-modulith
