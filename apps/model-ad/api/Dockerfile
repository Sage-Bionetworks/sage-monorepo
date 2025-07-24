FROM mirror.gcr.io/node:18.20.2-alpine

ENV APP_DIR=/app

RUN apk add --no-cache curl jq su-exec

WORKDIR /
COPY apps/model-ad/api/docker-entrypoint.sh .
RUN chmod +x docker-entrypoint.sh

WORKDIR ${APP_DIR}
COPY dist/apps/model-ad/api ${APP_DIR}

HEALTHCHECK --interval=2s --timeout=3s --retries=20 --start-period=5s \
  CMD curl --fail --silent "localhost:3333/health" | jq '.status' | grep UP || exit 1

EXPOSE 3333

ENTRYPOINT ["/docker-entrypoint.sh"]

CMD ["node", "main.js"]
