#!/usr/bin/env bash

product_name="amp-als"

args=(
  # List of services in alphanumeric order
  --file docker/"$product_name"/services/apex.yml
  --file docker/"$product_name"/services/api-docs.yml
  --file docker/"$product_name"/services/dataset-service.yml
  --file docker/"$product_name"/services/keycloak.yml
  --file docker/"$product_name"/services/opensearch.yml
  --file docker/"$product_name"/services/postgres.yml

  --file docker/"$product_name"/networks.yml

  --project-name "$product_name"

  up $1 --detach
)

docker compose "${args[@]}"
