#!/usr/bin/env bash

args=(
  # List of services in alphanumeric order
  --file docker/shared/services/sonarqube.yml

  --file docker/shared/networks.yml

  up $1 --detach --remove-orphans
)

docker compose "${args[@]}"
