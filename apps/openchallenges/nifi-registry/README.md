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

## Create a bucket

From the registry home page:

1. Click on the button "Settings" in the toolbar.
2. Click on the button "NEW BUCKET".
3. Enter the bucket information.
   - Name: openchallenges
4. Click on the button "CREATE".

## Stop and remove the NiFi registry container

```console
docker rm -f openchallenges-nifi-registry
```

## Data persistence

The data created in the registry are stored in a Docker volume.
