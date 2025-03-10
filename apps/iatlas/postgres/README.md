# iAtlas PostgreSQL

## Overview

This project provides a containerized PostgreSQL database for the iAtlas application.

## Create the initial config

Generate the `.env` configuration file before updating it manually:

```console
nx create-config iatlas-postgres
```

## Running the database

### Build the Docker image

Create a Docker image of the database:

```
nx build-image iatlas-postgres
```

### Run with Docker Compose

Start the database using Docker Compose:

```console
nx serve-detach iatlas-postgres
```
