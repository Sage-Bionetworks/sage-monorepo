FROM python:3.9.6-slim-buster

ENV APP_DIR=/opt/app

SHELL ["/bin/bash", "-euxo", "pipefail", "-c"]

# hadolint ignore=DL3008
RUN apt-get update -qq -y \
    && apt-get install --no-install-recommends -qq -y \
        build-essential \
        gosu \
        libpcre3 \
        libpcre3-dev \
    && apt-get -y autoclean \
    && apt-get -y autoremove \
    && rm -rf /var/lib/apt/lists/*

WORKDIR ${APP_DIR}
COPY openapi_server openapi_server/
COPY Pipfile Pipfile.lock uwsgi.ini ./

RUN pip install pipenv \
  && pipenv install --deploy --system \
  && pip install --no-cache-dir uWSGI==2.0.19.1 click==8.0.3

WORKDIR /
COPY docker-entrypoint.sh ./
RUN chmod +x docker-entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/docker-entrypoint.sh"]

# Run server in development mode
# CMD ["python", "-m", "openapi_server"]
# Run server in production mode
CMD ["uwsgi", "--ini", "uwsgi.ini", "--lazy", "--http", ":8080"]