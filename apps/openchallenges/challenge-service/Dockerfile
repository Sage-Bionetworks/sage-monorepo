FROM ghcr.io/sage-bionetworks/openchallenges-challenge-service-base:local

USER root

RUN apt-get update -qq -y && export DEBIAN_FRONTEND=noninteractive \
  && apt-get install --no-install-recommends -qq -y \
    curl jq \
  && apt-get -y autoclean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/*

# Requires the environment variable SERVER_PORT at runtime
HEALTHCHECK --interval=5s --timeout=3s --retries=20 --start-period=5s \
  CMD curl --fail --silent "localhost:${SERVER_PORT}/actuator/health/readiness" | jq '.status' | grep UP || exit 1

USER cnb