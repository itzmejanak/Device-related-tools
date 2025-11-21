# 🚀 Deployment Checklist - MQTT Integration

## ✅ Configuration Review

### 1. Environment Variables (.env.production)

```bash
# ✅ Database - VERIFIED
DB_NAME=db_share
DB_USERNAME=root
DB_PASSWORD=brezze123

# ✅ Redis - VERIFIED  
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_DATABASE=3

# ✅ RabbitMQ - VERIFIED
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=admin123

# ✅ MQTT/EMQX - CONFIGURED
MQTT_BROKER=ub0bc614.ala.dedicated.aws.emqxcloud.com
MQTT_PORT=1883
MQTT_USERNAME=chargeghar
MQTT_PASSWORD=5060
MQTT_CLIENT_ID=iotdemo-server-production
MQTT_SSL=false

# ✅ Product Configuration
PRODUCT_KEY=powerbank
TOPIC_TYPE=true

# ⚠️ YBT - KEEP FALSE (using MQTT now)
YBT_ENABLE=false
```

## 📋 Code Changes Summary

### New Files Created:
1. ✅ `MqttConfig.java` - MQTT client configuration
2. ✅ `MqttService.java` - MQTT publish service
3. ✅ `MqttMessageListener.java` - MQTT subscriber for device responses

### Modified Files:
1. ✅ `YbtServiceImpl.java` - Replaced IotUtil with MqttService

## 🔍 System Architecture Verification

### Database Layer ✅
- **Entity**: `Cabinet.java` - Stores device info
- **Mapper**: `CabinetMapper.java` - MyBatis Plus mapper
- **Table**: `bz_cabinet` - Verified in init-db scripts
- **Status**: WORKING

### Redis Layer ✅
- **Keys Used**:
  - `check:{deviceName}` - Device check_all responses
  - `getwifi:{deviceName}` - WiFi scan results
  - `cabinet:pop-up:interval:{cabinetNo}` - Popup rate limiting
- **Status**: WORKING

### RabbitMQ Layer ✅
- **Exchange**: `ybt.delay.exchange` (x-delayed-message)
- **Queues**:
  - `ybt.delay.pb.pop` - Delayed popup by SN
  - `ybt.delay.pb.open.lock` - Delayed popup by position
  - `ybt.device.status` - Device status updates
- **Consumers**: `MQYbt.java` - All 3 consumers configured
- **Status**: WORKING

### MQTT Layer ✅
- **Publisher**: `MqttService.java` → Sends commands to devices
- **Subscriber**: `MqttMessageListener.java` → Receives device responses
- **Topics**:
  - Publish: `/{productKey}/{deviceName}/user/get`
  - Subscribe: `/{productKey}/+/user/update`
- **Status**: READY TO TEST

## 🎯 Business Logic Flow

### 1. Check Device Status (GET /communication/ybt/check-all)
```
User Request → YbtController.checkAll()
    ↓
YbtService.getCheckMessage()
    ↓
Clear Redis cache: check:{deviceName}
    ↓
Send MQTT: {"cmd":"check_all"} via MqttService
    ↓
Wait 15 seconds for device response
    ↓
Device responds via MQTT → MqttMessageListener
    ↓
Store in Redis: check:{deviceName}
    ↓
Return device details to user
```
**Status**: ✅ COMPLETE

### 2. Popup by Position (POST /communication/ybt/test-util/openLock)
```
User Request → YbtController.openLockOrdered()
    ↓
YbtService.popUpByIndex(req)
    ↓
Check popup interval in Redis
    ↓
Send to RabbitMQ delay queue (5 sec delay)
    ↓
MQYbt.popUpPosDelay() consumes message
    ↓
YbtService.popUpByIndex(deviceName, pos, io)
    ↓
Send MQTT: {"cmd":"popup","data":"1","io":"1"}
    ↓
Device opens lock
```
**Status**: ✅ COMPLETE

### 3. Popup by SN (POST /communication/ybt/test-util/popup_sn)
```
User Request → YbtController.rentByPbNoOrdered()
    ↓
YbtService.popUpByPbNo(req)
    ↓
Check popup interval in Redis
    ↓
Send to RabbitMQ delay queue (5 sec delay)
    ↓
MQYbt.popUpPbNoDelay() consumes message
    ↓
YbtService.popUpByPbNo(deviceName, pbNo)
    ↓
Send MQTT: {"cmd":"popup_sn","data":"POWERBANK_SN"}
    ↓
Device opens lock with that SN
```
**Status**: ✅ COMPLETE

## ⚠️ Critical Points

