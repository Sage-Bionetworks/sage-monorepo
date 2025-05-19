#!/usr/bin/env bash

product_name="observability"

args=(
  # List of services in alphanumeric order
  --file docker/"$product_name"/services/grafana.yml
  --file docker/"$product_name"/services/prometheus.yml
  --file docker/"$product_name"/services/loki.yml
  --file docker/"$product_name"/services/tempo.yml

  --file docker/"$product_name"/networks.yml

  --project-name "$product_name"

  up $1 --detach
)

docker compose "${args[@]}"
