#!/usr/bin/env bash

args=(
  # List of services in alphanumeric order
  --file docker/synapse/services/api-docs.yml
  --file docker/synapse/services/rstudio.yml

  --file docker/synapse/networks.yml

  up $1 --detach --remove-orphans
)

docker compose "${args[@]}"
