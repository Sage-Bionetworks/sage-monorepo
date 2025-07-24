FROM mirror.gcr.io/postgres:16.9-bullseye

COPY docker-healthcheck /usr/local/bin/

HEALTHCHECK CMD ["docker-healthcheck"]