# iAtlas API

A GraphQL API that serves data from the iAtlas Data Database. This is built in Python and developed and deployed in Docker.

## Dependencies

- [Docker Desktop](https://www.docker.com/products/docker-desktop) (`docker`)
- [Visual Studio Code](https://code.visualstudio.com/) (`code`) - this is optional, but sure makes everything a lot easier.

## Development

The first time you checkout the project, run the following command to build the docker image:

```bash
./start.sh -b build
```

This will build the Docker image and run the container. Once the container is created, the Flask server will be started. Then a command prompt should open from within the container (looks like: `bash-5.0#`).

If you get _'Version in "./docker-compose.yml" is unsupported.'_, please update your version of Docker Desktop.

To exit the container's command prompt, type `exit` and enter. This will bring you back to your local command prompt.

The following command will stop the server and container:

```bash
./stop.sh
```

Once the image has been built, the container and server can be started with the following command:

```bash
./start.sh
```

If there are changes made to the container or image, first, stop the container `./start.sh`, then rebuild it and restarted it with `./start.sh -b build`.

Now head on over to
[http://localhost:5000/graphiql](http://localhost:5000/graphiql)
and run some queries!
