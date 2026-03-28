---
name: spring-webmvc-testing
description: Test Spring MVC controllers with focused slice tests and stable API contract assertions. Use for @RestController request/response behavior, validation errors, and JSON error payloads.
---

# Spring WebMvc Testing

Use this skill for controller contract tests with fast feedback.

## Workflow

1. Use the right scope
- Prefer `@WebMvcTest` for controller-only behavior.
- Use `@SpringBootTest` + `@AutoConfigureMockMvc` only when full wiring is required.

2. Test API contract explicitly
- Assert status, content type, and JSON structure.
- For write endpoints, test both valid and invalid payloads.
- Verify validation failures return expected error shape (prefer `ProblemDetail` fields where used).

3. Include security behavior where relevant
- For secured endpoints, test unauthenticated, unauthorized, and authorized outcomes.
- Use `user()`, `jwt()`, or `oauth2Login()` request post processors as needed.

4. Keep controllers thin in tests too
- Mock service collaborators in slice tests.
- Avoid repository or container setup in controller-only tests.

## Quality Gate

- Every endpoint has at least one success and one failure/validation test.
- Response schema assertions are stable and not overfitted to formatting details.

## Source Baseline

See [references/webmvc-testing-reference.md](references/webmvc-testing-reference.md).
