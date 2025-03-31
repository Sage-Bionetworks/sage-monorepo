#!/usr/bin/env bash

args=(
  # List of services in alphanumeric order
  --file docker/amp-als/services/api-docs.yml
  --file docker/amp-als/services/elasticsearch.yml
  --file docker/amp-als/services/mariadb.yml

  --file docker/amp-als/networks.yml

  up $1 --detach --remove-orphans
)

docker compose "${args[@]}"
