---
name: spring-jwt-claims-resolution
description: Implement and review JWT claim validation and claim-to-authority/user resolution in Spring Security resource server applications. Use for issuer/audience validation, JwtAuthenticationConverter mapping, and request-context user resolution patterns.
---

# Spring JWT Claims Resolution

Use this skill when authorization depends on JWT claims and domain user mapping.

## Workflow

1. Validate token trust boundary
- Configure issuer and audience validation explicitly.
- Keep decoder and token validators centralized.

2. Map claims to authorities deliberately
- Define claim-to-authority strategy clearly (`scope`, `roles`, or custom claims).
- Avoid implicit role inference with unclear fallbacks.

3. Resolve domain user context safely
- Keep request-context user resolution explicit and testable.
- Fail predictably when expected claims are missing or invalid.

4. Integrate with method and endpoint security
- Ensure mapped authorities align with `@PreAuthorize` and request matcher rules.
- Test both allow and deny paths for claim variants.

## Quality Gate

- Issuer and audience validation are enforced.
- Claim mapping logic is deterministic and covered by tests.
- User-resolution path handles missing/invalid claims safely.

## Source Baseline

See [references/spring-jwt-claims-resolution-reference.md](references/spring-jwt-claims-resolution-reference.md).
