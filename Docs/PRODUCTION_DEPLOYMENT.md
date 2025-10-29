# Power Bank Sharing System - Production Deployment Guide

## 🚀 One-Command Deployment Ready

This system is fully configured for **one-command deployment** with Docker Compose. All services are containerized and production-ready.

## 📋 System Overview

### Architecture
```
┌─────────────────────────────────────────────────────────────────┐
│                    Power Bank Sharing System                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────────┐    ┌─────────────────┐    ┌──────────────┐ │
│  │ device-util-demo│    │web-cabinet-bind │    │web-test-tool │ │
│  │   (Backend)     │◄──►│   (Binding)     │    │   (Testing)  │ │
│  │                 │    │                 │    │              │ │
│  │ Spring Boot API │    │ HTML/JS Tool    │    │ Nuxt.js PWA  │ │
│  │ Port: 10030     │    │ Port: 8080      │    │ Port: 8082   │ │
│  └─────────────────┘    └─────────────────┘    └──────────────┘ │
│           │                       │                      │      │
│           └───────────────────────┼──────────────────────┘      │
│                                   │                             │
│  ┌─────────────────────────────────▼─────────────────────────┐   │
│  │              Infrastructure Services                     │   │
│  │  • MySQL 8.0.12 (Port: 3306)                           │   │
│  │  • Redis 6-alpine (Port: 6379)                         │   │
│  │  • RabbitMQ 3-management + Delayed Plugin (5672/15672) │   │
│  │  • Nginx Reverse Proxy (Optional, Port: 80/443)        │   │
│  └───────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### Services Status ✅
- **Backend (device-util-demo)**: ✅ Ready - Spring Boot with Docker profile
- **Web Cabinet Bind**: ✅ Ready - Static HTML with environment config
- **Web Test Tool**: ✅ Ready - Nuxt.js PWA with Docker build
- **MySQL**: ✅ Ready - Configured with proper charset and auth
- **Redis**: ✅ Ready - Configured with persistence
- **RabbitMQ**: ✅ Ready - Custom build with delayed message plugin
- **Nginx**: ✅ Ready - Optional reverse proxy with SSL support

## 🚀 Quick Start (One Command)

### Prerequisites
- Docker 20.10+
- Docker Compose 2.0+
- 8GB+ RAM
- 20GB+ disk space

### Deploy Everything
```bash
# Clone repository
git clone <your-repo-url>
cd power-bank-sharing-system

# One-command deployment
./build.sh && ./start.sh
```

That's it! The system will be fully deployed and running.

## 🔧 Configuration Files

### 1. Environment Configuration (.env)
```bash
# Database Configuration
DB_NAME=db_share
DB_USERNAME=root
DB_PASSWORD=brezze123
DB_USER=brezze
DB_USER_PASSWORD=brezze123

# Redis Configuration
REDIS_PASSWORD=

# RabbitMQ Configuration
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=admin123

# YBT IoT Configuration (Optional)
YBT_ENABLE=false
YBT_ACCESS_KEY=your_ybt_access_key
YBT_ACCESS_SECRET=your_ybt_access_secret
YBT_UID=your_ybt_uid
YBT_REGION_ID=cn-shanghai
YBT_PRODUCT_KEY=your_ybt_product_key
YBT_CONSUMER_GROUP_ID=your_consumer_group_id

