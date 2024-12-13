FROM elasticsearch:7.17.8

COPY docker-healthcheck /usr/local/bin/

HEALTHCHECK --interval=5s --timeout=3s --retries=20 --start-period=5s \
  CMD ["docker-healthcheck"]