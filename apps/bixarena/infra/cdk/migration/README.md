# BixArena Database Migration Scripts

This directory contains scripts to migrate data from the BixArena stage database to a target database (local or production).

## Overview

The migration process consists of three main steps:

1. **Export** - Export data from the stage database to SQL files
2. **Truncate** - Clear existing data from the target database
3. **Import** - Import the exported data into the target database
4. **Validate** - Verify data integrity and foreign key relationships

## Prerequisites

### For Stage → Local Migration

1. **Local Database Running**: Ensure your local BixArena postgres container is running via Docker Compose
2. **SSM Tunnel to Stage**: Set up an SSM port forward to the stage database
   ```bash
   # Example SSM tunnel command (adjust based on your setup)
   aws ssm start-session --target <bastion-instance-id> \
     --document-name AWS-StartPortForwardingSessionToRemoteHost \
     --parameters '{"host":["<stage-rds-endpoint>"],"portNumber":["5432"],"localPortNumber":["5433"]}'
   ```
3. **Database Credentials**: Obtain stage database credentials from AWS Secrets Manager

### For Stage → Production Migration

1. **Production Database Access**: Set up SSM tunnel to production database
2. **RDS Snapshot**: Create a manual snapshot of the production database before migration
3. **Maintenance Window**: Schedule appropriate downtime
4. **Rollback Plan**: Document rollback procedure using the RDS snapshot

## Scripts

### 1. export-stage-data.sh

Exports data from the stage database to SQL files.

**Usage:**

```bash
cd /workspaces/sage-monorepo/apps/bixarena/infra/cdk

# Set environment variables
export STAGE_HOST=localhost
export STAGE_PORT=5433  # SSM tunnel port
export STAGE_USER=postgres
export STAGE_PASSWORD=your_password
export STAGE_DB=bixarena

# Run export
./migration/export-stage-data.sh
```

**Output:**

- Creates timestamped directory: `./migration/exports/YYYYMMDD_HHMMSS/`
- Exports each table to a separate SQL file
- Creates combined file: `all_data.sql`
- Creates symlink: `./migration/exports/latest/` → current export

### 2. truncate-tables.sql

Truncates all data tables in the target database (preserves schema).

**Usage:**

```bash
# For local database
psql -h localhost -p 21000 -U postgres -d bixarena \
  -f ./migration/truncate-tables.sql

# For production database (via SSM tunnel)
PGPASSWORD=prod_password psql -h localhost -p 5434 -U postgres -d bixarena \
  -f ./migration/truncate-tables.sql
```

**Note:** This script does NOT truncate `flyway_schema_history` tables.

### 3. import-data.sh

Imports data from exported SQL files into the target database.

**Usage:**

```bash
cd /workspaces/sage-monorepo/apps/bixarena/infra/cdk

# For local database
export TARGET_HOST=localhost
export TARGET_PORT=21000
export TARGET_USER=postgres
export TARGET_PASSWORD=changeme
export TARGET_DB=bixarena

# Run import
./migration/import-data.sh
```

**For production:**

```bash
export TARGET_HOST=localhost
export TARGET_PORT=5434  # SSM tunnel port
export TARGET_USER=postgres
export TARGET_PASSWORD=prod_password
export TARGET_DB=bixarena

./migration/import-data.sh
```

**What it does:**

1. Checks database connectivity
2. Truncates existing data
3. Imports tables in correct dependency order
4. Runs validation checks

### 4. validate-migration.sql

Validates data integrity after migration.

**Usage:**

```bash
# For local database
psql -h localhost -p 21000 -U postgres -d bixarena \
  -f ./migration/validate-migration.sql

# For production database
PGPASSWORD=prod_password psql -h localhost -p 5434 -U postgres -d bixarena \
  -f ./migration/validate-migration.sql
```

**Checks performed:**

- Row counts for all tables
- Foreign key integrity (orphaned records)
- Data quality (active models, enabled users, etc.)
- Sample data verification

## Complete Migration Workflow

### Phase 1: Stage → Local (Testing)

