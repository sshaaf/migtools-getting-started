# Spring Boot Dockerfile demonstrating patterns for Quarkus migration
# 
# Migration to Quarkus:
# - Replace this Dockerfile with Quarkus container image configuration
# - Use quarkus-container-image-jib or quarkus-container-image-docker extension
# - Configure container build in application.properties
# - Leverage Quarkus native image capabilities for smaller, faster containers

FROM openjdk:11-jre-slim

# Metadata
LABEL maintainer="MTA Migration Example"
LABEL description="Spring Boot to Quarkus Migration Demo"
LABEL version="1.0.0"

# Create application directory
WORKDIR /app

# Copy the Spring Boot JAR file
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /app
USER appuser

# Expose the port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=docker

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

# Alternative multi-stage build approach
# FROM maven:3.8-openjdk-11 AS build
# WORKDIR /app
# COPY pom.xml .
# COPY src ./src
# RUN mvn clean package -DskipTests
# 
# FROM openjdk:11-jre-slim
# WORKDIR /app
# COPY --from=build /app/target/*.jar app.jar
# EXPOSE 8080
# ENTRYPOINT ["java", "-jar", "app.jar"]
