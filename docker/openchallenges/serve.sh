#!/usr/bin/env bash

args=(
  # List of services in alphanumeric order
  --file docker/openchallenges/services/data-lambda.yml

  --file docker/openchallenges/networks.yml
  --file docker/openchallenges/volumes.yml

  --project-name openchallenges

  up $1
)

docker compose "${args[@]}"