```bash
# 1. Start local database
cd /workspaces/sage-monorepo/apps/bixarena
docker-compose up -d bixarena-postgres

# 2. Wait for database to be ready
docker-compose ps

# 3. Ensure Flyway migrations have run (start the API service)
# This creates the schema in the local database

# 4. Set up SSM tunnel to stage database
# (Run in separate terminal)
aws ssm start-session --target <bastion-instance-id> \
  --document-name AWS-StartPortForwardingSessionToRemoteHost \
  --parameters '{"host":["<stage-rds-endpoint>"],"portNumber":["5432"],"localPortNumber":["5433"]}'

# 5. Export data from stage
cd /workspaces/sage-monorepo/apps/bixarena/infra/cdk
export STAGE_HOST=localhost
export STAGE_PORT=5433
export STAGE_USER=postgres
export STAGE_PASSWORD=<from-secrets-manager>
export STAGE_DB=bixarena
./migration/export-stage-data.sh

# 6. Import data to local
export TARGET_HOST=localhost
export TARGET_PORT=21000
export TARGET_USER=postgres
export TARGET_PASSWORD=changeme
export TARGET_DB=bixarena
./migration/import-data.sh

# 7. Test the application with local database
# Verify functionality works as expected

# 8. Review validation results
# Check for any orphaned records or integrity issues
```

### Phase 2: Stage → Production

```bash
# 1. Create RDS snapshot (via AWS Console or CLI)
aws rds create-db-snapshot \
  --db-instance-identifier bixarena-prod-database \
  --db-snapshot-identifier bixarena-prod-pre-migration-$(date +%Y%m%d)

# 2. Set up SSM tunnels (in separate terminals)
# Stage tunnel (if not already running)
aws ssm start-session --target <bastion-instance-id> \
  --parameters '{"host":["<stage-rds>"],"portNumber":["5432"],"localPortNumber":["5433"]}'

# Prod tunnel
aws ssm start-session --target <bastion-instance-id> \
  --parameters '{"host":["<prod-rds>"],"portNumber":["5432"],"localPortNumber":["5434"]}'

# 3. Export from stage (if not already done)
export STAGE_HOST=localhost
export STAGE_PORT=5433
export STAGE_USER=postgres
export STAGE_PASSWORD=<from-secrets-manager>
export STAGE_DB=bixarena
./migration/export-stage-data.sh

# 4. Put application in maintenance mode
# (Update CDK stack or manually stop ECS tasks)

# 5. Import to production
export TARGET_HOST=localhost
export TARGET_PORT=5434
export TARGET_USER=postgres
export TARGET_PASSWORD=<from-secrets-manager>
export TARGET_DB=bixarena
./migration/import-data.sh

# 6. Validate production data
PGPASSWORD=<prod-password> psql -h localhost -p 5434 -U postgres -d bixarena \
  -f ./migration/validate-migration.sql

# 7. Run smoke tests against production database

# 8. Bring application out of maintenance mode

# 9. Monitor application logs for any issues
# IMPORTANT: Watch auth service logs during initial user logins
# Each user's external_id will be updated on first login (different OAuth clients)
# This is expected behavior - logs will show:
# "Updating existing external account: provider=synapse oldExternalId=... newExternalId=..."
```

## User Authentication After Production Migration

After migrating data from stage to production, here's what happens when users log in:

### First Login Post-Migration

1. **User authenticates** with Synapse using prod OAuth client
2. **Synapse returns** a new `sub` (external_id) specific to the prod OAuth client
3. **Auth service finds** the user by `username` (from stage data)
4. **Auth service detects** the user has an existing external_account for Synapse provider
5. **Auth service updates** the external_id from stage value to prod value
6. **User is logged in** successfully - no difference from their perspective

### What Gets Updated

- `auth.external_account.external_id` - Updated to prod OAuth client's ID
- `auth.external_account.external_username` - Updated if changed in Synapse
- `auth.external_account.external_email` - Updated if changed in Synapse
- `auth.user.last_login_at` - Updated to current timestamp

### Expected Logs

