#!/bin/bash

GREEN="\033[0;32m"
YELLOW="\033[1;33m"
# No Color
NC='\033[0m'

# The local scripts directory (assumes this file is in the root of the project folder).
PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
>&2 echo -e "${GREEN}Current project dir - ${PROJECT_DIR}${NC}"

# .env loading in the shell
ENV_FILE=${PROJECT_DIR}/.env-dev
dotenv() {
    if [ -f "${ENV_FILE}" ]
    then
      set -a
      [ -f ${ENV_FILE} ] && . ${ENV_FILE}
      set +a
    else
      >&2 echo -e "${YELLOW}No .env file found${NC}"
    fi
}
# Run dotenv
dotenv

# docker-compose build --tag iatlas-api-dev:1.0.0
docker-compose up -d

docker exec -ti iatlas-api-dev bash