#!/usr/bin/env sh
set -e

/docker-entrypoint.d/10-envsubst-on-app-config-template.sh

if [ "$1" = 'node' ]; then
    cd ${APP_DIR}
    exec su-exec node "$@"
fi

exec "$@"