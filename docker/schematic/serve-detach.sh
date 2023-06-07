#!/usr/bin/env bash

args=(
  # List of services in alphanumeric order
  --file docker/schematic/services/api.yml

  --file docker/schematic/networks.yml
  --file docker/schematic/volumes.yml

  up $1 --detach --remove-orphans
)

docker compose "${args[@]}"
