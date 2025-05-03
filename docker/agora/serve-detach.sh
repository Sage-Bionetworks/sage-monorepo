#!/usr/bin/env bash

args=(
  # List of services in alphanumeric order
  --file docker/agora/services/apex.yml
  --file docker/agora/services/api-docs.yml
  --file docker/agora/services/api.yml
  --file docker/agora/services/app.yml
  --file docker/agora/services/cgt-v2beta-service.yml
  --file docker/agora/services/data.yml
  --file docker/agora/services/mongo.yml

  --file docker/agora/networks.yml
  --file docker/agora/volumes.yml

  up $1 --detach --remove-orphans
)

docker compose "${args[@]}"