FROM ghcr.io/astral-sh/uv:0.5.14 AS uv

# Runtime stage
FROM mirror.gcr.io/python:3.13.3-slim

ARG USERNAME=app
ARG USER_UID=1000
ARG USER_GID=$USER_UID
ENV APP_DIR=/app \
    APP_USERNAME=${USERNAME}

# Copy uv binary
COPY --from=uv /uv /bin/uv

# Create user and install system dependencies
RUN groupadd --gid "$USER_GID" "$USERNAME" \
  && useradd --uid "$USER_UID" --gid "$USER_GID" -m "$USERNAME" \
  && apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y gosu \
  && apt-get -y clean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/*

WORKDIR ${APP_DIR}

# Install the built sdist - this will install all other dependencies
COPY dist/*.tar.gz /tmp/
RUN uv pip install /tmp/*.tar.gz --system

# Copy configuration files
COPY docker-entrypoint.sh /docker-entrypoint.sh

RUN chown -R ${APP_USERNAME}:${APP_USERNAME} ${APP_DIR} && \
    chmod +x /docker-entrypoint.sh

EXPOSE 8100

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["python"]
