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

## Prepare the dependencies

This project depends on this list of components. Please check that they are properly configured
before using this project.

- openchallenges-thumbor

### Start the image service

Start the image service and its dependencies:

```console
nx serve-detach openchallenges-image-service
```