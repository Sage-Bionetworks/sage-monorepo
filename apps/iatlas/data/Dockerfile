FROM python:3.10.13

RUN apt-get -y update && apt-get -y install \
    wget && \
    # Install the GPG key for the Postgres repo
    wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add - && \
    # Add the repo
    echo "deb http://apt.postgresql.org/pub/repos/apt/ bookworm-pgdg main" | tee /etc/apt/sources.list.d/pgdg.list && \
    apt-get -y update && \
    # Install the Postgres 12 client
    apt-get -y install postgresql-client-12 && \
    mkdir -p /home/gitlab/sage-iatlas-data

COPY requirements.txt requirements.txt
COPY src/ /src

RUN pip3 install -r requirements.txt

WORKDIR /
COPY docker-entrypoint.sh ./
RUN chmod +x docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]

# Set up the iAtlas database
CMD python /src/build_database.py