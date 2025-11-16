#!/bin/bash

# Comprehensive API Testing Script
# Tests ALL endpoints mentioned in README.md

BASE_URL="http://localhost:10030"
PASS_COUNT=0
FAIL_COUNT=0
TOTAL_COUNT=0

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print section header
print_header() {
    echo ""
    echo "═══════════════════════════════════════════════════════════════"
    echo -e "${BLUE}$1${NC}"
    echo "═══════════════════════════════════════════════════════════════"
}

# Function to test endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local description=$3
    local data=$4
    local expected_code=$5
    
    TOTAL_COUNT=$((TOTAL_COUNT + 1))
    
    echo ""
    echo -e "${YELLOW}TEST $TOTAL_COUNT: $description${NC}"
    echo "Endpoint: $method $endpoint"
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL$endpoint" 2>&1)
    elif [ "$method" = "POST" ]; then
        if [ -n "$data" ]; then
            response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL$endpoint" \
                -H "Content-Type: application/x-www-form-urlencoded" \
                -d "$data" 2>&1)
        else
            response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL$endpoint" 2>&1)
        fi
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" = "$expected_code" ]; then
        echo -e "${GREEN}✅ PASS${NC} - HTTP $http_code"
        PASS_COUNT=$((PASS_COUNT + 1))
    else
        echo -e "${RED}❌ FAIL${NC} - Expected HTTP $expected_code, got HTTP $http_code"
        FAIL_COUNT=$((FAIL_COUNT + 1))
    fi
    
    echo "Response: $body" | head -c 200
    echo ""
}

# Function to test JSON POST endpoint
test_json_endpoint() {
    local method=$1
    local endpoint=$2
    local description=$3
    local json_data=$4
    local expected_code=$5
    
    TOTAL_COUNT=$((TOTAL_COUNT + 1))
    
    echo ""
    echo -e "${YELLOW}TEST $TOTAL_COUNT: $description${NC}"
    echo "Endpoint: $method $endpoint"
    
    response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL$endpoint" \
        -H "Content-Type: application/json" \
        -d "$json_data" 2>&1)
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" = "$expected_code" ]; then
        echo -e "${GREEN}✅ PASS${NC} - HTTP $http_code"
        PASS_COUNT=$((PASS_COUNT + 1))
    else
        echo -e "${RED}❌ FAIL${NC} - Expected HTTP $expected_code, got HTTP $http_code"
        FAIL_COUNT=$((FAIL_COUNT + 1))
    fi
    
    echo "Response: $body" | head -c 200
    echo ""
}

# Start testing
print_header "🚀 COMPREHENSIVE API ENDPOINT TESTING"
echo "Base URL: $BASE_URL"
echo "Testing all endpoints from README.md"

# ═══════════════════════════════════════════════════════════════
# 1. BINDING API (BindingController)
# ═══════════════════════════════════════════════════════════════
print_header "1️⃣  BINDING API - BindingController"

# Test 1.1: Valid Bind
test_endpoint "POST" "/communication/common/brezze-test-util/cabinets/bind" \
    "Bind station SN and IMEI (Valid)" \
    "scanNo=TEST001&imei=123456789012345&vietqr=test_qr" \
    "200"

# Test 1.2: Duplicate Station SN (should fail)
test_endpoint "POST" "/communication/common/brezze-test-util/cabinets/bind" \
    "Bind duplicate station SN (Should fail)" \
    "scanNo=TEST001&imei=987654321098765" \
    "200"

# Test 1.3: Invalid IMEI format
test_endpoint "POST" "/communication/common/brezze-test-util/cabinets/bind" \
    "Bind with invalid IMEI (Should fail)" \
    "scanNo=TEST002&imei=12345" \
    "200"

# Test 1.4: Empty parameters
test_endpoint "POST" "/communication/common/brezze-test-util/cabinets/bind" \
    "Bind with empty parameters (Should fail)" \
    "scanNo=&imei=" \
    "200"

# Test 1.5: Valid second device
test_endpoint "POST" "/communication/common/brezze-test-util/cabinets/bind" \
    "Bind second device (Valid)" \
    "scanNo=TEST002&imei=987654321098765&vietqr=test_qr2" \
    "200"

# Test 1.6: Unbind device
test_endpoint "POST" "/communication/common/brezze-test-util/cabinets/unbind" \
    "Unbind station SN" \
    "scanNo=TEST001&imei=123456789012345" \
    "200"

# ═══════════════════════════════════════════════════════════════
# 2. DEVICE API (YbtController)
# ═══════════════════════════════════════════════════════════════
print_header "2️⃣  DEVICE API - YbtController"

