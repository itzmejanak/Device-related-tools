#!/bin/bash

# Production Deployment Script for Power Bank Sharing System
# Usage: ./deploy-production.sh [--setup-nginx] [--setup-ssl]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print status
print_status() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✅ $2${NC}"
    else
        echo -e "${RED}❌ $2${NC}"
        return 1
    fi
}

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

echo "🚀 Power Bank Sharing System - Production Deployment"
echo "=================================================="

# Check if running as root (needed for nginx setup)
if [[ "$1" == "--setup-nginx" ]] || [[ "$2" == "--setup-nginx" ]] || [[ "$1" == "--setup-ssl" ]] || [[ "$2" == "--setup-ssl" ]]; then
    if [ "$EUID" -ne 0 ]; then
        echo -e "${RED}❌ Please run with sudo for nginx/ssl setup${NC}"
        echo "Usage: sudo ./deploy-production.sh --setup-nginx --setup-ssl"
        exit 1
    fi
fi

# Step 1: Verify environment
echo ""
echo "📋 Step 1: Environment Verification"
echo "=================================="

# Check Docker
if command -v docker &> /dev/null; then
    print_status 0 "Docker is installed"
else
    print_status 1 "Docker not found"
    exit 1
fi

# Check Docker Compose
if docker compose version &> /dev/null; then
    print_status 0 "Docker Compose is installed"
else
    print_status 1 "Docker Compose not found"
    exit 1
fi

# Check if .env exists
if [ -f ".env" ]; then
    print_status 0 ".env file exists"
else
    print_warning ".env file not found, creating from template"
    cp .env.example .env 2>/dev/null || echo "# Please configure .env file" > .env
fi

# Step 2: Check port conflicts
echo ""
echo "🔍 Step 2: Port Conflict Check"
echo "============================="

ports_to_check=(3307 6380 10030 8084 8083 5672 15672)
conflicts_found=false

for port in "${ports_to_check[@]}"; do
    if netstat -tuln 2>/dev/null | grep -q ":$port "; then
        print_warning "Port $port is in use"
        conflicts_found=true
    else
        print_status 0 "Port $port available"
    fi
done

if [ "$conflicts_found" = true ]; then
    print_warning "Some ports are in use. Checking if they are from our containers..."
    
    # Check if conflicts are from our own containers
    our_containers_running=false
    if docker compose ps --quiet 2>/dev/null | grep -q .; then
        our_containers_running=true
        print_info "Found existing containers from this project"
        
        echo "Options:"
        echo "  1. Stop existing containers and redeploy (recommended)"
        echo "  2. Continue anyway (may cause conflicts)"
        echo "  3. Exit and handle manually"
        echo ""
        echo "Choose option (1/2/3): "
        read -r response
        
        case $response in
            1)
                print_info "Stopping existing containers..."
                docker compose down 2>/dev/null || true
                print_status 0 "Existing containers stopped"
                ;;
            2)
                print_warning "Continuing with potential conflicts..."
                ;;
            3|*)
                print_info "Exiting. You can manually stop containers with: docker compose down"
                exit 1
                ;;
        esac
    else
        print_warning "Port conflicts detected from external processes"
        echo "Continue anyway? (y/N)"
        read -r response
        if [[ ! "$response" =~ ^[Yy]$ ]]; then
            exit 1
        fi
    fi
fi

