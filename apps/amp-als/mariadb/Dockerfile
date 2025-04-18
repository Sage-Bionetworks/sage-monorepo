FROM mariadb:11.7.2

COPY docker-entrypoint-initdb.d /docker-entrypoint-initdb.d
COPY docker-healthcheck /usr/local/bin/

HEALTHCHECK CMD ["docker-healthcheck"]