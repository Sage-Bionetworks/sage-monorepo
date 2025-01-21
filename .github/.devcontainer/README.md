# Sage Monorepo Monorepo Dev Container Prebuild Image

## Build the image

From the root workspace:

```console
devcontainer build \
  --workspace-folder .github \
  --image-name ghcr.io/sage-bionetworks/sage-monorepo-devcontainer:local
```

## Start the dev container

```console
devcontainer up --workspace-folder .github
```

## Connect to the dev container

```console
docker exec -it sage-monorepo-devcontainer-prebuild bash
```

## Delete the dev container

```console
docker rm -f sage-monorepo-devcontainer-prebuild
```
