# Spring Security Testing Reference

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

- https://docs.spring.io/spring-security/reference/6.5/servlet/test/mockmvc/setup.html
- https://docs.spring.io/spring-security/reference/6.5/servlet/test/mockmvc/index.html
- https://docs.spring.io/spring-security/reference/6.5/servlet/authorization/method-security.html
