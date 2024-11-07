FROM node:20.7.0-alpine

ENV APP_DIR=/app

RUN apk add --no-cache curl envsubst jq su-exec

WORKDIR /
COPY apps/openchallenges/app/docker-entrypoint.sh .
COPY apps/openchallenges/app/docker-entrypoint.d /docker-entrypoint.d
RUN chmod +x docker-entrypoint.sh /docker-entrypoint.d/*

WORKDIR ${APP_DIR}
COPY dist/apps/openchallenges/app/browser/server ${APP_DIR}
# The path of the destination folder must be the same as the path specified in server.ts.
COPY dist/apps/openchallenges/app/browser/browser ${APP_DIR}/dist/apps/openchallenges/app/browser/browser

HEALTHCHECK --interval=2s --timeout=3s --retries=20 --start-period=5s \
  CMD curl --fail --silent "localhost:4200/health" | jq '.status' | grep UP || exit 1

EXPOSE 4200

ENTRYPOINT ["/docker-entrypoint.sh"]

CMD ["node", "main.js"]
