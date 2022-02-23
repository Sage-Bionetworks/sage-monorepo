FROM node:16.10.0-slim as build

ARG challengeRegistryBranch=main
challengeRegistryRepository=tschaffter/challenge-registry
# ARG challengeRegistryRepository=Sage-Bionetworks/challenge-registry

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

RUN mkdir -p /opt/target \
  && cp -v $WEB_APP_DIR/docker/10-envsubst-on-app-config-template.sh /opt/target \
  && mkdir -p /opt/target/html \
  && cp -rv $CHALLENGE_REGISTRY_DIR/dist/apps/web-app/* /opt/target/html \
  && mkdir -p /opt/target/docker/nginx \
  && cp -v $WEB_APP_DIR/nginx/nginx.conf /opt/target/nginx \
  && mkdir -p /opt/target/docker/nginx/templates \
  && cp -v $WEB_APP_DIR/nginx/templates/* /opt/target/nginx/templates


# COPY /home/tschaffter/dev2/tschaffter/challenge-registry ./


# # COPY rocc-client-angular rocc-client-angular/
# # COPY sage-angular sage-angular/
# # COPY src src/
# # COPY angular.json package.json package-lock.json tsconfig.app.json \
# #     tsconfig.json tsconfig.spec.json .eslintrc.json ./
# RUN npm install -g npm@8.4.0
# RUN npm ci
# RUN npm run build

# FROM nginx:1.21.6-alpine
# COPY --from=build /opt/target/html /usr/share/nginx/html
# COPY apps/web-app/nginx/nginx.conf /etc/nginx/nginx.conf
# COPY nginx/templates /etc/nginx/templates
# COPY 10-envsubst-on-app-config-template.sh /docker-entrypoint.d/.
# RUN chmod +x /docker-entrypoint.d/10-envsubst-on-app-config-template.sh

# EXPOSE 80

# # ENTRYPOINT ["/docker-entrypoint.sh"]
# CMD ["nginx", "-g", "daemon off;"]
