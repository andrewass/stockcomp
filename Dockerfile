# Build stage (Maven Wrapper + Java 25)
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app

# Copy Maven wrapper and pom first to improve layer caching for dependencies
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw -DskipTests dependency:go-offline

# Copy source and build application jar
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Runtime stage (Java 25 JRE, non-root)
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

RUN useradd --create-home --uid 10001 --shell /usr/sbin/nologin appuser
USER appuser

COPY --from=builder /app/target/stockcomp-0.0.1-SNAPSHOT.jar app.jar

# Expose app port. Configure debug options explicitly through the runtime environment when needed.
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
