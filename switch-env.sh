#!/bin/bash

# Environment Switcher Script
# Usage: ./switch-env.sh [local|production]

set -e

# Colors for output
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

# Function to show usage
show_usage() {
    echo "Usage: ./switch-env.sh [local|production]"
    echo ""
    echo "Environments:"
    echo "  local      - Switch to local development environment"
    echo "  production - Switch to production environment"
    echo ""
    echo "Current environment files:"
    if [ -f ".env" ]; then
        echo "  .env (active)"
        if grep -q "localhost" .env; then
            echo "    → Currently set to LOCAL"
        elif grep -q "chargeghar.com" .env; then
            echo "    → Currently set to PRODUCTION"
        else
            echo "    → Unknown configuration"
        fi
    else
        echo "  .env (not found)"
    fi
    
    if [ -f ".env.local" ]; then
        echo "  .env.local (available)"
    fi
    
    if [ -f ".env.production" ]; then
        echo "  .env.production (available)"
    fi
}

# Check arguments
if [ $# -eq 0 ]; then
    show_usage
    exit 1
fi

ENV_TYPE="$1"

case $ENV_TYPE in
    "local")
        print_info "Switching to LOCAL development environment..."
        
        if [ -f ".env.local" ]; then
            cp .env.local .env
            print_success "Switched to local environment"
            print_info "Configuration:"
            echo "  • Backend: http://localhost:10030"
            echo "  • Cabinet Bind: http://localhost:8084"
            echo "  • Test Tool: http://localhost:8083"
            echo "  • MySQL Port: 3307"
            echo "  • Redis Port: 6380"
        else
            print_warning ".env.local not found, creating default local configuration"
            cat > .env << EOF
# Local Development Environment
DB_NAME=db_share
DB_USERNAME=root
DB_PASSWORD=brezze123
DB_USER=brezze
DB_USER_PASSWORD=brezze123
REDIS_PASSWORD=
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=admin123
YBT_ENABLE=false
YBT_ACCESS_KEY=test_key
YBT_ACCESS_SECRET=test_secret
YBT_UID=test_uid
YBT_REGION_ID=cn-shanghai
YBT_PRODUCT_KEY=test_product
YBT_CONSUMER_GROUP_ID=test_group
BACKEND_URL=http://localhost:10030
CABINET_BIND_URL=http://localhost:8084
TEST_TOOL_URL=http://localhost:8083
EOF
            print_success "Created local environment configuration"
        fi
        ;;
        
    "production")
        print_info "Switching to PRODUCTION environment..."
        
        if [ -f ".env.production" ]; then
            cp .env.production .env
            print_success "Switched to production environment"
            print_warning "Please verify production credentials in .env file"
            print_info "Configuration:"
            echo "  • Backend: https://powerbank-api.chargeghar.com"
            echo "  • Cabinet Bind: https://cabinet.chargeghar.com"
            echo "  • Test Tool: https://test.chargeghar.com"
            echo "  • MySQL Port: 3307 (to avoid conflicts)"
            echo "  • Redis Port: 6380 (to avoid conflicts)"
        else
            print_warning ".env.production not found, creating template"
            cat > .env << EOF
# Production Environment - CONFIGURE THESE VALUES
DB_NAME=db_share
DB_USERNAME=root
DB_PASSWORD=brezze123
DB_USER=brezze
DB_USER_PASSWORD=brezze123
REDIS_PASSWORD=rev5060
RABBITMQ_USERNAME=manu_admin
RABBITMQ_PASSWORD=rev5060
YBT_ENABLE=false
YBT_ACCESS_KEY=your_production_ybt_access_key
YBT_ACCESS_SECRET=your_production_ybt_access_secret
YBT_UID=your_production_ybt_uid
YBT_REGION_ID=cn-shanghai
YBT_PRODUCT_KEY=your_production_ybt_product_key
YBT_CONSUMER_GROUP_ID=your_production_consumer_group_id
BACKEND_URL=https://powerbank-api.chargeghar.com
CABINET_BIND_URL=https://cabinet.chargeghar.com
TEST_TOOL_URL=https://test.chargeghar.com
EOF
            print_warning "Created production template - PLEASE UPDATE PASSWORDS AND CREDENTIALS!"
        fi
        ;;
        
    *)
        echo "Invalid environment: $ENV_TYPE"
        show_usage
        exit 1
        ;;
esac

echo ""
print_info "Environment switched successfully!"
print_info "Next steps:"
echo "  1. Review .env file configuration"
echo "  2. Run: ./build.sh && ./start.sh"