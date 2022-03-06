# Dev Container

## Build

Test

Get Docker Group ID (GID):

```console
$ cut -d: -f3 < <(getent group docker)
1001
```

From the root of the workspace:

```console
docker build \
  --build-arg imageVersion=bullseye-20211220-slim \
  --tag challenge-registry-dev \
  .devcontainer/
```