### 1. YBT_ENABLE Flag
**Answer: KEEP IT FALSE**
- `YBT_ENABLE=false` only disables `AliyunYbtClient` (old Aliyun AMQP listener)
- Our new MQTT system doesn't use this flag
- Setting it to `true` will try to connect to Aliyun IoT (which will fail)

### 2. MQTT Client ID
- Must be unique per instance
- Current: `iotdemo-server-production`
- If running multiple instances, use: `iotdemo-server-${HOSTNAME}`

### 3. Topic Format
- `TOPIC_TYPE=true` → `/powerbank/{deviceName}/user/get`
- `TOPIC_TYPE=false` → `/powerbank/{deviceName}/get`
- **Keep as `true`** to match your EMQX setup

### 4. Redis Cache Timeout
- Device responses cached for 24 hours
- Popup intervals cached for 5 seconds

## 🧪 Pre-Deployment Tests

### On Server (Before Deployment):

```bash
# 1. Check Docker containers
docker ps

# 2. Check current logs
docker-compose logs --tail=50 backend

# 3. Verify EMQX connectivity
curl -u "c2228527c6b07ddd:A5Mg-eO4zR8KYtOi1D9o54qR-NmWjbFH" \
  https://ub0bc614.ala.dedicated.aws.emqxcloud.com:8443/api/v5/clients

# 4. Check RabbitMQ
docker exec rabbitmq rabbitmqctl list_queues
```

## 🚀 Deployment Commands

```bash
cd /path/to/Device-related\ tools

# Pull latest code
git pull

# Stop backend
docker-compose stop backend

# Rebuild
docker-compose build backend

# Start
docker-compose up -d backend

# Monitor logs
docker-compose logs -f backend
```

## 📊 Post-Deployment Verification

### 1. Check MQTT Connection
Look for in logs:
```
✅ Connecting to MQTT broker: tcp://ub0bc614.ala.dedicated.aws.emqxcloud.com:1883
✅ MQTT client connected successfully
✅ Subscribed to MQTT topic: /powerbank/+/user/update
```

### 2. Test Endpoints

```bash
# Test 1: Health check
curl https://powerbank-api.chargeghar.com/actuator/health

# Test 2: Check device (will take ~15 seconds)
curl "https://powerbank-api.chargeghar.com/communication/ybt/check-all?scanNo=864601069946994"

# Test 3: Popup by position
curl -X POST "https://powerbank-api.chargeghar.com/communication/ybt/test-util/openLock" \
  -H "Content-Type: application/json" \
  -d '{"cabinetNo":"864601069946994","pos":1,"io":1}'

# Test 4: Popup by SN
curl -X POST "https://powerbank-api.chargeghar.com/communication/ybt/test-util/popup_sn" \
  -H "Content-Type: application/json" \
  -d '{"cabinetNo":"864601069946994","pbNo":"YOUR_SN"}'
```

### 3. Monitor EMQX Dashboard
- URL: https://ub0bc614.ala.dedicated.aws.emqxcloud.com:18083
- Check for:
  - Client `iotdemo-server-production` connected
  - Messages published to `/powerbank/864601069946994/user/get`
  - Messages received from `/powerbank/864601069946994/user/update`

## ❌ Rollback Plan

If deployment fails:

```bash
# Stop new version
docker-compose stop backend

# Revert code
git reset --hard HEAD~1

# Rebuild old version
docker-compose build backend

# Start
docker-compose up -d backend
```

## 📝 Known Limitations

1. **Device Response Timeout**: 15 seconds (hardcoded in YbtServiceImpl)
2. **Popup Interval**: 5 seconds between commands (rate limiting)
3. **Redis Cache**: Device data cached for 24 hours
4. **MQTT QoS**: 0 (fire and forget, no delivery guarantee)

## ✅ Final Checklist

- [ ] Code pulled from git
- [ ] Environment variables verified
- [ ] Docker containers healthy
- [ ] MQTT client connected
- [ ] MQTT subscriber active
- [ ] RabbitMQ queues created
- [ ] Redis accessible
- [ ] Database accessible
- [ ] All 3 endpoints tested
- [ ] Device responds to commands
- [ ] Logs show no errors

## 🎉 Success Criteria

✅ Backend starts without errors
✅ MQTT client connects to EMQX
✅ MQTT subscriber active on `/powerbank/+/user/update`
✅ `/check-all` endpoint returns device data
✅ `/openLock` endpoint triggers device popup
✅ `/popup_sn` endpoint triggers device popup
✅ Device responses cached in Redis
✅ No "invalid params" errors in logs
