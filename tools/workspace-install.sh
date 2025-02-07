#!/usr/bin/env bash

# Install Node.js dependencies
pnpm install --frozen-lockfile

# Install workspace Python dependencies
uv sync

# Prepare projects
pnpm dlx nx run-many --target=create-config
# Projects that can be prepared in parallel
pnpm dlx nx run-many --target=prepare --projects=!tag:language:java
# Python and Java projects must be installed one at a time
pnpm dlx nx run-many --target=prepare --projects=tag:language:java --parallel=1

