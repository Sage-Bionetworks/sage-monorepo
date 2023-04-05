# OpenChallenges Image Service

## Overview

This service manages images for the OpenChallenges app.

## Usage

### Prepare the service

Run this command to prepare this project, including creating the config file `.env`.

```console
nx prepare openchallenges-image-service
```

> **Note** The task `prepare` does not overwrites the config file `.env` if it already exists.

### Build the Docker image

```console
nx build-image openchallenges-image-service
```

## Prepare the dependencies

This project depends on this list of components. Please check that they are properly configured
before using this project.

- openchallenges-thumbor

### Start the image service

Start the image service and its dependencies:

```console
nx serve-detach openchallenges-image-service
```

### Start the service in development mode

The approach is to first start the service and its dependencies as a container with `serve-detach`
(see above), then stop and remove the container before starting the service with `serve`.

```console
nx serve-detach openchallenges-image-service
docker rm -f openchallenges-image-service

nx serve openchallenges-image-service
```