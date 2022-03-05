# Dev Container

## Build

From the root of the workspace:

```console
docker build \
  --build-arg imageVersion=bullseye-20211220-slim \
  --tag challenge-registry-dev \
  .devcontainer/
```