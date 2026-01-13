FROM ghcr.io/astral-sh/uv:0.5.14 AS uv

# First, bundle the dependencies into the task root.
FROM mirror.gcr.io/python:3.11.9-slim AS builder

# Enable bytecode compilation, to improve cold-start performance.
ENV UV_COMPILE_BYTECODE=1

# Disable installer metadata, to create a deterministic layer.
ENV UV_NO_INSTALLER_METADATA=1

# Enable copy mode to support bind mount caching.
ENV UV_LINK_MODE=copy

# Copy uv binary
COPY --from=uv /uv /bin/uv

# Copy dependency files
WORKDIR /app
COPY pyproject.toml uv.lock ./

# Generate requirements.txt and install dependencies
RUN uv export --frozen --no-emit-workspace --no-dev --no-editable -o requirements.txt && \
    uv pip install -r requirements.txt --target "/app"

FROM mirror.gcr.io/python:3.11.9-slim

ARG USERNAME=app
ARG USER_UID=1000
ARG USER_GID=$USER_UID
ENV APP_DIR=/app \
    APP_USERNAME=${USERNAME}
ENV PYTHONPATH="${APP_DIR}"

RUN groupadd --gid "$USER_GID" "$USERNAME" \
  && useradd --uid "$USER_UID" --gid "$USER_GID" -m "$USERNAME" \
  # Install dependencies
  && apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    ca-certificates \
    gosu \
    wget \
    gnupg \
  # Add MongoDB GPG key and repository (modern scoped trust approach)
  && wget -qO - https://www.mongodb.org/static/pgp/server-8.0.asc | gpg --dearmor -o /usr/share/keyrings/mongodb-server-8.0.gpg \
  && echo "deb [signed-by=/usr/share/keyrings/mongodb-server-8.0.gpg] http://repo.mongodb.org/apt/debian bookworm/mongodb-org/8.0 main" | tee /etc/apt/sources.list.d/mongodb-org-8.0.list \
  && apt-get update -qq -y \
  && apt-get install --no-install-recommends -qq -y \
    mongodb-database-tools \
  && apt-get -y clean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/*

# Copy the runtime dependencies from the builder stage.
COPY --from=builder ${APP_DIR} ${APP_DIR}

# Copy the application code.
COPY ./src /${APP_DIR}/src
RUN chown -R ${APP_USERNAME}:${APP_USERNAME} ${APP_DIR}

WORKDIR /
COPY docker-entrypoint.sh ./
RUN chmod +x docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]

CMD ["python", "src/main.py"]