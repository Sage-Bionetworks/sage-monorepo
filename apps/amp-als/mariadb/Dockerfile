FROM mariadb:10.9.3

COPY docker-entrypoint-initdb.d /docker-entrypoint-initdb.d
COPY docker-healthcheck /usr/local/bin/

HEALTHCHECK CMD ["docker-healthcheck"]