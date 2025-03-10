#!/usr/bin/env bash
set -e

if [ "$1" = 'python' ] || [ "$1" = 'uwsgi' ]; then
    cd ${APP_DIR}
    exec gosu www-data "$@"
fi

exec "$@"