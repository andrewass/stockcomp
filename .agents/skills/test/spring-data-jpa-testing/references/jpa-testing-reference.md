# Spring Data JPA Testing Reference

## Verification Metadata

- Last verified: 2026-03-28
- Verification method: Context7 against official Spring docs
- Primary Context7 libraries:
  - `/spring-projects/spring-boot/v4.0.3`
  - `/spring-projects/spring-data-jpa`

## Source Baseline

- Spring Boot testing docs (`@DataJpaTest`).
- Spring Data JPA docs for query methods, pagination/sorting, and transaction boundaries.

## Extracted Guidance

1. Use `@DataJpaTest` to isolate JPA repository behavior.
2. Validate custom JPQL/native query methods with real persistence interactions.
3. Verify paging and sorting semantics for repository APIs.
4. Test transactional write behavior for modifying operations.

## Links

- https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html
- https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html
- https://docs.spring.io/spring-data/jpa/reference/jpa/transactions.html
