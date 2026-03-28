---
name: spring-scheduling-shedlock
description: Implement and review scheduled job patterns in Spring Boot with ShedLock. Use for @Scheduled jobs, lock configuration, idempotency, transaction boundaries, and operational safety.
---

# Spring Scheduling with ShedLock

Use this skill for scheduled background processing in distributed deployments.

## Workflow

1. Define job contract
- Clarify trigger cadence, expected duration, and failure behavior.
- Make job logic idempotent and re-entrant.

2. Configure scheduler and lock behavior
- Use `@Scheduled` for cadence.
- Use `@SchedulerLock` with stable lock names.
- Set lock duration with realistic safety margins.

3. Control transactional scope
- Keep transactions short and scoped to the minimal unit of work.
- Prefer explicit propagation for state transitions that must commit independently.

4. Add operational guardrails
- Gate schedulers by property for safe local/test control.
- Log start/end/error with enough identifiers for debugging.
- Add metrics for run count, failure count, and duration where feasible.

## Quality Gate

- Job can run repeatedly without corrupting state.
- Lock name and lock duration are deliberate.
- Concurrency behavior under multiple app instances is defined.
- Failure and retry behavior is observable and testable.

## Source Baseline

See [references/spring-scheduling-shedlock-reference.md](references/spring-scheduling-shedlock-reference.md).
