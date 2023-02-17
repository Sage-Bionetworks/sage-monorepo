FROM vault:1.12.2

RUN apk add curl jq

HEALTHCHECK --interval=2s --timeout=3s --retries=20 --start-period=5s \
  CMD curl --fail --silent "localhost:${SERVER_PORT}/v1/sys/health" | jq '.initialized' | grep true || exit 1