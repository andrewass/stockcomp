# Spring WebMvc Testing Reference

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
- https://docs.spring.io/spring-framework/reference/6.2/web/webmvc/mvc-controller/ann-methods/requestbody.html
- https://docs.spring.io/spring-framework/reference/6.2/web/webmvc/mvc-ann-rest-exceptions.html
- https://docs.spring.io/spring-security/reference/6.5/servlet/test/mockmvc/index.html
