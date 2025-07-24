FROM mirror.gcr.io/opensearchproject/opensearch:2.19.1

COPY docker-healthcheck /usr/local/bin/

HEALTHCHECK --interval=2s --timeout=3s --retries=20 --start-period=5s \
  CMD ["docker-healthcheck"]