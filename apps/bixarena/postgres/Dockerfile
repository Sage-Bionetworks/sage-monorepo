FROM mirror.gcr.io/postgres:16.9-bookworm

# Install pgvector from apt.postgresql.org
RUN apt-get update \
  && apt-get install -y --no-install-recommends postgresql-16-pgvector \
  && rm -rf /var/lib/apt/lists/*
