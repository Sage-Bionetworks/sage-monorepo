FROM node:16.10.0-slim as build

# ARG challengeRegistryRepository=Sage-Bionetworks/challenge-registry
# ARG challengeRegistryBranch=main
ARG challengeRegistryRepository=tschaffter/challenge-registry
ARG challengeRegistryBranch=dockerize-web-app

ENV DEBIAN_FRONTEND=noninteractive

# hadolint ignore=DL3008
RUN apt-get update -qq -y && apt-get install --no-install-recommends -qq -y \
    git \
  && apt-get -y autoclean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /opt

# Build the web-app
RUN git clone --depth 1 --branch $challengeRegistryBranch git://github.com/${challengeRegistryRepository}.git \
  && cd challenge-registry \
  && yarn install --frozen-lockfile \
  && yarn nx build web-app

ENV CHALLENGE_REGISTRY_DIR=/opt/challenge-registry
ENV WEB_APP_DIR="$CHALLENGE_REGISTRY_DIR/apps/web-app"

# Package the files required to run the app
RUN mkdir -p /opt/target \
  && cp -v $WEB_APP_DIR/docker/10-envsubst-on-app-config-template.sh /opt/target \
  && mkdir -p /opt/target/nginx \
  && cp -rv $WEB_APP_DIR/docker/nginx/* /opt/target/nginx \
  && mkdir -p /opt/target/html \
  && cp -rv $CHALLENGE_REGISTRY_DIR/dist/apps/web-app/* /opt/target/html

FROM nginx:1.21.6-alpine

COPY --from=build /opt/target /tmp/target
RUN cp -r /tmp/target/html/* /usr/share/nginx/html \
  && cp /tmp/target/nginx/nginx.conf /etc/nginx/nginx.conf \
  && mkdir -p /etc/nginx/templates \
  && cp -r /tmp/target/nginx/templates/* /etc/nginx/templates \
  && cp /tmp/target/10-envsubst-on-app-config-template.sh /docker-entrypoint.d \
  && chmod +x /docker-entrypoint.d/10-envsubst-on-app-config-template.sh \
  && rm -fr /tmp/target

EXPOSE 80

# ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]
