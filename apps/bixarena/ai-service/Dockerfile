FROM ghcr.io/astral-sh/uv:0.5.14 AS uv

# Builder stage (compile native deps like ujson)
FROM mirror.gcr.io/python:3.13.3-slim AS builder

ARG USERNAME=app
ARG USER_UID=1000
ARG USER_GID=$USER_UID
ENV APP_DIR=/app \
    APP_USERNAME=${USERNAME}

# Copy uv binary into builder
COPY --from=uv /uv /bin/uv

WORKDIR ${APP_DIR}

# Install build toolchain only in builder
RUN apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    build-essential \
  && rm -rf /var/lib/apt/lists/*

# Copy distribution artifact(s) and install (brings in all dependencies)
COPY dist/*.tar.gz /tmp/
RUN uv pip install /tmp/*.tar.gz --system \
  && rm -rf /root/.cache

# Runtime stage (no build-essential)
FROM mirror.gcr.io/python:3.13.3-slim AS runtime

ARG USERNAME=app
ARG USER_UID=1000
ARG USER_GID=$USER_UID
ENV APP_DIR=/app \
    APP_USERNAME=${USERNAME}

# Copy uv binary into runtime
COPY --from=uv /uv /bin/uv

# Create user & minimal runtime deps only
RUN groupadd --gid "$USER_GID" "$USERNAME" \
  && useradd --uid "$USER_UID" --gid "$USER_GID" -m "$USERNAME" \
  && apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    gosu \
  && apt-get -y clean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/*

WORKDIR ${APP_DIR}

# Copy installed site-packages & console scripts from builder
# (These include the project package and all dependencies, including compiled wheels/modules)
COPY --from=builder /usr/local/lib/python3.13/site-packages /usr/local/lib/python3.13/site-packages
COPY --from=builder /usr/local/bin /usr/local/bin

# Copy runtime configuration / entrypoint script
COPY docker-entrypoint.sh /docker-entrypoint.sh

RUN chown -R ${APP_USERNAME}:${APP_USERNAME} ${APP_DIR} /docker-entrypoint.sh \
  && chmod +x /docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["python"]
