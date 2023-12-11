FROM tiangolo/uwsgi-nginx-flask:python3.10

# add version tag as a build argument
ARG TAG

# add label 
LABEL maintainer="Milen Nikolov, Andrew Lamb, Mialy DeFelice, Gianna Jordan, Lingling Peng"

# the environment variables defined here are the default
# and can be overwritten by docker run -e VARIABLE = XX
# or can be overwritten by .env when using docker compose
ENV PYTHONFAULTHANDLER=1 \
    PYTHONUNBUFFERED=1 \
    PYTHONHASHSEED=random \
    PIP_NO_CACHE_DIR=off \
    PIP_DISABLE_PIP_VERSION_CHECK=on \
    PIP_DEFAULT_TIMEOUT=200 \
    POETRY_VERSION=1.3.0 \
    APP_PARENT_DIR=/app \ 
    NGINX_CONFIG=/etc/nginx/conf.d \
    APP_DIR=/app/app \
    ROOT=/ \
    UWSGI_INI=/app/uwsgi.ini \ 
    NGINX_WORKER_PROCESSES=1 \
    VERSION=$TAG

# run open ssl and generate certificate
RUN apt update && \
    apt-get install openssl && \
    openssl req -x509 -nodes -days 365 \
    -subj  "/C=CA/ST=QC/O=Company" \
    -newkey rsa:2048 -keyout /etc/ssl/private/localhost.key \
    -out /etc/ssl/certs/localhost.crt;

# add dhparam.pem
# dhparam.pem was used in ssl-params.conf
RUN openssl dhparam -out /etc/ssl/dhparam.pem 4096

# copy config nginx files that handle encryption
WORKDIR ${NGINX_CONFIG}
COPY ./self-signed.conf ./ssl-params.conf ./certificate.conf ./redirect.conf ./

# copy to use custom uwsgi.ini and a custom prestart script
# the prestart script ensures us to run container as user www-data
WORKDIR /app
COPY ./uwsgi.ini ./
# COPY ./docker-entrypoint.sh ./prestart.sh
RUN rm -rf ${APP_PARENT_DIR}/prestart.sh

# copy files relevant for schematic apis
WORKDIR /app/app
COPY  schematic_api ./schematic_api
COPY pyproject.toml poetry.lock ./

# install dependencies
RUN pip install poetry \
  && poetry config --local virtualenvs.create false \
  && poetry run pip install "cython<3.0.0" \
  && poetry run pip install --no-build-isolation pyyaml==5.4.1 \
  && poetry install --with prod --no-root --no-interaction --no-ansi

# make any users able to read, write /root
# this is temporarily here to ensure that .synapseCache folder can be created
RUN chmod -R 777 /root

WORKDIR ${APP_DIR}

# Expose ports
EXPOSE 443
