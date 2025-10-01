FROM ghcr.io/astral-sh/uv:0.5.14 AS uv

# First, bundle the dependencies into the task root.
FROM mirror.gcr.io/python:3.13.3-slim AS builder

# Enable bytecode compilation, to improve cold-start performance.
ENV UV_COMPILE_BYTECODE=1

# Disable installer metadata, to create a deterministic layer.
ENV UV_NO_INSTALLER_METADATA=1

# Enable copy mode to support bind mount caching.
ENV UV_LINK_MODE=copy

# Copy uv binary
COPY --from=uv /uv /bin/uv

# Copy dependency files AND source code for workspace installation
WORKDIR /app
COPY pyproject.toml uv.lock ./
COPY ./src ./src

# Generate requirements.txt and install dependencies AND install fastchat package
RUN uv export --frozen --no-emit-workspace --no-dev --no-editable \
    --extra model_worker --extra webui \
    -o requirements.txt && \
    uv pip install -r requirements.txt --target "/app/deps" && \
    uv pip install . --target "/app/deps"


FROM mirror.gcr.io/python:3.13.3-slim

ARG USERNAME=app
ARG USER_UID=1000
ARG USER_GID=$USER_UID
ENV APP_DIR=/app \
    APP_USERNAME=${USERNAME}
ENV PYTHONPATH="${APP_DIR}/deps"


RUN groupadd --gid "$USER_GID" "$USERNAME" \
  && useradd --uid "$USER_UID" --gid "$USER_GID" -m "$USERNAME" \
  # Install sudo
  && apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    gettext-base gosu \
  && apt-get -y clean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/*

# Copy the runtime dependencies from the builder stage.
COPY --from=builder /app/deps ${APP_DIR}/deps

# Copy config files
COPY model_config.template.json ${APP_DIR}/
COPY docker-entrypoint.sh /docker-entrypoint.sh

RUN chown -R ${APP_USERNAME}:${APP_USERNAME} ${APP_DIR} && \
    chmod +x docker-entrypoint.sh

WORKDIR ${APP_DIR}
EXPOSE 8100

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["python"]