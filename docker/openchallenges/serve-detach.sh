#!/usr/bin/env bash

product_name="openchallenges"

args=(
  # List of services in alphanumeric order
  --file docker/"$product_name"/services/apex.yml
  --file docker/"$product_name"/services/api-gateway.yml
  --file docker/"$product_name"/services/app.yml
  --file docker/"$product_name"/services/auth-service.yml
  --file docker/"$product_name"/services/challenge-service.yml
  --file docker/"$product_name"/services/config-server.yml
  --file docker/"$product_name"/services/data-lambda.yml
  --file docker/"$product_name"/services/image-service.yml
  --file docker/"$product_name"/services/kafka.yml
  --file docker/"$product_name"/services/mcp-server.yml
  --file docker/"$product_name"/services/opensearch-dashboards.yml
  --file docker/"$product_name"/services/opensearch.yml
  --file docker/"$product_name"/services/organization-service.yml
  --file docker/"$product_name"/services/postgres.yml
  --file docker/"$product_name"/services/service-registry.yml
  --file docker/"$product_name"/services/thumbor.yml

  --file docker/"$product_name"/networks.yml
  --file docker/"$product_name"/volumes.yml

  --project-name "$product_name"

  up $1 --detach
)

docker compose "${args[@]}"
