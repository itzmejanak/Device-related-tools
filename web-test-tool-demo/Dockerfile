# Multi-stage build for web-test-tool-demo (Nuxt.js PWA)
# Stage 1: Build stage
FROM node:16-alpine AS build

# No build dependencies needed for sass (Dart Sass)

# Set working directory
WORKDIR /app

# Copy package files for better layer caching
COPY package*.json ./

# Install dependencies (including dev dependencies needed for build)
RUN npm config set registry https://registry.npmjs.org/ && \
    npm cache clean --force && \
    npm install --verbose

# Copy source code
COPY . .

# Create Docker environment file
RUN echo "BASE_URL=http://backend:10030" > .env.docker

# Build the application for Docker environment
RUN npm run generate:docker

# Stage 2: Runtime stage
FROM nginx:alpine

# Install envsubst for environment variable substitution
RUN apk add --no-cache gettext

# Create non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /usr/share/nginx/html

# Remove default nginx content
RUN rm -rf /usr/share/nginx/html/*

# Copy built application from build stage
COPY --from=build /app/dist/ ./

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Create configuration template for runtime environment variable substitution
RUN echo 'window.TEST_TOOL_CONFIG = { baseUrl: "${BASE_URL}" };' > /usr/share/nginx/html/config.template.js

# Create startup script for environment variable substitution
RUN echo '#!/bin/sh' > /docker-entrypoint.sh && \
    echo 'envsubst < /usr/share/nginx/html/config.template.js > /usr/share/nginx/html/config.js' >> /docker-entrypoint.sh && \
    echo 'nginx -g "daemon off;"' >> /docker-entrypoint.sh && \
    chmod +x /docker-entrypoint.sh

# Change ownership of files
RUN chown -R appuser:appgroup /usr/share/nginx/html && \
    chown -R appuser:appgroup /var/cache/nginx && \
    chown -R appuser:appgroup /var/log/nginx && \
    chown -R appuser:appgroup /etc/nginx/conf.d && \
    touch /var/run/nginx.pid && \
    chown -R appuser:appgroup /var/run/nginx.pid

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8081

# Environment variables with defaults
ENV BASE_URL=http://localhost:10030

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8081/ || exit 1

# Start nginx with environment variable substitution
ENTRYPOINT ["/docker-entrypoint.sh"]