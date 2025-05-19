#!/usr/bin/env bash

args=(
  # List of services in alphanumeric order
  --file docker/observability/services/grafana.yml
  --file docker/observability/services/prometheus.yml

  --file docker/observability/networks.yml

  --project-name observability

  up $1 --detach
)

docker compose "${args[@]}"
