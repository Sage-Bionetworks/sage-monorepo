#!/bin/bash

# Import data into target database from exported SQL files
# This script imports data that was exported using export-stage-data.sh
#
# Prerequisites:
# - Exported data files must exist in ./migration/exports/latest/
# - Target database must have schema created (via Flyway migrations)
# - Set environment variables: TARGET_HOST, TARGET_PORT, TARGET_USER, TARGET_PASSWORD, TARGET_DB
#
# Example usage (for local database):
# export TARGET_HOST=localhost
# export TARGET_PORT=21000
# export TARGET_USER=postgres
# export TARGET_PASSWORD=changeme
# export TARGET_DB=bixarena
# ./import-data.sh

set -e

# Configuration
IMPORT_DIR="./migration/exports/latest"

# Database connection details (override with environment variables)
DB_HOST=${TARGET_HOST:-localhost}
DB_PORT=${TARGET_PORT:-21000}
DB_USER=${TARGET_USER:-postgres}
DB_NAME=${TARGET_DB:-bixarena}
DB_PASSWORD=${TARGET_PASSWORD:-changeme}

# Color output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== BixArena Data Import ===${NC}"
echo "Import directory: ${IMPORT_DIR}"
echo "Target database: ${DB_HOST}:${DB_PORT}/${DB_NAME}"
echo ""

# Check if import directory exists
if [ ! -d "${IMPORT_DIR}" ]; then
    echo -e "${RED}Error: Import directory not found: ${IMPORT_DIR}${NC}"
    echo "Please run export-stage-data.sh first"
    exit 1
fi

# Set password for psql
export PGPASSWORD="${DB_PASSWORD}"

# Function to check database connectivity
check_connection() {
    echo -e "${YELLOW}Checking database connection...${NC}"
    if psql -h "${DB_HOST}" -p "${DB_PORT}" -U "${DB_USER}" -d "${DB_NAME}" -c "SELECT 1" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ Database connection successful${NC}"
    else
        echo -e "${RED}✗ Failed to connect to database${NC}"
        exit 1
    fi
}

# Function to import a table
import_table() {
    local schema=$1
    local table=$2
    local import_file="${IMPORT_DIR}/${schema}_${table}.sql"

    if [ ! -f "${import_file}" ]; then
        echo -e "${RED}✗ File not found: ${import_file}${NC}"
        exit 1
    fi

    echo -e "${YELLOW}Importing ${schema}.${table}...${NC}"

    psql \
        -h "${DB_HOST}" \
        -p "${DB_PORT}" \
        -U "${DB_USER}" \
        -d "${DB_NAME}" \
        -f "${import_file}" \
        -q

    if [ $? -eq 0 ]; then
        # Count rows after import
        local row_count=$(psql -h "${DB_HOST}" -p "${DB_PORT}" -U "${DB_USER}" -d "${DB_NAME}" \
            -t -c "SELECT COUNT(*) FROM ${schema}.${table}" | xargs)
        echo -e "${GREEN}✓ Imported ${schema}.${table} (${row_count} rows)${NC}"
    else
        echo -e "${RED}✗ Failed to import ${schema}.${table}${NC}"
        exit 1
    fi
}

# Check database connection
check_connection

# Truncate existing data
echo -e "\n${YELLOW}=== Step 1: Truncating existing data ===${NC}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
psql \
    -h "${DB_HOST}" \
    -p "${DB_PORT}" \
    -U "${DB_USER}" \
    -d "${DB_NAME}" \
    -f "${SCRIPT_DIR}/truncate-tables.sql"

# Import API schema tables (in dependency order)
echo -e "\n${GREEN}=== Step 2: Importing API Schema ===${NC}"
import_table "api" "leaderboard"
import_table "api" "model"
import_table "api" "example_prompt"
import_table "api" "leaderboard_snapshot"
import_table "api" "leaderboard_entry"
import_table "api" "message"
import_table "api" "battle"
import_table "api" "battle_round"
import_table "api" "battle_evaluation"
import_table "api" "model_error"

# Import AUTH schema tables (in dependency order)
echo -e "\n${GREEN}=== Step 3: Importing AUTH Schema ===${NC}"
import_table "auth" "user"
import_table "auth" "external_account"

# Run validation
echo -e "\n${YELLOW}=== Step 4: Running validation ===${NC}"
psql \
    -h "${DB_HOST}" \
    -p "${DB_PORT}" \
    -U "${DB_USER}" \
    -d "${DB_NAME}" \
    -f "${SCRIPT_DIR}/validate-migration.sql"

# Cleanup password variable
unset PGPASSWORD

echo -e "\n${GREEN}✓ Data import completed successfully!${NC}"
