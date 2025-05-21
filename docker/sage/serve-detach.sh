#!/usr/bin/env bash

product_name="sage"

args=(
  # List of services in alphanumeric order
  --file docker/"$product_name"/services/otel-collector.yml

  --file docker/"$product_name"/networks.yml

  --project-name "$product_name"

  up $1 --detach
)

docker compose "${args[@]}"