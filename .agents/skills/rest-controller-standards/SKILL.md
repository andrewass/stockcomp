---
name: rest-controller-standards
description: Standardize Spring REST controllers to current recommended practices. Use when creating or refactoring @RestController endpoints, request mapping, validation, RFC 9457 ProblemDetail errors, pagination, and API versioning in Spring Boot/Spring MVC applications.
---

# Rest Controller Standards

Use this skill to make Spring REST controllers consistent, testable, and aligned with current Spring recommendations.

## Workflow

1. Confirm baseline
- Confirm Spring Boot and web stack (Servlet MVC vs WebFlux).
- For this skill, use Spring Boot 4.x + Spring MVC defaults unless the user asks otherwise.

2. Refresh source guidance before large changes
- Use [references/rest-controller-reference.md](references/rest-controller-reference.md) as baseline.
- Re-check official docs via Context7 when changing error model, versioning, or validation behavior.

3. Apply controller design rules
- Use `@RestController` and composed mapping annotations (`@GetMapping`, `@PostMapping`, etc.).
- Keep controllers thin; move business logic to services.
- Use DTOs at the boundary. Do not expose persistence entities directly from controller methods.
- Use `ResponseEntity<T>` when you must control status or headers. Otherwise return DTO body types directly.

4. Apply validation rules
- Use `@Valid` or `@Validated` on `@RequestBody` inputs.
- Use parameter constraints deliberately (`@NotBlank`, `@Min`, etc.) and handle method-level validation consistently.
- Treat validation failures as client errors (`400`) with structured error payloads.

5. Standardize error handling
- Prefer RFC 9457 responses through `ProblemDetail` / `ErrorResponse`.
- Centralize exception mapping in `@RestControllerAdvice`, typically by extending `ResponseEntityExceptionHandler`.
- Keep error payload shape stable across endpoints.

6. Apply pagination and versioning conventions
- For list endpoints, use `Pageable`/`Sort` with explicit defaults and max page size guardrails.
- Choose one API versioning strategy per API surface (header/path/media type), and configure centrally.
- If using Spring Boot API versioning support, keep default version explicit in configuration.

7. Verify
- Add/update web tests for happy path and error path.
- Verify validation and exception flows return the expected `ProblemDetail` structure.

## Team Checklist

Before finishing, confirm:
- Every controller method has explicit request/response contract DTOs.
- Validation is applied on all write endpoints.
- `ProblemDetail` is used consistently for error responses.
- Pagination defaults and limits are documented and enforced.
- Versioning strategy is explicit and not mixed ad hoc across controllers.
