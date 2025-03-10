# iAtlas Data

## Overview

This project provides a Dockerized solution for downloading iAtlas data and its schema from Synapse
before seeding the iAtlas database (PostgreSQL).

## Create the initial config

Generate the `.env` configuration file before updating it manually:

```console
nx create-config iatlas-data
```

## Prepare the project

Set up the Python virtual environment and install dependencies:

```console
nx prepare iatlas-data
```

## Start the PostgreSQL Docker container

Refer to the `iatlas-data` project README for instructions on building and starting the PostgreSQL
Docker container.

## Running the application

### Run locally

Start the application in a local development environment:

```console
nx serve iatlas-data
```

### Build the Docker image

Create a Docker image of the application:

```console
nx build-image iatlas-data
```

### Run with Docker Compose

Start the application using Docker Compose. This command will automatically start the PostgreSQL
container if it is not already running, then launch the application container:

```console
nx serve-detach iatlas-data
```
