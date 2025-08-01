#!/bin/sh
envsubst < /app/models.template.json > /app/models.json
exec python -m fastchat.serve.gradio_web_server_multi \
  --controller-url "http://${CONTROLLER_HOST}:${CONTROLLER_PORT}" \
  --port 8100 \
  --register-api-endpoint-file /app/models.json
