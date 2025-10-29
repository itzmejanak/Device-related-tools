#!/bin/bash

# Stop script for Power Bank Sharing System
set -e

echo "🛑 Stopping Power Bank Sharing System..."

# Stop all services
docker compose down

echo "✅ System stopped successfully!"
echo ""
echo "📝 To remove all data (including database):"
echo "   docker compose down -v"
echo ""
echo "📝 To remove all images:"
echo "   docker compose down --rmi all"