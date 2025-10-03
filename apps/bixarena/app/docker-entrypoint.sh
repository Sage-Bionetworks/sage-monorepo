#!/usr/bin/env bash
set -e

if [ "$1" = 'python' ]; then
    cd ${APP_DIR}
    : "${APP_PORT:=8100}"
    # Use installed console script (provided by pyproject [project.scripts])
    exec gosu "$APP_USERNAME" bixarena-app \
        --host 0.0.0.0 \
        --port "${APP_PORT}"
fi

exec "$@"