FROM docker.elastic.co/kibana/kibana:8.10.2

COPY docker-healthcheck /usr/local/bin/

HEALTHCHECK --interval=2s --timeout=3s --retries=20 --start-period=5s \
  CMD ["docker-healthcheck"]