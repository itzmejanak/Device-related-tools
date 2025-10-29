#!/bin/bash

# Build script for Power Bank Sharing System
set -e

echo "🚀 Building Power Bank Sharing System..."

# Check if Docker and Docker Compose are installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

if ! docker compose version &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Create .env file if it doesn't exist
if [ ! -f .env ]; then
    echo "📝 Creating .env file from template..."
    cp .env.example .env
    echo "⚠️  Please edit .env file with your configuration before running the system."
fi

# Build all services
echo "🔨 Building Docker images..."
docker compose build --no-cache

echo "✅ Build completed successfully!"
echo ""
echo "📋 Next steps:"
echo "1. Review .env file configuration"
echo "2. Run: ./start.sh to start the system"
echo ""

# Show appropriate URLs based on environment
if [ -f ".env" ] && grep -q "chargeghar.com" .env; then
    echo "🌐 Production URLs (after deployment):"
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
    echo "   - Cabinet Bind Tool: http://localhost:8081"
    echo "   - Test Tool: http://localhost:8083"
    echo "   - API Documentation: http://localhost:10030/doc.html"
fi