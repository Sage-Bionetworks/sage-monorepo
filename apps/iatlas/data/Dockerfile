FROM python:3.11.10

ARG USERNAME=app
ARG USER_UID=1000
ARG USER_GID=$USER_UID
ENV APP_DIR=/opt/app \
    APP_USERNAME=${USERNAME}

SHELL ["/bin/bash", "-o", "pipefail", "-c"]

RUN groupadd --gid "$USER_GID" "$USERNAME" \
  && useradd --uid "$USER_UID" --gid "$USER_GID" -m "$USERNAME" \
  # Install sudo
  && apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    gosu wget \
  # Install the GPG key for the Postgres repo
  && wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add - \
  # Add the repo
  && echo "deb http://apt.postgresql.org/pub/repos/apt/ bookworm-pgdg main" | tee /etc/apt/sources.list.d/pgdg.list \
  && apt-get update -qq -y \
  # Install the Postgres 15 client
  && apt-get install --no-install-recommends -qq -y \
    postgresql-client-15 \
  && apt-get -y autoclean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/*

WORKDIR ${APP_DIR}
COPY src src/
COPY pyproject.toml poetry.lock ./

# Use the version of Poetry installed in the dev container.
# See /workspaces/sage-monorepo/tools/devcontainers/sage/.devcontainer/Dockerfile
RUN pip install --no-cache-dir poetry==1.8.3 \
  && poetry config --local virtualenvs.create false \
  && poetry install --no-root --no-interaction --no-ansi \
  && pip cache purge

WORKDIR /
COPY docker-entrypoint.sh ./
RUN chmod +x docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]

CMD ["python", "src/build_database.py"]



# RUN apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
#   && apt-get install --no-install-recommends -qq -y \
#     wget \
#   # Install the GPG key for the Postgres repo
#   && wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add - \
#   # Add the repo
#   && echo "deb http://apt.postgresql.org/pub/repos/apt/ bookworm-pgdg main" | tee /etc/apt/sources.list.d/pgdg.list \
#   && apt-get update -qq -y \
#   # Install the Postgres 15 client
#   && apt-get install --no-install-recommends -qq -y \
#     postgresql-client-15 \
#   && mkdir -p /home/gitlab/sage-iatlas-data \
#   && apt-get -y autoclean \
#   && apt-get -y autoremove \
#   && rm -rf /var/lib/apt/lists/*

# WORKDIR /
# COPY requirements.txt requirements.txt
# COPY src/ /src

# RUN pip install --no-cache-dir -r requirements.txt

# COPY docker-entrypoint.sh ./
# RUN chmod +x docker-entrypoint.sh

# ENTRYPOINT ["/docker-entrypoint.sh"]

# # Set up the iAtlas database
# CMD ["python", "/src/build_database.py"]