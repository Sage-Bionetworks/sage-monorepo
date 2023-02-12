#!/usr/bin/env bash

args=(
  # List of services listed in alphanumeric order
  --file docker/openchallenges/services/api-gateway.yml
  --file docker/openchallenges/services/challenge-service.yml
  --file docker/openchallenges/services/config-server.yml
  --file docker/openchallenges/services/elasticsearch.yml
  --file docker/openchallenges/services/mariadb.yml
  --file docker/openchallenges/services/service-registry.yml
  --file docker/openchallenges/services/vault.yml
  --file docker/openchallenges/services/zipkin.yml

  --file docker/openchallenges/networks.yml

  up $1 --detach
)

docker compose "${args[@]}"
