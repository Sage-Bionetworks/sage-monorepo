FROM node:18.10.0-slim as build

ARG openchallengesRepository=sage-monorepo
ARG openchallengesBranch=main

ENV DEBIAN_FRONTEND=noninteractive

# hadolint ignore=DL3008
RUN apt-get update -qq -y && apt-get install --no-install-recommends -qq -y \
    ca-certificates git \
  && apt-get -y autoclean \
  && apt-get -y autoremove \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /opt

# Build the app
RUN git clone --depth 1 --branch $openchallengesBranch https://github.com/Sage-Bionetworks/${openchallengesRepository}.git \
  && cd ${openchallengesRepository} \
  && yarn install --immutable \
  && yarn nx build openchallenges-app

ENV APP_DIR="${openchallengesRepository}/apps/openchallenges/app"

# # Package the files required to run the app
RUN mkdir -p /opt/target \
  && cp -v $APP_DIR/docker/10-envsubst-on-app-config-template.sh /opt/target \
  && mkdir -p /opt/target/nginx \
  && cp -rv $APP_DIR/docker/nginx/* /opt/target/nginx \
  && mkdir -p /opt/target/html \
  && cp -rv ${openchallengesRepository}/dist/apps/openchallenges/* /opt/target/html

FROM nginx:1.23.2-alpine

COPY --from=build /opt/target /tmp/target
RUN cp -r /tmp/target/html/* /usr/share/nginx/html \
  && cp /tmp/target/nginx/nginx.conf /etc/nginx/nginx.conf \
  && mkdir -p /etc/nginx/templates \
  && cp -r /tmp/target/nginx/templates/* /etc/nginx/templates \
  && cp /tmp/target/10-envsubst-on-app-config-template.sh /docker-entrypoint.d \
  && chmod +x /docker-entrypoint.d/10-envsubst-on-app-config-template.sh \
  && rm -fr /tmp/target

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
