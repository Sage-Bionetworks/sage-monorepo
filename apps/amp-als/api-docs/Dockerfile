FROM mirror.gcr.io/redocly/redoc:v2.1.5

HEALTHCHECK --interval=2s --timeout=3s --retries=5 --start-period=2s \
  CMD curl --fail --silent "localhost:8403" || exit 1

COPY build/redoc-static.html /usr/share/nginx/html/index.html
COPY favicon.ico /usr/share/nginx/html/

EXPOSE 8403