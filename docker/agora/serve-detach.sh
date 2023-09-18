#!/usr/bin/env bash

args=(
  # List of services in alphanumeric order
  --file docker/agora/services/mongo.yml
  # --file docker/agora/services/web-app.yml

  --file docker/agora/networks.yml
  --file docker/agora/volumes.yml

  up $1 --detach --remove-orphans
)

docker compose "${args[@]}"
