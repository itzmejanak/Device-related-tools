#!/bin/bash

# Smart Deployment Script for Power Bank Sharing System
# Automatically detects environment and deploys accordingly

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

echo "🚀 Power Bank Sharing System - Smart Deployment"
echo "=============================================="

# Detect environment
print_info "Detecting deployment environment..."

# Check if we're on the production server
if [ -f "/etc/nginx/sites-available/api.chargeghar.com" ] && [ -f "/etc/nginx/sites-available/main.chargeghar.com" ]; then
    ENVIRONMENT="production"
    print_success "Production server detected (srv998476)"
    
    # Check if running as root for production
    if [ "$EUID" -ne 0 ]; then
        print_error "Production deployment requires root privileges"
        echo "Please run: sudo ./deploy.sh"
        exit 1
    fi
    
    # Auto-switch to production environment
    print_info "Switching to production environment..."
    ./switch-env.sh production
    
    # Check if nginx setup is needed
    nginx_setup_needed=false
    ssl_setup_needed=false
    
    if [ ! -f "/etc/nginx/sites-available/powerbank-api.chargeghar.com" ]; then
        print_info "First deployment detected - nginx setup required"
        nginx_setup_needed=true
    fi
    
    if [ ! -f "/etc/letsencrypt/live/powerbank-api.chargeghar.com/fullchain.pem" ]; then
        print_info "SSL certificates not found - setup required"
        ssl_setup_needed=true
    fi
    
    # Build deployment command with appropriate flags
    deploy_cmd="./deploy-production.sh"
    if [ "$nginx_setup_needed" = true ]; then
        deploy_cmd="$deploy_cmd --setup-nginx"
    fi
    if [ "$ssl_setup_needed" = true ]; then
        deploy_cmd="$deploy_cmd --setup-ssl"
    fi
    
    # Deploy with appropriate setup
    print_info "Starting production deployment..."
    if [ "$nginx_setup_needed" = true ] || [ "$ssl_setup_needed" = true ]; then
        print_info "Auto-detected setup needed: nginx=$nginx_setup_needed, ssl=$ssl_setup_needed"
    fi
    
    $deploy_cmd
    
else
    ENVIRONMENT="local"
    print_success "Local development environment detected"
    
    # Auto-switch to local environment
    print_info "Switching to local development environment..."
    ./switch-env.sh local
    
    # Simple local deployment
    print_info "Starting local deployment..."
    
    # Check for port conflicts
    print_info "Checking for port conflicts..."
    ports_to_check=(3307 6380 10030 8084 8083 5672 15672)
    conflicts_found=false
    
    for port in "${ports_to_check[@]}"; do
        if netstat -tuln 2>/dev/null | grep -q ":$port "; then
            print_warning "Port $port is in use"
            conflicts_found=true
        fi
    done
    
    if [ "$conflicts_found" = true ]; then
        print_info "Port conflicts detected - stopping any existing containers..."
        docker compose down 2>/dev/null || true
        
        # Remove orphaned containers
        if docker ps -q --filter "name=powerbank-" | grep -q .; then
            print_info "Removing orphaned powerbank containers..."
            docker ps -q --filter "name=powerbank-" | xargs docker stop 2>/dev/null || true
            docker ps -aq --filter "name=powerbank-" | xargs docker rm 2>/dev/null || true
        fi
    else
        # Stop any existing containers anyway for clean deployment
        docker compose down 2>/dev/null || true
    fi
    
    # Build and start
    if ./build.sh && ./start.sh; then
        print_success "Local deployment completed successfully!"
        
        echo ""
        print_info "🌐 Local Access URLs:"
        echo "   • Backend API: http://localhost:10030"
        echo "   • Cabinet Bind Tool: http://localhost:8084"
        echo "   • Test Tool: http://localhost:8083"
        echo "   • API Documentation: http://localhost:10030/doc.html"
        echo "   • RabbitMQ Management: http://localhost:15672"
        
    else
        print_error "Local deployment failed!"
        exit 1
    fi
fi

echo ""
print_success "🎉 Deployment completed for $ENVIRONMENT environment!"

# Show next steps
echo ""
print_info "📋 Next Steps:"
if [ "$ENVIRONMENT" = "production" ]; then
    echo "   1. Verify DNS records point to this server"
    echo "   2. Test all URLs are accessible"
    echo "   3. Monitor logs: docker compose logs -f"
    echo "   4. Set up monitoring and backups"
else
    echo "   1. Test the application locally"
    echo "   2. Make any needed changes"
    echo "   3. Deploy to production when ready"
fi