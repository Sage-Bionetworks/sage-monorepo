FROM ghcr.io/astral-sh/uv:0.5.14 AS uv

# First, bundle the dependencies into the task root.
FROM python:3.12.8-slim AS builder

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

FROM python:3.12.8-slim

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

# WORKDIR ${APP_DIR}
# COPY src src/
# COPY pyproject.toml uv.lock ./

# Use the version of Poetry installed in the dev container.
# See /workspaces/sage-monorepo/tools/devcontainers/sage/.devcontainer/Dockerfile
# RUN pip install --no-cache-dir uv==0.5.14 \
#   && poetry config --local virtualenvs.create false \
#   && poetry install --with prod --no-root --no-interaction --no-ansi \
#   && pip cache purge

# Copy the runtime dependencies from the builder stage.
COPY --from=builder /app /app

# Copy the application code.
COPY ./src /app/src

WORKDIR /
COPY docker-entrypoint.sh ./
RUN chmod +x docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]

CMD ["python", "src/main.py"]