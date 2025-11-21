#!/bin/bash

echo "🔧 Deploying MQTT Fix to Production..."

# Stop backend container
echo "⏸️  Stopping backend container..."
docker-compose stop backend

# Rebuild backend
echo "🔨 Rebuilding backend..."
docker-compose build backend

# Start backend
echo "▶️  Starting backend..."
docker-compose up -d backend

# Wait for startup
echo "⏳ Waiting for backend to start..."
sleep 10

# Check logs
echo "📋 Checking logs..."
docker-compose logs --tail=50 backend

echo "✅ Deployment complete!"
echo "🧪 Test with: curl https://powerbank-api.chargeghar.com/communication/ybt/check-all?scanNo=864601069946994"
