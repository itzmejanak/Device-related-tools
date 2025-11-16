# 🚀 EMQX Integration - Deployment Checklist

## ✅ **Completed Steps**

1. ✅ **Code Update**: Applied manufacturer's EMQX integration code (105 files updated)
2. ✅ **Database**: `bz_cabinet` table working in production
3. ✅ **Binding API**: Tested and working in production
4. ✅ **Documentation**: Reviewed manufacturer's docs

## 📋 **What's New**

### **New Controllers:**
- `EmqxController.java` - Handles EMQX webhook events
- `MqttCmdController.java` - MQTT command interface

### **New Services:**
- `AdvertService.java` - Advertisement service
- Various utility classes for EMQX integration

### **New Documentation:**
- `docs/iot_emqx.md` - EMQX setup guide
- `docs/controller_usage_doc.md` - API documentation
- `docs/project_notice_docs.md` - Project notices

## 🔧 **Build & Test Commands**

### **1. Build Docker Containers:**
```bash
# Navigate to project root
cd "/home/revdev/Desktop/Daily/Devalaya/PowerBank/Emqx/Device-related tools"

# Build containers
docker-compose build

# Start services
docker-compose up -d

# Check status
docker ps
```

### **2. Test Database:**
```bash
# Check database connection
docker exec -it powerbank-mysql mysql -u root -pbrezze123 -e "USE db_share; SHOW TABLES;"

# Check existing records
docker exec -it powerbank-mysql mysql -u root -pbrezze123 -e "USE db_share; SELECT id, cabinet_no, imei, state FROM bz_cabinet ORDER BY id DESC LIMIT 5;"
```

### **3. Test Binding API (Already Working):**
```bash
# Test bind
curl -X POST "http://localhost:10030/communication/common/brezze-test-util/cabinets/bind" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "scanNo=EMQX_TEST&imei=222222222222222&vietqr=test"

# Test unbind
curl -X POST "http://localhost:10030/communication/common/brezze-test-util/cabinets/unbind" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "scanNo=EMQX_TEST&imei=222222222222222"
```

### **4. Test EMQX Endpoint (New):**
```bash
# Test client connected event
curl -X POST "http://localhost:10030/api/emqx/event" \
  -H "Content-Type: application/json" \
  -d '{
    "event": "client.connected",
    "clientid": "test_device_001",
    "username": "test_user",
    "peername": "192.168.1.100:12345",
    "timestamp": 1698765432000
  }'

# Test client disconnected event
curl -X POST "http://localhost:10030/api/emqx/event" \
  -H "Content-Type: application/json" \
  -d '{
    "event": "client.disconnected",
    "clientid": "test_device_001",
    "username": "test_user",
    "peername": "192.168.1.100:12345",
    "timestamp": 1698765433000
  }'
```

### **5. Check Application Logs:**
```bash
# View recent logs
docker logs powerbank-backend --tail 50

# Follow logs in real-time
docker logs powerbank-backend -f

# Check for errors
docker logs powerbank-backend 2>&1 | grep -i error
```

## 🌐 **Production Deployment**

### **1. Commit Changes:**
```bash
cd device-util-demo
git add .
git commit -m "feat: Add EMQX integration and documentation"
git push origin main
```

### **2. Deploy to Production:**
```bash
# On production server
cd /opt/tools
git pull origin main

# Rebuild containers
docker-compose build

# Restart services
docker-compose down
docker-compose up -d

# Check status
docker ps
docker logs powerbank-backend --tail 50
```

### **3. Test Production Endpoints:**
```bash
# Test binding (already working)
curl -X POST "https://powerbank-api.chargeghar.com/communication/common/brezze-test-util/cabinets/bind" \
  -d "scanNo=PROD_EMQX_TEST&imei=333333333333333"

# Test EMQX webhook
curl -X POST "https://powerbank-api.chargeghar.com/api/emqx/event" \
  -H "Content-Type: application/json" \
  -d '{"event":"client.connected","clientid":"prod_test"}'
```

## 📊 **EMQX Configuration**

### **Required Setup:**

1. **Install EMQX** (version 5.4.1 or 5.9.1)
   ```bash
   # Using Docker
   docker run -d --name emqx \
     -p 1883:1883 \
     -p 8083:8083 \
     -p 8084:8084 \
     -p 8883:8883 \
     -p 18083:18083 \
     emqx/emqx:5.4.1
   ```

2. **Configure Webhook in EMQX Console:**
   - Access EMQX dashboard: http://localhost:18083
   - Default credentials: admin / public
   - Navigate to: Integration → Webhook
   - Add webhook URL: `https://powerbank-api.chargeghar.com/api/emqx/event`
   - Select events: `client.connected`, `client.disconnected`

3. **Test Device Connection:**
   ```bash
   # Using mosquitto_pub
   mosquitto_pub -h localhost -p 1883 \
     -t "/powerbank/test_device/user/update" \
     -m '{"test":"message"}' \
     -i "test_device_001"
   ```

## ✅ **Verification Checklist**

- [ ] Docker containers built successfully
- [ ] All containers running (mysql, redis, rabbitmq, backend)
- [ ] Database connection working
- [ ] Binding API tested (bind/unbind)
- [ ] EMQX endpoint accessible
- [ ] Swagger documentation available
- [ ] Application logs show no errors
- [ ] Production deployment successful
- [ ] EMQX webhook configured
- [ ] Device connections tested

## 📝 **Important Notes**

1. **Database**: Already configured with `bz_cabinet` table ✅
2. **MyBatis**: Configured with `@MapperScan` ✅
3. **Binding Logic**: Fully tested and working ✅
4. **EMQX Integration**: New feature, requires EMQX server setup
5. **YBT Support**: Still available, can be toggled via config

## 🔗 **Access URLs**

### **Local:**
- API: http://localhost:10030
- Swagger: http://localhost:10030/doc.html
- Binding Tool: http://localhost:8084
- Test Tool: http://localhost:8083
- EMQX Dashboard: http://localhost:18083

### **Production:**
- API: https://powerbank-api.chargeghar.com
- Swagger: https://powerbank-api.chargeghar.com/doc.html
- Binding Tool: https://powerbank-api.chargeghar.com/binding
- Test Tool: https://powerbank-api.chargeghar.com/test

## 🎯 **Next Actions**

1. **Build and test locally** using the commands above
2. **Review application logs** for any issues
3. **Deploy to production** when local tests pass
4. **Configure EMQX** webhook in production
5. **Test with real devices** once EMQX is configured
6. **Monitor production** logs and performance

---

**Status**: Ready for Docker build and testing! 🚀
