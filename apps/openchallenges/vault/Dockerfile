FROM vault:1.12.2

ARG SERVER_PORT="8200"

RUN apk add curl jq

HEALTHCHECK --interval=2s --timeout=3s --retries=5 --start-period=2s \
  CMD /tools/curl --fail --silent "localhost:${SERVER_PORT}/v1/sys/health" | grep true || exit 1