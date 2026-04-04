#!/usr/bin/env sh

node /docker-entrypoint.d/create-config-json.js \
  "${APP_DIR}/browser/config/config.json.template" \
  "${APP_DIR}/browser/config/config.json"
