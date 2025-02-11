#!/usr/bin/env bash

# Remove files and folders from the workspace root folder.
rm -fr \
  .angular \
  .cache \
  .nx \
  .pnpm-store \
  coverage \
  playwright-report \
  reports

# Remove nested files and folders.
# find . -name "build" -print0 | xargs -0 rm -fr  # TODO: prevent OpenAPI build folders to be rm
find . \
  \( -name ".aws-sam" \
  -o -name ".coverage" \
  -o -name ".gradle" \
  -o -name ".pdm-build" \
  -o -name ".pytest_cache" \
  -o -name ".venv" \
  -o -name "bin" \
  -o -name "dist" \
  -o -name "node_modules" \) \
  -print0 | xargs -0 rm -rf