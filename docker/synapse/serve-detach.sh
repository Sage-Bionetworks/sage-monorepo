#!/usr/bin/env bash

product_name="synapse"

args=(
  # List of services in alphanumeric order
  --file docker/"$product_name"/services/api-docs.yml

  --file docker/"$product_name"/networks.yml

  --project-name "$product_name"

  up $1 --detach
)

docker compose "${args[@]}"
