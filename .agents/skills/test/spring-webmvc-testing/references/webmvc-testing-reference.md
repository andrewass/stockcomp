# Spring WebMvc Testing Reference

## Verification Metadata

- Last verified: 2026-03-28
- Verification method: Context7 against official Spring docs
- Primary Context7 libraries:
  - `/spring-projects/spring-boot/v4.0.3`
  - `/spring-projects/spring-framework/v7.0.5`
  - `/websites/spring_io_spring-security_reference_7_0`

## Source Baseline

- Spring Boot testing docs (`@WebMvcTest`, MockMvc, MockMvcTester).
- Spring Framework MVC validation and REST exception docs (`@Valid`, `MethodArgumentNotValidException`, `HandlerMethodValidationException`, `ProblemDetail`).
- Spring Security MockMvc testing docs for secured endpoints.

## Extracted Guidance

1. `@WebMvcTest` is the default for focused MVC endpoint tests.
2. Use MockMvc assertions for status, body, and headers.
3. Validate request payload handling and 400 responses for invalid inputs.
4. Prefer structured error responses via `ProblemDetail`/`ErrorResponse`.

## Links

- https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html
- https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/requestbody.html
- https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html
- https://docs.spring.io/spring-security/reference/7.0/servlet/test/mockmvc/index.html
