#!/bin/bash

# Start script for Power Bank Sharing System
# Note: Not using 'set -e' to allow graceful handling of service startup timing

echo "🚀 Starting Power Bank Sharing System..."

# Check if .env file exists
if [ ! -f .env ]; then
    echo "❌ .env file not found. Please run ./build.sh first."
    exit 1
fi

# Start infrastructure services first
echo "🔧 Starting infrastructure services..."
docker compose up -d mysql redis rabbitmq

# Wait for infrastructure services to be healthy
echo "⏳ Waiting for infrastructure services to be ready..."

# Simple wait for services to start
echo "   Waiting 30 seconds for services to initialize..."
sleep 30

# Check if services are running
echo "   Checking service status..."
if docker compose ps mysql | grep -q "Up"; then
    echo "   ✅ MySQL is running"
else
    echo "   ⚠️  MySQL may still be starting"
fi

if docker compose ps redis | grep -q "Up"; then
    echo "   ✅ Redis is running"
else
    echo "   ⚠️  Redis may still be starting"
fi

if docker compose ps rabbitmq | grep -q "Up"; then
    echo "   ✅ RabbitMQ is running"
else
    echo "   ⚠️  RabbitMQ may still be starting"
fi

echo "✅ Infrastructure services check completed"

# Start application services
echo "🚀 Starting application services..."
if docker compose up -d backend web-cabinet-bind web-test-tool; then
    echo "✅ Application services started successfully"
else
    echo "⚠️  Some application services may have had issues starting"
    echo "   Check logs with: docker compose logs -f"
fi

# Give services a moment to start
echo "⏳ Allowing services to initialize..."
sleep 10

# Show status
echo "📊 Service Status:"
docker compose ps

# Check if critical services are running
echo ""
echo "🔍 Service Health Check:"
services=("mysql" "redis" "rabbitmq" "backend" "web-cabinet-bind" "web-test-tool")
all_healthy=true

for service in "${services[@]}"; do
    if docker compose ps $service | grep -q "Up"; then
        if docker compose ps $service | grep -q "healthy"; then
            echo "   ✅ $service: Running and healthy"
        else
            echo "   🔄 $service: Running (health check in progress)"
        fi
    else
        echo "   ❌ $service: Not running"
        all_healthy=false
    fi
done

if [ "$all_healthy" = true ]; then
    echo ""
    echo "✅ All services are running!"
else
    echo ""
    echo "⚠️  Some services may need more time to start"
    echo "   Monitor with: docker compose logs -f"
fi

echo ""
echo "✅ System started successfully!"
echo ""

# Detect environment and show appropriate URLs
if [ -f ".env" ] && grep -q "chargeghar.com" .env; then
    echo "🌐 Production Access URLs:"
    BACKEND_URL=$(grep "BACKEND_URL=" .env | cut -d'=' -f2)
    CABINET_URL=$(grep "CABINET_BIND_URL=" .env | cut -d'=' -f2)
    TEST_URL=$(grep "TEST_TOOL_URL=" .env | cut -d'=' -f2)
    
    echo "   - Backend API: ${BACKEND_URL:-https://powerbank-api.chargeghar.com}"
    echo "   - Cabinet Bind Tool: ${CABINET_URL:-https://cabinet.chargeghar.com}"
    echo "   - Test Tool: ${TEST_URL:-https://test.chargeghar.com}"
    echo "   - API Documentation: ${BACKEND_URL:-https://powerbank-api.chargeghar.com}/doc.html"
else
    echo "🌐 Local Development URLs:"
    echo "   - Backend API: http://localhost:10030"
    echo "   - Cabinet Bind Tool: http://localhost:8084"
    echo "   - Test Tool: http://localhost:8083"
    echo "   - API Documentation: http://localhost:10030/doc.html"
    echo "   - RabbitMQ Management: http://localhost:15672"
fi
echo ""
echo "📝 Logs:"
echo "   - View all logs: docker compose logs -f"
echo "   - View backend logs: docker compose logs -f backend"
echo "   - View specific service: docker compose logs -f <service-name>"