FROM sagebionetworks/openchallenges-config-server-base:latest

ARG SERVER_PORT="8080"

USER root

# Add static curl binary
COPY ./tools/curl /tools/curl
RUN chown -R cnb:cnb /tools \
  && chmod u+x /tools/curl

HEALTHCHECK --interval=2s --timeout=3s --retries=5 --start-period=2s \
  CMD /tools/curl --fail --silent "localhost:${SERVER_PORT}/actuator/health/readiness" | grep UP || exit 1

USER cnb