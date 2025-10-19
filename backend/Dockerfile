# Multi-stage build for Spring Boot backend
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Copy Maven files
COPY pom.xml .
COPY .mvn/ .mvn/

# Build the application
COPY src/ src/
COPY mvnw .
COPY .mvn/ .mvn/
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests


# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Create non-root user for security
RUN useradd -m -u 1001 appuser

# Copy JAR from builder
COPY --from=builder /app/target/cloud-security-platform-1.0.0.jar app.jar

# Change ownership
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD java -cp app.jar org.springframework.boot.loader.JarLauncher || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]