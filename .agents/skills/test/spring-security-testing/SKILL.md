---
name: spring-security-testing
description: Test Spring Security behavior in web and service layers (MockMvc/Web tests, @WithMockUser, jwt(), oauth2Login(), method security allow/deny). Use for security test mechanics; pair with spring-security for architecture/configuration decisions.
---

# Spring Security Testing

Use this skill to prove security behavior, not just happy-path functionality.

## Scope and Precedence

- This skill is authoritative for security test cases and assertions.
- If `spring-security` is also active, let `spring-security` drive architecture/configuration and let this skill drive test mechanics.

## Workflow

1. Test web security paths
- Use MockMvc with Spring Security integration.
- Cover unauthenticated (`401` where expected), unauthorized (`403`), and authorized access.

2. Test authentication simulation correctly
- Use `@WithMockUser` for method security and simple role cases.
- Use request post processors such as `jwt()` and `oauth2Login()` for OAuth2/JWT flows.

3. Test method security explicitly
- Add tests for `@PreAuthorize` / `@PostAuthorize` behavior at service boundary.
- Assert both allow and deny outcomes.

4. Keep claims/authorities mapping visible
- When using JWT scopes/claims, assert mapped authorities drive authorization as expected.

## Quality Gate

- Every protected route/service has both allow and deny tests.
- Security tests assert status/result and not just that code executes.

## Source Baseline

See [references/security-testing-reference.md](references/security-testing-reference.md).
