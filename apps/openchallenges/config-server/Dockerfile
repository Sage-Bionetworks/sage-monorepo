FROM sagebionetworks/openchallenges-config-server-base:latest

USER root

# Add static curl binary
COPY ./tools/curl /tools/curl
RUN chown -R cnb:cnb /tools \
  && chmod u+x /tools/curl

USER cnb