# Migration to Single Domain Architecture

## Overview

This document describes the migration from multiple subdomains to a single domain architecture as recommended by the manufacturer.

## Architecture Change

### Before (Multiple Subdomains)
```
https://powerbank-api.chargeghar.com  → Backend API
https://cabinet.chargeghar.com        → Cabinet Bind Tool
https://test.chargeghar.com          → Test Tool
```

### After (Single Domain)
```
https://powerbank-api.chargeghar.com/         → Backend API
https://powerbank-api.chargeghar.com/binding/ → Cabinet Bind Tool
https://powerbank-api.chargeghar.com/test/    → Test Tool
https://powerbank-api.chargeghar.com/factory/ → Factory Tools (future)
```

## Benefits

1. **No CORS Issues**: All services under same domain
2. **Single SSL Certificate**: Only need one certificate
3. **Simplified DNS**: Only one A record needed
4. **Better Security**: Centralized access control
5. **Easier Maintenance**: Single nginx configuration
6. **Manufacturer Approved**: Follows recommended structure

## Files Changed

### Configuration Files
- `nginx/sites-available/powerbank-api.chargeghar.com` - Updated to serve all tools
- `.env` - Updated URLs to use single domain
- `.env.production` - Updated URLs to use single domain
- `switch-env.sh` - Updated URL references
- `deploy.sh` - Updated for single domain checks
- `deploy-production.sh` - Updated for single domain deployment
- `build.sh` - Updated URL references
- `start.sh` - Updated URL references

### New Files
- `cleanup-old-subdomains.sh` - Script to remove old configurations
- `MIGRATION_TO_SINGLE_DOMAIN.md` - This documentation

## Deployment Steps

1. **Deploy Updated Configuration**:
   ```bash
   sudo ./deploy.sh
   ```

2. **Test New URLs**:
   ```bash
   # Backend API
   curl https://powerbank-api.chargeghar.com/api/common/config/pre_auth_amount
   
   # Cabinet Bind Tool
   curl https://powerbank-api.chargeghar.com/binding/
   
   # Test Tool
   curl https://powerbank-api.chargeghar.com/test/
   ```

3. **Clean Up Old Configurations** (after testing):
   ```bash
   sudo ./cleanup-old-subdomains.sh
   ```

## DNS Changes Required

### Remove Old A Records
- Remove: `cabinet.chargeghar.com` A record
- Remove: `test.chargeghar.com` A record

### Keep Main A Record
- Keep: `powerbank-api.chargeghar.com` A record pointing to server IP

## Testing Checklist

- [ ] Backend API accessible at `https://powerbank-api.chargeghar.com/`
- [ ] Cabinet tool accessible at `https://powerbank-api.chargeghar.com/binding/`
- [ ] Test tool accessible at `https://powerbank-api.chargeghar.com/test/`
- [ ] API documentation at `https://powerbank-api.chargeghar.com/doc.html`
- [ ] No CORS errors in browser console
- [ ] All API calls working correctly
- [ ] SSL certificate valid for main domain

## Rollback Plan

If issues occur, you can rollback by:

1. Restore old nginx configurations:
   ```bash
   git checkout HEAD~1 -- nginx/sites-available/
   ```

2. Restore old environment variables:
   ```bash
   git checkout HEAD~1 -- .env .env.production
   ```

3. Redeploy:
   ```bash
   sudo ./deploy.sh
   ```

## Support

For issues or questions, refer to:
- Project documentation in `Docs/` directory
- Manufacturer recommendations in `issues.txt`
- Deployment logs: `docker compose logs -f`