FROM mariadb:10.9.3

COPY docker-healthcheck /usr/local/bin/
COPY my.cnf /etc/

HEALTHCHECK CMD ["docker-healthcheck"]