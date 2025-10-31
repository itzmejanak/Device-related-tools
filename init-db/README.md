# Database Initialization Scripts

This directory contains SQL scripts that are automatically executed when the MySQL container starts for the first time.

## Files:

- `01-create-cabinet-table.sql` - Creates the `bz_cabinet` table required for device binding functionality

## How it works:

1. When MySQL container starts, it automatically executes all `.sql` files in this directory
2. Files are executed in alphabetical order (hence the `01-` prefix)
3. The `bz_cabinet` table is created with all necessary fields and constraints
4. This ensures the database schema is ready before the Spring Boot application starts

## Table Structure:

The `bz_cabinet` table stores:
- Device binding information (Station SN and IMEI)
- Device status and metadata
- Network and location information
- Timestamps for tracking

## Important Notes:

- Scripts only run on **first container startup**
- If you need to recreate the database, remove the MySQL volume: `docker-compose down -v`
- The table uses `utf8mb4` charset for proper Unicode support
- Unique constraints ensure no duplicate Station SN or IMEI values