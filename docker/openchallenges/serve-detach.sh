#!/usr/bin/env bash

args=(
  # List of services in alphanumeric order
  --file docker/openchallenges/services/api-gateway.yml
  --file docker/openchallenges/services/app.yml
  --file docker/openchallenges/services/challenge-service.yml
  --file docker/openchallenges/services/config-server.yml
  --file docker/openchallenges/services/elasticsearch.yml
  --file docker/openchallenges/services/grafana.yml
  --file docker/openchallenges/services/kafka.yml
  --file docker/openchallenges/services/mariadb.yml
  --file docker/openchallenges/services/minio.yml
  --file docker/openchallenges/services/organization-service.yml
  --file docker/openchallenges/services/prometheus.yml
  --file docker/openchallenges/services/service-registry.yml
  --file docker/openchallenges/services/thumbor.yml
  --file docker/openchallenges/services/vault.yml
  --file docker/openchallenges/services/zipkin.yml

  --file docker/openchallenges/networks.yml
  --file docker/openchallenges/volumes.yml

  up $1 --detach --remove-orphans
)

docker compose "${args[@]}"
