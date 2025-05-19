#!/usr/bin/env bash

args=(
  # List of services in alphanumeric order
  --file docker/synapse/services/api-docs.yml

  --file docker/synapse/networks.yml

  --project-name sandbox

  up $1 --detach
)

docker compose "${args[@]}"