# Application URLs (for production)
BACKEND_URL=https://api.yourdomain.com
CABINET_BIND_URL=https://bind.yourdomain.com
TEST_TOOL_URL=https://test.yourdomain.com
```

### 2. Docker Compose Configuration
The `docker-compose.yml` includes:
- **Health checks** for all services
- **Dependency management** with proper startup order
- **Volume persistence** for data
- **Network isolation** with custom bridge network
- **Environment variable injection**
- **Resource limits** and restart policies

## 📦 Service Details

### Backend (device-util-demo)
- **Technology**: Spring Boot 2.2.6 + Java 8
- **Port**: 10030
- **Features**: 
  - IoT device communication (MQTT/EMQX)
  - Power bank cabinet control (YBT integration)
  - Order management and payment processing
  - Real-time device monitoring
  - API documentation (Swagger/Knife4j)
- **Health Check**: `/api/common/config/pre_auth_amount`
- **Docker Profile**: Uses `application-docker.yml` configuration

### Web Cabinet Bind Tool
- **Technology**: HTML5 + JavaScript + jQuery
- **Port**: 8080
- **Features**:
  - Device binding/unbinding (IMEI ↔ Station SN)
  - Barcode scanner integration
  - Multi-format QR code support
  - Multi-language support (EN/ZH)
- **Health Check**: `/health` endpoint
- **Configuration**: Environment-based BASE_URL injection

### Web Test Tool
- **Technology**: Nuxt.js 2.15.8 + Vue.js 2
- **Port**: 8082 (internal: 8081)
- **Features**:
  - QR code scanning for device identification
  - Real-time device status monitoring
  - Individual slot control (36-slot grid)
  - Dual serial port testing
  - Progressive Web App (PWA)
- **Health Check**: `/health` endpoint
- **Build**: Multi-stage Docker build (Node.js → Nginx)

### Infrastructure Services

#### MySQL 8.0.12
- **Port**: 3306
- **Database**: `db_share`
- **Features**: UTF8MB4 charset, native password auth
- **Volume**: `mysql_data` for persistence
- **Init Scripts**: `./init-db/` directory for initialization

#### Redis 6-alpine
- **Port**: 6379
- **Features**: AOF persistence enabled
- **Volume**: `redis_data` for persistence
- **Usage**: Caching, session management, distributed locking

#### RabbitMQ 3-management
- **Ports**: 5672 (AMQP), 15672 (Management UI)
- **Features**: 
  - **Delayed Message Exchange Plugin** (pre-installed)
  - Management UI for monitoring
  - Persistent message storage
- **Volume**: `rabbitmq_data` for persistence
- **Custom Build**: Includes delayed message plugin

## 🌐 Access URLs

### Local Development
- **Backend API**: http://localhost:10030
- **API Documentation**: http://localhost:10030/doc.html
- **Cabinet Bind Tool**: http://localhost:8080
- **Test Tool**: http://localhost:8082
- **RabbitMQ Management**: http://localhost:15672 (admin/admin123)

### Production (Configure in .env)
- **Backend API**: https://api.yourdomain.com
- **Cabinet Bind Tool**: https://bind.yourdomain.com
- **Test Tool**: https://test.yourdomain.com

## 🔒 Security Features

### Production Security Checklist
- [ ] Change all default passwords in `.env`
- [ ] Use strong passwords (12+ characters)
- [ ] Enable SSL/TLS certificates
- [ ] Configure firewall rules
- [ ] Use secrets management for sensitive data
- [ ] Enable container resource limits
- [ ] Configure log rotation
- [ ] Set up monitoring and alerting

### Built-in Security
- **Non-root containers**: All services run as non-root users
- **Network isolation**: Custom Docker network
- **Input validation**: Client and server-side validation
- **Health checks**: Automatic service monitoring
- **Resource limits**: Memory and CPU constraints

## 📊 Monitoring & Logging

### Health Monitoring
```bash
# Check all service health
docker-compose ps

# View service logs
docker-compose logs -f backend
docker-compose logs -f web-cabinet-bind
docker-compose logs -f web-test-tool

# Monitor resource usage
docker stats
```

### Log Locations
- **Backend Logs**: `backend_logs` volume → `/app/logs`
- **Container Logs**: `docker-compose logs <service>`
- **System Logs**: Docker daemon logs

### Monitoring Endpoints
- **Backend Health**: http://localhost:10030/actuator/health
- **RabbitMQ Management**: http://localhost:15672
- **Service Status**: `docker-compose ps`

## 🔄 Operations

### Start/Stop Services
```bash
# Start all services
./start.sh

# Stop all services
./stop.sh

# Restart specific service
docker-compose restart backend

# View logs
docker-compose logs -f
```

### Backup & Recovery
```bash
# Database backup
docker-compose exec mysql mysqldump -u root -p${DB_PASSWORD} db_share > backup.sql

# Volume backup
docker run --rm -v powerbank_mysql_data:/data -v $(pwd):/backup alpine tar czf /backup/mysql_backup.tar.gz /data

# Restore database
docker-compose exec -T mysql mysql -u root -p${DB_PASSWORD} db_share < backup.sql
```

### Updates & Maintenance
```bash
# Update images
docker-compose pull

# Rebuild and restart
docker-compose up -d --build

# Clean up unused resources
docker system prune -a
```

## 🐛 Troubleshooting

### Common Issues

#### Services won't start
```bash
# Check Docker daemon
systemctl status docker

# Check port conflicts
netstat -tulpn | grep :10030

# Check logs
docker-compose logs <service-name>
```

#### Database connection issues
```bash
# Test database connection
docker-compose exec mysql mysql -u root -p${DB_PASSWORD} -e "SHOW DATABASES;"

# Reset database
docker-compose down
docker volume rm powerbank_mysql_data
docker-compose up -d mysql
```

#### Frontend can't connect to backend
```bash
# Test network connectivity
docker-compose exec web-cabinet-bind wget -O- http://backend:10030/actuator/health

# Check environment variables
docker-compose exec backend env | grep DB_
```

#### RabbitMQ plugin issues
```bash
# Check plugins
docker-compose exec rabbitmq rabbitmq-plugins list

# Verify delayed message plugin
docker-compose exec rabbitmq rabbitmq-plugins list | grep delayed
```

### Reset Everything
```bash
# Complete reset (WARNING: Deletes all data)
docker-compose down -v --rmi all
docker system prune -a -f
```

## 🚀 Production Deployment

### 1. Server Requirements
- **CPU**: 4+ cores
- **RAM**: 8GB+ (16GB recommended)
- **Storage**: 50GB+ SSD
- **OS**: Ubuntu 20.04+ / CentOS 8+ / RHEL 8+
- **Network**: Static IP, domain name

### 2. Domain Configuration
```bash
# DNS Records (A records)
api.yourdomain.com     → YOUR_SERVER_IP
bind.yourdomain.com    → YOUR_SERVER_IP
test.yourdomain.com    → YOUR_SERVER_IP
```

### 3. SSL Certificate Setup
```bash
# Create SSL directory
mkdir -p nginx/ssl

