---
name: spring-data-jpa-testing
description: Test Spring Data JPA repositories and query behavior with focused persistence tests. Use for custom query correctness, paging/sorting behavior, and transactional data integrity checks.
---

# Spring Data JPA Testing

Use this skill for repository-level behavior and persistence correctness.

## Workflow

1. Prefer repository slice tests
- Use `@DataJpaTest` for repository and query behavior.
- Keep tests focused on mapping/query outcomes, not full HTTP flows.

2. Validate custom queries and transactions
- Test derived queries and `@Query` methods against realistic seed data.
- Verify transactional expectations for read/write methods and modifying queries.

3. Validate paging and sorting behavior
- Assert page size, page number, total elements (when using `Page`).
- Assert deterministic sort ordering.

4. Guard against false positives
- Flush/clear between arrange and assert when needed.
- Cover edge cases: empty result, duplicates, boundaries.

## Quality Gate

- Every non-trivial repository method has a behavior test.
- Paging/sorting methods are tested for both normal and edge cases.

## Source Baseline

See [references/jpa-testing-reference.md](references/jpa-testing-reference.md).
