# Spring Scheduling ShedLock Reference

## Verification Metadata

- Last verified: 2026-03-28
- Verification method: Context7 for Spring Boot scheduling + repository evidence for ShedLock usage
- Primary Context7 library:
  - `/spring-projects/spring-boot/v4.0.3`

## Source Baseline

- Spring Boot scheduling patterns (`@Scheduled` lifecycle and runtime behavior).
- Current repository use of ShedLock and scheduler toggles.

## Repository Evidence

- `@EnableScheduling` + `@EnableSchedulerLock` configuration.
- Multiple jobs using `@SchedulerLock`.
- Conditional scheduler toggle via property.

## Links

- https://docs.spring.io/spring-boot/reference/features/task-execution-and-scheduling.html
- https://github.com/lukas-krecan/ShedLock
