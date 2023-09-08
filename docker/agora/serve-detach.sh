#!/usr/bin/env bash

args=(
  # List of services in alphanumeric order
  --file docker/openchallenges/services/mongo.yml
  --file docker/openchallenges/services/web-app.yml

  --file docker/openchallenges/networks.yml
  --file docker/openchallenges/volumes.yml

  up $1 --detach --remove-orphans
)

docker compose "${args[@]}"
