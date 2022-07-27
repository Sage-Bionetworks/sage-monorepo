#!/usr/bin/env sh

cd /usr/share/nginx/html/config
envsubst < config.json.template > config.json
