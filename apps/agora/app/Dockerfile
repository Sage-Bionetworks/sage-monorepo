FROM nginx:1.27.4-alpine

RUN apk add --no-cache jq

WORKDIR /
COPY dist/apps/agora/app/browser/browser /usr/share/nginx/html
COPY apps/agora/app/docker/nginx /etc/nginx/.
COPY apps/agora/app/docker/10-envsubst-on-app-config-template.sh /docker-entrypoint.d/.
RUN chmod +x /docker-entrypoint.d/10-envsubst-on-app-config-template.sh

HEALTHCHECK --interval=2s --timeout=3s --retries=20 --start-period=5s \
  CMD curl --fail --silent "localhost:4200/health" | jq '.status' | grep UP || exit 1

EXPOSE 4200

ENTRYPOINT ["/docker-entrypoint.sh"]

CMD ["nginx", "-g", "daemon off;"]
