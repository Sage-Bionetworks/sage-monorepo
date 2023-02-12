#!/usr/bin/env bash

docker compose \
  --file docker/openchallenges/services/vault.yml \
  --file docker/openchallenges/services/config-server.yml \
  --file docker/openchallenges/networks.yml \
  up "$1" --detach