# Test 2.1: Android device authentication (GET)
test_endpoint "GET" "/api/iot/client/con?simUUID=test&simMobile=1234567890&sign=test" \
    "Android device authentication (GET)" \
    "" \
    "200"

# Test 2.2: MCU device authentication (POST)
test_endpoint "POST" "/api/iot/client/con" \
    "MCU device authentication (POST)" \
    "simUUID=test&simMobile=1234567890&sign=test" \
    "200"

# Test 2.3: Whole device report
test_endpoint "POST" "/api/rentbox/upload/data?rentboxSN=TEST001" \
    "Whole device report" \
    "" \
    "200"

# Test 2.4: Return power bank report
test_endpoint "GET" "/api/rentbox/order/return?rentboxSN=TEST001&pbSN=PB001&singleSN=SN001" \
    "Return power bank report" \
    "" \
    "200"

# Test 2.5: Device configuration report (GET)
test_endpoint "GET" "/api/rentbox/config/data?rentboxSN=TEST001" \
    "Device configuration report (GET)" \
    "" \
    "200"

# Test 2.6: Get MCU release version
test_endpoint "GET" "/api/iot/app/version/publish/mcu?appUuid=00000000000000000000000000000000" \
    "Get MCU release version" \
    "" \
    "200"

# Test 2.7: Get MCU test version
test_endpoint "GET" "/api/iot/app/version/test/mcu?appUuid=00000000000000000000000000000000" \
    "Get MCU test version" \
    "" \
    "200"

# Test 2.8: Get chip release version
test_endpoint "GET" "/api/iot/app/version/publish/chip?appUuid=00000000000000000000000000000000" \
    "Get chip release version" \
    "" \
    "200"

# Test 2.9: Get chip test version
test_endpoint "GET" "/api/iot/app/version/test/chip?appUuid=00000000000000000000000000000000" \
    "Get chip test version" \
    "" \
    "200"

# Test 2.10: Get advertisement list
test_endpoint "GET" "/api/advert/rentbox/distribute/list?rentboxSN=TEST001" \
    "Get advertisement list" \
    "" \
    "200"

# ═══════════════════════════════════════════════════════════════
# 3. DEVICE COMMANDS API (MqttCmdController)
# ═══════════════════════════════════════════════════════════════
print_header "3️⃣  DEVICE COMMANDS API - MqttCmdController"

# Test 3.1: Partial query (check)
test_json_endpoint "POST" "/communication/ybt/check" \
    "Partial query (5 power banks with highest power)" \
    '{"cabinetNo":"TEST001"}' \
    "200"

# Test 3.2: Full query (check-all)
test_json_endpoint "POST" "/communication/ybt/check-all" \
    "Full query (whole device data)" \
    '{"cabinetNo":"TEST001"}' \
    "200"

# Test 3.3: Open lock by position
test_json_endpoint "POST" "/communication/ybt/openLock" \
    "Open lock by position" \
    '{"cabinetNo":"TEST001","index":1}' \
    "200"

# Test 3.4: Pop up by power bank SN
test_json_endpoint "POST" "/communication/ybt/popup_sn" \
    "Pop up by power bank SN" \
    '{"cabinetNo":"TEST001","pbSN":"PB001"}' \
    "200"

# Test 3.5: Reset MCU
test_json_endpoint "POST" "/communication/ybt/reset-mcu" \
    "Reset MCU" \
    '{"cabinetNo":"TEST001"}' \
    "200"

# Test 3.6: Restart cabinet
test_json_endpoint "POST" "/communication/ybt/restart" \
    "Restart cabinet" \
    '{"cabinetNo":"TEST001"}' \
    "200"

# Test 3.7: Update advertisement
test_json_endpoint "POST" "/communication/ybt/load-ad" \
    "Update advertisement" \
    '{"cabinetNo":"TEST001"}' \
    "200"

# Test 3.8: Push Android release upgrade
test_json_endpoint "POST" "/communication/ybt/push-version-publish" \
    "Push Android release upgrade" \
    '{"cabinetNo":"TEST001","type":"android"}' \
    "200"

# Test 3.9: Push Android test upgrade
test_json_endpoint "POST" "/communication/ybt/push-version-test" \
    "Push Android test upgrade" \
    '{"cabinetNo":"TEST001","type":"android"}' \
    "200"

# Test 3.10: Push holesite release upgrade
test_json_endpoint "POST" "/communication/ybt/push-version-holesite" \
    "Push holesite release upgrade" \
    '{"cabinetNo":"TEST001"}' \
    "200"

# Test 3.11: HTTP whole device report
test_json_endpoint "POST" "/communication/ybt/upload-all" \
    "HTTP whole device report" \
    '{"cabinetNo":"TEST001"}' \
    "200"

