#!/bin/bash

# Quick Test Script for Single Domain Architecture
# Tests all endpoints to verify the migration was successful

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

echo "🧪 Testing Single Domain Architecture"
echo "===================================="

BASE_URL="https://powerbank-api.chargeghar.com"

print_info "Testing endpoints..."

# Test Backend API
print_info "Testing Backend API..."
if curl -s -f "$BASE_URL/api/common/config/pre_auth_amount" > /dev/null; then
    print_success "Backend API responding"
else
    print_error "Backend API not responding"
fi

# Test Cabinet Bind Tool
print_info "Testing Cabinet Bind Tool..."
response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/binding/")
if [ "$response" = "200" ]; then
    print_success "Cabinet Bind Tool responding"
else
    print_warning "Cabinet Bind Tool response: $response"
fi

# Test Tool
print_info "Testing Test Tool..."
response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/test/")
if [ "$response" = "200" ]; then
    print_success "Test Tool responding"
else
    print_warning "Test Tool response: $response"
fi

# Test API Documentation
print_info "Testing API Documentation..."
response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/doc.html")
if [ "$response" = "200" ]; then
    print_success "API Documentation responding"
else
    print_warning "API Documentation response: $response"
fi

echo ""
print_info "🌐 Access URLs:"
echo "   • Backend API: $BASE_URL/"
echo "   • Cabinet Tool: $BASE_URL/binding/"
echo "   • Test Tool: $BASE_URL/test/"
echo "   • API Docs: $BASE_URL/doc.html"

echo ""
print_info "📋 Manual Tests:"
echo "   1. Open browser and visit each URL above"
echo "   2. Check browser console for CORS errors"
echo "   3. Test device binding and testing functionality"
echo "   4. Verify all API calls work correctly"

echo ""
print_success "🎉 Single domain architecture test completed!"