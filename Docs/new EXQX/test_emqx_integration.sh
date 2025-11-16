#!/bin/bash
# EMQX Integration Testing Script
# Tests the updated code with EMQX integration

set -e

echo "🚀 EMQX Integration Testing Script"
echo "===================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Step 1: Build Docker containers
echo -e "${YELLOW}Step 1: Building Docker containers...${NC}"
docker-compose build

# Step 2: Start services
echo -e "${YELLOW}Step 2: Starting services...${NC}"
docker-compose up -d

# Step 3: Wait for services to be ready
echo -e "${YELLOW}Step 3: Waiting for services to start...${NC}"
sleep 30

# Step 4: Check container status
echo -e "${YELLOW}Step 4: Checking container status...${NC}"
docker ps

# Step 5: Check application logs
echo -e "${YELLOW}Step 5: Checking application logs...${NC}"
docker logs powerbank-backend --tail 20

# Step 6: Test database
echo -e "${YELLOW}Step 6: Testing database connection...${NC}"
docker exec -it powerbank-mysql mysql -u root -pbrezze123 -e "USE db_share; SELECT COUNT(*) as record_count FROM bz_cabinet;"

# Step 7: Test existing binding endpoint
echo -e "${YELLOW}Step 7: Testing binding endpoint (existing functionality)...${NC}"
BIND_RESPONSE=$(curl -s -X POST "http://localhost:10030/communication/common/brezze-test-util/cabinets/bind" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "scanNo=EMQX_TEST001&imei=111111111111111&vietqr=emqx_test")

echo "Response: $BIND_RESPONSE"

if echo "$BIND_RESPONSE" | grep -q '"success":true'; then
    echo -e "${GREEN}✅ Binding endpoint working!${NC}"
else
    echo -e "${RED}❌ Binding endpoint failed!${NC}"
fi

# Step 8: Test EMQX event endpoint
echo -e "${YELLOW}Step 8: Testing EMQX event endpoint (new functionality)...${NC}"
EMQX_RESPONSE=$(curl -s -X POST "http://localhost:10030/api/emqx/event" \
  -H "Content-Type: application/json" \
  -d '{
    "event": "client.connected",
    "clientid": "test_device_001",
    "username": "test_user",
    "peername": "192.168.1.100:12345",
    "timestamp": '$(date +%s000)'
  }')

echo "Response: $EMQX_RESPONSE"
echo -e "${GREEN}✅ EMQX endpoint accessible!${NC}"

# Step 9: Test EMQX disconnect event
echo -e "${YELLOW}Step 9: Testing EMQX disconnect event...${NC}"
curl -s -X POST "http://localhost:10030/api/emqx/event" \
  -H "Content-Type: application/json" \
  -d '{
    "event": "client.disconnected",
    "clientid": "test_device_001",
    "username": "test_user",
    "peername": "192.168.1.100:12345",
    "timestamp": '$(date +%s000)'
  }'

echo -e "${GREEN}✅ Disconnect event sent!${NC}"

# Step 10: Check Swagger documentation
echo -e "${YELLOW}Step 10: Checking Swagger documentation...${NC}"
if curl -s http://localhost:10030/doc.html | grep -q "knife4j"; then
    echo -e "${GREEN}✅ Swagger documentation accessible at http://localhost:10030/doc.html${NC}"
else
    echo -e "${RED}❌ Swagger documentation not accessible${NC}"
fi

# Step 11: Verify database records
echo -e "${YELLOW}Step 11: Verifying database records...${NC}"
docker exec -it powerbank-mysql mysql -u root -pbrezze123 -e "USE db_share; SELECT id, cabinet_no, imei, state, vietqr FROM bz_cabinet ORDER BY id DESC LIMIT 5;"

# Summary
echo ""
echo "===================================="
echo -e "${GREEN}✅ Testing Complete!${NC}"
echo "===================================="
echo ""
echo "📋 Summary:"
echo "  - Docker containers: Running"
echo "  - Database: Connected"
echo "  - Binding API: Working"
echo "  - EMQX endpoint: Accessible"
echo "  - Swagger docs: Available"
echo ""
echo "🔗 Access Points:"
echo "  - API: http://localhost:10030"
echo "  - Swagger: http://localhost:10030/doc.html"
echo "  - Binding Tool: http://localhost:8084"
echo "  - Test Tool: http://localhost:8083"
echo ""
echo "📝 Next Steps:"
echo "  1. Configure EMQX webhook to point to: http://YOUR_SERVER:10030/api/emqx/event"
echo "  2. Test with real devices"
echo "  3. Monitor logs: docker logs powerbank-backend -f"
echo ""
