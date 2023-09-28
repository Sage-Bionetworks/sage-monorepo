#!/usr/bin/env sh

cd "${APP_DIR}/dist/apps/openchallenges/app/browser/browser/config"
envsubst < config.json.template > config.json
