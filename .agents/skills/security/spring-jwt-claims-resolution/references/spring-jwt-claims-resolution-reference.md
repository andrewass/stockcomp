# Spring JWT Claims Resolution Reference

## Verification Metadata

- Last verified: 2026-03-28
- Verification method: Context7 against official Spring Security docs
- Primary Context7 library:
  - `/websites/spring_io_spring-security_reference_7_0`

## Source Baseline

- Spring Security 7 resource server JWT validation (`JwtDecoder`, issuer/audience validation).
- JWT authentication conversion (`JwtAuthenticationConverter`) and claim-to-authority mapping patterns.

## Extracted Guidance

1. Enforce issuer + audience checks on every validated token.
2. Keep claim extraction and authority mapping explicit.
3. Align converter output with method/endpoint authorization rules.
4. Treat claim resolution as a first-class tested contract.

## Links

- https://docs.spring.io/spring-security/reference/7.0/servlet/oauth2/resource-server/jwt.html
- https://docs.spring.io/spring-security/reference/7.0/servlet/oauth2/resource-server/index.html
