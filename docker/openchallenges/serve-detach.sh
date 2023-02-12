#!/usr/bin/env bash

docker compose \
  --file docker/openchallenges/services/api-gateway.yml \
  --file docker/openchallenges/services/config-server.yml \
  --file docker/openchallenges/services/mariadb.yml \
  --file docker/openchallenges/services/service-registry.yml \
  --file docker/openchallenges/services/vault.yml \
  --file docker/openchallenges/services/zipkin.yml \
  --file docker/openchallenges/networks.yml \
  up "$1" --detach