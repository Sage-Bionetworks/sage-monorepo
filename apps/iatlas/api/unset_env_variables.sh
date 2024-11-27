#!/bin/bash

# Defined some useful colors for echo outputs.
# Use Green for a successful action.
GREEN="\033[0;32m"
# No Color (used to stop or reset a color).
NC='\033[0m'


# Unset any previously set environment variables.
unset DOT_ENV_FILE
unset FLASK_APP
unset FLASK_ENV
unset FLASK_RUN_PORT
unset POSTGRES_DB
unset POSTGRES_HOST
unset POSTGRES_PORT
unset POSTGRES_PASSWORD
unset POSTGRES_USER
unset PYTHON_IMAGE_VERSION
unset SNAKEVIZ_PORT

>&2 echo -e "${GREEN}* Environment variables unset.${NC}"