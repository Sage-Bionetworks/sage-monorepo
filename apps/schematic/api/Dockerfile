FROM tiangolo/uwsgi-nginx-flask:python3.10

# add label
LABEL org.opencontainers.image.authors='Milen Nikolov <milen.nikolov@sagebase.org>, Andrew Lamb <andrew.lamb@sagebase.org>, Mialy DeFelice <mialy.defelice@sagebase.org>, Gianna Jordan <gianna.jordan@sagebase.org>, Lingling Peng <lingling.peng@sagebase.org>'

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
RUN apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
    && apt-get install --no-install-recommends -qq -y \
      openssl jq \
    && apt-get -y autoclean \
    && apt-get -y autoremove \
    && rm -rf /var/lib/apt/lists/*

# add dhparam.pem
# dhparam.pem was used in ssl-params.conf
COPY dhparam.pem /etc/ssl/dhparam.pem

# copy all nginx config files
WORKDIR ${NGINX_CONFIG}
COPY ./self-signed.conf ./ssl-params.conf ./certificate.conf ./

# copy to use custom uwsgi.ini
COPY uwsgi.ini /app/uwsgi.ini

# copy files relevant for schematic apis
COPY schematic_api pyproject.toml poetry.lock /app/app/

# install dependencies
WORKDIR /app/app
# Use the version of Poetry installed in the dev container.
# See /workspaces/sage-monorepo/tools/devcontainers/sage/.devcontainer/Dockerfile
RUN pip install --no-cache-dir poetry==1.6.1 \
  && poetry config --local virtualenvs.create false \
  && poetry run pip install "cython<3.0.0" \
  && poetry run pip install --no-build-isolation pyyaml==5.4.1 \
  && poetry install --with prod --no-root --no-interaction --no-ansi \
  # Update file permission
  && mkdir /root/.synapseCache /app/app/manifests \
  # temporary here to ensure .synapseCache is not empty
  && echo "This is a test file." > /root/.synapseCache/test.txt \
  # temporary here until we move .synapseCache to a different path
  && chmod -R 777 /root /app

# Modify entrypoint script to allow SSL private key and certificate to be saved
WORKDIR ${ROOT}
COPY uwsgi-nginx-entrypoint.sh ./entrypoint2.sh
COPY uwsgi-nginx-entrypoint.sh ./uwsgi-nginx-entrypoint2.sh
COPY save_key_certificate.py ./save_key_certificate.py

RUN chmod +x uwsgi-nginx-entrypoint2.sh \
  && chmod +x entrypoint2.sh \
  && chown -R nginx /uwsgi-nginx-entrypoint2.sh \
  && chown -R nginx /entrypoint2.sh

WORKDIR ${APP_DIR}

# specify entrypoint again to generate config
# have to respecify CMD too
ENTRYPOINT ["/entrypoint2.sh"]
CMD ["/start.sh"]

# Expose ports
EXPOSE 7443