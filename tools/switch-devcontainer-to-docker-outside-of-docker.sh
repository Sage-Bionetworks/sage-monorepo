#!/usr/bin/env bash

# Update the definition of the dev container to replace the feature "docker-in-docker" by
# "docker-outside-of-docker". The first purpose of this script is to build the Docker images of the
# stacks developed in this monorepo so that these images are available on the host (i.e. outside of
# the dev container).
#
# Usage: ./tools/switch-devcontainer-to-docker-outside-of-docker.sh
#
# References:
# - https://github.com/devcontainers/features/tree/main/src/docker-outside-of-docker

DEV_CONTAINER_DEFINITION_FILE=".devcontainer/devcontainer.json"

# Remove the feature "docker-in-docker" from the devcontainer definition
cat <<< $(jq 'delpaths([paths | select(.[0] == "features" and
    (.[1] | tostring | startswith("ghcr.io/devcontainers/features/docker-in-docker")))])' \
  $DEV_CONTAINER_DEFINITION_FILE) \
  > $DEV_CONTAINER_DEFINITION_FILE

# Add the feature "docker-outside-of-docker" to the new devcontainer definition
cat <<< $(jq '.features."ghcr.io/devcontainers/features/docker-outside-of-docker:1" = {}' \
  $DEV_CONTAINER_DEFINITION_FILE) \
  > $DEV_CONTAINER_DEFINITION_FILE