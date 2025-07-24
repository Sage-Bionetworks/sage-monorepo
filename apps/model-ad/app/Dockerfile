FROM mirror.gcr.io/node:22.17.0-alpine3.21

ENV APP_DIR=/opt/app

RUN apk add --no-cache curl envsubst jq su-exec

WORKDIR /
COPY apps/model-ad/app/docker-entrypoint.sh .
COPY apps/model-ad/app/docker-entrypoint.d /docker-entrypoint.d
RUN chmod +x docker-entrypoint.sh /docker-entrypoint.d/*

WORKDIR ${APP_DIR}
COPY dist/apps/model-ad/app/server ./server
COPY dist/apps/model-ad/app/browser ./browser

HEALTHCHECK --interval=2s --timeout=3s --retries=20 --start-period=5s \
  CMD curl --fail --silent "localhost:4200/health" | jq '.status' | grep UP || exit 1

EXPOSE 4200

ENTRYPOINT ["/docker-entrypoint.sh"]

CMD ["node", "server/server.mjs"]
