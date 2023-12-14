#!/usr/bin/env bash

args=(
  # List of services in alphanumeric order
  --file docker/iatlas/services/postgres.yml

  --file docker/iatlas/networks.yml
  --file docker/iatlas/volumes.yml

  up $1 --detach --remove-orphans
)

docker compose "${args[@]}"
