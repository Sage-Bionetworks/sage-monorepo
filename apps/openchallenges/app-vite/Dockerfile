FROM node:20.7.0-alpine

ENV APP_DIR=/opt/app

RUN apk add --no-cache curl envsubst jq su-exec

WORKDIR /
COPY apps/openchallenges/app-vite/docker-entrypoint.sh .
COPY apps/openchallenges/app-vite/docker-entrypoint.d /docker-entrypoint.d
RUN chmod +x docker-entrypoint.sh /docker-entrypoint.d/*

WORKDIR ${APP_DIR}
COPY dist/apps/openchallenges/app-vite/server ./server
COPY dist/apps/openchallenges/app-vite/browser ./browser

HEALTHCHECK --interval=2s --timeout=3s --retries=20 --start-period=5s \
  CMD curl --fail --silent "localhost:4200/health" | jq '.status' | grep UP || exit 1

EXPOSE 4200

ENTRYPOINT ["/docker-entrypoint.sh"]

CMD ["node", "server/server.mjs"]