# Step 3: Setup Nginx (if requested)
if [[ "$1" == "--setup-nginx" ]] || [[ "$2" == "--setup-nginx" ]]; then
    echo ""
    echo "🌐 Step 3: Nginx Configuration Setup"
    echo "==================================="
    
    # Copy nginx configurations
    if [ -d "/etc/nginx/sites-available" ]; then
        print_info "Installing nginx configurations..."
        
        # Copy main configuration (single domain architecture)
        cp nginx/sites-available/powerbank-api.chargeghar.com /etc/nginx/sites-available/ && print_status 0 "Installed powerbank-api.chargeghar.com config (single domain)" || print_warning "Could not copy powerbank-api config"
        
        # Enable main site (single domain serves all tools)
        ln -sf /etc/nginx/sites-available/powerbank-api.chargeghar.com /etc/nginx/sites-enabled/ && print_status 0 "Enabled powerbank-api.chargeghar.com (single domain)" || print_warning "Could not enable powerbank-api"
        
        # Test nginx configuration
        if nginx -t 2>/dev/null; then
            print_status 0 "Nginx configuration is valid"
            systemctl reload nginx && print_status 0 "Nginx reloaded successfully" || print_warning "Could not reload nginx"
        else
            print_warning "Nginx configuration test failed - please check manually"
        fi
    else
        print_warning "Nginx not found or not installed"
    fi
else
    echo ""
    echo "🌐 Step 3: Nginx Configuration (Skipped)"
    echo "======================================"
    print_info "Nginx configurations already exist - skipping setup"
    print_info "Use --setup-nginx flag to force reconfiguration"
fi

# Step 4: Setup SSL (if requested)
if [[ "$1" == "--setup-ssl" ]] || [[ "$2" == "--setup-ssl" ]]; then
    echo ""
    echo "🔒 Step 4: SSL Certificate Setup"
    echo "==============================="
    
    if command -v certbot &> /dev/null; then
        print_info "Setting up SSL certificates..."
        
        # Setup SSL for single domain (serves all tools)
        domains=("powerbank-api.chargeghar.com")
        for domain in "${domains[@]}"; do
            if [ ! -f "/etc/letsencrypt/live/$domain/fullchain.pem" ]; then
                print_info "Setting up SSL for $domain..."
                if certbot --nginx -d "$domain" --non-interactive --agree-tos --email admin@chargeghar.com; then
                    print_status 0 "SSL certificate installed for $domain"
                else
                    print_warning "SSL setup failed for $domain"
                fi
            else
                print_status 0 "SSL certificate already exists for $domain"
            fi
        done
    else
        print_warning "Certbot not found. Please install certbot first:"
        echo "  sudo apt update && sudo apt install certbot python3-certbot-nginx"
    fi
else
    echo ""
    echo "🔒 Step 4: SSL Certificate Setup (Skipped)"
    echo "========================================"
    print_info "SSL certificates already exist - skipping setup"
    print_info "Use --setup-ssl flag to force reconfiguration"
fi

# Step 5: Environment Verification
echo ""
echo "⚙️  Step 5: Environment Verification"
echo "==================================="

# Verify we're in production environment
if grep -q "powerbank-api.chargeghar.com" .env; then
    print_status 0 "Production environment confirmed"
else
    print_warning "Not in production environment - this should be handled by deploy.sh"
fi

