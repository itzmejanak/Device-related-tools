# Power Bank Sharing System - Docker Deployment

This repository contains a complete Docker containerization setup for the Power Bank Sharing System, consisting of three main applications and supporting infrastructure.

## 🏗️ System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Docker Network                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐     │
│  │   nginx     │    │  backend    │    │   mysql     │     │
│  │   :80       │◄──►│   :10030    │◄──►│   :3306     │     │
│  └─────────────┘    └─────────────┘    └─────────────┘     │
│         │                   │                              │
│         │                   ▼                              │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐     │
│  │web-cabinet  │    │   redis     │    │  rabbitmq   │     │
│  │web-test-tool│    │   :6379     │    │   :5672     │     │
│  └─────────────┘    └─────────────┘    └─────────────┘     │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## 📦 Services

### Application Services
- **backend** (device-util-demo) - Spring Boot API server
- **web-cabinet-bind** - Device binding tool (HTML/JS)
- **web-test-tool** - Device testing tool (Nuxt.js PWA)

### Infrastructure Services
- **mysql** - MySQL 8.0.12 database
- **redis** - Redis 6.x cache
- **rabbitmq** - RabbitMQ 3.x message queue

## 🚀 Quick Start

### Prerequisites
- Docker 20.10+
- Docker Compose 2.0+
- 8GB+ RAM recommended
- 20GB+ disk space

### 1. Clone and Setup
```bash
git clone <repository-url>
cd power-bank-sharing-system
```

### 2. Build System
```bash
./build.sh
```

### 3. Configure Environment
Edit `.env` file with your configuration:
```bash
cp .env.example .env
nano .env  # Edit with your settings
```

### 4. Start System
```bash
./start.sh
```

### 5. Access Applications
- **Backend API**: http://localhost:10030
- **Cabinet Bind Tool**: http://localhost:8080
- **Test Tool**: http://localhost:8081
- **API Documentation**: http://localhost:10030/doc.html
- **RabbitMQ Management**: http://localhost:15672

## 🔧 Configuration

### Environment Variables

#### Database Configuration
```bash
DB_NAME=db_share
DB_USERNAME=root
DB_PASSWORD=your_secure_password
```

#### Redis Configuration
```bash
REDIS_PASSWORD=your_redis_password
```

#### RabbitMQ Configuration
```bash
RABBITMQ_USERNAME=admin
RABBITMQ_PASSWORD=your_rabbitmq_password
```

#### YBT IoT Configuration
```bash
YBT_ENABLE=true
YBT_ACCESS_KEY=your_access_key
YBT_ACCESS_SECRET=your_access_secret
YBT_PRODUCT_KEY=your_product_key
```

## 🛠️ Development

### Building Individual Services
```bash
# Backend only
docker-compose build backend

# Frontend tools only
docker-compose build web-cabinet-bind web-test-tool

# Infrastructure only
docker-compose up -d mysql redis rabbitmq
```

### Viewing Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f web-cabinet-bind
docker-compose logs -f web-test-tool
```

### Debugging
```bash
# Enter container shell
docker-compose exec backend sh
docker-compose exec mysql mysql -u root -p

# Check service health
docker-compose ps
```

## 🔍 Monitoring

### Health Checks
All services include health checks:
- **Backend**: `/actuator/health`
- **Frontend Tools**: `/health`
- **Infrastructure**: Built-in health checks

### Service Status
```bash
docker-compose ps
```

### Resource Usage
```bash
docker stats
```

## 📊 Production Deployment

### 1. Security Considerations
- Change all default passwords
- Use strong passwords for all services
- Enable SSL/TLS for external access
- Configure firewall rules
- Use secrets management for sensitive data

### 2. Performance Tuning
- Adjust JVM heap size for backend
- Configure MySQL buffer pool size
- Set Redis memory limits
- Configure nginx worker processes

### 3. Backup Strategy
```bash
# Database backup
docker-compose exec mysql mysqldump -u root -p db_share > backup.sql

# Volume backup
docker run --rm -v powerbank_mysql_data:/data -v $(pwd):/backup alpine tar czf /backup/mysql_backup.tar.gz /data
```

### 4. SSL Configuration
Create `nginx/ssl/` directory with certificates:
```bash
mkdir -p nginx/ssl
# Add your SSL certificates
```

## 🐛 Troubleshooting

### Common Issues

#### Backend won't start
```bash
# Check database connection
docker-compose logs backend
docker-compose exec mysql mysql -u root -p -e "SHOW DATABASES;"
```

#### Frontend can't connect to backend
```bash
# Check network connectivity
docker-compose exec web-cabinet-bind wget -O- http://backend:10030/actuator/health
```

#### Database connection issues
```bash
# Reset database
docker-compose down
docker volume rm powerbank_mysql_data
docker-compose up -d mysql
```

#### Port conflicts
```bash
# Check port usage
netstat -tulpn | grep :10030
```

### Reset Everything
```bash
# Stop and remove all containers, networks, and volumes
docker-compose down -v --rmi all
docker system prune -a
```

## 📝 API Endpoints

### Backend API (Port 10030)
- `GET /communication/ybt/check-all` - Device status
- `POST /communication/common/brezze-test-util/cabinets/bind` - Bind device
- `POST /communication/common/brezze-test-util/cabinets/unbind` - Unbind device
- `POST /communication/ybt/test-util/popup_sn` - Popup by SN
- `POST /communication/ybt/test-util/openLock` - Popup by slot
- `GET /doc.html` - API documentation

### Frontend Applications
- **Cabinet Bind Tool** (Port 8080) - Device binding interface
- **Test Tool** (Port 8081) - Device testing interface

## 🔄 Updates and Maintenance

### Updating Services
```bash
# Pull latest images
docker-compose pull

# Rebuild and restart
docker-compose up -d --build
```

### Database Migrations
```bash
# Access MySQL
docker-compose exec mysql mysql -u root -p db_share

# Run migration scripts
docker-compose exec backend java -jar app.jar --spring.profiles.active=docker
```

## 📞 Support

For issues and questions:
1. Check logs: `docker-compose logs -f`
2. Verify configuration: Check `.env` file
3. Test connectivity: Use health check endpoints
4. Review documentation: API docs at `/doc.html`

## 📄 License

This project is licensed under the terms specified in the main repository.