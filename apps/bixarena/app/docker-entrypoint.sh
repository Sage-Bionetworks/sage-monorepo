#!/usr/bin/env bash
set -e

if [ "$1" = 'python' ]; then
    cd ${APP_DIR}
    gosu "$APP_USERNAME" envsubst < model_config.template.json > model_config.json
    exec gosu "$APP_USERNAME" python -m fastchat.serve.gradio_web_server_multi \
        --controller-url "" \
        --port "${APP_PORT}" \
        --register-api-endpoint-file model_config.json
fi

exec "$@"