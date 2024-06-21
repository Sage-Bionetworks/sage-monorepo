#!/usr/bin/env bash

files=(
  # List of services in alphanumeric order
  --file docker/agora/services/app.yml
  --file docker/agora/services/data.yml
  --file docker/agora/services/mongo.yml

  --file docker/agora/networks.yml
  --file docker/agora/volumes.yml
)