#!/usr/bin/env bash

args=(
  # List of services in alphanumeric order
  --file docker/iatlas/services/api.yml
  --file docker/iatlas/services/data.yml
  --file docker/iatlas/services/postgres.yml

  --file docker/iatlas/networks.yml
  --file docker/iatlas/volumes.yml

  --project-name iatlas

  up $1 --detach
)

docker compose "${args[@]}"
