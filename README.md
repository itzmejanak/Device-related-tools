# Power Bank Sharing System

A complete Docker-containerized Power Bank Sharing System with IoT device management, cabinet binding tools, and testing interfaces.

## 🚀 Quick Start

### First Time Setup
```bash
# Make all scripts executable (run once)
./setup.sh
```

### Smart Deployment (Recommended)
```bash
# One command for any environment - automatically detects and configures
./deploy.sh

# Alternative if script is not executable:
bash deploy.sh
```

### Manual Deployment

#### Local Development
```bash
# 1. Switch to local environment
./switch-env.sh local

# 2. Deploy locally
./build.sh && ./start.sh
```

#### Production Deployment (srv998476)
```bash
# 1. Switch to production environment
./switch-env.sh production

# 2. Edit production credentials (if needed)
nano .env

# 3. Deploy with nginx and SSL setup
sudo ./deploy-production.sh --setup-nginx --setup-ssl
```

## 📦 System Components

### Applications
- **Backend API** (Spring Boot) - Device management and IoT communication
- **Cabinet Bind Tool** (HTML/JS) - Device binding interface
- **Test Tool** (Nuxt.js PWA) - Device testing and monitoring

### Infrastructure
- **MySQL 8.0.12** - Database
- **Redis 6** - Cache and session management
- **RabbitMQ 3** - Message queue with delayed message plugin

## 🌐 Access URLs

### Local Development
- Backend API: http://localhost:10030
- Cabinet Bind Tool: http://localhost:8081
- Test Tool: http://localhost:8083
- API Documentation: http://localhost:10030/doc.html
- RabbitMQ Management: http://localhost:15672

### Production (srv998476)
- Backend API: https://powerbank-api.chargeghar.com
- Cabinet Bind Tool: https://cabinet.chargeghar.com
- Test Tool: https://test.chargeghar.com
- API Documentation: https://powerbank-api.chargeghar.com/doc.html

## 🔧 Available Scripts

### Environment Management
```bash
./switch-env.sh local       # Switch to local development
./switch-env.sh production  # Switch to production

# Alternative execution (if not executable):
bash switch-env.sh local
bash switch-env.sh production
```

### Deployment
```bash
./verify-deployment.sh      # Verify system readiness
./build.sh                  # Build Docker images
./start.sh                  # Start all services
./stop.sh                   # Stop all services
./deploy-production.sh      # Full production deployment
```

### Production-specific
```bash
# Check and resolve port conflicts
./fix-port-conflicts.sh

# Setup nginx configurations
sudo ./deploy-production.sh --setup-nginx

# Setup SSL certificates
sudo ./deploy-production.sh --setup-ssl

# Full setup (nginx + SSL + deploy)
sudo ./deploy-production.sh --setup-nginx --setup-ssl
```

## 📋 Port Configuration

### Local Development Ports
| Service | Port | Purpose |
|---------|------|---------|
| MySQL | 3307 | Database (avoids conflict with existing MySQL) |
| Redis | 6380 | Cache (avoids conflict with existing Redis) |
| Backend | 10030 | API Server |
| Cabinet Bind | 8081 | Device binding tool |
| Test Tool | 8083 | Device testing interface |
| RabbitMQ | 5672/15672 | Message queue + Management |

### Production Server (srv998476) - No Conflicts
✅ **Existing services preserved:**
- IoT Demo: api.chargeghar.com (port 8080)
- Power Bank Production: main.chargeghar.com (port 8010)
- Existing MySQL: port 3306
- Existing Redis: port 6379

✅ **Our services use different ports:**
- Our MySQL: port 3307
- Our Redis: port 6380
- Our applications: ports 10030, 8084, 8083

## ⚙️ Configuration

### Environment Files
- `.env` - Active configuration
- `.env.local` - Local development template
- `.env.production` - Production template

### Key Configuration Variables
```bash
# Database
DB_NAME=db_share
DB_USERNAME=root
DB_PASSWORD=your_password

# Application URLs
BACKEND_URL=https://powerbank-api.chargeghar.com
CABINET_BIND_URL=https://cabinet.chargeghar.com
TEST_TOOL_URL=https://test.chargeghar.com

# YBT IoT Configuration
YBT_ENABLE=true
YBT_ACCESS_KEY=your_access_key
YBT_ACCESS_SECRET=your_access_secret
```

## 🔍 Monitoring & Debugging

### Check Service Status
```bash
docker-compose ps
```

### View Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
docker-compose logs -f web-cabinet-bind
docker-compose logs -f web-test-tool
```

### Health Checks
```bash
# Backend API
curl https://powerbank-api.chargeghar.com/api/common/config/pre_auth_amount

# Frontend applications
curl -I https://cabinet.chargeghar.com
curl -I https://test.chargeghar.com
```

## 🛠️ Development

### Local Development Workflow
1. Switch to local environment: `./switch-env.sh local`
2. Make code changes
3. Rebuild and restart: `./build.sh && ./start.sh`
4. Test locally at http://localhost:10030

### Production Deployment Workflow
1. Test locally first
2. Switch to production: `./switch-env.sh production`
3. Update production credentials in `.env`
4. Deploy: `sudo ./deploy-production.sh --setup-nginx --setup-ssl`

## 🔒 Security Features

- Non-root containers
- Environment-based secrets
- SSL/TLS encryption
- Network isolation
- Health checks and monitoring
- No hardcoded credentials

## 📚 API Documentation

Interactive API documentation is available at:
- Local: http://localhost:10030/doc.html
- Production: https://powerbank-api.chargeghar.com/doc.html

## 🐛 Troubleshooting

### Common Issues

#### Port Conflicts
```bash
# Check what's using a port
netstat -tulpn | grep :8080

# Stop conflicting services if needed
docker-compose down
```

#### Service Won't Start
```bash
# Check logs
docker-compose logs backend

# Restart specific service
docker-compose restart backend
```

#### Nginx Issues
```bash
# Test nginx configuration
sudo nginx -t

# Reload nginx
sudo systemctl reload nginx
```

### Reset Everything
```bash
# Stop and remove all containers and volumes
docker-compose down -v --rmi all

# Clean up Docker system
docker system prune -a -f
```

## 📞 Support

For deployment issues:
1. Run verification: `./verify-deployment.sh`
2. Check logs: `docker-compose logs -f`
3. Verify configuration: Review `.env` file
4. Test connectivity: Use health check endpoints

## 🎯 Production Checklist

- [ ] DNS records configured for new subdomains
- [ ] SSL certificates installed
- [ ] Production credentials configured in `.env`
- [ ] Nginx configurations deployed
- [ ] All services running and healthy
- [ ] Health checks passing
- [ ] No conflicts with existing services
- [ ] Backup strategy implemented

---

**Ready for production deployment with zero conflicts!** 🚀