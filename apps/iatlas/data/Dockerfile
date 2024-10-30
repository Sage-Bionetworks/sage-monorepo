FROM python:3.11.9

SHELL ["/bin/bash", "-o", "pipefail", "-c"]

RUN apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    wget \
  # Install the GPG key for the Postgres repo
  && wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add - \
  # Add the repo
  && echo "deb http://apt.postgresql.org/pub/repos/apt/ bookworm-pgdg main" | tee /etc/apt/sources.list.d/pgdg.list \
  && apt-get update -qq -y \
  # Install the Postgres 12 client
  && apt-get install --no-install-recommends -qq -y \
    postgresql-client-12 \
  && mkdir -p /home/gitlab/sage-iatlas-data \
  && apt-get -y autoclean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /
COPY requirements.txt requirements.txt
COPY src/ /src

RUN pip install --no-cache-dir -r requirements.txt

COPY docker-entrypoint.sh ./
RUN chmod +x docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]

# Set up the iAtlas database
CMD ["python", "/src/build_database.py"]