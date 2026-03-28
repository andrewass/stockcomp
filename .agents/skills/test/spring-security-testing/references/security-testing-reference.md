# Spring Security Testing Reference

## Verification Metadata

- Last verified: 2026-03-28
- Verification method: Context7 against official Spring Security docs
- Primary Context7 library:
  - `/websites/spring_io_spring-security_reference_7_0`

## Source Baseline

- Spring Security test reference for Servlet stack:
  - MockMvc setup with Spring Security
  - `@WithMockUser` for method security tests
  - request post processors like `jwt()` and `oauth2Login()`

## Extracted Guidance

1. Enable security integration in MockMvc tests.
2. Verify authentication and authorization outcomes explicitly.
3. Use method-security tests for business-layer authorization.
4. Require allow + deny coverage for protected behavior.

## Links

- https://docs.spring.io/spring-security/reference/7.0/servlet/test/mockmvc/setup.html
- https://docs.spring.io/spring-security/reference/7.0/servlet/test/mockmvc/index.html
- https://docs.spring.io/spring-security/reference/7.0/servlet/authorization/method-security.html
