# Allow overriding the Python patch version at build time:
#   docker build --build-arg PYTHON_VERSION=3.13.4 -t bixarena-lambda .
ARG PYTHON_VERSION=3.13.3

FROM ghcr.io/astral-sh/uv:0.5.14 AS uv

FROM mirror.gcr.io/python:${PYTHON_VERSION}-slim AS builder

COPY --from=uv /uv /bin/uv

WORKDIR /build

# Install the pre-built shared library and its dependencies into /deps.
COPY dist/*.tar.gz /tmp/
RUN uv pip install /tmp/*.tar.gz --target /deps --system --no-cache-dir

# ---- final image ----
FROM mirror.gcr.io/python:${PYTHON_VERSION}-slim

WORKDIR /app

# Add installed packages to the module search path
ENV PYTHONPATH="/app:/deps"

COPY --from=builder /deps /deps

# Copy all handler modules
COPY leaderboard_snapshot/ /app/leaderboard_snapshot/

CMD ["python", "-m", "leaderboard_snapshot.handler"]
