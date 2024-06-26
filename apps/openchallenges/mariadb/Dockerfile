FROM mariadb:10.9.3

COPY docker-entrypoint-initdb.d /docker-entrypoint-initdb.d
COPY docker-healthcheck /usr/local/bin/
COPY my.cnf /etc/mysql/

HEALTHCHECK CMD ["docker-healthcheck"]