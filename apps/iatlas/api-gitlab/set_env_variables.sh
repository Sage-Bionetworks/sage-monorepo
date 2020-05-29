#!/bin/bash

GREEN="\033[0;32m"
YELLOW="\033[1;33m"
# No Color
NC='\033[0m'

# The project directory.
PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
>&2 echo -e "${GREEN}Current project dir - ${PROJECT_DIR}${NC}"

# .env-dev loading in the shell
DOT_ENV_FILE=${PROJECT_DIR}/.env-dev
dotenv() {
    if [ -f "${DOT_ENV_FILE}" ]
    then
        set -a
        [ -f ${DOT_ENV_FILE} ] && . ${DOT_ENV_FILE}
        set +a
    else
        DOT_ENV_FILE=${PROJECT_DIR}/.env-none
        >&2 echo -e "${YELLOW}No .env-dev file found${NC}"
    fi
}
# Run dotenv
dotenv

# If environment variables are set, use them. If not, use the defaults.
export DOT_ENV_FILE=${DOT_ENV_FILE}
export FLASK_APP=${FLASK_APP:-iatlasapi.py}
export FLASK_ENV=${FLASK_ENV:-development}
export FLASK_RUN_PORT=${FLASK_RUN_PORT:-5000}
export PG_DATABASE=${PG_DATABASE:-iatlas_dev}
export PG_HOST=${PG_HOST:-postgres}
export PG_PORT=${PG_PORT:-5432}
export PG_PW=${PG_PW:-docker}
export PG_USER=${PG_USER:-postgres}
export PYTHON_IMAGE_VERSION=${PYTHON_IMAGE_VERSION:-3.8-alpine}
