# Rest Controller Reference Notes

This file captures the source baseline used to define the `rest-controller-standards` skill.
Refresh with Context7 before major upgrades.

## Source Baseline

- Spring Boot reference docs, version `v4.0.3`:
  - `web/servlet.adoc` (REST controllers, default error handling, API versioning properties)
  - `web/reactive.adoc` (ProblemDetail RFC 9457 example payload format)
- Spring Framework reference docs, `6.2`:
  - `mvc-ann-rest-exceptions.html` (ProblemDetail, ErrorResponse, ErrorResponseException, ResponseEntityExceptionHandler)
  - `ann-exceptionhandler.html` (`@ExceptionHandler` return types including `ProblemDetail` and `ResponseEntity`)
  - `mvc-controller/ann-methods/requestbody.html` (`@Valid` + `@RequestBody`, `MethodArgumentNotValidException`)
  - `mvc-controller/ann-validation.html` (validation levels and `HandlerMethodValidationException`)

## Current Standards Extracted From Sources

1. REST controller mapping style
- Prefer `@RestController` with composed mapping annotations (`@GetMapping`, `@PostMapping`, etc.).

2. Validation behavior
- `@Valid` and `@Validated` on request body and method parameters trigger Bean Validation.
- Validation failures map to `400` errors (`MethodArgumentNotValidException` / `HandlerMethodValidationException` depending on validation level).

3. Error response model
- Use RFC 9457-compatible error responses via `ProblemDetail`/`ErrorResponse`.
- Centralized error handling is best implemented with `@ControllerAdvice` + `ResponseEntityExceptionHandler`.

4. Exception handler return contracts
- `@ExceptionHandler` methods may return `ProblemDetail`, `ErrorResponse`, `ResponseEntity`, or body objects.

5. API versioning
- Spring Boot 4 exposes MVC API versioning configuration (`spring.mvc.apiversion.*`) including a default version value.
- Keep version extraction strategy centralized and consistent.

## Links

- https://docs.spring.io/spring-boot/reference/web/servlet.html
- https://docs.spring.io/spring-framework/reference/6.2/web/webmvc/mvc-ann-rest-exceptions.html
- https://docs.spring.io/spring-framework/reference/6.2/web/webmvc/mvc-controller/ann-exceptionhandler.html
- https://docs.spring.io/spring-framework/reference/6.2/web/webmvc/mvc-controller/ann-methods/requestbody.html
- https://docs.spring.io/spring-framework/reference/6.2/web/webmvc/mvc-controller/ann-validation.html
