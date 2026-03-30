#!/usr/bin/env sh
set -e

if [ "$1" = 'node' ]; then
    cd ${APP_DIR}
    exec su-exec node "$@"
fi

exec "$@"