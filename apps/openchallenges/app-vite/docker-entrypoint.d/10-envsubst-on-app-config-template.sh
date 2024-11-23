#!/usr/bin/env sh

# Generate 'config.json' from 'config.json.template' using environment variables.
cd "${APP_DIR}/browser/config"
envsubst < config.json.template > config.json
