#!/usr/bin/env bash

# This script must be run from the folder that includes it.

devcontainer build \
  --image-name ghcr.io/sage-bionetworks/sage-devcontainer:testing \
  --workspace-folder sage
