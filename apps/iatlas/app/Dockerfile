FROM r-base:4.2.3

# Install supporting packages
RUN apt-get -y update && apt-get -y --no-install-recommends install \
    build-essential \
    gettext-base \
    libcurl4-openssl-dev \
    libnode-dev \
    libpq-dev \
    libssl-dev \
    libv8-dev \
    libxml2-dev \
    postgresql-client-16 \
  # Cleanup
  && apt-get -y autoclean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/app
COPY renv.lock .
COPY renv/activate.R renv/activate.R

# Resolve dependencies
RUN R -e "options(pkgType = 'binary'); install.packages('V8', type = 'source')"
# RUN R -e "options(pkgType='binary'); source('renv/activate.R'); renv::restore()"