# Add your certificates
cp your-domain.crt nginx/ssl/
cp your-domain.key nginx/ssl/

# Enable nginx proxy
docker-compose --profile proxy up -d
```

### 4. Production Environment
```bash
# Update .env for production
DB_PASSWORD=your_super_secure_password
RABBITMQ_PASSWORD=your_rabbitmq_password
REDIS_PASSWORD=your_redis_password

# Production URLs
BACKEND_URL=https://api.yourdomain.com
CABINET_BIND_URL=https://bind.yourdomain.com
TEST_TOOL_URL=https://test.yourdomain.com
```

### 5. Firewall Configuration
```bash
# Allow required ports
ufw allow 22    # SSH
ufw allow 80    # HTTP
ufw allow 443   # HTTPS
ufw enable

# Block direct access to services (optional)
ufw deny 3306   # MySQL
ufw deny 6379   # Redis
ufw deny 5672   # RabbitMQ
```

## 📈 Performance Tuning

### JVM Tuning (Backend)
```yaml
# In docker-compose.yml backend service
environment:
  JAVA_OPTS: "-Xms1g -Xmx2g -XX:+UseG1GC"
```

### MySQL Tuning
```yaml
# In docker-compose.yml mysql service
command: >
  --default-authentication-plugin=mysql_native_password
  --character-set-server=utf8mb4
  --collation-server=utf8mb4_unicode_ci
  --innodb-buffer-pool-size=1G
  --max-connections=200
```

### Redis Tuning
```yaml
# In docker-compose.yml redis service
command: >
  redis-server
  --appendonly yes
  --maxmemory 512mb
  --maxmemory-policy allkeys-lru
```

## 📋 API Documentation

### Backend API Endpoints
- **Device Status**: `GET /communication/ybt/check-all`
- **Device Binding**: `POST /common/brezze-test-util/cabinets/bind`
- **Device Unbinding**: `POST /common/brezze-test-util/cabinets/unbind`
- **Slot Control**: `POST /communication/ybt/test-util/openLock`
- **Power Bank Popup**: `POST /communication/ybt/test-util/popup_sn`
- **Send Commands**: `POST /communication/ybt/send`

### Interactive Documentation
- **Swagger UI**: http://localhost:10030/doc.html
- **API Testing**: Built-in request/response testing
- **Authentication**: Language header support

## 🎯 Business Workflows

### 1. Device Binding Workflow
```
Technician → Cabinet Bind Tool → Backend API → Database
1. Scan device IMEI and Station SN
2. Validate and bind in system
3. Confirm binding success
```

### 2. Device Testing Workflow
```
Technician → Test Tool → Backend API → IoT Device
1. Scan cabinet QR code
2. View real-time device status
3. Test individual slots
4. Send control commands
```

### 3. Live Operations
```
User → Mobile App → Backend API → IoT Device
1. Rent power bank
2. Process payment
3. Control cabinet hardware
4. Monitor usage
```

## ✅ Deployment Checklist

### Pre-Deployment
- [ ] Docker and Docker Compose installed
- [ ] Server meets minimum requirements
- [ ] Domain names configured
- [ ] SSL certificates obtained (for production)
- [ ] Firewall rules configured
- [ ] Environment variables configured

### Deployment
- [ ] Repository cloned
- [ ] `.env` file configured
- [ ] `./build.sh` executed successfully
- [ ] `./start.sh` executed successfully
- [ ] All services healthy (`docker-compose ps`)

### Post-Deployment
- [ ] Backend API accessible
- [ ] Frontend tools accessible
- [ ] API documentation accessible
- [ ] Database connection working
- [ ] RabbitMQ management accessible
- [ ] Health checks passing
- [ ] Logs monitoring configured
- [ ] Backup strategy implemented

## 🎉 Success Indicators

When deployment is successful, you should see:

1. **All services running**: `docker-compose ps` shows all services as "Up (healthy)"
2. **Backend API responding**: http://localhost:10030/actuator/health returns 200 OK
3. **Frontend tools loading**: Both tools accessible and can connect to backend
4. **API documentation**: Swagger UI accessible at http://localhost:10030/doc.html
5. **RabbitMQ management**: Management UI accessible with delayed message plugin enabled
6. **Database connectivity**: Backend logs show successful database connection
7. **No error logs**: Clean startup logs without critical errors

## 📞 Support

For deployment issues:
1. **Check logs**: `docker-compose logs -f`
2. **Verify configuration**: Review `.env` file
3. **Test connectivity**: Use health check endpoints
4. **Review documentation**: This guide and API docs
5. **Check resources**: Ensure adequate CPU/RAM/disk

---

**🚀 Ready for Production!** This system is fully containerized and production-ready with one-command deployment.