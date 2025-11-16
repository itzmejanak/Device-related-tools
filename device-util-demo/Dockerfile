# Multi-stage build for device-util-demo (Spring Boot Backend)
# Stage 1: Build stage
FROM maven:3.8.6-openjdk-8-slim AS build

# Set working directory
WORKDIR /app

# Copy parent pom.xml first for better layer caching
COPY pom.xml .

# Copy module pom.xml files
COPY utils-common/pom.xml ./utils-common/
COPY brezze-communication/pom.xml ./brezze-communication/

# Skip dependency download due to platform-specific artifacts
# Dependencies will be downloaded during build

# Copy source code
COPY utils-common/src ./utils-common/src
COPY brezze-communication/src ./brezze-communication/src

# Build the application (skip tests for faster build, enable if needed)
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime stage
FROM eclipse-temurin:8-jre-alpine

# Install required packages for potential debugging and health checks
RUN apk add --no-cache curl tzdata

# Set timezone (adjust as needed)
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Create non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Create logs directory with proper permissions
RUN mkdir -p /app/logs && \
    chown -R appuser:appgroup /app

# Copy the built JAR from build stage
COPY --from=build /app/brezze-communication/target/brezze-communication-1.0.jar app.jar

# Change ownership of the JAR file
RUN chown appuser:appgroup app.jar

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 10030

# Environment variables for configuration
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=10030

# Database configuration
ENV DB_HOST=mysql
ENV DB_PORT=3306
ENV DB_NAME=db_share
ENV DB_USERNAME=root
ENV DB_PASSWORD=123456

# Redis configuration
ENV REDIS_HOST=redis
ENV REDIS_PORT=6379
ENV REDIS_PASSWORD=
ENV REDIS_DATABASE=3

# RabbitMQ configuration
ENV RABBITMQ_HOST=rabbitmq
ENV RABBITMQ_PORT=5672
ENV RABBITMQ_USERNAME=admin
ENV RABBITMQ_PASSWORD=admin
ENV RABBITMQ_VHOST=/

# EMQX MQTT Configuration
ENV MQTT_BROKER=nf0e7f22.ala.dedicated.aws.emqxcloud.com
ENV MQTT_PORT=1883
ENV MQTT_USERNAME=chargeghar
ENV MQTT_PASSWORD=5060
ENV MQTT_CLIENT_ID=iotdemo-server
ENV MQTT_SSL=false

# EMQX Management API Configuration
ENV EMQX_API_URL=https://nf0e7f22.ala.dedicated.aws.emqxcloud.com:8443/api/v5
ENV EMQX_API_KEY=o138f3b1feb110b0
ENV EMQX_API_SECRET=IDwov-pD9dqQcC9sHlsHAXj!cwGdi.wZ

# Product Key and Topic Configuration
ENV PRODUCT_KEY=powerbank
ENV TOPIC_TYPE=true

# YBT configuration (Legacy - disabled by default)
ENV YBT_ENABLE=false
ENV YBT_ACCESS_KEY=
ENV YBT_ACCESS_SECRET=
ENV YBT_UID=
ENV YBT_REGION_ID=cn-shanghai
ENV YBT_PRODUCT_KEY=
ENV YBT_CONSUMER_GROUP_ID=

# JVM options for container environment
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:+UseStringDeduplication"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${SERVER_PORT}/api/common/config/pre_auth_amount || exit 1

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]