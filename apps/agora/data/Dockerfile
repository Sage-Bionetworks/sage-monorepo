FROM python:3.11.9-slim

ARG LOCAL_DATA_DIR="local/data"
ARG USERNAME=app
ARG USER_UID=1000
ARG USER_GID=$USER_UID
ENV APP_DIR=/opt/app \
    APP_USERNAME=${USERNAME}

RUN groupadd --gid "$USER_GID" "$USERNAME" \
  && useradd --uid "$USER_UID" --gid "$USER_GID" -m "$USERNAME" \
  # Install sudo
  && apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    gosu \
  && apt-get -y autoclean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/*

WORKDIR ${APP_DIR}
COPY src src/
COPY pyproject.toml poetry.lock ./

# Use the version of Poetry installed in the dev container.
# See /workspaces/sage-monorepo/tools/devcontainers/sage/.devcontainer/Dockerfile
RUN pip install --no-cache-dir poetry==1.6.1 \
  && poetry config --local virtualenvs.create false \
  && poetry install --with prod --no-root --no-interaction --no-ansi \
  && pip cache purge

# Give APP_USER write permission to directory where synapse data will be written
RUN mkdir -p ${LOCAL_DATA_DIR}
RUN chown ${APP_USERNAME} ${LOCAL_DATA_DIR}

WORKDIR /
COPY docker-entrypoint.sh ./
RUN chmod +x docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]

CMD ["python", "src/main.py"]