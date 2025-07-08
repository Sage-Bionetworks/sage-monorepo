#!/usr/bin/env sh

cd "${APP_DIR}/browser/config"
envsubst < config.json.template > config.json