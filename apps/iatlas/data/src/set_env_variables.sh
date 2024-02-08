#!/bin/bash

GREEN="\033[0;32m"
YELLOW="\033[1;33m"
# No Color
NC='\033[0m'

# The project directory.
PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && cd ../ && pwd )"
>&2 echo -e "${GREEN}Current project dir - ${PROJECT_DIR}${NC}"

# .env-dev loading in the shell
IATLAS_DOT_ENV_FILE=${PROJECT_DIR}/.env
dotenv() {
    if [ -f "${IATLAS_DOT_ENV_FILE}" ]
    then
        set -a
        [ -f ${IATLAS_DOT_ENV_FILE} ] && . ${IATLAS_DOT_ENV_FILE}
        set +a
    else
        IATLAS_DOT_ENV_FILE=${PROJECT_DIR}/.env-none
        >&2 echo -e "${YELLOW}Not using a .env file${NC}"
    fi
}
# Run dotenv
dotenv

# If environment variables are set, use them. If not, use the defaults.
export CRYPTOGRAPHY_DONT_BUILD_RUST=${CRYPTOGRAPHY_DONT_BUILD_RUST:-true}
export IATLAS_DOT_ENV_FILE=${IATLAS_DOT_ENV_FILE}
export IATLAS_ENV=${IATLAS_ENV:-dev}
# database variables
export DB_NAME=${DB_NAME:-iatlas_dev}
export DB_HOST=${DB_HOST:-localhost}
export DB_PORT=${DB_PORT:-2432}
export DB_PW=${DB_PW:-changeme}
export DB_USER=${DB_USER:-postgres}
# schema variables
# Curently using test database variables
export SCHEMA_URL=${SCHEMA_URL:-https://raw.githubusercontent.com/Sage-Bionetworks/Schematic-DB-Test-Schemas/main/test_schema.jsonld}
export SCHEMA_PROJECT_ID=${SCHEMA_PROJECT_ID: syn47994571}
export SCHEMA_ASSET_VIEW_ID=${SCHEMA_ASSET_VIEW_ID: syn47997084}

echo $DB_USER
