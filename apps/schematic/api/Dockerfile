FROM python:3.10.12-slim-buster

ENV APP_DIR=/opt/app

SHELL ["/bin/bash", "-euxo", "pipefail", "-c"]

# hadolint ignore=DL3008
RUN apt-get update -qq -y \
    && apt-get install --no-install-recommends -qq -y \
        build-essential \
        gosu \
        libpcre3 \
        libpcre3-dev \
        python3-dev \
    && apt-get -y autoclean \
    && apt-get -y autoremove \
    && rm -rf /var/lib/apt/lists/*

WORKDIR ${APP_DIR}
COPY schematic_api schematic_api/
COPY pyproject.toml poetry.lock uwsgi.ini ./

RUN pip install poetry \
  && poetry config --local virtualenvs.create false \
  && poetry run pip install "cython<3.0.0" \
  && poetry run pip install --no-build-isolation pyyaml==5.4.1 \
  && poetry install --with prod --no-root --no-interaction --no-ansi \
  && pip cache purge


WORKDIR /var/www/
# create user group www-data
# and create www-data as a user
RUN usermod -aG www-data www-data
# change the group ownership of /var/www, /opt/app recursively to www-data
# /opt/apt folder is relevant for storing manifests when executing endpoints
# such as /assetTypes/{assetTypes}/projects/{projectId}/manifests
RUN chgrp -R www-data /var/www /opt/app

# add write permission for group for folder /var/www, opt/app recursively
RUN chmod -R g+w /var/www /opt/app
# make sure that all folders created under /var/www are owned by /var/www, opt/app
RUN find /var/www /opt/app -type d -exec chmod 2775 {} \;   
# find all files in /var/www, opt/app and add read and write permission for owner and group
RUN find /var/www /opt/app -type f -exec chmod ug+rw {} \;

WORKDIR /
COPY docker-entrypoint.sh ./
RUN chmod +x docker-entrypoint.sh

EXPOSE 7080

ENTRYPOINT ["/docker-entrypoint.sh"]