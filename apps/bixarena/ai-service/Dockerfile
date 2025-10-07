 # Allow overriding the Python patch version at build time:
 #   docker build --build-arg PYTHON_VERSION=3.13.4 -t bixarena-ai-service .
ARG PYTHON_VERSION=3.13.3

FROM ghcr.io/astral-sh/uv:0.5.14 AS uv

# Builder stage (compile native deps like ujson)
FROM mirror.gcr.io/python:${PYTHON_VERSION}-slim AS builder

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

# Archive only the site-packages (avoid copying the full stdlib later)
RUN python - <<'EOF'
import site, tarfile, pathlib
sp = site.getsitepackages()[0]
with tarfile.open('/tmp/site-packages.tar', 'w') as tf:
    for p in pathlib.Path(sp).iterdir():
        tf.add(p, arcname=p.name)
EOF

# Runtime stage (no build-essential)
FROM mirror.gcr.io/python:${PYTHON_VERSION}-slim AS runtime

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

# Extract archived site-packages into the runtime's site-packages path
COPY --from=builder /tmp/site-packages.tar /tmp/site-packages.tar
RUN set -e; python - <<'EOF'
import site, tarfile, os
sp = site.getsitepackages()[0]
with tarfile.open('/tmp/site-packages.tar') as tf:
  tf.extractall(sp)
os.remove('/tmp/site-packages.tar')
EOF

# Copy only necessary console scripts (uvicorn + project entry point)
COPY --from=builder /usr/local/bin/uvicorn /usr/local/bin/uvicorn
COPY --from=builder /usr/local/bin/bixarena-ai-service /usr/local/bin/bixarena-ai-service

# Copy runtime configuration / entrypoint script
COPY docker-entrypoint.sh /docker-entrypoint.sh

RUN chown -R ${APP_USERNAME}:${APP_USERNAME} ${APP_DIR} /docker-entrypoint.sh \
  && chmod +x /docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["python"]
