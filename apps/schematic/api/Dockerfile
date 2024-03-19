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
    APP_DIR=/app/app \
    ROOT=/ \
    UWSGI_INI=/app/uwsgi.ini \ 
    NGINX_WORKER_PROCESSES=1 \
    VERSION=$TAG

# run open ssl and generate certificate
RUN apt update \
    && apt-get -y autoclean \
    && apt-get -y autoremove \
    && rm -rf /var/lib/apt/lists/* \
    && apt-get update \ 
    && apt-get install --no-install-recommends -y openssl jq 

# add dhparam.pem
COPY dhparam.pem /etc/ssl/dhparam.pem

# copy all nginx config files
COPY ./self-signed.conf ./ssl-params.conf ./certificate.conf /etc/nginx/conf.d/

# copy to use custom uwsgi.ini
COPY uwsgi.ini /app/uwsgi.ini 

# copy files relevant for schematic apis, including uwsgi.ini
# also install dependencies
WORKDIR /app/app
COPY pyproject.toml poetry.lock /app/app/
COPY ./schematic_api /app/app/schematic_api

RUN pip install --no-cache-dir poetry \
  && poetry config --local virtualenvs.create false \
  && poetry run pip install "cython<3.0.0" \
  && poetry run pip install --no-build-isolation pyyaml==5.4.1 \
  && poetry install --with prod --no-root --no-interaction --no-ansi

# temporary here until schematic allows .synapseCache to a different path
RUN mkdir -p /root/.synapseCache /app/app/manifests \
    && echo "This is a test file." > /root/.synapseCache/test.txt \
    && chmod -R 777 /root /app

# Modify entrypoint script to allow SSL private key and certificate to be saved
WORKDIR ${ROOT}
COPY uwsgi-nginx-entrypoint.sh entrypoint2.sh
COPY uwsgi-nginx-entrypoint.sh uwsgi-nginx-entrypoint2.sh
COPY save_key_certificate.py ./

RUN chmod +x uwsgi-nginx-entrypoint2.sh entrypoint2.sh \
    && chown -R nginx /uwsgi-nginx-entrypoint2.sh /entrypoint2.sh


WORKDIR ${APP_DIR}

# specify entrypoint again to generate config
# have to respecify CMD too
ENTRYPOINT ["/entrypoint2.sh"]
CMD ["/start.sh"]

# Expose ports
EXPOSE 7443