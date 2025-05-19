#!/usr/bin/env bash

args=(
  # List of services in alphanumeric order
  --file docker/sandbox/services/lambda-nodejs.yml

  --file docker/sandbox/networks.yml
  --file docker/sandbox/volumes.yml

  up $1 --detach
)

docker compose "${args[@]}"
