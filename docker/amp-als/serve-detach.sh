#!/usr/bin/env bash

args=(
  # List of services in alphanumeric order
  --file docker/amp-als/services/apex.yml
  --file docker/amp-als/services/api-docs.yml
  --file docker/amp-als/services/dataset-service.yml
  --file docker/amp-als/services/keycloak.yml
  --file docker/amp-als/services/mariadb.yml
  --file docker/amp-als/services/opensearch.yml

  --file docker/amp-als/networks.yml

  --project-name amp-als

  up $1 --detach
)

docker compose "${args[@]}"
