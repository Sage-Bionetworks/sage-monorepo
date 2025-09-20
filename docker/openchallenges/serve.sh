#!/usr/bin/env bash

product_name="openchallenges"

args=(
  # List of services in alphanumeric order

  --file docker/"$product_name"/networks.yml
  --file docker/"$product_name"/volumes.yml

  --project-name "$product_name"

  up $1
)

docker compose "${args[@]}"
