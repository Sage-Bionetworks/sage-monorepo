#!/bin/bash

# This script must be run from the folder that includes it.

imageTag="${1:-test}"

devcontainer build \
  --no-cache \
  --disable-telemetry true \
  --image-name "sagebionetworks/challenge-devcontainer:$imageTag"