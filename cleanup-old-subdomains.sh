#!/bin/bash

# Cleanup Script for Old Subdomain Architecture
# This script removes old subdomain configurations after migrating to single domain

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

echo "🧹 Cleaning Up Old Subdomain Architecture"
echo "========================================"

# Check if running as root
if [ "$EUID" -ne 0 ]; then
    print_error "This script requires root privileges"
    echo "Please run: sudo ./cleanup-old-subdomains.sh"
    exit 1
fi

print_info "This will remove old subdomain configurations:"
echo "  • cabinet.chargeghar.com nginx config"
echo "  • test.chargeghar.com nginx config"
echo "  • Associated SSL certificates (optional)"
echo ""
# Check if running in auto mode (non-interactive)
if [ "$1" = "--auto" ] || [ ! -t 0 ]; then
    print_info "Running in automatic mode..."
    AUTO_MODE=true
else
    print_warning "Make sure the new single domain setup is working before proceeding!"
    echo ""
    echo "Continue? (y/N)"
    read -r response

    if [[ ! "$response" =~ ^[Yy]$ ]]; then
        print_info "Cleanup cancelled"
        exit 0
    fi
    AUTO_MODE=false
fi

print_info "Starting cleanup..."

# Remove nginx site configurations
print_info "Removing nginx configurations..."

# Disable sites
if [ -L "/etc/nginx/sites-enabled/cabinet.chargeghar.com" ]; then
    rm -f /etc/nginx/sites-enabled/cabinet.chargeghar.com
    print_success "Disabled cabinet.chargeghar.com"
else
    print_info "cabinet.chargeghar.com already disabled"
fi

if [ -L "/etc/nginx/sites-enabled/test.chargeghar.com" ]; then
    rm -f /etc/nginx/sites-enabled/test.chargeghar.com
    print_success "Disabled test.chargeghar.com"
else
    print_info "test.chargeghar.com already disabled"
fi

# Remove site configurations
if [ -f "/etc/nginx/sites-available/cabinet.chargeghar.com" ]; then
    rm -f /etc/nginx/sites-available/cabinet.chargeghar.com
    print_success "Removed cabinet.chargeghar.com config"
else
    print_info "cabinet.chargeghar.com config not found"
fi

if [ -f "/etc/nginx/sites-available/test.chargeghar.com" ]; then
    rm -f /etc/nginx/sites-available/test.chargeghar.com
    print_success "Removed test.chargeghar.com config"
else
    print_info "test.chargeghar.com config not found"
fi

# Test nginx configuration
print_info "Testing nginx configuration..."
if nginx -t 2>/dev/null; then
    print_success "Nginx configuration is valid"
    systemctl reload nginx && print_success "Nginx reloaded successfully"
else
    print_error "Nginx configuration test failed!"
    print_warning "Please check nginx configuration manually"
    exit 1
fi

# Optional: Remove SSL certificates
echo ""
print_info "SSL Certificate Cleanup (Optional)"
echo "================================="
if [ "$AUTO_MODE" = true ]; then
    print_info "Auto mode: Keeping SSL certificates (safer option)"
    ssl_response="n"
else
    print_warning "Do you want to remove old SSL certificates? (y/N)"
    print_info "Note: This will remove certificates for cabinet.chargeghar.com and test.chargeghar.com"
    read -r ssl_response
fi

if [[ "$ssl_response" =~ ^[Yy]$ ]]; then
    print_info "Removing SSL certificates..."
    
    if [ -d "/etc/letsencrypt/live/cabinet.chargeghar.com" ]; then
        certbot delete --cert-name cabinet.chargeghar.com --non-interactive 2>/dev/null || print_warning "Could not remove cabinet.chargeghar.com certificate"
        print_success "Removed cabinet.chargeghar.com SSL certificate"
    fi
    
    if [ -d "/etc/letsencrypt/live/test.chargeghar.com" ]; then
        certbot delete --cert-name test.chargeghar.com --non-interactive 2>/dev/null || print_warning "Could not remove test.chargeghar.com certificate"
        print_success "Removed test.chargeghar.com SSL certificate"
    fi
else
    print_info "SSL certificates kept (you can remove them manually later)"
fi

# Remove local nginx config files from project
print_info "Cleaning up local project files..."

if [ -f "nginx/sites-available/cabinet.chargeghar.com" ]; then
    rm -f nginx/sites-available/cabinet.chargeghar.com
    print_success "Removed local cabinet.chargeghar.com config"
fi

if [ -f "nginx/sites-available/test.chargeghar.com" ]; then
    rm -f nginx/sites-available/test.chargeghar.com
    print_success "Removed local test.chargeghar.com config"
fi

echo ""
print_success "🎉 Cleanup completed successfully!"
echo ""
print_info "Summary of changes:"
echo "  ✅ Removed cabinet.chargeghar.com nginx configuration"
echo "  ✅ Removed test.chargeghar.com nginx configuration"
echo "  ✅ Nginx configuration tested and reloaded"
if [[ "$ssl_response" =~ ^[Yy]$ ]]; then
    echo "  ✅ Removed old SSL certificates"
fi
echo ""
print_info "Current architecture:"
echo "  • Single domain: powerbank-api.chargeghar.com"
echo "  • Cabinet tool: powerbank-api.chargeghar.com/binding/"
echo "  • Test tool: powerbank-api.chargeghar.com/test/"
echo "  • Backend API: powerbank-api.chargeghar.com/"
echo ""
print_info "Next steps:"
echo "  1. Update DNS records (remove A records for cabinet and test subdomains)"
echo "  2. Test all URLs to ensure they work correctly"
echo "  3. Monitor logs: docker compose logs -f"