When monitoring the auth service after production launch, you'll see these logs for each user's first login:

```
INFO: Linking existing user to new provider: userId=... username=tschaffter provider=synapse externalId=<new-prod-id>
INFO: Updating existing external account: provider=synapse oldExternalId=<stage-id> newExternalId=<prod-id> userId=...
```

This is **normal and expected** behavior. Users will not experience any issues or notice any difference.

### Subsequent Logins

After the first login, the external_id is now correct for the prod OAuth client. Future logins will follow the normal path:

```
INFO: Existing user login: userId=... username=... provider=synapse externalId=<prod-id>
```

## Rollback Procedure

If the migration fails or issues are discovered:

1. **Stop the application** (if not already in maintenance mode)
2. **Restore from RDS snapshot:**
   ```bash
   # This will create a new database instance from the snapshot
   # You'll need to update your CDK stack to point to the new instance
   # Or restore to the existing instance (requires deletion/recreation)
   ```
3. **Verify restored data**
4. **Restart application**
5. **Document what went wrong**

## Troubleshooting

### Export fails with connection timeout

- Verify SSM tunnel is running
- Check database credentials
- Ensure security groups allow connection from bastion

### Import fails with foreign key constraint error

- Check that tables are being imported in the correct order
- Verify the export completed successfully
- Check for data inconsistencies in the source database

### Validation shows orphaned records

- Review the specific foreign key that's broken
- Check if data was partially imported
- May need to re-export and re-import

### Large message table slows migration

- Consider breaking the import into smaller batches
- Use `COPY` command instead of `INSERT` statements for better performance
- Temporarily disable indexes during import (advanced)

### Login fails after migration with "duplicate key" error on external_account

- **Cause**: The auth service tried to create a new external account when one already existed
- **Solution**: This was fixed in the auth service code (UserService.java) to update existing external accounts instead of creating duplicates
- **Expected behavior after fix**: On first login, the external_id gets updated to match the current environment's OAuth client
- Check logs for: `Updating existing external account: provider=synapse oldExternalId=... newExternalId=...`

## Important Notes

1. **Flyway Schema History**: Never migrate the `flyway_schema_history` tables. These are managed by Flyway.

2. **Reference Data**: The V2 and V3 migrations contain reference data (models, prompts). This data will be overwritten by the stage data during migration.

3. **Dev Mock Data**: The V999 migration contains dev-only mock data. This should NOT exist in stage or prod.

4. **OAuth Client Differences**: Different environments use different Synapse OAuth clients because they have different redirect URIs. This means:

   - Stage and prod MUST use different OAuth clients (different redirect URIs)
   - The `external_id` in the `auth.external_account` table is OAuth client-specific
   - When migrating stage → prod, ALL users will have different external IDs
   - **On first login after migration**, each user's external_id will be automatically updated to match the prod OAuth client
   - The auth service handles this transparently - users will not notice any difference
   - This is expected behavior and handled automatically by the UserService
   - Monitor logs during initial post-migration logins for: `Updating existing external account: provider=synapse oldExternalId=... newExternalId=...`

5. **Data Volumes**: Monitor disk space during export. The `message` table can be large for chatbot applications.

6. **Downtime**: Plan for appropriate maintenance window. Migration time depends on data volume (estimate ~1-2 hours for production).

7. **Testing**: Always test the migration on local database first before attempting production migration.

## File Structure

```
migration/
├── README.md                    # This file
├── export-stage-data.sh         # Export script
├── import-data.sh               # Import script
├── truncate-tables.sql          # Truncation script
├── validate-migration.sql       # Validation script
└── exports/                     # Export directory (git-ignored)
    ├── 20241204_120000/         # Timestamped exports
    │   ├── api_leaderboard.sql
    │   ├── api_model.sql
    │   ├── ...
    │   └── all_data.sql
    └── latest/                  # Symlink to latest export
```

## Support

For issues or questions:

1. Review the troubleshooting section above
2. Check CloudWatch logs for database errors
3. Verify Flyway migration versions match between environments
4. Contact the infrastructure team for AWS-specific issues
