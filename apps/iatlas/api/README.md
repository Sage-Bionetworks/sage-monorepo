# iAtlas API

A GraphQL API that serves data from the iAtlas Data Database. This is built in Python and developed
and deployed in Docker.

## Usage

Show the tasks available to this project:

```console
nx show project iatlas-api
```

## Create the project configuration file

```console
nx create-config iatlas-api
```

## Prepare the Python environment

```console
nx prepare iatlas-api
```

## Build the Docker image

```console
nx build-image iatlas-api
```

## Start the API with Docker Compose

```console
nx serve-detach iatlas-api
```

## Open the GraphQL Playground

To access the GraphQL Playground, open your browser and navigate to:

`http://http://localhost:5000/graphiql`
