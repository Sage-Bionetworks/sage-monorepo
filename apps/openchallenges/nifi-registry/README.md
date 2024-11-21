# OpenChallenges NiFi Registry

## Build the Docker image

```console
nx build-image openchallenges-nifi-registry
```

## Deploy the registry with Docker Compose

```console
nx serve-detach openchallenges-nifi-registry
```

## Access the registry UI

In your browser, navigate to http://localhost:18080/nifi-registry.

## Stop the registry with Docker Compose

```console
docker rm -f openchallenges-nifi-registry
```

## Data persistence

The data created in the registry are stored in a Docker volume.