# Check if passwords are still default
if grep -q "CHANGE_THIS_PASSWORD" .env; then
    print_warning "Default passwords detected in .env file!"
    echo "Please update the following in .env file:"
    echo "  - DB_PASSWORD"
    echo "  - REDIS_PASSWORD" 
    echo "  - RABBITMQ_PASSWORD"
    echo "Continue anyway? (y/N)"
    read -r response
    if [[ ! "$response" =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Step 6: Build and Deploy
echo ""
echo "🔨 Step 6: Build and Deploy Application"
echo "======================================"

# Clean up any existing containers and networks
print_info "Cleaning up existing deployment..."
docker compose down -v 2>/dev/null || true

# Remove any orphaned containers that might be using our ports
print_info "Checking for orphaned containers..."
if docker ps -q --filter "name=powerbank-" | grep -q .; then
    print_warning "Found orphaned powerbank containers, removing them..."
    docker ps -q --filter "name=powerbank-" | xargs docker stop 2>/dev/null || true
    docker ps -aq --filter "name=powerbank-" | xargs docker rm 2>/dev/null || true
fi

# Build images
print_info "Building Docker images..."
if ./build.sh; then
    print_status 0 "Docker images built successfully"
else
    print_status 1 "Docker build failed"
    exit 1
fi

# Start services
print_info "Starting services..."
if ./start.sh; then
    print_status 0 "Services started successfully"
else
    print_warning "Service startup had issues, checking status..."
    
    # Check what services are actually running
    echo ""
    print_info "Current service status:"
    docker compose ps
    
    # Check for port conflicts specifically
    echo ""
    print_info "Checking for port conflicts:"
    for port in 3307 6380 10030 8084 8083 5672 15672; do
        if netstat -tuln 2>/dev/null | grep -q ":$port "; then
            echo "   ⚠️  Port $port is in use"
        else
            echo "   ✅ Port $port is available"
        fi
    done
    
    echo ""
    print_warning "Some services may have failed to start due to conflicts"
    print_info "Check logs with: docker compose logs -f"
    
    echo "Continue with verification anyway? (y/N)"
    read -r response
    if [[ ! "$response" =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Step 7: Verification
echo ""
echo "✅ Step 7: Deployment Verification"
echo "================================="

# Wait for services to be ready
print_info "Waiting for services to be ready..."
sleep 30

# Check service status
print_info "Checking service status..."
docker compose ps

# Verify no port conflicts remain
echo ""
print_info "Final port conflict check:"
final_conflicts=false
for port in 3307 6380 10030 8084 8083 5672 15672; do
    if netstat -tuln 2>/dev/null | grep -q ":$port " && ! docker compose ps --format "{{.Ports}}" | grep -q ":$port->"; then
        echo "   ⚠️  Port $port conflict detected (not from our containers)"
        final_conflicts=true
    else
        echo "   ✅ Port $port OK"
    fi
done

if [ "$final_conflicts" = true ]; then
    print_warning "Port conflicts detected - some services may not be accessible"
else
    print_status 0 "No port conflicts detected"
fi

# Test endpoints
echo ""
print_info "Testing endpoints..."

# Test backend API
if curl -s -f http://localhost:10030/api/common/config/pre_auth_amount > /dev/null; then
    print_status 0 "Backend API responding"
else
    print_warning "Backend API not responding"
fi

# Test frontend applications
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8084 | grep -q "200"; then
    print_status 0 "Cabinet Bind Tool responding"
else
    print_warning "Cabinet Bind Tool not responding"
fi

if curl -s -o /dev/null -w "%{http_code}" http://localhost:8083 | grep -q "200"; then
    print_status 0 "Test Tool responding"
else
    print_warning "Test Tool not responding"
fi

# Final summary
echo ""
echo "🎉 Deployment Summary"
echo "==================="
echo ""
echo "📊 Service Status:"
docker compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}"
echo ""
echo "🌐 Access URLs:"
echo "   • Backend API: https://powerbank-api.chargeghar.com"
echo "   • API Documentation: https://powerbank-api.chargeghar.com/doc.html"
echo "   • Cabinet Bind Tool: https://powerbank-api.chargeghar.com/binding"
echo "   • Test Tool: https://powerbank-api.chargeghar.com/test"
echo "   • RabbitMQ Management: http://$(hostname -I | awk '{print $1}'):15672"
echo ""
echo "🔧 Local Access (for debugging):"
echo "   • Backend API: http://localhost:10030"
echo "   • Cabinet Bind Tool: http://localhost:8084"
echo "   • Test Tool: http://localhost:8083"
echo ""
echo "📝 Useful Commands:"
echo "   • View logs: docker compose logs -f"
echo "   • Restart services: docker compose restart"
echo "   • Stop services: docker compose down"
echo ""

if [[ "$1" == "--setup-nginx" ]] || [[ "$2" == "--setup-nginx" ]]; then
    echo "🌐 Nginx Status:"
    systemctl status nginx --no-pager -l
fi

echo -e "${GREEN}✅ Deployment completed successfully!${NC}"