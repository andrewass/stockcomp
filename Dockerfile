# Build stage (with Maven)
FROM maven:3.9.11-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Runtime stage (Distroless JRE)
FROM gcr.io/distroless/java21:nonroot
WORKDIR /app
COPY --from=builder /app/target/stockcomp-0.0.1-SNAPSHOT.jar app.jar

# Expose app port and debug port
EXPOSE 8080 5010

# Set nonroot user (distroless already provides one)
USER nonroot

# Entrypoint: distroless expects exec form
ENTRYPOINT [ "java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5010", "-jar", "/app/app.jar" ]
