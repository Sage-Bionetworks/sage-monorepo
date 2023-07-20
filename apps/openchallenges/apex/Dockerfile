FROM nginx:1.25.1-alpine

RUN apk add --no-cache jq

HEALTHCHECK --interval=2s --timeout=3s --retries=20 --start-period=5s \
  CMD curl --fail --silent "localhost:8000/health" | jq '.status' | grep UP || exit 1

COPY templates /etc/nginx/templates/
COPY nginx.conf /etc/nginx/

EXPOSE 8000

CMD ["nginx", "-g", "daemon off;"]