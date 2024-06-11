#!/usr/bin/env bash

args=(
  # List of services in alphanumeric order
  --file docker/model-ad/services/app.yml
  --file docker/model-ad/services/mongo.yml

  --file docker/model-ad/networks.yml

  up $1 --detach --remove-orphans
)

docker compose "${args[@]}"
