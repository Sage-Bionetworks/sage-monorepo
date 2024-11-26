#!/usr/bin/env bash

args=(
  # List of services in alphanumeric order
  --file docker/sage/services/otel-collector.yml

  --file docker/sage/networks.yml

  up $1 --detach --remove-orphans
)

docker compose "${args[@]}"