# Sage Dev Container

Learn how to build and run dev containers with the devcontainer CLI:

https://code.visualstudio.com/docs/remote/devcontainer-cli

# Build the image with devcontainer

```console
devcontainer build \
  --image-name ghcr.io/sage-bionetworks/sage-devcontainer:test \
  --workspace-folder ../sage
```

# Build the image with Docker

```console
docker build -t ghcr.io/sage-bionetworks/sage-devcontainer:test .devcontainer/
```
