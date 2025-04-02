#!/usr/bin/env sh

cd "${APP_DIR}/dist/apps/amp-als/app/browser/browser/config"
envsubst < config.json.template > config.json
