FROM mirror.gcr.io/postgres:16.9-bullseye

# Install pgvector from apt.postgresql.org
RUN apt-get update \
  && apt-get install -y --no-install-recommends postgresql-16-pgvector \
  && rm -rf /var/lib/apt/lists/*

COPY docker-healthcheck /usr/local/bin/

HEALTHCHECK CMD ["docker-healthcheck"]
