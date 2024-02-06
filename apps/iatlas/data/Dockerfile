FROM python:3.10.13 as python-base

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

RUN pip3 install -r requirements.txt
