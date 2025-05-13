#!/usr/bin/env bash
set -e

if [ "$1" = 'python' ]; then
    cd ${APP_DIR}
    exec gosu "$APP_USERNAME" "$@"
fi

exec "$@"