# Test 3.12: Set WiFi
test_json_endpoint "POST" "/communication/ybt/set-wifi" \
    "Set WiFi (popular version)" \
    '{"cabinetNo":"TEST001","ssid":"TestWiFi","password":"test123"}' \
    "200"

# Test 3.13: Set volume/network priority
test_json_endpoint "POST" "/communication/ybt/volume" \
    "Set network priority (popular version)" \
    '{"cabinetNo":"TEST001","volume":"50"}' \
    "200"

# Test 3.14: Set audio
test_json_endpoint "POST" "/communication/ybt/setAudio" \
    "Set audio (popular version)" \
    '{"cabinetNo":"TEST001","audio":"50"}' \
    "200"

# Test 3.15: Set network mode
test_json_endpoint "POST" "/communication/ybt/setMode" \
    "Set network mode (popular version)" \
    '{"cabinetNo":"TEST001","mode":"4G"}' \
    "200"

# Test 3.16: Get WiFi list
test_json_endpoint "POST" "/communication/ybt/wifi" \
    "Get WiFi list (popular version)" \
    '{"cabinetNo":"TEST001"}' \
    "200"

# ═══════════════════════════════════════════════════════════════
# 4. EMQX WEBHOOK API (EmqxController)
# ═══════════════════════════════════════════════════════════════
print_header "4️⃣  EMQX WEBHOOK API - EmqxController"

# Test 4.1: EMQX event webhook (device connected)
test_json_endpoint "POST" "/api/emqx/event" \
    "EMQX event webhook (device connected)" \
    '{"event":"client.connected","clientid":"TEST001","username":"test"}' \
    "200"

# Test 4.2: EMQX event webhook (device disconnected)
test_json_endpoint "POST" "/api/emqx/event" \
    "EMQX event webhook (device disconnected)" \
    '{"event":"client.disconnected","clientid":"TEST001","username":"test"}' \
    "200"

# Test 4.3: EMQX message webhook
test_json_endpoint "POST" "/api/emqx/message" \
    "EMQX message webhook" \
    '{"topic":"/powerbank/TEST001/user/update","payload":"dGVzdA==","qos":1}' \
    "200"

# ═══════════════════════════════════════════════════════════════
# 5. TEST TOOLS API (YbtController - Test Endpoints)
# ═══════════════════════════════════════════════════════════════
print_header "5️⃣  TEST TOOLS API - YbtController Test Endpoints"

# Test 5.1: Send command (general)
test_json_endpoint "POST" "/communication/ybt/send" \
    "Send command (general test tool)" \
    '{"cabinetNo":"TEST001","cmd":"test"}' \
    "200"

# Test 5.2: Get station detail (test tool)
test_endpoint "GET" "/communication/ybt/check-all?cabinetNo=TEST001" \
    "Get station detail (test tool)" \
    "" \
    "200"

# Test 5.3: Pop up by power bank SN (test tool)
test_json_endpoint "POST" "/communication/ybt/test-util/popup_sn" \
    "Pop up by power bank SN (test tool)" \
    '{"cabinetNo":"TEST001","pbSN":"PB001"}' \
    "200"

# Test 5.4: Pop up by slot (test tool)
test_json_endpoint "POST" "/communication/ybt/test-util/openLock" \
    "Pop up by slot (test tool)" \
    '{"cabinetNo":"TEST001","index":1}' \
    "200"

# ═══════════════════════════════════════════════════════════════
# 6. COMMON API
# ═══════════════════════════════════════════════════════════════
print_header "6️⃣  COMMON API - Configuration & Health"

# Test 6.1: Get pre-auth amount config
test_endpoint "GET" "/api/common/config/pre_auth_amount" \
    "Get pre-auth amount configuration" \
    "" \
    "200"

# Test 6.2: Swagger documentation
test_endpoint "GET" "/doc.html" \
    "Swagger API documentation" \
    "" \
    "200"

# ═══════════════════════════════════════════════════════════════
# FINAL SUMMARY
# ═══════════════════════════════════════════════════════════════
print_header "📊 TEST SUMMARY"

echo ""
echo "Total Tests: $TOTAL_COUNT"
echo -e "${GREEN}Passed: $PASS_COUNT${NC}"
echo -e "${RED}Failed: $FAIL_COUNT${NC}"
echo ""

if [ $FAIL_COUNT -eq 0 ]; then
    echo -e "${GREEN}✅ ALL TESTS PASSED! 🎉${NC}"
    exit 0
else
    echo -e "${RED}❌ SOME TESTS FAILED${NC}"
    exit 1
fi
