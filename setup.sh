#!/bin/bash

# Setup script for Power Bank Sharing System
# Makes all scripts executable and verifies the setup

echo "🔧 Setting up Power Bank Sharing System..."

# Make all shell scripts executable
echo "📝 Making scripts executable..."
chmod +x *.sh
chmod +x verify-deployment.sh 2>/dev/null || true

echo "✅ All scripts are now executable!"
echo ""

# Show available scripts
echo "📋 Available Scripts:"
echo "==================="
echo ""
echo "🚀 Deployment Scripts:"
echo "   ./deploy.sh              - Smart deployment (auto-detects environment)"
echo "   ./deploy-production.sh   - Production deployment with nginx/SSL"
echo "   ./build.sh               - Build Docker images"
echo "   ./start.sh               - Start all services"
echo "   ./stop.sh                - Stop all services"
echo ""
echo "⚙️  Configuration Scripts:"
echo "   ./switch-env.sh local    - Switch to local development"
echo "   ./switch-env.sh production - Switch to production environment"
echo "   ./verify-deployment.sh   - Verify system readiness"
echo ""
echo "📖 Alternative Execution Methods:"
echo "================================"
echo ""
echo "If scripts are not executable, you can run them with:"
echo "   bash deploy.sh           (instead of ./deploy.sh)"
echo "   bash build.sh            (instead of ./build.sh)"
echo "   bash start.sh            (instead of ./start.sh)"
echo ""
echo "🎯 Quick Start:"
echo "=============="
echo ""
echo "For Local Development:"
echo "   ./deploy.sh"
echo ""
echo "For Production (on srv998476):"
echo "   sudo ./deploy.sh"
echo ""
echo "✅ Setup completed! You're ready to deploy."