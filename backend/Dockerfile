# ==========================================
# Stage 1: Build JAR using Maven & JDK 21
# ==========================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Pre-fetch dependencies to leverage Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and build production JAR (skipping tests for faster build)
COPY src ./src
RUN mvn clean package -DskipTests -B

# ==========================================
# Stage 2: Production Lightweight Runtime
# ==========================================
FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app

# Install curl for healthcheck capability
RUN apk add --no-cache curl

# Create directory for persistent file uploads (KTP, selfie, loan documents)
RUN mkdir -p /app/uploads

# Copy compiled JAR from builder stage
COPY --from=builder /build/target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8080

# Configure JVM flags optimized for container memory limits
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
