FROM python:3.8.20-slim-bullseye

ENV APP_DIR=/opt/app

RUN apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    # Required to install uWSGI
    build-essential \
    # Used in docker-entrypoint.sh
    gosu \
  && apt-get -y autoclean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/*

WORKDIR ${APP_DIR}
COPY api ./api
COPY pyproject.toml poetry.lock iatlasapi.py config.py uwsgi.ini ./

# Install the app dependencies.
# Install the version of Poetry used inside the dev container.
# See /workspaces/sage-monorepo/tools/devcontainers/sage/.devcontainer/Dockerfile
RUN pip install --no-cache-dir poetry==1.8.3 \
  && poetry config --local virtualenvs.create false \
  && poetry install --with prod --no-root --no-interaction --no-ansi \
  && pip cache purge \
  && chown -R www-data:www-data ${APP_DIR}

WORKDIR /
COPY docker-entrypoint.sh .
RUN chmod +x docker-entrypoint.sh

EXPOSE 5000

ENTRYPOINT ["/docker-entrypoint.sh"]

# Run production server
CMD ["uwsgi", "--ini", "uwsgi.ini", "--lazy", "--http", ":5000"]
