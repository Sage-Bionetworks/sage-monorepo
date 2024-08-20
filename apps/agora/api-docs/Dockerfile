FROM redocly/redoc:v2.0.0

HEALTHCHECK --interval=2s --timeout=3s --retries=5 --start-period=2s \
  CMD curl --fail --silent "localhost:8010" || exit 1

COPY build/redoc-static.html /usr/share/nginx/html/index.html
COPY favicon.ico /usr/share/nginx/html/

EXPOSE 8010