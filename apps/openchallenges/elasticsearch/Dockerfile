FROM docker.elastic.co/elasticsearch/elasticsearch:8.10.2

COPY docker-healthcheck /usr/local/bin/

HEALTHCHECK --interval=2s --timeout=3s --retries=20 --start-period=5s \
  CMD ["docker-healthcheck"]