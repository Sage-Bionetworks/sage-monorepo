FROM sagebionetworks/openchallenges-kaggle-to-kafka-service-base:latest

USER root

RUN apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    curl jq \
  && apt-get -y autoclean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/*

# Requires the environment variable SERVER_PORT at runtime
COPY docker-healthcheck /usr/local/bin/

HEALTHCHECK --interval=2s --timeout=3s --retries=20 --start-period=2s \
  CMD ["docker-healthcheck"]

USER cnb