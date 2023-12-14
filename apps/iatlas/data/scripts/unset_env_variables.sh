#!/bin/bash

# Defined some useful colors for echo outputs.
# Use Green for a successful action.
GREEN="\033[0;32m"
# No Color (used to stop or reset a color).
NC='\033[0m'


# Unset any previously set environment variables.
unset CRYPTOGRAPHY_DONT_BUILD_RUST
unset IATLAS_DOT_ENV_FILE
unset IATLAS_ENV
unset DB_NAME
unset DB_HOST
unset DB_PORT
unset DB_PW
unset DB_USER
unset FILEVIEW_VERSION
unset SCHEMA_URL
unset SCHEMA_PROJECT_ID
unset SCHEMA_ASSET_VIEW_ID
unset SCHEMA_AUTH_TOKEN

>&2 echo -e "${GREEN}* Environment variables unset.${NC}"