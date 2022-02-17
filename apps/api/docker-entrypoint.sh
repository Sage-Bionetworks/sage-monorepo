#!/usr/bin/env bash
set -e

if [ "$1" = 'uwsgi' ] || [ "$1" = 'python' ]; then
    cd ${APP_DIR}
    exec gosu www-data "$@"
fi

exec "$@"