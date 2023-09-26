# Serve app with nginx server
FROM nginx:1.23.2-alpine

RUN apk add --no-cache jq

COPY dist/apps/openchallenges/app/browser/browser /usr/share/nginx/html
COPY docker/nginx /etc/nginx/.
COPY docker/10-envsubst-on-app-config-template.sh /docker-entrypoint.d/.

RUN chmod +x /docker-entrypoint.d/10-envsubst-on-app-config-template.sh

HEALTHCHECK --interval=2s --timeout=3s --retries=20 --start-period=5s \
  CMD curl --fail --silent "localhost:4200/health" | jq '.status' | grep UP || exit 1

EXPOSE 4200

CMD ["nginx", "-g", "daemon off;"]
