FROM ghcr.io/astral-sh/uv:0.5.14 AS uv

# First, bundle the dependencies into the task root.
FROM mirror.gcr.io/python:3.8.20-slim AS builder

# Enable bytecode compilation, to improve cold-start performance.
ENV UV_COMPILE_BYTECODE=1

# Disable installer metadata, to create a deterministic layer.
ENV UV_NO_INSTALLER_METADATA=1

# Enable copy mode to support bind mount caching.
ENV UV_LINK_MODE=copy

# Copy uv binary.
COPY --from=uv /uv /bin/uv

# Copy dependency files.
WORKDIR /app
COPY pyproject.toml uv.lock ./

# Install C compiler to build uWSGI.
RUN apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    build-essential

# Generate requirements.txt and install dependencies.
RUN uv export --frozen --no-emit-workspace --no-dev --group prod --no-editable -o requirements.txt \
  && uv pip install -r requirements.txt --target "/app"

FROM mirror.gcr.io/python:3.8.20-slim

ENV APP_DIR=/app
ENV PYTHONPATH="${APP_DIR}"
ENV PATH="${APP_DIR}/bin:${PATH}"

RUN apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    # Used in docker-entrypoint.sh
    gosu \
  && apt-get -y clean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/*

# Copy the runtime dependencies from the builder stage.
COPY --from=builder ${APP_DIR} ${APP_DIR}

# Copy the application code.
COPY iatlasapi.py config.py uwsgi.ini ${APP_DIR}/
COPY ./api ${APP_DIR}/api
RUN chown -R www-data:www-data ${APP_DIR}

WORKDIR /
COPY docker-entrypoint.sh ./
RUN chmod +x docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]

CMD ["uwsgi", "--ini", "uwsgi.ini", "--lazy", "--http", ":2000"]