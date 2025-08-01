#!/usr/bin/env bash
cd ${APP_DIR}

python generate_model_config.py

# Run the application as app user
exec gosu "$APP_USERNAME" python -m fastchat.serve.gradio_web_server_multi \
    --controller-url "http://${CONTROLLER_HOST}:${CONTROLLER_PORT}" \
    --port "${APP_PORT}" \
    --register-api-endpoint-file model_config.json