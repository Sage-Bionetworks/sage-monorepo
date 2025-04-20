#!/usr/bin/env sh

# Generate 'config.json' from 'config.json.template' using environment variables.
cd "${APP_DIR}/browser/config"
envsubst < config.json.template | jq '
  with_entries(
    if .value == "true" then .value = true
    elif .value == "false" then .value = false
    else .
    end
  )
' > config.json
