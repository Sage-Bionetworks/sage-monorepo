#!/bin/bash

# Export data from Stage database to SQL files
# This script exports data only (not schema) from the stage database
#
# Prerequisites:
# - SSM tunnel to stage database must be running
# - Set environment variables: STAGE_HOST, STAGE_PORT, STAGE_USER, STAGE_PASSWORD, STAGE_DB
#
# Example usage:
# export STAGE_HOST=localhost
# export STAGE_PORT=5432
# export STAGE_USER=postgres
# export STAGE_PASSWORD=your_password
# export STAGE_DB=bixarena
# ./export-stage-data.sh

set -e

# Configuration
EXPORT_DIR="./migration/exports"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
EXPORT_DIR_TIMESTAMPED="${EXPORT_DIR}/${TIMESTAMP}"

# Database connection details (override with environment variables)
DB_HOST=${STAGE_HOST:-localhost}
DB_PORT=${STAGE_PORT:-5432}
DB_USER=${STAGE_USER:-postgres}
DB_NAME=${STAGE_DB:-bixarena}
DB_PASSWORD=${STAGE_PASSWORD}

# Color output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== BixArena Stage Data Export ===${NC}"
echo "Timestamp: ${TIMESTAMP}"
echo "Export directory: ${EXPORT_DIR_TIMESTAMPED}"
echo "Database: ${DB_HOST}:${DB_PORT}/${DB_NAME}"
echo ""

# Create export directory
mkdir -p "${EXPORT_DIR_TIMESTAMPED}"

# Set password for pg_dump
export PGPASSWORD="${DB_PASSWORD}"

# Function to export a table
export_table() {
    local schema=$1
    local table=$2
    local output_file="${EXPORT_DIR_TIMESTAMPED}/${schema}_${table}.sql"

    echo -e "${YELLOW}Exporting ${schema}.${table}...${NC}"

    pg_dump \
        -h "${DB_HOST}" \
        -p "${DB_PORT}" \
        -U "${DB_USER}" \
        -d "${DB_NAME}" \
        --schema="${schema}" \
        --table="${schema}.${table}" \
        --data-only \
        --inserts \
        --no-owner \
        --no-acl \
        -f "${output_file}"

    if [ $? -eq 0 ]; then
        local row_count=$(grep -c "INSERT INTO" "${output_file}" || echo "0")
        echo -e "${GREEN}✓ Exported ${schema}.${table} (${row_count} rows)${NC}"
    else
        echo -e "${RED}✗ Failed to export ${schema}.${table}${NC}"
        exit 1
    fi
}

# Export API schema tables (in dependency order)
echo -e "\n${GREEN}=== Exporting API Schema ===${NC}"
export_table "api" "leaderboard"
export_table "api" "model"
export_table "api" "example_prompt"
export_table "api" "leaderboard_snapshot"
export_table "api" "leaderboard_entry"
export_table "api" "message"
export_table "api" "battle"
export_table "api" "battle_round"
export_table "api" "battle_evaluation"
export_table "api" "model_error"

# Export AUTH schema tables (in dependency order)
echo -e "\n${GREEN}=== Exporting AUTH Schema ===${NC}"
export_table "auth" "user"
export_table "auth" "external_account"

# Create a combined export file for convenience
echo -e "\n${YELLOW}Creating combined export file...${NC}"
cat "${EXPORT_DIR_TIMESTAMPED}"/*.sql > "${EXPORT_DIR_TIMESTAMPED}/all_data.sql"
echo -e "${GREEN}✓ Combined export: ${EXPORT_DIR_TIMESTAMPED}/all_data.sql${NC}"

# Create a latest symlink
rm -f "${EXPORT_DIR}/latest"
ln -s "${TIMESTAMP}" "${EXPORT_DIR}/latest"

# Print summary
echo -e "\n${GREEN}=== Export Complete ===${NC}"
echo "Files exported to: ${EXPORT_DIR_TIMESTAMPED}"
echo "Latest export link: ${EXPORT_DIR}/latest"
echo ""
echo "File listing:"
ls -lh "${EXPORT_DIR_TIMESTAMPED}"

# Cleanup password variable
unset PGPASSWORD

echo -e "\n${GREEN}✓ Stage data export completed successfully!${NC}"
