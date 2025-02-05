#!/usr/bin/env bash

# Install Node.js dependencies
pnpm install --frozen-lockfile

# Install workspace Python dependencies
uv sync

# Prepare projects
pnpm dlx nx run-many --target=create-config
pnpm dlx nx run-many --target=prepare --projects=tag:language:java --parallel=1
# nx run-many --target=prepare --projects=tag:language:python --parallel=1
pnpm dlx nx run-many --target=prepare --projects=tag:language:r

