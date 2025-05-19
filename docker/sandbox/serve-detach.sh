#!/usr/bin/env bash

product_name="sandbox"

args=(
  # List of services in alphanumeric order
  --file docker/"$product_name"/services/lambda-nodejs.yml

  --file docker/"$product_name"/networks.yml
  --file docker/"$product_name"/volumes.yml

  --project-name "$product_name"

  up $1 --detach
)

docker compose "${args[@]}"
