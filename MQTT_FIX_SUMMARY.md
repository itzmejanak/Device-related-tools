# MQTT Fix Summary

## 🔴 Problems Found

1. **YBT_ENABLE=false** - Disabled in production
2. **Using Aliyun IoT SDK** - Code was calling `IotUtil.pubGetTopicMsg()` which uses Aliyun's proprietary API
3. **No MQTT Client** - No direct MQTT client configured to publish to EMQX
4. **Invalid Params Error** - `IotUtil` was checking for Aliyun credentials (accessKey, accessSecret) which are empty

## ✅ Fixes Applied

### 1. Created `MqttConfig.java`
- Configures Eclipse Paho MQTT client
- Reads from `application-docker.yml` MQTT settings
- Auto-connects to EMQX broker on startup

### 2. Created `MqttService.java`
- Simple service to publish messages to devices
- Builds correct topic: `/{productKey}/{deviceName}/user/get`
- Handles MQTT publishing with proper error logging

### 3. Updated `YbtServiceImpl.java`
- Replaced `IotUtil.pubGetTopicMsg()` with `mqttService.publish()`
- Removed dependency on Aliyun IoT SDK for device commands
- Now uses direct MQTT publishing to EMQX

## 📋 Files Changed

1. `/brezze-communication/src/main/java/com/brezze/share/communication/config/MqttConfig.java` - **NEW**
2. `/brezze-communication/src/main/java/com/brezze/share/communication/service/MqttService.java` - **NEW**
3. `/brezze-communication/src/main/java/com/brezze/share/communication/cabinet/service/impl/YbtServiceImpl.java` - **MODIFIED**

## 🚀 Deployment Steps

### On Your Server (ssh root@213.210.21.113):

```bash
cd /path/to/Device-related\ tools

# Pull latest code
git pull

# Run deployment script
./deploy-mqtt-fix.sh
```

### Or Manual Steps:

```bash
# Stop backend
docker-compose stop backend

# Rebuild
docker-compose build backend

# Start
docker-compose up -d backend

# Check logs
docker-compose logs -f backend
```

## 🧪 Testing After Deployment

### Test 1: Check Device Status
```bash
curl "https://powerbank-api.chargeghar.com/communication/ybt/check-all?scanNo=864601069946994"
```

**Expected:** Should return device details after ~15 seconds (waiting for device response)

### Test 2: Pop Up by Position
```bash
curl -X POST "https://powerbank-api.chargeghar.com/communication/ybt/test-util/openLock" \
  -H "Content-Type: application/json" \
  -d '{
    "cabinetNo": "864601069946994",
    "pos": 1,
    "io": 1
  }'
```

### Test 3: Pop Up by SN
```bash
curl -X POST "https://powerbank-api.chargeghar.com/communication/ybt/test-util/popup_sn" \
  -H "Content-Type: application/json" \
  -d '{
    "cabinetNo": "864601069946994",
    "pbNo": "YOUR_POWERBANK_SN"
  }'
```

## 📊 What to Look For in Logs

### ✅ Success Indicators:
```
INFO  - Connecting to MQTT broker: tcp://ub0bc614.ala.dedicated.aws.emqxcloud.com:1883
INFO  - MQTT client connected successfully
INFO  - Publishing to topic: /powerbank/864601069946994/user/get | payload: {"cmd":"check_all"}
INFO  - Message published successfully
```

### ❌ Error Indicators:
```
ERROR - Failed to publish MQTT message to device
ERROR - Connection lost
```

## 🔧 Configuration Used

From `.env.production`:
```bash
MQTT_BROKER=ub0bc614.ala.dedicated.aws.emqxcloud.com
MQTT_PORT=1883
MQTT_USERNAME=chargeghar
MQTT_PASSWORD=5060
MQTT_CLIENT_ID=iotdemo-server-local
PRODUCT_KEY=powerbank
TOPIC_TYPE=true
```

## 🎯 Next Steps

1. Deploy the fix to production
2. Test all three endpoints
3. Monitor MQTT connection in EMQX dashboard
4. Verify device receives commands
5. Check Redis cache for device responses

## 📝 Notes

- The `YBT_ENABLE` flag is now irrelevant since we're using MQTT directly
- `AliyunYbtClient` is still in code but won't be initialized (can be removed later)
- MQTT client auto-reconnects if connection drops
- Topic format matches your EMQX configuration: `/{productKey}/{deviceName}/user/get`
