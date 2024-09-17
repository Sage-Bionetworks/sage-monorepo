# Sage Dev Container

Learn how to build and run dev containers with the devcontainer CLI:

https://code.visualstudio.com/docs/remote/devcontainer-cli

# Build the image with dev container

```console
devcontainer build \
  --image-name ghcr.io/sage-bionetworks/sage-devcontainer:test \
  --workspace-folder ../sage
```

# Start the dev container

```console
devcontainer up --workspace-folder ../sage
```

# Step into the dev container

```console
docker exec -it sage_devcontainer_test bash
```
