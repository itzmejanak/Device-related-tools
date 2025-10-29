# Dockerfile for web-cabinet-bind (Static HTML/JS Application)
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

# Copy static files
COPY cabinet.html index.html
COPY css/ ./css/
COPY js/ ./js/
COPY layer/ ./layer/

# Create img directory (will be empty for now)
RUN mkdir -p ./img/

# Create configuration template for environment variable substitution
COPY config.template.js ./config.template.js

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

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
EXPOSE 8080

# Environment variables with defaults
ENV BASE_URL=http://localhost:10030
ENV DEFAULT_LANGUAGE=EN

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

# Start nginx with environment variable substitution
ENTRYPOINT ["/docker-entrypoint.sh"]