# OpenChallenges Image Service

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=openchallenges-image-service&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=openchallenges-image-service)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=openchallenges-image-service&metric=coverage)](https://sonarcloud.io/summary/new_code?id=openchallenges-image-service)

[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=openchallenges-image-service&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=openchallenges-image-service)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=openchallenges-image-service&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=openchallenges-image-service)

[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=openchallenges-image-service&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=openchallenges-image-service)

[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=openchallenges-image-service&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=openchallenges-image-service)

[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=openchallenges-image-service&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=openchallenges-image-service)

[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=openchallenges-image-service&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=openchallenges-image-service)

[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=openchallenges-image-service&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=openchallenges-image-service)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=openchallenges-image-service&metric=bugs)](https://sonarcloud.io/summary/new_code?id=openchallenges-image-service)

## Overview

This service manages images for the OpenChallenges app.

## Usage

### Prepare the service

Run this command to prepare this project, including creating the config file `.env`.

```console
nx create-config openchallenges-image-service
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

The approach is to first start the service and its dependencies as a container with `serve-detach`,
then stop and remove the container before starting the service with `serve`.

```console
nx serve-detach openchallenges-image-service
docker rm -f openchallenges-image-service

nx serve openchallenges-image-service
```
