# iAtlas API

## Overview

A GraphQL API that serves data from the iAtlas Data Database. This is built in Python and developed
and deployed in Docker.

## Usage

Show the tasks available to this project:

```console
nx show project iatlas-api
```

## Create the project config

Generate the `.env` configuration file before updating it manually:

```console
nx create-config iatlas-api
```

## Prepare the project

Set up the Python virtual environment and install dependencies:

```console
nx prepare iatlas-api
```

## Start the PostgreSQL Docker container

Refer to the `iatlas-postgres` project README for instructions on building and starting the
PostgreSQL Docker container.

## Populate the PostgreSQL database

Refer to the `iatlas-data` project README for instructions.

## Running the application

### Run locally

Start the application in a local development environment:

```console
nx serve iatlas-api
```

### Build the Docker image

Create a Docker image of the application:

```console
nx build-image iatlas-api
```

### Run with Docker Compose

Start the application using Docker Compose. This command will automatically start the PostgreSQL
container if it is not already running, then populate the data before launching the application
container:

```console
nx serve-detach iatlas-api
```

## Open the GraphQL Playground

To access the GraphQL Playground, open your browser and navigate to:

`http://http://localhost:5000/graphiql`

Use the query shown below to test the API or use one included in `./example_queries`.

```json
{
  __schema {
    queryType {
      name
    }
  }
}
```
